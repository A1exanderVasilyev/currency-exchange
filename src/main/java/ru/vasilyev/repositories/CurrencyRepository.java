package ru.vasilyev.repositories;

import ru.vasilyev.models.Currency;

import java.sql.SQLException;
import java.util.Optional;

public interface CurrencyRepository extends Repository<Currency> {
    Optional<Currency> findByCode(String code) throws SQLException;
}
