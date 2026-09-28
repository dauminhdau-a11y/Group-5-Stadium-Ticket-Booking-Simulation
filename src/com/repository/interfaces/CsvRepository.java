package com.repository.interfaces;

import java.util.List;
import java.util.Optional;

import com.model.interfaces.BaseEntity;

public interface CsvRepository<T extends BaseEntity> {
    List<T> findAll();
    Optional<T> findById(String id);
    boolean save(T entity);
    boolean update(T entity);
    boolean deleteById(String id);
}
