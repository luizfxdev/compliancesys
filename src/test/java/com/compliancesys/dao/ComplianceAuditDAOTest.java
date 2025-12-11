package com.compliancesys.dao;

import com.compliancesys.dao.impl.ComplianceAuditDAOImpl;
import com.compliancesys.dao.impl.DriverDAOImpl;
import com.compliancesys.dao.impl.JourneyDAOImpl;
import com.compliancesys.model.ComplianceAudit;
import com.compliancesys.model.Driver;
import com.compliancesys.model.Journey;
// import com.compliancesys.model.enums.ComplianceStatus; // Removido, pois ComplianceAudit agora usa String
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

class ComplianceAuditDAOTest {

    private ComplianceAuditDAO complianceAuditDAO;
    private DriverDAO driverDAO;
    private JourneyDAO journeyDAO;
    private Connection connection;

    private static final String DB_URL = "jdbc:h2:mem:testdb_compliance_audit;DB_CLOSE_DELAY=-1";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        createSchema(connection);
        complianceAuditDAO = new ComplianceAuditDAOImpl(connection);
        driverDAO = new DriverDAOImpl(connection);
        journeyDAO = new JourneyDAOImpl(connection);
    }

    private void createSchema(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS compliance_audits;");
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

            // Recriando a tabela compliance_audits (ALINHADO COM O SCHEMA.SQL FORNECIDO)
            stmt.execute("CREATE TABLE compliance_audits (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "journey_id INT NOT NULL," +
                    "audit_date TIMESTAMP NOT NULL," +
                    "status VARCHAR(50) NOT NULL," + // Usando 'status' como no schema.sql
                    "auditor_name VARCHAR(255)," +
                    "notes TEXT," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (journey_id) REFERENCES journeys(id)" +
                    ");");
        }
    }

    @Test
    void testCreateAndFindComplianceAudit() throws SQLException {
        // Driver agora precisa de companyId
        Driver testDriver = new Driver(1, 1, "Test Driver", "11122233344", "LIC123", "B", LocalDate.now().plusYears(1), LocalDate.of(1990, 1, 1), "11987654321", "test@driver.com");
        int testDriverId = driverDAO.create(testDriver);

        // Journey agora usa o novo construtor alinhado com o schema.sql
        Journey testJourney = new Journey(testDriverId, LocalDate.now(), 480, 60, "PENDING", false);
        int testJourneyId = journeyDAO.create(testJourney);

        // ComplianceAudit agora usa String para o status
        ComplianceAudit audit = new ComplianceAudit(testJourneyId, LocalDateTime.now(), "CONFORME", "Auditor A", "Notas do auditor");
        int auditId = complianceAuditDAO.create(audit);

        assertTrue(auditId > 0);

        Optional<ComplianceAudit> foundAudit = complianceAuditDAO.findById(auditId);
        assertTrue(foundAudit.isPresent());
        assertEquals(auditId, foundAudit.get().getId());
        assertEquals(testJourneyId, foundAudit.get().getJourneyId());
        assertEquals("CONFORME", foundAudit.get().getComplianceStatus()); // Comparando String
        assertEquals("Auditor A", foundAudit.get().getAuditorName());
        assertEquals("Notas do auditor", foundAudit.get().getNotes());
    }

    @Test
    void testFindAllComplianceAudits() throws SQLException {
        // Driver agora precisa de companyId
        Driver testDriver = new Driver(1, 1, "Test Driver", "11122233344", "LIC123", "B", LocalDate.now().plusYears(1), LocalDate.of(1990, 1, 1), "11987654321", "test@driver.com");
        int testDriverId = driverDAO.create(testDriver);

        // Journeys agora usam o novo construtor alinhado com o schema.sql
        Journey testJourney1 = new Journey(testDriverId, LocalDate.now(), 480, 60, "COMPLETED", false);
        int testJourneyId1 = journeyDAO.create(testJourney1);

        Journey testJourney2 = new Journey(testDriverId, LocalDate.now().plusDays(1), 500, 70, "COMPLETED", true);
        int testJourneyId2 = journeyDAO.create(testJourney2);

        // ComplianceAudit agora usa String para o status
        complianceAuditDAO.create(new ComplianceAudit(testJourneyId1, LocalDateTime.now(), "CONFORME", "Auditor A", "Notas A"));
        complianceAuditDAO.create(new ComplianceAudit(testJourneyId2, LocalDateTime.now().plusDays(1), "NAO_CONFORME", "Auditor B", "Notas B"));

        List<ComplianceAudit> audits = complianceAuditDAO.findAll();
        assertNotNull(audits);
        assertEquals(2, audits.size());
    }

    @Test
    void testUpdateComplianceAudit() throws SQLException {
        // Driver agora precisa de companyId
        Driver testDriver = new Driver(1, 1, "Test Driver", "11122233344", "LIC123", "B", LocalDate.now().plusYears(1), LocalDate.of(1990, 1, 1), "11987654321", "test@driver.com");
        int testDriverId = driverDAO.create(testDriver);

        // Journey agora usa o novo construtor alinhado com o schema.sql
        Journey testJourney = new Journey(testDriverId, LocalDate.now(), 480, 60, "COMPLETED", false);
        int testJourneyId = journeyDAO.create(testJourney);

        // ComplianceAudit agora usa String para o status
        ComplianceAudit audit = new ComplianceAudit(testJourneyId, LocalDateTime.now(), "PENDING", "Auditor X", "Notas X");
        int auditId = complianceAuditDAO.create(audit);

        Optional<ComplianceAudit> createdAudit = complianceAuditDAO.findById(auditId);
        assertTrue(createdAudit.isPresent());

        ComplianceAudit auditToUpdate = createdAudit.get();
        auditToUpdate.setComplianceStatus("CONFORME"); // Setando String
        auditToUpdate.setAuditorName("Auditor Y");
        auditToUpdate.setNotes("Notas Y atualizadas");

        boolean updated = complianceAuditDAO.update(auditToUpdate);
        assertTrue(updated);

        Optional<ComplianceAudit> foundUpdatedAudit = complianceAuditDAO.findById(auditId);
        assertTrue(foundUpdatedAudit.isPresent());
        assertEquals("CONFORME", foundUpdatedAudit.get().getComplianceStatus()); // Comparando String
        assertEquals("Auditor Y", foundUpdatedAudit.get().getAuditorName());
        assertEquals("Notas Y atualizadas", foundUpdatedAudit.get().getNotes());
    }

    @Test
    void testDeleteComplianceAudit() throws SQLException {
        // Driver agora precisa de companyId
        Driver testDriver = new Driver(1, 1, "Test Driver", "11122233344", "LIC123", "B", LocalDate.now().plusYears(1), LocalDate.of(1990, 1, 1), "11987654321", "test@driver.com");
        int testDriverId = driverDAO.create(testDriver);

        // Journey agora usa o novo construtor alinhado com o schema.sql
        Journey testJourney = new Journey(testDriverId, LocalDate.now(), 480, 60, "COMPLETED", false);
        int testJourneyId = journeyDAO.create(testJourney);

        // ComplianceAudit agora usa String para o status
        ComplianceAudit audit = new ComplianceAudit(testJourneyId, LocalDateTime.now(), "CONFORME", "Auditor Z", "Notas Z");
        int auditId = complianceAuditDAO.create(audit);

        assertTrue(complianceAuditDAO.findById(auditId).isPresent());

        boolean deleted = complianceAuditDAO.delete(auditId);
        assertTrue(deleted);

        assertFalse(complianceAuditDAO.findById(auditId).isPresent());
    }

    @Test
    void testFindByJourneyId() throws SQLException {
        // Driver agora precisa de companyId
        Driver testDriver = new Driver(1, 1, "Test Driver", "11122233344", "LIC123", "B", LocalDate.now().plusYears(1), LocalDate.of(1990, 1, 1), "11987654321", "test@driver.com");
        int testDriverId = driverDAO.create(testDriver);

        // Journeys agora usam o novo construtor alinhado com o schema.sql
        Journey testJourney1 = new Journey(testDriverId, LocalDate.now(), 480, 60, "COMPLETED", false);
        int testJourneyId1 = journeyDAO.create(testJourney1);

        Journey testJourney2 = new Journey(testDriverId, LocalDate.now().plusDays(1), 500, 70, "COMPLETED", true);
        int testJourneyId2 = journeyDAO.create(testJourney2);

        // ComplianceAudit agora usa String para o status
        complianceAuditDAO.create(new ComplianceAudit(testJourneyId1, LocalDateTime.now(), "CONFORME", "Auditor A", "Notas A"));
        complianceAuditDAO.create(new ComplianceAudit(testJourneyId1, LocalDateTime.now().plusHours(1), "NAO_CONFORME", "Auditor A", "Notas A2"));
        complianceAuditDAO.create(new ComplianceAudit(testJourneyId2, LocalDateTime.now().plusDays(1), "PENDING", "Auditor B", "Notas B"));

        List<ComplianceAudit> audits = complianceAuditDAO.findByJourneyId(testJourneyId1);
        assertNotNull(audits);
        assertEquals(2, audits.size());
        assertTrue(audits.stream().allMatch(a -> a.getJourneyId() == testJourneyId1));
    }
}
