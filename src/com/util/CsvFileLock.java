package com.util;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public final class CsvFileLock {
    private static final ConcurrentMap<Path, ReentrantLock> JVM_LOCKS = new ConcurrentHashMap<Path, ReentrantLock>();

    private CsvFileLock() { }

    public static <T> T withLock(Path lockPath, Supplier<T> operation) {
        Path normalizedPath = lockPath.toAbsolutePath().normalize();
        ReentrantLock jvmLock = JVM_LOCKS.computeIfAbsent(normalizedPath, key -> new ReentrantLock());
        jvmLock.lock();
        Path parent = normalizedPath.getParent();
        try {
            if (parent != null) {
                Files.createDirectories(parent);
            }
            try (FileChannel channel = FileChannel.open(normalizedPath, StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE); FileLock ignored = channel.lock()) {
                return operation.get();
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to acquire file lock " + normalizedPath + ".", exception);
        } finally {
            jvmLock.unlock();
        }
    }
}