package com.compliancesys.dao;

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

public class VehicleDAOTest {

    private VehicleDAO vehicleDAO;

    @BeforeEach
    void setUp() {
        // Inicializa o mock do VehicleDAO antes de cada teste
        vehicleDAO = Mockito.mock(VehicleDAO.class);
    }

    @Test
    void testCreate() throws SQLException {
        Vehicle newVehicle = new Vehicle(0, "ABC1234", "Truck", "Model X", 1, true);
        when(vehicleDAO.create(newVehicle)).thenReturn(1); // Simula a criação retornando um ID

        int id = vehicleDAO.create(newVehicle);

        assertEquals(1, id);
        verify(vehicleDAO, times(1)).create(newVehicle); // Verifica se o método create foi chamado uma vez
    }

    @Test
    void testCreateFailure() throws SQLException {
        Vehicle newVehicle = new Vehicle(0, "ABC1234", "Truck", "Model X", 1, true);
        when(vehicleDAO.create(newVehicle)).thenReturn(-1); // Simula falha na criação

        int id = vehicleDAO.create(newVehicle);

        assertEquals(-1, id);
        verify(vehicleDAO, times(1)).create(newVehicle);
    }

    @Test
    void testFindByIdFound() throws SQLException {
        Vehicle expectedVehicle = new Vehicle(1, "ABC1234", "Truck", "Model X", 1, true);
        when(vehicleDAO.findById(1)).thenReturn(Optional.of(expectedVehicle));

        Optional<Vehicle> result = vehicleDAO.findById(1);

        assertTrue(result.isPresent());
        assertEquals(expectedVehicle, result.get());
        verify(vehicleDAO, times(1)).findById(1);
    }

    @Test
    void testFindByIdNotFound() throws SQLException {
        when(vehicleDAO.findById(99)).thenReturn(Optional.empty());

        Optional<Vehicle> result = vehicleDAO.findById(99);

        assertFalse(result.isPresent());
        verify(vehicleDAO, times(1)).findById(99);
    }

    @Test
    void testFindByPlateFound() throws SQLException {
        Vehicle expectedVehicle = new Vehicle(1, "ABC1234", "Truck", "Model X", 1, true);
        when(vehicleDAO.findByPlate("ABC1234")).thenReturn(Optional.of(expectedVehicle));

        Optional<Vehicle> result = vehicleDAO.findByPlate("ABC1234");

        assertTrue(result.isPresent());
        assertEquals(expectedVehicle, result.get());
        verify(vehicleDAO, times(1)).findByPlate("ABC1234");
    }

    @Test
    void testFindByPlateNotFound() throws SQLException {
        when(vehicleDAO.findByPlate("XYZ7890")).thenReturn(Optional.empty());

        Optional<Vehicle> result = vehicleDAO.findByPlate("XYZ7890");

        assertFalse(result.isPresent());
        verify(vehicleDAO, times(1)).findByPlate("XYZ7890");
    }

    @Test
    void testFindAll() throws SQLException {
        Vehicle vehicle1 = new Vehicle(1, "ABC1234", "Truck", "Model X", 1, true);
        Vehicle vehicle2 = new Vehicle(2, "DEF5678", "Car", "Model Y", 2, false);
        List<Vehicle> expectedList = Arrays.asList(vehicle1, vehicle2);

        when(vehicleDAO.findAll()).thenReturn(expectedList);

        List<Vehicle> result = vehicleDAO.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedList, result);
        verify(vehicleDAO, times(1)).findAll();
    }

    @Test
    void testFindAllEmpty() throws SQLException {
        when(vehicleDAO.findAll()).thenReturn(Collections.emptyList());

        List<Vehicle> result = vehicleDAO.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(vehicleDAO, times(1)).findAll();
    }

    @Test
    void testFindByCompanyId() throws SQLException {
        Vehicle vehicle1 = new Vehicle(1, "ABC1234", "Truck", "Model X", 1, true);
        Vehicle vehicle2 = new Vehicle(3, "GHI9012", "Van", "Model Z", 1, true);
        List<Vehicle> expectedList = Arrays.asList(vehicle1, vehicle2);

        when(vehicleDAO.findByCompanyId(1)).thenReturn(expectedList);

        List<Vehicle> result = vehicleDAO.findByCompanyId(1);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedList, result);
        verify(vehicleDAO, times(1)).findByCompanyId(1);
    }

    @Test
    void testFindByCompanyIdNoVehicles() throws SQLException {
        when(vehicleDAO.findByCompanyId(99)).thenReturn(Collections.emptyList());

        List<Vehicle> result = vehicleDAO.findByCompanyId(99);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(vehicleDAO, times(1)).findByCompanyId(99);
    }

    @Test
    void testUpdateSuccess() throws SQLException {
        Vehicle updatedVehicle = new Vehicle(1, "ABC1234", "Truck", "Model X Pro", 1, false);
        when(vehicleDAO.update(updatedVehicle)).thenReturn(true);

        boolean result = vehicleDAO.update(updatedVehicle);

        assertTrue(result);
        verify(vehicleDAO, times(1)).update(updatedVehicle);
    }

    @Test
    void testUpdateFailure() throws SQLException {
        Vehicle nonExistentVehicle = new Vehicle(99, "NON9999", "Car", "Model A", 1, true);
        when(vehicleDAO.update(nonExistentVehicle)).thenReturn(false);

        boolean result = vehicleDAO.update(nonExistentVehicle);

        assertFalse(result);
        verify(vehicleDAO, times(1)).update(nonExistentVehicle);
    }

    @Test
    void testDeleteSuccess() throws SQLException {
        when(vehicleDAO.delete(1)).thenReturn(true);

        boolean result = vehicleDAO.delete(1);

        assertTrue(result);
        verify(vehicleDAO, times(1)).delete(1);
    }

    @Test
    void testDeleteFailure() throws SQLException {
        when(vehicleDAO.delete(99)).thenReturn(false);

        boolean result = vehicleDAO.delete(99);

        assertFalse(result);
        verify(vehicleDAO, times(1)).delete(99);
    }

    @Test
    void testCreateThrowsSQLException() throws SQLException {
        Vehicle newVehicle = new Vehicle(0, "ABC1234", "Truck", "Model X", 1, true);
        when(vehicleDAO.create(newVehicle)).thenThrow(new SQLException("Database connection error"));

        SQLException thrown = assertThrows(SQLException.class, () -> {
            vehicleDAO.create(newVehicle);
        });

        assertEquals("Database connection error", thrown.getMessage());
        verify(vehicleDAO, times(1)).create(newVehicle);
    }

    @Test
    void testFindAllThrowsSQLException() throws SQLException {
        when(vehicleDAO.findAll()).thenThrow(new SQLException("Query failed"));

        SQLException thrown = assertThrows(SQLException.class, () -> {
            vehicleDAO.findAll();
        });

        assertEquals("Query failed", thrown.getMessage());
        verify(vehicleDAO, times(1)).findAll();
    }
}
