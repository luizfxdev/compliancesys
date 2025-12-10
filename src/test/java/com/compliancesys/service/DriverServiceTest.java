package com.compliancesys.service;

import com.compliancesys.dao.DriverDAO;
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.Driver;
import com.compliancesys.service.impl.DriverServiceImpl;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.Mockito.*;

class DriverServiceTest {

    @Mock
    private DriverDAO driverDAO;

    @InjectMocks
    private DriverServiceImpl driverService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateDriverSuccess() throws BusinessException, SQLException {
        Driver newDriver = new Driver(0, 1, "João Silva", "12345678901", "ABC12345678", "D",
                LocalDate.of(2028, 12, 31), LocalDate.of(1990, 5, 15));
        Driver expectedDriver = new Driver(1, 1, "João Silva", "12345678901", "ABC12345678", "D",
                LocalDate.of(2028, 12, 31), LocalDate.of(1990, 5, 15), "999999999", "joao@example.com",
                LocalDateTime.now(), LocalDateTime.now());

        when(driverDAO.findByCpf(newDriver.getCpf())).thenReturn(Optional.empty());
        when(driverDAO.findByLicenseNumber(newDriver.getLicenseNumber())).thenReturn(Optional.empty());
        when(driverDAO.create(any(Driver.class))).thenReturn(expectedDriver.getId());

        Driver createdDriver = driverService.createDriver(newDriver);

        assertNotNull(createdDriver);
        assertEquals(expectedDriver.getId(), createdDriver.getId());
        assertEquals(expectedDriver.getCpf(), createdDriver.getCpf());
        verify(driverDAO, times(1)).create(any(Driver.class));
    }

    @Test
    void testCreateDriverInvalidCpf() throws SQLException {
        Driver newDriver = new Driver(0, 1, "João Silva", "123", "ABC12345678", "D",
                LocalDate.of(2028, 12, 31), LocalDate.of(1990, 5, 15));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            driverService.createDriver(newDriver);
        });
        assertEquals("CPF inválido", exception.getMessage());
        verify(driverDAO, never()).create(any(Driver.class));
    }

    @Test
    void testCreateDriverCpfAlreadyExists() throws SQLException {
        Driver existingDriver = new Driver(1, 1, "João Silva", "12345678901", "ABC12345678", "D",
                LocalDate.of(2028, 12, 31), LocalDate.of(1990, 5, 15));
        Driver newDriver = new Driver(0, 1, "João Silva", "12345678901", "DEF98765432", "D",
                LocalDate.of(2028, 12, 31), LocalDate.of(1990, 5, 15));

        when(driverDAO.findByCpf(newDriver.getCpf())).thenReturn(Optional.of(existingDriver));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            driverService.createDriver(newDriver);
        });
        assertEquals("CPF já cadastrado", exception.getMessage());
        verify(driverDAO, never()).create(any(Driver.class));
    }

    @Test
    void testGetDriverByIdFound() throws SQLException, BusinessException {
        int driverId = 1;
        Driver expectedDriver = new Driver(driverId, 1, "Maria Souza", "98765432109", "DEF98765432", "C",
                LocalDate.of(2027, 11, 20), LocalDate.of(1985, 8, 22));

        when(driverDAO.findById(driverId)).thenReturn(Optional.of(expectedDriver));

        Optional<Driver> foundDriver = driverService.getDriverById(driverId);

        assertTrue(foundDriver.isPresent());
        assertEquals(expectedDriver.getName(), foundDriver.get().getName());
        verify(driverDAO, times(1)).findById(driverId);
    }

    @Test
    void testGetDriverByIdNotFound() throws SQLException, BusinessException {
        int driverId = 99;
        when(driverDAO.findById(driverId)).thenReturn(Optional.empty());

        Optional<Driver> foundDriver = driverService.getDriverById(driverId);

        assertFalse(foundDriver.isPresent());
        verify(driverDAO, times(1)).findById(driverId);
    }

    @Test
    void testGetAllDrivers() throws SQLException, BusinessException {
        Driver driver1 = new Driver(1, 1, "João Silva", "12345678901", "ABC12345678", "D",
                LocalDate.of(2028, 12, 31), LocalDate.of(1990, 5, 15));
        Driver driver2 = new Driver(2, 1, "Maria Souza", "98765432109", "DEF98765432", "C",
                LocalDate.of(2027, 11, 20), LocalDate.of(1985, 8, 22));
        List<Driver> expectedDrivers = Arrays.asList(driver1, driver2);

        when(driverDAO.findAll()).thenReturn(expectedDrivers);

        List<Driver> actualDrivers = driverService.getAllDrivers();

        assertFalse(actualDrivers.isEmpty());
        assertEquals(2, actualDrivers.size());
        verify(driverDAO, times(1)).findAll(); // CORRIGIDO: Removido o parêntese extra
    }

    @Test
    void testGetAllDriversEmpty() throws SQLException, BusinessException {
        when(driverDAO.findAll()).thenReturn(Collections.emptyList());

        List<Driver> actualDrivers = driverService.getAllDrivers();

        assertTrue(actualDrivers.isEmpty());
        verify(driverDAO, times(1)).findAll(); // CORRIGIDO: Removido o parêntese extra
    }

    @Test
    void testUpdateDriverSuccess() throws BusinessException, SQLException {
        Driver existingDriver = new Driver(1, 1, "João Silva", "12345678901", "ABC12345678", "D",
                LocalDate.of(2028, 12, 31), LocalDate.of(1990, 5, 15));
        Driver updatedDriver = new Driver(1, 1, "João Silva Atualizado", "12345678901", "ABC12345678", "D",
                LocalDate.of(2029, 1, 1), LocalDate.of(1990, 5, 15));

        when(driverDAO.findById(updatedDriver.getId())).thenReturn(Optional.of(existingDriver));
        when(driverDAO.update(any(Driver.class))).thenReturn(true);

        Driver result = driverService.updateDriver(updatedDriver);

        assertNotNull(result);
        assertEquals("João Silva Atualizado", result.getName());
        verify(driverDAO, times(1)).update(any(Driver.class));
    }

    @Test
    void testUpdateDriverNotFound() throws SQLException {
        Driver nonExistentDriver = new Driver(99, 1, "Driver Inexistente", "11122233344", "XYZ00000000", "B",
                LocalDate.of(2026, 6, 1), LocalDate.of(1995, 1, 1));

        when(driverDAO.findById(nonExistentDriver.getId())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            driverService.updateDriver(nonExistentDriver);
        });
        assertEquals("Motorista não encontrado para atualização.", exception.getMessage());
        verify(driverDAO, never()).update(any(Driver.class));
    }

    @Test
    void testDeleteDriverSuccess() throws SQLException, BusinessException {
        int driverId = 1;
        Driver driverToDelete = new Driver(driverId, 1, "João Silva", "12345678901", "ABC12345678", "D",
                LocalDate.of(2028, 12, 31), LocalDate.of(1990, 5, 15));

        when(driverDAO.findById(driverId)).thenReturn(Optional.of(driverToDelete));
        when(driverDAO.delete(driverId)).thenReturn(true);

        boolean result = driverService.deleteDriver(driverId);

        assertTrue(result);
        verify(driverDAO, times(1)).delete(driverId);
    }

    @Test
    void testDeleteDriverNotFound() throws SQLException {
        int driverId = 99;
        when(driverDAO.findById(driverId)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            driverService.deleteDriver(driverId);
        });
        assertEquals("Motorista não encontrado para exclusão.", exception.getMessage());
        verify(driverDAO, never()).delete(driverId);
    }
}
