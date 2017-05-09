package com.educatejava.spring.watcher;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.nio.file.StandardWatchEventKinds.*;

public class FileWatcher implements Runnable, AutoCloseable {

    private final WatchService service;
    private final Map<Path, WatchTarget> watchTargets = new HashMap<>();
    private final List<FileListener> fileListeners = new CopyOnWriteArrayList<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock r = lock.readLock();
    private final Lock w = lock.writeLock();
    private final AtomicBoolean running = new AtomicBoolean(false);

    public FileWatcher() throws IOException {
        service = FileSystems.getDefault().newWatchService();
    }

    @Override
    public void run() {
        if (running.compareAndSet(false, true)) {
            while (running.get()) {
                WatchKey key;
                try {
                    key = service.take();
                } catch (Throwable e) {
                    break;
                }
                if (key.isValid()) {
                    r.lock();
                    try {
                        key.pollEvents().stream()
                                .filter(e -> e.kind() != OVERFLOW)
                                .forEach(e -> watchTargets.values().stream()
                                        .filter(t -> t.isInterested(e))
                                        .forEach(t -> fireOnEvent(t.path, e.kind())));
                    } finally {
                        r.unlock();
                    }
                    if (!key.reset()) {
                        break;
                    }
                }
            }
            running.set(false);
        }
    }

    public boolean registerPath(Path path, boolean updateIfExists, WatchEvent.Kind... eventKinds) {
        w.lock();
        try {
            WatchTarget target = watchTargets.get(path);
            if (!updateIfExists && target != null) {
                return false;
            }
            Path parent = path.getParent();
            if (parent != null) {
                if (target == null) {
                    watchTargets.put(path, new WatchTarget(path, eventKinds));
                    parent.register(service, eventKinds);
                } else {
                    target.setEventKinds(eventKinds);
                }
                return true;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            w.unlock();
        }
        return false;
    }

    public void addFileListener(FileListener fileListener) {
        fileListeners.add(fileListener);
    }

    public void removeFileListener(FileListener fileListener) {
        fileListeners.remove(fileListener);
    }

    private void fireOnEvent(Path path, WatchEvent.Kind eventKind) {
        for (FileListener fileListener : fileListeners) {
            fileListener.onEvent(path, eventKind);
        }
    }

    public boolean isRunning() {
        return running.get();
    }

    @Override
    public void close() throws IOException {
        running.set(false);
        w.lock();
        try {
            service.close();
        } finally {
            w.unlock();
        }
    }

    private final class WatchTarget {

        private final Path path;
        private final Path fileName;
        private final Set<String> eventNames = new HashSet<>();
        private final Event lastEvent = new Event();

        private WatchTarget(Path path, WatchEvent.Kind[] eventKinds) {
            this.path = path;
            this.fileName = path.getFileName();
            setEventKinds(eventKinds);
        }

        private void setEventKinds(WatchEvent.Kind[] eventKinds) {
            eventNames.clear();
            for (WatchEvent.Kind k : eventKinds) {
                eventNames.add(k.name());
            }
        }

        private boolean isInterested(WatchEvent e) {
            long now = System.currentTimeMillis();
            String name = e.kind().name();
            if (e.context().equals(fileName) && eventNames.contains(name)) {
                if (lastEvent.name == null || !lastEvent.name.equals(name) || now - lastEvent.when > 100) {
                    lastEvent.name = name;
                    lastEvent.when = now;
                    return true;
                }
            }
            return false;
        }

        @Override
        public int hashCode() {
            return path.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return obj == this || obj != null && obj instanceof WatchTarget && Objects.equals(path, ((WatchTarget) obj).path);
        }

    }

    private final class Event {

        private String name;
        private long when;

    }

    public static void main(String[] args) throws IOException, InterruptedException {
        FileWatcher watcher = new FileWatcher();
        if (watcher.registerPath(Paths.get("C:\\temp\\New folder\\desds.txt"), false, ENTRY_MODIFY, ENTRY_CREATE, ENTRY_DELETE)) {
            watcher.addFileListener((path, eventKind) -> System.out.println(path + " -> " + eventKind.name()));
            new Thread(watcher).start();
            System.in.read();
        }
        watcher.close();
        System.exit(0);
    }

}
