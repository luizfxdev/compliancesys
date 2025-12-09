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
import java.util.logging.Logger;

import com.compliancesys.config.DatabaseConfig;
import com.compliancesys.dao.CompanyDAO;
import com.compliancesys.model.Company;

public class CompanyDAOImpl implements CompanyDAO {

    private static final Logger LOGGER = Logger.getLogger(CompanyDAOImpl.class.getName());

    @Override
    public int create(Company company) throws SQLException {
        String sql = "INSERT INTO companies (name, cnpj, email, phone, address, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getInstance().getConnection();


             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, company.getName());
            stmt.setString(2, company.getCnpj()); // CORRIGIDO: getCnpj()
            stmt.setString(3, company.getEmail()); // CORRIGIDO: getEmail()
            stmt.setString(4, company.getPhone());
            stmt.setString(5, company.getAddress());
            stmt.setObject(6, company.getCreatedAt());
            stmt.setObject(7, company.getUpdatedAt());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Falha ao criar empresa, nenhuma linha afetada.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Falha ao criar empresa, nenhum ID obtido.");
                }
            }
        }
    }

    @Override
    public Optional<Company> findById(int id) throws SQLException {
        String sql = "SELECT id, name, cnpj, email, phone, address, created_at, updated_at FROM companies WHERE id = ?";
        try (Connection conn = DatabaseConfig.getInstance().getConnection();

             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCompany(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Company> findByCnpj(String cnpj) throws SQLException {
        String sql = "SELECT id, name, cnpj, email, phone, address, created_at, updated_at FROM companies WHERE cnpj = ?";
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
    public List<Company> findAll() throws SQLException {
        List<Company> companies = new ArrayList<>();
        String sql = "SELECT id, name, cnpj, email, phone, address, created_at, updated_at FROM companies";
        try (Connection conn = DatabaseConfig.getInstance().getConnection();

             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                companies.add(mapResultSetToCompany(rs));
            }
        }
        return companies;
    }

    @Override
    public boolean update(Company company) throws SQLException {
        String sql = "UPDATE companies SET name = ?, cnpj = ?, email = ?, phone = ?, address = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getInstance().getConnection();

             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, company.getName());
            stmt.setString(2, company.getCnpj()); // CORRIGIDO: getCnpj()
            stmt.setString(3, company.getEmail()); // CORRIGIDO: getEmail()
            stmt.setString(4, company.getPhone());
            stmt.setString(5, company.getAddress());
            stmt.setObject(6, company.getUpdatedAt());
            stmt.setInt(7, company.getId());

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM companies WHERE id = ?";
        try (Connection conn = DatabaseConfig.getInstance().getConnection();

             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private Company mapResultSetToCompany(ResultSet rs) throws SQLException {
        return new Company(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("cnpj"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getString("address"),
                rs.getObject("created_at", LocalDateTime.class),
                rs.getObject("updated_at", LocalDateTime.class)
        );
    }
}
