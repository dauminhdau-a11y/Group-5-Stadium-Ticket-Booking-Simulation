package com.stadium.repository.interfaces;

import com.stadium.model.interfaces.BaseEntity;
import java.util.List;
import java.util.Optional;

public interface CsvRepository<T extends BaseEntity> {
    List<T> findAll();
    Optional<T> findById(String id);
    boolean save(T entity);
    boolean update(T entity);
    boolean deleteById(String id);
}
