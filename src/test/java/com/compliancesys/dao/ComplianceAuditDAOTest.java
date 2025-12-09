package com.compliancesys.dao;

import com.compliancesys.dao.impl.ComplianceAuditDAOImpl;
import com.compliancesys.dao.impl.DriverDAOImpl; // Para criar um driver e jornada de teste
import com.compliancesys.dao.impl.JourneyDAOImpl; // Para criar uma jornada de teste
import com.compliancesys.model.ComplianceAudit;
import com.compliancesys.model.enums.ComplianceStatus;
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
 * Classe de teste para ComplianceAuditDAO.
 * Utiliza um banco de dados em memória H2 para isolar os testes.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ComplianceAuditDAOTest {

    private static ComplianceAuditDAO complianceAuditDAO;
    private static JourneyDAO journeyDAO; // Para gerenciar jornadas de teste
    private static DriverDAO driverDAO; // Para gerenciar motoristas de teste
    private static Connection connection;

    // IDs para entidades de teste
    private static int testDriverId;
    private static int testJourneyId;
    private static int testAuditId;

    @BeforeAll
    static void setUpBeforeAll() throws SQLException {
        connection = DatabaseConnection.getTestConnection();
        complianceAuditDAO = new ComplianceAuditDAOImpl(connection);
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
                "CREATE TABLE IF NOT EXISTS COMPLIANCE_AUDIT (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "journey_id INT NOT NULL," +
                        "audit_date DATE NOT NULL," +
                        "auditor_name VARCHAR(255) NOT NULL," +
                        "status VARCHAR(50) NOT NULL," + // Enum ComplianceStatus
                        "notes TEXT," +
                        "created_at TIMESTAMP NOT NULL," +
                        "updated_at TIMESTAMP NOT NULL," +
                        "FOREIGN KEY (journey_id) REFERENCES JOURNEY(id)" +
                        ");"
        )) {
            stmt.execute();
        }

        // Cria um motorista e uma jornada para serem usados nos testes de auditoria
        Driver driver = new Driver(0, "Motorista Teste", "11122233344", "12345678901", "B", LocalDate.now().plusYears(5), "999999999", "driver@test.com", LocalDateTime.now(), LocalDateTime.now());
        testDriverId = driverDAO.create(driver);

        Journey journey = new Journey(0, testDriverId, LocalDate.now(), "Origem Teste", "Destino Teste", 100.0, 2.0, LocalDateTime.now(), LocalDateTime.now());
        testJourneyId = journeyDAO.create(journey);
    }

    @AfterAll
    static void tearDownAfterAll() throws SQLException {
        // Limpa as tabelas e fecha a conexão após todos os testes
        try (PreparedStatement stmt = connection.prepareStatement("DROP TABLE IF EXISTS COMPLIANCE_AUDIT;")) {
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
        // Limpa a tabela de auditorias antes de cada teste para garantir isolamento
        try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM COMPLIANCE_AUDIT; ALTER TABLE COMPLIANCE_AUDIT ALTER COLUMN id RESTART WITH 1;")) {
            stmt.execute();
        }
    }

    @Test
    @Order(1)
    @DisplayName("1. Deve criar uma nova auditoria de conformidade com sucesso")
    void testCreateComplianceAuditSuccess() throws SQLException {
        ComplianceAudit audit = new ComplianceAudit(0, testJourneyId, LocalDate.now(), "Auditor A", ComplianceStatus.CONFORME, "Notas iniciais", LocalDateTime.now(), LocalDateTime.now());
        int id = complianceAuditDAO.create(audit);

        assertTrue(id > 0, "O ID da auditoria deve ser maior que 0 após a criação.");
        audit.setId(id);

        Optional<ComplianceAudit> foundAudit = complianceAuditDAO.findById(id);
        assertTrue(foundAudit.isPresent(), "A auditoria criada deve ser encontrada pelo ID.");
        assertEquals(audit.getAuditorName(), foundAudit.get().getAuditorName());
        assertEquals(audit.getStatus(), foundAudit.get().getStatus());
        testAuditId = id; // Guarda o ID para outros testes
    }

    @Test
    @Order(2)
    @DisplayName("2. Deve encontrar auditoria pelo ID")
    void testFindById() throws SQLException {
        ComplianceAudit audit = new ComplianceAudit(0, testJourneyId, LocalDate.now(), "Auditor B", ComplianceStatus.NAO_CONFORME, "Notas B", LocalDateTime.now(), LocalDateTime.now());
        int id = complianceAuditDAO.create(audit);

        Optional<ComplianceAudit> foundAudit = complianceAuditDAO.findById(id);
        assertTrue(foundAudit.isPresent(), "Auditoria deve ser encontrada pelo ID.");
        assertEquals(id, foundAudit.get().getId());
        assertEquals(audit.getAuditorName(), foundAudit.get().getAuditorName());
    }

    @Test
    @Order(3)
    @DisplayName("3. Deve retornar Optional vazio para ID não existente")
    void testFindByIdNotFound() throws SQLException {
        Optional<ComplianceAudit> foundAudit = complianceAuditDAO.findById(9999); // ID que não existe
        assertFalse(foundAudit.isPresent(), "Não deve encontrar auditoria para ID não existente.");
    }

    @Test
    @Order(4)
    @DisplayName("4. Deve retornar todas as auditorias")
    void testFindAll() throws SQLException {
        complianceAuditDAO.create(new ComplianceAudit(0, testJourneyId, LocalDate.now(), "Auditor 1", ComplianceStatus.CONFORME, "Notas 1", LocalDateTime.now(), LocalDateTime.now()));
        complianceAuditDAO.create(new ComplianceAudit(0, testJourneyId, LocalDate.now().minusDays(1), "Auditor 2", ComplianceStatus.PENDENTE, "Notas 2", LocalDateTime.now(), LocalDateTime.now()));

        List<ComplianceAudit> audits = complianceAuditDAO.findAll();
        assertNotNull(audits, "A lista de auditorias não deve ser nula.");
        assertEquals(2, audits.size(), "Deve retornar duas auditorias.");
    }

    @Test
    @Order(5)
    @DisplayName("5. Deve encontrar auditorias por ID de jornada")
    void testFindByJourneyId() throws SQLException {
        // Cria uma segunda jornada para testar
        Journey anotherJourney = new Journey(0, testDriverId, LocalDate.now().plusDays(1), "Outra Origem", "Outro Destino", 50.0, 1.0, LocalDateTime.now(), LocalDateTime.now());
        int anotherJourneyId = journeyDAO.create(anotherJourney);

        complianceAuditDAO.create(new ComplianceAudit(0, testJourneyId, LocalDate.now(), "Auditor J1", ComplianceStatus.CONFORME, "Notas J1", LocalDateTime.now(), LocalDateTime.now()));
        complianceAuditDAO.create(new ComplianceAudit(0, testJourneyId, LocalDate.now().minusDays(2), "Auditor J2", ComplianceStatus.NAO_CONFORME, "Notas J2", LocalDateTime.now(), LocalDateTime.now()));
        complianceAuditDAO.create(new ComplianceAudit(0, anotherJourneyId, LocalDate.now(), "Auditor J3", ComplianceStatus.PENDENTE, "Notas J3", LocalDateTime.now(), LocalDateTime.now()));

        List<ComplianceAudit> auditsForTestJourney = complianceAuditDAO.findByJourneyId(testJourneyId);
        assertNotNull(auditsForTestJourney);
        assertEquals(2, auditsForTestJourney.size());
        assertTrue(auditsForTestJourney.stream().allMatch(a -> a.getJourneyId() == testJourneyId));

        List<ComplianceAudit> auditsForAnotherJourney = complianceAuditDAO.findByJourneyId(anotherJourneyId);
        assertNotNull(auditsForAnotherJourney);
        assertEquals(1, auditsForAnotherJourney.size());
        assertTrue(auditsForAnotherJourney.stream().allMatch(a -> a.getJourneyId() == anotherJourneyId));
    }

    @Test
    @Order(6)
    @DisplayName("6. Deve encontrar auditorias por ID de motorista e período")
    void testFindByDriverIdAndDateRange() throws SQLException {
        // Cria mais jornadas para o mesmo motorista em datas diferentes
        Journey journeyDayBefore = new Journey(0, testDriverId, LocalDate.now().minusDays(1), "Origem -1", "Destino -1", 80.0, 1.5, LocalDateTime.now(), LocalDateTime.now());
        int journeyIdDayBefore = journeyDAO.create(journeyDayBefore);

        Journey journeyDayAfter = new Journey(0, testDriverId, LocalDate.now().plusDays(1), "Origem +1", "Destino +1", 120.0, 2.5, LocalDateTime.now(), LocalDateTime.now());
        int journeyIdDayAfter = journeyDAO.create(journeyDayAfter);

        // Auditorias para o motorista de teste
        complianceAuditDAO.create(new ComplianceAudit(0, journeyIdDayBefore, LocalDate.now().minusDays(1), "Auditor D1", ComplianceStatus.CONFORME, "Notas D1", LocalDateTime.now(), LocalDateTime.now()));
        complianceAuditDAO.create(new ComplianceAudit(0, testJourneyId, LocalDate.now(), "Auditor D2", ComplianceStatus.NAO_CONFORME, "Notas D2", LocalDateTime.now(), LocalDateTime.now()));
        complianceAuditDAO.create(new ComplianceAudit(0, journeyIdDayAfter, LocalDate.now().plusDays(1), "Auditor D3", ComplianceStatus.PENDENTE, "Notas D3", LocalDateTime.now(), LocalDateTime.now()));

        // Busca para o período de hoje
        List<ComplianceAudit> auditsToday = complianceAuditDAO.findByDriverIdAndDateRange(testDriverId, LocalDate.now(), LocalDate.now());
        assertNotNull(auditsToday);
        assertEquals(1, auditsToday.size());
        assertEquals(testJourneyId, auditsToday.get(0).getJourneyId());

        // Busca para um período mais amplo
        List<ComplianceAudit> auditsFullRange = complianceAuditDAO.findByDriverIdAndDateRange(testDriverId, LocalDate.now().minusDays(2), LocalDate.now().plusDays(2));
        assertNotNull(auditsFullRange);
        assertEquals(3, auditsFullRange.size());
    }

    @Test
    @Order(7)
    @DisplayName("7. Deve encontrar auditorias por período de data")
    void testFindByDateRange() throws SQLException {
        // Cria auditorias em diferentes datas
        complianceAuditDAO.create(new ComplianceAudit(0, testJourneyId, LocalDate.now().minusDays(5), "Auditor R1", ComplianceStatus.CONFORME, "Notas R1", LocalDateTime.now(), LocalDateTime.now()));
        complianceAuditDAO.create(new ComplianceAudit(0, testJourneyId, LocalDate.now().minusDays(2), "Auditor R2", ComplianceStatus.NAO_CONFORME, "Notas R2", LocalDateTime.now(), LocalDateTime.now()));
        complianceAuditDAO.create(new ComplianceAudit(0, testJourneyId, LocalDate.now(), "Auditor R3", ComplianceStatus.PENDENTE, "Notas R3", LocalDateTime.now(), LocalDateTime.now()));
        complianceAuditDAO.create(new ComplianceAudit(0, testJourneyId, LocalDate.now().plusDays(3), "Auditor R4", ComplianceStatus.CONFORME, "Notas R4", LocalDateTime.now(), LocalDateTime.now()));

        // Busca para um período específico
        List<ComplianceAudit> auditsInRange = complianceAuditDAO.findByDateRange(LocalDate.now().minusDays(3), LocalDate.now().plusDays(1));
        assertNotNull(auditsInRange);
        assertEquals(2, auditsInRange.size()); // R2 e R3
        assertTrue(auditsInRange.stream().anyMatch(a -> a.getAuditorName().equals("Auditor R2")));
        assertTrue(auditsInRange.stream().anyMatch(a -> a.getAuditorName().equals("Auditor R3")));
    }

    @Test
    @Order(8)
    @DisplayName("8. Deve atualizar uma auditoria existente com sucesso")
    void testUpdateComplianceAuditSuccess() throws SQLException {
        ComplianceAudit audit = new ComplianceAudit(0, testJourneyId, LocalDate.now(), "Auditor Antigo", ComplianceStatus.PENDENTE, "Notas antigas", LocalDateTime.now(), LocalDateTime.now());
        int id = complianceAuditDAO.create(audit);
        audit.setId(id);

        audit.setAuditorName("Auditor Novo");
        audit.setStatus(ComplianceStatus.CONFORME);
        audit.setNotes("Notas atualizadas e conformes.");
        audit.setUpdatedAt(LocalDateTime.now()); // Simula a atualização da data

        boolean updated = complianceAuditDAO.update(audit);
        assertTrue(updated, "A auditoria deve ser atualizada com sucesso.");

        Optional<ComplianceAudit> foundAudit = complianceAuditDAO.findById(id);
        assertTrue(foundAudit.isPresent());
        assertEquals("Auditor Novo", foundAudit.get().getAuditorName());
        assertEquals(ComplianceStatus.CONFORME, foundAudit.get().getStatus());
        assertEquals("Notas atualizadas e conformes.", foundAudit.get().getNotes());
        assertTrue(foundAudit.get().getUpdatedAt().isAfter(audit.getCreatedAt()));
    }

    @Test
    @Order(9)
    @DisplayName("9. Não deve atualizar auditoria com ID não existente")
    void testUpdateComplianceAuditNotFound() throws SQLException {
        ComplianceAudit nonExistentAudit = new ComplianceAudit(9999, testJourneyId, LocalDate.now(), "Auditor Falso", ComplianceStatus.NAO_CONFORME, "Notas falsas", LocalDateTime.now(), LocalDateTime.now());
        boolean updated = complianceAuditDAO.update(nonExistentAudit);
        assertFalse(updated, "Não deve atualizar uma auditoria com ID não existente.");
    }

    @Test
    @Order(10)
    @DisplayName("10. Deve deletar uma auditoria existente com sucesso")
    void testDeleteComplianceAuditSuccess() throws SQLException {
        ComplianceAudit audit = new ComplianceAudit(0, testJourneyId, LocalDate.now(), "Auditor para Deletar", ComplianceStatus.PENDENTE, "Notas para deletar", LocalDateTime.now(), LocalDateTime.now());
        int id = complianceAuditDAO.create(audit);

        boolean deleted = complianceAuditDAO.delete(id);
        assertTrue(deleted, "A auditoria deve ser deletada com sucesso.");

        Optional<ComplianceAudit> foundAudit = complianceAuditDAO.findById(id);
        assertFalse(foundAudit.isPresent(), "A auditoria deletada não deve ser encontrada.");
    }

    @Test
    @Order(11)
    @DisplayName("11. Não deve deletar auditoria com ID não existente")
    void testDeleteComplianceAuditNotFound() throws SQLException {
        boolean deleted = complianceAuditDAO.delete(9999); // ID que não existe
        assertFalse(deleted, "Não deve deletar uma auditoria com ID não existente.");
    }
}
