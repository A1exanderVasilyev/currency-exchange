package ru.vasilyev.repositories;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface Repository<T> {
    T save(T entity) throws SQLException;
    List<T> findAll() throws SQLException;
    Optional<T> findById(int id) throws SQLException;
    void update(T entity) throws SQLException;
    void delete(int id) throws SQLException;
    boolean existsById(int id) throws SQLException;
}
