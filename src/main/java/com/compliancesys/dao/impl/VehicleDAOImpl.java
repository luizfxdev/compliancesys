package com.compliancesys.dao.impl;

import com.compliancesys.config.DatabaseConfig;
import com.compliancesys.dao.VehicleDAO;
import com.compliancesys.model.Vehicle;
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

public class VehicleDAOImpl implements VehicleDAO {

    private static final Logger LOGGER = Logger.getLogger(VehicleDAOImpl.class.getName());

    @Override
    public int create(Vehicle vehicle) throws SQLException { // CORRIGIDO: Retorna int
        String sql = "INSERT INTO vehicles (company_id, plate, manufacturer, model, year, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, vehicle.getCompanyId());
            stmt.setString(2, vehicle.getPlate());
            stmt.setString(3, vehicle.getManufacturer()); // CORRIGIDO: getManufacturer()
            stmt.setString(4, vehicle.getModel());
            stmt.setInt(5, vehicle.getYear());
            stmt.setObject(6, vehicle.getCreatedAt());
            stmt.setObject(7, vehicle.getUpdatedAt());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Falha ao criar veículo, nenhuma linha afetada.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Falha ao criar veículo, nenhum ID obtido.");
                }
            }
        }
    }

    @Override
    public Optional<Vehicle> findById(int id) throws SQLException {
        String sql = "SELECT id, company_id, plate, manufacturer, model, year, created_at, updated_at FROM vehicles WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToVehicle(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Vehicle> findByPlate(String plate) throws SQLException {
        String sql = "SELECT id, company_id, plate, manufacturer, model, year, created_at, updated_at FROM vehicles WHERE plate = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, plate);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToVehicle(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Vehicle> findAll() throws SQLException {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT id, company_id, plate, manufacturer, model, year, created_at, updated_at FROM vehicles";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                vehicles.add(mapResultSetToVehicle(rs));
            }
        }
        return vehicles;
    }

    @Override
    public boolean update(Vehicle vehicle) throws SQLException {
        String sql = "UPDATE vehicles SET company_id = ?, plate = ?, manufacturer = ?, model = ?, year = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, vehicle.getCompanyId());
            stmt.setString(2, vehicle.getPlate());
            stmt.setString(3, vehicle.getManufacturer()); // CORRIGIDO: getManufacturer()
            stmt.setString(4, vehicle.getModel());
            stmt.setInt(5, vehicle.getYear());
            stmt.setObject(6, vehicle.getUpdatedAt());
            stmt.setInt(7, vehicle.getId());

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM vehicles WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private Vehicle mapResultSetToVehicle(ResultSet rs) throws SQLException {
        return new Vehicle(
                rs.getInt("id"),
                rs.getString("plate"),
                rs.getString("manufacturer"), // CORRIGIDO: manufacturer
                rs.getString("model"),
                rs.getInt("year"),
                rs.getInt("company_id"),
                rs.getObject("created_at", LocalDateTime.class),
                rs.getObject("updated_at", LocalDateTime.class)
        );
    }
}
