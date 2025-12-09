package com.compliancesys.dao;

import com.compliancesys.dao.impl.DriverDAOImpl; // Para criar um driver de teste
import com.compliancesys.dao.impl.JourneyDAOImpl; // Para criar uma jornada de teste
import com.compliancesys.dao.impl.MobileCommunicationDAOImpl;
import com.compliancesys.dao.impl.PointRecordDAOImpl; // Para criar um registro de ponto de teste
import com.compliancesys.model.Driver;
import com.compliancesys.model.Journey;
import com.compliancesys.model.MobileCommunication;
import com.compliancesys.model.PointRecord;
import com.compliancesys.model.PointType;
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
 * Classe de teste para MobileCommunicationDAO.
 * Utiliza um banco de dados em memória H2 para isolar os testes.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MobileCommunicationDAOTest {

    private static MobileCommunicationDAO mobileCommunicationDAO;
    private static PointRecordDAO pointRecordDAO; // Para gerenciar registros de ponto de teste
    private static JourneyDAO journeyDAO; // Para gerenciar jornadas de teste
    private static DriverDAO driverDAO; // Para gerenciar motoristas de teste
    private static Connection connection;

    // IDs para entidades de teste
    private static int testDriverId;
    private static int testJourneyId;
    private static int testPointRecordId;

    @BeforeAll
    static void setUpBeforeAll() throws SQLException {
        connection = DatabaseConnection.getTestConnection();
        mobileCommunicationDAO = new MobileCommunicationDAOImpl(connection);
        pointRecordDAO = new PointRecordDAOImpl(connection);
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
        try (PreparedStatement stmt = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS POINT_RECORD (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "journey_id INT NOT NULL," +
                        "timestamp TIMESTAMP NOT NULL," +
                        "point_type VARCHAR(50) NOT NULL," + // Enum PointType
                        "latitude DOUBLE NOT NULL," +
                        "longitude DOUBLE NOT NULL," +
                        "created_at TIMESTAMP NOT NULL," +
                        "updated_at TIMESTAMP NOT NULL," +
                        "FOREIGN KEY (journey_id) REFERENCES JOURNEY(id)" +
                        ");"
        )) {
            stmt.execute();
        }
        try (PreparedStatement stmt = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS MOBILE_COMMUNICATION (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "record_id INT NOT NULL," +
                        "timestamp TIMESTAMP NOT NULL," +
                        "latitude DOUBLE NOT NULL," +
                        "longitude DOUBLE NOT NULL," +
                        "signal_strength INT," +
                        "battery_level INT," +
                        "created_at TIMESTAMP NOT NULL," +
                        "updated_at TIMESTAMP NOT NULL," +
                        "FOREIGN KEY (record_id) REFERENCES POINT_RECORD(id)" +
                        ");"
        )) {
            stmt.execute();
        }

        // Cria um motorista, uma jornada e um registro de ponto para serem usados nos testes de comunicação móvel
        Driver driver = new Driver(0, "Motorista Mobile", "11122233344", "12345678901", "B", LocalDate.now().plusYears(5), "999999999", "driver.mobile@test.com", LocalDateTime.now(), LocalDateTime.now());
        testDriverId = driverDAO.create(driver);

        Journey journey = new Journey(0, testDriverId, LocalDate.now(), "Origem Mobile", "Destino Mobile", 100.0, 2.0, LocalDateTime.now(), LocalDateTime.now());
        testJourneyId = journeyDAO.create(journey);

        PointRecord pointRecord = new PointRecord(0, testJourneyId, LocalDateTime.now(), PointType.INICIO_JORNADA, -23.5505, -46.6333, LocalDateTime.now(), LocalDateTime.now());
        testPointRecordId = pointRecordDAO.create(pointRecord);
    }

    @AfterAll
    static void tearDownAfterAll() throws SQLException {
        // Limpa as tabelas e fecha a conexão após todos os testes
        try (PreparedStatement stmt = connection.prepareStatement("DROP TABLE IF EXISTS MOBILE_COMMUNICATION;")) {
            stmt.execute();
        }
        try (PreparedStatement stmt = connection.prepareStatement("DROP TABLE IF EXISTS POINT_RECORD;")) {
            stmt.execute();
        }
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
        // Limpa a tabela de comunicações móveis antes de cada teste para garantir isolamento
        try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM MOBILE_COMMUNICATION; ALTER TABLE MOBILE_COMMUNICATION ALTER COLUMN id RESTART WITH 1;")) {
            stmt.execute();
        }
    }

    @Test
    @Order(1)
    @DisplayName("1. Deve criar um novo registro de comunicação móvel com sucesso")
    void testCreateMobileCommunicationSuccess() throws SQLException {
        MobileCommunication communication = new MobileCommunication(0, testPointRecordId, LocalDateTime.now().plusMinutes(1), -23.5506, -46.6334, 80, 95, LocalDateTime.now(), LocalDateTime.now());
        int id = mobileCommunicationDAO.create(communication);

        assertTrue(id > 0, "O ID da comunicação móvel deve ser maior que 0 após a criação.");
        communication.setId(id);

        Optional<MobileCommunication> foundCommunication = mobileCommunicationDAO.findById(id);
        assertTrue(foundCommunication.isPresent(), "A comunicação móvel criada deve ser encontrada pelo ID.");
        assertEquals(communication.getRecordId(), foundCommunication.get().getRecordId());
        assertEquals(communication.getLatitude(), foundCommunication.get().getLatitude());
    }

    @Test
    @Order(2)
    @DisplayName("2. Deve encontrar registro de comunicação móvel pelo ID")
    void testFindById() throws SQLException {
        MobileCommunication communication = new MobileCommunication(0, testPointRecordId, LocalDateTime.now().plusMinutes(2), -23.5507, -46.6335, 75, 90, LocalDateTime.now(), LocalDateTime.now());
        int id = mobileCommunicationDAO.create(communication);

        Optional<MobileCommunication> foundCommunication = mobileCommunicationDAO.findById(id);
        assertTrue(foundCommunication.isPresent(), "Comunicação móvel deve ser encontrada pelo ID.");
        assertEquals(id, foundCommunication.get().getId());
        assertEquals(communication.getSignalStrength(), foundCommunication.get().getSignalStrength());
    }

    @Test
    @Order(3)
    @DisplayName("3. Deve retornar Optional vazio para ID não existente")
    void testFindByIdNotFound() throws SQLException {
        Optional<MobileCommunication> foundCommunication = mobileCommunicationDAO.findById(9999); // ID que não existe
        assertFalse(foundCommunication.isPresent(), "Não deve encontrar comunicação móvel para ID não existente.");
    }

    @Test
    @Order(4)
    @DisplayName("4. Deve retornar todos os registros de comunicação móvel")
    void testFindAll() throws SQLException {
        mobileCommunicationDAO.create(new MobileCommunication(0, testPointRecordId, LocalDateTime.now().plusMinutes(3), -23.5508, -46.6336, 85, 80, LocalDateTime.now(), LocalDateTime.now()));
        mobileCommunicationDAO.create(new MobileCommunication(0, testPointRecordId, LocalDateTime.now().plusMinutes(4), -23.5509, -46.6337, 90, 70, LocalDateTime.now(), LocalDateTime.now()));

        List<MobileCommunication> communications = mobileCommunicationDAO.findAll();
        assertNotNull(communications, "A lista de comunicações móveis não deve ser nula.");
        assertEquals(2, communications.size(), "Deve retornar duas comunicações móveis.");
    }

    @Test
    @Order(5)
    @DisplayName("5. Deve encontrar registros de comunicação móvel por ID de registro de ponto")
    void testFindByRecordId() throws SQLException {
        // Cria um segundo registro de ponto para testar
        PointRecord anotherPointRecord = new PointRecord(0, testJourneyId, LocalDateTime.now().plusHours(1), PointType.FIM_JORNADA, -23.6000, -46.7000, LocalDateTime.now(), LocalDateTime.now());
        int anotherPointRecordId = pointRecordDAO.create(anotherPointRecord);

        mobileCommunicationDAO.create(new MobileCommunication(0, testPointRecordId, LocalDateTime.now().plusMinutes(5), -23.5510, -46.6338, 70, 60, LocalDateTime.now(), LocalDateTime.now()));
        mobileCommunicationDAO.create(new MobileCommunication(0, testPointRecordId, LocalDateTime.now().plusMinutes(6), -23.5511, -46.6339, 65, 50, LocalDateTime.now(), LocalDateTime.now()));
        mobileCommunicationDAO.create(new MobileCommunication(0, anotherPointRecordId, LocalDateTime.now().plusMinutes(7), -23.6001, -46.7001, 95, 100, LocalDateTime.now(), LocalDateTime.now()));

        List<MobileCommunication> communicationsForTestRecord = mobileCommunicationDAO.findByRecordId(testPointRecordId);
        assertNotNull(communicationsForTestRecord);
        assertEquals(2, communicationsForTestRecord.size());
        assertTrue(communicationsForTestRecord.stream().allMatch(c -> c.getRecordId() == testPointRecordId));

        List<MobileCommunication> communicationsForAnotherRecord = mobileCommunicationDAO.findByRecordId(anotherPointRecordId);
        assertNotNull(communicationsForAnotherRecord);
        assertEquals(1, communicationsForAnotherRecord.size());
        assertTrue(communicationsForAnotherRecord.stream().allMatch(c -> c.getRecordId() == anotherPointRecordId));
    }

    @Test
    @Order(6)
    @DisplayName("6. Deve atualizar um registro de comunicação móvel existente com sucesso")
    void testUpdateMobileCommunicationSuccess() throws SQLException {
        MobileCommunication communication = new MobileCommunication(0, testPointRecordId, LocalDateTime.now().plusMinutes(8), -23.5512, -46.6340, 50, 40, LocalDateTime.now(), LocalDateTime.now());
        int id = mobileCommunicationDAO.create(communication);
        communication.setId(id);

        communication.setSignalStrength(99);
        communication.setBatteryLevel(100);
        communication.setLatitude(-23.1234);
        communication.setLongitude(-46.5678);
        communication.setUpdatedAt(LocalDateTime.now()); // Simula a atualização da data

        boolean updated = mobileCommunicationDAO.update(communication);
        assertTrue(updated, "A comunicação móvel deve ser atualizada com sucesso.");

        Optional<MobileCommunication> foundCommunication = mobileCommunicationDAO.findById(id);
        assertTrue(foundCommunication.isPresent());
        assertEquals(99, foundCommunication.get().getSignalStrength());
        assertEquals(100, foundCommunication.get().getBatteryLevel());
        assertEquals(-23.1234, foundCommunication.get().getLatitude());
        assertEquals(-46.5678, foundCommunication.get().getLongitude());
        // Verifica se a data de atualização foi realmente alterada (pode haver pequena diferença de milissegundos)
        assertTrue(foundCommunication.get().getUpdatedAt().isAfter(communication.getCreatedAt()));
    }

    @Test
    @Order(7)
    @DisplayName("7. Não deve atualizar registro de comunicação móvel com ID não existente")
    void testUpdateMobileCommunicationNotFound() throws SQLException {
        MobileCommunication nonExistentCommunication = new MobileCommunication(9999, testPointRecordId, LocalDateTime.now().plusMinutes(9), -23.5513, -46.6341, 10, 5, LocalDateTime.now(), LocalDateTime.now());
        boolean updated = mobileCommunicationDAO.update(nonExistentCommunication);
        assertFalse(updated, "Não deve atualizar uma comunicação móvel com ID não existente.");
    }

    @Test
    @Order(8)
    @DisplayName("8. Deve deletar um registro de comunicação móvel existente com sucesso")
    void testDeleteMobileCommunicationSuccess() throws SQLException {
        MobileCommunication communication = new MobileCommunication(0, testPointRecordId, LocalDateTime.now().plusMinutes(10), -23.5514, -46.6342, 40, 30, LocalDateTime.now(), LocalDateTime.now());
        int id = mobileCommunicationDAO.create(communication);

        boolean deleted = mobileCommunicationDAO.delete(id);
        assertTrue(deleted, "A comunicação móvel deve ser deletada com sucesso.");

        Optional<MobileCommunication> foundCommunication = mobileCommunicationDAO.findById(id);
        assertFalse(foundCommunication.isPresent(), "A comunicação móvel deletada não deve ser encontrada.");
    }

    @Test
    @Order(9)
    @DisplayName("9. Não deve deletar registro de comunicação móvel com ID não existente")
    void testDeleteMobileCommunicationNotFound() throws SQLException {
        boolean deleted = mobileCommunicationDAO.delete(9999); // ID que não existe
        assertFalse(deleted, "Não deve deletar uma comunicação móvel com ID não existente.");
    }
}
