package com.compliancesys.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import com.compliancesys.config.DatabaseConfig;
import com.compliancesys.dao.DriverDAO;
import com.compliancesys.model.Driver;

public class DriverDAOImpl implements DriverDAO {

    private static final Logger LOGGER = Logger.getLogger(DriverDAOImpl.class.getName());

    @Override
    public int create(Driver driver) throws SQLException {
        String sql = "INSERT INTO drivers (company_id, name, cpf, license_number, birth_date, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getInstance().getConnection();

             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, driver.getCompanyId());
            stmt.setString(2, driver.getName());
            stmt.setString(3, driver.getCpf());
            stmt.setString(4, driver.getLicenseNumber());
            stmt.setObject(5, driver.getBirthDate());
            stmt.setObject(6, driver.getCreatedAt());
            stmt.setObject(7, driver.getUpdatedAt());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Falha ao criar motorista, nenhuma linha afetada.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Falha ao criar motorista, nenhum ID obtido.");
                }
            }
        }
    }

    @Override
    public Optional<Driver> findById(int id) throws SQLException {
        String sql = "SELECT id, company_id, name, cpf, license_number, birth_date, created_at, updated_at FROM drivers WHERE id = ?";
        try (Connection conn = DatabaseConfig.getInstance().getConnection();

             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToDriver(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Driver> findByCpf(String cpf) throws SQLException {
        String sql = "SELECT id, company_id, name, cpf, license_number, birth_date, created_at, updated_at FROM drivers WHERE cpf = ?";
        try (Connection conn = DatabaseConfig.getInstance().getConnection();

             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToDriver(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Driver> findByLicenseNumber(String licenseNumber) throws SQLException {
        String sql = "SELECT id, company_id, name, cpf, license_number, birth_date, created_at, updated_at FROM drivers WHERE license_number = ?";
        try (Connection conn = DatabaseConfig.getInstance().getConnection();

             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, licenseNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToDriver(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Driver> findAll() throws SQLException {
        List<Driver> drivers = new ArrayList<>();
        String sql = "SELECT id, company_id, name, cpf, license_number, birth_date, created_at, updated_at FROM drivers";
        try (Connection conn = DatabaseConfig.getInstance().getConnection();

             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                drivers.add(mapResultSetToDriver(rs));
            }
        }
        return drivers;
    }

    @Override
    public boolean update(Driver driver) throws SQLException {
        String sql = "UPDATE drivers SET company_id = ?, name = ?, cpf = ?, license_number = ?, birth_date = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getInstance().getConnection();

             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, driver.getCompanyId());
            stmt.setString(2, driver.getName());
            stmt.setString(3, driver.getCpf());
            stmt.setString(4, driver.getLicenseNumber());
            stmt.setObject(5, driver.getBirthDate());
            stmt.setObject(6, driver.getUpdatedAt());
            stmt.setInt(7, driver.getId());

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM drivers WHERE id = ?";
        try (Connection conn = DatabaseConfig.getInstance().getConnection();

             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private Driver mapResultSetToDriver(ResultSet rs) throws SQLException {
        return new Driver(
                rs.getInt("id"),
                rs.getInt("company_id"),
                rs.getString("name"),
                rs.getString("cpf"),
                rs.getString("license_number"),
                rs.getObject("birth_date", LocalDate.class),
                rs.getObject("created_at", LocalDateTime.class),
                rs.getObject("updated_at", LocalDateTime.class)
        );
    }
}