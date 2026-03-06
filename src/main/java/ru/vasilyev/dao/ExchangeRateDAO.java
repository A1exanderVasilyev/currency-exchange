package ru.vasilyev.dao;

import ru.vasilyev.models.ExchangeRate;

import java.util.List;
import java.util.Optional;

public interface ExchangeRateDAO extends DAO<ExchangeRate> {
    Optional<ExchangeRate> findByCodePair(String baseCurrencyCode,
                                          String targetCurrencyCode);
    List<ExchangeRate> findAllPairsWithBaseCurrency(String baseCurrencyCode);
}
