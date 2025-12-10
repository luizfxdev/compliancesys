package com.compliancesys.dao;

import com.compliancesys.dao.impl.ComplianceAuditDAOImpl;
import com.compliancesys.dao.impl.DriverDAOImpl;
import com.compliancesys.dao.impl.JourneyDAOImpl;
import com.compliancesys.model.ComplianceAudit;
import com.compliancesys.model.Driver;
import com.compliancesys.model.Journey;
import com.compliancesys.model.enums.ComplianceStatus;
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

class ComplianceAuditDAOTest {

    private ComplianceAuditDAO complianceAuditDAO;
    private DriverDAO driverDAO;
    private JourneyDAO journeyDAO;

    private int testDriverId;
    private int testJourneyId;

    @BeforeEach
    void setUp() throws SQLException {
        complianceAuditDAO = new ComplianceAuditDAOImpl();
        driverDAO = new DriverDAOImpl();
        journeyDAO = new JourneyDAOImpl();
        clearDatabase(); // Limpa o banco antes de cada teste

        // Cria um driver e uma jornada para os testes de auditoria
        Driver testDriver = new Driver(1, "Driver Teste", "12345678901", "LIC123", "B", LocalDate.now().plusYears(1), LocalDate.of(1990, 1, 1), "11987654321", "driver@test.com");
        testDriverId = driverDAO.create(testDriver);

        Journey testJourney = new Journey(testDriverId, 1, LocalDate.now(), LocalDateTime.now(), LocalDateTime.now().plusHours(2), "IN_PROGRESS", "Origem", 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, "Destino", false);
        testJourneyId = journeyDAO.create(testJourney);
    }

    @AfterEach
    void tearDown() throws SQLException {
        clearDatabase(); // Limpa o banco após cada teste
    }

    private void clearDatabase() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM compliance_audits; DELETE FROM journeys; DELETE FROM drivers;")) {
            stmt.executeUpdate();
        }
    }

    @Test
    void testCreateAndFindById() throws SQLException {
        ComplianceAudit audit = new ComplianceAudit(testJourneyId, LocalDate.now(), "Auditor Teste", ComplianceStatus.CONFORME, "Notas de teste", LocalDateTime.now(), LocalDateTime.now());
        int id = complianceAuditDAO.create(audit);

        assertTrue(id > 0);
        audit.setId(id); // Atribui o ID gerado para comparação

        Optional<ComplianceAudit> foundAudit = complianceAuditDAO.findById(id);
        assertTrue(foundAudit.isPresent());
        assertEquals(audit.getJourneyId(), foundAudit.get().getJourneyId());
        assertEquals(audit.getAuditorName(), foundAudit.get().getAuditorName());
        assertEquals(audit.getComplianceStatus(), foundAudit.get().getComplianceStatus());
    }

    @Test
    void testUpdate() throws SQLException {
        ComplianceAudit audit = new ComplianceAudit(testJourneyId, LocalDate.now(), "Auditor Original", ComplianceStatus.NAO_CONFORME, "Notas originais", LocalDateTime.now(), LocalDateTime.now());
        int id = complianceAuditDAO.create(audit);
        audit.setId(id);

        audit.setAuditorName("Auditor Atualizado");
        audit.setComplianceStatus(ComplianceStatus.CONFORME);
        audit.setNotes("Notas atualizadas");
        audit.setUpdatedAt(LocalDateTime.now());

        boolean updated = complianceAuditDAO.update(audit);
        assertTrue(updated);

        Optional<ComplianceAudit> foundAudit = complianceAuditDAO.findById(id);
        assertTrue(foundAudit.isPresent());
        assertEquals("Auditor Atualizado", foundAudit.get().getAuditorName());
        assertEquals(ComplianceStatus.CONFORME, foundAudit.get().getComplianceStatus());
        assertEquals("Notas atualizadas", foundAudit.get().getNotes());
    }

    @Test
    void testDelete() throws SQLException {
        ComplianceAudit audit = new ComplianceAudit(testJourneyId, LocalDate.now(), "Auditor para Deletar", ComplianceStatus.PENDENTE, "Notas para deletar", LocalDateTime.now(), LocalDateTime.now());
        int id = complianceAuditDAO.create(audit);

        boolean deleted = complianceAuditDAO.delete(id);
        assertTrue(deleted);

        Optional<ComplianceAudit> foundAudit = complianceAuditDAO.findById(id);
        assertFalse(foundAudit.isPresent());
    }

    @Test
    void testFindAll() throws SQLException {
        complianceAuditDAO.create(new ComplianceAudit(testJourneyId, LocalDate.now(), "Auditor 1", ComplianceStatus.CONFORME, "Notas 1", LocalDateTime.now(), LocalDateTime.now()));
        complianceAuditDAO.create(new ComplianceAudit(testJourneyId, LocalDate.now(), "Auditor 2", ComplianceStatus.NAO_CONFORME, "Notas 2", LocalDateTime.now(), LocalDateTime.now()));

        List<ComplianceAudit> audits = complianceAuditDAO.findAll();
        assertNotNull(audits);
        assertEquals(2, audits.size());
    }

    @Test
    void testFindByJourneyId() throws SQLException {
        int otherJourneyId = journeyDAO.create(new Journey(testDriverId, 1, LocalDate.now(), LocalDateTime.now(), LocalDateTime.now().plusHours(3), "COMPLETED", "Outra Origem", 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, "Outro Destino", true));

        complianceAuditDAO.create(new ComplianceAudit(testJourneyId, LocalDate.now(), "Auditor J1", ComplianceStatus.CONFORME, "Notas J1", LocalDateTime.now(), LocalDateTime.now()));
        complianceAuditDAO.create(new ComplianceAudit(testJourneyId, LocalDate.now(), "Auditor J2", ComplianceStatus.NAO_CONFORME, "Notas J2", LocalDateTime.now(), LocalDateTime.now()));
        complianceAuditDAO.create(new ComplianceAudit(otherJourneyId, LocalDate.now(), "Auditor J3", ComplianceStatus.PENDENTE, "Notas J3", LocalDateTime.now(), LocalDateTime.now()));

        List<ComplianceAudit> audits = complianceAuditDAO.findByJourneyId(testJourneyId);
        assertNotNull(audits);
        assertEquals(2, audits.size());
        assertTrue(audits.stream().allMatch(a -> a.getJourneyId() == testJourneyId));
    }

    @Test
    void testFindByAuditorName() throws SQLException {
        complianceAuditDAO.create(new ComplianceAudit(testJourneyId, LocalDate.now(), "Auditor A", ComplianceStatus.CONFORME, "Notas A", LocalDateTime.now(), LocalDateTime.now()));
        complianceAuditDAO.create(new ComplianceAudit(testJourneyId, LocalDate.now(), "Auditor B", ComplianceStatus.NAO_CONFORME, "Notas B", LocalDateTime.now(), LocalDateTime.now()));
        complianceAuditDAO.create(new ComplianceAudit(testJourneyId, LocalDate.now(), "Auditor A", ComplianceStatus.PENDENTE, "Notas C", LocalDateTime.now(), LocalDateTime.now()));

        List<ComplianceAudit> audits = complianceAuditDAO.findByAuditorName("Auditor A");
        assertNotNull(audits);
        assertEquals(2, audits.size());
        assertTrue(audits.stream().allMatch(a -> a.getAuditorName().equals("Auditor A")));
    }

    @Test
    void testFindByComplianceStatus() throws SQLException {
        complianceAuditDAO.create(new ComplianceAudit(testJourneyId, LocalDate.now(), "Auditor S1", ComplianceStatus.CONFORME, "Notas S1", LocalDateTime.now(), LocalDateTime.now()));
        complianceAuditDAO.create(new ComplianceAudit(testJourneyId, LocalDate.now(), "Auditor S2", ComplianceStatus.NAO_CONFORME, "Notas S2", LocalDateTime.now(), LocalDateTime.now()));
        complianceAuditDAO.create(new ComplianceAudit(testJourneyId, LocalDate.now(), "Auditor S3", ComplianceStatus.CONFORME, "Notas S3", LocalDateTime.now(), LocalDateTime.now()));

        List<ComplianceAudit> audits = complianceAuditDAO.findByComplianceStatus(ComplianceStatus.CONFORME);
        assertNotNull(audits);
        assertEquals(2, audits.size());
        assertTrue(audits.stream().allMatch(a -> a.getComplianceStatus() == ComplianceStatus.CONFORME));
    }

    @Test
    void testFindByAuditDateRange() throws SQLException {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        LocalDate tomorrow = today.plusDays(1);

        complianceAuditDAO.create(new ComplianceAudit(testJourneyId, yesterday, "Auditor Y", ComplianceStatus.CONFORME, "Notas Y", LocalDateTime.now(), LocalDateTime.now()));
        complianceAuditDAO.create(new ComplianceAudit(testJourneyId, today, "Auditor T1", ComplianceStatus.NAO_CONFORME, "Notas T1", LocalDateTime.now(), LocalDateTime.now()));
        complianceAuditDAO.create(new ComplianceAudit(testJourneyId, today, "Auditor T2", ComplianceStatus.PENDENTE, "Notas T2", LocalDateTime.now(), LocalDateTime.now()));
        complianceAuditDAO.create(new ComplianceAudit(testJourneyId, tomorrow, "Auditor M", ComplianceStatus.CONFORME, "Notas M", LocalDateTime.now(), LocalDateTime.now()));

        List<ComplianceAudit> audits = complianceAuditDAO.findByAuditDateRange(yesterday, today);
        assertNotNull(audits);
        assertEquals(3, audits.size());
        assertTrue(audits.stream().allMatch(a -> !a.getAuditDate().toLocalDate().isAfter(today) && !a.getAuditDate().toLocalDate().isBefore(yesterday)));
    }
}
