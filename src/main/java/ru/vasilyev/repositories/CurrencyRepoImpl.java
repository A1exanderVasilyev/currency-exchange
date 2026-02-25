package ru.vasilyev.repositories;

import ru.vasilyev.models.Currency;
import ru.vasilyev.utils.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyRepoImpl implements CurrencyRepository {
    private static final CurrencyRepoImpl INSTANCE = new CurrencyRepoImpl();
    final String FIND_ALL_SQL = """
            SELECT
                c.id,
                c.code,
                c.fullname,
                c.sign
            FROM currencies c
            """;

    private CurrencyRepoImpl() {
    }

    public static CurrencyRepoImpl getInstance() {
        return INSTANCE;
    }

    @Override
    public Optional<Currency> findByCode(String code) {
        return Optional.empty();
    }

    @Override
    public Currency save(Currency entity) {
        final String INSERT_SQL = """
                INSERT INTO currencies(code, fullname, sign) 
                VALUES (?, ?, ?);
                """;
        try (Connection connection = ConnectionManager.get()) {
            PreparedStatement preparedStatement =
                    connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, entity.getCode());
            preparedStatement.setString(2, entity.getFullName());
            preparedStatement.setString(3, entity.getSign());

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
    public List<Currency> findAll() {
        List<Currency> currencies = new ArrayList<>();
        try (Connection connection = ConnectionManager.get()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(FIND_ALL_SQL);
            while (resultSet.next()) {
                Currency currency = new Currency(
                        resultSet.getInt("id"),
                        resultSet.getString("code"),
                        resultSet.getString("fullname"),
                        resultSet.getString("sign")
                );
                currencies.add(currency);
            }
            return currencies;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Currency> findById(int id) {
        final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
                WHERE c.id = ?;
                """;
        try (Connection connection = ConnectionManager.get()) {
            PreparedStatement preparedStatement =
                    connection.prepareStatement(FIND_BY_ID_SQL);
            preparedStatement.setInt(1, id);
            Currency currency = null;
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                currency = new Currency(
                        id,
                        resultSet.getString("code"),
                        resultSet.getString("fullname"),
                        resultSet.getString("sign")
                );
            }
            return Optional.ofNullable(currency);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Currency entity) {
        final String UPDATE_SQL = """
                UPDATE currencies
                SET code = ?,
                    fullname = ?,
                    sign = ?
                WHERE id = ?;
                """;
        try (Connection connection = ConnectionManager.get()) {
            PreparedStatement preparedStatement =
                    connection.prepareStatement(UPDATE_SQL);
            preparedStatement.setString(1, entity.getCode());
            preparedStatement.setString(2, entity.getFullName());
            preparedStatement.setString(3, entity.getSign());
            preparedStatement.setInt(4, entity.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean delete(int id) {
        final String DELETE_SQL = """
                DELETE FROM currencies
                WHERE id = ?
                """;

        try (Connection connection = ConnectionManager.get()) {
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_SQL);
            preparedStatement.setInt(1, id);
            int deleteCount = preparedStatement.executeUpdate();
            return deleteCount > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean existsById(int id) {
        return false;
    }
}
