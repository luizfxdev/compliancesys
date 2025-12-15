package com.compliancesys.service;

import com.compliancesys.dao.CompanyDAO;
import com.compliancesys.dao.VehicleDAO;
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.Company;
import com.compliancesys.model.Vehicle;
import com.compliancesys.service.impl.VehicleServiceImpl;
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

class VehicleServiceTest {

    @Mock
    private VehicleDAO vehicleDAO;
    @Mock
    private CompanyDAO companyDAO; // Mock para verificar existência da Company
    @Mock
    private Validator validator;

    @InjectMocks
    private VehicleServiceImpl vehicleService;

    private Company testCompany;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Configurações padrão para o validator
        when(validator.isValidId(anyInt())).thenReturn(true);
        when(validator.isValidPlate(anyString())).thenReturn(true);
        when(validator.isValidModel(anyString())).thenReturn(true);
        when(validator.isValidBrand(anyString())).thenReturn(true);
        when(validator.isValidYear(anyInt())).thenReturn(true);

        // Configuração padrão para o CompanyDAO
        testCompany = new Company(1, "Empresa Teste", "12345678000190", "Rua Teste, 123", "11987654321", "teste@empresa.com", LocalDateTime.now(), LocalDateTime.now());
        when(companyDAO.findById(anyInt())).thenReturn(Optional.of(testCompany));
    }

    // Construtor auxiliar para criar Vehicle de forma mais consistente nos testes
    private Vehicle createTestVehicle(int id, String plate, String brand, String model, int year, int companyId) {
        return new Vehicle(id, plate, brand, model, year, companyId, LocalDateTime.now(), LocalDateTime.now());
    }

    private Vehicle createTestVehicle(String plate, String brand, String model, int year, int companyId) {
        return new Vehicle(0, plate, brand, model, year, companyId, null, null);
    }

    @Test
    @DisplayName("Deve registrar um novo veículo com sucesso")
    void shouldRegisterVehicleSuccessfully() throws BusinessException, SQLException {
        Vehicle newVehicle = createTestVehicle("ABC1234", "MarcaX", "ModeloY", 2020, testCompany.getId());
        Vehicle vehicleWithId = createTestVehicle(1, "ABC1234", "MarcaX", "ModeloY", 2020, testCompany.getId());

        when(vehicleDAO.findByPlate(newVehicle.getPlate())).thenReturn(Optional.empty());
        when(vehicleDAO.create(any(Vehicle.class))).thenReturn(1);
        when(vehicleDAO.findById(1)).thenReturn(Optional.of(vehicleWithId));

        Vehicle registeredVehicle = vehicleService.registerVehicle(newVehicle);

        assertNotNull(registeredVehicle);
        assertEquals(1, registeredVehicle.getId());
        assertEquals(newVehicle.getPlate(), registeredVehicle.getPlate());
        verify(validator, times(1)).isValidPlate(newVehicle.getPlate());
        verify(validator, times(1)).isValidBrand(newVehicle.getBrand());
        verify(validator, times(1)).isValidModel(newVehicle.getModel());
        verify(validator, times(1)).isValidYear(newVehicle.getYear());
        verify(validator, times(1)).isValidId(newVehicle.getCompanyId());
        verify(companyDAO, times(1)).findById(newVehicle.getCompanyId());
        verify(vehicleDAO, times(1)).findByPlate(newVehicle.getPlate());
        verify(vehicleDAO, times(1)).create(any(Vehicle.class));
    }

    @Test
    @DisplayName("Não deve registrar veículo com placa inválida")
    void shouldNotRegisterVehicleWithInvalidPlate() {
        Vehicle newVehicle = createTestVehicle("INVALID", "MarcaX", "ModeloY", 2020, testCompany.getId());

        when(validator.isValidPlate(newVehicle.getPlate())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                vehicleService.registerVehicle(newVehicle));

        assertEquals("Placa inválida.", exception.getMessage());
        verify(validator, times(1)).isValidPlate(newVehicle.getPlate());
        verify(vehicleDAO, never()).findByPlate(anyString());
        verify(vehicleDAO, never()).create(any(Vehicle.class));
    }

    @Test
    @DisplayName("Não deve registrar veículo com marca inválida")
    void shouldNotRegisterVehicleWithInvalidBrand() {
        Vehicle newVehicle = createTestVehicle("ABC1234", "", "ModeloY", 2020, testCompany.getId()); // Marca vazia

        when(validator.isValidPlate(newVehicle.getPlate())).thenReturn(true);
        when(validator.isValidBrand(newVehicle.getBrand())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                vehicleService.registerVehicle(newVehicle));

        assertEquals("Marca do veículo inválida.", exception.getMessage());
        verify(validator, times(1)).isValidBrand(newVehicle.getBrand());
        verify(vehicleDAO, never()).findByPlate(anyString());
        verify(vehicleDAO, never()).create(any(Vehicle.class));
    }

    @Test
    @DisplayName("Não deve registrar veículo com modelo inválido")
    void shouldNotRegisterVehicleWithInvalidModel() {
        Vehicle newVehicle = createTestVehicle("ABC1234", "MarcaX", null, 2020, testCompany.getId()); // Modelo nulo

        when(validator.isValidPlate(newVehicle.getPlate())).thenReturn(true);
        when(validator.isValidBrand(newVehicle.getBrand())).thenReturn(true);
        when(validator.isValidModel(newVehicle.getModel())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                vehicleService.registerVehicle(newVehicle));

        assertEquals("Modelo do veículo inválido.", exception.getMessage());
        verify(validator, times(1)).isValidModel(newVehicle.getModel());
        verify(vehicleDAO, never()).findByPlate(anyString());
        verify(vehicleDAO, never()).create(any(Vehicle.class));
    }

    @Test
    @DisplayName("Não deve registrar veículo com ano inválido")
    void shouldNotRegisterVehicleWithInvalidYear() {
        Vehicle newVehicle = createTestVehicle("ABC1234", "MarcaX", "ModeloY", 1800, testCompany.getId()); // Ano inválido

        when(validator.isValidPlate(newVehicle.getPlate())).thenReturn(true);
        when(validator.isValidBrand(newVehicle.getBrand())).thenReturn(true);
        when(validator.isValidModel(newVehicle.getModel())).thenReturn(true);
        when(validator.isValidYear(newVehicle.getYear())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                vehicleService.registerVehicle(newVehicle));

        assertEquals("Ano do veículo inválido.", exception.getMessage());
        verify(validator, times(1)).isValidYear(newVehicle.getYear());
        verify(vehicleDAO, never()).findByPlate(anyString());
        verify(vehicleDAO, never()).create(any(Vehicle.class));
    }

    @Test
    @DisplayName("Não deve registrar veículo com ID de empresa inválido")
    void shouldNotRegisterVehicleWithInvalidCompanyId() {
        Vehicle newVehicle = createTestVehicle("ABC1234", "MarcaX", "ModeloY", 2020, -1);

        when(validator.isValidId(newVehicle.getCompanyId())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                vehicleService.registerVehicle(newVehicle));

        assertEquals("ID da empresa inválido.", exception.getMessage());
        verify(validator, times(1)).isValidId(newVehicle.getCompanyId());
        verify(companyDAO, never()).findById(anyInt());
        verify(vehicleDAO, never()).create(any(Vehicle.class));
    }

    @Test
    @DisplayName("Não deve registrar veículo se empresa não existir")
    void shouldNotRegisterVehicleIfCompanyNotFound() throws SQLException {
        Vehicle newVehicle = createTestVehicle("ABC1234", "MarcaX", "ModeloY", 2020, 99);

        when(companyDAO.findById(newVehicle.getCompanyId())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                vehicleService.registerVehicle(newVehicle));

        assertEquals("Empresa não encontrada.", exception.getMessage());
        verify(companyDAO, times(1)).findById(newVehicle.getCompanyId());
        verify(vehicleDAO, never()).create(any(Vehicle.class));
    }

    @Test
    @DisplayName("Não deve registrar veículo com placa já existente")
    void shouldNotRegisterVehiclePlateAlreadyExists() throws SQLException {
        Vehicle existingVehicle = createTestVehicle(1, "ABC1234", "MarcaX", "ModeloY", 2020, testCompany.getId());
        Vehicle newVehicle = createTestVehicle("ABC1234", "MarcaZ", "ModeloW", 2021, testCompany.getId());

        when(vehicleDAO.findByPlate(newVehicle.getPlate())).thenReturn(Optional.of(existingVehicle));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                vehicleService.registerVehicle(newVehicle));

        assertEquals("Já existe um veículo cadastrado com esta placa.", exception.getMessage());
        verify(validator, times(1)).isValidPlate(newVehicle.getPlate());
        verify(vehicleDAO, times(1)).findByPlate(newVehicle.getPlate());
        verify(vehicleDAO, never()).create(any(Vehicle.class));
    }

    @Test
    @DisplayName("Deve retornar veículo por ID quando encontrado")
    void shouldReturnVehicleByIdWhenFound() throws BusinessException, SQLException {
        int vehicleId = 1;
        Vehicle expectedVehicle = createTestVehicle(vehicleId, "ABC1234", "MarcaX", "ModeloY", 2020, testCompany.getId());

        when(vehicleDAO.findById(vehicleId)).thenReturn(Optional.of(expectedVehicle));

        Optional<Vehicle> actualVehicle = vehicleService.getVehicleById(vehicleId);

        assertTrue(actualVehicle.isPresent());
        assertEquals(expectedVehicle, actualVehicle.get());
        verify(validator, times(1)).isValidId(vehicleId);
        verify(vehicleDAO, times(1)).findById(vehicleId);
    }

    @Test
    @DisplayName("Deve retornar Optional vazio por ID quando não encontrado")
    void shouldReturnEmptyOptionalByIdWhenNotFound() throws BusinessException, SQLException {
        int vehicleId = 99;
        when(vehicleDAO.findById(vehicleId)).thenReturn(Optional.empty());

        Optional<Vehicle> actualVehicle = vehicleService.getVehicleById(vehicleId);

        assertFalse(actualVehicle.isPresent());
        verify(validator, times(1)).isValidId(vehicleId);
        verify(vehicleDAO, times(1)).findById(vehicleId);
    }

    @Test
    @DisplayName("Não deve buscar veículo com ID inválido")
    void shouldNotGetVehicleWithInvalidId() {
        int vehicleId = -1;

        when(validator.isValidId(vehicleId)).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                vehicleService.getVehicleById(vehicleId));

        assertEquals("ID do veículo inválido.", exception.getMessage());
        verify(validator, times(1)).isValidId(vehicleId);
        verify(vehicleDAO, never()).findById(anyInt());
    }

    @Test
    @DisplayName("Deve retornar todos os veículos")
    void shouldReturnAllVehicles() throws BusinessException, SQLException {
        List<Vehicle> expectedVehicles = Arrays.asList(
                createTestVehicle(1, "ABC1234", "MarcaX", "ModeloY", 2020, testCompany.getId()),
                createTestVehicle(2, "DEF5678", "MarcaA", "ModeloB", 2021, testCompany.getId())
        );
        when(vehicleDAO.findAll()).thenReturn(expectedVehicles);

        List<Vehicle> actualVehicles = vehicleService.getAllVehicles();

        assertNotNull(actualVehicles);
        assertEquals(2, actualVehicles.size());
        assertEquals(expectedVehicles, actualVehicles);
        verify(vehicleDAO, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há veículos")
    void shouldReturnEmptyListWhenNoVehicles() throws BusinessException, SQLException {
        when(vehicleDAO.findAll()).thenReturn(Collections.emptyList());

        List<Vehicle> actualVehicles = vehicleService.getAllVehicles();

        assertNotNull(actualVehicles);
        assertTrue(actualVehicles.isEmpty());
        verify(vehicleDAO, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve atualizar um veículo existente com sucesso")
    void shouldUpdateExistingVehicleSuccessfully() throws BusinessException, SQLException {
        Vehicle existingVehicle = createTestVehicle(1, "ABC1234", "MarcaX", "ModeloY", 2020, testCompany.getId());
        Vehicle updatedVehicle = createTestVehicle(1, "ABC1234", "MarcaAtualizada", "ModeloAtualizado", 2022, testCompany.getId());

        when(vehicleDAO.findById(updatedVehicle.getId())).thenReturn(Optional.of(existingVehicle));
        when(vehicleDAO.findByPlate(updatedVehicle.getPlate())).thenReturn(Optional.of(existingVehicle)); // Placa é a mesma
        when(vehicleDAO.update(any(Vehicle.class))).thenReturn(true);

        boolean result = vehicleService.updateVehicle(updatedVehicle);

        assertTrue(result);
        verify(validator, times(1)).isValidId(updatedVehicle.getId());
        verify(validator, times(1)).isValidPlate(updatedVehicle.getPlate());
        verify(validator, times(1)).isValidBrand(updatedVehicle.getBrand());
        verify(validator, times(1)).isValidModel(updatedVehicle.getModel());
        verify(validator, times(1)).isValidYear(updatedVehicle.getYear());
        verify(validator, times(1)).isValidId(updatedVehicle.getCompanyId());
        verify(companyDAO, times(1)).findById(updatedVehicle.getCompanyId());
        verify(vehicleDAO, times(1)).findById(updatedVehicle.getId());
        verify(vehicleDAO, times(1)).findByPlate(updatedVehicle.getPlate());
        verify(vehicleDAO, times(1)).update(any(Vehicle.class));
    }

    @Test
    @DisplayName("Não deve atualizar veículo com ID inválido")
    void shouldNotUpdateVehicleWithInvalidId() {
        Vehicle updatedVehicle = createTestVehicle(-1, "ABC1234", "MarcaAtualizada", "ModeloAtualizado", 2022, testCompany.getId());

        when(validator.isValidId(updatedVehicle.getId())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                vehicleService.updateVehicle(updatedVehicle));

        assertEquals("ID do veículo inválido.", exception.getMessage());
        verify(validator, times(1)).isValidId(updatedVehicle.getId());
        verify(vehicleDAO, never()).findById(anyInt());
        verify(vehicleDAO, never()).update(any(Vehicle.class));
    }

    @Test
    @DisplayName("Não deve atualizar veículo com placa inválida")
    void shouldNotUpdateVehicleWithInvalidPlate() {
        Vehicle updatedVehicle = createTestVehicle(1, "INVALID", "MarcaAtualizada", "ModeloAtualizado", 2022, testCompany.getId());

        when(validator.isValidId(updatedVehicle.getId())).thenReturn(true);
        when(validator.isValidPlate(updatedVehicle.getPlate())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                vehicleService.updateVehicle(updatedVehicle));

        assertEquals("Placa inválida.", exception.getMessage());
        verify(validator, times(1)).isValidPlate(updatedVehicle.getPlate());
        verify(vehicleDAO, never()).findById(anyInt());
        verify(vehicleDAO, never()).update(any(Vehicle.class));
    }

    @Test
    @DisplayName("Não deve atualizar veículo com marca inválida")
    void shouldNotUpdateVehicleWithInvalidBrand() {
        Vehicle updatedVehicle = createTestVehicle(1, "ABC1234", "", "ModeloAtualizado", 2022, testCompany.getId());

        when(validator.isValidId(updatedVehicle.getId())).thenReturn(true);
        when(validator.isValidPlate(updatedVehicle.getPlate())).thenReturn(true);
        when(validator.isValidBrand(updatedVehicle.getBrand())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                vehicleService.updateVehicle(updatedVehicle));

        assertEquals("Marca do veículo inválida.", exception.getMessage());
        verify(validator, times(1)).isValidBrand(updatedVehicle.getBrand());
        verify(vehicleDAO, never()).findById(anyInt());
        verify(vehicleDAO, never()).update(any(Vehicle.class));
    }

    @Test
    @DisplayName("Não deve atualizar veículo com modelo inválido")
    void shouldNotUpdateVehicleWithInvalidModel() {
        Vehicle updatedVehicle = createTestVehicle(1, "ABC1234", "MarcaAtualizada", null, 2022, testCompany.getId());

        when(validator.isValidId(updatedVehicle.getId())).thenReturn(true);
        when(validator.isValidPlate(updatedVehicle.getPlate())).thenReturn(true);
        when(validator.isValidBrand(updatedVehicle.getBrand())).thenReturn(true);
        when(validator.isValidModel(updatedVehicle.getModel())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                vehicleService.updateVehicle(updatedVehicle));

        assertEquals("Modelo do veículo inválido.", exception.getMessage());
        verify(validator, times(1)).isValidModel(updatedVehicle.getModel());
        verify(vehicleDAO, never()).findById(anyInt());
        verify(vehicleDAO, never()).update(any(Vehicle.class));
    }

    @Test
    @DisplayName("Não deve atualizar veículo com ano inválido")
    void shouldNotUpdateVehicleWithInvalidYear() {
        Vehicle updatedVehicle = createTestVehicle(1, "ABC1234", "MarcaAtualizada", "ModeloAtualizado", 1800, testCompany.getId());

        when(validator.isValidId(updatedVehicle.getId())).thenReturn(true);
        when(validator.isValidPlate(updatedVehicle.getPlate())).thenReturn(true);
        when(validator.isValidBrand(updatedVehicle.getBrand())).thenReturn(true);
        when(validator.isValidModel(updatedVehicle.getModel())).thenReturn(true);
        when(validator.isValidYear(updatedVehicle.getYear())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                vehicleService.updateVehicle(updatedVehicle));

        assertEquals("Ano do veículo inválido.", exception.getMessage());
        verify(validator, times(1)).isValidYear(updatedVehicle.getYear());
        verify(vehicleDAO, never()).findById(anyInt());
        verify(vehicleDAO, never()).update(any(Vehicle.class));
    }

    @Test
    @DisplayName("Não deve atualizar veículo com ID de empresa inválido")
    void shouldNotUpdateVehicleWithInvalidCompanyId() {
        Vehicle updatedVehicle = createTestVehicle(1, "ABC1234", "MarcaAtualizada", "ModeloAtualizado", 2022, -1);

        when(validator.isValidId(updatedVehicle.getId())).thenReturn(true);
        when(validator.isValidPlate(updatedVehicle.getPlate())).thenReturn(true);
        when(validator.isValidBrand(updatedVehicle.getBrand())).thenReturn(true);
        when(validator.isValidModel(updatedVehicle.getModel())).thenReturn(true);
        when(validator.isValidYear(updatedVehicle.getYear())).thenReturn(true);
        when(validator.isValidId(updatedVehicle.getCompanyId())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                vehicleService.updateVehicle(updatedVehicle));

        assertEquals("ID da empresa inválido.", exception.getMessage());
        verify(validator, times(1)).isValidId(updatedVehicle.getCompanyId());
        verify(companyDAO, never()).findById(anyInt());
        verify(vehicleDAO, never()).findById(anyInt());
        verify(vehicleDAO, never()).update(any(Vehicle.class));
    }

    @Test
    @DisplayName("Não deve atualizar veículo se empresa não existir")
    void shouldNotUpdateVehicleIfCompanyNotFound() throws SQLException {
        Vehicle existingVehicle = createTestVehicle(1, "ABC1234", "MarcaX", "ModeloY", 2020, testCompany.getId());
        Vehicle updatedVehicle = createTestVehicle(1, "ABC1234", "MarcaAtualizada", "ModeloAtualizado", 2022, 99);

        when(vehicleDAO.findById(updatedVehicle.getId())).thenReturn(Optional.of(existingVehicle));
        when(companyDAO.findById(updatedVehicle.getCompanyId())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                vehicleService.updateVehicle(updatedVehicle));

        assertEquals("Empresa não encontrada.", exception.getMessage());
        verify(companyDAO, times(1)).findById(updatedVehicle.getCompanyId());
        verify(vehicleDAO, times(1)).findById(updatedVehicle.getId());
        verify(vehicleDAO, never()).update(any(Vehicle.class));
    }

    @Test
    @DisplayName("Não deve atualizar veículo que não existe")
    void shouldNotUpdateNonExistentVehicle() throws SQLException {
        Vehicle nonExistentVehicle = createTestVehicle(99, "XYZ9999", "MarcaNaoExiste", "ModeloNaoExiste", 2023, testCompany.getId());

        when(vehicleDAO.findById(nonExistentVehicle.getId())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                vehicleService.updateVehicle(nonExistentVehicle));

        assertEquals("Veículo não encontrado para atualização.", exception.getMessage());
        verify(validator, times(1)).isValidId(nonExistentVehicle.getId());
        verify(vehicleDAO, times(1)).findById(nonExistentVehicle.getId());
        verify(vehicleDAO, never()).update(any(Vehicle.class));
    }

    @Test
    @DisplayName("Não deve atualizar veículo com placa já existente em outro veículo")
    void shouldNotUpdateVehicleWithExistingPlateInOtherVehicle() throws SQLException {
        Vehicle existingVehicle1 = createTestVehicle(1, "ABC1234", "MarcaX", "ModeloY", 2020, testCompany.getId());
        Vehicle existingVehicle2 = createTestVehicle(2, "DEF5678", "MarcaA", "ModeloB", 2021, testCompany.getId());
        Vehicle updatedVehicle = createTestVehicle(1, "DEF5678", "MarcaAtualizada", "ModeloAtualizado", 2022, testCompany.getId()); // Tentando usar placa do Vehicle 2

        when(vehicleDAO.findById(updatedVehicle.getId())).thenReturn(Optional.of(existingVehicle1));
        when(vehicleDAO.findByPlate(updatedVehicle.getPlate())).thenReturn(Optional.of(existingVehicle2)); // Encontra outro veículo com a placa

        BusinessException exception = assertThrows(BusinessException.class, () ->
                vehicleService.updateVehicle(updatedVehicle));

        assertEquals("Já existe um veículo cadastrado com esta placa.", exception.getMessage());
        verify(validator, times(1)).isValidPlate(updatedVehicle.getPlate());
        verify(vehicleDAO, times(1)).findById(updatedVehicle.getId());
        verify(vehicleDAO, times(1)).findByPlate(updatedVehicle.getPlate());
        verify(vehicleDAO, never()).update(any(Vehicle.class));
    }

    @Test
    @DisplayName("Deve deletar um veículo existente com sucesso")
    void shouldDeleteExistingVehicleSuccessfully() throws SQLException, BusinessException {
        int vehicleId = 1;
        Vehicle vehicleToDelete = createTestVehicle(vehicleId, "ABC1234", "MarcaX", "ModeloY", 2020, testCompany.getId());

        when(vehicleDAO.findById(vehicleId)).thenReturn(Optional.of(vehicleToDelete));
        when(vehicleDAO.delete(vehicleId)).thenReturn(true);

        boolean result = vehicleService.deleteVehicle(vehicleId);

        assertTrue(result);
        verify(validator, times(1)).isValidId(vehicleId);
        verify(vehicleDAO, times(1)).findById(vehicleId);
        verify(vehicleDAO, times(1)).delete(vehicleId);
    }

    @Test
    @DisplayName("Não deve deletar veículo com ID inválido")
    void shouldNotDeleteVehicleWithInvalidId() {
        int vehicleId = -1;

        when(validator.isValidId(vehicleId)).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                vehicleService.deleteVehicle(vehicleId));

        assertEquals("ID do veículo inválido.", exception.getMessage());
        verify(validator, times(1)).isValidId(vehicleId);
        verify(vehicleDAO, never()).findById(anyInt());
        verify(vehicleDAO, never()).delete(anyInt());
    }

    @Test
    @DisplayName("Não deve deletar veículo que não existe")
    void shouldNotDeleteNonExistentVehicle() throws SQLException {
        int vehicleId = 99;
        when(vehicleDAO.findById(vehicleId)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                vehicleService.deleteVehicle(vehicleId));

        assertEquals("Veículo não encontrado para exclusão.", exception.getMessage());
        verify(validator, times(1)).isValidId(vehicleId);
        verify(vehicleDAO, times(1)).findById(vehicleId);
        verify(vehicleDAO, never()).delete(anyInt());
    }

    @Test
    @DisplayName("Deve lançar BusinessException em caso de erro de SQL na criação")
    void shouldThrowBusinessExceptionOnSqlErrorDuringCreate() throws SQLException {
        Vehicle newVehicle = createTestVehicle("ABC1234", "MarcaX", "ModeloY", 2020, testCompany.getId());

        when(vehicleDAO.findByPlate(anyString())).thenReturn(Optional.empty());
        when(vehicleDAO.create(any(Vehicle.class))).thenThrow(new SQLException("Erro de DB na criação"));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                vehicleService.registerVehicle(newVehicle));

        assertTrue(exception.getMessage().contains("Erro interno ao registrar veículo."));
        verify(vehicleDAO, times(1)).create(any(Vehicle.class));
    }

    @Test
    @DisplayName("Deve lançar BusinessException em caso de erro de SQL na busca por ID")
    void shouldThrowBusinessExceptionOnSqlErrorDuringGetById() throws SQLException {
        int vehicleId = 1;

        when(vehicleDAO.findById(anyInt())).thenThrow(new SQLException("Erro de DB na busca"));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                vehicleService.getVehicleById(vehicleId));

        assertTrue(exception.getMessage().contains("Erro interno ao buscar veículo por ID."));
        verify(vehicleDAO, times(1)).findById(vehicleId);
    }

    @Test
    @DisplayName("Deve lançar BusinessException em caso de erro de SQL na busca de todos")
    void shouldThrowBusinessExceptionOnSqlErrorDuringGetAll() throws SQLException {
        when(vehicleDAO.findAll()).thenThrow(new SQLException("Erro de DB na busca de todos"));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                vehicleService.getAllVehicles());

        assertTrue(exception.getMessage().contains("Erro interno ao buscar todos os veículos."));
        verify(vehicleDAO, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve lançar BusinessException em caso de erro de SQL na atualização")
    void shouldThrowBusinessExceptionOnSqlErrorDuringUpdate() throws SQLException {
        Vehicle existingVehicle = createTestVehicle(1, "ABC1234", "MarcaX", "ModeloY", 2020, testCompany.getId());
        Vehicle updatedVehicle = createTestVehicle(1, "ABC1234", "MarcaAtualizada", "ModeloAtualizado", 2022, testCompany.getId());

        when(vehicleDAO.findById(updatedVehicle.getId())).thenReturn(Optional.of(existingVehicle));
        when(vehicleDAO.findByPlate(updatedVehicle.getPlate())).thenReturn(Optional.of(existingVehicle));
        when(vehicleDAO.update(any(Vehicle.class))).thenThrow(new SQLException("Erro de DB na atualização"));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                vehicleService.updateVehicle(updatedVehicle));

        assertTrue(exception.getMessage().contains("Erro interno ao atualizar veículo."));
        verify(vehicleDAO, times(1)).update(any(Vehicle.class));
    }

    @Test
    @DisplayName("Deve lançar BusinessException em caso de erro de SQL na exclusão")
    void shouldThrowBusinessExceptionOnSqlErrorDuringDelete() throws SQLException {
        int vehicleId = 1;
        Vehicle vehicleToDelete = createTestVehicle(vehicleId, "ABC1234", "MarcaX", "ModeloY", 2020, testCompany.getId());

        when(vehicleDAO.findById(vehicleId)).thenReturn(Optional.of(vehicleToDelete));
        when(vehicleDAO.delete(vehicleId)).thenThrow(new SQLException("Erro de DB na exclusão"));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                vehicleService.deleteVehicle(vehicleId));

        assertTrue(exception.getMessage().contains("Erro interno ao deletar veículo."));
        verify(vehicleDAO, times(1)).delete(vehicleId);
    }
}
