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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleDAO vehicleDAO;

    @Mock
    private CompanyDAO companyDAO;

    @Mock
    private Validator validator;

    @InjectMocks
    private VehicleServiceImpl vehicleService;

    private Vehicle testVehicle;
    private Company testCompany;

    @BeforeEach
    void setUp() {
        testCompany = new Company();
        testCompany.setId(1);
        testCompany.setCnpj("12.345.678/0001-90");
        testCompany.setLegalName("Empresa Teste");

        testVehicle = new Vehicle();
        testVehicle.setId(1);
        testVehicle.setPlate("ABC1234");
        testVehicle.setManufacturer("Volvo");
        testVehicle.setModel("FH 540");
        testVehicle.setYear(2022);
        testVehicle.setCompanyId(1);
    }

    @Test
    @DisplayName("Deve criar veículo com sucesso")
    void createVehicle_Success() throws SQLException, BusinessException {
        doNothing().when(validator).validate(any(Vehicle.class));
        when(vehicleDAO.findByPlate(anyString())).thenReturn(Optional.empty());
        when(companyDAO.findById(anyInt())).thenReturn(Optional.of(testCompany));
        when(vehicleDAO.create(any(Vehicle.class))).thenReturn(1);

        Vehicle result = vehicleService.createVehicle(testVehicle);

        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(vehicleDAO).create(any(Vehicle.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar veículo com placa duplicada")
    void createVehicle_DuplicatePlate() throws SQLException {
        doNothing().when(validator).validate(any(Vehicle.class));
        when(vehicleDAO.findByPlate(anyString())).thenReturn(Optional.of(testVehicle));

        assertThrows(BusinessException.class, () -> vehicleService.createVehicle(testVehicle));
        verify(vehicleDAO, never()).create(any(Vehicle.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar veículo com empresa inexistente")
    void createVehicle_CompanyNotFound() throws SQLException {
        doNothing().when(validator).validate(any(Vehicle.class));
        when(vehicleDAO.findByPlate(anyString())).thenReturn(Optional.empty());
        when(companyDAO.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> vehicleService.createVehicle(testVehicle));
        verify(vehicleDAO, never()).create(any(Vehicle.class));
    }

    @Test
    @DisplayName("Deve atualizar veículo com sucesso")
    void updateVehicle_Success() throws SQLException, BusinessException {
        doNothing().when(validator).validate(any(Vehicle.class));
        when(validator.isValidId(anyInt())).thenReturn(true);
        when(vehicleDAO.findById(1)).thenReturn(Optional.of(testVehicle));
        when(vehicleDAO.findByPlate(anyString())).thenReturn(Optional.of(testVehicle));
        when(companyDAO.findById(anyInt())).thenReturn(Optional.of(testCompany));
        when(vehicleDAO.update(any(Vehicle.class))).thenReturn(true);

        Vehicle result = vehicleService.updateVehicle(testVehicle);

        assertNotNull(result);
        verify(vehicleDAO).update(any(Vehicle.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar veículo com ID inválido")
    void updateVehicle_InvalidId() throws SQLException {
        doNothing().when(validator).validate(any(Vehicle.class));
        when(validator.isValidId(anyInt())).thenReturn(false);

        assertThrows(BusinessException.class, () -> vehicleService.updateVehicle(testVehicle));
        verify(vehicleDAO, never()).update(any(Vehicle.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar veículo inexistente")
    void updateVehicle_NotFound() throws SQLException {
        doNothing().when(validator).validate(any(Vehicle.class));
        when(validator.isValidId(anyInt())).thenReturn(true);
        when(vehicleDAO.findById(1)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> vehicleService.updateVehicle(testVehicle));
        verify(vehicleDAO, never()).update(any(Vehicle.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar com placa de outro veículo")
    void updateVehicle_PlateBelongsToAnother() throws SQLException {
        Vehicle anotherVehicle = new Vehicle();
        anotherVehicle.setId(2);
        anotherVehicle.setPlate("ABC1234");

        doNothing().when(validator).validate(any(Vehicle.class));
        when(validator.isValidId(anyInt())).thenReturn(true);
        when(vehicleDAO.findById(1)).thenReturn(Optional.of(testVehicle));
        when(vehicleDAO.findByPlate(anyString())).thenReturn(Optional.of(anotherVehicle));

        assertThrows(BusinessException.class, () -> vehicleService.updateVehicle(testVehicle));
        verify(vehicleDAO, never()).update(any(Vehicle.class));
    }

    @Test
    @DisplayName("Deve deletar veículo com sucesso")
    void deleteVehicle_Success() throws SQLException, BusinessException {
        when(validator.isValidId(1)).thenReturn(true);
        when(vehicleDAO.delete(1)).thenReturn(true);

        boolean result = vehicleService.deleteVehicle(1);

        assertTrue(result);
        verify(vehicleDAO).delete(1);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar veículo com ID inválido")
    void deleteVehicle_InvalidId() throws SQLException {
        when(validator.isValidId(0)).thenReturn(false);

        assertThrows(BusinessException.class, () -> vehicleService.deleteVehicle(0));
        verify(vehicleDAO, never()).delete(anyInt());
    }

    @Test
    @DisplayName("Deve buscar veículo por ID")
    void getVehicleById_Success() throws SQLException {
        when(validator.isValidId(1)).thenReturn(true);
        when(vehicleDAO.findById(1)).thenReturn(Optional.of(testVehicle));

        Optional<Vehicle> result = vehicleService.getVehicleById(1);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar veículo com ID inválido")
    void getVehicleById_InvalidId() throws SQLException {
        when(validator.isValidId(0)).thenReturn(false);

        Optional<Vehicle> result = vehicleService.getVehicleById(0);

        assertFalse(result.isPresent());
        verify(vehicleDAO, never()).findById(anyInt());
    }

    @Test
    @DisplayName("Deve buscar veículo por placa")
    void getVehicleByPlate_Success() throws SQLException {
        when(vehicleDAO.findByPlate("ABC1234")).thenReturn(Optional.of(testVehicle));

        Optional<Vehicle> result = vehicleService.getVehicleByPlate("ABC1234");

        assertTrue(result.isPresent());
        assertEquals("ABC1234", result.get().getPlate());
    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar veículo com placa nula")
    void getVehicleByPlate_NullPlate() throws SQLException {
        Optional<Vehicle> result = vehicleService.getVehicleByPlate(null);

        assertFalse(result.isPresent());
        verify(vehicleDAO, never()).findByPlate(anyString());
    }

    @Test
    @DisplayName("Deve listar todos os veículos")
    void getAllVehicles_Success() throws SQLException {
        Vehicle vehicle2 = new Vehicle();
        vehicle2.setId(2);
        vehicle2.setPlate("XYZ5678");

        when(vehicleDAO.findAll()).thenReturn(Arrays.asList(testVehicle, vehicle2));

        List<Vehicle> result = vehicleService.getAllVehicles();

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Deve buscar veículos por ID da empresa")
    void getVehiclesByCompanyId_Success() throws SQLException {
        when(validator.isValidId(1)).thenReturn(true);
        when(vehicleDAO.findByCompanyId(1)).thenReturn(Arrays.asList(testVehicle));

        List<Vehicle> result = vehicleService.getVehiclesByCompanyId(1);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Deve retornar lista vazia ao buscar veículos com ID de empresa inválido")
    void getVehiclesByCompanyId_InvalidId() throws SQLException {
        when(validator.isValidId(0)).thenReturn(false);

        List<Vehicle> result = vehicleService.getVehiclesByCompanyId(0);

        assertTrue(result.isEmpty());
        verify(vehicleDAO, never()).findByCompanyId(anyInt());
    }

    @Test
    @DisplayName("Deve buscar veículos por modelo")
    void getVehiclesByModel_Success() throws SQLException {
        when(vehicleDAO.findByModel("FH 540")).thenReturn(Arrays.asList(testVehicle));

        List<Vehicle> result = vehicleService.getVehiclesByModel("FH 540");

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Deve retornar lista vazia ao buscar veículos com modelo nulo")
    void getVehiclesByModel_NullModel() throws SQLException {
        List<Vehicle> result = vehicleService.getVehiclesByModel(null);

        assertTrue(result.isEmpty());
        verify(vehicleDAO, never()).findByModel(anyString());
    }

    @Test
    @DisplayName("Deve buscar veículos por ano")
    void getVehiclesByYear_Success() throws SQLException {
        when(vehicleDAO.findByYear(2022)).thenReturn(Arrays.asList(testVehicle));

        List<Vehicle> result = vehicleService.getVehiclesByYear(2022);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Deve retornar lista vazia ao buscar veículos com ano inválido")
    void getVehiclesByYear_InvalidYear() throws SQLException {
        List<Vehicle> result = vehicleService.getVehiclesByYear(1800);

        assertTrue(result.isEmpty());
        verify(vehicleDAO, never()).findByYear(anyInt());
    }
}