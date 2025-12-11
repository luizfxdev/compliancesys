package com.compliancesys.service;

import com.compliancesys.dao.CompanyDAO;
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.Company;
import com.compliancesys.service.impl.CompanyServiceImpl;
import com.compliancesys.util.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class CompanyServiceTest {

    @Mock
    private CompanyDAO companyDAO;

    @Mock
    private Validator validator;

    @InjectMocks
    private CompanyServiceImpl companyService; // Assumindo que a implementação é CompanyServiceImpl

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Deve criar uma nova empresa com sucesso")
    void shouldCreateCompanySuccessfully() throws SQLException, BusinessException {
        Company newCompany = new Company("11.222.333/0001-44", "Empresa Teste LTDA", "Teste Company");
        Company companyWithId = new Company(1, "11.222.333/0001-44", "Empresa Teste LTDA", "Teste Company", LocalDateTime.now(), LocalDateTime.now());

        when(validator.isValidCnpj(anyString())).thenReturn(true);
        when(companyDAO.findByCnpj(anyString())).thenReturn(Optional.empty()); // Garante que o CNPJ não existe
        when(companyDAO.create(any(Company.class))).thenReturn(1); // Retorna o ID gerado

        Company createdCompany = companyService.createCompany(newCompany);

        assertNotNull(createdCompany);
        assertEquals(1, createdCompany.getId());
        assertEquals(newCompany.getCnpj(), createdCompany.getCnpj());
        verify(validator, times(1)).isValidCnpj(newCompany.getCnpj());
        verify(companyDAO, times(1)).findByCnpj(newCompany.getCnpj());
        verify(companyDAO, times(1)).create(any(Company.class));
    }

    @Test
    @DisplayName("Não deve criar empresa com CNPJ inválido")
    void shouldNotCreateCompanyWithInvalidCnpj() {
        Company newCompany = new Company("invalid-cnpj", "Empresa Teste LTDA", "Teste Company");

        when(validator.isValidCnpj(anyString())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                companyService.createCompany(newCompany));

        assertEquals("CNPJ inválido.", exception.getMessage());
        verify(validator, times(1)).isValidCnpj(newCompany.getCnpj());
        verify(companyDAO, never()).findByCnpj(anyString());
        verify(companyDAO, never()).create(any(Company.class));
    }

    @Test
    @DisplayName("Não deve criar empresa com CNPJ já existente")
    void shouldNotCreateCompanyWithExistingCnpj() throws SQLException {
        Company existingCompany = new Company(1, "11.222.333/0001-44", "Empresa Existente", "Existente", LocalDateTime.now(), LocalDateTime.now());
        Company newCompany = new Company("11.222.333/0001-44", "Empresa Teste LTDA", "Teste Company");

        when(validator.isValidCnpj(anyString())).thenReturn(true);
        when(companyDAO.findByCnpj(anyString())).thenReturn(Optional.of(existingCompany));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                companyService.createCompany(newCompany));

        assertEquals("CNPJ já cadastrado.", exception.getMessage());
        verify(validator, times(1)).isValidCnpj(newCompany.getCnpj());
        verify(companyDAO, times(1)).findByCnpj(newCompany.getCnpj());
        verify(companyDAO, never()).create(any(Company.class));
    }

    @Test
    @DisplayName("Deve retornar empresa por ID quando encontrada")
    void shouldReturnCompanyByIdWhenFound() throws SQLException, BusinessException {
        int companyId = 1;
        Company expectedCompany = new Company(companyId, "11.222.333/0001-44", "Empresa Teste LTDA", "Teste Company", LocalDateTime.now(), LocalDateTime.now());

        when(validator.isValidId(anyInt())).thenReturn(true);
        when(companyDAO.findById(companyId)).thenReturn(Optional.of(expectedCompany));

        Optional<Company> result = companyService.getCompanyById(companyId);

        assertTrue(result.isPresent());
        assertEquals(expectedCompany, result.get());
        verify(validator, times(1)).isValidId(companyId);
        verify(companyDAO, times(1)).findById(companyId);
    }

    @Test
    @DisplayName("Deve retornar Optional vazio por ID quando não encontrada")
    void shouldReturnEmptyOptionalByIdWhenNotFound() throws SQLException, BusinessException {
        int companyId = 99;

        when(validator.isValidId(anyInt())).thenReturn(true);
        when(companyDAO.findById(companyId)).thenReturn(Optional.empty());

        Optional<Company> result = companyService.getCompanyById(companyId);

        assertFalse(result.isPresent());
        verify(validator, times(1)).isValidId(companyId);
        verify(companyDAO, times(1)).findById(companyId);
    }

    @Test
    @DisplayName("Não deve buscar empresa com ID inválido")
    void shouldNotGetCompanyWithInvalidId() {
        int companyId = -1;

        when(validator.isValidId(anyInt())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                companyService.getCompanyById(companyId));

        assertEquals("ID da empresa inválido.", exception.getMessage());
        verify(validator, times(1)).isValidId(companyId);
        verify(companyDAO, never()).findById(anyInt());
    }

    @Test
    @DisplayName("Deve retornar todas as empresas")
    void shouldReturnAllCompanies() throws SQLException, BusinessException {
        List<Company> expectedCompanies = Arrays.asList(
                new Company(1, "11.222.333/0001-44", "Empresa A", "A", LocalDateTime.now(), LocalDateTime.now()),
                new Company(2, "22.333.444/0001-55", "Empresa B", "B", LocalDateTime.now(), LocalDateTime.now())
        );

        when(companyDAO.findAll()).thenReturn(expectedCompanies);

        List<Company> result = companyService.getAllCompanies();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedCompanies, result);
        verify(companyDAO, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve atualizar uma empresa existente com sucesso")
    void shouldUpdateExistingCompanySuccessfully() throws SQLException, BusinessException {
        Company existingCompany = new Company(1, "11.222.333/0001-44", "Empresa Antiga", "Antiga", LocalDateTime.now(), LocalDateTime.now());
        Company updatedCompany = new Company(1, "11.222.333/0001-44", "Empresa Nova", "Nova", LocalDateTime.now(), LocalDateTime.now());

        when(validator.isValidId(anyInt())).thenReturn(true);
        when(validator.isValidCnpj(anyString())).thenReturn(true);
        when(companyDAO.findById(anyInt())).thenReturn(Optional.of(existingCompany)); // Empresa existe
        when(companyDAO.findByCnpj(anyString())).thenReturn(Optional.of(existingCompany)); // CNPJ é da própria empresa
        when(companyDAO.update(any(Company.class))).thenReturn(true);

        boolean result = companyService.updateCompany(updatedCompany);

        assertTrue(result);
        verify(validator, times(1)).isValidId(updatedCompany.getId());
        verify(validator, times(1)).isValidCnpj(updatedCompany.getCnpj());
        verify(companyDAO, times(1)).findById(updatedCompany.getId());
        verify(companyDAO, times(1)).findByCnpj(updatedCompany.getCnpj());
        verify(companyDAO, times(1)).update(any(Company.class));
    }

    @Test
    @DisplayName("Não deve atualizar empresa com ID inválido")
    void shouldNotUpdateCompanyWithInvalidId() {
        Company updatedCompany = new Company(-1, "11.222.333/0001-44", "Empresa Nova", "Nova", LocalDateTime.now(), LocalDateTime.now());

        when(validator.isValidId(anyInt())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                companyService.updateCompany(updatedCompany));

        assertEquals("ID da empresa inválido.", exception.getMessage());
        verify(validator, times(1)).isValidId(updatedCompany.getId());
        verify(validator, never()).isValidCnpj(anyString());
        verify(companyDAO, never()).findById(anyInt());
        verify(companyDAO, never()).update(any(Company.class));
    }

    @Test
    @DisplayName("Não deve atualizar empresa com CNPJ inválido")
    void shouldNotUpdateCompanyWithInvalidCnpj() {
        Company updatedCompany = new Company(1, "invalid-cnpj", "Empresa Nova", "Nova", LocalDateTime.now(), LocalDateTime.now());

        when(validator.isValidId(anyInt())).thenReturn(true);
        when(validator.isValidCnpj(anyString())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                companyService.updateCompany(updatedCompany));

        assertEquals("CNPJ inválido.", exception.getMessage());
        verify(validator, times(1)).isValidId(updatedCompany.getId());
        verify(validator, times(1)).isValidCnpj(updatedCompany.getCnpj());
        verify(companyDAO, never()).findById(anyInt());
        verify(companyDAO, never()).update(any(Company.class));
    }

    @Test
    @DisplayName("Não deve atualizar empresa com CNPJ já existente em outra empresa")
    void shouldNotUpdateCompanyWithExistingCnpjInOtherCompany() throws SQLException {
        Company existingCompany1 = new Company(1, "11.222.333/0001-44", "Empresa 1", "Empresa 1", LocalDateTime.now(), LocalDateTime.now());
        Company existingCompany2 = new Company(2, "22.333.444/0001-55", "Empresa 2", "Empresa 2", LocalDateTime.now(), LocalDateTime.now());
        Company updatedCompany = new Company(1, "22.333.444/0001-55", "Empresa 1 Nova", "Empresa 1 Nova", LocalDateTime.now(), LocalDateTime.now()); // Tentando usar CNPJ da Empresa 2

        when(validator.isValidId(anyInt())).thenReturn(true);
        when(validator.isValidCnpj(anyString())).thenReturn(true);
        when(companyDAO.findById(1)).thenReturn(Optional.of(existingCompany1));
        when(companyDAO.findByCnpj("22.333.444/0001-55")).thenReturn(Optional.of(existingCompany2)); // Encontra outra empresa com o CNPJ

        BusinessException exception = assertThrows(BusinessException.class, () ->
                companyService.updateCompany(updatedCompany));

        assertEquals("CNPJ já cadastrado em outra empresa.", exception.getMessage());
        verify(validator, times(1)).isValidId(updatedCompany.getId());
        verify(validator, times(1)).isValidCnpj(updatedCompany.getCnpj());
        verify(companyDAO, times(1)).findById(updatedCompany.getId());
        verify(companyDAO, times(1)).findByCnpj(updatedCompany.getCnpj());
        verify(companyDAO, never()).update(any(Company.class));
    }

    @Test
    @DisplayName("Deve deletar uma empresa existente com sucesso")
    void shouldDeleteExistingCompanySuccessfully() throws SQLException, BusinessException {
        int companyId = 1;

        when(validator.isValidId(anyInt())).thenReturn(true);
        when(companyDAO.delete(companyId)).thenReturn(true);

        boolean result = companyService.deleteCompany(companyId);

        assertTrue(result);
        verify(validator, times(1)).isValidId(companyId);
        verify(companyDAO, times(1)).delete(companyId);
    }

    @Test
    @DisplayName("Não deve deletar empresa com ID inválido")
    void shouldNotDeleteCompanyWithInvalidId() {
        int companyId = -1;

        when(validator.isValidId(anyInt())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                companyService.deleteCompany(companyId));

        assertEquals("ID da empresa inválido.", exception.getMessage());
        verify(validator, times(1)).isValidId(companyId);
        verify(companyDAO, never()).delete(anyInt());
    }

    @Test
    @DisplayName("Deve lançar BusinessException em caso de erro de SQL na criação")
    void shouldThrowBusinessExceptionOnSqlErrorDuringCreate() throws SQLException {
        Company newCompany = new Company("11.222.333/0001-44", "Empresa Teste LTDA", "Teste Company");

        when(validator.isValidCnpj(anyString())).thenReturn(true);
        when(companyDAO.findByCnpj(anyString())).thenReturn(Optional.empty());
        when(companyDAO.create(any(Company.class))).thenThrow(new SQLException("Erro de DB na criação"));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                companyService.createCompany(newCompany));

        assertTrue(exception.getMessage().contains("Erro interno ao criar empresa."));
        verify(companyDAO, times(1)).create(any(Company.class));
    }

    @Test
    @DisplayName("Deve lançar BusinessException em caso de erro de SQL na busca por ID")
    void shouldThrowBusinessExceptionOnSqlErrorDuringGetById() throws SQLException {
        int companyId = 1;

        when(validator.isValidId(anyInt())).thenReturn(true);
        when(companyDAO.findById(anyInt())).thenThrow(new SQLException("Erro de DB na busca"));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                companyService.getCompanyById(companyId));

        assertTrue(exception.getMessage().contains("Erro interno ao buscar empresa por ID."));
        verify(companyDAO, times(1)).findById(companyId);
    }

    @Test
    @DisplayName("Deve lançar BusinessException em caso de erro de SQL na busca de todos")
    void shouldThrowBusinessExceptionOnSqlErrorDuringGetAll() throws SQLException {
        when(companyDAO.findAll()).thenThrow(new SQLException("Erro de DB na busca de todos"));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                companyService.getAllCompanies());

        assertTrue(exception.getMessage().contains("Erro interno ao buscar todas as empresas."));
        verify(companyDAO, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve lançar BusinessException em caso de erro de SQL na atualização")
    void shouldThrowBusinessExceptionOnSqlErrorDuringUpdate() throws SQLException {
        Company existingCompany = new Company(1, "11.222.333/0001-44", "Empresa Antiga", "Antiga", LocalDateTime.now(), LocalDateTime.now());
        Company updatedCompany = new Company(1, "11.222.333/0001-44", "Empresa Nova", "Nova", LocalDateTime.now(), LocalDateTime.now());

        when(validator.isValidId(anyInt())).thenReturn(true);
        when(validator.isValidCnpj(anyString())).thenReturn(true);
        when(companyDAO.findById(anyInt())).thenReturn(Optional.of(existingCompany));
        when(companyDAO.findByCnpj(anyString())).thenReturn(Optional.of(existingCompany));
        when(companyDAO.update(any(Company.class))).thenThrow(new SQLException("Erro de DB na atualização"));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                companyService.updateCompany(updatedCompany));

        assertTrue(exception.getMessage().contains("Erro interno ao atualizar empresa."));
        verify(companyDAO, times(1)).update(any(Company.class));
    }

    @Test
    @DisplayName("Deve lançar BusinessException em caso de erro de SQL na exclusão")
    void shouldThrowBusinessExceptionOnSqlErrorDuringDelete() throws SQLException {
        int companyId = 1;

        when(validator.isValidId(anyInt())).thenReturn(true);
        when(companyDAO.delete(companyId)).thenThrow(new SQLException("Erro de DB na exclusão"));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                companyService.deleteCompany(companyId));

        assertTrue(exception.getMessage().contains("Erro interno ao deletar empresa."));
        verify(companyDAO, times(1)).delete(companyId);
    }
}
