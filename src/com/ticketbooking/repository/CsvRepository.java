package com.ticketbooking.repository;

import com.ticketbooking.model.BaseEntity;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public abstract class CsvRepository<T extends BaseEntity> {
    private Path filePath;

    public CsvRepository(String fileName) {
        this.filePath = Paths.get(fileName);
    }

    public abstract T parseLine(String line);

    public Path getFilePath() {
        return filePath;
    }

    public void setFilePath(Path filePath) {
        this.filePath = filePath;
    }

    public T findById(String id) throws IOException {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }
        String normalizedId = id.trim();
        return findFirst(entity -> normalizedId.equals(entity.getId())).orElse(null);
    }

    public List<T> findByCondition(Predicate<T> predicate) throws IOException {
        if (predicate == null) {
            throw new IllegalArgumentException("Search predicate must not be null.");
        }
        List<T> matches = new ArrayList<>();
        for (T entity : findAll()) {
            if (predicate.test(entity)) {
                matches.add(entity);
            }
        }
        return matches;
    }

    public boolean isHeaderLine(String line) {
        return false;
    }

    public List<T> findAll() throws IOException {
        List<T> entities = new ArrayList<>();
        if (!Files.exists(filePath)) {
            return entities;
        }

        for (String line : Files.readAllLines(filePath, StandardCharsets.UTF_8)) {
            if (!line.isEmpty() && line.charAt(0) == '\uFEFF') {
                line = line.substring(1);
            }
            if (!line.trim().isEmpty() && !isHeaderLine(line)) {
                entities.add(parseLine(line));
            }
        }
        return entities;
    }

    public Optional<T> findFirst(Predicate<T> predicate) throws IOException {
        if (predicate == null) {
            throw new IllegalArgumentException("Search predicate must not be null.");
        }
        for (T entity : findAll()) {
            if (predicate.test(entity)) {
                return Optional.of(entity);
            }
        }
        return Optional.empty();
    }

    public void save(T entity) throws IOException {
        if (entity == null || entity.getId() == null || entity.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("Entity and entity ID must not be empty");
        }
        List<T> entities = findAll();
        boolean replaced = false;
        for (int i = 0; i < entities.size(); i++) {
            if (entity.getId().equals(entities.get(i).getId())) {
                entities.set(i, entity);
                replaced = true;
                break;
            }
        }
        if (!replaced) {
            entities.add(entity);
        }
        writeAll(entities);
    }

    public boolean deleteById(String id) throws IOException {
        List<T> entities = findAll();
        boolean removed = false;
        for (int i = 0; i < entities.size(); i++) {
            if (id.equals(entities.get(i).getId())) {
                entities.remove(i);
                removed = true;
                break;
            }
        }
        if (removed) {
            writeAll(entities);
        }
        return removed;
    }

    public void writeAll(List<T> entities) throws IOException {
        Path parent = filePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        List<String> lines = new ArrayList<>();
        for (T entity : entities) {
            lines.add(entity.toCsvLine());
        }
        Files.write(filePath, lines, StandardCharsets.UTF_8);
    }
}