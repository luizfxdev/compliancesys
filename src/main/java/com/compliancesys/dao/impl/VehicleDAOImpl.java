package com.compliancesys.dao.impl;

import com.compliancesys.config.DatabaseConfig;
import com.compliancesys.dao.VehicleDAO;
import com.compliancesys.model.Vehicle;

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
 * Implementação da interface VehicleDAO para operações de persistência da entidade Vehicle.
 * Interage com o banco de dados PostgreSQL.
 */
public class VehicleDAOImpl implements VehicleDAO {

    private static final Logger LOGGER = Logger.getLogger(VehicleDAOImpl.class.getName());
    private final DatabaseConfig dbConfig;

    public VehicleDAOImpl() {
        this.dbConfig = DatabaseConfig.getInstance();
    }

    @Override
    public int create(Vehicle vehicle) throws SQLException {
        String sql = "INSERT INTO vehicles (plate, model, year, company_id, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, vehicle.getPlate());
            stmt.setString(2, vehicle.getModel());
            stmt.setInt(3, vehicle.getYear());
            stmt.setInt(4, vehicle.getCompanyId()); // company_id
            stmt.setObject(5, vehicle.getCreatedAt());
            stmt.setObject(6, vehicle.getUpdatedAt());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
            return -1; // Indica falha na criação
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao criar veículo: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Optional<Vehicle> findById(int id) throws SQLException {
        String sql = "SELECT id, plate, model, year, company_id, created_at, updated_at FROM vehicles WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToVehicle(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar veículo por ID: " + e.getMessage(), e);
            throw e;
        }
        return Optional.empty();
    }

    @Override
    public Optional<Vehicle> findByPlate(String plate) throws SQLException {
        String sql = "SELECT id, plate, model, year, company_id, created_at, updated_at FROM vehicles WHERE plate = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, plate);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToVehicle(rs));
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
        String sql = "SELECT id, plate, model, year, company_id, created_at, updated_at FROM vehicles";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
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
    public List<Vehicle> findByCompanyId(int companyId) throws SQLException {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT id, plate, model, year, company_id, created_at, updated_at FROM vehicles WHERE company_id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, companyId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                vehicles.add(mapResultSetToVehicle(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar veículos por ID da empresa: " + e.getMessage(), e);
            throw e;
        }
        return vehicles;
    }

    @Override
    public boolean update(Vehicle vehicle) throws SQLException {
        String sql = "UPDATE vehicles SET plate = ?, model = ?, year = ?, company_id = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, vehicle.getPlate());
            stmt.setString(2, vehicle.getModel());
            stmt.setInt(3, vehicle.getYear());
            stmt.setInt(4, vehicle.getCompanyId());
            stmt.setObject(5, LocalDateTime.now()); // Atualiza o updated_at automaticamente
            stmt.setInt(6, vehicle.getId());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao atualizar veículo: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM vehicles WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao deletar veículo: " + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Mapeia um ResultSet para um objeto Vehicle.
     * @param rs ResultSet contendo os dados do veículo.
     * @return Objeto Vehicle.
     * @throws SQLException Se ocorrer um erro ao acessar os dados do ResultSet.
     */
    private Vehicle mapResultSetToVehicle(ResultSet rs) throws SQLException {
        Vehicle vehicle = new Vehicle();
        vehicle.setId(rs.getInt("id"));
        vehicle.setPlate(rs.getString("plate"));
        vehicle.setModel(rs.getString("model"));
        vehicle.setYear(rs.getInt("year"));
        vehicle.setCompanyId(rs.getInt("company_id"));

        // Converte OffsetDateTime (timestamptz) para LocalDateTime
        OffsetDateTime createdAtOffset = rs.getObject("created_at", OffsetDateTime.class);
        vehicle.setCreatedAt(createdAtOffset != null ? createdAtOffset.toLocalDateTime() : null);

        OffsetDateTime updatedAtOffset = rs.getObject("updated_at", OffsetDateTime.class);
        vehicle.setUpdatedAt(updatedAtOffset != null ? updatedAtOffset.toLocalDateTime() : null);

        return vehicle;
    }
}
