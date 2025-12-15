package com.compliancesys.dao;

import com.compliancesys.config.DatabaseConfig;
import com.compliancesys.dao.impl.CompanyDAOImpl;
import com.compliancesys.dao.impl.VehicleDAOImpl;
import com.compliancesys.model.Company;
import com.compliancesys.model.Vehicle;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class VehicleDAOTest {
    private Connection connection;
    private VehicleDAO vehicleDAO;
    private CompanyDAO companyDAO; // Para criar empresas associadas aos veículos
    private int companyId;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DatabaseConfig.getInstance().getConnection();
        connection.setAutoCommit(false); // Inicia transação para rollback
        vehicleDAO = new VehicleDAOImpl(connection);
        companyDAO = new CompanyDAOImpl(connection);

        loadSchema(connection); // Carrega o schema do banco de dados
        clearTables(connection); // Limpa as tabelas antes de cada teste

        // Cria uma empresa para associar aos veículos de teste
        Company company = new Company(0, "Empresa Teste Veiculo", "11223344000199", "Rua da Empresa, 500", "1199887722", "empresa.veiculo@test.com", LocalDateTime.now(), LocalDateTime.now());
        companyId = companyDAO.create(company);
        assertTrue(companyId > 0, "A empresa deve ser criada com sucesso.");
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.rollback(); // Desfaz todas as operações do teste
        connection.close();
    }

    private void loadSchema(Connection conn) throws SQLException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("schema.sql");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String schemaSql = reader.lines().collect(Collectors.joining("\n"));
            for (String command : schemaSql.split(";")) {
                if (!command.trim().isEmpty()) {
                    try (Statement stmt = conn.createStatement()) {
                        stmt.execute(command);
                    }
                }
            }
        } catch (Exception e) {
            throw new SQLException("Erro ao carregar o schema.sql para o banco de dados de teste.", e);
        }
    }

    private void clearTables(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            // Limpa as tabelas na ordem correta devido a chaves estrangeiras
            stmt.execute("DELETE FROM compliance_audits");
            stmt.execute("DELETE FROM time_records");
            stmt.execute("DELETE FROM mobile_communications");
            stmt.execute("DELETE FROM journeys");
            stmt.execute("DELETE FROM drivers");
            stmt.execute("DELETE FROM vehicles");
            stmt.execute("DELETE FROM companies");
        }
    }

    @Test
    void testCreateVehicle() throws SQLException {
        Vehicle vehicle = new Vehicle(0, companyId, "ABC1234", "Caminhão Teste", 2020, "Marca X", LocalDateTime.now(), LocalDateTime.now());
        int id = vehicleDAO.create(vehicle);
        assertTrue(id > 0);
        Optional<Vehicle> foundVehicle = vehicleDAO.findById(id);
        assertTrue(foundVehicle.isPresent());
        assertEquals(vehicle.getPlate(), foundVehicle.get().getPlate());
        assertEquals(companyId, foundVehicle.get().getCompanyId());
    }

    @Test
    void testFindById() throws SQLException {
        Vehicle vehicle = new Vehicle(0, companyId, "DEF5678", "Carro Teste", 2021, "Marca Y", LocalDateTime.now(), LocalDateTime.now());
        int id = vehicleDAO.create(vehicle);
        Optional<Vehicle> foundVehicle = vehicleDAO.findById(id);
        assertTrue(foundVehicle.isPresent());
        assertEquals(id, foundVehicle.get().getId());
    }

    @Test
    void testFindAll() throws SQLException {
        vehicleDAO.create(new Vehicle(0, companyId, "GHI9012", "Van Teste", 2019, "Marca Z", LocalDateTime.now(), LocalDateTime.now()));
        vehicleDAO.create(new Vehicle(0, companyId, "JKL3456", "Ônibus Teste", 2022, "Marca W", LocalDateTime.now(), LocalDateTime.now()));
        List<Vehicle> vehicles = vehicleDAO.findAll();
        assertFalse(vehicles.isEmpty());
        assertEquals(2, vehicles.size());
    }

    @Test
    void testUpdateVehicle() throws SQLException {
        Vehicle vehicle = new Vehicle(0, companyId, "MNO7890", "Moto Antiga", 2018, "Marca K", LocalDateTime.now(), LocalDateTime.now());
        int id = vehicleDAO.create(vehicle);
        vehicle.setId(id);
        vehicle.setModel("Moto Nova");
        vehicle.setYear(2023);
        vehicle.setUpdatedAt(LocalDateTime.now());

        boolean updated = vehicleDAO.update(vehicle);
        assertTrue(updated);

        Optional<Vehicle> foundVehicle = vehicleDAO.findById(id);
        assertTrue(foundVehicle.isPresent());
        assertEquals("Moto Nova", foundVehicle.get().getModel());
        assertEquals(2023, foundVehicle.get().getYear());
        assertTrue(foundVehicle.get().getUpdatedAt().isAfter(vehicle.getCreatedAt()));
    }

    @Test
    void testDeleteVehicle() throws SQLException {
        Vehicle vehicle = new Vehicle(0, companyId, "PQR1234", "Veículo para Deletar", 2017, "Marca L", LocalDateTime.now(), LocalDateTime.now());
        int id = vehicleDAO.create(vehicle);
        boolean deleted = vehicleDAO.delete(id);
        assertTrue(deleted);
        Optional<Vehicle> foundVehicle = vehicleDAO.findById(id);
        assertFalse(foundVehicle.isPresent());
    }

    @Test
    void testFindByCompanyId() throws SQLException {
        // Cria uma segunda empresa para garantir que a filtragem por companyId funciona
        Company anotherCompany = new Company(0, "Outra Empresa", "99887766000111", "Outro Endereço", "11911112222", "outra.empresa@test.com", LocalDateTime.now(), LocalDateTime.now());
        int anotherCompanyId = companyDAO.create(anotherCompany);

        vehicleDAO.create(new Vehicle(0, companyId, "STU5678", "Caminhonete", 2020, "Marca M", LocalDateTime.now(), LocalDateTime.now()));
        vehicleDAO.create(new Vehicle(0, companyId, "VWX9012", "Furgão", 2021, "Marca N", LocalDateTime.now(), LocalDateTime.now()));
        vehicleDAO.create(new Vehicle(0, anotherCompanyId, "YZA3456", "Carro Pequeno", 2022, "Marca O", LocalDateTime.now(), LocalDateTime.now())); // Este não deve ser encontrado

        List<Vehicle> vehicles = vehicleDAO.findByCompanyId(companyId);
        assertFalse(vehicles.isEmpty());
        assertEquals(2, vehicles.size());
        assertTrue(vehicles.stream().allMatch(v -> v.getCompanyId() == companyId));
    }

    @Test
    void testFindByPlate() throws SQLException {
        String plate = "ABC1234";
        vehicleDAO.create(new Vehicle(0, companyId, plate, "Caminhão Teste", 2020, "Marca X", LocalDateTime.now(), LocalDateTime.now()));
        Optional<Vehicle> foundVehicle = vehicleDAO.findByPlate(plate);
        assertTrue(foundVehicle.isPresent());
        assertEquals(plate, foundVehicle.get().getPlate());
    }

    @Test
    void testFindByModel() throws SQLException {
        String model = "Caminhão Teste";
        vehicleDAO.create(new Vehicle(0, companyId, "ABC1234", model, 2020, "Marca X", LocalDateTime.now(), LocalDateTime.now()));
        List<Vehicle> foundVehicles = vehicleDAO.findByModel(model);
        assertFalse(foundVehicles.isEmpty());
        assertEquals(1, foundVehicles.size());
        assertEquals(model, foundVehicles.get(0).getModel());
    }

    @Test
    void testFindByYear() throws SQLException {
        int year = 2020;
        vehicleDAO.create(new Vehicle(0, companyId, "ABC1234", "Caminhão Teste", year, "Marca X", LocalDateTime.now(), LocalDateTime.now()));
        List<Vehicle> foundVehicles = vehicleDAO.findByYear(year);
        assertFalse(foundVehicles.isEmpty());
        assertEquals(1, foundVehicles.size());
        assertEquals(year, foundVehicles.get(0).getYear());
    }
}
