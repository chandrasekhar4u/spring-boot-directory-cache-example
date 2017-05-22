package com.educatejava.spring.cache;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sun.nio.file.SensitivityWatchEventModifier;

@Service
public class RecursiveWatcherService {

	private static final Logger LOG = LoggerFactory.getLogger(RecursiveWatcherService.class);

	@Value("${root.folder}")
	private File rootFolder;
	
	@Value("${root.folder1}")
	private File rootFolder1;

	private WatchService watcher;
	
	private ExecutorService executor;
	
	private ConcurrentHashMap<String, String> directoryMap;

	@PostConstruct
	public void init() throws IOException {
		directoryMap=new ConcurrentHashMap<>();
		watcher = FileSystems.getDefault().newWatchService();
		executor = Executors.newSingleThreadExecutor();
		// executor = Executors.newCachedThreadPool();
		startRecursiveWatcher();
	}

	@PreDestroy
	public void cleanup() {
		try {
			watcher.close();
			LOG.debug("closing watcher service");
		} catch (IOException e) {
			LOG.error("Error closing watcher service", e);
		}
		executor.shutdown();
	}
	
	private static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>)event;
    }
	

	private void startRecursiveWatcher() throws IOException {
		LOG.info("Starting Recursive Watcher");

		final Map<WatchKey, Path> keys = new HashMap<>();

		Consumer<Path> register = p -> {
			if (!p.toFile().exists() || !p.toFile().isDirectory()) {
				LOG.error("folder " + p + " does not exist or is not a directory");
				throw new RuntimeException("folder " + p + " does not exist or is not a directory");
			}
			try {
				Files.walkFileTree(p, new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
						LOG.info("registering " + dir + " in watcher service");
						WatchKey watchKey = dir.register(watcher,
								new WatchEvent.Kind[] { ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY },
								SensitivityWatchEventModifier.HIGH);
						keys.put(watchKey, dir);
						System.out.println("File Walk Tree : "+dir);
						return FileVisitResult.CONTINUE;
					}
				});
			} catch (IOException e) {
				throw new RuntimeException("Error registering path " + p);
			}
		};

		// TODO: check impact on non-windows file watching.
		register.accept(rootFolder.toPath());
		register.accept(rootFolder1.toPath());

		executor.submit(() -> {
			while (true) {
				final WatchKey key;
				try {
					key = watcher.take(); // wait for a key to be available
				} catch (InterruptedException ex) {
					return;
				}

				final Path dir = keys.get(key);
				LOG.debug("Directory " + dir);
				if (dir == null) {
					System.err.println("WatchKey " + key + " not recognized!");
					continue;
				}

				/*key.pollEvents().stream().filter(e ->
				e.kind() != OVERFLOW).map(e -> ((WatchEvent<Path>) e).context()).forEach(p -> {
					
					final Path absPath = dir.resolve(p);
					if (absPath.toFile().isDirectory()) {
						register.accept(absPath);
					} else {
						final File f = absPath.toFile();
						LOG.info("Detected file create/change event at: " + f.getAbsolutePath());
					}
				});*/
				
			        for (WatchEvent<?> event : key.pollEvents()) {
			            Kind<?> eventKind = event.kind();

			            // Overflow occurs when the watch event queue is overflown with events.
			            if (eventKind.equals(OVERFLOW)) {
			                // TODO: Notify all listeners.
			                return;
			            }
			            WatchEvent<Path> pathEvent = cast(event);
			            Path file = pathEvent.context();
			            final Path absPath = dir.resolve(file);
			            if (absPath.toFile().isDirectory()) {
							register.accept(absPath);
						}
			            else if (eventKind.equals(ENTRY_CREATE)) {
			            	LOG.info("Detected file ENTRY_CREATE event at: " + absPath);
			            } else if (eventKind.equals(ENTRY_MODIFY)) {
			            	LOG.info("Detected file ENTRY_MODIFY event at: " + absPath);
			            } else if (eventKind.equals(ENTRY_DELETE)) {
			            	LOG.info("Detected file ENTRY_DELETE event at: " + absPath);
			            }
			        }
			        // IMPORTANT: The key must be reset after processed
				boolean valid = key.reset();
				if (!valid) {
					break;
				}
			}
		});
	}
}
