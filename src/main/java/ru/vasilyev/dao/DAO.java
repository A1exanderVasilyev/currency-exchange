package ru.vasilyev.dao;

import java.util.List;
import java.util.Optional;

public interface DAO<T> {
    T save(T entity);
    List<T> findAll();
    Optional<T> findById(int id);
    void update(T entity);
    boolean delete(int id);
    boolean existsById(int id);
}
