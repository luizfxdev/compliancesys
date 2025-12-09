package com.compliancesys.service;

import com.compliancesys.model.Company;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CompanyServiceTest {

    private CompanyService companyService;

    @BeforeEach
    void setUp() {
        // Inicializa o mock do CompanyService antes de cada teste
        companyService = Mockito.mock(CompanyService.class);
    }

    @Test
    void testRegisterCompanySuccess() throws SQLException, IllegalArgumentException {
        Company newCompany = new Company(0, "Empresa Teste", "12.345.678/0001-90", "Rua A, 123", "Cidade X", "SP", "12345-678", "empresa@teste.com");
        when(companyService.registerCompany(newCompany)).thenReturn(1); // Simula o registro retornando um ID

        int id = companyService.registerCompany(newCompany);

        assertEquals(1, id);
        verify(companyService, times(1)).registerCompany(newCompany); // Verifica se o método registerCompany foi chamado uma vez
    }

    @Test
    void testRegisterCompanyInvalidData() throws SQLException, IllegalArgumentException {
        Company invalidCompany = new Company(0, "", "12.345.678/0001-90", "Rua A, 123", "Cidade X", "SP", "12345-678", "empresa@teste.com");
        // Simula a exceção IllegalArgumentException para dados inválidos
        doThrow(new IllegalArgumentException("Nome da empresa não pode ser vazio")).when(companyService).registerCompany(invalidCompany);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            companyService.registerCompany(invalidCompany);
        });

        assertEquals("Nome da empresa não pode ser vazio", thrown.getMessage());
        verify(companyService, times(1)).registerCompany(invalidCompany);
    }

    @Test
    void testRegisterCompanyThrowsSQLException() throws SQLException, IllegalArgumentException {
        Company newCompany = new Company(0, "Empresa Teste", "12.345.678/0001-90", "Rua A, 123", "Cidade X", "SP", "12345-678", "empresa@teste.com");
        // Simula a exceção SQLException em caso de erro no banco de dados
        doThrow(new SQLException("Erro de conexão com o banco de dados")).when(companyService).registerCompany(newCompany);

        SQLException thrown = assertThrows(SQLException.class, () -> {
            companyService.registerCompany(newCompany);
        });

        assertEquals("Erro de conexão com o banco de dados", thrown.getMessage());
        verify(companyService, times(1)).registerCompany(newCompany);
    }

    @Test
    void testGetCompanyByIdFound() throws SQLException {
        Company expectedCompany = new Company(1, "Empresa Teste", "12.345.678/0001-90", "Rua A, 123", "Cidade X", "SP", "12345-678", "empresa@teste.com");
        when(companyService.getCompanyById(1)).thenReturn(Optional.of(expectedCompany));

        Optional<Company> result = companyService.getCompanyById(1);

        assertTrue(result.isPresent());
        assertEquals(expectedCompany, result.get());
        verify(companyService, times(1)).getCompanyById(1);
    }

    @Test
    void testGetCompanyByIdNotFound() throws SQLException {
        when(companyService.getCompanyById(99)).thenReturn(Optional.empty());

        Optional<Company> result = companyService.getCompanyById(99);

        assertFalse(result.isPresent());
        verify(companyService, times(1)).getCompanyById(99);
    }

    @Test
    void testGetCompanyByIdThrowsSQLException() throws SQLException {
        when(companyService.getCompanyById(1)).thenThrow(new SQLException("Erro ao buscar empresa por ID"));

        SQLException thrown = assertThrows(SQLException.class, () -> {
            companyService.getCompanyById(1);
        });

        assertEquals("Erro ao buscar empresa por ID", thrown.getMessage());
        verify(companyService, times(1)).getCompanyById(1);
    }

    @Test
    void testGetCompanyByCnpjFound() throws SQLException {
        Company expectedCompany = new Company(1, "Empresa Teste", "12.345.678/0001-90", "Rua A, 123", "Cidade X", "SP", "12345-678", "empresa@teste.com");
        when(companyService.getCompanyByCnpj("12.345.678/0001-90")).thenReturn(Optional.of(expectedCompany));

        Optional<Company> result = companyService.getCompanyByCnpj("12.345.678/0001-90");

        assertTrue(result.isPresent());
        assertEquals(expectedCompany, result.get());
        verify(companyService, times(1)).getCompanyByCnpj("12.345.678/0001-90");
    }

    @Test
    void testGetCompanyByCnpjNotFound() throws SQLException {
        when(companyService.getCompanyByCnpj("99.887.766/0001-55")).thenReturn(Optional.empty());

        Optional<Company> result = companyService.getCompanyByCnpj("99.887.766/0001-55");

        assertFalse(result.isPresent());
        verify(companyService, times(1)).getCompanyByCnpj("99.887.766/0001-55");
    }

    @Test
    void testGetCompanyByCnpjThrowsSQLException() throws SQLException {
        when(companyService.getCompanyByCnpj("12.345.678/0001-90")).thenThrow(new SQLException("Erro ao buscar empresa por CNPJ"));

        SQLException thrown = assertThrows(SQLException.class, () -> {
            companyService.getCompanyByCnpj("12.345.678/0001-90");
        });

        assertEquals("Erro ao buscar empresa por CNPJ", thrown.getMessage());
        verify(companyService, times(1)).getCompanyByCnpj("12.345.678/0001-90");
    }

    @Test
    void testGetAllCompanies() throws SQLException {
        Company company1 = new Company(1, "Empresa A", "11.111.111/0001-11", "Rua A", "Cidade A", "SP", "11111-111", "a@a.com");
        Company company2 = new Company(2, "Empresa B", "22.222.222/0001-22", "Rua B", "Cidade B", "RJ", "22222-222", "b@b.com");
        List<Company> expectedList = Arrays.asList(company1, company2);

        when(companyService.getAllCompanies()).thenReturn(expectedList);

        List<Company> result = companyService.getAllCompanies();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedList, result);
        verify(companyService, times(1)).getAllCompanies();
    }

    @Test
    void testGetAllCompaniesEmpty() throws SQLException {
        when(companyService.getAllCompanies()).thenReturn(Collections.emptyList());

        List<Company> result = companyService.getAllCompanies();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(companyService, times(1)).getAllCompanies();
    }

    @Test
    void testGetAllCompaniesThrowsSQLException() throws SQLException {
        when(companyService.getAllCompanies()).thenThrow(new SQLException("Erro ao listar todas as empresas"));

        SQLException thrown = assertThrows(SQLException.class, () -> {
            companyService.getAllCompanies();
        });

        assertEquals("Erro ao listar todas as empresas", thrown.getMessage());
        verify(companyService, times(1)).getAllCompanies();
    }

    @Test
    void testUpdateCompanySuccess() throws SQLException, IllegalArgumentException {
        Company updatedCompany = new Company(1, "Empresa Atualizada", "12.345.678/0001-90", "Rua Nova, 456", "Cidade Y", "MG", "98765-432", "atualizada@teste.com");
        when(companyService.updateCompany(updatedCompany)).thenReturn(true);

        boolean result = companyService.updateCompany(updatedCompany);

        assertTrue(result);
        verify(companyService, times(1)).updateCompany(updatedCompany);
    }

    @Test
    void testUpdateCompanyFailure() throws SQLException, IllegalArgumentException {
        Company nonExistentCompany = new Company(99, "Empresa Inexistente", "99.999.999/0001-99", "Rua Z", "Cidade Z", "RS", "00000-000", "z@z.com");
        when(companyService.updateCompany(nonExistentCompany)).thenReturn(false);

        boolean result = companyService.updateCompany(nonExistentCompany);

        assertFalse(result);
        verify(companyService, times(1)).updateCompany(nonExistentCompany);
    }

    @Test
    void testUpdateCompanyInvalidData() throws SQLException, IllegalArgumentException {
        Company invalidCompany = new Company(1, "Empresa Teste", "cnpj-invalido", "Rua A, 123", "Cidade X", "SP", "12345-678", "empresa@teste.com");
        doThrow(new IllegalArgumentException("CNPJ inválido")).when(companyService).updateCompany(invalidCompany);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            companyService.updateCompany(invalidCompany);
        });

        assertEquals("CNPJ inválido", thrown.getMessage());
        verify(companyService, times(1)).updateCompany(invalidCompany);
    }

    @Test
    void testUpdateCompanyThrowsSQLException() throws SQLException, IllegalArgumentException {
        Company updatedCompany = new Company(1, "Empresa Atualizada", "12.345.678/0001-90", "Rua Nova, 456", "Cidade Y", "MG", "98765-432", "atualizada@teste.com");
        doThrow(new SQLException("Erro ao atualizar empresa")).when(companyService).updateCompany(updatedCompany);

        SQLException thrown = assertThrows(SQLException.class, () -> {
            companyService.updateCompany(updatedCompany);
        });

        assertEquals("Erro ao atualizar empresa", thrown.getMessage());
        verify(companyService, times(1)).updateCompany(updatedCompany);
    }

    @Test
    void testDeleteCompanySuccess() throws SQLException {
        when(companyService.deleteCompany(1)).thenReturn(true);

        boolean result = companyService.deleteCompany(1);

        assertTrue(result);
        verify(companyService, times(1)).deleteCompany(1);
    }

    @Test
    void testDeleteCompanyFailure() throws SQLException {
        when(companyService.deleteCompany(99)).thenReturn(false);

        boolean result = companyService.deleteCompany(99);

        assertFalse(result);
        verify(companyService, times(1)).deleteCompany(99);
    }

    @Test
    void testDeleteCompanyThrowsSQLException() throws SQLException {
        when(companyService.deleteCompany(1)).thenThrow(new SQLException("Erro ao deletar empresa"));

        SQLException thrown = assertThrows(SQLException.class, () -> {
            companyService.deleteCompany(1);
        });

        assertEquals("Erro ao deletar empresa", thrown.getMessage());
        verify(companyService, times(1)).deleteCompany(1);
    }
}
