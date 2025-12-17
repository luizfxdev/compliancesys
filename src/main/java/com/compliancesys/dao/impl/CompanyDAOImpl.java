package com.compliancesys.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.compliancesys.dao.CompanyDAO;
import com.compliancesys.model.Company;
import com.compliancesys.util.ConnectionFactory; 
public class CompanyDAOImpl implements CompanyDAO {
    private static final Logger LOGGER = Logger.getLogger(CompanyDAOImpl.class.getName());
    private final ConnectionFactory connectionFactory; 
    public CompanyDAOImpl(ConnectionFactory connectionFactory) { 
        this.connectionFactory = connectionFactory;
    }

    @Override
    public int create(Company company) throws SQLException {
        String sql = "INSERT INTO companies (cnpj, legal_name, trading_name, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = connectionFactory.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            LocalDateTime now = LocalDateTime.now();
            stmt.setString(1, company.getCnpj());
            stmt.setString(2, company.getLegalName());
            stmt.setString(3, company.getTradingName());
            stmt.setObject(4, now);
            stmt.setObject(5, now);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Falha ao criar empresa, nenhuma linha afetada.");
            }
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Falha ao criar empresa, nenhum ID gerado.");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao criar empresa: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Optional<Company> findById(int id) throws SQLException {
        String sql = "SELECT id, cnpj, legal_name, trading_name, created_at, updated_at FROM companies WHERE id = ?";
        try (Connection conn = connectionFactory.getConnection();
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
    public List<Company> findAll() throws SQLException {
        List<Company> companies = new ArrayList<>();
        String sql = "SELECT id, cnpj, legal_name, trading_name, created_at, updated_at FROM companies";
        try (Connection conn = connectionFactory.getConnection(); 
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
        String sql = "UPDATE companies SET cnpj = ?, legal_name = ?, trading_name = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = connectionFactory.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, company.getCnpj());
            stmt.setString(2, company.getLegalName());
            stmt.setString(3, company.getTradingName());
            stmt.setObject(4, LocalDateTime.now());
            stmt.setInt(5, company.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao atualizar empresa: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM companies WHERE id = ?";
        try (Connection conn = connectionFactory.getConnection(); 
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
        String sql = "SELECT id, cnpj, legal_name, trading_name, created_at, updated_at FROM companies WHERE cnpj = ?";
        try (Connection conn = connectionFactory.getConnection();
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

  
    private Company mapResultSetToCompany(ResultSet rs) throws SQLException {
        return new Company(
                rs.getInt("id"),
                rs.getString("cnpj"),
                rs.getString("legal_name"),
                rs.getString("trading_name"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getTimestamp("updated_at").toLocalDateTime()
        );
    }
}
