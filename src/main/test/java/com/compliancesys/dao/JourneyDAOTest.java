package com.compliancesys.dao;

import com.compliancesys.dao.impl.DriverDAOImpl; // Para criar um driver de teste
import com.compliancesys.dao.impl.JourneyDAOImpl;
import com.compliancesys.model.Driver;
import com.compliancesys.model.Journey;
import com.compliancesys.util.DatabaseConnection;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de teste para JourneyDAO.
 * Utiliza um banco de dados em memória H2 para isolar os testes.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class JourneyDAOTest {

    private static JourneyDAO journeyDAO;
    private static DriverDAO driverDAO; // Para gerenciar motoristas de teste
    private static Connection connection;

    // ID para o motorista de teste
    private static int testDriverId;

    @BeforeAll
    static void setUpBeforeAll() throws SQLException {
        connection = DatabaseConnection.getTestConnection();
        journeyDAO = new JourneyDAOImpl(connection);
        driverDAO = new DriverDAOImpl(connection);

        // Cria as tabelas necessárias no banco de dados H2
        try (PreparedStatement stmt = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS DRIVER (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "name VARCHAR(255) NOT NULL," +
                        "cpf VARCHAR(11) NOT NULL UNIQUE," +
                        "license_number VARCHAR(20) NOT NULL," +
                        "license_category VARCHAR(5) NOT NULL," +
                        "license_expiration_date DATE NOT NULL," +
                        "phone VARCHAR(20)," +
                        "email VARCHAR(255)," +
                        "created_at TIMESTAMP NOT NULL," +
                        "updated_at TIMESTAMP NOT NULL" +
                        ");"
        )) {
            stmt.execute();
        }
        try (PreparedStatement stmt = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS JOURNEY (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "driver_id INT NOT NULL," +
                        "journey_date DATE NOT NULL," +
                        "start_location VARCHAR(255) NOT NULL," +
                        "end_location VARCHAR(255) NOT NULL," +
                        "distance_km DOUBLE," +
                        "duration_hours DOUBLE," +
                        "created_at TIMESTAMP NOT NULL," +
                        "updated_at TIMESTAMP NOT NULL," +
                        "FOREIGN KEY (driver_id) REFERENCES DRIVER(id)" +
                        ");"
        )) {
            stmt.execute();
        }

        // Cria um motorista para ser usado nos testes de jornada
        Driver driver = new Driver(0, "Motorista Jornada", "11122233344", "12345678901", "B", LocalDate.now().plusYears(5), "999999999", "driver.journey@test.com", LocalDateTime.now(), LocalDateTime.now());
        testDriverId = driverDAO.create(driver);
    }

    @AfterAll
    static void tearDownAfterAll() throws SQLException {
        // Limpa as tabelas e fecha a conexão após todos os testes
        try (PreparedStatement stmt = connection.prepareStatement("DROP TABLE IF EXISTS JOURNEY;")) {
            stmt.execute();
        }
        try (PreparedStatement stmt = connection.prepareStatement("DROP TABLE IF EXISTS DRIVER;")) {
            stmt.execute();
        }
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @BeforeEach
    void setUp() throws SQLException {
        // Limpa a tabela de jornadas antes de cada teste para garantir isolamento
        try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM JOURNEY; ALTER TABLE JOURNEY ALTER COLUMN id RESTART WITH 1;")) {
            stmt.execute();
        }
    }

    @Test
    @Order(1)
    @DisplayName("1. Deve criar uma nova jornada com sucesso")
    void testCreateJourneySuccess() throws SQLException {
        Journey journey = new Journey(0, testDriverId, LocalDate.now(), "Rua A, 100", "Av. B, 200", 50.5, 1.5, LocalDateTime.now(), LocalDateTime.now());
        int id = journeyDAO.create(journey);

        assertTrue(id > 0, "O ID da jornada deve ser maior que 0 após a criação.");
        journey.setId(id); // Define o ID para futuras verificações

        Optional<Journey> foundJourney = journeyDAO.findById(id);
        assertTrue(foundJourney.isPresent(), "A jornada criada deve ser encontrada pelo ID.");
        assertEquals(journey.getDriverId(), foundJourney.get().getDriverId());
        assertEquals(journey.getJourneyDate(), foundJourney.get().getJourneyDate());
    }

    @Test
    @Order(2)
    @DisplayName("2. Não deve criar jornada com driver_id e journey_date duplicados")
    void testCreateJourneyDuplicateDriverAndDate() throws SQLException {
        LocalDate today = LocalDate.now();
        Journey journey1 = new Journey(0, testDriverId, today, "Origem 1", "Destino 1", 10.0, 0.5, LocalDateTime.now(), LocalDateTime.now());
        journeyDAO.create(journey1);

        Journey journey2 = new Journey(0, testDriverId, today, "Origem 2", "Destino 2", 20.0, 1.0, LocalDateTime.now(), LocalDateTime.now());

        // Espera que uma SQLException seja lançada devido à restrição UNIQUE (se houver, ou lógica de negócio)
        // No H2, para simular, precisamos de uma UNIQUE constraint composta ou tratar na camada de serviço.
        // Assumindo que a DAO permite, mas a camada de serviço impediria. Para o teste DAO, se não houver UNIQUE no DB, ele criaria.
        // Vamos adicionar uma UNIQUE constraint composta para este teste no H2.
        try (PreparedStatement stmt = connection.prepareStatement(
                "ALTER TABLE JOURNEY ADD CONSTRAINT UQ_DRIVER_DATE UNIQUE (driver_id, journey_date);"
        )) {
            stmt.execute();
        } catch (SQLException e) {
            // Se a constraint já existe, ignora
            if (!e.getMessage().contains("already exists")) {
                throw e;
            }
        }

        SQLException thrown = assertThrows(SQLException.class, () -> journeyDAO.create(journey2),
                "Deve lançar SQLException ao tentar criar jornada com driver_id e journey_date duplicados.");
        assertTrue(thrown.getMessage().contains("UNIQUE constraint"), "A mensagem de erro deve indicar violação de constraint única.");
    }

    @Test
    @Order(3)
    @DisplayName("3. Deve encontrar jornada pelo ID")
    void testFindById() throws SQLException {
        Journey journey = new Journey(0, testDriverId, LocalDate.now().plusDays(1), "Ponto A", "Ponto B", 75.0, 2.0, LocalDateTime.now(), LocalDateTime.now());
        int id = journeyDAO.create(journey);

        Optional<Journey> foundJourney = journeyDAO.findById(id);
        assertTrue(foundJourney.isPresent(), "Jornada deve ser encontrada pelo ID.");
        assertEquals(id, foundJourney.get().getId());
        assertEquals(journey.getStartLocation(), foundJourney.get().getStartLocation());
    }

    @Test
    @Order(4)
    @DisplayName("4. Deve retornar Optional vazio para ID não existente")
    void testFindByIdNotFound() throws SQLException {
        Optional<Journey> foundJourney = journeyDAO.findById(9999); // ID que não existe
        assertFalse(foundJourney.isPresent(), "Não deve encontrar jornada para ID não existente.");
    }

    @Test
    @Order(5)
    @DisplayName("5. Deve encontrar jornada por ID de motorista e data")
    void testFindByDriverIdAndDate() throws SQLException {
        LocalDate specificDate = LocalDate.now().plusDays(2);
        Journey journey = new Journey(0, testDriverId, specificDate, "Local X", "Local Y", 120.0, 3.0, LocalDateTime.now(), LocalDateTime.now());
        journeyDAO.create(journey);

        Optional<Journey> foundJourney = journeyDAO.findByDriverIdAndDate(testDriverId, specificDate);
        assertTrue(foundJourney.isPresent(), "Jornada deve ser encontrada pelo ID do motorista e data.");
        assertEquals(journey.getStartLocation(), foundJourney.get().getStartLocation());
        assertEquals(journey.getJourneyDate(), foundJourney.get().getJourneyDate());
    }

    @Test
    @Order(6)
    @DisplayName("6. Deve retornar Optional vazio para ID de motorista e data não existentes")
    void testFindByDriverIdAndDateNotFound() throws SQLException {
        Optional<Journey> foundJourney = journeyDAO.findByDriverIdAndDate(testDriverId, LocalDate.now().plusDays(10)); // Data que não existe
        assertFalse(foundJourney.isPresent(), "Não deve encontrar jornada para ID de motorista e data não existentes.");
    }

    @Test
    @Order(7)
    @DisplayName("7. Deve retornar todas as jornadas")
    void testFindAll() throws SQLException {
        journeyDAO.create(new Journey(0, testDriverId, LocalDate.now().minusDays(1), "O1", "D1", 10.0, 0.5, LocalDateTime.now(), LocalDateTime.now()));
        journeyDAO.create(new Journey(0, testDriverId, LocalDate.now().minusDays(2), "O2", "D2", 20.0, 1.0, LocalDateTime.now(), LocalDateTime.now()));

        List<Journey> journeys = journeyDAO.findAll();
        assertNotNull(journeys, "A lista de jornadas não deve ser nula.");
        assertEquals(2, journeys.size(), "Deve retornar duas jornadas.");
    }

    @Test
    @Order(8)
    @DisplayName("8. Deve retornar jornadas por ID de motorista")
    void testFindByDriverId() throws SQLException {
        // Cria um segundo motorista para testar
        Driver anotherDriver = new Driver(0, "Outro Motorista", "55566677788", "DEF45678901", "C", LocalDate.now().plusYears(2), "888888888", "another.driver@test.com", LocalDateTime.now(), LocalDateTime.now());
        int anotherDriverId = driverDAO.create(anotherDriver);

        journeyDAO.create(new Journey(0, testDriverId, LocalDate.now().minusDays(3), "O3", "D3", 30.0, 1.5, LocalDateTime.now(), LocalDateTime.now()));
        journeyDAO.create(new Journey(0, testDriverId, LocalDate.now().minusDays(4), "O4", "D4", 40.0, 2.0, LocalDateTime.now(), LocalDateTime.now()));
        journeyDAO.create(new Journey(0, anotherDriverId, LocalDate.now().minusDays(5), "O5", "D5", 50.0, 2.5, LocalDateTime.now(), LocalDateTime.now()));

        List<Journey> journeysForTestDriver = journeyDAO.findByDriverId(testDriverId);
        assertNotNull(journeysForTestDriver);
        assertEquals(2, journeysForTestDriver.size());
        assertTrue(journeysForTestDriver.stream().allMatch(j -> j.getDriverId() == testDriverId));

        List<Journey> journeysForAnotherDriver = journeyDAO.findByDriverId(anotherDriverId);
        assertNotNull(journeysForAnotherDriver);
        assertEquals(1, journeysForAnotherDriver.size());
        assertTrue(journeysForAnotherDriver.stream().allMatch(j -> j.getDriverId() == anotherDriverId));
    }

    @Test
    @Order(9)
    @DisplayName("9. Deve atualizar uma jornada existente com sucesso")
    void testUpdateJourneySuccess() throws SQLException {
        Journey journey = new Journey(0, testDriverId, LocalDate.now().plusDays(3), "Origem Antiga", "Destino Antigo", 60.0, 1.8, LocalDateTime.now(), LocalDateTime.now());
        int id = journeyDAO.create(journey);
        journey.setId(id);

        journey.setStartLocation("Nova Origem");
        journey.setEndLocation("Novo Destino");
        journey.setDistanceKm(65.0);
        journey.setUpdatedAt(LocalDateTime.now()); // Simula a atualização da data

        boolean updated = journeyDAO.update(journey);
        assertTrue(updated, "A jornada deve ser atualizada com sucesso.");

        Optional<Journey> foundJourney = journeyDAO.findById(id);
        assertTrue(foundJourney.isPresent());
        assertEquals("Nova Origem", foundJourney.get().getStartLocation());
        assertEquals("Novo Destino", foundJourney.get().getEndLocation());
        assertEquals(65.0, foundJourney.get().getDistanceKm());
        // Verifica se a data de atualização foi realmente alterada (pode haver pequena diferença de milissegundos)
        assertTrue(foundJourney.get().getUpdatedAt().isAfter(journey.getCreatedAt()));
    }

    @Test
    @Order(10)
    @DisplayName("10. Não deve atualizar jornada com ID não existente")
    void testUpdateJourneyNotFound() throws SQLException {
        Journey nonExistentJourney = new Journey(9999, testDriverId, LocalDate.now().plusDays(4), "Origem Falsa", "Destino Falso", 100.0, 2.0, LocalDateTime.now(), LocalDateTime.now());
        boolean updated = journeyDAO.update(nonExistentJourney);
        assertFalse(updated, "Não deve atualizar uma jornada com ID não existente.");
    }

    @Test
    @Order(11)
    @DisplayName("11. Não deve atualizar jornada para um driver_id e journey_date já existentes em outra jornada")
    void testUpdateJourneyDuplicateDriverAndDateConflict() throws SQLException {
        LocalDate conflictDate = LocalDate.now().plusDays(5);
        Journey journey1 = new Journey(0, testDriverId, conflictDate, "Jornada Um", "Destino Um", 10.0, 0.5, LocalDateTime.now(), LocalDateTime.now());
        int id1 = journeyDAO.create(journey1);
        journey1.setId(id1);

        // Cria uma segunda jornada para o mesmo motorista, mas em outra data
        Journey journey2 = new Journey(0, testDriverId, LocalDate.now().plusDays(6), "Jornada Dois", "Destino Dois", 20.0, 1.0, LocalDateTime.now(), LocalDateTime.now());
        int id2 = journeyDAO.create(journey2);
        journey2.setId(id2);

        // Tenta atualizar journey2 para ter a mesma data de journey1
        journey2.setJourneyDate(conflictDate);

        SQLException thrown = assertThrows(SQLException.class, () -> journeyDAO.update(journey2),
                "Deve lançar SQLException ao tentar atualizar jornada com driver_id e journey_date duplicados de outra.");
        assertTrue(thrown.getMessage().contains("UNIQUE constraint"), "A mensagem de erro deve indicar violação de constraint única.");
    }

    @Test
    @Order(12)
    @DisplayName("12. Deve deletar uma jornada existente com sucesso")
    void testDeleteJourneySuccess() throws SQLException {
        Journey journey = new Journey(0, testDriverId, LocalDate.now().plusDays(7), "Origem para Deletar", "Destino para Deletar", 80.0, 2.2, LocalDateTime.now(), LocalDateTime.now());
        int id = journeyDAO.create(journey);

        boolean deleted = journeyDAO.delete(id);
        assertTrue(deleted, "A jornada deve ser deletada com sucesso.");

        Optional<Journey> foundJourney = journeyDAO.findById(id);
        assertFalse(foundJourney.isPresent(), "A jornada deletada não deve ser encontrada.");
    }

    @Test
    @Order(13)
    @DisplayName("13. Não deve deletar jornada com ID não existente")
    void testDeleteJourneyNotFound() throws SQLException {
        boolean deleted = journeyDAO.delete(9999); // ID que não existe
        assertFalse(deleted, "Não deve deletar uma jornada com ID não existente.");
    }
}
