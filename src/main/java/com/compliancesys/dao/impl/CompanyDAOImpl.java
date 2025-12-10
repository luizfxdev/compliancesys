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

import com.compliancesys.config.DatabaseConfig;
import com.compliancesys.dao.CompanyDAO;
import com.compliancesys.model.Company;

public class CompanyDAOImpl implements CompanyDAO {

    // Construtor padrão sem argumentos, conforme esperado pelos testes e Spring/CDI
    public CompanyDAOImpl() {
    }

    @Override
    public int create(Company company) throws SQLException {
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
                }
            }
        }
        return -1; // Indica falha na inserção
    }

    @Override
    public Optional<Company> findById(int id) throws SQLException {
        String sql = "SELECT * FROM companies WHERE id = ?";
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
    public List<Company> findAll() throws SQLException {
        List<Company> companies = new ArrayList<>();
        String sql = "SELECT * FROM companies";
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
            stmt.setString(2, company.getCnpj());
            stmt.setString(3, company.getEmail());
            stmt.setString(4, company.getPhone());
            stmt.setString(5, company.getAddress());
            stmt.setObject(6, LocalDateTime.now()); // Atualiza updated_at
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
