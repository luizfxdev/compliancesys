package com.compliancesys.dao.impl;

import com.compliancesys.config.DatabaseConfig;
import com.compliancesys.dao.CompanyDAO;
import com.compliancesys.model.Company;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementação da interface CompanyDAO para operações de persistência da entidade Company.
 * Interage com o banco de dados PostgreSQL.
 */
public class CompanyDAOImpl implements CompanyDAO {

    private static final Logger LOGGER = Logger.getLogger(CompanyDAOImpl.class.getName());
    private final DatabaseConfig dbConfig;

    public CompanyDAOImpl() {
        this.dbConfig = DatabaseConfig.getInstance();
    }

    @Override
    public int create(Company company) throws SQLException {
        String sql = "INSERT INTO companies (cnpj, legal_name, trading_name, created_at, updated_at) VALUES (?, ?, ?, ?, ?) RETURNING id";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, company.getCnpj());
            stmt.setString(2, company.getLegalName());
            stmt.setString(3, company.getTradingName());
            stmt.setObject(4, company.getCreatedAt());
            stmt.setObject(5, company.getUpdatedAt());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
            return -1; // Indica falha na criação
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao criar empresa: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Optional<Company> findById(int id) throws SQLException {
        String sql = "SELECT id, cnpj, legal_name, trading_name, created_at, updated_at FROM companies WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToCompany(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar empresa por ID: " + e.getMessage(), e);
            throw e;
        }
        return Optional.empty();
    }

    @Override
    public Optional<Company> findByCnpj(String cnpj) throws SQLException {
        String sql = "SELECT id, cnpj, legal_name, trading_name, created_at, updated_at FROM companies WHERE cnpj = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cnpj);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToCompany(rs));
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
        String sql = "SELECT id, cnpj, legal_name, trading_name, created_at, updated_at FROM companies";
        try (Connection conn = dbConfig.getConnection();
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
        String sql = "UPDATE companies SET cnpj = ?, legal_name = ?, trading_name = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, company.getCnpj());
            stmt.setString(2, company.getLegalName());
            stmt.setString(3, company.getTradingName());
            stmt.setObject(4, LocalDateTime.now()); // Atualiza o updated_at automaticamente
            stmt.setInt(5, company.getId());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao atualizar empresa: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM companies WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao deletar empresa: " + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Mapeia um ResultSet para um objeto Company.
     * @param rs ResultSet contendo os dados da empresa.
     * @return Objeto Company.
     * @throws SQLException Se ocorrer um erro ao acessar os dados do ResultSet.
     */
    private Company mapResultSetToCompany(ResultSet rs) throws SQLException {
        Company company = new Company();
        company.setId(rs.getInt("id"));
        company.setCnpj(rs.getString("cnpj"));
        company.setLegalName(rs.getString("legal_name"));
        company.setTradingName(rs.getString("trading_name"));

        // Converte OffsetDateTime (timestamptz) para LocalDateTime
        OffsetDateTime createdAtOffset = rs.getObject("created_at", OffsetDateTime.class);
        company.setCreatedAt(createdAtOffset != null ? createdAtOffset.toLocalDateTime() : null);

        OffsetDateTime updatedAtOffset = rs.getObject("updated_at", OffsetDateTime.class);
        company.setUpdatedAt(updatedAtOffset != null ? updatedAtOffset.toLocalDateTime() : null);

        return company;
    }
}
