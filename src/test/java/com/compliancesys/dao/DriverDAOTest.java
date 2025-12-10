package com.compliancesys.dao;

import com.compliancesys.dao.impl.DriverDAOImpl;
import com.compliancesys.model.Driver;
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

class DriverDAOTest {

    private DriverDAO driverDAO;

    @BeforeEach
    void setUp() throws SQLException {
        driverDAO = new DriverDAOImpl();
        clearDatabase(); // Limpa o banco antes de cada teste
    }

    @AfterEach
    void tearDown() throws SQLException {
        clearDatabase(); // Limpa o banco após cada teste
    }

    private void clearDatabase() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM drivers")) {
            stmt.executeUpdate();
        }
    }

    @Test
    void testCreateAndFindById() throws SQLException {
        Driver driver = new Driver(1, "João Silva", "12345678901", "ABC12345678", "A", LocalDate.now().plusYears(5), LocalDate.of(1980, 1, 1), "11987654321", "joao@example.com");
        int id = driverDAO.create(driver);

        assertTrue(id > 0);
        driver.setId(id); // Atribui o ID gerado para comparação

        Optional<Driver> foundDriver = driverDAO.findById(id);
        assertTrue(foundDriver.isPresent());
        assertEquals(driver.getName(), foundDriver.get().getName());
        assertEquals(driver.getCpf(), foundDriver.get().getCpf());
    }

    @Test
    void testUpdate() throws SQLException {
        Driver driver = new Driver(1, "Maria Souza", "09876543210", "DEF87654321", "B", LocalDate.now().plusYears(3), LocalDate.of(1985, 2, 2), "11998765432", "maria@example.com");
        int id = driverDAO.create(driver);
        driver.setId(id);

        driver.setName("Maria Atualizada");
        driver.setEmail("maria.atualizada@example.com");
        driver.setUpdatedAt(LocalDateTime.now());

        boolean updated = driverDAO.update(driver);
        assertTrue(updated);

        Optional<Driver> foundDriver = driverDAO.findById(id);
        assertTrue(foundDriver.isPresent());
        assertEquals("Maria Atualizada", foundDriver.get().getName());
        assertEquals("maria.atualizada@example.com", foundDriver.get().getEmail());
    }

    @Test
    void testDelete() throws SQLException {
        Driver driver = new Driver(1, "Pedro Excluir", "11223344556", "GHI11223344", "C", LocalDate.now().plusYears(2), LocalDate.of(1990, 3, 3), "11911223344", "pedro@example.com");
        int id = driverDAO.create(driver);

        boolean deleted = driverDAO.delete(id);
        assertTrue(deleted);

        Optional<Driver> foundDriver = driverDAO.findById(id);
        assertFalse(foundDriver.isPresent());
    }

    @Test
    void testFindAll() throws SQLException {
        driverDAO.create(new Driver(1, "Ana", "11122233344", "JKL55667788", "D", LocalDate.now().plusYears(4), LocalDate.of(1992, 4, 4), "11955667788", "ana@example.com"));
        driverDAO.create(new Driver(1, "Bruno", "55566677788", "MNO99001122", "E", LocalDate.now().plusYears(1), LocalDate.of(1995, 5, 5), "11999001122", "bruno@example.com"));

        List<Driver> drivers = driverDAO.findAll();
        assertNotNull(drivers);
        assertEquals(2, drivers.size());
    }

    @Test
    void testFindByCpf() throws SQLException {
        String cpf = "88899900011";
        driverDAO.create(new Driver(1, "Igor Almeida", cpf, "BCD11223344", "D", LocalDate.now().plusYears(2), LocalDate.of(1990, 1, 1), "11911223344", "igor@email.com"));
        driverDAO.create(new Driver(1, "Julia Martins", "00011122233", "EFG55667788", "C", LocalDate.now().plusYears(4), LocalDate.of(1990, 1, 1), "11955667788", "julia@email.com"));

        Optional<Driver> foundDriver = driverDAO.findByCpf(cpf);
        assertTrue(foundDriver.isPresent());
        assertEquals(cpf, foundDriver.get().getCpf());
    }

    @Test
    void testFindByLicenseNumber() throws SQLException {
        String licenseNumber = "HIJ99887766";
        driverDAO.create(new Driver(1, "Luana Ribeiro", "11122233344", licenseNumber, "B", LocalDate.now().plusYears(5), LocalDate.of(1990, 1, 1), "11999887766", "luana@email.com"));
        driverDAO.create(new Driver(1, "Carlos Eduardo", "22233344455", "KLM11223344", "A", LocalDate.now().plusYears(6), LocalDate.of(1990, 1, 1), "11911223344", "carlos@email.com"));

        Optional<Driver> foundDriver = driverDAO.findByLicenseNumber(licenseNumber);
        assertTrue(foundDriver.isPresent());
        assertEquals(licenseNumber, foundDriver.get().getLicenseNumber());
    }
}
