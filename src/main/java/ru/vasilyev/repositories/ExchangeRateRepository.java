package ru.vasilyev.repositories;

import ru.vasilyev.models.ExchangeRate;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ExchangeRateRepository extends Repository<ExchangeRate> {
    Optional<ExchangeRate> findByCodePair(String baseCurrencyCode,
                                          String targetCurrencyCode) throws SQLException;
    List<ExchangeRate> findAllPairsWithBaseCurrency(String baseCurrencyCode) throws SQLException;
}
