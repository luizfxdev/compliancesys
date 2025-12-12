package com.compliancesys.dao.impl;

import com.compliancesys.dao.CompanyDAO;
import com.compliancesys.model.Company;
import javax.sql.DataSource; // Importa DataSource
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
<<<<<<< Updated upstream

import com.compliancesys.config.DatabaseConfig;
import com.compliancesys.dao.CompanyDAO;
import com.compliancesys.model.Company;
=======
import java.util.logging.Level; // Adicionado para Logger
import java.util.logging.Logger; // Adicionado para Logger
>>>>>>> Stashed changes

/**
 * Implementação do Data Access Object (DAO) para a entidade Company.
 * Gerencia a persistência de dados de empresas no banco de dados.
 */
public class CompanyDAOImpl implements CompanyDAO {

<<<<<<< Updated upstream
    // Construtor padrão sem argumentos, conforme esperado pelos testes e Spring/CDI
    public CompanyDAOImpl() {
=======
    private static final Logger LOGGER = Logger.getLogger(CompanyDAOImpl.class.getName()); // Adicionado Logger
    private final DataSource dataSource; // Injeção do DataSource

    // Construtor que recebe o DataSource
    public CompanyDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
>>>>>>> Stashed changes
    }

    @Override
    public int create(Company company) throws SQLException {
<<<<<<< Updated upstream
        String sql = "INSERT INTO companies (name, cnpj, email, phone, address, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, company.getName());
            stmt.setString(2, company.getCnpj());
            stmt.setString(3, company.getEmail());
            stmt.setString(4, company.getPhone());
            stmt.setString(5, company.getAddress());
            stmt.setObject(6, LocalDateTime.now());
            stmt.setObject(7, LocalDateTime.now());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
=======
        String sql = "INSERT INTO companies (cnpj, legal_name, trading_name, email, phone, address, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection(); // Usa o DataSource
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            LocalDateTime now = LocalDateTime.now();
            stmt.setString(1, company.getCnpj());
            stmt.setString(2, company.getLegalName());
            stmt.setString(3, company.getTradingName());
            stmt.setString(4, company.getEmail());
            stmt.setString(5, company.getPhone());
            stmt.setString(6, company.getAddress());
            stmt.setObject(7, now); // created_at
            stmt.setObject(8, now); // updated_at

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Falha ao criar empresa, nenhuma linha afetada.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Falha ao criar empresa, nenhum ID gerado.");
>>>>>>> Stashed changes
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao criar empresa: " + e.getMessage(), e);
            throw e;
        }
        return -1; // Indica falha na inserção
    }

    @Override
    public Optional<Company> findById(int id) throws SQLException {
<<<<<<< Updated upstream
        String sql = "SELECT * FROM companies WHERE id = ?";
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
=======
        String sql = "SELECT id, cnpj, legal_name, trading_name, email, phone, address, created_at, updated_at FROM companies WHERE id = ?";
        try (Connection conn = dataSource.getConnection(); // Usa o DataSource
>>>>>>> Stashed changes
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCompany(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar empresa por ID: " + e.getMessage(), e);
            throw e;
        }
        return Optional.empty();
    }

    @Override
<<<<<<< Updated upstream
    public List<Company> findAll() throws SQLException {
        List<Company> companies = new ArrayList<>();
        String sql = "SELECT * FROM companies";
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
=======
    public Optional<Company> findByCnpj(String cnpj) throws SQLException {
        String sql = "SELECT id, cnpj, legal_name, trading_name, email, phone, address, created_at, updated_at FROM companies WHERE cnpj = ?";
        try (Connection conn = dataSource.getConnection(); // Usa o DataSource
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cnpj);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCompany(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar empresa por CNPJ: " + e.getMessage(), e);
            throw e;
        }
        return Optional.empty();
    }

    @Override
    public List<Company> findAll() throws SQLException {
        List<Company> companies = new ArrayList<>();
        String sql = "SELECT id, cnpj, legal_name, trading_name, email, phone, address, created_at, updated_at FROM companies";
        try (Connection conn = dataSource.getConnection(); // Usa o DataSource
>>>>>>> Stashed changes
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                companies.add(mapResultSetToCompany(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar todas as empresas: " + e.getMessage(), e);
            throw e;
        }
        return companies;
    }

    @Override
    public boolean update(Company company) throws SQLException {
<<<<<<< Updated upstream
        String sql = "UPDATE companies SET name = ?, cnpj = ?, email = ?, phone = ?, address = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, company.getName());
            stmt.setString(2, company.getCnpj());
            stmt.setString(3, company.getEmail());
            stmt.setString(4, company.getPhone());
            stmt.setString(5, company.getAddress());
            stmt.setObject(6, LocalDateTime.now()); // Atualiza updated_at
            stmt.setInt(7, company.getId());
=======
        String sql = "UPDATE companies SET cnpj = ?, legal_name = ?, trading_name = ?, email = ?, phone = ?, address = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection(); // Usa o DataSource
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, company.getCnpj());
            stmt.setString(2, company.getLegalName());
            stmt.setString(3, company.getTradingName());
            stmt.setString(4, company.getEmail());
            stmt.setString(5, company.getPhone());
            stmt.setString(6, company.getAddress());
            stmt.setObject(7, LocalDateTime.now()); // updated_at
            stmt.setInt(8, company.getId());

>>>>>>> Stashed changes
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao atualizar empresa: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM companies WHERE id = ?";
<<<<<<< Updated upstream
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
=======
        try (Connection conn = dataSource.getConnection(); // Usa o DataSource
>>>>>>> Stashed changes
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao deletar empresa: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Optional<Company> findByCnpj(String cnpj) throws SQLException {
        String sql = "SELECT * FROM companies WHERE cnpj = ?";
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cnpj);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCompany(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Company> findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM companies WHERE email = ?";
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCompany(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Company> findByPhone(String phone) throws SQLException {
        String sql = "SELECT * FROM companies WHERE phone = ?";
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, phone);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCompany(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Company> findByName(String name) throws SQLException { // CORRIGIDO: Retorna Optional<Company>
        String sql = "SELECT * FROM companies WHERE name = ?"; // Busca exata por nome para retornar Optional
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCompany(rs));
                }
            }
        }
        return Optional.empty();
    }

    private Company mapResultSetToCompany(ResultSet rs) throws SQLException {
        // Mapeamento ajustado para incluir trading_name e garantir a ordem correta
        return new Company(
                rs.getInt("id"),
                rs.getString("cnpj"),
                rs.getString("legal_name"),
                rs.getString("trading_name"), // Adicionado
                rs.getString("email"),
                rs.getString("phone"),
                rs.getString("address"),
                rs.getObject("created_at", LocalDateTime.class),
                rs.getObject("updated_at", LocalDateTime.class)
        );
    }
}
