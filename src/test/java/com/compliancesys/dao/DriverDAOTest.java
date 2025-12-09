package com.compliancesys.dao;

import com.compliancesys.dao.impl.DriverDAOImpl;
import com.compliancesys.model.Driver;
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
 * Classe de teste para DriverDAO.
 * Utiliza um banco de dados em memória H2 para isolar os testes.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DriverDAOTest {

    private static DriverDAO driverDAO;
    private static Connection connection;

    @BeforeAll
    static void setUpBeforeAll() throws SQLException {
        connection = DatabaseConnection.getTestConnection();
        driverDAO = new DriverDAOImpl(connection);

        // Cria a tabela DRIVER no banco de dados H2
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
    }

    @AfterAll
    static void tearDownAfterAll() throws SQLException {
        // Limpa a tabela e fecha a conexão após todos os testes
        try (PreparedStatement stmt = connection.prepareStatement("DROP TABLE IF EXISTS DRIVER;")) {
            stmt.execute();
        }
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @BeforeEach
    void setUp() throws SQLException {
        // Limpa a tabela antes de cada teste para garantir isolamento
        try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM DRIVER; ALTER TABLE DRIVER ALTER COLUMN id RESTART WITH 1;")) {
            stmt.execute();
        }
    }

    @Test
    @Order(1)
    @DisplayName("1. Deve criar um novo motorista com sucesso")
    void testCreateDriverSuccess() throws SQLException {
        Driver driver = new Driver(0, "João Silva", "12345678901", "ABC12345678", "D", LocalDate.now().plusYears(5), "11987654321", "joao@email.com", LocalDateTime.now(), LocalDateTime.now());
        int id = driverDAO.create(driver);

        assertTrue(id > 0, "O ID do motorista deve ser maior que 0 após a criação.");
        driver.setId(id); // Define o ID para futuras verificações

        Optional<Driver> foundDriver = driverDAO.findById(id);
        assertTrue(foundDriver.isPresent(), "O motorista criado deve ser encontrado pelo ID.");
        assertEquals(driver.getName(), foundDriver.get().getName());
        assertEquals(driver.getCpf(), foundDriver.get().getCpf());
    }

    @Test
    @Order(2)
    @DisplayName("2. Não deve criar motorista com CPF duplicado")
    void testCreateDriverDuplicateCpf() throws SQLException {
        Driver driver1 = new Driver(0, "Maria Souza", "11122233344", "DEF98765432", "C", LocalDate.now().plusYears(3), "11911112222", "maria@email.com", LocalDateTime.now(), LocalDateTime.now());
        driverDAO.create(driver1);

        Driver driver2 = new Driver(0, "Pedro Santos", "11122233344", "GHI12312312", "B", LocalDate.now().plusYears(2), "11933334444", "pedro@email.com", LocalDateTime.now(), LocalDateTime.now());

        // Espera que uma SQLException seja lançada devido à restrição UNIQUE do CPF
        SQLException thrown = assertThrows(SQLException.class, () -> driverDAO.create(driver2),
                "Deve lançar SQLException ao tentar criar motorista com CPF duplicado.");
        assertTrue(thrown.getMessage().contains("UNIQUE constraint"), "A mensagem de erro deve indicar violação de constraint única.");
    }

    @Test
    @Order(3)
    @DisplayName("3. Deve encontrar motorista pelo ID")
    void testFindById() throws SQLException {
        Driver driver = new Driver(0, "Ana Paula", "22233344455", "JKL54321098", "A", LocalDate.now().plusYears(4), "11955556666", "ana@email.com", LocalDateTime.now(), LocalDateTime.now());
        int id = driverDAO.create(driver);

        Optional<Driver> foundDriver = driverDAO.findById(id);
        assertTrue(foundDriver.isPresent(), "Motorista deve ser encontrado pelo ID.");
        assertEquals(id, foundDriver.get().getId());
        assertEquals(driver.getName(), foundDriver.get().getName());
    }

    @Test
    @Order(4)
    @DisplayName("4. Deve retornar Optional vazio para ID não existente")
    void testFindByIdNotFound() throws SQLException {
        Optional<Driver> foundDriver = driverDAO.findById(9999); // ID que não existe
        assertFalse(foundDriver.isPresent(), "Não deve encontrar motorista para ID não existente.");
    }

    @Test
    @Order(5)
    @DisplayName("5. Deve encontrar motorista pelo CPF")
    void testFindByCpf() throws SQLException {
        Driver driver = new Driver(0, "Carlos Lima", "33344455566", "MNO98765432", "E", LocalDate.now().plusYears(6), "11977778888", "carlos@email.com", LocalDateTime.now(), LocalDateTime.now());
        driverDAO.create(driver);

        Optional<Driver> foundDriver = driverDAO.findByCpf("33344455566");
        assertTrue(foundDriver.isPresent(), "Motorista deve ser encontrado pelo CPF.");
        assertEquals(driver.getName(), foundDriver.get().getName());
        assertEquals(driver.getCpf(), foundDriver.get().getCpf());
    }

    @Test
    @Order(6)
    @DisplayName("6. Deve retornar Optional vazio para CPF não existente")
    void testFindByCpfNotFound() throws SQLException {
        Optional<Driver> foundDriver = driverDAO.findByCpf("99988877766"); // CPF que não existe
        assertFalse(foundDriver.isPresent(), "Não deve encontrar motorista para CPF não existente.");
    }

    @Test
    @Order(7)
    @DisplayName("7. Deve retornar todos os motoristas")
    void testFindAll() throws SQLException {
        driverDAO.create(new Driver(0, "Fernanda Costa", "44455566677", "PQR12345678", "D", LocalDate.now().plusYears(1), "11912345678", "fernanda@email.com", LocalDateTime.now(), LocalDateTime.now()));
        driverDAO.create(new Driver(0, "Gustavo Rocha", "55566677788", "STU98765432", "C", LocalDate.now().plusYears(2), "11987654321", "gustavo@email.com", LocalDateTime.now(), LocalDateTime.now()));

        List<Driver> drivers = driverDAO.findAll();
        assertNotNull(drivers, "A lista de motoristas não deve ser nula.");
        assertEquals(2, drivers.size(), "Deve retornar dois motoristas.");
    }

    @Test
    @Order(8)
    @DisplayName("8. Deve atualizar um motorista existente com sucesso")
    void testUpdateDriverSuccess() throws SQLException {
        Driver driver = new Driver(0, "Helena Pereira", "66677788899", "VWX11223344", "B", LocalDate.now().plusYears(3), "11911223344", "helena@email.com", LocalDateTime.now(), LocalDateTime.now());
        int id = driverDAO.create(driver);
        driver.setId(id);

        driver.setName("Helena Pereira Atualizada");
        driver.setPhone("11998877665");
        driver.setUpdatedAt(LocalDateTime.now()); // Simula a atualização da data

        boolean updated = driverDAO.update(driver);
        assertTrue(updated, "O motorista deve ser atualizado com sucesso.");

        Optional<Driver> foundDriver = driverDAO.findById(id);
        assertTrue(foundDriver.isPresent());
        assertEquals("Helena Pereira Atualizada", foundDriver.get().getName());
        assertEquals("11998877665", foundDriver.get().getPhone());
        // Verifica se a data de atualização foi realmente alterada (pode haver pequena diferença de milissegundos)
        assertTrue(foundDriver.get().getUpdatedAt().isAfter(driver.getCreatedAt()));
    }

    @Test
    @Order(9)
    @DisplayName("9. Não deve atualizar motorista com ID não existente")
    void testUpdateDriverNotFound() throws SQLException {
        Driver nonExistentDriver = new Driver(9999, "Motorista Falso", "77788899900", "YZA55667788", "A", LocalDate.now().plusYears(1), "11955667788", "fake@email.com", LocalDateTime.now(), LocalDateTime.now());
        boolean updated = driverDAO.update(nonExistentDriver);
        assertFalse(updated, "Não deve atualizar um motorista com ID não existente.");
    }

    @Test
    @Order(10)
    @DisplayName("10. Não deve atualizar motorista para um CPF já existente em outro motorista")
    void testUpdateDriverDuplicateCpfConflict() throws SQLException {
        Driver driver1 = new Driver(0, "Igor Almeida", "88899900011", "BCD11223344", "D", LocalDate.now().plusYears(2), "11911223344", "igor@email.com", LocalDateTime.now(), LocalDateTime.now());
        int id1 = driverDAO.create(driver1);
        driver1.setId(id1);

        Driver driver2 = new Driver(0, "Julia Martins", "00011122233", "EFG55667788", "C", LocalDate.now().plusYears(4), "11955667788", "julia@email.com", LocalDateTime.now(), LocalDateTime.now());
        int id2 = driverDAO.create(driver2);
        driver2.setId(id2);

        // Tenta atualizar driver2 para ter o CPF de driver1
        driver2.setCpf("88899900011");

        SQLException thrown = assertThrows(SQLException.class, () -> driverDAO.update(driver2),
                "Deve lançar SQLException ao tentar atualizar motorista com CPF duplicado de outro.");
        assertTrue(thrown.getMessage().contains("UNIQUE constraint"), "A mensagem de erro deve indicar violação de constraint única.");
    }

    @Test
    @Order(11)
    @DisplayName("11. Deve deletar um motorista existente com sucesso")
    void testDeleteDriverSuccess() throws SQLException {
        Driver driver = new Driver(0, "Luana Ribeiro", "11122233344", "HIJ99887766", "B", LocalDate.now().plusYears(5), "11999887766", "luana@email.com", LocalDateTime.now(), LocalDateTime.now());
        int id = driverDAO.create(driver);

        boolean deleted = driverDAO.delete(id);
        assertTrue(deleted, "O motorista deve ser deletado com sucesso.");

        Optional<Driver> foundDriver = driverDAO.findById(id);
        assertFalse(foundDriver.isPresent(), "O motorista deletado não deve ser encontrado.");
    }

    @Test
    @Order(12)
    @DisplayName("12. Não deve deletar motorista com ID não existente")
    void testDeleteDriverNotFound() throws SQLException {
        boolean deleted = driverDAO.delete(9999); // ID que não existe
        assertFalse(deleted, "Não deve deletar um motorista com ID não existente.");
    }
}
