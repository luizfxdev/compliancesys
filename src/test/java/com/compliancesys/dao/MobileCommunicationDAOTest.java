package com.compliancesys.dao;

import com.compliancesys.config.DatabaseConfig;
import com.compliancesys.dao.impl.CompanyDAOImpl;
import com.compliancesys.dao.impl.DriverDAOImpl;
import com.compliancesys.dao.impl.MobileCommunicationDAOImpl;
import com.compliancesys.model.Company;
import com.compliancesys.model.Driver;
import com.compliancesys.model.MobileCommunication;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class MobileCommunicationDAOTest {
    private Connection connection;
    private MobileCommunicationDAO mobileCommunicationDAO;
    private CompanyDAO companyDAO;
    private DriverDAO driverDAO;

    private int companyId;
    private int driverId;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DatabaseConfig.getInstance().getConnection();
        connection.setAutoCommit(false); // Inicia transação para rollback
        mobileCommunicationDAO = new MobileCommunicationDAOImpl(connection);
        companyDAO = new CompanyDAOImpl(connection);
        driverDAO = new DriverDAOImpl(connection);

        loadSchema(connection); // Carrega o schema do banco de dados
        clearTables(connection); // Limpa as tabelas antes de cada teste

        // Cria uma empresa e um motorista para associar às comunicações de teste
        Company company = new Company(0, "Empresa MobileComm", "11223344000177", "Rua da Empresa, 300", "1199887744", "empresa.mobile@test.com", LocalDateTime.now(), LocalDateTime.now());
        companyId = companyDAO.create(company);
        assertTrue(companyId > 0, "A empresa deve ser criada com sucesso.");

        Driver driver = new Driver(0, companyId, "Motorista Mobile", "11122233344", "12345678901", "B",
                LocalDate.of(2028, 1, 1), LocalDate.of(1980, 1, 1), "motorista.mobile@test.com", "11987654321",
                "Endereço do Motorista", LocalDateTime.now(), LocalDateTime.now());
        driverId = driverDAO.create(driver);
        assertTrue(driverId > 0, "O motorista deve ser criado com sucesso.");
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.rollback(); // Desfaz todas as operações do teste
        connection.close();
    }

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

    private void clearTables(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            // Limpa as tabelas na ordem correta devido a chaves estrangeiras
            stmt.execute("DELETE FROM mobile_communications");
            stmt.execute("DELETE FROM drivers");
            stmt.execute("DELETE FROM companies");
        }
    }

    @Test
    void testCreateMobileCommunication() throws SQLException {
        MobileCommunication comm = new MobileCommunication(0, driverId, companyId, "CALL",
                LocalDateTime.now().minusMinutes(10), LocalDateTime.now().minusMinutes(5), "11987654321",
                "11998877665", 300, "Localização A", LocalDateTime.now(), LocalDateTime.now());
        int id = mobileCommunicationDAO.create(comm);
        assertTrue(id > 0);

        Optional<MobileCommunication> foundComm = mobileCommunicationDAO.findById(id);
        assertTrue(foundComm.isPresent());
        assertEquals(comm.getCommunicationType(), foundComm.get().getCommunicationType());
        assertEquals(comm.getDriverId(), foundComm.get().getDriverId());
    }

    @Test
    void testFindById() throws SQLException {
        MobileCommunication comm = new MobileCommunication(0, driverId, companyId, "SMS",
                LocalDateTime.now().minusMinutes(20), LocalDateTime.now().minusMinutes(18), "11987654321",
                "11998877665", 10, "Localização B", LocalDateTime.now(), LocalDateTime.now());
        int id = mobileCommunicationDAO.create(comm);

        Optional<MobileCommunication> foundComm = mobileCommunicationDAO.findById(id);
        assertTrue(foundComm.isPresent());
        assertEquals(id, foundComm.get().getId());
    }

    @Test
    void testFindAll() throws SQLException {
        mobileCommunicationDAO.create(new MobileCommunication(0, driverId, companyId, "CALL",
                LocalDateTime.now().minusMinutes(10), LocalDateTime.now().minusMinutes(5), "11987654321",
                "11998877665", 300, "Localização A", LocalDateTime.now(), LocalDateTime.now()));
        mobileCommunicationDAO.create(new MobileCommunication(0, driverId, companyId, "DATA",
                LocalDateTime.now().minusMinutes(30), LocalDateTime.now().minusMinutes(25), "11987654321",
                "11998877665", 500, "Localização C", LocalDateTime.now(), LocalDateTime.now()));

        List<MobileCommunication> communications = mobileCommunicationDAO.findAll();
        assertFalse(communications.isEmpty());
        assertEquals(2, communications.size());
    }

    @Test
    void testUpdateMobileCommunication() throws SQLException {
        MobileCommunication comm = new MobileCommunication(0, driverId, companyId, "CALL",
                LocalDateTime.now().minusMinutes(10), LocalDateTime.now().minusMinutes(5), "11987654321",
                "11998877665", 300, "Localização A", LocalDateTime.now(), LocalDateTime.now());
        int id = mobileCommunicationDAO.create(comm);

        comm.setId(id);
        comm.setCommunicationType("DATA");
        comm.setDurationSeconds(600);
        comm.setUpdatedAt(LocalDateTime.now());

        boolean updated = mobileCommunicationDAO.update(comm);
        assertTrue(updated);

        Optional<MobileCommunication> foundComm = mobileCommunicationDAO.findById(id);
        assertTrue(foundComm.isPresent());
        assertEquals("DATA", foundComm.get().getCommunicationType());
        assertEquals(600, foundComm.get().getDurationSeconds());
    }

    @Test
    void testDeleteMobileCommunication() throws SQLException {
        MobileCommunication comm = new MobileCommunication(0, driverId, companyId, "SMS",
                LocalDateTime.now().minusMinutes(10), LocalDateTime.now().minusMinutes(5), "11987654321",
                "11998877665", 300, "Localização A", LocalDateTime.now(), LocalDateTime.now());
        int id = mobileCommunicationDAO.create(comm);

        boolean deleted = mobileCommunicationDAO.delete(id);
        assertTrue(deleted);

        Optional<MobileCommunication> foundComm = mobileCommunicationDAO.findById(id);
        assertFalse(foundComm.isPresent());
    }

    @Test
    void testFindByDriverId() throws SQLException {
        mobileCommunicationDAO.create(new MobileCommunication(0, driverId, companyId, "CALL",
                LocalDateTime.now().minusMinutes(10), LocalDateTime.now().minusMinutes(5), "11987654321",
                "11998877665", 300, "Localização A", LocalDateTime.now(), LocalDateTime.now()));
        mobileCommunicationDAO.create(new MobileCommunication(0, driverId, companyId, "SMS",
                LocalDateTime.now().minusMinutes(20), LocalDateTime.now().minusMinutes(15), "11987654321",
                "11998877665", 100, "Localização B", LocalDateTime.now(), LocalDateTime.now()));

        // Criar outro motorista e comunicação para garantir que a busca é isolada
        Company anotherCompany = new Company(0, "Outra Empresa", "99887766000100", "End Outro", "999", "outro@emp.com", LocalDateTime.now(), LocalDateTime.now());
        int anotherCompanyId = companyDAO.create(anotherCompany);
        Driver anotherDriver = new Driver(0, anotherCompanyId, "Outro Motorista", "55566677788", "98765432109", "C",
                LocalDate.of(2029, 1, 1), LocalDate.of(1985, 1, 1), "outro.motorista@test.com", "11900000000",
                "End Outro Motorista", LocalDateTime.now(), LocalDateTime.now());
        int anotherDriverId = driverDAO.create(anotherDriver);
        mobileCommunicationDAO.create(new MobileCommunication(0, anotherDriverId, anotherCompanyId, "DATA",
                LocalDateTime.now().minusMinutes(5), LocalDateTime.now().minusMinutes(2), "11900000000",
                "11911111111", 50, "Localização D", LocalDateTime.now(), LocalDateTime.now()));

        List<MobileCommunication> communications = mobileCommunicationDAO.findByDriverId(driverId);
        assertFalse(communications.isEmpty());
        assertEquals(2, communications.size());
        assertTrue(communications.stream().allMatch(c -> c.getDriverId() == driverId));
    }

    @Test
    void testFindByCommunicationType() throws SQLException {
        String type = "CALL";
        mobileCommunicationDAO.create(new MobileCommunication(0, driverId, companyId, type,
                LocalDateTime.now().minusMinutes(10), LocalDateTime.now().minusMinutes(5), "11987654321",
                "11998877665", 300, "Localização A", LocalDateTime.now(), LocalDateTime.now()));
        mobileCommunicationDAO.create(new MobileCommunication(0, driverId, companyId, "SMS",
                LocalDateTime.now().minusMinutes(20), LocalDateTime.now().minusMinutes(15), "11987654321",
                "11998877665", 100, "Localização B", LocalDateTime.now(), LocalDateTime.now()));

        List<MobileCommunication> communications = mobileCommunicationDAO.findByCommunicationType(type);
        assertFalse(communications.isEmpty());
        assertEquals(1, communications.size());
        assertEquals(type, communications.get(0).getCommunicationType());
    }

    @Test
    void testFindByStartTimeBetween() throws SQLException {
        LocalDateTime startRange = LocalDateTime.now().minusHours(1);
        LocalDateTime endRange = LocalDateTime.now();

        mobileCommunicationDAO.create(new MobileCommunication(0, driverId, companyId, "CALL",
                LocalDateTime.now().minusMinutes(30), LocalDateTime.now().minusMinutes(25), "11987654321",
                "11998877665", 300, "Localização A", LocalDateTime.now(), LocalDateTime.now()));
        mobileCommunicationDAO.create(new MobileCommunication(0, driverId, companyId, "SMS",
                LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1).minusMinutes(5), "11987654321",
                "11998877665", 100, "Localização B", LocalDateTime.now(), LocalDateTime.now())); // Fora do range

        List<MobileCommunication> communications = mobileCommunicationDAO.findByStartTimeBetween(startRange, endRange);
        assertFalse(communications.isEmpty());
        assertEquals(1, communications.size());
        assertTrue(communications.get(0).getStartTime().isAfter(startRange.minusMinutes(1)));
        assertTrue(communications.get(0).getStartTime().isBefore(endRange.plusMinutes(1)));
    }
}
