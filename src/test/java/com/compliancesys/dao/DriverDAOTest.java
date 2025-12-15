package com.compliancesys.dao;

import com.compliancesys.config.DatabaseConfig;
import com.compliancesys.dao.impl.CompanyDAOImpl;
import com.compliancesys.dao.impl.DriverDAOImpl;
import com.compliancesys.model.Company;
import com.compliancesys.model.Driver;
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

class DriverDAOTest {
    private Connection connection;
    private DriverDAO driverDAO;
    private CompanyDAO companyDAO; // Para criar empresas associadas aos motoristas
    private int companyId;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DatabaseConfig.getInstance().getConnection();
        connection.setAutoCommit(false); // Inicia transação para rollback
        driverDAO = new DriverDAOImpl(connection);
        companyDAO = new CompanyDAOImpl(connection);

        loadSchema(connection); // Carrega o schema do banco de dados
        clearTables(connection); // Limpa as tabelas antes de cada teste

        // Cria uma empresa para associar aos motoristas de teste
        Company company = new Company(0, "Empresa Teste Driver", "11223344000155", "Rua da Empresa, 100", "1199887766", "empresa.driver@test.com", LocalDateTime.now(), LocalDateTime.now());
        companyId = companyDAO.create(company);
        assertTrue(companyId > 0, "A empresa deve ser criada com sucesso.");
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
            stmt.execute("DELETE FROM drivers");
            stmt.execute("DELETE FROM companies"); // Limpa também as empresas
        }
    }

    @Test
    void testCreateDriver() throws SQLException {
        Driver driver = new Driver(0, companyId, "João Silva", "12345678901", "98765432101", "B",
                LocalDate.of(2028, 12, 31), LocalDate.of(1980, 5, 10), "joao.silva@test.com", "11999998888",
                "Rua A, 123", LocalDateTime.now(), LocalDateTime.now());
        int id = driverDAO.create(driver);
        assertTrue(id > 0);

        Optional<Driver> foundDriver = driverDAO.findById(id);
        assertTrue(foundDriver.isPresent());
        assertEquals(driver.getName(), foundDriver.get().getName());
        assertEquals(driver.getCpf(), foundDriver.get().getCpf());
    }

    @Test
    void testFindById() throws SQLException {
        Driver driver = new Driver(0, companyId, "Maria Souza", "10987654321", "12345678901", "C",
                LocalDate.of(2027, 11, 30), LocalDate.of(1985, 8, 15), "maria.souza@test.com", "11988887777",
                "Av. B, 456", LocalDateTime.now(), LocalDateTime.now());
        int id = driverDAO.create(driver);

        Optional<Driver> foundDriver = driverDAO.findById(id);
        assertTrue(foundDriver.isPresent());
        assertEquals(id, foundDriver.get().getId());
    }

    @Test
    void testFindAll() throws SQLException {
        driverDAO.create(new Driver(0, companyId, "Pedro Santos", "11122233344", "11111111111", "D",
                LocalDate.of(2026, 10, 29), LocalDate.of(1990, 1, 1), "pedro.santos@test.com", "11977776666",
                "Rua C, 789", LocalDateTime.now(), LocalDateTime.now()));
        driverDAO.create(new Driver(0, companyId, "Ana Costa", "55566677788", "22222222222", "E",
                LocalDate.of(2025, 9, 28), LocalDate.of(1995, 2, 2), "ana.costa@test.com", "11966665555",
                "Travessa D, 101", LocalDateTime.now(), LocalDateTime.now()));

        List<Driver> drivers = driverDAO.findAll();
        assertFalse(drivers.isEmpty());
        assertEquals(2, drivers.size());
    }

    @Test
    void testUpdateDriver() throws SQLException {
        Driver driver = new Driver(0, companyId, "Antônio Lima", "00011122233", "33333333333", "A",
                LocalDate.of(2029, 1, 1), LocalDate.of(1975, 3, 3), "antonio.lima@test.com", "11955554444",
                "Praça E, 202", LocalDateTime.now(), LocalDateTime.now());
        int id = driverDAO.create(driver);

        driver.setId(id);
        driver.setName("Antônio Lima Atualizado");
        driver.setLicenseCategory("AB");
        driver.setUpdatedAt(LocalDateTime.now());

        boolean updated = driverDAO.update(driver);
        assertTrue(updated);

        Optional<Driver> foundDriver = driverDAO.findById(id);
        assertTrue(foundDriver.isPresent());
        assertEquals("Antônio Lima Atualizado", foundDriver.get().getName());
        assertEquals("AB", foundDriver.get().getLicenseCategory());
        assertTrue(foundDriver.get().getUpdatedAt().isAfter(driver.getCreatedAt()));
    }

    @Test
    void testDeleteDriver() throws SQLException {
        Driver driver = new Driver(0, companyId, "Carla Rocha", "44455566677", "44444444444", "B",
                LocalDate.of(2024, 8, 1), LocalDate.of(1988, 4, 4), "carla.rocha@test.com", "11944443333",
                "Alameda F, 303", LocalDateTime.now(), LocalDateTime.now());
        int id = driverDAO.create(driver);

        boolean deleted = driverDAO.delete(id);
        assertTrue(deleted);

        Optional<Driver> foundDriver = driverDAO.findById(id);
        assertFalse(foundDriver.isPresent());
    }

    @Test
    void testFindByCpf() throws SQLException {
        String cpf = "77788899900";
        driverDAO.create(new Driver(0, companyId, "Lucas Mendes", cpf, "55555555555", "C",
                LocalDate.of(2026, 7, 15), LocalDate.of(1992, 5, 5), "lucas.mendes@test.com", "11933332222",
                "Rua G, 404", LocalDateTime.now(), LocalDateTime.now()));

        Optional<Driver> foundDriver = driverDAO.findByCpf(cpf);
        assertTrue(foundDriver.isPresent());
        assertEquals(cpf, foundDriver.get().getCpf());
    }

    @Test
    void testFindByLicenseNumber() throws SQLException {
        String licenseNumber = "66677788899";
        driverDAO.create(new Driver(0, companyId, "Fernanda Dias", "12312312312", licenseNumber, "D",
                LocalDate.of(2027, 6, 14), LocalDate.of(1993, 6, 6), "fernanda.dias@test.com", "11922221111",
                "Av. H, 505", LocalDateTime.now(), LocalDateTime.now()));

        Optional<Driver> foundDriver = driverDAO.findByLicenseNumber(licenseNumber);
        assertTrue(foundDriver.isPresent());
        assertEquals(licenseNumber, foundDriver.get().getLicenseNumber());
    }

    @Test
    void testFindByCompanyId() throws SQLException {
        // Já temos companyId do setUp
        driverDAO.create(new Driver(0, companyId, "Gabriel Rocha", "11111111111", "77777777777", "E",
                LocalDate.of(2028, 5, 13), LocalDate.of(1994, 7, 7), "gabriel.rocha@test.com", "11911110000",
                "Rua I, 606", LocalDateTime.now(), LocalDateTime.now()));
        driverDAO.create(new Driver(0, companyId, "Isabela Santos", "22222222222", "88888888888", "A",
                LocalDate.of(2029, 4, 12), LocalDate.of(1996, 8, 8), "isabela.santos@test.com", "11900009999",
                "Rua J, 707", LocalDateTime.now(), LocalDateTime.now()));

        // Cria outra empresa e um motorista para ela, para garantir que não seja retornado
        Company otherCompany = new Company(0, "Outra Empresa", "99887766000111", "Outro End", "111", "other@test.com", LocalDateTime.now(), LocalDateTime.now());
        int otherCompanyId = companyDAO.create(otherCompany);
        driverDAO.create(new Driver(0, otherCompanyId, "Outro Motorista", "33333333333", "99999999999", "B",
                LocalDate.of(2025, 3, 11), LocalDate.of(1997, 9, 9), "outro.motorista@test.com", "11999990000",
                "Rua K, 808", LocalDateTime.now(), LocalDateTime.now()));


        List<Driver> drivers = driverDAO.findByCompanyId(companyId);
        assertFalse(drivers.isEmpty());
        assertEquals(2, drivers.size());
        assertTrue(drivers.stream().allMatch(d -> d.getCompanyId() == companyId));
    }

    @Test
    void testFindByName() throws SQLException {
        String name = "Mariana Oliveira";
        driverDAO.create(new Driver(0, companyId, name, "33344455566", "10101010101", "C",
                LocalDate.of(2026, 2, 10), LocalDate.of(1998, 10, 10), "mariana.oliveira@test.com", "11988889999",
                "Rua L, 909", LocalDateTime.now(), LocalDateTime.now()));
        driverDAO.create(new Driver(0, companyId, "Mariana Silva", "33344455567", "10101010102", "C",
                LocalDate.of(2026, 2, 10), LocalDate.of(1998, 10, 10), "mariana.silva@test.com", "11988889998",
                "Rua L, 910", LocalDateTime.now(), LocalDateTime.now()));

        List<Driver> drivers = driverDAO.findByName("Mariana"); // Busca parcial
        assertFalse(drivers.isEmpty());
        assertEquals(2, drivers.size());
        assertTrue(drivers.stream().allMatch(d -> d.getName().contains("Mariana")));
    }

    @Test
    void testFindByLicenseCategory() throws SQLException {
        driverDAO.create(new Driver(0, companyId, "Rafael Pereira", "44455566677", "11223344556", "B",
                LocalDate.of(2027, 1, 9), LocalDate.of(1999, 11, 11), "rafael.pereira@test.com", "11977778888",
                "Rua M, 111", LocalDateTime.now(), LocalDateTime.now()));
        driverDAO.create(new Driver(0, companyId, "Sofia Martins", "55566677788", "11223344557", "B",
                LocalDate.of(2028, 12, 8), LocalDate.of(2000, 12, 12), "sofia.martins@test.com", "11966667777",
                "Rua N, 222", LocalDateTime.now(), LocalDateTime.now()));
        driverDAO.create(new Driver(0, companyId, "Bruno Alves", "66677788899", "11223344558", "C",
                LocalDate.of(2029, 11, 7), LocalDate.of(1980, 1, 1), "bruno.alves@test.com", "11955556666",
                "Rua O, 333", LocalDateTime.now(), LocalDateTime.now()));

        List<Driver> drivers = driverDAO.findByLicenseCategory("B");
        assertFalse(drivers.isEmpty());
        assertEquals(2, drivers.size());
        assertTrue(drivers.stream().allMatch(d -> d.getLicenseCategory().equals("B")));
    }

    @Test
    void testFindByLicenseExpirationBefore() throws SQLException {
        LocalDate expirationDate = LocalDate.now().plusMonths(6); // Expira nos próximos 6 meses

        driverDAO.create(new Driver(0, companyId, "Larissa Gomes", "77788899900", "11223344559", "A",
                LocalDate.now().plusMonths(3), LocalDate.of(1981, 2, 2), "larissa.gomes@test.com", "11944445555",
                "Rua P, 444", LocalDateTime.now(), LocalDateTime.now()));
        driverDAO.create(new Driver(0, companyId, "Diego Ferreira", "88899900011", "11223344560", "B",
                LocalDate.now().plusYears(1), LocalDate.of(1982, 3, 3), "diego.ferreira@test.com", "11933334444",
                "Rua Q, 555", LocalDateTime.now(), LocalDateTime.now())); // Não deve aparecer

        List<Driver> drivers = driverDAO.findByLicenseExpirationBefore(expirationDate);
        assertFalse(drivers.isEmpty());
        assertEquals(1, drivers.size());
        assertTrue(drivers.get(0).getLicenseExpirationDate().isBefore(expirationDate.plusDays(1)));
    }

    @Test
    void testFindByBirthDateBetween() throws SQLException {
        LocalDate startDate = LocalDate.of(1980, 1, 1);
        LocalDate endDate = LocalDate.of(1990, 12, 31);

        driverDAO.create(new Driver(0, companyId, "Guilherme Costa", "99900011122", "11223344561", "C",
                LocalDate.of(2025, 10, 1), LocalDate.of(1985, 4, 4), "guilherme.costa@test.com", "11922223333",
                "Rua R, 666", LocalDateTime.now(), LocalDateTime.now()));
        driverDAO.create(new Driver(0, companyId, "Helena Pires", "00011122233", "11223344562", "D",
                LocalDate.of(2026, 9, 2), LocalDate.of(1995, 5, 5), "helena.pires@test.com", "11911112222",
                "Rua S, 777", LocalDateTime.now(), LocalDateTime.now())); // Fora do range

        List<Driver> drivers = driverDAO.findByBirthDateBetween(startDate, endDate);
        assertFalse(drivers.isEmpty());
        assertEquals(1, drivers.size());
        assertTrue(drivers.get(0).getBirthDate().isAfter(startDate.minusDays(1)));
        assertTrue(drivers.get(0).getBirthDate().isBefore(endDate.plusDays(1)));
    }

    @Test
    void testFindByPhone() throws SQLException {
        String phone = "11987654321";
        driverDAO.create(new Driver(0, companyId, "Laura Almeida", "11122233344", "11223344563", "E",
                LocalDate.of(2027, 8, 3), LocalDate.of(1986, 6, 6), "laura.almeida@test.com", phone,
                "Rua T, 888", LocalDateTime.now(), LocalDateTime.now()));

        Optional<Driver> foundDriver = driverDAO.findByPhone(phone);
        assertTrue(foundDriver.isPresent());
        assertEquals(phone, foundDriver.get().getPhone());
    }

    @Test
    void testFindByEmail() throws SQLException {
        String email = "email.driver@test.com";
        driverDAO.create(new Driver(0, companyId, "Carlos Eduardo", "22233344455", "11223344564", "A",
                LocalDate.of(2028, 7, 4), LocalDate.of(1987, 7, 7), email, "11900001111",
                "Rua U, 999", LocalDateTime.now(), LocalDateTime.now()));

        Optional<Driver> foundDriver = driverDAO.findByEmail(email);
        assertTrue(foundDriver.isPresent());
        assertEquals(email, foundDriver.get().getEmail());
    }
}
