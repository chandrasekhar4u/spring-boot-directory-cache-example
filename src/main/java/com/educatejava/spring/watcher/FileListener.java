package com.educatejava.spring.watcher;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

public interface FileListener {

    void onEvent(Path path, WatchEvent.Kind eventKind);

}
