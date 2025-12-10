package com.compliancesys.dao;

import com.compliancesys.config.DatabaseConfig;
import com.compliancesys.dao.impl.JourneyDAOImpl;
import com.compliancesys.model.Journey;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class JourneyDAOTest {

    private Connection connection;
    private JourneyDAO journeyDAO;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DatabaseConfig.getInstance().getConnection();
        connection.setAutoCommit(false); // Inicia transação para rollback
        journeyDAO = new JourneyDAOImpl(connection);
        // Limpa a tabela antes de cada teste, se necessário
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM journeys");
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.rollback(); // Desfaz todas as operações do teste
        connection.close();
    }

    @Test
    void testCreateJourney() throws SQLException {
        Journey journey = new Journey(0, 1, 1, LocalDate.now(), LocalDateTime.now(), null,
                "Origem Teste", "Destino Teste", 50.0, 1.5, 1.0, 0.2, 0.3, 0.0, "IN_PROGRESS", false,
                LocalDateTime.now(), LocalDateTime.now());

        int id = journeyDAO.create(journey);

        assertTrue(id > 0);
        Optional<Journey> foundJourney = journeyDAO.findById(id);
        assertTrue(foundJourney.isPresent());
        assertEquals(journey.getStartLocation(), foundJourney.get().getStartLocation());
    }

    @Test
    void testFindById() throws SQLException {
        Journey journey = new Journey(0, 1, 1, LocalDate.now(), LocalDateTime.now(), null,
                "Origem Teste", "Destino Teste", 50.0, 1.5, 1.0, 0.2, 0.3, 0.0, "IN_PROGRESS", false,
                LocalDateTime.now(), LocalDateTime.now());
        int id = journeyDAO.create(journey);

        Optional<Journey> foundJourney = journeyDAO.findById(id);

        assertTrue(foundJourney.isPresent());
        assertEquals(id, foundJourney.get().getId());
    }

    @Test
    void testFindAll() throws SQLException {
        journeyDAO.create(new Journey(0, 1, 1, LocalDate.now(), LocalDateTime.now(), null,
                "Origem 1", "Destino 1", 50.0, 1.5, 1.0, 0.2, 0.3, 0.0, "IN_PROGRESS", false,
                LocalDateTime.now(), LocalDateTime.now()));
        journeyDAO.create(new Journey(0, 2, 2, LocalDate.now(), LocalDateTime.now(), null,
                "Origem 2", "Destino 2", 70.0, 2.0, 1.5, 0.3, 0.2, 0.0, "COMPLETED", false,
                LocalDateTime.now(), LocalDateTime.now()));

        List<Journey> journeys = journeyDAO.findAll();

        assertFalse(journeys.isEmpty());
        assertEquals(2, journeys.size());
    }

    @Test
    void testUpdateJourney() throws SQLException {
        Journey journey = new Journey(0, 1, 1, LocalDate.now(), LocalDateTime.now(), null,
                "Origem Antiga", "Destino Antigo", 60.0, 1.8, 1.5, 0.2, 0.1, 0.0, "IN_PROGRESS", false,
                LocalDateTime.now(), LocalDateTime.now());
        int id = journeyDAO.create(journey);

        journey.setId(id);
        journey.setEndLocation("Destino Novo");
        journey.setTotalDistance(65.0);
        journey.setStatus("COMPLETED");
        journey.setUpdatedAt(LocalDateTime.now());

        boolean updated = journeyDAO.update(journey);

        assertTrue(updated);
        Optional<Journey> foundJourney = journeyDAO.findById(id);
        assertTrue(foundJourney.isPresent());
        assertEquals("Destino Novo", foundJourney.get().getEndLocation());
        assertEquals(65.0, foundJourney.get().getTotalDistance()); // Corrigido para getTotalDistance
        assertEquals("COMPLETED", foundJourney.get().getStatus());
    }

    @Test
    void testDeleteJourney() throws SQLException {
        Journey journey = new Journey(0, 1, 1, LocalDate.now(), LocalDateTime.now(), null,
                "Origem para Deletar", "Destino para Deletar", 80.0, 2.2, 1.8, 0.2, 0.2, 0.0, "IN_PROGRESS", false,
                LocalDateTime.now(), LocalDateTime.now());
        int id = journeyDAO.create(journey);

        boolean deleted = journeyDAO.delete(id);

        assertTrue(deleted);
        Optional<Journey> foundJourney = journeyDAO.findById(id);
        assertFalse(foundJourney.isPresent());
    }

    @Test
    void testFindByDriverId() throws SQLException {
        int testDriverId = 10;
        journeyDAO.create(new Journey(0, testDriverId, 1, LocalDate.now(), LocalDateTime.now(), null,
                "Jornada Driver 1", "Destino Driver 1", 10.0, 0.5, 0.4, 0.05, 0.05, 0.0, "IN_PROGRESS", false,
                LocalDateTime.now(), LocalDateTime.now()));
        journeyDAO.create(new Journey(0, testDriverId, 2, LocalDate.now().plusDays(1), LocalDateTime.now(), null,
                "Jornada Driver 2", "Destino Driver 2", 20.0, 1.0, 0.8, 0.1, 0.1, 0.0, "COMPLETED", false,
                LocalDateTime.now(), LocalDateTime.now()));
        journeyDAO.create(new Journey(0, 99, 3, LocalDate.now(), LocalDateTime.now(), null,
                "Jornada Outro Driver", "Destino Outro Driver", 5.0, 0.2, 0.1, 0.05, 0.05, 0.0, "PENDING", false,
                LocalDateTime.now(), LocalDateTime.now()));

        List<Journey> journeys = journeyDAO.findByDriverId(testDriverId);

        assertFalse(journeys.isEmpty());
        assertEquals(2, journeys.size());
        assertTrue(journeys.stream().allMatch(j -> j.getDriverId() == testDriverId));
    }

    @Test
    void testFindByDriverIdAndDate() throws SQLException {
        int testDriverId = 10;
        LocalDate testDate = LocalDate.now().plusDays(3);
        journeyDAO.create(new Journey(0, testDriverId, 1, testDate, LocalDateTime.now(), null,
                "Jornada Data 1", "Destino Data 1", 15.0, 0.8, 0.7, 0.05, 0.05, 0.0, "IN_PROGRESS", false,
                LocalDateTime.now(), LocalDateTime.now()));
        journeyDAO.create(new Journey(0, testDriverId, 2, testDate.plusDays(1), LocalDateTime.now(), null,
                "Jornada Data 2", "Destino Data 2", 25.0, 1.2, 1.0, 0.1, 0.1, 0.0, "COMPLETED", false,
                LocalDateTime.now(), LocalDateTime.now()));

        Optional<Journey> foundJourney = journeyDAO.findByDriverIdAndDate(testDriverId, testDate);

        assertTrue(foundJourney.isPresent());
        assertEquals(testDriverId, foundJourney.get().getDriverId());
        assertEquals(testDate, foundJourney.get().getJourneyDate());
    }

    @Test
    void testFindByVehicleId() throws SQLException {
        int testVehicleId = 20;
        journeyDAO.create(new Journey(0, 1, testVehicleId, LocalDate.now(), LocalDateTime.now(), null,
                "Jornada Veiculo 1", "Destino Veiculo 1", 30.0, 1.0, 0.8, 0.1, 0.1, 0.0, "IN_PROGRESS", false,
                LocalDateTime.now(), LocalDateTime.now()));
        journeyDAO.create(new Journey(0, 2, testVehicleId, LocalDate.now().plusDays(1), LocalDateTime.now(), null,
                "Jornada Veiculo 2", "Destino Veiculo 2", 40.0, 1.3, 1.0, 0.15, 0.15, 0.0, "COMPLETED", false,
                LocalDateTime.now(), LocalDateTime.now()));
        journeyDAO.create(new Journey(0, 3, 99, LocalDate.now(), LocalDateTime.now(), null,
                "Jornada Outro Veiculo", "Destino Outro Veiculo", 5.0, 0.2, 0.1, 0.05, 0.05, 0.0, "PENDING", false,
                LocalDateTime.now(), LocalDateTime.now()));

        List<Journey> journeys = journeyDAO.findByVehicleId(testVehicleId);

        assertFalse(journeys.isEmpty());
        assertEquals(2, journeys.size());
        assertTrue(journeys.stream().allMatch(j -> j.getVehicleId() == testVehicleId));
    }

    @Test
    void testFindByVehicleIdAndDate() throws SQLException {
        int testVehicleId = 20;
        LocalDate testDate = LocalDate.now().plusDays(4);
        journeyDAO.create(new Journey(0, 1, testVehicleId, testDate, LocalDateTime.now(), null,
                "Jornada Veiculo Data 1", "Destino Veiculo Data 1", 35.0, 1.1, 0.9, 0.1, 0.1, 0.0, "IN_PROGRESS", false,
                LocalDateTime.now(), LocalDateTime.now()));
        journeyDAO.create(new Journey(0, 2, testVehicleId, testDate.plusDays(1), LocalDateTime.now(), null,
                "Jornada Veiculo Data 2", "Destino Veiculo Data 2", 45.0, 1.4, 1.1, 0.15, 0.15, 0.0, "COMPLETED", false,
                LocalDateTime.now(), LocalDateTime.now()));

        List<Journey> foundJourneys = journeyDAO.findByVehicleIdAndDate(testVehicleId, testDate);

        assertFalse(foundJourneys.isEmpty());
        assertEquals(1, foundJourneys.size());
        assertEquals(testVehicleId, foundJourneys.get(0).getVehicleId());
        assertEquals(testDate, foundJourneys.get(0).getJourneyDate());
    }

    @Test
    void testFindByStatus() throws SQLException {
        journeyDAO.create(new Journey(0, 1, 1, LocalDate.now(), LocalDateTime.now(), null,
                "Jornada Status 1", "Destino Status 1", 10.0, 0.5, 0.4, 0.05, 0.05, 0.0, "IN_PROGRESS", false,
                LocalDateTime.now(), LocalDateTime.now()));
        journeyDAO.create(new Journey(0, 2, 2, LocalDate.now(), LocalDateTime.now(), null,
                "Jornada Status 2", "Destino Status 2", 20.0, 1.0, 0.8, 0.1, 0.1, 0.0, "COMPLETED", false,
                LocalDateTime.now(), LocalDateTime.now()));
        journeyDAO.create(new Journey(0, 3, 3, LocalDate.now(), LocalDateTime.now(), null,
                "Jornada Status 3", "Destino Status 3", 30.0, 1.5, 1.2, 0.15, 0.15, 0.0, "IN_PROGRESS", false,
                LocalDateTime.now(), LocalDateTime.now()));

        List<Journey> inProgressJourneys = journeyDAO.findByStatus("IN_PROGRESS");

        assertFalse(inProgressJourneys.isEmpty());
        assertEquals(2, inProgressJourneys.size());
        assertTrue(inProgressJourneys.stream().allMatch(j -> j.getStatus().equals("IN_PROGRESS")));
    }

    @Test
    void testFindByDateRange() throws SQLException {
        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(1);

        journeyDAO.create(new Journey(0, 1, 1, LocalDate.now().minusDays(2), LocalDateTime.now(), null,
                "Fora do Range", "Fora do Range", 10.0, 0.5, 0.4, 0.05, 0.05, 0.0, "IN_PROGRESS", false,
                LocalDateTime.now(), LocalDateTime.now()));
        journeyDAO.create(new Journey(0, 2, 2, LocalDate.now(), LocalDateTime.now(), null,
                "Dentro do Range 1", "Dentro do Range 1", 20.0, 1.0, 0.8, 0.1, 0.1, 0.0, "COMPLETED", false,
                LocalDateTime.now(), LocalDateTime.now()));
        journeyDAO.create(new Journey(0, 3, 3, LocalDate.now().plusDays(1), LocalDateTime.now(), null,
                "Dentro do Range 2", "Dentro do Range 2", 30.0, 1.5, 1.2, 0.15, 0.15, 0.0, "IN_PROGRESS", false,
                LocalDateTime.now(), LocalDateTime.now()));

        List<Journey> journeys = journeyDAO.findByDateRange(startDate, endDate);

        assertFalse(journeys.isEmpty());
        assertEquals(2, journeys.size());
        assertTrue(journeys.stream().allMatch(j -> !j.getJourneyDate().isBefore(startDate) && !j.getJourneyDate().isAfter(endDate)));
    }

    @Test
    void testFindByDriverIdAndDateRange() throws SQLException {
        int testDriverId = 30;
        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(1);

        journeyDAO.create(new Journey(0, testDriverId, 1, LocalDate.now().minusDays(2), LocalDateTime.now(), null,
                "Fora do Range Driver", "Fora do Range Driver", 10.0, 0.5, 0.4, 0.05, 0.05, 0.0, "IN_PROGRESS", false,
                LocalDateTime.now(), LocalDateTime.now()));
        journeyDAO.create(new Journey(0, testDriverId, 2, LocalDate.now(), LocalDateTime.now(), null,
                "Dentro do Range Driver 1", "Dentro do Range Driver 1", 20.0, 1.0, 0.8, 0.1, 0.1, 0.0, "COMPLETED", false,
                LocalDateTime.now(), LocalDateTime.now()));
        journeyDAO.create(new Journey(0, testDriverId, 3, LocalDate.now().plusDays(1), LocalDateTime.now(), null,
                "Dentro do Range Driver 2", "Dentro do Range Driver 2", 30.0, 1.5, 1.2, 0.15, 0.15, 0.0, "IN_PROGRESS", false,
                LocalDateTime.now(), LocalDateTime.now()));
        journeyDAO.create(new Journey(0, 99, 4, LocalDate.now(), LocalDateTime.now(), null,
                "Outro Driver", "Outro Driver", 5.0, 0.2, 0.1, 0.05, 0.05, 0.0, "PENDING", false,
                LocalDateTime.now(), LocalDateTime.now()));

        List<Journey> journeys = journeyDAO.findByDriverIdAndDateRange(testDriverId, startDate, endDate);

        assertFalse(journeys.isEmpty());
        assertEquals(2, journeys.size());
        assertTrue(journeys.stream().allMatch(j -> j.getDriverId() == testDriverId && !j.getJourneyDate().isBefore(startDate) && !j.getJourneyDate().isAfter(endDate)));
    }

    @Test
    void testFindByStatusAndDateRange() throws SQLException {
        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(1);
        String status = "COMPLETED";

        journeyDAO.create(new Journey(0, 1, 1, LocalDate.now().minusDays(2), LocalDateTime.now(), null,
                "Fora do Range Status", "Fora do Range Status", 10.0, 0.5, 0.4, 0.05, 0.05, 0.0, "COMPLETED", false,
                LocalDateTime.now(), LocalDateTime.now()));
        journeyDAO.create(new Journey(0, 2, 2, LocalDate.now(), LocalDateTime.now(), null,
                "Dentro do Range Status 1", "Dentro do Range Status 1", 20.0, 1.0, 0.8, 0.1, 0.1, 0.0, "COMPLETED", false,
                LocalDateTime.now(), LocalDateTime.now()));
        journeyDAO.create(new Journey(0, 3, 3, LocalDate.now().plusDays(1), LocalDateTime.now(), null,
                "Dentro do Range Status 2", "Dentro do Range Status 2", 30.0, 1.5, 1.2, 0.15, 0.15, 0.0, "IN_PROGRESS", false,
                LocalDateTime.now(), LocalDateTime.now()));

        List<Journey> journeys = journeyDAO.findByStatusAndDateRange(status, startDate, endDate);

        assertFalse(journeys.isEmpty());
        assertEquals(1, journeys.size());
        assertTrue(journeys.stream().allMatch(j -> j.getStatus().equals(status) && !j.getJourneyDate().isBefore(startDate) && !j.getJourneyDate().isAfter(endDate)));
    }
}
