package com.compliancesys.dao;

import com.compliancesys.config.DatabaseConfig;
import com.compliancesys.dao.impl.CompanyDAOImpl;
import com.compliancesys.dao.impl.DriverDAOImpl;
import com.compliancesys.dao.impl.JourneyDAOImpl;
import com.compliancesys.dao.impl.TimeRecordDAOImpl;
import com.compliancesys.dao.impl.VehicleDAOImpl;
import com.compliancesys.model.Company;
import com.compliancesys.model.Driver;
import com.compliancesys.model.Journey;
import com.compliancesys.model.TimeRecord;
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

class TimeRecordDAOTest {
    private Connection connection;
    private TimeRecordDAO timeRecordDAO;
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
        timeRecordDAO = new TimeRecordDAOImpl(connection);
        companyDAO = new CompanyDAOImpl(connection);
        driverDAO = new DriverDAOImpl(connection);
        vehicleDAO = new VehicleDAOImpl(connection);
        journeyDAO = new JourneyDAOImpl(connection);

        loadSchema(connection); // Carrega o schema do banco de dados
        clearTables(connection); // Limpa as tabelas antes de cada teste

        // Cria uma empresa, motorista, veículo e jornada para associar aos registros de tempo
        Company company = new Company(0, "Empresa TimeRecord", "11223344000188", "Rua da Empresa, 400", "1199887733", "empresa.timerecord@test.com", LocalDateTime.now(), LocalDateTime.now());
        companyId = companyDAO.create(company);
        assertTrue(companyId > 0, "A empresa deve ser criada com sucesso.");

        Driver driver = new Driver(0, companyId, "Motorista TimeRecord", "11122233344", "12345678901", "B",
                LocalDate.of(2028, 1, 1), LocalDate.of(1980, 1, 1), "motorista.timerecord@test.com", "11987654321",
                "Endereço Motorista", LocalDateTime.now(), LocalDateTime.now());
        driverId = driverDAO.create(driver);
        assertTrue(driverId > 0, "O motorista deve ser criado com sucesso.");

        Vehicle vehicle = new Vehicle(0, companyId, "ABC1234", "Caminhão Teste", 2020, LocalDateTime.now(), LocalDateTime.now());
        vehicleId = vehicleDAO.create(vehicle);
        assertTrue(vehicleId > 0, "O veículo deve ser criado com sucesso.");

        Journey journey = new Journey(0, driverId, vehicleId, companyId, "Origem TR", "Destino TR",
                LocalDateTime.now().minusHours(5), LocalDateTime.now(), "Em Andamento", LocalDateTime.now(), LocalDateTime.now());
        journeyId = journeyDAO.create(journey);
        assertTrue(journeyId > 0, "A jornada deve ser criada com sucesso.");
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
            stmt.execute("DELETE FROM time_records");
            stmt.execute("DELETE FROM journeys");
            stmt.execute("DELETE FROM drivers");
            stmt.execute("DELETE FROM vehicles");
            stmt.execute("DELETE FROM companies");
        }
    }

    @Test
    void testCreateTimeRecord() throws SQLException {
        TimeRecord record = new TimeRecord(0, journeyId, driverId, companyId, "START_DRIVING",
                LocalDateTime.now().minusHours(4), "Início da direção", LocalDateTime.now(), LocalDateTime.now());
        int id = timeRecordDAO.create(record);

        assertTrue(id > 0);
        Optional<TimeRecord> foundRecord = timeRecordDAO.findById(id);
        assertTrue(foundRecord.isPresent());
        assertEquals(journeyId, foundRecord.get().getJourneyId());
        assertEquals("START_DRIVING", foundRecord.get().getRecordType());
    }

    @Test
    void testFindById() throws SQLException {
        TimeRecord record = new TimeRecord(0, journeyId, driverId, companyId, "END_DRIVING",
                LocalDateTime.now().minusHours(2), "Fim da direção", LocalDateTime.now(), LocalDateTime.now());
        int id = timeRecordDAO.create(record);

        Optional<TimeRecord> foundRecord = timeRecordDAO.findById(id);
        assertTrue(foundRecord.isPresent());
        assertEquals(id, foundRecord.get().getId());
    }

    @Test
    void testFindAll() throws SQLException {
        timeRecordDAO.create(new TimeRecord(0, journeyId, driverId, companyId, "START_WORK",
                LocalDateTime.now().minusHours(8), "Início do trabalho", LocalDateTime.now(), LocalDateTime.now()));
        timeRecordDAO.create(new TimeRecord(0, journeyId, driverId, companyId, "END_WORK",
                LocalDateTime.now().minusHours(1), "Fim do trabalho", LocalDateTime.now(), LocalDateTime.now()));

        List<TimeRecord> records = timeRecordDAO.findAll();
        assertFalse(records.isEmpty());
        assertEquals(2, records.size());
    }

    @Test
    void testUpdateTimeRecord() throws SQLException {
        TimeRecord record = new TimeRecord(0, journeyId, driverId, companyId, "START_BREAK",
                LocalDateTime.now().minusHours(3), "Início do descanso", LocalDateTime.now(), LocalDateTime.now());
        int id = timeRecordDAO.create(record);

        record.setId(id);
        record.setRecordType("END_BREAK");
        record.setDescription("Fim do descanso atualizado");
        record.setRecordTime(LocalDateTime.now().minusHours(2));
        record.setUpdatedAt(LocalDateTime.now());

        boolean updated = timeRecordDAO.update(record);
        assertTrue(updated);

        Optional<TimeRecord> foundRecord = timeRecordDAO.findById(id);
        assertTrue(foundRecord.isPresent());
        assertEquals("END_BREAK", foundRecord.get().getRecordType());
        assertEquals("Fim do descanso atualizado", foundRecord.get().getDescription());
    }

    @Test
    void testDeleteTimeRecord() throws SQLException {
        TimeRecord record = new TimeRecord(0, journeyId, driverId, companyId, "OTHER",
                LocalDateTime.now().minusHours(1), "Registro para deletar", LocalDateTime.now(), LocalDateTime.now());
        int id = timeRecordDAO.create(record);

        boolean deleted = timeRecordDAO.delete(id);
        assertTrue(deleted);

        Optional<TimeRecord> foundRecord = timeRecordDAO.findById(id);
        assertFalse(foundRecord.isPresent());
    }

    @Test
    void testFindByJourneyId() throws SQLException {
        int anotherCompanyId = companyDAO.create(new Company(0, "Empresa TR2", "22334455000199", "Rua TR2", "1199887722", "tr2@test.com", LocalDateTime.now(), LocalDateTime.now()));
        int anotherDriverId = driverDAO.create(new Driver(0, anotherCompanyId, "Motorista TR2", "22233344455", "98765432109", "C", LocalDate.of(2029, 2, 2), LocalDate.of(1990, 2, 2), "tr2.driver@test.com", "11977776666", "End TR2", LocalDateTime.now(), LocalDateTime.now()));
        int anotherVehicleId = vehicleDAO.create(new Vehicle(0, anotherCompanyId, "XYZ5678", "Van TR2", 2022, LocalDateTime.now(), LocalDateTime.now()));
        int anotherJourneyId = journeyDAO.create(new Journey(0, anotherDriverId, anotherVehicleId, anotherCompanyId, "O2", "D2", LocalDateTime.now().minusHours(6), LocalDateTime.now(), "Concluída", LocalDateTime.now(), LocalDateTime.now()));

        timeRecordDAO.create(new TimeRecord(0, journeyId, driverId, companyId, "START_DRIVING",
                LocalDateTime.now().minusHours(4), "Início 1", LocalDateTime.now(), LocalDateTime.now()));
        timeRecordDAO.create(new TimeRecord(0, journeyId, driverId, companyId, "END_DRIVING",
                LocalDateTime.now().minusHours(3), "Fim 1", LocalDateTime.now(), LocalDateTime.now()));
        timeRecordDAO.create(new TimeRecord(0, anotherJourneyId, anotherDriverId, anotherCompanyId, "START_WORK",
                LocalDateTime.now().minusHours(5), "Início 2", LocalDateTime.now(), LocalDateTime.now()));

        List<TimeRecord> records = timeRecordDAO.findByJourneyId(journeyId);
        assertFalse(records.isEmpty());
        assertEquals(2, records.size());
        assertTrue(records.stream().allMatch(r -> r.getJourneyId() == journeyId));
    }

    @Test
    void testFindByDriverId() throws SQLException {
        int anotherCompanyId = companyDAO.create(new Company(0, "Empresa TR3", "33445566000100", "Rua TR3", "1199887711", "tr3@test.com", LocalDateTime.now(), LocalDateTime.now()));
        int anotherDriverId = driverDAO.create(new Driver(0, anotherCompanyId, "Motorista TR3", "33344455566", "10987654321", "D", LocalDate.of(2030, 3, 3), LocalDate.of(1991, 3, 3), "tr3.driver@test.com", "11966665555", "End TR3", LocalDateTime.now(), LocalDateTime.now()));
        int anotherVehicleId = vehicleDAO.create(new Vehicle(0, anotherCompanyId, "DEF9012", "Moto TR3", 2023, LocalDateTime.now(), LocalDateTime.now()));
        int anotherJourneyId = journeyDAO.create(new Journey(0, anotherDriverId, anotherVehicleId, anotherCompanyId, "O3", "D3", LocalDateTime.now().minusHours(7), LocalDateTime.now(), "Concluída", LocalDateTime.now(), LocalDateTime.now()));

        timeRecordDAO.create(new TimeRecord(0, journeyId, driverId, companyId, "START_BREAK",
                LocalDateTime.now().minusHours(2), "Início Break 1", LocalDateTime.now(), LocalDateTime.now()));
        timeRecordDAO.create(new TimeRecord(0, journeyId, driverId, companyId, "END_BREAK",
                LocalDateTime.now().minusHours(1), "Fim Break 1", LocalDateTime.now(), LocalDateTime.now()));
        timeRecordDAO.create(new TimeRecord(0, anotherJourneyId, anotherDriverId, anotherCompanyId, "START_DRIVING",
                LocalDateTime.now().minusHours(6), "Início Driving 2", LocalDateTime.now(), LocalDateTime.now()));

        List<TimeRecord> records = timeRecordDAO.findByDriverId(driverId);
        assertFalse(records.isEmpty());
        assertEquals(2, records.size());
        assertTrue(records.stream().allMatch(r -> r.getDriverId() == driverId));
    }

    @Test
    void testFindByRecordType() throws SQLException {
        String recordType = "START_DRIVING";
        timeRecordDAO.create(new TimeRecord(0, journeyId, driverId, companyId, recordType,
                LocalDateTime.now().minusHours(4), "Início da direção", LocalDateTime.now(), LocalDateTime.now()));
        timeRecordDAO.create(new TimeRecord(0, journeyId, driverId, companyId, "END_DRIVING",
                LocalDateTime.now().minusHours(3), "Fim da direção", LocalDateTime.now(), LocalDateTime.now()));

        List<TimeRecord> records = timeRecordDAO.findByRecordType(recordType);
        assertFalse(records.isEmpty());
        assertEquals(1, records.size());
        assertEquals(recordType, records.get(0).getRecordType());
    }

    @Test
    void testFindByRecordTimeBetween() throws SQLException {
        LocalDateTime startRange = LocalDateTime.now().minusHours(5);
        LocalDateTime endRange = LocalDateTime.now().minusHours(2);

        timeRecordDAO.create(new TimeRecord(0, journeyId, driverId, companyId, "START_WORK",
                LocalDateTime.now().minusHours(4), "Início do trabalho", LocalDateTime.now(), LocalDateTime.now()));
        timeRecordDAO.create(new TimeRecord(0, journeyId, driverId, companyId, "END_WORK",
                LocalDateTime.now().minusHours(1), "Fim do trabalho", LocalDateTime.now(), LocalDateTime.now())); // Fora do range

        List<TimeRecord> records = timeRecordDAO.findByRecordTimeBetween(startRange, endRange);
        assertFalse(records.isEmpty());
        assertEquals(1, records.size());
        assertTrue(records.get(0).getRecordTime().isAfter(startRange.minusMinutes(1)));
        assertTrue(records.get(0).getRecordTime().isBefore(endRange.plusMinutes(1)));
    }
}
