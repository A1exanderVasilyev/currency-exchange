package ru.vasilyev.repositories;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface Repository<T> {
    T save(T entity);
    List<T> findAll();
    Optional<T> findById(int id);
    void update(T entity);
    boolean delete(int id);
    boolean existsById(int id);
}
