package ru.vasilyev.services;

import ru.vasilyev.dao.ExchangeRateDAO;
import ru.vasilyev.dao.ExchangeRateDAOImpl;
import ru.vasilyev.models.ExchangeRate;

import java.util.List;

public class ExchangeRateService {
    private static final ExchangeRateService INSTANCE = new ExchangeRateService();
    private final ExchangeRateDAO exchangeRateDAO = ExchangeRateDAOImpl.getInstance();

    private ExchangeRateService() {
    }

    public static ExchangeRateService getInstance() {
        return INSTANCE;
    }

    public List<ExchangeRate> getAllExchangeRates() {
        return exchangeRateDAO.findAll();
    }

    public ExchangeRate getExchangeRateByCodePair(String base, String target) {
        return exchangeRateDAO.findByCodePair(base, target).orElse(null);
    }

    public ExchangeRate saveExchangeRate(ExchangeRate exchangeRate) {
        return exchangeRateDAO.save(exchangeRate);
    }

    public boolean isExchangeRateExists(String base, String target) {
        return exchangeRateDAO.findByCodePair(base, target).isPresent();
    }

    public void updateExchangeRate(ExchangeRate exchangeRate) {
        exchangeRateDAO.update(exchangeRate);
    }
}
