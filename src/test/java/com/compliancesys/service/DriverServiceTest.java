package com.compliancesys.service;

import com.compliancesys.dao.DriverDAO;
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.Driver;
import com.compliancesys.service.impl.DriverServiceImpl;
import com.compliancesys.util.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.time.LocalDate;
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
class DriverServiceTest {

    @Mock
    private DriverDAO driverDAO;

    @Mock
    private Validator validator;

    @InjectMocks
    private DriverServiceImpl driverService;

    private Driver testDriver;

    @BeforeEach
    void setUp() {
        testDriver = new Driver();
        testDriver.setId(1);
        testDriver.setCompanyId(1);
        testDriver.setName("João Silva");
        testDriver.setCpf("123.456.789-00");
        testDriver.setLicenseNumber("12345678901");
        testDriver.setLicenseCategory("C");
        testDriver.setLicenseExpiration(LocalDate.now().plusYears(2));
        testDriver.setBirthDate(LocalDate.of(1985, 5, 15));
        testDriver.setPhone("11999998888");
        testDriver.setEmail("joao.silva@email.com");
    }

    @Test
    @DisplayName("Deve registrar motorista com sucesso")
    void registerDriver_Success() throws SQLException, BusinessException {
        doNothing().when(validator).validate(any(Driver.class));
        when(driverDAO.findByCpf(anyString())).thenReturn(Optional.empty());
        when(driverDAO.findByLicenseNumber(anyString())).thenReturn(Optional.empty());
        when(driverDAO.findByEmail(anyString())).thenReturn(Optional.empty());
        when(driverDAO.create(any(Driver.class))).thenReturn(1);

        Driver result = driverService.registerDriver(testDriver);

        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(driverDAO).create(any(Driver.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao registrar motorista nulo")
    void registerDriver_NullDriver() throws SQLException {
        assertThrows(BusinessException.class, () -> driverService.registerDriver(null));
        verify(driverDAO, never()).create(any(Driver.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao registrar motorista com CPF duplicado")
    void registerDriver_DuplicateCpf() throws SQLException {
        doNothing().when(validator).validate(any(Driver.class));
        when(driverDAO.findByCpf(anyString())).thenReturn(Optional.of(testDriver));

        assertThrows(BusinessException.class, () -> driverService.registerDriver(testDriver));
        verify(driverDAO, never()).create(any(Driver.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao registrar motorista com CNH duplicada")
    void registerDriver_DuplicateLicense() throws SQLException {
        doNothing().when(validator).validate(any(Driver.class));
        when(driverDAO.findByCpf(anyString())).thenReturn(Optional.empty());
        when(driverDAO.findByLicenseNumber(anyString())).thenReturn(Optional.of(testDriver));

        assertThrows(BusinessException.class, () -> driverService.registerDriver(testDriver));
        verify(driverDAO, never()).create(any(Driver.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao registrar motorista com email duplicado")
    void registerDriver_DuplicateEmail() throws SQLException {
        doNothing().when(validator).validate(any(Driver.class));
        when(driverDAO.findByCpf(anyString())).thenReturn(Optional.empty());
        when(driverDAO.findByLicenseNumber(anyString())).thenReturn(Optional.empty());
        when(driverDAO.findByEmail(anyString())).thenReturn(Optional.of(testDriver));

        assertThrows(BusinessException.class, () -> driverService.registerDriver(testDriver));
        verify(driverDAO, never()).create(any(Driver.class));
    }

    @Test
    @DisplayName("Deve atualizar motorista com sucesso")
    void updateDriver_Success() throws SQLException, BusinessException {
        doNothing().when(validator).validate(any(Driver.class));
        when(driverDAO.findById(1)).thenReturn(Optional.of(testDriver));
        when(driverDAO.findByCpf(anyString())).thenReturn(Optional.of(testDriver));
        when(driverDAO.findByLicenseNumber(anyString())).thenReturn(Optional.of(testDriver));
        when(driverDAO.findByEmail(anyString())).thenReturn(Optional.of(testDriver));
        when(driverDAO.update(any(Driver.class))).thenReturn(true);

        Driver result = driverService.updateDriver(testDriver);

        assertNotNull(result);
        verify(driverDAO).update(any(Driver.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar motorista nulo")
    void updateDriver_NullDriver() throws SQLException {
        assertThrows(BusinessException.class, () -> driverService.updateDriver(null));
        verify(driverDAO, never()).update(any(Driver.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar motorista com ID inválido")
    void updateDriver_InvalidId() throws SQLException {
        testDriver.setId(0);
        assertThrows(BusinessException.class, () -> driverService.updateDriver(testDriver));
        verify(driverDAO, never()).update(any(Driver.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar motorista inexistente")
    void updateDriver_NotFound() throws SQLException {
        doNothing().when(validator).validate(any(Driver.class));
        when(driverDAO.findById(1)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> driverService.updateDriver(testDriver));
        verify(driverDAO, never()).update(any(Driver.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar com CPF de outro motorista")
    void updateDriver_CpfBelongsToAnother() throws SQLException {
        Driver anotherDriver = new Driver();
        anotherDriver.setId(2);
        anotherDriver.setCpf("123.456.789-00");

        doNothing().when(validator).validate(any(Driver.class));
        when(driverDAO.findById(1)).thenReturn(Optional.of(testDriver));
        when(driverDAO.findByCpf(anyString())).thenReturn(Optional.of(anotherDriver));

        assertThrows(BusinessException.class, () -> driverService.updateDriver(testDriver));
        verify(driverDAO, never()).update(any(Driver.class));
    }

    @Test
    @DisplayName("Deve deletar motorista com sucesso")
    void deleteDriver_Success() throws SQLException, BusinessException {
        when(driverDAO.findById(1)).thenReturn(Optional.of(testDriver));
        when(driverDAO.delete(1)).thenReturn(true);

        boolean result = driverService.deleteDriver(1);

        assertTrue(result);
        verify(driverDAO).delete(1);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar motorista com ID inválido")
    void deleteDriver_InvalidId() throws SQLException {
        assertThrows(BusinessException.class, () -> driverService.deleteDriver(0));
        verify(driverDAO, never()).delete(anyInt());
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar motorista inexistente")
    void deleteDriver_NotFound() throws SQLException {
        when(driverDAO.findById(999)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> driverService.deleteDriver(999));
        verify(driverDAO, never()).delete(anyInt());
    }

    @Test
    @DisplayName("Deve buscar motorista por ID")
    void getDriverById_Success() throws SQLException {
        when(driverDAO.findById(1)).thenReturn(Optional.of(testDriver));

        Optional<Driver> result = driverService.getDriverById(1);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar motorista com ID inválido")
    void getDriverById_InvalidId() throws SQLException {
        Optional<Driver> result = driverService.getDriverById(0);

        assertFalse(result.isPresent());
        verify(driverDAO, never()).findById(anyInt());
    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar motorista inexistente")
    void getDriverById_NotFound() throws SQLException {
        when(driverDAO.findById(999)).thenReturn(Optional.empty());

        Optional<Driver> result = driverService.getDriverById(999);

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Deve listar todos os motoristas")
    void getAllDrivers_Success() throws SQLException {
        Driver driver2 = new Driver();
        driver2.setId(2);
        driver2.setName("Maria Santos");

        when(driverDAO.findAll()).thenReturn(Arrays.asList(testDriver, driver2));

        List<Driver> result = driverService.getAllDrivers();

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há motoristas")
    void getAllDrivers_Empty() throws SQLException {
        when(driverDAO.findAll()).thenReturn(new ArrayList<>());

        List<Driver> result = driverService.getAllDrivers();

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Deve buscar motorista por CPF")
    void getDriverByCpf_Success() throws SQLException {
        when(driverDAO.findByCpf("123.456.789-00")).thenReturn(Optional.of(testDriver));

        Optional<Driver> result = driverService.getDriverByCpf("123.456.789-00");

        assertTrue(result.isPresent());
        assertEquals("123.456.789-00", result.get().getCpf());
    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar motorista com CPF nulo")
    void getDriverByCpf_NullCpf() throws SQLException {
        Optional<Driver> result = driverService.getDriverByCpf(null);

        assertFalse(result.isPresent());
        verify(driverDAO, never()).findByCpf(anyString());
    }

    @Test
    @DisplayName("Deve buscar motorista por número da CNH")
    void getDriverByLicenseNumber_Success() throws SQLException {
        when(driverDAO.findByLicenseNumber("12345678901")).thenReturn(Optional.of(testDriver));

        Optional<Driver> result = driverService.getDriverByLicenseNumber("12345678901");

        assertTrue(result.isPresent());
        assertEquals("12345678901", result.get().getLicenseNumber());
    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar motorista com CNH nula")
    void getDriverByLicenseNumber_NullLicense() throws SQLException {
        Optional<Driver> result = driverService.getDriverByLicenseNumber(null);

        assertFalse(result.isPresent());
        verify(driverDAO, never()).findByLicenseNumber(anyString());
    }

    @Test
    @DisplayName("Deve buscar motoristas por ID da empresa")
    void getDriversByCompanyId_Success() throws SQLException {
        Driver driver2 = new Driver();
        driver2.setId(2);
        driver2.setCompanyId(1);

        when(driverDAO.findByCompanyId(1)).thenReturn(Arrays.asList(testDriver, driver2));

        List<Driver> result = driverService.getDriversByCompanyId(1);

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Deve retornar lista vazia ao buscar motoristas com ID de empresa inválido")
    void getDriversByCompanyId_InvalidId() throws SQLException {
        List<Driver> result = driverService.getDriversByCompanyId(0);

        assertTrue(result.isEmpty());
        verify(driverDAO, never()).findByCompanyId(anyInt());
    }

    @Test
    @DisplayName("Deve buscar motorista por email")
    void getDriverByEmail_Success() throws SQLException {
        when(driverDAO.findByEmail("joao.silva@email.com")).thenReturn(Optional.of(testDriver));

        Optional<Driver> result = driverService.getDriverByEmail("joao.silva@email.com");

        assertTrue(result.isPresent());
        assertEquals("joao.silva@email.com", result.get().getEmail());
    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar motorista com email nulo")
    void getDriverByEmail_NullEmail() throws SQLException {
        Optional<Driver> result = driverService.getDriverByEmail(null);

        assertFalse(result.isPresent());
        verify(driverDAO, never()).findByEmail(anyString());
    }
}