package repository;

import java.util.List;
import java.util.function.Predicate;

public interface CsvRepository<T> {
    List<T> findAll();
    T findById(String id);
    void save(T entity);
    void delete(String id);
    List<T> findByCondition(Predicate<T> predicate);
}
