package com.stadium.repository.impl;

import com.stadium.model.interfaces.BaseEntity;
import com.stadium.repository.interfaces.CsvRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AbstractCsvRepository<T extends BaseEntity> implements CsvRepository<T> {
    private final List<T> entities = new ArrayList<>();

    @Override public List<T> findAll() { return List.copyOf(entities); }
    @Override public Optional<T> findById(String id) {
        return entities.stream().filter(entity -> entity.getId().equals(id)).findFirst();
    }
    @Override public boolean save(T entity) {
        if (entity == null || findById(entity.getId()).isPresent()) return false;
        return entities.add(entity);
    }
    @Override public boolean update(T entity) {
        if (entity == null) return false;
        for (int i = 0; i < entities.size(); i++) {
            if (entities.get(i).getId().equals(entity.getId())) {
                entities.set(i, entity);
                return true;
            }
        }
        return false;
    }
    @Override public boolean deleteById(String id) {
        return entities.removeIf(entity -> entity.getId().equals(id));
    }
}
