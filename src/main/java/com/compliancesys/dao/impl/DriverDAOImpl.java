// src/main/java/com/compliancesys/dao/impl/DriverDAOImpl.java
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
import java.util.logging.Level;
import java.util.logging.Logger;

import com.compliancesys.dao.DriverDAO;
import com.compliancesys.model.Driver;

public class DriverDAOImpl implements DriverDAO {
    private static final Logger LOGGER = Logger.getLogger(DriverDAOImpl.class.getName());
    private final Connection connection;

    public DriverDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public int create(Driver driver) throws SQLException {
        String sql = "INSERT INTO drivers (name, cpf, birth_date, license_number, license_category, license_expiration, phone, email, company_id, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            LocalDateTime now = LocalDateTime.now();
            stmt.setString(1, driver.getName());
            stmt.setString(2, driver.getCpf());
            stmt.setObject(3, driver.getBirthDate());
            stmt.setString(4, driver.getLicenseNumber());
            stmt.setString(5, driver.getLicenseCategory());
            stmt.setObject(6, driver.getLicenseExpiration());
            stmt.setString(7, driver.getPhone());
            stmt.setString(8, driver.getEmail());
            stmt.setInt(9, driver.getCompanyId()); // Adicionado company_id
            stmt.setObject(10, now);
            stmt.setObject(11, now);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Falha ao criar motorista, nenhuma linha afetada");
            }
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Falha ao criar motorista, nenhum ID gerado");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao criar motorista: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Optional<Driver> findById(int id) throws SQLException {
        String sql = "SELECT id, name, cpf, birth_date, license_number, license_category, license_expiration, phone, email, company_id, created_at, updated_at FROM drivers WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToDriver(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar motorista por ID: " + e.getMessage(), e);
            throw e;
        }
        return Optional.empty();
    }

    @Override
    public List<Driver> findAll() throws SQLException {
        List<Driver> drivers = new ArrayList<>();
        String sql = "SELECT id, name, cpf, birth_date, license_number, license_category, license_expiration, phone, email, company_id, created_at, updated_at FROM drivers";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
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
        String sql = "UPDATE drivers SET name = ?, cpf = ?, birth_date = ?, license_number = ?, license_category = ?, license_expiration = ?, phone = ?, email = ?, company_id = ?, updated_at = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, driver.getName());
            stmt.setString(2, driver.getCpf());
            stmt.setObject(3, driver.getBirthDate());
            stmt.setString(4, driver.getLicenseNumber());
            stmt.setString(5, driver.getLicenseCategory());
            stmt.setObject(6, driver.getLicenseExpiration());
            stmt.setString(7, driver.getPhone());
            stmt.setString(8, driver.getEmail());
            stmt.setInt(9, driver.getCompanyId()); // Adicionado company_id
            stmt.setObject(10, LocalDateTime.now());
            stmt.setInt(11, driver.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao atualizar motorista: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM drivers WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao deletar motorista: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Optional<Driver> findByCpf(String cpf) throws SQLException {
        String sql = "SELECT id, name, cpf, birth_date, license_number, license_category, license_expiration, phone, email, company_id, created_at, updated_at FROM drivers WHERE cpf = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToDriver(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar motorista por CPF: " + e.getMessage(), e);
            throw e;
        }
        return Optional.empty();
    }

    @Override
    public Optional<Driver> findByLicenseNumber(String licenseNumber) throws SQLException {
        String sql = "SELECT id, name, cpf, birth_date, license_number, license_category, license_expiration, phone, email, company_id, created_at, updated_at FROM drivers WHERE license_number = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, licenseNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToDriver(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar motorista por número de licença: " + e.getMessage(), e);
            throw e;
        }
        return Optional.empty();
    }

    @Override
    public List<Driver> findByCompanyId(int companyId) throws SQLException {
        List<Driver> drivers = new ArrayList<>();
        String sql = "SELECT id, name, cpf, birth_date, license_number, license_category, license_expiration, phone, email, company_id, created_at, updated_at FROM drivers WHERE company_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, companyId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    drivers.add(mapResultSetToDriver(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar motoristas por Company ID: " + e.getMessage(), e);
            throw e;
        }
        return drivers;
    }

    @Override
    public Optional<Driver> findByEmail(String email) throws SQLException {
        String sql = "SELECT id, name, cpf, birth_date, license_number, license_category, license_expiration, phone, email, company_id, created_at, updated_at FROM drivers WHERE email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToDriver(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar motorista por email: " + e.getMessage(), e);
            throw e;
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
