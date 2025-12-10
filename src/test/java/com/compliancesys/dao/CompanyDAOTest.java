package com.compliancesys.dao;

import com.compliancesys.config.DatabaseConfig;
import com.compliancesys.dao.impl.CompanyDAOImpl;
import com.compliancesys.model.Company;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CompanyDAOTest {

    private Connection connection;
    private CompanyDAO companyDAO;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DatabaseConfig.getInstance().getConnection();
        connection.setAutoCommit(false); // Inicia transação para rollback
        companyDAO = new CompanyDAOImpl(connection); // Injeta a conexão de teste
        // Limpa a tabela antes de cada teste, se necessário
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM companies");
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.rollback(); // Desfaz todas as operações do teste
        connection.close();
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
    }

    @Test
    void testFindById() throws SQLException {
        Company company = new Company(0, "Empresa Teste", "12345678000190", "Rua Teste, 123", "11987654321", "teste@empresa.com",
                LocalDateTime.now(), LocalDateTime.now());
        int id = companyDAO.create(company);

        Optional<Company> foundCompany = companyDAO.findById(id);

        assertTrue(foundCompany.isPresent());
        assertEquals(id, foundCompany.get().getId());
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
        company.setUpdatedAt(LocalDateTime.now());

        boolean updated = companyDAO.update(company);

        assertTrue(updated);
        Optional<Company> foundCompany = companyDAO.findById(id);
        assertTrue(foundCompany.isPresent());
        assertEquals("Empresa Nova", foundCompany.get().getName());
        assertEquals("888", foundCompany.get().getPhone());
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
}
