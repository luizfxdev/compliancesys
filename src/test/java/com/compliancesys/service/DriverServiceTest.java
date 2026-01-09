package com.compliancesys.service;

import com.compliancesys.dao.CompanyDAO;
import com.compliancesys.dao.DriverDAO;
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.Company;
import com.compliancesys.model.Driver;
import com.compliancesys.service.impl.DriverServiceImpl;
import com.compliancesys.util.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.time.LocalDate;
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

class DriverServiceTest {

    @Mock
    private DriverDAO driverDAO;
    @Mock
    private CompanyDAO companyDAO; // Mock para verificar existência da Company
    @Mock
    private Validator validator;

    @InjectMocks
    private DriverServiceImpl driverService;

    private Company testCompany;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Configurações padrão para o validator
        when(validator.isValidId(anyInt())).thenReturn(true);
        when(validator.isValidCpf(anyString())).thenReturn(true);
        when(validator.isValidCnh(anyString())).thenReturn(true);
        when(validator.isValidCnhCategory(anyString())).thenReturn(true);
        when(validator.isValidDate(any(LocalDate.class))).thenReturn(true);
        when(validator.isValidName(anyString())).thenReturn(true);
        when(validator.isValidEmail(anyString())).thenReturn(true);
        when(validator.isValidPhone(anyString())).thenReturn(true);
        when(validator.isValidAddress(anyString())).thenReturn(true);

        // Configuração padrão para o CompanyDAO
        testCompany = new Company(1, "Empresa Teste", "12345678000190", "Rua Teste, 123", "11987654321", "teste@empresa.com", LocalDateTime.now(), LocalDateTime.now());
        when(companyDAO.findById(anyInt())).thenReturn(Optional.of(testCompany));
    }

    // Construtor auxiliar para criar Driver de forma mais consistente nos testes
    private Driver createTestDriver(int id, int companyId, String name, String cpf, String cnh, String cnhCategory,
                                    LocalDate cnhExpiration, LocalDate birthDate, String email, String phone, String address) {
        return new Driver(id, companyId, name, cpf, cnh, cnhCategory, cnhExpiration, birthDate, email, phone, address, LocalDateTime.now(), LocalDateTime.now());
    }

    private Driver createTestDriver(int companyId, String name, String cpf, String cnh, String cnhCategory,
                                    LocalDate cnhExpiration, LocalDate birthDate, String email, String phone, String address) {
        return new Driver(0, companyId, name, cpf, cnh, cnhCategory, cnhExpiration, birthDate, email, phone, address, null, null);
    }

    @Test
    @DisplayName("Deve registrar um novo motorista com sucesso")
    void shouldRegisterDriverSuccessfully() throws SQLException, BusinessException {
        Driver newDriver = createTestDriver(testCompany.getId(), "João Silva", "11122233344", "12345678901", "B",
                LocalDate.now().plusYears(5), LocalDate.now().minusYears(30), "joao.silva@test.com", "11987654321", "Rua A, 100");
        Driver driverWithId = createTestDriver(1, testCompany.getId(), "João Silva", "11122233344", "12345678901", "B",
                LocalDate.now().plusYears(5), LocalDate.now().minusYears(30), "joao.silva@test.com", "11987654321", "Rua A, 100");

        when(driverDAO.findByCpf(anyString())).thenReturn(Optional.empty());
        when(driverDAO.findByCnh(anyString())).thenReturn(Optional.empty());
        when(driverDAO.findByEmail(anyString())).thenReturn(Optional.empty());
        when(driverDAO.findByPhone(anyString())).thenReturn(Optional.empty());
        when(driverDAO.create(any(Driver.class))).thenReturn(1);
        when(driverDAO.findById(1)).thenReturn(Optional.of(driverWithId));

        Driver registeredDriver = driverService.registerDriver(newDriver);

        assertNotNull(registeredDriver);
        assertEquals(1, registeredDriver.getId());
        assertEquals(newDriver.getCpf(), registeredDriver.getCpf());
        verify(validator, times(1)).isValidId(newDriver.getCompanyId());
        verify(validator, times(1)).isValidName(newDriver.getName());
        verify(validator, times(1)).isValidCpf(newDriver.getCpf());
        verify(validator, times(1)).isValidCnh(newDriver.getCnh());
        verify(validator, times(1)).isValidCnhCategory(newDriver.getCnhCategory());
        verify(validator, times(1)).isValidDate(newDriver.getCnhExpiration());
        verify(validator, times(1)).isValidDate(newDriver.getBirthDate());
        verify(validator, times(1)).isValidEmail(newDriver.getEmail());
        verify(validator, times(1)).isValidPhone(newDriver.getPhone());
        verify(validator, times(1)).isValidAddress(newDriver.getAddress());
        verify(companyDAO, times(1)).findById(newDriver.getCompanyId());
        verify(driverDAO, times(1)).findByCpf(newDriver.getCpf());
        verify(driverDAO, times(1)).findByCnh(newDriver.getCnh());
        verify(driverDAO, times(1)).findByEmail(newDriver.getEmail());
        verify(driverDAO, times(1)).findByPhone(newDriver.getPhone());
        verify(driverDAO, times(1)).create(any(Driver.class));
    }

    @Test
    @DisplayName("Não deve registrar motorista com ID de empresa inválido")
    void shouldNotRegisterDriverWithInvalidCompanyId() {
        Driver newDriver = createTestDriver(-1, "João Silva", "11122233344", "12345678901", "B",
                LocalDate.now().plusYears(5), LocalDate.now().minusYears(30), "joao.silva@test.com", "11987654321", "Rua A, 100");

        when(validator.isValidId(newDriver.getCompanyId())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                driverService.registerDriver(newDriver));

        assertEquals("ID da empresa inválido.", exception.getMessage());
        verify(validator, times(1)).isValidId(newDriver.getCompanyId());
        verify(companyDAO, never()).findById(anyInt());
        verify(driverDAO, never()).create(any(Driver.class));
    }

    @Test
    @DisplayName("Não deve registrar motorista se empresa não existir")
    void shouldNotRegisterDriverIfCompanyNotFound() throws SQLException {
        Driver newDriver = createTestDriver(99, "João Silva", "11122233344", "12345678901", "B",
                LocalDate.now().plusYears(5), LocalDate.now().minusYears(30), "joao.silva@test.com", "11987654321", "Rua A, 100");

        when(companyDAO.findById(newDriver.getCompanyId())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                driverService.registerDriver(newDriver));

        assertEquals("Empresa não encontrada.", exception.getMessage());
        verify(companyDAO, times(1)).findById(newDriver.getCompanyId());
        verify(driverDAO, never()).create(any(Driver.class));
    }

    @Test
    @DisplayName("Não deve registrar motorista com nome inválido")
    void shouldNotRegisterDriverWithInvalidName() {
        Driver newDriver = createTestDriver(testCompany.getId(), "", "11122233344", "12345678901", "B",
                LocalDate.now().plusYears(5), LocalDate.now().minusYears(30), "joao.silva@test.com", "11987654321", "Rua A, 100");

        when(validator.isValidName(newDriver.getName())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                driverService.registerDriver(newDriver));

        assertEquals("Nome do motorista inválido.", exception.getMessage());
        verify(validator, times(1)).isValidName(newDriver.getName());
        verify(driverDAO, never()).create(any(Driver.class));
    }

    @Test
    @DisplayName("Não deve registrar motorista com CPF inválido")
    void shouldNotRegisterDriverWithInvalidCpf() {
        Driver newDriver = createTestDriver(testCompany.getId(), "João Silva", "invalid-cpf", "12345678901", "B",
                LocalDate.now().plusYears(5), LocalDate.now().minusYears(30), "joao.silva@test.com", "11987654321", "Rua A, 100");

        when(validator.isValidCpf(newDriver.getCpf())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                driverService.registerDriver(newDriver));

        assertEquals("CPF inválido.", exception.getMessage());
        verify(validator, times(1)).isValidCpf(newDriver.getCpf());
        verify(driverDAO, never()).create(any(Driver.class));
    }

    @Test
    @DisplayName("Não deve registrar motorista com CPF já existente")
    void shouldNotRegisterDriverWithExistingCpf() throws SQLException {
        Driver existingDriver = createTestDriver(1, testCompany.getId(), "Maria Silva", "11122233344", "98765432109", "B",
                LocalDate.now().plusYears(4), LocalDate.now().minusYears(25), "maria.silva@test.com", "11998765432", "Rua B, 200");
        Driver newDriver = createTestDriver(testCompany.getId(), "João Silva", "11122233344", "12345678901", "B",
                LocalDate.now().plusYears(5), LocalDate.now().minusYears(30), "joao.silva@test.com", "11987654321", "Rua A, 100");

        when(driverDAO.findByCpf(newDriver.getCpf())).thenReturn(Optional.of(existingDriver));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                driverService.registerDriver(newDriver));

        assertEquals("CPF já cadastrado.", exception.getMessage());
        verify(validator, times(1)).isValidCpf(newDriver.getCpf());
        verify(driverDAO, times(1)).findByCpf(newDriver.getCpf());
        verify(driverDAO, never()).create(any(Driver.class));
    }

    @Test
    @DisplayName("Não deve registrar motorista com CNH inválida")
    void shouldNotRegisterDriverWithInvalidCnh() {
        Driver newDriver = createTestDriver(testCompany.getId(), "João Silva", "11122233344", "invalid-cnh", "B",
                LocalDate.now().plusYears(5), LocalDate.now().minusYears(30), "joao.silva@test.com", "11987654321", "Rua A, 100");

        when(validator.isValidCnh(newDriver.getCnh())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                driverService.registerDriver(newDriver));

        assertEquals("CNH inválida.", exception.getMessage());
        verify(validator, times(1)).isValidCnh(newDriver.getCnh());
        verify(driverDAO, never()).create(any(Driver.class));
    }

    @Test
    @DisplayName("Não deve registrar motorista com CNH já existente")
    void shouldNotRegisterDriverWithExistingCnh() throws SQLException {
        Driver existingDriver = createTestDriver(1, testCompany.getId(), "Maria Silva", "99988877766", "12345678901", "B",
                LocalDate.now().plusYears(4), LocalDate.now().minusYears(25), "maria.silva@test.com", "11998765432", "Rua B, 200");
        Driver newDriver = createTestDriver(testCompany.getId(), "João Silva", "11122233344", "12345678901", "B",
                LocalDate.now().plusYears(5), LocalDate.now().minusYears(30), "joao.silva@test.com", "11987654321", "Rua A, 100");

        when(driverDAO.findByCnh(newDriver.getCnh())).thenReturn(Optional.of(existingDriver));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                driverService.registerDriver(newDriver));

        assertEquals("CNH já cadastrada.", exception.getMessage());
        verify(validator, times(1)).isValidCnh(newDriver.getCnh());
        verify(driverDAO, times(1)).findByCnh(newDriver.getCnh());
        verify(driverDAO, never()).create(any(Driver.class));
    }

    @Test
    @DisplayName("Não deve registrar motorista com categoria de CNH inválida")
    void shouldNotRegisterDriverWithInvalidCnhCategory() {
        Driver newDriver = createTestDriver(testCompany.getId(), "João Silva", "11122233344", "12345678901", "Z", // Categoria inválida
                LocalDate.now().plusYears(5), LocalDate.now().minusYears(30), "joao.silva@test.com", "11987654321", "Rua A, 100");

        when(validator.isValidCnhCategory(newDriver.getCnhCategory())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                driverService.registerDriver(newDriver));

        assertEquals("Categoria da CNH inválida.", exception.getMessage());
        verify(validator, times(1)).isValidCnhCategory(newDriver.getCnhCategory());
        verify(driverDAO, never()).create(any(Driver.class));
    }

    @Test
    @DisplayName("Não deve registrar motorista com data de expiração da CNH inválida")
    void shouldNotRegisterDriverWithInvalidCnhExpirationDate() {
        Driver newDriver = createTestDriver(testCompany.getId(), "João Silva", "11122233344", "12345678901", "B",
                null, LocalDate.now().minusYears(30), "joao.silva@test.com", "11987654321", "Rua A, 100"); // Data nula

        when(validator.isValidDate(newDriver.getCnhExpiration())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                driverService.registerDriver(newDriver));

        assertEquals("Data de expiração da CNH inválida.", exception.getMessage());
        verify(validator, times(1)).isValidDate(newDriver.getCnhExpiration());
        verify(driverDAO, never()).create(any(Driver.class));
    }

    @Test
    @DisplayName("Não deve registrar motorista com data de nascimento inválida")
    void shouldNotRegisterDriverWithInvalidBirthDate() {
        Driver newDriver = createTestDriver(testCompany.getId(), "João Silva", "11122233344", "12345678901", "B",
                LocalDate.now().plusYears(5), null, "joao.silva@test.com", "11987654321", "Rua A, 100"); // Data nula

        when(validator.isValidDate(newDriver.getBirthDate())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                driverService.registerDriver(newDriver));

        assertEquals("Data de nascimento inválida.", exception.getMessage());
        verify(validator, times(1)).isValidDate(newDriver.getBirthDate());
        verify(driverDAO, never()).create(any(Driver.class));
    }

    @Test
    @DisplayName("Não deve registrar motorista com email inválido")
    void shouldNotRegisterDriverWithInvalidEmail() {
        Driver newDriver = createTestDriver(testCompany.getId(), "João Silva", "11122233344", "12345678901", "B",
                LocalDate.now().plusYears(5), LocalDate.now().minusYears(30), "invalid-email", "11987654321", "Rua A, 100");

        when(validator.isValidEmail(newDriver.getEmail())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                driverService.registerDriver(newDriver));

        assertEquals("Email inválido.", exception.getMessage());
        verify(validator, times(1)).isValidEmail(newDriver.getEmail());
        verify(driverDAO, never()).create(any(Driver.class));
    }

    @Test
    @DisplayName("Não deve registrar motorista com email já existente")
    void shouldNotRegisterDriverWithExistingEmail() throws SQLException {
        Driver existingDriver = createTestDriver(1, testCompany.getId(), "Maria Silva", "99988877766", "98765432109", "B",
                LocalDate.now().plusYears(4), LocalDate.now().minusYears(25), "joao.silva@test.com", "11998765432", "Rua B, 200");
        Driver newDriver = createTestDriver(testCompany.getId(), "João Silva", "11122233344", "12345678901", "B",
                LocalDate.now().plusYears(5), LocalDate.now().minusYears(30), "joao.silva@test.com", "11987654321", "Rua A, 100");

        when(driverDAO.findByEmail(newDriver.getEmail())).thenReturn(Optional.of(existingDriver));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                driverService.registerDriver(newDriver));

        assertEquals("Email já cadastrado.", exception.getMessage());
        verify(validator, times(1)).isValidEmail(newDriver.getEmail());
        verify(driverDAO, times(1)).findByEmail(newDriver.getEmail());
        verify(driverDAO, never()).create(any(Driver.class));
    }

    @Test
    @DisplayName("Não deve registrar motorista com telefone inválido")
    void shouldNotRegisterDriverWithInvalidPhone() {
        Driver newDriver = createTestDriver(testCompany.getId(), "João Silva", "11122233344", "12345678901", "B",
                LocalDate.now().plusYears(5), LocalDate.now().minusYears(30), "joao.silva@test.com", "invalid-phone", "Rua A, 100");

        when(validator.isValidPhone(newDriver.getPhone())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                driverService.registerDriver(newDriver));

        assertEquals("Telefone inválido.", exception.getMessage());
        verify(validator, times(1)).isValidPhone(newDriver.getPhone());
        verify(driverDAO, never()).create(any(Driver.class));
    }

    @Test
    @DisplayName("Não deve registrar motorista com telefone já existente")
    void shouldNotRegisterDriverWithExistingPhone() throws SQLException {
        Driver existingDriver = createTestDriver(1, testCompany.getId(), "Maria Silva", "99988877766", "98765432109", "B",
                LocalDate.now().plusYears(4), LocalDate.now().minusYears(25), "maria.silva@test.com", "11987654321", "Rua B, 200");
        Driver newDriver = createTestDriver(testCompany.getId(), "João Silva", "11122233344", "12345678901", "B",
                LocalDate.now().plusYears(5), LocalDate.now().minusYears(30), "joao.silva@test.com", "11987654321", "Rua A, 100");

        when(driverDAO.findByPhone(newDriver.getPhone())).thenReturn(Optional.of(existingDriver));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                driverService.registerDriver(newDriver));

        assertEquals("Telefone já cadastrado.", exception.getMessage());
        verify(validator, times(1)).isValidPhone(newDriver.getPhone());
        verify(driverDAO, times(1)).findByPhone(newDriver.getPhone());
        verify(driverDAO, never()).create(any(Driver.class));
    }

    @Test
    @DisplayName("Não deve registrar motorista com endereço inválido")
    void shouldNotRegisterDriverWithInvalidAddress() {
        Driver newDriver = createTestDriver(testCompany.getId(), "João Silva", "11122233344", "12345678901", "B",
                LocalDate.now().plusYears(5), LocalDate.now().minusYears(30), "joao.silva@test.com", "11987654321", ""); // Endereço vazio

        when(validator.isValidAddress(newDriver.getAddress())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                driverService.registerDriver(newDriver));

        assertEquals("Endereço inválido.", exception.getMessage());
        verify(validator, times(1)).isValidAddress(newDriver.getAddress());
        verify(driverDAO, never()).create(any(Driver.class));
    }

    @Test
    @DisplayName("Deve retornar motorista por ID quando encontrado")
    void shouldReturnDriverByIdWhenFound() throws SQLException, BusinessException {
        int driverId = 1;
        Driver expectedDriver = createTestDriver(driverId, testCompany.getId(), "João Silva", "11122233344", "12345678901", "B",
                LocalDate.now().plusYears(5), LocalDate.now().minusYears(30), "joao.silva@test.com", "11987654321", "Rua A, 100");

        when(driverDAO.findById(driverId)).thenReturn(Optional.of(expectedDriver));

        Optional<Driver> result = driverService.getDriverById(driverId);

        assertTrue(result.isPresent());
        assertEquals(expectedDriver, result.get());
        verify(validator, times(1)).isValidId(driverId);
        verify(driverDAO, times(1)).findById(driverId);
    }

    @Test
    @DisplayName("Deve retornar Optional vazio por ID quando não encontrado")
    void shouldReturnEmptyOptionalByIdWhenNotFound() throws SQLException, BusinessException {
        int driverId = 99;

        when(driverDAO.findById(driverId)).thenReturn(Optional.empty());

        Optional<Driver> result = driverService.getDriverById(driverId);

        assertFalse(result.isPresent());
        verify(validator, times(1)).isValidId(driverId);
        verify(driverDAO, times(1)).findById(driverId);
    }

    @Test
    @DisplayName("Não deve buscar motorista com ID inválido")
    void shouldNotGetDriverWithInvalidId() {
        int driverId = -1;

        when(validator.isValidId(anyInt())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                driverService.getDriverById(driverId));

        assertEquals("ID do motorista inválido.", exception.getMessage());
        verify(validator, times(1)).isValidId(driverId);
        verify(driverDAO, never()).findById(anyInt());
    }

    @Test
    @DisplayName("Deve retornar todos os motoristas")
    void shouldReturnAllDrivers() throws SQLException, BusinessException {
        List<Driver> expectedDrivers = Arrays.asList(
                createTestDriver(1, testCompany.getId(), "João Silva", "11122233344", "12345678901", "B",
                        LocalDate.now().plusYears(5), LocalDate.now().minusYears(30), "joao.silva@test.com", "11987654321", "Rua A, 100"),
                createTestDriver(2, testCompany.getId(), "Maria Souza", "44455566677", "98765432109", "C",
                        LocalDate.now().plusYears(3), LocalDate.now().minusYears(25), "maria.souza@test.com", "11998765432", "Rua B, 200")
        );

        when(driverDAO.findAll()).thenReturn(expectedDrivers);

        List<Driver> result = driverService.getAllDrivers();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedDrivers, result);
        verify(driverDAO, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar motoristas por ID da empresa")
    void shouldReturnDriversByCompanyId() throws SQLException, BusinessException {
        int companyId = testCompany.getId();
        List<Driver> expectedDrivers = Arrays.asList(
                createTestDriver(1, companyId, "João Silva", "11122233344", "12345678901", "B",
                        LocalDate.now().plusYears(5), LocalDate.now().minusYears(30), "joao.silva@test.com", "11987654321", "Rua A, 100"),
                createTestDriver(2, companyId, "Maria Souza", "44455566677", "98765432109", "C",
                        LocalDate.now().plusYears(3), LocalDate.now().minusYears(25), "maria.souza@test.com", "11998765432", "Rua B, 200")
        );

        when(driverDAO.findByCompanyId(companyId)).thenReturn(expectedDrivers);

        List<Driver> result = driverService.getDriversByCompanyId(companyId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedDrivers, result);
        verify(validator, times(1)).isValidId(companyId);
        verify(driverDAO, times(1)).findByCompanyId(companyId);
    }

    @Test
    @DisplayName("Não deve buscar motoristas com ID de empresa inválido")
    void shouldNotGetDriversWithInvalidCompanyId() {
        int companyId = -1;

        when(validator.isValidId(companyId)).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                driverService.getDriversByCompanyId(companyId));

        assertEquals("ID da empresa inválido.", exception.getMessage());
        verify(validator, times(1)).isValidId(companyId);
        verify(driverDAO, never()).findByCompanyId(anyInt());
    }

    @Test
    @DisplayName("Deve retornar motorista por CPF")
    void shouldReturnDriverByCpf() throws SQLException, BusinessException {
        String cpf = "11122233344";
        Driver expectedDriver = createTestDriver(1, testCompany.getId(), "João Silva", cpf, "12345678901", "B",
                LocalDate.now().plusYears(5), LocalDate.now().minusYears(30), "joao.silva@test.com", "11987654321", "Rua A, 100");

        when(driverDAO.findByCpf(cpf)).thenReturn(Optional.of(expectedDriver));

        Optional<Driver> result = driverService.getDriverByCpf(cpf);

        assertTrue(result.isPresent());
        assertEquals(expectedDriver, result.get());
        verify(validator, times(1)).isValidCpf(cpf);
        verify(driverDAO, times(1)).findByCpf(cpf);
    }

    @Test
    @DisplayName("Não deve buscar motorista com CPF inválido")
    void shouldNotGetDriverWithInvalidCpf() {
        String cpf = "invalid-cpf";

        when(validator.isValidCpf(cpf)).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                driverService.getDriverByCpf(cpf));

        assertEquals("CPF inválido.", exception.getMessage());
        verify(validator, times(1)).isValidCpf(cpf);
        verify(driverDAO, never()).findByCpf(anyString());
    }

    @Test
    @DisplayName("Deve retornar motorista por CNH")
    void shouldReturnDriverByCnh() throws SQLException, BusinessException {
        String cnh = "12345678901";
        Driver expectedDriver = createTestDriver(1, testCompany.getId(), "João Silva", "11122233344", cnh, "B",
                LocalDate.now().plusYears(5), LocalDate.now().minusYears(30), "joao.silva@test.com", "11987654321", "Rua A, 100");

        when(driverDAO.findByCnh(cnh)).thenReturn(Optional.of(expectedDriver));

        Optional<Driver> result = driverService.getDriverByCnh(cnh);

        assertTrue(result.isPresent());
        assertEquals(expectedDriver, result.get());
        verify(validator, times(1)).isValidCnh(cnh);
        verify(driverDAO, times(1)).findByCnh(cnh);
    }

    @Test
    @DisplayName("Não deve buscar motorista com CNH inválida")
    void shouldNotGetDriverWithInvalidCnh() {
        String cnh = "invalid-cnh";

        when(validator.isValidCnh(cnh)).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                driverService.getDriverByCnh(cnh));

        assertEquals("CNH inválida.", exception.getMessage());
        verify(validator, times(1)).isValidCnh(cnh);
        verify(driverDAO, never()).findByCnh(anyString());
    }

    @Test
    @DisplayName("Deve retornar motorista por email")
    void shouldReturnDriverByEmail() throws SQLException, BusinessException {
        String email = "joao.silva@test.com";
        Driver expectedDriver = createTestDriver(1, testCompany.getId(), "João Silva", "11122233344", "12345678901", "B",
                LocalDate.now().plusYears(5), LocalDate.now().minusYears(30), email, "11987654321", "Rua A, 100");

        when(driverDAO.findByEmail(email)).thenReturn(Optional.of(expectedDriver));

        Optional<Driver> result = driverService.getDriverByEmail(email);

        assertTrue(result.isPresent());
        assertEquals(expectedDriver, result.get());
        verify(validator, times(1)).isValidEmail(email);
        verify(driverDAO, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("Não deve buscar motorista com email inválido")
    void shouldNotGetDriverWithInvalidEmail() {
        String email = "invalid-email";

        when(validator.isValidEmail(email)).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                driverService.getDriverByEmail(email));

        assertEquals("Email inválido.", exception.getMessage());
        verify(validator, times(1)).isValidEmail(email);
        verify(driverDAO, never()).findByEmail(anyString());
    }

    @Test
    @DisplayName("Deve retornar motorista por telefone")
    void shouldReturnDriverByPhone() throws SQLException, BusinessException {
        String phone = "11987654321";
        Driver expectedDriver = createTestDriver(1, testCompany.getId(), "João Silva", "11122233344", "12345678901", "B",
                LocalDate.now().plusYears(5), LocalDate.now().minusYears(30), "joao.silva@test.com", phone, "Rua A, 100");

        when(driverDAO.findByPhone(phone)).thenReturn(Optional.of(expectedDriver));

        Optional<Driver> result = driverService.getDriverByPhone(phone);

        assertTrue(result.isPresent());
        assertEquals(expectedDriver, result.get());
        verify(validator, times(1)).isValidPhone(phone);
        verify(driverDAO, times(1)).findByPhone(phone);
    }

    @Test
    @DisplayName("Não deve buscar motorista com telefone inválido")
    void shouldNotGetDriverWithInvalidPhone() {
        String phone = "invalid-phone";

        when(validator.isValidPhone(phone)).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                driverService.getDriverByPhone(phone));

        assertEquals("Telefone inválido.", exception.getMessage());
        verify(validator, times(1)).isValidPhone(phone);
        verify(driverDAO, never()).findByPhone(anyString());
    }

    @Test
    @DisplayName("Deve retornar motoristas por período de data de nascimento")
    void shouldReturnDriversByBirthDateBetween() throws SQLException, BusinessException {
        LocalDate startDate = LocalDate.now().minusYears(35);
        LocalDate endDate = LocalDate.now().minusYears(20);
        List<Driver> expectedDrivers = Arrays.asList(
                createTestDriver(1, testCompany.getId(), "João Silva", "11122233344", "12345678901", "B",
                        LocalDate.now().plusYears(5), LocalDate.now().minusYears(30), "joao.silva@test.com", "11987654321", "Rua A, 100"),
                createTestDriver(2, testCompany.getId(), "Maria Souza", "44455566677", "98765432109", "C",
                        LocalDate.now().plusYears(3), LocalDate.now().minusYears(25), "maria.souza@test.com", "11998765432", "Rua B, 200")
        );

        when(driverDAO.findByBirthDateBetween(startDate, endDate)).thenReturn(expectedDrivers);

        List<Driver> result = driverService.getDriversByBirthDateBetween(startDate, endDate);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedDrivers, result);
        verify(validator, times(1)).isValidDate(startDate);
        verify(validator, times(1)).isValidDate(endDate);
        verify(driverDAO, times(1)).findByBirthDateBetween(startDate, endDate);
    }

    @Test
    @DisplayName("Não deve buscar motoristas com data de início de nascimento inválida")
    void shouldNotGetDriversWithInvalidBirthStartDate() {
        LocalDate startDate = null;
        LocalDate endDate = LocalDate.now();

        when(validator.isValidDate(startDate)).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                driverService.getDriversByBirthDateBetween(startDate, endDate));

        assertEquals("Data de início inválida.", exception.getMessage());
        verify(validator, times(1)).isValidDate(startDate);
        verify(driverDAO, never()).findByBirthDateBetween(any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    @DisplayName("Não deve buscar motoristas com data de fim de nascimento inválida")
    void shouldNotGetDriversWithInvalidBirthEndDate() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = null;

        when(validator.isValidDate(endDate)).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                driverService.getDriversByBirthDateBetween(startDate, endDate));

        assertEquals("Data de fim inválida.", exception.getMessage());
        verify(validator, times(1)).isValidDate(endDate);
        verify(driverDAO, never()).findByBirthDateBetween(any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    @DisplayName("Não deve buscar motoristas se data de início de nascimento for posterior à data de fim")
    void shouldNotGetDriversIfBirthStartDateAfterEndDate() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().minusDays(1);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                driverService.getDriversByBirthDateBetween(startDate, endDate));

        assertEquals("A data de início não pode ser posterior à data de fim.", exception.getMessage());
        verify(driverDAO, never()).findByBirthDateBetween(any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    @DisplayName("Deve atualizar um motorista existente com sucesso")
    void shouldUpdateExistingDriverSuccessfully() throws SQLException, BusinessException {
        Driver existingDriver = createTestDriver(1, testCompany.getId(), "João Silva", "11122233344", "12345678901", "B",
                LocalDate.now().plusYears(5), LocalDate.now().minusYears(30), "joao.silva@test.com", "11987654321", "Rua A, 100");
        Driver updatedDriver = createTestDriver(1, testCompany.getId(), "João Silva Atualizado", "11122233344", "12345678901", "C",
                LocalDate.now().plusYears(6), LocalDate.now().minusYears(31), "joao.atualizado@test.com", "11999998888", "Rua A, 101");

        when(driverDAO.findById(updatedDriver.getId())).thenReturn(Optional.of(existingDriver));
        when(driverDAO.findByCpf(updatedDriver.getCpf())).thenReturn(Optional.of(existingDriver)); // CPF é da própria empresa
        when(driverDAO.findByCnh(updatedDriver.getCnh())).thenReturn(Optional.of(existingDriver)); // CNH é da própria empresa
        when(driverDAO.findByEmail(updatedDriver.getEmail())).thenReturn(Optional.empty()); // Novo email não existe
        when(driverDAO.findByPhone(updatedDriver.getPhone())).thenReturn(Optional.empty()); // Novo telefone não existe
        when(driverDAO.update(any(Driver.class))).thenReturn(true);

        boolean result = driverService.updateDriver(updatedDriver);

        assertTrue(result);
        verify(validator, times(1)).isValidId(updatedDriver.getId());
        verify(validator, times(1)).isValidId(updatedDriver.getCompanyId());
        verify(validator, times(1)).isValidName(updatedDriver.getName());
        verify(validator, times(1)).isValidCpf(updatedDriver.getCpf());
        verify(validator, times(1)).isValidCnh(updatedDriver.getCnh());
        verify(validator, times(1)).isValidCnhCategory(updatedDriver.getCnhCategory());
        verify(validator, times(1)).isValidDate(updatedDriver.getCnhExpiration());
        verify(validator, times(1)).isValidDate(updatedDriver.getBirthDate());
        verify(validator, times(1)).isValidEmail(updatedDriver.getEmail());
        verify(validator, times(1)).isValidPhone(updatedDriver.getPhone());
        verify(validator, times(1)).isValidAddress(updatedDriver.getAddress());
        verify(companyDAO, times(1)).findById(updatedDriver.getCompanyId());
        verify(driverDAO, times(1)).findById(updatedDriver.getId());
        verify(driverDAO, times(1)).findByCpf(updatedDriver.getCpf());
        verify(driverDAO, times(1)).findByCnh(updatedDriver.getCnh());
        verify(driverDAO, times(1)).findByEmail(updatedDriver.getEmail());
        verify(driverDAO, times(1)).findByPhone(updatedDriver.getPhone());
        verify(driverDAO, times(1)).update(any(Driver.class));
    }

    @Test
    @DisplayName("Não deve atualizar motorista com ID inválido")
    void shouldNotUpdateDriverWithInvalidId() {
        Driver updatedDriver = createTestDriver(-1, testCompany.getId(), "João Silva Atualizado", "11122233344", "12345678901", "C",
                LocalDate.now().plusYears(6), LocalDate.now().minusYears(31), "joao.atualizado@test.com", "11999998888", "Rua A, 101");

        when(validator.isValidId(anyInt())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                driverService.updateDriver(updatedDriver));

        assertEquals("ID do motorista inválido.", exception.getMessage());
        verify(validator, times(1)).isValidId(updatedDriver.getId());
        verify(driverDAO, never()).findById(anyInt());
        verify(driverDAO, never()).update(any(Driver.class));
    }

    @Test
    @DisplayName("Não deve atualizar motorista que não existe")
    void shouldNotUpdateNonExistentDriver() throws SQLException {
        Driver nonExistentDriver = createTestDriver(99, testCompany.getId(), "Motorista Não Existe", "99999999999", "99999999999", "A",
                LocalDate.now().plusYears(1), LocalDate.now().minusYears(20), "naoexiste@test.com", "11900000000", "Rua Z, 0");

        when(driverDAO.findById(nonExistentDriver.getId())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                driverService.updateDriver(nonExistentDriver));

        assertEquals("Motorista não encontrado para atualização.", exception.getMessage());
        verify(validator, times(1)).isValidId(nonExistentDriver.getId());
        verify(driverDAO, times(1)).findById(nonExistentDriver.getId());
        verify(driverDAO, never()).update(any(Driver.class));
    }

    @Test
    @DisplayName("Não deve atualizar motorista com CPF já existente em outro motorista")
    void shouldNotUpdateDriverWithExistingCpfInOtherDriver() throws SQLException {
        Driver existingDriver1 = createTestDriver(1, testCompany.getId(), "João Silva", "11122233344", "12345678901", "B",
                LocalDate.now().plusYears(5), LocalDate.now().minusYears(30), "joao.silva@test.com", "11987654321", "Rua A, 100");
        Driver existingDriver2 = createTestDriver(2, testCompany.getId(), "Maria Souza", "22233344455", "98765432109", "C",
                LocalDate.now().plusYears(3), LocalDate.now().minusYears(25), "maria.souza@test.com", "11998765432", "Rua B, 200");
        Driver updatedDriver = createTestDriver(1, testCompany.getId(), "João Silva Atualizado", "22233344455", "12345678901", "B", // Tentando usar CPF da Maria
                LocalDate.now().plusYears(5), LocalDate.now().minusYears(30), "joao.silva@test.com", "11987654321", "Rua A, 100");

        when(driverDAO.findById(updatedDriver.getId())).thenReturn(Optional.of(existingDriver1));
        when(driverDAO.findByCpf(updatedDriver.getCpf())).thenReturn(Optional.of(existingDriver2)); // Encontra outro motorista com o CPF

        BusinessException exception = assertThrows(BusinessException.class, () ->
                driverService.updateDriver(updatedDriver));

        assertEquals("CPF já cadastrado em outro motorista.", exception.getMessage());
        verify(validator, times(1)).isValidCpf(updatedDriver.getCpf());
        verify(driverDAO, times(1)).findById(updatedDriver.getId());
        verify(driverDAO, times(1)).findByCpf(updatedDriver.getCpf());
        verify(driverDAO, never()).update(any(Driver.class));
    }

    @Test
    @DisplayName("Não deve atualizar motorista com CNH já existente em outro motorista")
    void shouldNotUpdateDriverWithExistingCnhInOtherDriver() throws SQLException {
        Driver existingDriver1 = createTestDriver(1, testCompany.getId(), "João Silva", "11122233344", "12345678901", "B",
                LocalDate.now().plusYears(5), LocalDate.now().minusYears(30), "joao.silva@test.com", "11987654321", "Rua A, 100");
        Driver existingDriver2 = createTestDriver(2, testCompany.getId(), "Maria Souza", "44455566677", "98765432109", "C",
                LocalDate.now().plusYears(3), LocalDate.now().minusYears(25), "maria.souza@test.com", "11998765432", "Rua B, 200");
        Driver updatedDriver = createTestDriver(1, testCompany.getId(), "João Silva Atualizado", "11122233344", "98765432109", "B", // Tentando usar CNH da Maria
                LocalDate.now().plusYears(5), LocalDate.now().minusYears(30), "joao.silva@test.com", "11987654321", "Rua A, 100");

        when(driverDAO.findById(updatedDriver.getId())).thenReturn(Optional.of(existingDriver1));
        when(driverDAO.findByCpf(updatedDriver.getCpf())).thenReturn(Optional.of(existingDriver1)); // CPF é da própria empresa
        when(driverDAO.findByCnh(updatedDriver.getCnh())).thenReturn(Optional.of(existingDriver2)); // Encontra outro motorista com a CNH

        BusinessException exception = assertThrows(BusinessException.class, () ->
                driverService.updateDriver(updatedDriver));

        assertEquals("CNH já cadastrada em outro motorista.", exception.getMessage());
        verify(validator, times(1)).isValidCnh(updatedDriver.getCnh());
        verify(driverDAO, times(1)).findById(updatedDriver.getId());
        verify(driverDAO, times(1)).findByCnh(updatedDriver.getCnh());
        verify(driverDAO, never()).update(any(Driver.class));
    }

    @Test
    @DisplayName("Não deve atualizar motorista com email já existente em outro motorista")
    void shouldNotUpdateDriverWithExistingEmailInOtherDriver() throws SQLException {
        Driver existingDriver1 = createTestDriver(1, testCompany.getId(), "João Silva", "11122233344", "12345678901", "B",
                LocalDate.now().plusYears(5), LocalDate.now().minusYears(30), "joao.silva@test.com", "11987654321", "Rua A, 100");
        Driver existingDriver2 = createTestDriver(2, testCompany.getId(), "Maria Souza", "44455566677", "98765432109", "C",
                LocalDate.now().plusYears(3), LocalDate.now().minusYears(25), "maria.souza@test.com", "11998765432", "Rua B, 200");
        Driver updatedDriver = createTestDriver(1, testCompany.getId(), "João Silva Atualizado", "11122233344", "12345678901", "B",
                LocalDate.now().plusYears(5), LocalDate.now().minusYears(30), "maria.souza@test.com", "11987654321", "Rua A, 100"); // Tentando usar email da Maria

        when(driverDAO.findById(updatedDriver.getId())).thenReturn(Optional.of(existingDriver1));
        when(driverDAO.findByCpf(updatedDriver.getCpf())).thenReturn(Optional.of(existingDriver1));
        when(driverDAO.findByCnh(updatedDriver.getCnh())).thenReturn(Optional.of(existingDriver1));
        when(driverDAO.findByEmail(updatedDriver.getEmail())).thenReturn(Optional.of(existingDriver2)); // Encontra outro motorista com o email

        BusinessException exception = assertThrows(BusinessException.class, () ->
                driverService.updateDriver(updatedDriver));

        assertEquals("Email já cadastrado em outro motorista.", exception.getMessage());
        verify(validator, times(1)).isValidEmail(updatedDriver.getEmail());
        verify(driverDAO, times(1)).findById(updatedDriver.getId());
        verify(driverDAO, times(1)).findByEmail(updatedDriver.getEmail());
        verify(driverDAO, never()).update(any(Driver.class));
    }

    @Test
    @DisplayName("Não deve atualizar motorista com telefone já existente em outro motorista")
    void shouldNotUpdateDriverWithExistingPhoneInOtherDriver() throws SQLException {
        Driver existingDriver1 = createTestDriver(1, testCompany.getId(), "João Silva", "11122233344", "12345678901", "B",
                LocalDate.now().plusYears(5), LocalDate.now().minusYears(30), "joao.silva@test.com", "11987654321", "Rua A, 100");
        Driver existingDriver2 = createTestDriver(2, testCompany.getId(), "Maria Souza", "44455566677", "98765432109", "C",
                LocalDate.now().plusYears(3), LocalDate.now().minusYears(25), "maria.souza@test.com", "11998765432", "Rua B, 200");
        Driver updatedDriver = createTestDriver(1, testCompany.getId(), "João Silva Atualizado", "11122233344", "12345678901", "B",
                LocalDate.now().plusYears(5), LocalDate.now().minusYears(30), "joao.silva@test.com", "11998765432", "Rua A, 100"); // Tentando usar telefone da Maria

        when(driverDAO.findById(updatedDriver.getId())).thenReturn(Optional.of(existingDriver1));
        when(driverDAO.findByCpf(updatedDriver.getCpf())).thenReturn(Optional.of(existingDriver1));
        when(driverDAO.findByCnh(updatedDriver.getCnh())).thenReturn(Optional.of(existingDriver1));
        when(driverDAO.findByEmail(updatedDriver.getEmail())).thenReturn(Optional.of(existingDriver1));
        when(driverDAO.findByPhone(updatedDriver.getPhone())).thenReturn(Optional.of(existingDriver2)); // Encontra outro motorista com o telefone

        BusinessException exception = assertThrows(BusinessException.class, () ->
                driverService.updateDriver(updatedDriver));

        assertEquals("Telefone já cadastrado em outro motorista.", exception.getMessage());
        verify(validator, times(1)).isValidPhone(updatedDriver.getPhone());
        verify(driverDAO, times(1)).findById(updatedDriver.getId());
        verify(driverDAO, times(1)).findByPhone(updatedDriver.getPhone());
        verify(driverDAO, never()).update(any(Driver.class));
    }

    @Test
    @DisplayName("Deve deletar um motorista existente com sucesso")
    void shouldDeleteExistingDriverSuccessfully() throws SQLException, BusinessException {
        int driverId = 1;
        Driver driverToDelete = createTestDriver(driverId, testCompany.getId(), "Motorista para Deletar", "11122233344", "12345678901", "B",
                LocalDate.now().plusYears(5), LocalDate.now().minusYears(30), "deletar@test.com", "11987654321", "Rua A, 100");

        when(driverDAO.findById(driverId)).thenReturn(Optional.of(driverToDelete));
        when(driverDAO.delete(driverId)).thenReturn(true);

        boolean result = driverService.deleteDriver(driverId);

        assertTrue(result);
        verify(validator, times(1)).isValidId(driverId);
        verify(driverDAO, times(1)).findById(driverId);
        verify(driverDAO, times(1)).delete(driverId);
    }

    @Test
    @DisplayName("Não deve deletar motorista com ID inválido")
    void shouldNotDeleteDriverWithInvalidId() {
        int driverId = -1;

        when(validator.isValidId(anyInt())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                driverService.deleteDriver(driverId));

        assertEquals("ID do motorista inválido.", exception.getMessage());
        verify(validator, times(1)).isValidId(driverId);
        verify(driverDAO, never()).findById(anyInt());
        verify(driverDAO, never()).delete(anyInt());
    }

    @Test
    @DisplayName("Não deve deletar motorista que não existe")
    void shouldNotDeleteNonExistentDriver() throws SQLException {
        int driverId = 99;

        when(driverDAO.findById(driverId)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                driverService.deleteDriver(driverId));

        assertEquals("Motorista não encontrado para exclusão.", exception.getMessage());
        verify(validator, times(1)).isValidId(driverId);
        verify(driverDAO, times(1)).findById(driverId);
        verify(driverDAO, never()).delete(anyInt());
    }

    @Test
    @DisplayName("Deve lançar BusinessException em caso de erro de SQL na criação")
    void shouldThrowBusinessExceptionOnSqlErrorDuringCreate() throws SQLException {
        Driver newDriver = createTestDriver(testCompany.getId(), "João Silva", "11122233344", "12345678901", "B",
                LocalDate.now().plusYears(5), LocalDate.now().minusYears(30), "joao.silva@test.com", "11987654321", "Rua A, 100");

        when(driverDAO.findByCpf(anyString())).thenReturn(Optional.empty());
        when(driverDAO.findByCnh(anyString())).thenReturn(Optional.empty());
        when(driverDAO.findByEmail(anyString())).thenReturn(Optional.empty());
        when(driverDAO.findByPhone(anyString())).thenReturn(Optional.empty());
        when(driverDAO.create(any(Driver.class))).thenThrow(new SQLException("Erro de DB na criação"));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                driverService.registerDriver(newDriver));

        assertTrue(exception.getMessage().contains("Erro interno ao registrar motorista."));
        verify(driverDAO, times(1)).create(any(Driver.class));
    }

    @Test
    @DisplayName("Deve lançar BusinessException em caso de erro de SQL na busca por ID")
    void shouldThrowBusinessExceptionOnSqlErrorDuringGetById() throws SQLException {
        int driverId = 1;

        when(driverDAO.findById(anyInt())).thenThrow(new SQLException("Erro de DB na busca"));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                driverService.getDriverById(driverId));

        assertTrue(exception.getMessage().contains("Erro interno ao buscar motorista por ID."));
        verify(driverDAO, times(1)).findById(driverId);
    }

    @Test
    @DisplayName("Deve lançar BusinessException em caso de erro de SQL na busca de todos")
    void shouldThrowBusinessExceptionOnSqlErrorDuringGetAll() throws SQLException {
        when(driverDAO.findAll()).thenThrow(new SQLException("Erro de DB na busca de todos"));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                driverService.getAllDrivers());

        assertTrue(exception.getMessage().contains("Erro interno ao buscar todos os motoristas."));
        verify(driverDAO, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve lançar BusinessException em caso de erro de SQL na atualização")
    void shouldThrowBusinessExceptionOnSqlErrorDuringUpdate() throws SQLException {
        Driver existingDriver = createTestDriver(1, testCompany.getId(), "João Silva", "11122233344", "12345678901", "B",
                LocalDate.now().plusYears(5), LocalDate.now().minusYears(30), "joao.silva@test.com", "11987654321", "Rua A, 100");
        Driver updatedDriver = createTestDriver(1, testCompany.getId(), "João Silva Atualizado", "11122233344", "12345678901", "C",
                LocalDate.now().plusYears(6), LocalDate.now().minusYears(31), "joao.atualizado@test.com", "11999998888", "Rua A, 101");

        when(driverDAO.findById(updatedDriver.getId())).thenReturn(Optional.of(existingDriver));
        when(driverDAO.findByCpf(updatedDriver.getCpf())).thenReturn(Optional.of(existingDriver));
        when(driverDAO.findByCnh(updatedDriver.getCnh())).thenReturn(Optional.of(existingDriver));
        when(driverDAO.findByEmail(updatedDriver.getEmail())).thenReturn(Optional.empty());
        when(driverDAO.findByPhone(updatedDriver.getPhone())).thenReturn(Optional.empty());
        when(driverDAO.update(any(Driver.class))).thenThrow(new SQLException("Erro de DB na atualização"));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                driverService.updateDriver(updatedDriver));

        assertTrue(exception.getMessage().contains("Erro interno ao atualizar motorista."));
        verify(driverDAO, times(1)).update(any(Driver.class));
    }

    @Test
    @DisplayName("Deve lançar BusinessException em caso de erro de SQL na exclusão")
    void shouldThrowBusinessExceptionOnSqlErrorDuringDelete() throws SQLException {
        int driverId = 1;
        Driver driverToDelete = createTestDriver(driverId, testCompany.getId(), "Motorista para Deletar", "11122233344", "12345678901", "B",
                LocalDate.now().plusYears(5), LocalDate.now().minusYears(30), "deletar@test.com", "11987654321", "Rua A, 100");

        when(driverDAO.findById(driverId)).thenReturn(Optional.of(driverToDelete));
        when(driverDAO.delete(driverId)).thenThrow(new SQLException("Erro de DB na exclusão"));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                driverService.deleteDriver(driverId));

        assertTrue(exception.getMessage().contains("Erro interno ao deletar motorista."));
        verify(driverDAO, times(1)).delete(driverId);
    }
}
