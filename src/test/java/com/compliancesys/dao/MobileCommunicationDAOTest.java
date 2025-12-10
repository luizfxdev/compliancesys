package com.compliancesys.dao;

import com.compliancesys.dao.impl.DriverDAOImpl;
import com.compliancesys.dao.impl.JourneyDAOImpl;
import com.compliancesys.dao.impl.MobileCommunicationDAOImpl;
import com.compliancesys.model.Driver;
import com.compliancesys.model.Journey;
import com.compliancesys.model.MobileCommunication;
import com.compliancesys.util.DatabaseConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MobileCommunicationDAOTest {

    private MobileCommunicationDAO mobileCommunicationDAO;
    private DriverDAO driverDAO;
    private JourneyDAO journeyDAO;

    private int testDriverId;
    private int testJourneyId;
    private int testTimeRecordId; // ID do registro de ponto (Journey)

    @BeforeEach
    void setUp() throws SQLException {
        mobileCommunicationDAO = new MobileCommunicationDAOImpl();
        driverDAO = new DriverDAOImpl();
        journeyDAO = new JourneyDAOImpl();
        clearDatabase(); // Limpa o banco antes de cada teste

        // Cria um driver e uma jornada para os testes de comunicação móvel
        Driver testDriver = new Driver(1, "Driver Teste", "12345678901", "LIC123", "B", LocalDate.now().plusYears(1), LocalDate.of(1990, 1, 1), "11987654321", "driver@test.com");
        testDriverId = driverDAO.create(testDriver);

        Journey testJourney = new Journey(testDriverId, 1, LocalDate.now(), LocalDateTime.now(), LocalDateTime.now().plusHours(2), "IN_PROGRESS", "Origem", 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, "Destino", false);
        testJourneyId = journeyDAO.create(testJourney);
        testTimeRecordId = testJourneyId; // Usando o ID da jornada como ID do registro de ponto
    }

    @AfterEach
    void tearDown() throws SQLException {
        clearDatabase(); // Limpa o banco após cada teste
    }

    private void clearDatabase() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM mobile_communications; DELETE FROM journeys; DELETE FROM drivers;")) {
            stmt.executeUpdate();
        }
    }

    @Test
    void testCreateAndFindById() throws SQLException {
        MobileCommunication communication = new MobileCommunication(testDriverId, testTimeRecordId, LocalDateTime.now(), -23.5505, -46.6333, LocalDateTime.now(), true, null);
        int id = mobileCommunicationDAO.create(communication);

        assertTrue(id > 0);
        communication.setId(id); // Atribui o ID gerado para comparação

        Optional<MobileCommunication> foundCommunication = mobileCommunicationDAO.findById(id);
        assertTrue(foundCommunication.isPresent());
        assertEquals(communication.getDriverId(), foundCommunication.get().getDriverId());
        assertEquals(communication.getRecordId(), foundCommunication.get().getRecordId());
        assertEquals(communication.getLatitude(), foundCommunication.get().getLatitude());
    }

    @Test
    void testUpdate() throws SQLException {
        MobileCommunication communication = new MobileCommunication(testDriverId, testTimeRecordId, LocalDateTime.now(), -23.5505, -46.6333, LocalDateTime.now(), true, null);
        int id = mobileCommunicationDAO.create(communication);
        communication.setId(id);

        communication.setLatitude(-23.6000);
        communication.setLongitude(-46.7000);
        communication.setErrorMessage("Erro de teste");
        communication.setUpdatedAt(LocalDateTime.now());

        boolean updated = mobileCommunicationDAO.update(communication);
        assertTrue(updated);

        Optional<MobileCommunication> foundCommunication = mobileCommunicationDAO.findById(id);
        assertTrue(foundCommunication.isPresent());
        assertEquals(-23.6000, foundCommunication.get().getLatitude());
        assertEquals(-46.7000, foundCommunication.get().getLongitude());
        assertEquals("Erro de teste", foundCommunication.get().getErrorMessage());
    }

    @Test
    void testDelete() throws SQLException {
        MobileCommunication communication = new MobileCommunication(testDriverId, testTimeRecordId, LocalDateTime.now(), -23.5505, -46.6333, LocalDateTime.now(), true, null);
        int id = mobileCommunicationDAO.create(communication);

        boolean deleted = mobileCommunicationDAO.delete(id);
        assertTrue(deleted);

        Optional<MobileCommunication> foundCommunication = mobileCommunicationDAO.findById(id);
        assertFalse(foundCommunication.isPresent());
    }

    @Test
    void testFindAll() throws SQLException {
        mobileCommunicationDAO.create(new MobileCommunication(testDriverId, testTimeRecordId, LocalDateTime.now(), -23.5505, -46.6333, LocalDateTime.now(), true, null));
        mobileCommunicationDAO.create(new MobileCommunication(testDriverId, testTimeRecordId, LocalDateTime.now().plusMinutes(1), -23.5506, -46.6334, LocalDateTime.now().plusMinutes(1), true, null));

        List<MobileCommunication> communications = mobileCommunicationDAO.findAll();
        assertNotNull(communications);
        assertEquals(2, communications.size());
    }

    @Test
    void testFindByDriverId() throws SQLException {
        int otherDriverId = driverDAO.create(new Driver(1, "Outro Driver", "99988877766", "LIC456", "C", LocalDate.now().plusYears(2), LocalDate.of(1985, 5, 5), "11977665544", "outro@test.com"));

        mobileCommunicationDAO.create(new MobileCommunication(testDriverId, testTimeRecordId, LocalDateTime.now(), -23.5505, -46.6333, LocalDateTime.now(), true, null));
        mobileCommunicationDAO.create(new MobileCommunication(testDriverId, testTimeRecordId, LocalDateTime.now().plusMinutes(1), -23.5506, -46.6334, LocalDateTime.now().plusMinutes(1), true, null));
        mobileCommunicationDAO.create(new MobileCommunication(otherDriverId, testTimeRecordId, LocalDateTime.now().plusMinutes(2), -23.5507, -46.6335, LocalDateTime.now().plusMinutes(2), true, null));

        List<MobileCommunication> communications = mobileCommunicationDAO.findByDriverId(testDriverId);
        assertNotNull(communications);
        assertEquals(2, communications.size());
        assertTrue(communications.stream().allMatch(c -> c.getDriverId() == testDriverId));
    }

    @Test
    void testFindByRecordId() throws SQLException {
        int otherRecordId = journeyDAO.create(new Journey(testDriverId, 1, LocalDate.now(), LocalDateTime.now(), LocalDateTime.now().plusHours(3), "COMPLETED", "Outra Origem", 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, "Outro Destino", true));

        mobileCommunicationDAO.create(new MobileCommunication(testDriverId, testTimeRecordId, LocalDateTime.now(), -23.5505, -46.6333, LocalDateTime.now(), true, null));
        mobileCommunicationDAO.create(new MobileCommunication(testDriverId, testTimeRecordId, LocalDateTime.now().minusMinutes(30), 1.0, 1.0, LocalDateTime.now().minusMinutes(30), true, null));
        mobileCommunicationDAO.create(new MobileCommunication(testDriverId, otherRecordId, LocalDateTime.now(), 2.0, 2.0, LocalDateTime.now(), true, null));

        List<MobileCommunication> communications = mobileCommunicationDAO.findByRecordId(testTimeRecordId);
        assertNotNull(communications);
        assertEquals(2, communications.size());
        assertTrue(communications.stream().allMatch(c -> c.getRecordId() == testTimeRecordId));
    }
}
