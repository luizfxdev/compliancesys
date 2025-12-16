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
import com.compliancesys.util.ConnectionFactory; 

public class DriverDAOImpl implements DriverDAO {

    private static final Logger LOGGER = Logger.getLogger(DriverDAOImpl.class.getName());
    private final ConnectionFactory connectionFactory;

    public DriverDAOImpl(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public int create(Driver driver) throws SQLException {
        String sql = "INSERT INTO drivers (company_id, name, cpf, license_number, license_category, license_expiration, birth_date, phone, email, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = connectionFactory.getConnection();
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
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao criar motorista: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Optional<Driver> findById(int id) throws SQLException {
        String sql = "SELECT * FROM drivers WHERE id = ?";
        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
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
        String sql = "SELECT * FROM drivers";
        try (Connection conn = connectionFactory.getConnection();
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
        String sql = "UPDATE drivers SET company_id = ?, name = ?, cpf = ?, license_number = ?, license_category = ?, license_expiration = ?, birth_date = ?, phone = ?, email = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = connectionFactory.getConnection();
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
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao atualizar motorista: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM drivers WHERE id = ?";
        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao deletar motorista: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Optional<Driver> findByCpf(String cpf) throws SQLException {
        String sql = "SELECT * FROM drivers WHERE cpf = ?";
        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
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
        String sql = "SELECT * FROM drivers WHERE license_number = ?";
        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
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
        String sql = "SELECT * FROM drivers WHERE company_id = ?";
        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, companyId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    drivers.add(mapResultSetToDriver(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar motoristas por ID da empresa: " + e.getMessage(), e);
            throw e;
        }
        return drivers;
    }

    @Override
    public Optional<Driver> findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM drivers WHERE email = ?";
        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
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
        int id = rs.getInt("id");
        int companyId = rs.getInt("company_id");
        String name = rs.getString("name");
        String cpf = rs.getString("cpf");
        String licenseNumber = rs.getString("license_number");
        String licenseCategory = rs.getString("license_category");
        LocalDate licenseExpiration = rs.getObject("license_expiration", LocalDate.class);
        LocalDate birthDate = rs.getObject("birth_date", LocalDate.class);
        String phone = rs.getString("phone");
        String email = rs.getString("email");
        LocalDateTime createdAt = rs.getObject("created_at", LocalDateTime.class);
        LocalDateTime updatedAt = rs.getObject("updated_at", LocalDateTime.class);

        return new Driver(id, companyId, name, cpf, licenseNumber, licenseCategory,
                          licenseExpiration, birthDate, phone, email, createdAt, updatedAt);
    }
}
