package ru.vasilyev.dao;

import ru.vasilyev.models.Currency;

import java.sql.SQLException;
import java.util.Optional;

public interface CurrencyDAO extends DAO<Currency> {
    Optional<Currency> findByCode(String code) throws RuntimeException;
}
