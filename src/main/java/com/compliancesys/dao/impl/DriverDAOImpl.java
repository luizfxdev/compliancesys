package com.compliancesys.dao.impl;

<<<<<<< Updated upstream
=======
import com.compliancesys.dao.DriverDAO;
import com.compliancesys.model.Driver;

import javax.sql.DataSource; // Importa DataSource
>>>>>>> Stashed changes
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement; // Importa Statement para getGeneratedKeys
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
<<<<<<< Updated upstream

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
=======
import java.util.logging.Level; // Adicionado para Logger
import java.util.logging.Logger; // Adicionado para Logger

public class DriverDAOImpl implements DriverDAO {

    private static final Logger LOGGER = Logger.getLogger(DriverDAOImpl.class.getName()); // Adicionado Logger
    private final DataSource dataSource; // Campo para o DataSource

    // Construtor que recebe o DataSource
    public DriverDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
>>>>>>> Stashed changes
    }

    @Override
    public int create(Driver driver) throws SQLException {
<<<<<<< Updated upstream
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
=======
        String sql = "INSERT INTO drivers (company_id, name, cpf, license_number, birth_date, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        int generatedId = -1;
        try (Connection conn = dataSource.getConnection(); // Usa dataSource para obter conexão
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) { // Retorna chaves geradas

            stmt.setInt(1, driver.getCompanyId());
            stmt.setString(2, driver.getName());
            stmt.setString(3, driver.getCpf());
            stmt.setString(4, driver.getLicenseNumber());
            stmt.setObject(5, driver.getBirthDate());
            stmt.setObject(6, LocalDateTime.now()); // Define created_at
            stmt.setObject(7, LocalDateTime.now()); // Define updated_at

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Falha ao criar motorista, nenhuma linha afetada.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    generatedId = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Falha ao criar motorista, nenhum ID gerado.");
>>>>>>> Stashed changes
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao criar motorista: " + e.getMessage(), e);
            throw e;
        }
<<<<<<< Updated upstream
        return -1;
=======
        return generatedId;
>>>>>>> Stashed changes
    }

    @Override
    public Optional<Driver> findById(int id) throws SQLException {
<<<<<<< Updated upstream
        String sql = "SELECT * FROM drivers WHERE id = ?";
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
=======
        String sql = "SELECT id, company_id, name, cpf, license_number, birth_date, created_at, updated_at FROM drivers WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
    public List<Driver> findAll() throws SQLException {
        List<Driver> drivers = new ArrayList<>();
        String sql = "SELECT * FROM drivers";
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
=======
    public Optional<Driver> findByCpf(String cpf) throws SQLException {
        String sql = "SELECT id, company_id, name, cpf, license_number, birth_date, created_at, updated_at FROM drivers WHERE cpf = ?";
        try (Connection conn = dataSource.getConnection();
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
    public List<Driver> findAll() throws SQLException {
        List<Driver> drivers = new ArrayList<>();
        String sql = "SELECT id, company_id, name, cpf, license_number, birth_date, created_at, updated_at FROM drivers";
        try (Connection conn = dataSource.getConnection();
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
        String sql = "UPDATE drivers SET name = ?, cpf = ?, birth_date = ?, license_number = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
=======
        String sql = "UPDATE drivers SET company_id = ?, name = ?, cpf = ?, license_number = ?, birth_date = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
>>>>>>> Stashed changes
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, driver.getName());
            stmt.setString(2, driver.getCpf());
            stmt.setObject(3, driver.getBirthDate());
            stmt.setString(4, driver.getLicenseNumber());
<<<<<<< Updated upstream
            stmt.setObject(5, LocalDateTime.now());
            stmt.setInt(6, driver.getId());
=======
            stmt.setObject(5, driver.getBirthDate());
            stmt.setObject(6, LocalDateTime.now()); // Atualiza updated_at
            stmt.setInt(7, driver.getId());

>>>>>>> Stashed changes
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao atualizar motorista: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM drivers WHERE id = ?";
<<<<<<< Updated upstream
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
=======
        try (Connection conn = dataSource.getConnection();
>>>>>>> Stashed changes
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao deletar motorista: " + e.getMessage(), e);
            throw e;
        }
    }

<<<<<<< Updated upstream
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
=======
    private Driver mapResultSetToDriver(ResultSet rs) throws SQLException {
        // Garanta que o construtor de Driver aceite esses parâmetros na ordem correta
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
>>>>>>> Stashed changes
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