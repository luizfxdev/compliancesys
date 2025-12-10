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

    public DriverDAOImpl() {
        // Construtor padrão
    }

    @Override
    public int create(Driver driver) throws SQLException {
        String sql = "INSERT INTO drivers (company_id, name, cpf, license_number, license_category, license_expiration, birth_date, phone, email, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, driver.getCompanyId());
            stmt.setString(2, driver.getName());
            stmt.setString(3, driver.getCpf());
            stmt.setString(4, driver.getLicenseNumber());
            stmt.setString(5, driver.getLicenseCategory());
            stmt.setObject(6, driver.getLicenseExpiration());
            stmt.setObject(7, driver.getBirthDate());
            stmt.setString(8, driver.getPhone());
            stmt.setString(9, driver.getEmail());
            stmt.setObject(10, LocalDateTime.now());
            stmt.setObject(11, LocalDateTime.now());
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
        String sql = "UPDATE drivers SET company_id = ?, name = ?, cpf = ?, license_number = ?, license_category = ?, license_expiration = ?, birth_date = ?, phone = ?, email = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, driver.getCompanyId());
            stmt.setString(2, driver.getName());
            stmt.setString(3, driver.getCpf());
            stmt.setString(4, driver.getLicenseNumber());
            stmt.setString(5, driver.getLicenseCategory());
            stmt.setObject(6, driver.getLicenseExpiration());
            stmt.setObject(7, driver.getBirthDate());
            stmt.setString(8, driver.getPhone());
            stmt.setString(9, driver.getEmail());
            stmt.setObject(10, LocalDateTime.now());
            stmt.setInt(11, driver.getId());
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
        List<Driver> drivers = new ArrayList<>();
        String sql = "SELECT * FROM drivers WHERE company_id = ?";
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, companyId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    drivers.add(mapResultSetToDriver(rs));
                }
            }
        }
        return drivers;
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
        List<Driver> drivers = new ArrayList<>();
        String sql = "SELECT * FROM drivers WHERE license_category = ?";
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, licenseCategory);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    drivers.add(mapResultSetToDriver(rs));
                }
            }
        }
        return drivers;
    }

    @Override
    public List<Driver> findByLicenseExpirationBefore(LocalDate date) throws SQLException {
        List<Driver> drivers = new ArrayList<>();
        String sql = "SELECT * FROM drivers WHERE license_expiration < ?";
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, date);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    drivers.add(mapResultSetToDriver(rs));
                }
            }
        }
        return drivers;
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
        String sql = "SELECT * FROM drivers WHERE phone = ?";
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, phone);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToDriver(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Driver> findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM drivers WHERE email = ?";
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToDriver(rs));
                }
            }
        }
        return Optional.empty();
    }

    private Driver mapResultSetToDriver(ResultSet rs) throws SQLException {
        return new Driver(
                rs.getInt("id"),
                rs.getInt("company_id"),
                rs.getString("name"),
                rs.getString("cpf"),
                rs.getString("license_number"),
                rs.getString("license_category"),
                rs.getObject("license_expiration", LocalDate.class),
                rs.getObject("birth_date", LocalDate.class),
                rs.getString("phone"),
                rs.getString("email"),
                rs.getObject("created_at", LocalDateTime.class),
                rs.getObject("updated_at", LocalDateTime.class)
        );
    }
}
