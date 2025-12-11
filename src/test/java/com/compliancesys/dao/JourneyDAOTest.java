package com.compliancesys.dao;

import com.compliancesys.config.DatabaseConfig;
import com.compliancesys.dao.impl.JourneyDAOImpl;
import com.compliancesys.model.Journey;
import com.compliancesys.model.enums.ComplianceStatus; // Importar ComplianceStatus
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class JourneyDAOTest {

    private JourneyDAO journeyDAO;
    private Connection connection;
    private DatabaseConfig dbConfig; // Adicionado para gerenciar o DataSource

    @BeforeEach
    void setUp() throws SQLException {
        dbConfig = DatabaseConfig.getInstance(); // Obter a instância do Singleton
        connection = dbConfig.getConnection(); // Obter uma conexão do pool
        connection.setAutoCommit(false); // Iniciar transação para rollback
        journeyDAO = new JourneyDAOImpl(connection);
        // Limpar a tabela antes de cada teste, se necessário, ou usar um banco de dados em memória
        // Para testes de integração, é comum limpar ou resetar o estado do DB
        // executeSql("DELETE FROM journeys"); // Exemplo, ajuste conforme sua necessidade
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (connection != null) {
            connection.rollback(); // Reverter todas as operações
            connection.close(); // Devolver a conexão ao pool
        }
    }

    // Método auxiliar para executar SQL (se necessário para setup/teardown)
    private void executeSql(String sql) throws SQLException {
        try (java.sql.Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    @Test
    @DisplayName("Deve criar uma nova jornada e retornar o ID gerado")
    void shouldCreateNewJourneyAndReturnGeneratedId() throws SQLException {
        // Usando o construtor para inserção (sem ID, createdAt, updatedAt)
        Journey newJourney = new Journey(
                1, // driverId
                LocalDate.now(), // journeyDate
                480, // totalDrivingTimeMinutes
                60,  // totalRestTimeMinutes
                ComplianceStatus.PENDENTE.name(), // complianceStatus (convertido para String)
                false // dailyLimitExceeded
        );

        int generatedId = journeyDAO.create(newJourney);

        assertTrue(generatedId > 0);
        Optional<Journey> foundJourney = journeyDAO.findById(generatedId);
        assertTrue(foundJourney.isPresent());
        assertEquals(newJourney.getDriverId(), foundJourney.get().getDriverId());
        assertEquals(newJourney.getJourneyDate(), foundJourney.get().getJourneyDate());
        assertEquals(newJourney.getTotalDrivingTimeMinutes(), foundJourney.get().getTotalDrivingTimeMinutes());
        assertEquals(newJourney.getTotalRestTimeMinutes(), foundJourney.get().getTotalRestTimeMinutes());
        assertEquals(newJourney.getComplianceStatus(), foundJourney.get().getComplianceStatus());
        assertEquals(newJourney.isDailyLimitExceeded(), foundJourney.get().isDailyLimitExceeded());
    }

    @Test
    @DisplayName("Deve encontrar uma jornada existente por ID")
    void shouldFindExistingJourneyById() throws SQLException {
        // Criar uma jornada para buscar
        Journey journeyToCreate = new Journey(
                1, // driverId
                LocalDate.now().minusDays(1), // journeyDate
                400, // totalDrivingTimeMinutes
                50,  // totalRestTimeMinutes
                ComplianceStatus.CONFORME.name(), // complianceStatus
                false // dailyLimitExceeded
        );
        int createdId = journeyDAO.create(journeyToCreate);

        Optional<Journey> foundJourney = journeyDAO.findById(createdId);

        assertTrue(foundJourney.isPresent());
        assertEquals(createdId, foundJourney.get().getId());
        assertEquals(journeyToCreate.getDriverId(), foundJourney.get().getDriverId());
        assertEquals(journeyToCreate.getJourneyDate(), foundJourney.get().getJourneyDate());
        assertEquals(journeyToCreate.getComplianceStatus(), foundJourney.get().getComplianceStatus());
    }

    @Test
    @DisplayName("Não deve encontrar jornada para um ID inexistente")
    void shouldNotFindJourneyForNonExistentId() throws SQLException {
        Optional<Journey> foundJourney = journeyDAO.findById(9999);
        assertFalse(foundJourney.isPresent());
    }

    @Test
    @DisplayName("Deve atualizar uma jornada existente")
    void shouldUpdateExistingJourney() throws SQLException {
        // Criar uma jornada para atualizar
        Journey journeyToCreate = new Journey(
                2, // driverId
                LocalDate.now().minusDays(2), // journeyDate
                300, // totalDrivingTimeMinutes
                40,  // totalRestTimeMinutes
                ComplianceStatus.ALERTA.name(), // complianceStatus
                true // dailyLimitExceeded
        );
        int createdId = journeyDAO.create(journeyToCreate);

        // Obter a jornada e modificar
        Optional<Journey> foundJourneyOptional = journeyDAO.findById(createdId);
        assertTrue(foundJourneyOptional.isPresent());
        Journey journeyToUpdate = foundJourneyOptional.get();

        journeyToUpdate.setTotalDrivingTimeMinutes(500);
        journeyToUpdate.setTotalRestTimeMinutes(70);
        journeyToUpdate.setComplianceStatus(ComplianceStatus.NAO_CONFORME.name()); // Atualizar status
        journeyToUpdate.setDailyLimitExceeded(false);
        journeyToUpdate.setUpdatedAt(LocalDateTime.now()); // Simular atualização da data

        boolean updated = journeyDAO.update(journeyToUpdate);
        assertTrue(updated);

        Optional<Journey> updatedJourneyOptional = journeyDAO.findById(createdId);
        assertTrue(updatedJourneyOptional.isPresent());
        Journey updatedJourney = updatedJourneyOptional.get();

        assertEquals(journeyToUpdate.getTotalDrivingTimeMinutes(), updatedJourney.getTotalDrivingTimeMinutes());
        assertEquals(journeyToUpdate.getTotalRestTimeMinutes(), updatedJourney.getTotalRestTimeMinutes());
        assertEquals(journeyToUpdate.getComplianceStatus(), updatedJourney.getComplianceStatus());
        assertEquals(journeyToUpdate.isDailyLimitExceeded(), updatedJourney.isDailyLimitExceeded());
    }

    @Test
    @DisplayName("Deve deletar uma jornada existente por ID")
    void shouldDeleteExistingJourneyById() throws SQLException {
        // Criar uma jornada para deletar
        Journey journeyToCreate = new Journey(
                3, // driverId
                LocalDate.now().minusDays(3), // journeyDate
                200, // totalDrivingTimeMinutes
                30,  // totalRestTimeMinutes
                ComplianceStatus.CONFORME.name(), // complianceStatus
                false // dailyLimitExceeded
        );
        int createdId = journeyDAO.create(journeyToCreate);

        boolean deleted = journeyDAO.delete(createdId);
        assertTrue(deleted);

        Optional<Journey> foundJourney = journeyDAO.findById(createdId);
        assertFalse(foundJourney.isPresent());
    }

    @Test
    @DisplayName("Não deve deletar jornada para um ID inexistente")
    void shouldNotDeleteJourneyForNonExistentId() throws SQLException {
        boolean deleted = journeyDAO.delete(9999);
        assertFalse(deleted);
    }

    @Test
    @DisplayName("Deve encontrar jornadas por ID do motorista")
    void shouldFindJourneysByDriverId() throws SQLException {
        int driverId = 4;
        journeyDAO.create(new Journey(driverId, LocalDate.now().minusDays(1), 100, 10, ComplianceStatus.CONFORME.name(), false));
        journeyDAO.create(new Journey(driverId, LocalDate.now(), 200, 20, ComplianceStatus.ALERTA.name(), true));

        List<Journey> journeys = journeyDAO.findByDriverId(driverId);

        assertNotNull(journeys);
        assertEquals(2, journeys.size());
        assertTrue(journeys.stream().allMatch(j -> j.getDriverId() == driverId));
    }

    @Test
    @DisplayName("Deve encontrar jornada por ID do motorista e data")
    void shouldFindJourneyByDriverIdAndDate() throws SQLException {
        int driverId = 5;
        LocalDate journeyDate = LocalDate.now().minusDays(5);
        journeyDAO.create(new Journey(driverId, journeyDate, 300, 30, ComplianceStatus.NAO_CONFORME.name(), true));

        Optional<Journey> foundJourney = journeyDAO.findByDriverIdAndDate(driverId, journeyDate);

        assertTrue(foundJourney.isPresent());
        assertEquals(driverId, foundJourney.get().getDriverId());
        assertEquals(journeyDate, foundJourney.get().getJourneyDate());
    }

    @Test
    @DisplayName("Não deve encontrar jornada por ID do motorista e data inexistente")
    void shouldNotFindJourneyByDriverIdAndNonExistentDate() throws SQLException {
        int driverId = 6;
        LocalDate journeyDate = LocalDate.now().minusDays(6);
        journeyDAO.create(new Journey(driverId, journeyDate, 300, 30, ComplianceStatus.NAO_CONFORME.name(), true));

        Optional<Journey> foundJourney = journeyDAO.findByDriverIdAndDate(driverId, LocalDate.now().minusDays(7));

        assertFalse(foundJourney.isPresent());
    }

    @Test
    @DisplayName("Deve encontrar jornadas por status")
    void shouldFindJourneysByStatus() throws SQLException {
        journeyDAO.create(new Journey(7, LocalDate.now().minusDays(1), 100, 10, ComplianceStatus.CONFORME.name(), false));
        journeyDAO.create(new Journey(8, LocalDate.now(), 200, 20, ComplianceStatus.CONFORME.name(), false));
        journeyDAO.create(new Journey(9, LocalDate.now(), 300, 30, ComplianceStatus.NAO_CONFORME.name(), true));

        List<Journey> conformeJourneys = journeyDAO.findByStatus(ComplianceStatus.CONFORME.name());

        assertNotNull(conformeJourneys);
        assertEquals(2, conformeJourneys.size());
        assertTrue(conformeJourneys.stream().allMatch(j -> j.getComplianceStatus().equals(ComplianceStatus.CONFORME.name())));
    }

    @Test
    @DisplayName("Deve encontrar jornadas por status e período de datas")
    void shouldFindJourneysByStatusAndDateRange() throws SQLException {
        LocalDate startDate = LocalDate.now().minusDays(10);
        LocalDate endDate = LocalDate.now().minusDays(5);

        journeyDAO.create(new Journey(10, LocalDate.now().minusDays(9), 100, 10, ComplianceStatus.CONFORME.name(), false));
        journeyDAO.create(new Journey(11, LocalDate.now().minusDays(7), 200, 20, ComplianceStatus.CONFORME.name(), false));
        journeyDAO.create(new Journey(12, LocalDate.now().minusDays(6), 300, 30, ComplianceStatus.NAO_CONFORME.name(), true));
        journeyDAO.create(new Journey(13, LocalDate.now().minusDays(4), 400, 40, ComplianceStatus.CONFORME.name(), false)); // Fora do range

        List<Journey> conformeJourneys = journeyDAO.findByStatusAndDateRange(ComplianceStatus.CONFORME.name(), startDate, endDate);

        assertNotNull(conformeJourneys);
        assertEquals(2, conformeJourneys.size());
        assertTrue(conformeJourneys.stream().allMatch(j -> j.getComplianceStatus().equals(ComplianceStatus.CONFORME.name()) &&
                !j.getJourneyDate().isBefore(startDate) && !j.getJourneyDate().isAfter(endDate)));
    }
}
