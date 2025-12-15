package com.compliancesys.dao;

import com.compliancesys.config.DatabaseConfig;
import com.compliancesys.dao.impl.CompanyDAOImpl;
import com.compliancesys.dao.impl.DriverDAOImpl;
import com.compliancesys.dao.impl.JourneyDAOImpl;
import com.compliancesys.dao.impl.VehicleDAOImpl;
import com.compliancesys.model.Company;
import com.compliancesys.model.Driver;
import com.compliancesys.model.Journey;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class JourneyDAOTest {
    private Connection connection;
    private JourneyDAO journeyDAO;
    private CompanyDAO companyDAO;
    private DriverDAO driverDAO;
    private VehicleDAO vehicleDAO;

    private int companyId;
    private int driverId;
    private int vehicleId;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DatabaseConfig.getInstance().getConnection();
        connection.setAutoCommit(false); // Inicia transação para rollback
        journeyDAO = new JourneyDAOImpl(connection);
        companyDAO = new CompanyDAOImpl(connection);
        driverDAO = new DriverDAOImpl(connection);
        vehicleDAO = new VehicleDAOImpl(connection);

        loadSchema(connection); // Carrega o schema do banco de dados
        clearTables(connection); // Limpa as tabelas antes de cada teste

        // Cria uma empresa, motorista e veículo para associar às jornadas de teste
        Company company = new Company(0, "Empresa Journey", "11223344000166", "Rua da Empresa, 200", "1199887755", "empresa.journey@test.com", LocalDateTime.now(), LocalDateTime.now());
        companyId = companyDAO.create(company);
        assertTrue(companyId > 0, "A empresa deve ser criada com sucesso.");

        Driver driver = new Driver(0, companyId, "Motorista Journey", "11122233344", "12345678901", "B",
                LocalDate.of(2028, 1, 1), LocalDate.of(1980, 1, 1), "motorista.journey@test.com", "11987654321",
                "Endereço Motorista", LocalDateTime.now(), LocalDateTime.now());
        driverId = driverDAO.create(driver);
        assertTrue(driverId > 0, "O motorista deve ser criado com sucesso.");

        Vehicle vehicle = new Vehicle(0, companyId, "ABC1234", "Caminhão Teste", 2020, LocalDateTime.now(), LocalDateTime.now());
        vehicleId = vehicleDAO.create(vehicle);
        assertTrue(vehicleId > 0, "O veículo deve ser criado com sucesso.");
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.rollback(); // Desfaz todas as operações do teste
        connection.close();
    }

    /**
     * Carrega e executa o script SQL do schema para o banco de dados de teste.
     * Assume que 'schema.sql' está na pasta 'src/main/resources'.
     */
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

    /**
     * Limpa as tabelas na ordem correta devido a chaves estrangeiras.
     */
    private void clearTables(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM journeys");
            stmt.execute("DELETE FROM drivers");
            stmt.execute("DELETE FROM vehicles");
            stmt.execute("DELETE FROM companies");
        }
    }

    @Test
    void testCreateJourney() throws SQLException {
        Journey journey = new Journey(0, driverId, vehicleId, companyId,
                "Origem Teste", "Destino Teste", LocalDateTime.now(), null,
                "Em Andamento", LocalDateTime.now(), LocalDateTime.now());
        int id = journeyDAO.create(journey);
        assertTrue(id > 0);
        Optional<Journey> foundJourney = journeyDAO.findById(id);
        assertTrue(foundJourney.isPresent());
        assertEquals(journey.getOrigin(), foundJourney.get().getOrigin());
        assertEquals(journey.getDriverId(), foundJourney.get().getDriverId());
    }

    @Test
    void testFindById() throws SQLException {
        Journey journey = new Journey(0, driverId, vehicleId, companyId,
                "Origem Busca", "Destino Busca", LocalDateTime.now(), null,
                "Iniciada", LocalDateTime.now(), LocalDateTime.now());
        int id = journeyDAO.create(journey);
        Optional<Journey> foundJourney = journeyDAO.findById(id);
        assertTrue(foundJourney.isPresent());
        assertEquals(id, foundJourney.get().getId());
    }

    @Test
    void testFindAll() throws SQLException {
        journeyDAO.create(new Journey(0, driverId, vehicleId, companyId, "O1", "D1", LocalDateTime.now(), null, "S1", LocalDateTime.now(), LocalDateTime.now()));
        journeyDAO.create(new Journey(0, driverId, vehicleId, companyId, "O2", "D2", LocalDateTime.now(), null, "S2", LocalDateTime.now(), LocalDateTime.now()));
        List<Journey> journeys = journeyDAO.findAll();
        assertFalse(journeys.isEmpty());
        assertEquals(2, journeys.size());
    }

    @Test
    void testUpdateJourney() throws SQLException {
        Journey journey = new Journey(0, driverId, vehicleId, companyId,
                "Origem Antiga", "Destino Antigo", LocalDateTime.now().minusHours(2), null,
                "Em Andamento", LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(2));
        int id = journeyDAO.create(journey);

        journey.setId(id);
        journey.setDestination("Destino Novo");
        journey.setEndTime(LocalDateTime.now());
        journey.setStatus("Concluída");
        journey.setUpdatedAt(LocalDateTime.now());

        boolean updated = journeyDAO.update(journey);
        assertTrue(updated);

        Optional<Journey> foundJourney = journeyDAO.findById(id);
        assertTrue(foundJourney.isPresent());
        assertEquals("Destino Novo", foundJourney.get().getDestination());
        assertEquals("Concluída", foundJourney.get().getStatus());
        assertNotNull(foundJourney.get().getEndTime());
        assertTrue(foundJourney.get().getUpdatedAt().isAfter(journey.getCreatedAt()));
    }

    @Test
    void testDeleteJourney() throws SQLException {
        Journey journey = new Journey(0, driverId, vehicleId, companyId,
                "Origem Deletar", "Destino Deletar", LocalDateTime.now(), null,
                "Pendente", LocalDateTime.now(), LocalDateTime.now());
        int id = journeyDAO.create(journey);

        boolean deleted = journeyDAO.delete(id);
        assertTrue(deleted);

        Optional<Journey> foundJourney = journeyDAO.findById(id);
        assertFalse(foundJourney.isPresent());
    }

    @Test
    void testFindByDriverId() throws SQLException {
        journeyDAO.create(new Journey(0, driverId, vehicleId, companyId, "O1", "D1", LocalDateTime.now(), null, "S1", LocalDateTime.now(), LocalDateTime.now()));
        journeyDAO.create(new Journey(0, driverId, vehicleId, companyId, "O2", "D2", LocalDateTime.now(), null, "S2", LocalDateTime.now(), LocalDateTime.now()));

        // Criar outro motorista e jornada para garantir que a busca é específica
        Company anotherCompany = new Company(0, "Empresa Outro Driver", "55667788000199", "Rua Outra, 300", "11911112222", "outra.empresa@test.com", LocalDateTime.now(), LocalDateTime.now());
        int anotherCompanyId = companyDAO.create(anotherCompany);
        Driver anotherDriver = new Driver(0, anotherCompanyId, "Outro Motorista", "44455566677", "98765432109", "C",
                LocalDate.of(2029, 2, 2), LocalDate.of(1990, 2, 2), "outro.motorista@test.com", "11933334444",
                "Endereço Outro", LocalDateTime.now(), LocalDateTime.now());
        int anotherDriverId = driverDAO.create(anotherDriver);
        journeyDAO.create(new Journey(0, anotherDriverId, vehicleId, anotherCompanyId, "O3", "D3", LocalDateTime.now(), null, "S3", LocalDateTime.now(), LocalDateTime.now()));

        List<Journey> journeys = journeyDAO.findByDriverId(driverId);
        assertFalse(journeys.isEmpty());
        assertEquals(2, journeys.size());
        assertTrue(journeys.stream().allMatch(j -> j.getDriverId() == driverId));
    }

    @Test
    void testFindByVehicleId() throws SQLException {
        journeyDAO.create(new Journey(0, driverId, vehicleId, companyId, "O1", "D1", LocalDateTime.now(), null, "S1", LocalDateTime.now(), LocalDateTime.now()));
        journeyDAO.create(new Journey(0, driverId, vehicleId, companyId, "O2", "D2", LocalDateTime.now(), null, "S2", LocalDateTime.now(), LocalDateTime.now()));

        // Criar outro veículo e jornada para garantir que a busca é específica
        Vehicle anotherVehicle = new Vehicle(0, companyId, "XYZ5678", "Carro Teste", 2022, LocalDateTime.now(), LocalDateTime.now());
        int anotherVehicleId = vehicleDAO.create(anotherVehicle);
        journeyDAO.create(new Journey(0, driverId, anotherVehicleId, companyId, "O3", "D3", LocalDateTime.now(), null, "S3", LocalDateTime.now(), LocalDateTime.now()));

        List<Journey> journeys = journeyDAO.findByVehicleId(vehicleId);
        assertFalse(journeys.isEmpty());
        assertEquals(2, journeys.size());
        assertTrue(journeys.stream().allMatch(j -> j.getVehicleId() == vehicleId));
    }

    @Test
    void testFindByCompanyId() throws SQLException {
        journeyDAO.create(new Journey(0, driverId, vehicleId, companyId, "O1", "D1", LocalDateTime.now(), null, "S1", LocalDateTime.now(), LocalDateTime.now()));
        journeyDAO.create(new Journey(0, driverId, vehicleId, companyId, "O2", "D2", LocalDateTime.now(), null, "S2", LocalDateTime.now(), LocalDateTime.now()));

        // Criar outra empresa, motorista, veículo e jornada para garantir que a busca é específica
        Company anotherCompany = new Company(0, "Empresa Outra Journey", "55667788000199", "Rua Outra, 300", "11911112222", "outra.empresa@test.com", LocalDateTime.now(), LocalDateTime.now());
        int anotherCompanyId = companyDAO.create(anotherCompany);
        Driver anotherDriver = new Driver(0, anotherCompanyId, "Outro Motorista", "44455566677", "98765432109", "C",
                LocalDate.of(2029, 2, 2), LocalDate.of(1990, 2, 2), "outro.motorista@test.com", "11933334444",
                "Endereço Outro", LocalDateTime.now(), LocalDateTime.now());
        int anotherDriverId = driverDAO.create(anotherDriver);
        Vehicle anotherVehicle = new Vehicle(0, anotherCompanyId, "XYZ5678", "Carro Teste", 2022, LocalDateTime.now(), LocalDateTime.now());
        int anotherVehicleId = vehicleDAO.create(anotherVehicle);
        journeyDAO.create(new Journey(0, anotherDriverId, anotherVehicleId, anotherCompanyId, "O3", "D3", LocalDateTime.now(), null, "S3", LocalDateTime.now(), LocalDateTime.now()));

        List<Journey> journeys = journeyDAO.findByCompanyId(companyId);
        assertFalse(journeys.isEmpty());
        assertEquals(2, journeys.size());
        assertTrue(journeys.stream().allMatch(j -> j.getCompanyId() == companyId));
    }

    @Test
    void testFindByStatus() throws SQLException {
        String status = "Concluída";
        journeyDAO.create(new Journey(0, driverId, vehicleId, companyId, "O1", "D1", LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1), status, LocalDateTime.now(), LocalDateTime.now()));
        journeyDAO.create(new Journey(0, driverId, vehicleId, companyId, "O2", "D2", LocalDateTime.now(), null, "Em Andamento", LocalDateTime.now(), LocalDateTime.now()));

        List<Journey> journeys = journeyDAO.findByStatus(status);
        assertFalse(journeys.isEmpty());
        assertEquals(1, journeys.size());
        assertEquals(status, journeys.get(0).getStatus());
    }

    @Test
    void testFindByStartTimeBetween() throws SQLException {
        LocalDateTime startRange = LocalDateTime.now().minusDays(1);
        LocalDateTime endRange = LocalDateTime.now().plusDays(1);

        journeyDAO.create(new Journey(0, driverId, vehicleId, companyId, "O1", "D1", LocalDateTime.now().minusHours(1), null, "S1", LocalDateTime.now(), LocalDateTime.now()));
        journeyDAO.create(new Journey(0, driverId, vehicleId, companyId, "O2", "D2", LocalDateTime.now().minusDays(2), null, "S2", LocalDateTime.now(), LocalDateTime.now())); // Fora do range

        List<Journey> journeys = journeyDAO.findByStartTimeBetween(startRange, endRange);
        assertFalse(journeys.isEmpty());
        assertEquals(1, journeys.size());
        assertTrue(journeys.get(0).getStartTime().isAfter(startRange.minusMinutes(1)));
        assertTrue(journeys.get(0).getStartTime().isBefore(endRange.plusMinutes(1)));
    }
}
