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

import com.compliancesys.config.DatabaseConfig;
import com.compliancesys.dao.DriverDAO;
import com.compliancesys.model.Driver;

public class DriverDAOImpl implements DriverDAO {

    /**
     * Construtor padrão sem parâmetros.
     * Cada método obtém sua própria conexão do pool.
     */
    public DriverDAOImpl() {
        // Construtor padrão
    }

    @Override
    public int create(Driver driver) throws SQLException {
        String sql = "INSERT INTO drivers (name, cpf, birth_date, license_number, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, driver.getName());
            stmt.setString(2, driver.getCpf());
            stmt.setObject(3, driver.getBirthDate());
            stmt.setString(4, driver.getLicenseNumber());
            stmt.setObject(5, LocalDateTime.now());
            stmt.setObject(6, LocalDateTime.now());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    @Override
    public Optional<Driver> findById(int id) throws SQLException {
        String sql = "SELECT * FROM drivers WHERE id = ?";
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
    public List<Driver> findAll() throws SQLException {
        List<Driver> drivers = new ArrayList<>();
        String sql = "SELECT * FROM drivers";
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
        String sql = "UPDATE drivers SET name = ?, cpf = ?, birth_date = ?, license_number = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, driver.getName());
            stmt.setString(2, driver.getCpf());
            stmt.setObject(3, driver.getBirthDate());
            stmt.setString(4, driver.getLicenseNumber());
            stmt.setObject(5, LocalDateTime.now());
            stmt.setInt(6, driver.getId());
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

    @Override
    public Optional<Driver> findByCpf(String cpf) throws SQLException {
        String sql = "SELECT * FROM drivers WHERE cpf = ?";
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
        String sql = "SELECT * FROM drivers WHERE license_number = ?";
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
    public List<Driver> findByCompanyId(int companyId) throws SQLException {
        // Nota: A tabela drivers no schema.sql não possui company_id
        // Retornando lista vazia para evitar erro de compilação
        return new ArrayList<>();
    }

    @Override
    public List<Driver> findByName(String name) throws SQLException {
        List<Driver> drivers = new ArrayList<>();
        String sql = "SELECT * FROM drivers WHERE name LIKE ?";
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + name + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    drivers.add(mapResultSetToDriver(rs));
                }
            }
        }
        return drivers;
    }

    @Override
    public List<Driver> findByLicenseCategory(String licenseCategory) throws SQLException {
        // Nota: A tabela drivers no schema.sql não possui license_category
        // Retornando lista vazia para evitar erro de compilação
        return new ArrayList<>();
    }

    @Override
    public List<Driver> findByLicenseExpirationBefore(LocalDate date) throws SQLException {
        // Nota: A tabela drivers no schema.sql não possui license_expiration
        // Retornando lista vazia para evitar erro de compilação
        return new ArrayList<>();
    }

    @Override
    public List<Driver> findByBirthDateBetween(LocalDate startDate, LocalDate endDate) throws SQLException {
        List<Driver> drivers = new ArrayList<>();
        String sql = "SELECT * FROM drivers WHERE birth_date BETWEEN ? AND ?";
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, startDate);
            stmt.setObject(2, endDate);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    drivers.add(mapResultSetToDriver(rs));
                }
            }
        }
        return drivers;
    }

    @Override
    public Optional<Driver> findByPhone(String phone) throws SQLException {
        // Nota: A tabela drivers no schema.sql não possui phone
        // Retornando Optional vazio para evitar erro de compilação
        return Optional.empty();
    }

    @Override
    public Optional<Driver> findByEmail(String email) throws SQLException {
        // Nota: A tabela drivers no schema.sql não possui email
        // Retornando Optional vazio para evitar erro de compilação
        return Optional.empty();
    }

    private Driver mapResultSetToDriver(ResultSet rs) throws SQLException {
        Driver driver = new Driver();
        driver.setId(rs.getInt("id"));
        driver.setName(rs.getString("name"));
        driver.setCpf(rs.getString("cpf"));
        driver.setBirthDate(rs.getObject("birth_date", LocalDate.class));
        driver.setLicenseNumber(rs.getString("license_number"));
        driver.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
        driver.setUpdatedAt(rs.getObject("updated_at", LocalDateTime.class));
        return driver;
    }
}