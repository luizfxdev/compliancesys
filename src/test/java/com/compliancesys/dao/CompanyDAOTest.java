package com.compliancesys.dao;

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

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.compliancesys.config.DatabaseConfig;
import com.compliancesys.dao.impl.CompanyDAOImpl;
import com.compliancesys.model.Company;

class CompanyDAOTest {
    private Connection connection;
    private CompanyDAO companyDAO;

    @BeforeEach
    void setUp() throws SQLException {
        // Obter conexão do DatabaseConfig, que agora usa HikariCP e carrega as propriedades de teste
        connection = DatabaseConfig.getInstance().getConnection();
        connection.setAutoCommit(false); // Inicia transação para rollback
        companyDAO = new CompanyDAOImpl(connection); // Injeta a conexão de teste

        // Carrega o schema do banco de dados para garantir que as tabelas existam
        loadSchema(connection);
        // Limpa a tabela antes de cada teste para garantir isolamento
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM companies");
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.rollback(); // Desfaz todas as operações do teste
        connection.close(); // Retorna a conexão ao pool
        // O HikariCP gerencia o fechamento do pool, não precisamos chamar closeDataSource aqui
        // pois o pool pode ser reutilizado por outros testes.
        // Se fosse um banco de dados em memória que precisa ser destruído, seria diferente.
    }

    /**
     * Carrega e executa o script SQL do schema para o banco de dados de teste.
     * Assume que 'schema.sql' está na pasta 'src/main/resources'.
     */
    private void loadSchema(Connection conn) throws SQLException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("schema.sql");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            if (is == null) {
                throw new SQLException("Arquivo schema.sql não encontrado em src/main/resources.");
            }
            String schemaSql = reader.lines().collect(Collectors.joining("\n"));
            // Dividir o script em comandos individuais, se houver múltiplos (ex: CREATE TABLE; INSERT INTO;)
            // Isso é uma simplificação, para scripts mais complexos, pode ser necessário um parser SQL.
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
    void testCreateCompany() throws SQLException {
        Company company = new Company(0, "Empresa Teste", "12345678000190", "Rua Teste, 123", "11987654321", "teste@empresa.com",
                LocalDateTime.now(), LocalDateTime.now());
        int id = companyDAO.create(company);
        assertTrue(id > 0);
        Optional<Company> foundCompany = companyDAO.findById(id);
        assertTrue(foundCompany.isPresent());
        assertEquals(company.getName(), foundCompany.get().getName());
        assertEquals(company.getCnpj(), foundCompany.get().getCnpj());
    }

    @Test
    void testFindById() throws SQLException {
        Company company = new Company(0, "Empresa Teste Find", "12345678000191", "Rua Teste, 124", "11987654322", "teste2@empresa.com",
                LocalDateTime.now(), LocalDateTime.now());
        int id = companyDAO.create(company);
        Optional<Company> foundCompany = companyDAO.findById(id);
        assertTrue(foundCompany.isPresent());
        assertEquals(id, foundCompany.get().getId());
        assertEquals(company.getName(), foundCompany.get().getName());
    }

    @Test
    void testFindAll() throws SQLException {
        companyDAO.create(new Company(0, "Empresa 1", "11111111000111", "End 1", "111", "e1@e.com", LocalDateTime.now(), LocalDateTime.now()));
        companyDAO.create(new Company(0, "Empresa 2", "22222222000122", "End 2", "222", "e2@e.com", LocalDateTime.now(), LocalDateTime.now()));
        List<Company> companies = companyDAO.findAll();
        assertFalse(companies.isEmpty());
        assertEquals(2, companies.size());
    }

    @Test
    void testUpdateCompany() throws SQLException {
        Company company = new Company(0, "Empresa Antiga", "99999999000199", "End Antigo", "999", "antiga@e.com",
                LocalDateTime.now(), LocalDateTime.now());
        int id = companyDAO.create(company);
        company.setId(id);
        company.setName("Empresa Nova");
        company.setPhone("888");
        company.setUpdatedAt(LocalDateTime.now()); // Simula a atualização do timestamp

        boolean updated = companyDAO.update(company);
        assertTrue(updated);

        Optional<Company> foundCompany = companyDAO.findById(id);
        assertTrue(foundCompany.isPresent());
        assertEquals("Empresa Nova", foundCompany.get().getName());
        assertEquals("888", foundCompany.get().getPhone());
        // Verifica se o updated_at foi realmente atualizado (com uma pequena margem de erro)
        assertTrue(foundCompany.get().getUpdatedAt().isAfter(company.getCreatedAt()));
    }

    @Test
    void testDeleteCompany() throws SQLException {
        Company company = new Company(0, "Empresa para Deletar", "00000000000100", "End Deletar", "000", "del@e.com",
                LocalDateTime.now(), LocalDateTime.now());
        int id = companyDAO.create(company);
        boolean deleted = companyDAO.delete(id);
        assertTrue(deleted);
        Optional<Company> foundCompany = companyDAO.findById(id);
        assertFalse(foundCompany.isPresent());
    }

    @Test
    void testFindByCnpj() throws SQLException {
        String cnpj = "12345678000190";
        companyDAO.create(new Company(0, "Empresa CNPJ", cnpj, "Rua CNPJ", "111", "cnpj@e.com", LocalDateTime.now(), LocalDateTime.now()));
        Optional<Company> foundCompany = companyDAO.findByCnpj(cnpj);
        assertTrue(foundCompany.isPresent());
        assertEquals(cnpj, foundCompany.get().getCnpj());
    }

    @Test
    void testFindByName() throws SQLException {
        String name = "Empresa Nome Teste";
        companyDAO.create(new Company(0, name, "12345678000192", "Rua Nome", "112", "nome@e.com", LocalDateTime.now(), LocalDateTime.now()));
        Optional<Company> foundCompany = companyDAO.findByName(name);
        assertTrue(foundCompany.isPresent());
        assertEquals(name, foundCompany.get().getName());
    }

    @Test
    void testFindByEmail() throws SQLException {
        String email = "email@teste.com";
        companyDAO.create(new Company(0, "Empresa Email", "12345678000193", "Rua Email", "113", email, LocalDateTime.now(), LocalDateTime.now()));
        Optional<Company> foundCompany = companyDAO.findByEmail(email);
        assertTrue(foundCompany.isPresent());
        assertEquals(email, foundCompany.get().getEmail());
    }

    @Test
    void testFindByPhone() throws SQLException {
        String phone = "11987654300";
        companyDAO.create(new Company(0, "Empresa Telefone", "12345678000194", "Rua Telefone", phone, "phone@e.com", LocalDateTime.now(), LocalDateTime.now()));
        Optional<Company> foundCompany = companyDAO.findByPhone(phone);
        assertTrue(foundCompany.isPresent());
        assertEquals(phone, foundCompany.get().getPhone());
    }
}
