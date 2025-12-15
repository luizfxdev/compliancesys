package com.compliancesys.dao;

import com.compliancesys.config.DatabaseConfig;
import com.compliancesys.dao.impl.ComplianceAuditDAOImpl;
import com.compliancesys.dao.impl.CompanyDAOImpl;
import com.compliancesys.dao.impl.DriverDAOImpl;
import com.compliancesys.dao.impl.JourneyDAOImpl;
import com.compliancesys.dao.impl.VehicleDAOImpl;
import com.compliancesys.model.Company;
import com.compliancesys.model.ComplianceAudit;
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

class ComplianceAuditDAOTest {
    private Connection connection;
    private ComplianceAuditDAO complianceAuditDAO;
    private CompanyDAO companyDAO;
    private DriverDAO driverDAO;
    private VehicleDAO vehicleDAO;
    private JourneyDAO journeyDAO;

    private int companyId;
    private int driverId;
    private int vehicleId;
    private int journeyId;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DatabaseConfig.getInstance().getConnection();
        connection.setAutoCommit(false); // Inicia transação para rollback

        // Carrega o schema do banco de dados para garantir que as tabelas existam
        loadSchema(connection);

        // Limpa as tabelas antes de cada teste para garantir isolamento
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM compliance_audits");
            stmt.execute("DELETE FROM journeys");
            stmt.execute("DELETE FROM drivers");
            stmt.execute("DELETE FROM vehicles");
            stmt.execute("DELETE FROM companies");
        }

        companyDAO = new CompanyDAOImpl(connection);
        driverDAO = new DriverDAOImpl(connection);
        vehicleDAO = new VehicleDAOImpl(connection);
        journeyDAO = new JourneyDAOImpl(connection);
        complianceAuditDAO = new ComplianceAuditDAOImpl(connection);

        // Cria dados de teste para chaves estrangeiras
        Company company = new Company(0, "Audit Company", "11111111000111", "Audit Address", "11111111111", "audit@company.com", LocalDateTime.now(), LocalDateTime.now());
        companyId = companyDAO.create(company);

        Driver driver = new Driver(0, companyId, "Audit Driver", "12345678901", "12345678901", LocalDate.of(2028, 1, 1), "B", LocalDate.of(1980, 5, 10), "audit.driver@example.com", "99999999999", "Rua Audit, 100", LocalDateTime.now(), LocalDateTime.now());
        driverId = driverDAO.create(driver);

        Vehicle vehicle = new Vehicle(0, "AUD1234", "Audit Model", 2020, companyId, LocalDateTime.now(), LocalDateTime.now());
        vehicleId = vehicleDAO.create(vehicle);

        Journey journey = new Journey(0, driverId, vehicleId, companyId, LocalDateTime.now().minusHours(5), LocalDateTime.now().minusHours(1), "Origem Audit", "Destino Audit", 100.0, "Concluída", LocalDateTime.now(), LocalDateTime.now());
        journeyId = journeyDAO.create(journey);
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

    @Test
    void testCreateComplianceAudit() throws SQLException {
        ComplianceAudit audit = new ComplianceAudit(0, journeyId, driverId, companyId,
                LocalDateTime.now().minusMinutes(30), LocalDateTime.now(),
                "VIOLATION_DRIVING_HOURS", "Motorista excedeu horas de direção contínua.",
                "Pendente", LocalDateTime.now(), LocalDateTime.now());
        int id = complianceAuditDAO.create(audit);
        assertTrue(id > 0);

        Optional<ComplianceAudit> foundAudit = complianceAuditDAO.findById(id);
        assertTrue(foundAudit.isPresent());
        assertEquals(audit.getViolationType(), foundAudit.get().getViolationType());
        assertEquals(audit.getDescription(), foundAudit.get().getDescription());
    }

    @Test
    void testFindById() throws SQLException {
        ComplianceAudit audit = new ComplianceAudit(0, journeyId, driverId, companyId,
                LocalDateTime.now().minusMinutes(30), LocalDateTime.now(),
                "VIOLATION_BREAK_TIME", "Motorista não fez pausa suficiente.",
                "Pendente", LocalDateTime.now(), LocalDateTime.now());
        int id = complianceAuditDAO.create(audit);

        Optional<ComplianceAudit> foundAudit = complianceAuditDAO.findById(id);
        assertTrue(foundAudit.isPresent());
        assertEquals(id, foundAudit.get().getId());
    }

    @Test
    void testFindAll() throws SQLException {
        complianceAuditDAO.create(new ComplianceAudit(0, journeyId, driverId, companyId, LocalDateTime.now().minusHours(1), LocalDateTime.now(), "TYPE1", "Desc1", "Status1", LocalDateTime.now(), LocalDateTime.now()));
        complianceAuditDAO.create(new ComplianceAudit(0, journeyId, driverId, companyId, LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1), "TYPE2", "Desc2", "Status2", LocalDateTime.now(), LocalDateTime.now()));

        List<ComplianceAudit> audits = complianceAuditDAO.findAll();
        assertFalse(audits.isEmpty());
        assertEquals(2, audits.size());
    }

    @Test
    void testUpdateComplianceAudit() throws SQLException {
        ComplianceAudit audit = new ComplianceAudit(0, journeyId, driverId, companyId,
                LocalDateTime.now().minusMinutes(30), LocalDateTime.now(),
                "VIOLATION_DRIVING_HOURS", "Motorista excedeu horas de direção contínua.",
                "Pendente", LocalDateTime.now(), LocalDateTime.now());
        int id = complianceAuditDAO.create(audit);

        audit.setId(id);
        audit.setStatus("Resolvido");
        audit.setUpdatedAt(LocalDateTime.now());

        boolean updated = complianceAuditDAO.update(audit);
        assertTrue(updated);

        Optional<ComplianceAudit> foundAudit = complianceAuditDAO.findById(id);
        assertTrue(foundAudit.isPresent());
        assertEquals("Resolvido", foundAudit.get().getStatus());
        assertTrue(foundAudit.get().getUpdatedAt().isAfter(audit.getCreatedAt()));
    }

    @Test
    void testDeleteComplianceAudit() throws SQLException {
        ComplianceAudit audit = new ComplianceAudit(0, journeyId, driverId, companyId,
                LocalDateTime.now().minusMinutes(30), LocalDateTime.now(),
                "VIOLATION_TEST", "Auditoria para deletar.",
                "Pendente", LocalDateTime.now(), LocalDateTime.now());
        int id = complianceAuditDAO.create(audit);

        boolean deleted = complianceAuditDAO.delete(id);
        assertTrue(deleted);

        Optional<ComplianceAudit> foundAudit = complianceAuditDAO.findById(id);
        assertFalse(foundAudit.isPresent());
    }

    @Test
    void testFindByJourneyId() throws SQLException {
        complianceAuditDAO.create(new ComplianceAudit(0, journeyId, driverId, companyId, LocalDateTime.now().minusHours(1), LocalDateTime.now(), "TYPE_J1", "Desc J1", "Status J1", LocalDateTime.now(), LocalDateTime.now()));
        complianceAuditDAO.create(new ComplianceAudit(0, journeyId, driverId, companyId, LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1), "TYPE_J2", "Desc J2", "Status J2", LocalDateTime.now(), LocalDateTime.now()));

        List<ComplianceAudit> audits = complianceAuditDAO.findByJourneyId(journeyId);
        assertFalse(audits.isEmpty());
        assertEquals(2, audits.size());
        assertTrue(audits.stream().allMatch(a -> a.getJourneyId() == journeyId));
    }

    @Test
    void testFindByDriverId() throws SQLException {
        complianceAuditDAO.create(new ComplianceAudit(0, journeyId, driverId, companyId, LocalDateTime.now().minusHours(1), LocalDateTime.now(), "TYPE_D1", "Desc D1", "Status D1", LocalDateTime.now(), LocalDateTime.now()));
        complianceAuditDAO.create(new ComplianceAudit(0, journeyId, driverId, companyId, LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1), "TYPE_D2", "Desc D2", "Status D2", LocalDateTime.now(), LocalDateTime.now()));

        List<ComplianceAudit> audits = complianceAuditDAO.findByDriverId(driverId);
        assertFalse(audits.isEmpty());
        assertEquals(2, audits.size());
        assertTrue(audits.stream().allMatch(a -> a.getDriverId() == driverId));
    }

    @Test
    void testFindByCompanyId() throws SQLException {
        complianceAuditDAO.create(new ComplianceAudit(0, journeyId, driverId, companyId, LocalDateTime.now().minusHours(1), LocalDateTime.now(), "TYPE_C1", "Desc C1", "Status C1", LocalDateTime.now(), LocalDateTime.now()));
        complianceAuditDAO.create(new ComplianceAudit(0, journeyId, driverId, companyId, LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1), "TYPE_C2", "Desc C2", "Status C2", LocalDateTime.now(), LocalDateTime.now()));

        List<ComplianceAudit> audits = complianceAuditDAO.findByCompanyId(companyId);
        assertFalse(audits.isEmpty());
        assertEquals(2, audits.size());
        assertTrue(audits.stream().allMatch(a -> a.getCompanyId() == companyId));
    }

    @Test
    void testFindByViolationType() throws SQLException {
        String violationType = "VIOLATION_DRIVING_HOURS";
        complianceAuditDAO.create(new ComplianceAudit(0, journeyId, driverId, companyId, LocalDateTime.now().minusHours(1), LocalDateTime.now(), violationType, "Desc V1", "Status V1", LocalDateTime.now(), LocalDateTime.now()));
        complianceAuditDAO.create(new ComplianceAudit(0, journeyId, driverId, companyId, LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1), "OTHER_TYPE", "Desc V2", "Status V2", LocalDateTime.now(), LocalDateTime.now()));

        List<ComplianceAudit> audits = complianceAuditDAO.findByViolationType(violationType);
        assertFalse(audits.isEmpty());
        assertEquals(1, audits.size());
        assertEquals(violationType, audits.get(0).getViolationType());
    }

    @Test
    void testFindByStatus() throws SQLException {
        String status = "Pendente";
        complianceAuditDAO.create(new ComplianceAudit(0, journeyId, driverId, companyId, LocalDateTime.now().minusHours(1), LocalDateTime.now(), "TYPE_S1", "Desc S1", status, LocalDateTime.now(), LocalDateTime.now()));
        complianceAuditDAO.create(new ComplianceAudit(0, journeyId, driverId, companyId, LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1), "TYPE_S2", "Desc S2", "Resolvido", LocalDateTime.now(), LocalDateTime.now()));

        List<ComplianceAudit> audits = complianceAuditDAO.findByStatus(status);
        assertFalse(audits.isEmpty());
        assertEquals(1, audits.size());
        assertEquals(status, audits.get(0).getStatus());
    }

    @Test
    void testFindByAuditDateBetween() throws SQLException {
        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(1);

        complianceAuditDAO.create(new ComplianceAudit(0, journeyId, driverId, companyId, LocalDateTime.now().minusHours(1), LocalDateTime.now(), "TYPE_D1", "Desc D1", "Status D1", LocalDateTime.now(), LocalDateTime.now()));
        complianceAuditDAO.create(new ComplianceAudit(0, journeyId, driverId, companyId, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(2), "TYPE_D2", "Desc D2", "Status D2", LocalDateTime.now(), LocalDateTime.now())); // Fora do range

        List<ComplianceAudit> audits = complianceAuditDAO.findByAuditDateBetween(startDate, endDate);
        assertFalse(audits.isEmpty());
        assertEquals(1, audits.size());
        assertTrue(audits.get(0).getAuditStart().toLocalDate().isAfter(startDate.minusDays(1)));
        assertTrue(audits.get(0).getAuditEnd().toLocalDate().isBefore(endDate.plusDays(1)));
    }
}
