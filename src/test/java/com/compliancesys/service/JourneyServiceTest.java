package com.compliancesys.service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.compliancesys.dao.DriverDAO;
import com.compliancesys.dao.JourneyDAO;
import com.compliancesys.dao.TimeRecordDAO;
import com.compliancesys.dao.VehicleDAO;
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.Driver;
import com.compliancesys.model.Journey;
import com.compliancesys.model.TimeRecord;
import com.compliancesys.model.Vehicle;
import com.compliancesys.service.impl.JourneyServiceImpl;
import com.compliancesys.util.TimeUtil;
import com.compliancesys.util.Validator;

@ExtendWith(MockitoExtension.class)
class JourneyServiceTest {

    @Mock
    private JourneyDAO journeyDAO;

    @Mock
    private DriverDAO driverDAO;

    @Mock
    private VehicleDAO vehicleDAO;

    @Mock
    private TimeRecordDAO timeRecordDAO;

    @Mock
    private Validator validator;

    @Mock
    private TimeUtil timeUtil;

    private JourneyServiceImpl journeyService;

    private Journey testJourney;
    private Driver testDriver;
    private Vehicle testVehicle;

    @BeforeEach
    void setUp() {
        journeyService = new JourneyServiceImpl(journeyDAO, driverDAO, vehicleDAO, timeRecordDAO, validator, timeUtil);

        testDriver = new Driver();
        testDriver.setId(1);
        testDriver.setName("Motorista Teste");

        testVehicle = new Vehicle();
        testVehicle.setId(1);
        testVehicle.setPlate("ABC1234");

        testJourney = new Journey();
        testJourney.setId(1);
        testJourney.setDriverId(1);
        testJourney.setVehicleId(1);
        testJourney.setCompanyId(1);
        testJourney.setJourneyDate(LocalDate.now());
        testJourney.setStartLocation("São Paulo, SP");
        testJourney.setComplianceStatus("PENDING");
    }

    @Test
    @DisplayName("Deve criar jornada com sucesso")
    void createJourney_Success() throws SQLException, BusinessException {
        when(validator.isValidId(anyInt())).thenReturn(true);
        when(driverDAO.findById(1)).thenReturn(Optional.of(testDriver));
        when(vehicleDAO.findById(1)).thenReturn(Optional.of(testVehicle));
        when(journeyDAO.findByDriverIdAndDate(anyInt(), any(LocalDate.class))).thenReturn(Optional.empty());
        when(journeyDAO.create(any(Journey.class))).thenReturn(1);

        Journey result = journeyService.createJourney(testJourney);

        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(journeyDAO).create(any(Journey.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar jornada nula")
    void createJourney_NullJourney() throws SQLException { // Adicionado throws SQLException
        assertThrows(BusinessException.class, () -> journeyService.createJourney(null));
        verify(journeyDAO, never()).create(any(Journey.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar jornada com motorista inválido")
    void createJourney_InvalidDriverId() throws SQLException {
        when(validator.isValidId(1)).thenReturn(false);

        assertThrows(BusinessException.class, () -> journeyService.createJourney(testJourney));
        verify(journeyDAO, never()).create(any(Journey.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar jornada com motorista inexistente")
    void createJourney_DriverNotFound() throws SQLException {
        when(validator.isValidId(anyInt())).thenReturn(true);
        when(driverDAO.findById(1)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> journeyService.createJourney(testJourney));
        verify(journeyDAO, never()).create(any(Journey.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar jornada com veículo inexistente")
    void createJourney_VehicleNotFound() throws SQLException {
        when(validator.isValidId(anyInt())).thenReturn(true);
        when(driverDAO.findById(1)).thenReturn(Optional.of(testDriver));
        when(vehicleDAO.findById(1)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> journeyService.createJourney(testJourney));
        verify(journeyDAO, never()).create(any(Journey.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar jornada duplicada para motorista na mesma data")
    void createJourney_DuplicateJourneyForDriver() throws SQLException {
        when(validator.isValidId(anyInt())).thenReturn(true);
        when(driverDAO.findById(1)).thenReturn(Optional.of(testDriver));
        when(vehicleDAO.findById(1)).thenReturn(Optional.of(testVehicle));
        when(journeyDAO.findByDriverIdAndDate(anyInt(), any(LocalDate.class))).thenReturn(Optional.of(testJourney));

        assertThrows(BusinessException.class, () -> journeyService.createJourney(testJourney));
        verify(journeyDAO, never()).create(any(Journey.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar jornada sem data")
    void createJourney_NullDate() throws SQLException {
        testJourney.setJourneyDate(null);
        when(validator.isValidId(anyInt())).thenReturn(true);

        assertThrows(BusinessException.class, () -> journeyService.createJourney(testJourney));
        verify(journeyDAO, never()).create(any(Journey.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar jornada sem local de início")
    void createJourney_NullStartLocation() throws SQLException {
        testJourney.setStartLocation(null);
        when(validator.isValidId(anyInt())).thenReturn(true);

        assertThrows(BusinessException.class, () -> journeyService.createJourney(testJourney));
        verify(journeyDAO, never()).create(any(Journey.class));
    }

    @Test
    @DisplayName("Deve buscar jornada por ID")
    void getJourneyById_Success() throws SQLException, BusinessException {
        when(validator.isValidId(1)).thenReturn(true);
        when(journeyDAO.findById(1)).thenReturn(Optional.of(testJourney));

        Optional<Journey> result = journeyService.getJourneyById(1);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar jornada com ID inválido")
    void getJourneyById_InvalidId() throws SQLException {
        when(validator.isValidId(0)).thenReturn(false);

        assertThrows(BusinessException.class, () -> journeyService.getJourneyById(0));
        verify(journeyDAO, never()).findById(anyInt());
    }

    @Test
    @DisplayName("Deve listar todas as jornadas")
    void getAllJourneys_Success() throws SQLException, BusinessException {
        Journey journey2 = new Journey();
        journey2.setId(2);

        when(journeyDAO.findAll()).thenReturn(Arrays.asList(testJourney, journey2));

        List<Journey> result = journeyService.getAllJourneys();

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Deve atualizar jornada com sucesso")
    void updateJourney_Success() throws SQLException, BusinessException {
        when(validator.isValidId(anyInt())).thenReturn(true);
        when(journeyDAO.findById(1)).thenReturn(Optional.of(testJourney));
        when(driverDAO.findById(1)).thenReturn(Optional.of(testDriver));
        when(vehicleDAO.findById(1)).thenReturn(Optional.of(testVehicle));
        when(journeyDAO.findByDriverIdAndDate(anyInt(), any(LocalDate.class))).thenReturn(Optional.of(testJourney));
        when(journeyDAO.update(any(Journey.class))).thenReturn(true);

        Journey result = journeyService.updateJourney(testJourney);

        assertNotNull(result);
        verify(journeyDAO).update(any(Journey.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar jornada nula")
    void updateJourney_NullJourney() throws SQLException { // Adicionado throws SQLException
        assertThrows(BusinessException.class, () -> journeyService.updateJourney(null));
        verify(journeyDAO, never()).update(any(Journey.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar jornada inexistente")
    void updateJourney_NotFound() throws SQLException {
        when(validator.isValidId(anyInt())).thenReturn(true);
        when(journeyDAO.findById(1)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> journeyService.updateJourney(testJourney));
        verify(journeyDAO, never()).update(any(Journey.class));
    }

    @Test
    @DisplayName("Deve deletar jornada com sucesso")
    void deleteJourney_Success() throws SQLException, BusinessException {
        when(validator.isValidId(1)).thenReturn(true);
        when(journeyDAO.findById(1)).thenReturn(Optional.of(testJourney));
        when(timeRecordDAO.findByJourneyId(1)).thenReturn(new ArrayList<>());
        when(journeyDAO.delete(1)).thenReturn(true);

        boolean result = journeyService.deleteJourney(1);

        assertTrue(result);
        verify(journeyDAO).delete(1);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar jornada com ID inválido")
    void deleteJourney_InvalidId() throws SQLException {
        when(validator.isValidId(0)).thenReturn(false);

        assertThrows(BusinessException.class, () -> journeyService.deleteJourney(0));
        verify(journeyDAO, never()).delete(anyInt());
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar jornada inexistente")
    void deleteJourney_NotFound() throws SQLException {
        when(validator.isValidId(999)).thenReturn(true);
        when(journeyDAO.findById(999)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> journeyService.deleteJourney(999));
        verify(journeyDAO, never()).delete(anyInt());
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar jornada com registros de tempo associados")
    void deleteJourney_HasTimeRecords() throws SQLException {
        TimeRecord timeRecord = new TimeRecord();
        timeRecord.setId(1);
        timeRecord.setJourneyId(1);

        when(validator.isValidId(1)).thenReturn(true);
        when(journeyDAO.findById(1)).thenReturn(Optional.of(testJourney));
        when(timeRecordDAO.findByJourneyId(1)).thenReturn(Arrays.asList(timeRecord));

        assertThrows(BusinessException.class, () -> journeyService.deleteJourney(1));
        verify(journeyDAO, never()).delete(anyInt());
    }

    @Test
    @DisplayName("Deve buscar jornadas por ID do motorista")
    void getJourneysByDriverId_Success() throws SQLException, BusinessException {
        when(validator.isValidId(1)).thenReturn(true);
        when(journeyDAO.findByDriverId(1)).thenReturn(Arrays.asList(testJourney));

        List<Journey> result = journeyService.getJourneysByDriverId(1);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar jornadas com ID de motorista inválido")
    void getJourneysByDriverId_InvalidId() throws SQLException {
        when(validator.isValidId(0)).thenReturn(false);

        assertThrows(BusinessException.class, () -> journeyService.getJourneysByDriverId(0));
        verify(journeyDAO, never()).findByDriverId(anyInt());
    }

    @Test
    @DisplayName("Deve buscar jornadas por ID do veículo")
    void getJourneysByVehicleId_Success() throws SQLException, BusinessException {
        when(validator.isValidId(1)).thenReturn(true);
        when(journeyDAO.findByVehicleId(1)).thenReturn(Arrays.asList(testJourney));

        List<Journey> result = journeyService.getJourneysByVehicleId(1);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Deve buscar jornadas por ID da empresa")
    void getJourneysByCompanyId_Success() throws SQLException, BusinessException {
        when(validator.isValidId(1)).thenReturn(true);
        when(journeyDAO.findByCompanyId(1)).thenReturn(Arrays.asList(testJourney));

        List<Journey> result = journeyService.getJourneysByCompanyId(1);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Deve buscar jornadas por intervalo de datas")
    void getJourneysByDateRange_Success() throws SQLException, BusinessException { // Adicionado throws SQLException
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();

        when(journeyDAO.findByDateRange(startDate, endDate)).thenReturn(Arrays.asList(testJourney));

        List<Journey> result = journeyService.getJourneysByDateRange(startDate, endDate);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar jornadas com intervalo de datas inválido")
    void getJourneysByDateRange_InvalidRange() throws SQLException { // Adicionado throws SQLException
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().minusDays(7);

        assertThrows(BusinessException.class, () -> journeyService.getJourneysByDateRange(startDate, endDate));
        verify(journeyDAO, never()).findByDateRange(any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    @DisplayName("Deve buscar jornada por motorista e data")
    void getJourneyByDriverIdAndDate_Success() throws SQLException, BusinessException {
        when(validator.isValidId(1)).thenReturn(true);
        when(journeyDAO.findByDriverIdAndDate(1, LocalDate.now())).thenReturn(Optional.of(testJourney));

        Optional<Journey> result = journeyService.getJourneyByDriverIdAndDate(1, LocalDate.now());

        assertTrue(result.isPresent());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar jornada por motorista e data com data nula")
    void getJourneyByDriverIdAndDate_NullDate() throws SQLException {
        when(validator.isValidId(1)).thenReturn(true);

        assertThrows(BusinessException.class, () -> journeyService.getJourneyByDriverIdAndDate(1, null));
        verify(journeyDAO, never()).findByDriverIdAndDate(anyInt(), any(LocalDate.class));
    }
}
