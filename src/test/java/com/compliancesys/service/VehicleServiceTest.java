package com.compliancesys.service;

import com.compliancesys.model.Vehicle;
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

public class VehicleServiceTest {

    private VehicleService vehicleService;

    @BeforeEach
    void setUp() {
        // Inicializa o mock do VehicleService antes de cada teste
        vehicleService = Mockito.mock(VehicleService.class);
    }

    @Test
    void testRegisterVehicleSuccess() throws SQLException, IllegalArgumentException {
        Vehicle newVehicle = new Vehicle(0, "ABC1234", "Truck", "Model X", 1, true);
        when(vehicleService.registerVehicle(newVehicle)).thenReturn(1); // Simula o registro retornando um ID

        int id = vehicleService.registerVehicle(newVehicle);

        assertEquals(1, id);
        verify(vehicleService, times(1)).registerVehicle(newVehicle);
    }

    @Test
    void testRegisterVehicleInvalidData() throws SQLException, IllegalArgumentException {
        Vehicle invalidVehicle = new Vehicle(0, "", "Truck", "Model X", 1, true); // Placa vazia
        // Simula a exceção IllegalArgumentException para dados inválidos
        doThrow(new IllegalArgumentException("Placa do veículo não pode ser vazia")).when(vehicleService).registerVehicle(invalidVehicle);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            vehicleService.registerVehicle(invalidVehicle);
        });

        assertEquals("Placa do veículo não pode ser vazia", thrown.getMessage());
        verify(vehicleService, times(1)).registerVehicle(invalidVehicle);
    }

    @Test
    void testRegisterVehicleThrowsSQLException() throws SQLException, IllegalArgumentException {
        Vehicle newVehicle = new Vehicle(0, "ABC1234", "Truck", "Model X", 1, true);
        // Simula a exceção SQLException em caso de erro no banco de dados
        doThrow(new SQLException("Erro de conexão com o banco de dados")).when(vehicleService).registerVehicle(newVehicle);

        SQLException thrown = assertThrows(SQLException.class, () -> {
            vehicleService.registerVehicle(newVehicle);
        });

        assertEquals("Erro de conexão com o banco de dados", thrown.getMessage());
        verify(vehicleService, times(1)).registerVehicle(newVehicle);
    }

    @Test
    void testGetVehicleByIdFound() throws SQLException {
        Vehicle expectedVehicle = new Vehicle(1, "ABC1234", "Truck", "Model X", 1, true);
        when(vehicleService.getVehicleById(1)).thenReturn(Optional.of(expectedVehicle));

        Optional<Vehicle> result = vehicleService.getVehicleById(1);

        assertTrue(result.isPresent());
        assertEquals(expectedVehicle, result.get());
        verify(vehicleService, times(1)).getVehicleById(1);
    }

    @Test
    void testGetVehicleByIdNotFound() throws SQLException {
        when(vehicleService.getVehicleById(99)).thenReturn(Optional.empty());

        Optional<Vehicle> result = vehicleService.getVehicleById(99);

        assertFalse(result.isPresent());
        verify(vehicleService, times(1)).getVehicleById(99);
    }

    @Test
    void testGetVehicleByIdThrowsSQLException() throws SQLException {
        when(vehicleService.getVehicleById(1)).thenThrow(new SQLException("Erro ao buscar veículo por ID"));

        SQLException thrown = assertThrows(SQLException.class, () -> {
            vehicleService.getVehicleById(1);
        });

        assertEquals("Erro ao buscar veículo por ID", thrown.getMessage());
        verify(vehicleService, times(1)).getVehicleById(1);
    }

    @Test
    void testGetVehicleByPlateFound() throws SQLException {
        Vehicle expectedVehicle = new Vehicle(1, "ABC1234", "Truck", "Model X", 1, true);
        when(vehicleService.getVehicleByPlate("ABC1234")).thenReturn(Optional.of(expectedVehicle));

        Optional<Vehicle> result = vehicleService.getVehicleByPlate("ABC1234");

        assertTrue(result.isPresent());
        assertEquals(expectedVehicle, result.get());
        verify(vehicleService, times(1)).getVehicleByPlate("ABC1234");
    }

    @Test
    void testGetVehicleByPlateNotFound() throws SQLException {
        when(vehicleService.getVehicleByPlate("XYZ7890")).thenReturn(Optional.empty());

        Optional<Vehicle> result = vehicleService.getVehicleByPlate("XYZ7890");

        assertFalse(result.isPresent());
        verify(vehicleService, times(1)).getVehicleByPlate("XYZ7890");
    }

    @Test
    void testGetVehicleByPlateThrowsSQLException() throws SQLException {
        when(vehicleService.getVehicleByPlate("ABC1234")).thenThrow(new SQLException("Erro ao buscar veículo por placa"));

        SQLException thrown = assertThrows(SQLException.class, () -> {
            vehicleService.getVehicleByPlate("ABC1234");
        });

        assertEquals("Erro ao buscar veículo por placa", thrown.getMessage());
        verify(vehicleService, times(1)).getVehicleByPlate("ABC1234");
    }

    @Test
    void testGetAllVehicles() throws SQLException {
        Vehicle vehicle1 = new Vehicle(1, "ABC1234", "Truck", "Model X", 1, true);
        Vehicle vehicle2 = new Vehicle(2, "DEF5678", "Car", "Model Y", 2, false);
        List<Vehicle> expectedList = Arrays.asList(vehicle1, vehicle2);

        when(vehicleService.getAllVehicles()).thenReturn(expectedList);

        List<Vehicle> result = vehicleService.getAllVehicles();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedList, result);
        verify(vehicleService, times(1)).getAllVehicles();
    }

    @Test
    void testGetAllVehiclesEmpty() throws SQLException {
        when(vehicleService.getAllVehicles()).thenReturn(Collections.emptyList());

        List<Vehicle> result = vehicleService.getAllVehicles();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(vehicleService, times(1)).getAllVehicles();
    }

    @Test
    void testGetAllVehiclesThrowsSQLException() throws SQLException {
        when(vehicleService.getAllVehicles()).thenThrow(new SQLException("Erro ao listar todos os veículos"));

        SQLException thrown = assertThrows(SQLException.class, () -> {
            vehicleService.getAllVehicles();
        });

        assertEquals("Erro ao listar todos os veículos", thrown.getMessage());
        verify(vehicleService, times(1)).getAllVehicles();
    }

    @Test
    void testGetVehiclesByCompanyId() throws SQLException {
        Vehicle vehicle1 = new Vehicle(1, "ABC1234", "Truck", "Model X", 1, true);
        Vehicle vehicle2 = new Vehicle(3, "GHI9012", "Van", "Model Z", 1, true);
        List<Vehicle> expectedList = Arrays.asList(vehicle1, vehicle2);

        when(vehicleService.getVehiclesByCompanyId(1)).thenReturn(expectedList);

        List<Vehicle> result = vehicleService.getVehiclesByCompanyId(1);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedList, result);
        verify(vehicleService, times(1)).getVehiclesByCompanyId(1);
    }

    @Test
    void testGetVehiclesByCompanyIdNoVehicles() throws SQLException {
        when(vehicleService.getVehiclesByCompanyId(99)).thenReturn(Collections.emptyList());

        List<Vehicle> result = vehicleService.getVehiclesByCompanyId(99);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(vehicleService, times(1)).getVehiclesByCompanyId(99);
    }

    @Test
    void testGetVehiclesByCompanyIdThrowsSQLException() throws SQLException {
        when(vehicleService.getVehiclesByCompanyId(1)).thenThrow(new SQLException("Erro ao buscar veículos por ID de empresa"));

        SQLException thrown = assertThrows(SQLException.class, () -> {
            vehicleService.getVehiclesByCompanyId(1);
        });

        assertEquals("Erro ao buscar veículos por ID de empresa", thrown.getMessage());
        verify(vehicleService, times(1)).getVehiclesByCompanyId(1);
    }

    @Test
    void testUpdateVehicleSuccess() throws SQLException, IllegalArgumentException {
        Vehicle updatedVehicle = new Vehicle(1, "ABC1234", "Truck", "Model X Pro", 1, false);
        when(vehicleService.updateVehicle(updatedVehicle)).thenReturn(true);

        boolean result = vehicleService.updateVehicle(updatedVehicle);

        assertTrue(result);
        verify(vehicleService, times(1)).updateVehicle(updatedVehicle);
    }

    @Test
    void testUpdateVehicleFailure() throws SQLException, IllegalArgumentException {
        Vehicle nonExistentVehicle = new Vehicle(99, "NON9999", "Car", "Model A", 1, true);
        when(vehicleService.updateVehicle(nonExistentVehicle)).thenReturn(false);

        boolean result = vehicleService.updateVehicle(nonExistentVehicle);

        assertFalse(result);
        verify(vehicleService, times(1)).updateVehicle(nonExistentVehicle);
    }

    @Test
    void testUpdateVehicleInvalidData() throws SQLException, IllegalArgumentException {
        Vehicle invalidVehicle = new Vehicle(1, "ABC1234", "", "Model X", 1, true); // Tipo vazio
        doThrow(new IllegalArgumentException("Tipo de veículo não pode ser vazio")).when(vehicleService).updateVehicle(invalidVehicle);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            vehicleService.updateVehicle(invalidVehicle);
        });

        assertEquals("Tipo de veículo não pode ser vazio", thrown.getMessage());
        verify(vehicleService, times(1)).updateVehicle(invalidVehicle);
    }

    @Test
    void testUpdateVehicleThrowsSQLException() throws SQLException, IllegalArgumentException {
        Vehicle updatedVehicle = new Vehicle(1, "ABC1234", "Truck", "Model X Pro", 1, false);
        doThrow(new SQLException("Erro ao atualizar veículo")).when(vehicleService).updateVehicle(updatedVehicle);

        SQLException thrown = assertThrows(SQLException.class, () -> {
            vehicleService.updateVehicle(updatedVehicle);
        });

        assertEquals("Erro ao atualizar veículo", thrown.getMessage());
        verify(vehicleService, times(1)).updateVehicle(updatedVehicle);
    }

    @Test
    void testDeleteVehicleSuccess() throws SQLException {
        when(vehicleService.deleteVehicle(1)).thenReturn(true);

        boolean result = vehicleService.deleteVehicle(1);

        assertTrue(result);
        verify(vehicleService, times(1)).deleteVehicle(1);
    }

    @Test
    void testDeleteVehicleFailure() throws SQLException {
        when(vehicleService.deleteVehicle(99)).thenReturn(false);

        boolean result = vehicleService.deleteVehicle(99);

        assertFalse(result);
        verify(vehicleService, times(1)).deleteVehicle(99);
    }

    @Test
    void testDeleteVehicleThrowsSQLException() throws SQLException {
        when(vehicleService.deleteVehicle(1)).thenThrow(new SQLException("Erro ao deletar veículo"));

        SQLException thrown = assertThrows(SQLException.class, () -> {
            vehicleService.deleteVehicle(1);
        });

        assertEquals("Erro ao deletar veículo", thrown.getMessage());
        verify(vehicleService, times(1)).deleteVehicle(1);
    }
}
