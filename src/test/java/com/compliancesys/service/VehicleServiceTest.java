package com.compliancesys.service;

import com.compliancesys.dao.VehicleDAO;
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.Vehicle;
import com.compliancesys.service.impl.VehicleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.Mockito.*;

class VehicleServiceTest {

    @Mock
    private VehicleDAO vehicleDAO;

    @InjectMocks
    private VehicleServiceImpl vehicleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterVehicleSuccess() throws BusinessException, SQLException {
        // Usando o construtor de inserção (sem ID, createdAt, updatedAt)
        Vehicle newVehicle = new Vehicle("ABC1234", "MarcaX", "ModeloY", 2020, 1);
        Vehicle expectedVehicle = new Vehicle(1, "ABC1234", "MarcaX", "ModeloY", 2020, 1, LocalDateTime.now(), LocalDateTime.now());

        when(vehicleDAO.findByPlate(newVehicle.getPlate())).thenReturn(Optional.empty());
        when(vehicleDAO.create(any(Vehicle.class))).thenReturn(1); // DAO retorna o ID
        when(vehicleDAO.findById(1)).thenReturn(Optional.of(expectedVehicle)); // Para simular o retorno do service

        Vehicle registeredVehicle = vehicleService.registerVehicle(newVehicle);

        assertNotNull(registeredVehicle);
        assertEquals(1, registeredVehicle.getId());
        assertEquals("ABC1234", registeredVehicle.getPlate());
        verify(vehicleDAO, times(1)).create(any(Vehicle.class));
    }

    @Test
    void testRegisterVehicleInvalidPlate() throws SQLException {
        Vehicle newVehicle = new Vehicle("INVALID", "MarcaX", "ModeloY", 2020, 1);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            vehicleService.registerVehicle(newVehicle);
        });
        assertEquals("Placa inválida.", exception.getMessage());
        verify(vehicleDAO, never()).create(any(Vehicle.class));
    }

    @Test
    void testRegisterVehiclePlateAlreadyExists() throws SQLException {
        Vehicle existingVehicle = new Vehicle(1, "ABC1234", "MarcaX", "ModeloY", 2020, 1);
        Vehicle newVehicle = new Vehicle("ABC1234", "MarcaZ", "ModeloW", 2021, 1);

        when(vehicleDAO.findByPlate(newVehicle.getPlate())).thenReturn(Optional.of(existingVehicle));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            vehicleService.registerVehicle(newVehicle);
        });
        assertEquals("Já existe um veículo cadastrado com esta placa.", exception.getMessage());
        verify(vehicleDAO, never()).create(any(Vehicle.class));
    }

    @Test
    void testGetVehicleByIdSuccess() throws BusinessException, SQLException {
        int vehicleId = 1;
        Vehicle expectedVehicle = new Vehicle(vehicleId, "ABC1234", "MarcaX", "ModeloY", 2020, 1, LocalDateTime.now(), LocalDateTime.now());

        when(vehicleDAO.findById(vehicleId)).thenReturn(Optional.of(expectedVehicle));

        Optional<Vehicle> actualVehicle = vehicleService.getVehicleById(vehicleId);

        assertTrue(actualVehicle.isPresent());
        assertEquals(expectedVehicle, actualVehicle.get());
        verify(vehicleDAO, times(1)).findById(vehicleId);
    }

    @Test
    void testGetVehicleByIdNotFound() throws BusinessException, SQLException {
        int vehicleId = 99;
        when(vehicleDAO.findById(vehicleId)).thenReturn(Optional.empty());

        Optional<Vehicle> actualVehicle = vehicleService.getVehicleById(vehicleId);

        assertFalse(actualVehicle.isPresent());
        verify(vehicleDAO, times(1)).findById(vehicleId);
    }

    @Test
    void testGetAllVehiclesSuccess() throws BusinessException, SQLException {
        List<Vehicle> expectedVehicles = Arrays.asList(
                new Vehicle(1, "ABC1234", "MarcaX", "ModeloY", 2020, 1, LocalDateTime.now(), LocalDateTime.now()),
                new Vehicle(2, "DEF5678", "MarcaA", "ModeloB", 2021, 1, LocalDateTime.now(), LocalDateTime.now())
        );
        when(vehicleDAO.findAll()).thenReturn(expectedVehicles);

        List<Vehicle> actualVehicles = vehicleService.getAllVehicles();

        assertNotNull(actualVehicles);
        assertEquals(2, actualVehicles.size());
        assertEquals(expectedVehicles, actualVehicles);
        verify(vehicleDAO, times(1)).findAll();
    }

    @Test
    void testGetAllVehiclesEmpty() throws BusinessException, SQLException {
        when(vehicleDAO.findAll()).thenReturn(Collections.emptyList());

        List<Vehicle> actualVehicles = vehicleService.getAllVehicles();

        assertNotNull(actualVehicles);
        assertTrue(actualVehicles.isEmpty());
        verify(vehicleDAO, times(1)).findAll();
    }

    @Test
    void testUpdateVehicleSuccess() throws BusinessException, SQLException {
        Vehicle existingVehicle = new Vehicle(1, "ABC1234", "MarcaX", "ModeloY", 2020, 1, LocalDateTime.now(), LocalDateTime.now());
        Vehicle updatedVehicle = new Vehicle(1, "ABC1234", "MarcaAtualizada", "ModeloAtualizado", 2022, 1, LocalDateTime.now(), LocalDateTime.now());

        when(vehicleDAO.findById(updatedVehicle.getId())).thenReturn(Optional.of(existingVehicle));
        when(vehicleDAO.update(any(Vehicle.class))).thenReturn(true);

        boolean result = vehicleService.updateVehicle(updatedVehicle);

        assertTrue(result);
        verify(vehicleDAO, times(1)).update(any(Vehicle.class));
    }

    @Test
    void testUpdateVehicleNotFound() throws SQLException {
        Vehicle nonExistentVehicle = new Vehicle(99, "XYZ9999", "MarcaNaoExiste", "ModeloNaoExiste", 2023, 1, LocalDateTime.now(), LocalDateTime.now());

        when(vehicleDAO.findById(nonExistentVehicle.getId())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            vehicleService.updateVehicle(nonExistentVehicle);
        });
        assertEquals("Veículo não encontrado para atualização.", exception.getMessage());
        verify(vehicleDAO, never()).update(any(Vehicle.class));
    }

    @Test
    void testDeleteVehicleSuccess() throws SQLException, BusinessException {
        int vehicleId = 1;
        Vehicle vehicleToDelete = new Vehicle(vehicleId, "ABC1234", "MarcaX", "ModeloY", 2020, 1, LocalDateTime.now(), LocalDateTime.now());

        when(vehicleDAO.findById(vehicleId)).thenReturn(Optional.of(vehicleToDelete));
        when(vehicleDAO.delete(vehicleId)).thenReturn(true);

        boolean result = vehicleService.deleteVehicle(vehicleId);

        assertTrue(result);
        verify(vehicleDAO, times(1)).delete(vehicleId);
    }

    @Test
    void testDeleteVehicleNotFound() throws SQLException {
        int vehicleId = 99;
        when(vehicleDAO.findById(vehicleId)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            vehicleService.deleteVehicle(vehicleId);
        });
        assertEquals("Veículo não encontrado para exclusão.", exception.getMessage());
        verify(vehicleDAO, never()).delete(vehicleId);
    }
}

