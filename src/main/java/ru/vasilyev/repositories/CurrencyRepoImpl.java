package ru.vasilyev.repositories;

import ru.vasilyev.models.Currency;
import ru.vasilyev.utils.ConnectionManager;

import java.sql.*;
import java.util.List;
import java.util.Optional;

public class CurrencyRepoImpl implements CurrencyRepository {
    private static final CurrencyRepoImpl INSTANCE = new CurrencyRepoImpl();

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
        return List.of();
    }

    @Override
    public Optional<Currency> findById(int id) {
        return Optional.empty();
    }

    @Override
    public void update(Currency entity) {

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
