package com.compliancesys.dao.impl;

<<<<<<< Updated upstream
=======
import com.compliancesys.dao.VehicleDAO;
import com.compliancesys.model.Vehicle;
import javax.sql.DataSource; // Importa DataSource
>>>>>>> Stashed changes
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
<<<<<<< Updated upstream
import java.util.logging.Logger;

import com.compliancesys.config.DatabaseConfig;
import com.compliancesys.dao.VehicleDAO;
import com.compliancesys.model.Vehicle;

=======
import java.util.logging.Level; // Adicionado para Logger
import java.util.logging.Logger; // Adicionado para Logger

/**
 * Implementação do Data Access Object (DAO) para a entidade Vehicle.
 * Gerencia a persistência de dados de veículos no banco de dados.
 */
>>>>>>> Stashed changes
public class VehicleDAOImpl implements VehicleDAO {

    private static final Logger LOGGER = Logger.getLogger(VehicleDAOImpl.class.getName()); // Adicionado Logger
    private final DataSource dataSource; // Injeção do DataSource

    /**
     * Construtor que recebe o DataSource para gerenciamento de conexões.
     * @param dataSource O DataSource configurado (ex: HikariCP).
     */
    public VehicleDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
<<<<<<< Updated upstream
    public int create(Vehicle vehicle) throws SQLException { // CORRIGIDO: Retorna int
        String sql = "INSERT INTO vehicles (company_id, plate, manufacturer, model, year, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getInstance().getConnection();

=======
    public int create(Vehicle vehicle) throws SQLException {
        String sql = "INSERT INTO vehicles (plate, manufacturer, model, year, company_id, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection(); // Usa o DataSource
>>>>>>> Stashed changes
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, vehicle.getPlate());
            stmt.setString(2, vehicle.getManufacturer());
            stmt.setString(3, vehicle.getModel());
            stmt.setInt(4, vehicle.getYear());
            stmt.setInt(5, vehicle.getCompanyId());
            stmt.setObject(6, LocalDateTime.now()); // Define created_at
            stmt.setObject(7, LocalDateTime.now()); // Define updated_at

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                LOGGER.log(Level.SEVERE, "Falha ao criar veículo, nenhuma linha afetada.");
                throw new SQLException("Falha ao criar veículo, nenhuma linha afetada.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    LOGGER.log(Level.SEVERE, "Falha ao criar veículo, nenhum ID obtido.");
                    throw new SQLException("Falha ao criar veículo, nenhum ID obtido.");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao criar veículo: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Optional<Vehicle> findById(int id) throws SQLException {
<<<<<<< Updated upstream
        String sql = "SELECT id, company_id, plate, manufacturer, model, year, created_at, updated_at FROM vehicles WHERE id = ?";
        try (Connection conn = DatabaseConfig.getInstance().getConnection();

=======
        String sql = "SELECT id, plate, manufacturer, model, year, company_id, created_at, updated_at FROM vehicles WHERE id = ?";
        try (Connection conn = dataSource.getConnection(); // Usa o DataSource
>>>>>>> Stashed changes
             PreparedStatement stmt = conn.prepareStatement(sql)) {

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
<<<<<<< Updated upstream
        String sql = "SELECT id, company_id, plate, manufacturer, model, year, created_at, updated_at FROM vehicles WHERE plate = ?";
        try (Connection conn = DatabaseConfig.getInstance().getConnection();

=======
        String sql = "SELECT id, plate, manufacturer, model, year, company_id, created_at, updated_at FROM vehicles WHERE plate = ?";
        try (Connection conn = dataSource.getConnection(); // Usa o DataSource
>>>>>>> Stashed changes
             PreparedStatement stmt = conn.prepareStatement(sql)) {

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
<<<<<<< Updated upstream
        String sql = "SELECT id, company_id, plate, manufacturer, model, year, created_at, updated_at FROM vehicles";
       try (Connection conn = DatabaseConfig.getInstance().getConnection();

=======
        String sql = "SELECT id, plate, manufacturer, model, year, company_id, created_at, updated_at FROM vehicles";
        try (Connection conn = dataSource.getConnection(); // Usa o DataSource
>>>>>>> Stashed changes
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
    public boolean update(Vehicle vehicle) throws SQLException {
<<<<<<< Updated upstream
        String sql = "UPDATE vehicles SET company_id = ?, plate = ?, manufacturer = ?, model = ?, year = ?, updated_at = ? WHERE id = ?";
      try (Connection conn = DatabaseConfig.getInstance().getConnection();

=======
        String sql = "UPDATE vehicles SET plate = ?, manufacturer = ?, model = ?, year = ?, company_id = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection(); // Usa o DataSource
>>>>>>> Stashed changes
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, vehicle.getPlate());
            stmt.setString(2, vehicle.getManufacturer());
            stmt.setString(3, vehicle.getModel());
            stmt.setInt(4, vehicle.getYear());
            stmt.setInt(5, vehicle.getCompanyId());
            stmt.setObject(6, LocalDateTime.now()); // Atualiza updated_at
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
<<<<<<< Updated upstream
        try (Connection conn = DatabaseConfig.getInstance().getConnection();

=======
        try (Connection conn = dataSource.getConnection(); // Usa o DataSource
>>>>>>> Stashed changes
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao deletar veículo: " + e.getMessage(), e);
            throw e;
        }
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
