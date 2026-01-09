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
    private CompanyServiceImpl companyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Configurações padrão para o validator, para não precisar mockar em cada teste de sucesso
        when(validator.isValidId(anyInt())).thenReturn(true);
        when(validator.isValidCnpj(anyString())).thenReturn(true);
        when(validator.isValidName(anyString())).thenReturn(true);
        when(validator.isValidAddress(anyString())).thenReturn(true);
        when(validator.isValidPhone(anyString())).thenReturn(true);
        when(validator.isValidEmail(anyString())).thenReturn(true);
    }

    // Construtor auxiliar para criar Company de forma mais consistente nos testes
    private Company createTestCompany(int id, String cnpj, String name, String address, String phone, String email) {
        return new Company(id, name, cnpj, address, phone, email, LocalDateTime.now(), LocalDateTime.now());
    }

    private Company createTestCompany(String cnpj, String name, String address, String phone, String email) {
        return new Company(0, name, cnpj, address, phone, email, null, null); // ID 0 para novas empresas
    }

    @Test
    @DisplayName("Deve criar uma nova empresa com sucesso")
    void shouldCreateCompanySuccessfully() throws SQLException, BusinessException {
        Company newCompany = createTestCompany("11.222.333/0001-44", "Empresa Teste LTDA", "Rua A, 123", "11987654321", "teste@empresa.com");
        Company companyWithId = createTestCompany(1, "11.222.333/0001-44", "Empresa Teste LTDA", "Rua A, 123", "11987654321", "teste@empresa.com");

        when(companyDAO.findByCnpj(anyString())).thenReturn(Optional.empty());
        when(companyDAO.create(any(Company.class))).thenReturn(1);
        when(companyDAO.findById(1)).thenReturn(Optional.of(companyWithId)); // Para o service retornar o objeto completo

        Company createdCompany = companyService.createCompany(newCompany);

        assertNotNull(createdCompany);
        assertEquals(1, createdCompany.getId());
        assertEquals(newCompany.getCnpj(), createdCompany.getCnpj());
        verify(validator, times(1)).isValidCnpj(newCompany.getCnpj());
        verify(validator, times(1)).isValidName(newCompany.getName());
        verify(validator, times(1)).isValidAddress(newCompany.getAddress());
        verify(validator, times(1)).isValidPhone(newCompany.getPhone());
        verify(validator, times(1)).isValidEmail(newCompany.getEmail());
        verify(companyDAO, times(1)).findByCnpj(newCompany.getCnpj());
        verify(companyDAO, times(1)).create(any(Company.class));
        verify(companyDAO, times(1)).findById(1); // Verifica se buscou o objeto completo após a criação
    }

    @Test
    @DisplayName("Não deve criar empresa com CNPJ inválido")
    void shouldNotCreateCompanyWithInvalidCnpj() {
        Company newCompany = createTestCompany("invalid-cnpj", "Empresa Teste LTDA", "Rua A, 123", "11987654321", "teste@empresa.com");

        when(validator.isValidCnpj(anyString())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                companyService.createCompany(newCompany));

        assertEquals("CNPJ inválido.", exception.getMessage());
        verify(validator, times(1)).isValidCnpj(newCompany.getCnpj());
        verify(validator, never()).isValidName(anyString()); // Não deve validar outros campos se o CNPJ já falhou
        verify(companyDAO, never()).findByCnpj(anyString());
        verify(companyDAO, never()).create(any(Company.class));
    }

    @Test
    @DisplayName("Não deve criar empresa com nome inválido")
    void shouldNotCreateCompanyWithInvalidName() {
        Company newCompany = createTestCompany("11.222.333/0001-44", "", "Rua A, 123", "11987654321", "teste@empresa.com");

        when(validator.isValidName(anyString())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                companyService.createCompany(newCompany));

        assertEquals("Nome da empresa inválido.", exception.getMessage());
        verify(validator, times(1)).isValidCnpj(newCompany.getCnpj());
        verify(validator, times(1)).isValidName(newCompany.getName());
        verify(companyDAO, never()).findByCnpj(anyString());
        verify(companyDAO, never()).create(any(Company.class));
    }

    @Test
    @DisplayName("Não deve criar empresa com endereço inválido")
    void shouldNotCreateCompanyWithInvalidAddress() {
        Company newCompany = createTestCompany("11.222.333/0001-44", "Empresa Teste LTDA", "", "11987654321", "teste@empresa.com");

        when(validator.isValidAddress(anyString())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                companyService.createCompany(newCompany));

        assertEquals("Endereço da empresa inválido.", exception.getMessage());
        verify(validator, times(1)).isValidCnpj(newCompany.getCnpj());
        verify(validator, times(1)).isValidName(newCompany.getName());
        verify(validator, times(1)).isValidAddress(newCompany.getAddress());
        verify(companyDAO, never()).findByCnpj(anyString());
        verify(companyDAO, never()).create(any(Company.class));
    }

    @Test
    @DisplayName("Não deve criar empresa com telefone inválido")
    void shouldNotCreateCompanyWithInvalidPhone() {
        Company newCompany = createTestCompany("11.222.333/0001-44", "Empresa Teste LTDA", "Rua A, 123", "123", "teste@empresa.com");

        when(validator.isValidPhone(anyString())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                companyService.createCompany(newCompany));

        assertEquals("Telefone da empresa inválido.", exception.getMessage());
        verify(validator, times(1)).isValidCnpj(newCompany.getCnpj());
        verify(validator, times(1)).isValidName(newCompany.getName());
        verify(validator, times(1)).isValidAddress(newCompany.getAddress());
        verify(validator, times(1)).isValidPhone(newCompany.getPhone());
        verify(companyDAO, never()).findByCnpj(anyString());
        verify(companyDAO, never()).create(any(Company.class));
    }

    @Test
    @DisplayName("Não deve criar empresa com email inválido")
    void shouldNotCreateCompanyWithInvalidEmail() {
        Company newCompany = createTestCompany("11.222.333/0001-44", "Empresa Teste LTDA", "Rua A, 123", "11987654321", "invalid-email");

        when(validator.isValidEmail(anyString())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                companyService.createCompany(newCompany));

        assertEquals("Email da empresa inválido.", exception.getMessage());
        verify(validator, times(1)).isValidCnpj(newCompany.getCnpj());
        verify(validator, times(1)).isValidName(newCompany.getName());
        verify(validator, times(1)).isValidAddress(newCompany.getAddress());
        verify(validator, times(1)).isValidPhone(newCompany.getPhone());
        verify(validator, times(1)).isValidEmail(newCompany.getEmail());
        verify(companyDAO, never()).findByCnpj(anyString());
        verify(companyDAO, never()).create(any(Company.class));
    }

    @Test
    @DisplayName("Não deve criar empresa com CNPJ já existente")
    void shouldNotCreateCompanyWithExistingCnpj() throws SQLException {
        Company existingCompany = createTestCompany(1, "11.222.333/0001-44", "Empresa Existente", "Rua B, 456", "11998877665", "existente@empresa.com");
        Company newCompany = createTestCompany("11.222.333/0001-44", "Empresa Teste LTDA", "Rua A, 123", "11987654321", "teste@empresa.com");

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
        Company expectedCompany = createTestCompany(companyId, "11.222.333/0001-44", "Empresa Teste LTDA", "Rua A, 123", "11987654321", "teste@empresa.com");

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
                createTestCompany(1, "11.222.333/0001-44", "Empresa A", "Rua A", "111", "a@a.com"),
                createTestCompany(2, "22.333.444/0001-55", "Empresa B", "Rua B", "222", "b@b.com")
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
        Company existingCompany = createTestCompany(1, "11.222.333/0001-44", "Empresa Antiga", "End Antigo", "999", "antiga@e.com");
        Company updatedCompany = createTestCompany(1, "11.222.333/0001-44", "Empresa Nova", "End Novo", "888", "nova@e.com");

        when(companyDAO.findById(updatedCompany.getId())).thenReturn(Optional.of(existingCompany));
        when(companyDAO.findByCnpj(updatedCompany.getCnpj())).thenReturn(Optional.of(existingCompany)); // CNPJ é da própria empresa
        when(companyDAO.update(any(Company.class))).thenReturn(true);

        boolean result = companyService.updateCompany(updatedCompany);

        assertTrue(result);
        verify(validator, times(1)).isValidId(updatedCompany.getId());
        verify(validator, times(1)).isValidCnpj(updatedCompany.getCnpj());
        verify(validator, times(1)).isValidName(updatedCompany.getName());
        verify(validator, times(1)).isValidAddress(updatedCompany.getAddress());
        verify(validator, times(1)).isValidPhone(updatedCompany.getPhone());
        verify(validator, times(1)).isValidEmail(updatedCompany.getEmail());
        verify(companyDAO, times(1)).findById(updatedCompany.getId());
        verify(companyDAO, times(1)).findByCnpj(updatedCompany.getCnpj());
        verify(companyDAO, times(1)).update(any(Company.class));
    }

    @Test
    @DisplayName("Não deve atualizar empresa com ID inválido")
    void shouldNotUpdateCompanyWithInvalidId() {
        Company updatedCompany = createTestCompany(-1, "11.222.333/0001-44", "Empresa Nova", "End Novo", "888", "nova@e.com");

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
        Company updatedCompany = createTestCompany(1, "invalid-cnpj", "Empresa Nova", "End Novo", "888", "nova@e.com");

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
    @DisplayName("Não deve atualizar empresa com nome inválido")
    void shouldNotUpdateCompanyWithInvalidName() {
        Company updatedCompany = createTestCompany(1, "11.222.333/0001-44", "", "End Novo", "888", "nova@e.com");

        when(validator.isValidName(anyString())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                companyService.updateCompany(updatedCompany));

        assertEquals("Nome da empresa inválido.", exception.getMessage());
        verify(validator, times(1)).isValidId(updatedCompany.getId());
        verify(validator, times(1)).isValidCnpj(updatedCompany.getCnpj());
        verify(validator, times(1)).isValidName(updatedCompany.getName());
        verify(companyDAO, never()).findById(anyInt());
        verify(companyDAO, never()).update(any(Company.class));
    }

    @Test
    @DisplayName("Não deve atualizar empresa com CNPJ já existente em outra empresa")
    void shouldNotUpdateCompanyWithExistingCnpjInOtherCompany() throws SQLException {
        Company existingCompany1 = createTestCompany(1, "11.222.333/0001-44", "Empresa 1", "End 1", "111", "e1@e.com");
        Company existingCompany2 = createTestCompany(2, "22.333.444/0001-55", "Empresa 2", "End 2", "222", "e2@e.com");
        Company updatedCompany = createTestCompany(1, "22.333.444/0001-55", "Empresa 1 Nova", "End 1 Novo", "111", "e1nova@e.com"); // Tentando usar CNPJ da Empresa 2

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
    @DisplayName("Não deve atualizar empresa que não existe")
    void shouldNotUpdateNonExistentCompany() throws SQLException {
        Company updatedCompany = createTestCompany(99, "11.222.333/0001-44", "Empresa Nova", "End Novo", "888", "nova@e.com");

        when(companyDAO.findById(updatedCompany.getId())).thenReturn(Optional.empty()); // Empresa não encontrada

        BusinessException exception = assertThrows(BusinessException.class, () ->
                companyService.updateCompany(updatedCompany));

        assertEquals("Empresa não encontrada para atualização.", exception.getMessage());
        verify(validator, times(1)).isValidId(updatedCompany.getId());
        verify(validator, times(1)).isValidCnpj(updatedCompany.getCnpj());
        verify(companyDAO, times(1)).findById(updatedCompany.getId());
        verify(companyDAO, never()).findByCnpj(anyString()); // Não precisa verificar CNPJ se a empresa não existe
        verify(companyDAO, never()).update(any(Company.class));
    }

    @Test
    @DisplayName("Deve deletar uma empresa existente com sucesso")
    void shouldDeleteExistingCompanySuccessfully() throws SQLException, BusinessException {
        int companyId = 1;

        when(companyDAO.findById(companyId)).thenReturn(Optional.of(createTestCompany(companyId, "11.222.333/0001-44", "Empresa para Deletar", "End Del", "000", "del@e.com")));
        when(companyDAO.delete(companyId)).thenReturn(true);

        boolean result = companyService.deleteCompany(companyId);

        assertTrue(result);
        verify(validator, times(1)).isValidId(companyId);
        verify(companyDAO, times(1)).findById(companyId);
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
        verify(companyDAO, never()).findById(anyInt());
        verify(companyDAO, never()).delete(anyInt());
    }

    @Test
    @DisplayName("Não deve deletar empresa que não existe")
    void shouldNotDeleteNonExistentCompany() throws SQLException {
        int companyId = 99;

        when(companyDAO.findById(companyId)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                companyService.deleteCompany(companyId));

        assertEquals("Empresa não encontrada para exclusão.", exception.getMessage());
        verify(validator, times(1)).isValidId(companyId);
        verify(companyDAO, times(1)).findById(companyId);
        verify(companyDAO, never()).delete(anyInt());
    }

    @Test
    @DisplayName("Deve lançar BusinessException em caso de erro de SQL na criação")
    void shouldThrowBusinessExceptionOnSqlErrorDuringCreate() throws SQLException {
        Company newCompany = createTestCompany("11.222.333/0001-44", "Empresa Teste LTDA", "Rua A, 123", "11987654321", "teste@empresa.com");

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
        Company existingCompany = createTestCompany(1, "11.222.333/0001-44", "Empresa Antiga", "End Antigo", "999", "antiga@e.com");
        Company updatedCompany = createTestCompany(1, "11.222.333/0001-44", "Empresa Nova", "End Novo", "888", "nova@e.com");

        when(companyDAO.findById(updatedCompany.getId())).thenReturn(Optional.of(existingCompany));
        when(companyDAO.findByCnpj(updatedCompany.getCnpj())).thenReturn(Optional.of(existingCompany));
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

        when(companyDAO.findById(companyId)).thenReturn(Optional.of(createTestCompany(companyId, "11.222.333/0001-44", "Empresa para Deletar", "End Del", "000", "del@e.com")));
        when(companyDAO.delete(companyId)).thenThrow(new SQLException("Erro de DB na exclusão"));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                companyService.deleteCompany(companyId));

        assertTrue(exception.getMessage().contains("Erro interno ao deletar empresa."));
        verify(companyDAO, times(1)).delete(companyId);
    }
}
