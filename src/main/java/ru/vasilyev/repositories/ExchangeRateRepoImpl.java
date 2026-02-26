package ru.vasilyev.repositories;

import ru.vasilyev.models.Currency;
import ru.vasilyev.models.ExchangeRate;
import ru.vasilyev.utils.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRateRepoImpl implements ExchangeRateRepository {
    public static final ExchangeRateRepoImpl INSTANCE = new ExchangeRateRepoImpl();

    private ExchangeRateRepoImpl() {
    }

    public ExchangeRateRepoImpl getInstance() {
        return INSTANCE;
    }

    final String FIND_ALL_SQL = """
            SELECT
                er.id,
                bc.id as baseId,
                bc.code as baseCode,
                bc.fullname as baseFullName,
                bc.sign as baseSign,
                tc.id as targetId,
                tc.code as targetCode,
                tc.fullname as targetFullName,
                tc.sign as targetSign,
                er.rate
            FROM exchangerates er
            JOIN currencies bc ON er.basecurrencyid = bc.id
            JOIN currencies tc ON er.targetcurrencyid = tc.id
            """;

    @Override
    public Optional<ExchangeRate> findByCodePair(String baseCurrencyCode, String targetCurrencyCode) throws SQLException {
        return Optional.empty();
    }

    @Override
    public List<ExchangeRate> findAllPairsWithBaseCurrency(String baseCurrencyCode) throws SQLException {
        return List.of();
    }

    @Override
    public ExchangeRate save(ExchangeRate entity) {
        final String SAVE_SQL = """
                INSERT INTO exchangerates(basecurrencyid, targetcurrencyid, rate)
                VALUES (?, ?, ?)
                """;
        try (Connection connection = ConnectionManager.get()) {
            PreparedStatement preparedStatement =
                    connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                entity.setId(generatedKeys.getInt("id"));
            }
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ExchangeRate> findAll() {
        List<ExchangeRate> exchangeRates = new ArrayList<>();
        try (Connection connection = ConnectionManager.get()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(FIND_ALL_SQL);
            while (resultSet.next()) {
                Currency baseCurrency = new Currency(
                        resultSet.getInt("baseId"),
                        resultSet.getString("baseCode"),
                        resultSet.getString("baseFullName"),
                        resultSet.getString("baseSign")
                );
                Currency targetCurrency = new Currency(
                        resultSet.getInt("targetId"),
                        resultSet.getString("targetCode"),
                        resultSet.getString("targetFullName"),
                        resultSet.getString("targetSign")
                );
                ExchangeRate exchangeRate = new ExchangeRate(
                        resultSet.getInt("id"),
                        baseCurrency,
                        targetCurrency,
                        resultSet.getBigDecimal("rate")
                );
                exchangeRates.add(exchangeRate);
            }
            return exchangeRates;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<ExchangeRate> findById(int id) {
        final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
                WHERE er.id = ?;
                """;
        try (Connection connection = ConnectionManager.get()) {
            PreparedStatement preparedStatement =
                    connection.prepareStatement(FIND_BY_ID_SQL);
            preparedStatement.setInt(1, id);
            ExchangeRate exchangeRate = null;
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Currency baseCurrency = new Currency(
                        resultSet.getInt("baseId"),
                        resultSet.getString("baseCode"),
                        resultSet.getString("baseFullName"),
                        resultSet.getString("baseSign")
                );
                Currency targetCurrency = new Currency(
                        resultSet.getInt("targetId"),
                        resultSet.getString("targetCode"),
                        resultSet.getString("targetFullName"),
                        resultSet.getString("targetSign")
                );
                exchangeRate = new ExchangeRate(
                        resultSet.getInt("id"),
                        baseCurrency,
                        targetCurrency,
                        resultSet.getBigDecimal("rate")
                );
            }
            return Optional.ofNullable(exchangeRate);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(ExchangeRate entity) {

    }

    @Override
    public boolean delete(int id) {
        return false;
    }

    @Override
    public boolean existsById(int id) {
        return false;
    }
}
