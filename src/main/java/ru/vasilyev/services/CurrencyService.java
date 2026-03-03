package ru.vasilyev.services;

import ru.vasilyev.dao.CurrencyDAO;
import ru.vasilyev.dao.CurrencyDAOImpl;
import ru.vasilyev.models.Currency;

import java.util.List;

public class CurrencyService {
    private static final CurrencyService INSTANCE = new CurrencyService();

    private final CurrencyDAO currencyDAO = CurrencyDAOImpl.getInstance();

    private CurrencyService() {
    }

    public static CurrencyService getInstance() {
        return INSTANCE;
    }

    public List<Currency> getAllCurrencies() {
        return currencyDAO.findAll();
    }
}
