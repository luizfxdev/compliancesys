package com.compliancesys.dao.impl;

import com.compliancesys.config.DatabaseConfig;
import com.compliancesys.dao.DriverDAO;
import com.compliancesys.model.Driver;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementação da interface DriverDAO para operações de persistência da entidade Driver.
 * Interage com o banco de dados PostgreSQL.
 */
public class DriverDAOImpl implements DriverDAO {

    private static final Logger LOGGER = Logger.getLogger(DriverDAOImpl.class.getName());
    private final DatabaseConfig dbConfig;

    public DriverDAOImpl() {
        this.dbConfig = DatabaseConfig.getInstance();
    }

    @Override
    public int create(Driver driver) throws SQLException {
        String sql = "INSERT INTO drivers (name, cpf, birth_date, license_number, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, driver.getName());
            stmt.setString(2, driver.getCpf());
            stmt.setObject(3, driver.getBirthDate()); // LocalDate
            stmt.setString(4, driver.getLicenseNumber());
            stmt.setObject(5, driver.getCreatedAt());
            stmt.setObject(6, driver.getUpdatedAt());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
            return -1; // Indica falha na criação
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao criar motorista: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Optional<Driver> findById(int id) throws SQLException {
        String sql = "SELECT id, name, cpf, birth_date, license_number, created_at, updated_at FROM drivers WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToDriver(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar motorista por ID: " + e.getMessage(), e);
            throw e;
        }
        return Optional.empty();
    }

    @Override
    public Optional<Driver> findByCpf(String cpf) throws SQLException {
        String sql = "SELECT id, name, cpf, birth_date, license_number, created_at, updated_at FROM drivers WHERE cpf = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpf);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToDriver(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar motorista por CPF: " + e.getMessage(), e);
            throw e;
        }
        return Optional.empty();
    }

    @Override
    public List<Driver> findAll() throws SQLException {
        List<Driver> drivers = new ArrayList<>();
        String sql = "SELECT id, name, cpf, birth_date, license_number, created_at, updated_at FROM drivers";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                drivers.add(mapResultSetToDriver(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar todos os motoristas: " + e.getMessage(), e);
            throw e;
        }
        return drivers;
    }

    @Override
    public boolean update(Driver driver) throws SQLException {
        String sql = "UPDATE drivers SET name = ?, cpf = ?, birth_date = ?, license_number = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, driver.getName());
            stmt.setString(2, driver.getCpf());
            stmt.setObject(3, driver.getBirthDate());
            stmt.setString(4, driver.getLicenseNumber());
            stmt.setObject(5, LocalDateTime.now()); // Atualiza o updated_at automaticamente
            stmt.setInt(6, driver.getId());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao atualizar motorista: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM drivers WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao deletar motorista: " + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Mapeia um ResultSet para um objeto Driver.
     * @param rs ResultSet contendo os dados do motorista.
     * @return Objeto Driver.
     * @throws SQLException Se ocorrer um erro ao acessar os dados do ResultSet.
     */
    private Driver mapResultSetToDriver(ResultSet rs) throws SQLException {
        Driver driver = new Driver();
        driver.setId(rs.getInt("id"));
        driver.setName(rs.getString("name"));
        driver.setCpf(rs.getString("cpf"));
        driver.setBirthDate(rs.getObject("birth_date", LocalDate.class)); // LocalDate
        driver.setLicenseNumber(rs.getString("license_number"));

        // Converte OffsetDateTime (timestamptz) para LocalDateTime
        OffsetDateTime createdAtOffset = rs.getObject("created_at", OffsetDateTime.class);
        driver.setCreatedAt(createdAtOffset != null ? createdAtOffset.toLocalDateTime() : null);

        OffsetDateTime updatedAtOffset = rs.getObject("updated_at", OffsetDateTime.class);
        driver.setUpdatedAt(updatedAtOffset != null ? updatedAtOffset.toLocalDateTime() : null);

        return driver;
    }
}
