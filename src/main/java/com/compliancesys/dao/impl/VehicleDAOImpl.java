// src/main/java/com/compliancesys/dao/impl/VehicleDAOImpl.java
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

import com.compliancesys.dao.VehicleDAO;
import com.compliancesys.model.Vehicle;

public class VehicleDAOImpl implements VehicleDAO {
    private static final Logger LOGGER = Logger.getLogger(VehicleDAOImpl.class.getName());
    private final Connection connection;

    public VehicleDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public int create(Vehicle vehicle) throws SQLException {
        String sql = "INSERT INTO vehicles (plate, manufacturer, model, year, company_id, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, vehicle.getPlate());
            stmt.setString(2, vehicle.getManufacturer());
            stmt.setString(3, vehicle.getModel());
            stmt.setInt(4, vehicle.getYear());
            stmt.setInt(5, vehicle.getCompanyId());
            stmt.setObject(6, LocalDateTime.now());
            stmt.setObject(7, LocalDateTime.now());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                LOGGER.log(Level.SEVERE, "Falha ao criar veículo, nenhuma linha afetada");
                throw new SQLException("Falha ao criar veículo, nenhuma linha afetada");
            }
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    LOGGER.log(Level.SEVERE, "Falha ao criar veículo, nenhum ID obtido");
                    throw new SQLException("Falha ao criar veículo, nenhum ID obtido");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao criar veículo: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Optional<Vehicle> findById(int id) throws SQLException {
        String sql = "SELECT id, plate, manufacturer, model, year, company_id, created_at, updated_at FROM vehicles WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToVehicle(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar veículo por ID: " + e.getMessage(), e);
            throw e;
        }
        return Optional.empty();
    }

    @Override
    public Optional<Vehicle> findByPlate(String plate) throws SQLException {
        String sql = "SELECT id, plate, manufacturer, model, year, company_id, created_at, updated_at FROM vehicles WHERE plate = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, plate);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToVehicle(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar veículo por placa: " + e.getMessage(), e);
            throw e;
        }
        return Optional.empty();
    }

    @Override
    public List<Vehicle> findAll() throws SQLException {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT id, plate, manufacturer, model, year, company_id, created_at, updated_at FROM vehicles";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                vehicles.add(mapResultSetToVehicle(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar todos os veículos: " + e.getMessage(), e);
            throw e;
        }
        return vehicles;
    }

    @Override
    public boolean update(Vehicle vehicle) throws SQLException {
        String sql = "UPDATE vehicles SET plate = ?, manufacturer = ?, model = ?, year = ?, company_id = ?, updated_at = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, vehicle.getPlate());
            stmt.setString(2, vehicle.getManufacturer());
            stmt.setString(3, vehicle.getModel());
            stmt.setInt(4, vehicle.getYear());
            stmt.setInt(5, vehicle.getCompanyId());
            stmt.setObject(6, LocalDateTime.now());
            stmt.setInt(7, vehicle.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao atualizar veículo: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM vehicles WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao deletar veículo: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<Vehicle> findByCompanyId(int companyId) throws SQLException {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT id, plate, manufacturer, model, year, company_id, created_at, updated_at FROM vehicles WHERE company_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, companyId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    vehicles.add(mapResultSetToVehicle(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar veículos por Company ID: " + e.getMessage(), e);
            throw e;
        }
        return vehicles;
    }

    @Override
    public List<Vehicle> findByModel(String model) throws SQLException {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT id, plate, manufacturer, model, year, company_id, created_at, updated_at FROM vehicles WHERE model = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, model);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    vehicles.add(mapResultSetToVehicle(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar veículos por modelo: " + e.getMessage(), e);
            throw e;
        }
        return vehicles;
    }

    @Override
    public List<Vehicle> findByYear(int year) throws SQLException {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT id, plate, manufacturer, model, year, company_id, created_at, updated_at FROM vehicles WHERE year = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, year);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    vehicles.add(mapResultSetToVehicle(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar veículos por ano: " + e.getMessage(), e);
            throw e;
        }
        return vehicles;
    }

    private Vehicle mapResultSetToVehicle(ResultSet rs) throws SQLException {
        return new Vehicle(
                rs.getInt("id"),
                rs.getString("plate"),
                rs.getString("manufacturer"),
                rs.getString("model"),
                rs.getInt("year"),
                rs.getInt("company_id"),
                rs.getObject("created_at", LocalDateTime.class),
                rs.getObject("updated_at", LocalDateTime.class)
        );
    }
}