package com.repository.impl;

import com.model.interfaces.BaseEntity;
import com.repository.interfaces.CsvRepository;
import com.util.CsvParser;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

public abstract class AbstractCsvRepository<T extends BaseEntity> implements CsvRepository<T> {
    private static final ConcurrentMap<Path, ReentrantLock> JVM_LOCKS = new ConcurrentHashMap<Path, ReentrantLock>();

    private final List<T> memoryEntities = new ArrayList<T>();
    private final Class<T> entityType;
    private final Path csvPath;

    protected AbstractCsvRepository() {
        this.entityType = null;
        this.csvPath = null;
    }

    protected AbstractCsvRepository(Class<T> entityType, String fileName) {
        this(entityType, defaultCsvPath(fileName));
    }

    protected AbstractCsvRepository(Class<T> entityType, Path csvPath) {
        if (entityType == null || csvPath == null) {
            throw new IllegalArgumentException("Entity type and CSV path are required.");
        }
        this.entityType = entityType;
        this.csvPath = csvPath.toAbsolutePath().normalize();
    }

    protected static Path defaultCsvPath(String fileName) {
        return Paths.get(System.getProperty("stadium.data.dir", "data"), fileName);
    }

    protected static Path dataDirectory() {
        return Paths.get(System.getProperty("stadium.data.dir", "data")).toAbsolutePath().normalize();
    }

    @Override public List<T> findAll() {
        return withStore(false, entities -> new ArrayList<T>(entities));
    }

    @Override public Optional<T> findById(String id) {
        return withStore(false, entities -> {
            for (T entity : entities) {
                if (id != null && id.equals(entity.getId())) {
                    return Optional.of(entity);
                }
            }
            return Optional.empty();
        });
    }

    @Override public boolean save(T entity) {
        if (entity == null || entity.getId() == null) {
            return false;
        }
        return withStore(true, entities -> {
            for (T existing : entities) {
                if (entity.getId().equals(existing.getId())) {
                    return false;
                }
            }
            entities.add(entity);
            return true;
        });
    }

    public int saveAll(List<T> newEntities) {
        if (newEntities == null || newEntities.isEmpty()) {
            return 0;
        }
        return withStore(true, entities -> {
            Set<String> existingIds = new HashSet<String>();
            for (T entity : entities) {
                existingIds.add(entity.getId());
            }
            int saved = 0;
            for (T entity : newEntities) {
                if (entity != null && entity.getId() != null && existingIds.add(entity.getId())) {
                    entities.add(entity);
                    saved++;
                }
            }
            return saved;
        });
    }

    public int updateAll(List<T> updates) {
        if (updates == null || updates.isEmpty()) {
            return 0;
        }
        return withStore(true, entities -> {
            int updated = 0;
            for (T replacement : updates) {
                if (replacement == null || replacement.getId() == null) {
                    continue;
                }
                for (int i = 0; i < entities.size(); i++) {
                    if (replacement.getId().equals(entities.get(i).getId())) {
                        entities.set(i, replacement);
                        updated++;
                        break;
                    }
                }
            }
            return updated;
        });
    }

    @Override public boolean update(T entity) {
        if (entity == null || entity.getId() == null) {
            return false;
        }
        return withStore(true, entities -> {
            for (int i = 0; i < entities.size(); i++) {
                if (entity.getId().equals(entities.get(i).getId())) {
                    entities.set(i, entity);
                    return true;
                }
            }
            return false;
        });
    }

    @Override public boolean deleteById(String id) {
        if (id == null) {
            return false;
        }
        return withStore(true, entities -> entities.removeIf(entity -> id.equals(entity.getId())));
    }

    private <R> R withStore(boolean write, Function<List<T>, R> operation) {
        if (csvPath == null) {
            synchronized (memoryEntities) {
                return operation.apply(memoryEntities);
            }
        }

        Path lockPath = csvPath.resolveSibling(csvPath.getFileName().toString() + ".lock");
        ReentrantLock jvmLock = JVM_LOCKS.computeIfAbsent(lockPath, key -> new ReentrantLock());
        jvmLock.lock();
        Path parent = csvPath.getParent();
        try {
            if (parent != null) {
                Files.createDirectories(parent);
            }
            try (FileChannel channel = FileChannel.open(lockPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                 FileLock ignored = channel.lock()) {
                List<T> entities = readEntities();
                R result = operation.apply(entities);
                if (write) {
                    writeEntities(entities);
                }
                return result;
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to access CSV repository " + csvPath + ".", exception);
        } finally {
            jvmLock.unlock();
        }
    }

    private List<T> readEntities() throws IOException {
        List<T> entities = new ArrayList<T>();
        if (!Files.exists(csvPath)) {
            return entities;
        }
        Constructor<T> constructor;
        try {
            constructor = entityType.getDeclaredConstructor();
            constructor.setAccessible(true);
        } catch (NoSuchMethodException exception) {
            throw new IllegalStateException("CSV entity must have a no-argument constructor: " + entityType.getName(), exception);
        }
        String content = new String(Files.readAllBytes(csvPath), StandardCharsets.UTF_8);
        int rowNumber = 0;
        for (String line : CsvParser.parseRecords(content)) {
            rowNumber++;
            try {
                T entity = constructor.newInstance();
                entity.fromCsvLine(line);
                entities.add(entity);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException exception) {
                throw new IllegalStateException("Unable to parse CSV row " + rowNumber + " in " + csvPath + ".", exception);
            } catch (RuntimeException exception) {
                throw new IllegalStateException("Invalid CSV row " + rowNumber + " in " + csvPath + ".", exception);
            }
        }
        return entities;
    }

    private void writeEntities(List<T> entities) throws IOException {
        Path temporaryPath = csvPath.resolveSibling(csvPath.getFileName().toString()
                + "." + UUID.randomUUID().toString() + ".tmp");
        List<String> lines = new ArrayList<String>(entities.size());
        for (T entity : entities) {
            lines.add(entity.toCsvLine());
        }
        try {
            Files.write(temporaryPath, lines, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
            try {
                Files.move(temporaryPath, csvPath, StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporaryPath, csvPath, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporaryPath);
        }
    }
}
