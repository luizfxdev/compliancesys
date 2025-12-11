package com.compliancesys.dao;

import com.compliancesys.dao.impl.DriverDAOImpl;
import com.compliancesys.dao.impl.JourneyDAOImpl;
import com.compliancesys.dao.impl.MobileCommunicationDAOImpl;
import com.compliancesys.model.Driver;
import com.compliancesys.model.Journey;
import com.compliancesys.model.MobileCommunication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MobileCommunicationDAOTest {

    private MobileCommunicationDAO mobileCommunicationDAO;
    private DriverDAO driverDAO;
    private JourneyDAO journeyDAO;
    private Connection connection;

    private static final String DB_URL = "jdbc:h2:mem:testdb_mobile_comm;DB_CLOSE_DELAY=-1";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        createSchema(connection);
        mobileCommunicationDAO = new MobileCommunicationDAOImpl(connection);
        driverDAO = new DriverDAOImpl(connection);
        journeyDAO = new JourneyDAOImpl(connection);
    }

    private void createSchema(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS mobile_communications;");
            stmt.execute("DROP TABLE IF EXISTS journeys;");
            stmt.execute("DROP TABLE IF EXISTS drivers;");
            stmt.execute("DROP TABLE IF EXISTS vehicles;"); // Adicionado para consistência

            // Recriando a tabela drivers com company_id
            stmt.execute("CREATE TABLE drivers (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "company_id INT NOT NULL," + // Adicionado company_id
                    "name VARCHAR(255) NOT NULL," +
                    "cpf VARCHAR(14) UNIQUE NOT NULL," +
                    "license_number VARCHAR(20) UNIQUE NOT NULL," +
                    "license_category VARCHAR(5) NOT NULL," +
                    "license_expiration DATE NOT NULL," +
                    "birth_date DATE NOT NULL," +
                    "phone VARCHAR(20)," +
                    "email VARCHAR(255)," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ");");

            // Recriando a tabela vehicles
            stmt.execute("CREATE TABLE vehicles (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "plate VARCHAR(10) UNIQUE NOT NULL," +
                    "manufacturer VARCHAR(255)," +
                    "model VARCHAR(255)," +
                    "year INT," +
                    "company_id INT NOT NULL," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ");");

            // Recriando a tabela journeys (ALINHADO COM O SCHEMA.SQL FORNECIDO)
            stmt.execute("CREATE TABLE journeys (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "driver_id INT NOT NULL," +
                    "journey_date DATE NOT NULL," +
                    "total_driving_time_minutes INT NOT NULL DEFAULT 0," +
                    "total_rest_time_minutes INT NOT NULL DEFAULT 0," +
                    "compliance_status VARCHAR(50) NOT NULL DEFAULT 'PENDING'," +
                    "daily_limit_exceeded BOOLEAN NOT NULL DEFAULT FALSE," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (driver_id) REFERENCES drivers(id)" +
                    ");");

            // Recriando a tabela mobile_communications
            stmt.execute("CREATE TABLE mobile_communications (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "driver_id INT NOT NULL," +
                    "record_id INT NOT NULL," + // ID do registro de ponto associado
                    "timestamp TIMESTAMP NOT NULL," +
                    "latitude DOUBLE," +
                    "longitude DOUBLE," +
                    "send_timestamp TIMESTAMP," +
                    "send_success BOOLEAN," +
                    "error_message TEXT," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (driver_id) REFERENCES drivers(id)" +
                    ");");
        }
    }

    @Test
    void testCreateAndFindMobileCommunication() throws SQLException {
        // Driver agora precisa de companyId
        Driver testDriver = new Driver(1, 1, "Test Driver", "11122233344", "LIC123", "B", LocalDate.now().plusYears(1), LocalDate.of(1990, 1, 1), "11987654321", "test@driver.com");
        int testDriverId = driverDAO.create(testDriver);

        // Journey agora usa o novo construtor alinhado com o schema.sql
        Journey testJourney = new Journey(testDriverId, LocalDate.now(), 480, 60, "PENDING", false);
        int testJourneyId = journeyDAO.create(testJourney);

        MobileCommunication communication = new MobileCommunication(testDriverId, testJourneyId, LocalDateTime.now(), -23.5505, -46.6333, LocalDateTime.now(), true, null);
        int commId = mobileCommunicationDAO.create(communication);

        assertTrue(commId > 0);

        Optional<MobileCommunication> foundComm = mobileCommunicationDAO.findById(commId);
        assertTrue(foundComm.isPresent());
        assertEquals(commId, foundComm.get().getId());
        assertEquals(testDriverId, foundComm.get().getDriverId());
        assertEquals(testJourneyId, foundComm.get().getRecordId());
        assertEquals(-23.5505, foundComm.get().getLatitude(), 0.0001);
        assertEquals(-46.6333, foundComm.get().getLongitude(), 0.0001);
    }

    @Test
    void testFindAllMobileCommunications() throws SQLException {
        // Drivers agora precisam de companyId
        Driver testDriver1 = new Driver(1, 1, "Driver One", "11122233344", "LIC111", "B", LocalDate.now().plusYears(1), LocalDate.of(1990, 1, 1), "11987654321", "one@driver.com");
        int testDriverId1 = driverDAO.create(testDriver1);
        Driver testDriver2 = new Driver(1, 1, "Driver Two", "55566677788", "LIC222", "C", LocalDate.now().plusYears(2), LocalDate.of(1991, 2, 2), "11987654322", "two@driver.com");
        int testDriverId2 = driverDAO.create(testDriver2);

        // Journeys agora usam o novo construtor alinhado com o schema.sql
        Journey testJourney1 = new Journey(testDriverId1, LocalDate.now(), 480, 60, "PENDING", false);
        int testJourneyId1 = journeyDAO.create(testJourney1);
        Journey testJourney2 = new Journey(testDriverId2, LocalDate.now().plusDays(1), 500, 70, "COMPLETED", true);
        int testJourneyId2 = journeyDAO.create(testJourney2);

        mobileCommunicationDAO.create(new MobileCommunication(testDriverId1, testJourneyId1, LocalDateTime.now(), -23.5505, -46.6333, LocalDateTime.now(), true, null));
        mobileCommunicationDAO.create(new MobileCommunication(testDriverId2, testJourneyId2, LocalDateTime.now().plusHours(1), -23.5600, -46.6400, LocalDateTime.now().plusHours(1), false, "Error"));

        List<MobileCommunication> communications = mobileCommunicationDAO.findAll();
        assertNotNull(communications);
        assertEquals(2, communications.size());
    }

    @Test
    void testUpdateMobileCommunication() throws SQLException {
        // Driver agora precisa de companyId
        Driver testDriver = new Driver(1, 1, "Test Driver", "11122233344", "LIC123", "B", LocalDate.now().plusYears(1), LocalDate.of(1990, 1, 1), "11987654321", "test@driver.com");
        int testDriverId = driverDAO.create(testDriver);

        // Journey agora usa o novo construtor alinhado com o schema.sql
        Journey testJourney = new Journey(testDriverId, LocalDate.now(), 480, 60, "PENDING", false);
        int testJourneyId = journeyDAO.create(testJourney);

        MobileCommunication communication = new MobileCommunication(testDriverId, testJourneyId, LocalDateTime.now(), -23.5505, -46.6333, LocalDateTime.now(), true, null);
        int commId = mobileCommunicationDAO.create(communication);

        Optional<MobileCommunication> createdComm = mobileCommunicationDAO.findById(commId);
        assertTrue(createdComm.isPresent());

        MobileCommunication commToUpdate = createdComm.get();
        commToUpdate.setLatitude(-24.0000);
        commToUpdate.setLongitude(-47.0000);
        commToUpdate.setErrorMessage("Updated Error");
        commToUpdate.setSendSuccess(false);

        boolean updated = mobileCommunicationDAO.update(commToUpdate);
        assertTrue(updated);

        Optional<MobileCommunication> foundUpdatedComm = mobileCommunicationDAO.findById(commId);
        assertTrue(foundUpdatedComm.isPresent());
        assertEquals(-24.0000, foundUpdatedComm.get().getLatitude(), 0.0001);
        assertEquals(-47.0000, foundUpdatedComm.get().getLongitude(), 0.0001);
        assertEquals("Updated Error", foundUpdatedComm.get().getErrorMessage());
        assertFalse(foundUpdatedComm.get().isSendSuccess());
    }

    @Test
    void testDeleteMobileCommunication() throws SQLException {
        // Driver agora precisa de companyId
        Driver testDriver = new Driver(1, 1, "Test Driver", "11122233344", "LIC123", "B", LocalDate.now().plusYears(1), LocalDate.of(1990, 1, 1), "11987654321", "test@driver.com");
        int testDriverId = driverDAO.create(testDriver);

        // Journey agora usa o novo construtor alinhado com o schema.sql
        Journey testJourney = new Journey(testDriverId, LocalDate.now(), 480, 60, "PENDING", false);
        int testJourneyId = journeyDAO.create(testJourney);

        MobileCommunication communication = new MobileCommunication(testDriverId, testJourneyId, LocalDateTime.now(), -23.5505, -46.6333, LocalDateTime.now(), true, null);
        int commId = mobileCommunicationDAO.create(communication);

        assertTrue(mobileCommunicationDAO.findById(commId).isPresent());

        boolean deleted = mobileCommunicationDAO.delete(commId);
        assertTrue(deleted);

        assertFalse(mobileCommunicationDAO.findById(commId).isPresent());
    }

    @Test
    void testFindByDriverId() throws SQLException {
        // Drivers agora precisam de companyId
        Driver testDriver1 = new Driver(1, 1, "Driver One", "11122233344", "LIC111", "B", LocalDate.now().plusYears(1), LocalDate.of(1990, 1, 1), "11987654321", "one@driver.com");
        int testDriverId1 = driverDAO.create(testDriver1);
        Driver testDriver2 = new Driver(1, 1, "Driver Two", "55566677788", "LIC222", "C", LocalDate.now().plusYears(2), LocalDate.of(1991, 2, 2), "11987654322", "two@driver.com");
        int testDriverId2 = driverDAO.create(testDriver2);

        // Journeys agora usam o novo construtor alinhado com o schema.sql
        Journey testJourney1 = new Journey(testDriverId1, LocalDate.now(), 480, 60, "PENDING", false);
        int testJourneyId1 = journeyDAO.create(testJourney1);
        Journey testJourney2 = new Journey(testDriverId2, LocalDate.now().plusDays(1), 500, 70, "COMPLETED", true);
        int testJourneyId2 = journeyDAO.create(testJourney2);

        mobileCommunicationDAO.create(new MobileCommunication(testDriverId1, testJourneyId1, LocalDateTime.now(), -23.5505, -46.6333, LocalDateTime.now(), true, null));
        mobileCommunicationDAO.create(new MobileCommunication(testDriverId1, testJourneyId1, LocalDateTime.now().plusHours(1), -23.5600, -46.6400, LocalDateTime.now().plusHours(1), false, "Error"));
        mobileCommunicationDAO.create(new MobileCommunication(testDriverId2, testJourneyId2, LocalDateTime.now().plusHours(2), -23.5700, -46.6500, LocalDateTime.now().plusHours(2), true, null));

        List<MobileCommunication> communications = mobileCommunicationDAO.findByDriverId(testDriverId1);
        assertNotNull(communications);
        assertEquals(2, communications.size());
        assertTrue(communications.stream().allMatch(c -> c.getDriverId() == testDriverId1));
    }

    @Test
    void testFindByRecordId() throws SQLException {
        // Driver agora precisa de companyId
        Driver testDriver = new Driver(1, 1, "Driver Teste", "12345678901", "LIC123", "B", LocalDate.now().plusYears(1), LocalDate.of(1990, 1, 1), "11987654321", "driver@test.com");
        int testDriverId = driverDAO.create(testDriver);

        // Journey agora usa o novo construtor alinhado com o schema.sql
        Journey testJourney = new Journey(testDriverId, LocalDate.now(), 480, 60, "PENDING", false);
        int testJourneyId = journeyDAO.create(testJourney);

        mobileCommunicationDAO.create(new MobileCommunication(testDriverId, testJourneyId, LocalDateTime.now(), -23.5505, -46.6333, LocalDateTime.now(), true, null));
        mobileCommunicationDAO.create(new MobileCommunication(testDriverId, testJourneyId, LocalDateTime.now().plusHours(1), -23.5600, -46.6400, LocalDateTime.now().plusHours(1), false, "Error"));

        List<MobileCommunication> communications = mobileCommunicationDAO.findByRecordId(testJourneyId);
        assertNotNull(communications);
        assertEquals(2, communications.size());
        assertTrue(communications.stream().allMatch(c -> c.getRecordId() == testJourneyId));
    }
}
