package ru.vasilyev.repositories;

import ru.vasilyev.models.Currency;
import ru.vasilyev.models.ExchangeRate;
import ru.vasilyev.utils.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRateRepoImpl implements ExchangeRateRepository {
    private static final ExchangeRateRepoImpl INSTANCE = new ExchangeRateRepoImpl();

    private ExchangeRateRepoImpl() {
    }

    public static ExchangeRateRepoImpl getInstance() {
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
    public Optional<ExchangeRate> findByCodePair(String baseCurrencyCode, String targetCurrencyCode) {
        final String FIND_BY_CODE_PAIR = FIND_ALL_SQL + """
                WHERE bc.code = ? AND tc.code = ?;
                """;
        try (Connection connection = ConnectionManager.get()) {
            PreparedStatement preparedStatement =
                    connection.prepareStatement(FIND_BY_CODE_PAIR);
            preparedStatement.setString(1, baseCurrencyCode);
            preparedStatement.setString(2, targetCurrencyCode);
            ResultSet resultSet = preparedStatement.executeQuery();
            ExchangeRate exchangeRate = null;
            if (resultSet.next()) {
                exchangeRate = buildExchangeRate(resultSet);
            }
            return Optional.ofNullable(exchangeRate);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ExchangeRate> findAllPairsWithBaseCurrency(String baseCurrencyCode) {
        final String FIND_ALL_PAIRS_WITH_BASE_CURRENCY = FIND_ALL_SQL + """
                WHERE bc.code = ?;
                """;
        try (Connection connection = ConnectionManager.get()) {
            PreparedStatement preparedStatement =
                    connection.prepareStatement(FIND_ALL_PAIRS_WITH_BASE_CURRENCY);
            preparedStatement.setString(1, baseCurrencyCode);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<ExchangeRate> exchangeRates = new ArrayList<>();
            while (resultSet.next()) {
                exchangeRates.add(buildExchangeRate(resultSet));
            }
            return exchangeRates;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
            preparedStatement.setInt(1, entity.getBaseCurrency().getId());
            preparedStatement.setInt(2, entity.getTargetCurrency().getId());
            preparedStatement.setBigDecimal(3, entity.getRate());
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
                exchangeRates.add(buildExchangeRate(resultSet));
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
                exchangeRate = buildExchangeRate(resultSet);
            }
            return Optional.ofNullable(exchangeRate);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(ExchangeRate entity) {
        final String UPDATE_SQL = """
                UPDATE exchangerates
                SET rate = ?
                WHERE id = ?;
                """;
        try (Connection connection = ConnectionManager.get()) {
            PreparedStatement preparedStatement =
                    connection.prepareStatement(UPDATE_SQL);
            preparedStatement.setBigDecimal(1, entity.getRate());
            preparedStatement.setInt(2, entity.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean delete(int id) {
        final String DELETE_SQL = """
                DELETE
                FROM exchangerates
                WHERE id = ?
                """;
        try (Connection connection = ConnectionManager.get()) {
            PreparedStatement preparedStatement =
                    connection.prepareStatement(DELETE_SQL);
            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean existsById(int id) {
        return findById(id).isPresent();
    }

    private static ExchangeRate buildExchangeRate(ResultSet resultSet) throws SQLException {
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
        return new ExchangeRate(
                resultSet.getInt("id"),
                baseCurrency,
                targetCurrency,
                resultSet.getBigDecimal("rate")
        );
    }
}
