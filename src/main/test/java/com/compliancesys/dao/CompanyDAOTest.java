package com.compliancesys.dao;

import com.compliancesys.dao.impl.CompanyDAOImpl;
import com.compliancesys.model.Company;
import com.compliancesys.util.DatabaseConnection;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de teste para CompanyDAO.
 * Utiliza um banco de dados em memória H2 para isolar os testes.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CompanyDAOTest {

    private static CompanyDAO companyDAO;
    private static Connection connection;

    @BeforeAll
    static void setUpBeforeAll() throws SQLException {
        // Configura a conexão com o banco de dados H2 em memória para os testes
        // Certifique-se de que DatabaseConnection.getConnection() pode ser configurado para H2
        // Ou crie uma conexão H2 diretamente aqui para os testes.
        // Para este exemplo, vamos simular uma conexão H2.
        connection = DatabaseConnection.getTestConnection(); // Assumindo um método para conexão de teste
        companyDAO = new CompanyDAOImpl(connection); // Injeta a conexão de teste

        // Cria a tabela COMPANY no banco de dados H2
        try (PreparedStatement stmt = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS COMPANY (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "name VARCHAR(255) NOT NULL," +
                        "cnpj VARCHAR(14) NOT NULL UNIQUE," +
                        "address VARCHAR(255)," +
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
        try (PreparedStatement stmt = connection.prepareStatement("DROP TABLE IF EXISTS COMPANY;")) {
            stmt.execute();
        }
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @BeforeEach
    void setUp() throws SQLException {
        // Limpa a tabela antes de cada teste para garantir isolamento
        try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM COMPANY; ALTER TABLE COMPANY ALTER COLUMN id RESTART WITH 1;")) {
            stmt.execute();
        }
    }

    @Test
    @Order(1)
    @DisplayName("1. Deve criar uma nova empresa com sucesso")
    void testCreateCompanySuccess() throws SQLException {
        Company company = new Company(0, "Empresa Teste S.A.", "12345678000190", "Rua Teste, 123", "11987654321", "contato@empresa.com", LocalDateTime.now(), LocalDateTime.now());
        int id = companyDAO.create(company);

        assertTrue(id > 0, "O ID da empresa deve ser maior que 0 após a criação.");
        company.setId(id); // Define o ID para futuras verificações

        Optional<Company> foundCompany = companyDAO.findById(id);
        assertTrue(foundCompany.isPresent(), "A empresa criada deve ser encontrada pelo ID.");
        assertEquals(company.getName(), foundCompany.get().getName());
        assertEquals(company.getCnpj(), foundCompany.get().getCnpj());
    }

    @Test
    @Order(2)
    @DisplayName("2. Não deve criar empresa com CNPJ duplicado")
    void testCreateCompanyDuplicateCnpj() throws SQLException {
        Company company1 = new Company(0, "Empresa A", "11111111000111", "End A", "1", "a@a.com", LocalDateTime.now(), LocalDateTime.now());
        companyDAO.create(company1);

        Company company2 = new Company(0, "Empresa B", "11111111000111", "End B", "2", "b@b.com", LocalDateTime.now(), LocalDateTime.now());

        // Espera que uma SQLException seja lançada devido à restrição UNIQUE do CNPJ
        SQLException thrown = assertThrows(SQLException.class, () -> companyDAO.create(company2),
                "Deve lançar SQLException ao tentar criar empresa com CNPJ duplicado.");
        assertTrue(thrown.getMessage().contains("UNIQUE constraint"), "A mensagem de erro deve indicar violação de constraint única.");
    }

    @Test
    @Order(3)
    @DisplayName("3. Deve encontrar empresa pelo ID")
    void testFindById() throws SQLException {
        Company company = new Company(0, "Empresa Busca ID", "22222222000122", "Rua ID", "3", "id@id.com", LocalDateTime.now(), LocalDateTime.now());
        int id = companyDAO.create(company);

        Optional<Company> foundCompany = companyDAO.findById(id);
        assertTrue(foundCompany.isPresent(), "Empresa deve ser encontrada pelo ID.");
        assertEquals(id, foundCompany.get().getId());
        assertEquals(company.getName(), foundCompany.get().getName());
    }

    @Test
    @Order(4)
    @DisplayName("4. Deve retornar Optional vazio para ID não existente")
    void testFindByIdNotFound() throws SQLException {
        Optional<Company> foundCompany = companyDAO.findById(9999); // ID que não existe
        assertFalse(foundCompany.isPresent(), "Não deve encontrar empresa para ID não existente.");
    }

    @Test
    @Order(5)
    @DisplayName("5. Deve encontrar empresa pelo CNPJ")
    void testFindByCnpj() throws SQLException {
        Company company = new Company(0, "Empresa Busca CNPJ", "33333333000133", "Rua CNPJ", "4", "cnpj@cnpj.com", LocalDateTime.now(), LocalDateTime.now());
        companyDAO.create(company);

        Optional<Company> foundCompany = companyDAO.findByCnpj("33333333000133");
        assertTrue(foundCompany.isPresent(), "Empresa deve ser encontrada pelo CNPJ.");
        assertEquals(company.getName(), foundCompany.get().getName());
        assertEquals(company.getCnpj(), foundCompany.get().getCnpj());
    }

    @Test
    @Order(6)
    @DisplayName("6. Deve retornar Optional vazio para CNPJ não existente")
    void testFindByCnpjNotFound() throws SQLException {
        Optional<Company> foundCompany = companyDAO.findByCnpj("99999999000199"); // CNPJ que não existe
        assertFalse(foundCompany.isPresent(), "Não deve encontrar empresa para CNPJ não existente.");
    }

    @Test
    @Order(7)
    @DisplayName("7. Deve retornar todas as empresas")
    void testFindAll() throws SQLException {
        companyDAO.create(new Company(0, "Empresa 1", "44444444000144", "End 1", "5", "e1@e.com", LocalDateTime.now(), LocalDateTime.now()));
        companyDAO.create(new Company(0, "Empresa 2", "55555555000155", "End 2", "6", "e2@e.com", LocalDateTime.now(), LocalDateTime.now()));

        List<Company> companies = companyDAO.findAll();
        assertNotNull(companies, "A lista de empresas não deve ser nula.");
        assertEquals(2, companies.size(), "Deve retornar duas empresas.");
    }

    @Test
    @Order(8)
    @DisplayName("8. Deve atualizar uma empresa existente com sucesso")
    void testUpdateCompanySuccess() throws SQLException {
        Company company = new Company(0, "Empresa Original", "66666666000166", "End Original", "7", "orig@orig.com", LocalDateTime.now(), LocalDateTime.now());
        int id = companyDAO.create(company);
        company.setId(id);

        company.setName("Empresa Atualizada");
        company.setAddress("Novo Endereço, 456");
        company.setUpdatedAt(LocalDateTime.now()); // Simula a atualização da data

        boolean updated = companyDAO.update(company);
        assertTrue(updated, "A empresa deve ser atualizada com sucesso.");

        Optional<Company> foundCompany = companyDAO.findById(id);
        assertTrue(foundCompany.isPresent());
        assertEquals("Empresa Atualizada", foundCompany.get().getName());
        assertEquals("Novo Endereço, 456", foundCompany.get().getAddress());
        // Verifica se a data de atualização foi realmente alterada (pode haver pequena diferença de milissegundos)
        assertTrue(foundCompany.get().getUpdatedAt().isAfter(company.getCreatedAt()));
    }

    @Test
    @Order(9)
    @DisplayName("9. Não deve atualizar empresa com ID não existente")
    void testUpdateCompanyNotFound() throws SQLException {
        Company nonExistentCompany = new Company(9999, "Empresa Falsa", "77777777000177", "End Falso", "8", "fake@fake.com", LocalDateTime.now(), LocalDateTime.now());
        boolean updated = companyDAO.update(nonExistentCompany);
        assertFalse(updated, "Não deve atualizar uma empresa com ID não existente.");
    }

    @Test
    @Order(10)
    @DisplayName("10. Não deve atualizar empresa para um CNPJ já existente em outra empresa")
    void testUpdateCompanyDuplicateCnpjConflict() throws SQLException {
        Company company1 = new Company(0, "Empresa Um", "88888888000188", "End Um", "9", "um@um.com", LocalDateTime.now(), LocalDateTime.now());
        int id1 = companyDAO.create(company1);
        company1.setId(id1);

        Company company2 = new Company(0, "Empresa Dois", "99999999000199", "End Dois", "10", "dois@dois.com", LocalDateTime.now(), LocalDateTime.now());
        int id2 = companyDAO.create(company2);
        company2.setId(id2);

        // Tenta atualizar company2 para ter o CNPJ de company1
        company2.setCnpj("88888888000188");

        SQLException thrown = assertThrows(SQLException.class, () -> companyDAO.update(company2),
                "Deve lançar SQLException ao tentar atualizar empresa com CNPJ duplicado de outra.");
        assertTrue(thrown.getMessage().contains("UNIQUE constraint"), "A mensagem de erro deve indicar violação de constraint única.");
    }

    @Test
    @Order(11)
    @DisplayName("11. Deve deletar uma empresa existente com sucesso")
    void testDeleteCompanySuccess() throws SQLException {
        Company company = new Company(0, "Empresa para Deletar", "10101010000101", "End Deletar", "11", "del@del.com", LocalDateTime.now(), LocalDateTime.now());
        int id = companyDAO.create(company);

        boolean deleted = companyDAO.delete(id);
        assertTrue(deleted, "A empresa deve ser deletada com sucesso.");

        Optional<Company> foundCompany = companyDAO.findById(id);
        assertFalse(foundCompany.isPresent(), "A empresa deletada não deve ser encontrada.");
    }

    @Test
    @Order(12)
    @DisplayName("12. Não deve deletar empresa com ID não existente")
    void testDeleteCompanyNotFound() throws SQLException {
        boolean deleted = companyDAO.delete(9999); // ID que não existe
        assertFalse(deleted, "Não deve deletar uma empresa com ID não existente.");
    }
}
