package com.compliancesys.service;

import com.compliancesys.dao.DriverDAO;
import com.compliancesys.dao.JourneyDAO;
import com.compliancesys.dao.TimeRecordDAO;
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.Driver;
import com.compliancesys.model.Journey;
import com.compliancesys.model.TimeRecord;
import com.compliancesys.model.enums.EventType;
import com.compliancesys.service.impl.TimeRecordServiceImpl;
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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TimeRecordServiceTest {

    @Mock
    private TimeRecordDAO timeRecordDAO;

    @Mock
    private DriverDAO driverDAO;

    @Mock
    private JourneyDAO journeyDAO;

    @Mock
    private Validator validator;

    @InjectMocks
    private TimeRecordServiceImpl timeRecordService;

    private Driver testDriver;
    private Journey testJourney;
    private TimeRecord testTimeRecord;

    @BeforeEach
    void setUp() {
        testDriver = new Driver();
        testDriver.setId(1);
        testDriver.setName("Motorista Teste");
        testDriver.setCpf("123.456.789-00");
        testDriver.setLicenseNumber("12345678901");
        testDriver.setBirthDate(LocalDate.of(1990, 1, 1));

        testJourney = new Journey();
        testJourney.setId(1);
        testJourney.setDriverId(1);
        testJourney.setJourneyDate(LocalDate.now());
        testJourney.setComplianceStatus("PENDING");

        testTimeRecord = new TimeRecord();
        testTimeRecord.setId(1);
        testTimeRecord.setDriverId(1);
        testTimeRecord.setJourneyId(1);
        testTimeRecord.setRecordTime(LocalDateTime.now());
        testTimeRecord.setEventType(EventType.START_DRIVE);
        testTimeRecord.setLocation("Local Teste");
    }

    @Test
    @DisplayName("Deve criar registro de tempo com sucesso")
    void createTimeRecord_Success() throws SQLException, BusinessException {
        doNothing().when(validator).validate(any(TimeRecord.class));
        when(driverDAO.findById(1)).thenReturn(Optional.of(testDriver));
        when(journeyDAO.findById(1)).thenReturn(Optional.of(testJourney));
        when(timeRecordDAO.create(any(TimeRecord.class))).thenReturn(1);

        TimeRecord result = timeRecordService.createTimeRecord(testTimeRecord);

        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(timeRecordDAO).create(any(TimeRecord.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar registro com motorista inexistente")
    void createTimeRecord_DriverNotFound() throws SQLException {
        doNothing().when(validator).validate(any(TimeRecord.class));
        when(driverDAO.findById(1)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> timeRecordService.createTimeRecord(testTimeRecord));
        verify(timeRecordDAO, never()).create(any(TimeRecord.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar registro com jornada inexistente")
    void createTimeRecord_JourneyNotFound() throws SQLException {
        doNothing().when(validator).validate(any(TimeRecord.class));
        when(driverDAO.findById(1)).thenReturn(Optional.of(testDriver));
        when(journeyDAO.findById(1)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> timeRecordService.createTimeRecord(testTimeRecord));
        verify(timeRecordDAO, never()).create(any(TimeRecord.class));
    }

    @Test
    @DisplayName("Deve atualizar registro de tempo com sucesso")
    void updateTimeRecord_Success() throws SQLException, BusinessException {
        doNothing().when(validator).validate(any(TimeRecord.class));
        when(timeRecordDAO.findById(1)).thenReturn(Optional.of(testTimeRecord));
        when(driverDAO.findById(1)).thenReturn(Optional.of(testDriver));
        when(journeyDAO.findById(1)).thenReturn(Optional.of(testJourney));
        when(timeRecordDAO.update(any(TimeRecord.class))).thenReturn(true);

        TimeRecord result = timeRecordService.updateTimeRecord(testTimeRecord);

        assertNotNull(result);
        verify(timeRecordDAO).update(any(TimeRecord.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar registro inexistente")
    void updateTimeRecord_NotFound() throws SQLException {
        doNothing().when(validator).validate(any(TimeRecord.class));
        when(timeRecordDAO.findById(1)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> timeRecordService.updateTimeRecord(testTimeRecord));
        verify(timeRecordDAO, never()).update(any(TimeRecord.class));
    }

    @Test
    @DisplayName("Deve deletar registro de tempo com sucesso")
    void deleteTimeRecord_Success() throws SQLException, BusinessException {
        when(timeRecordDAO.findById(1)).thenReturn(Optional.of(testTimeRecord));
        when(timeRecordDAO.delete(1)).thenReturn(true);

        boolean result = timeRecordService.deleteTimeRecord(1);

        assertTrue(result);
        verify(timeRecordDAO).delete(1);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar registro inexistente")
    void deleteTimeRecord_NotFound() throws SQLException {
        when(timeRecordDAO.findById(1)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> timeRecordService.deleteTimeRecord(1));
        verify(timeRecordDAO, never()).delete(anyInt());
    }

    @Test
    @DisplayName("Deve buscar registro por ID")
    void getTimeRecordById_Success() throws SQLException {
        when(timeRecordDAO.findById(1)).thenReturn(Optional.of(testTimeRecord));

        Optional<TimeRecord> result = timeRecordService.getTimeRecordById(1);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar registro inexistente")
    void getTimeRecordById_NotFound() throws SQLException {
        when(timeRecordDAO.findById(999)).thenReturn(Optional.empty());

        Optional<TimeRecord> result = timeRecordService.getTimeRecordById(999);

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Deve listar todos os registros")
    void getAllTimeRecords_Success() throws SQLException {
        TimeRecord record2 = new TimeRecord();
        record2.setId(2);
        record2.setDriverId(1);
        record2.setJourneyId(1);
        record2.setRecordTime(LocalDateTime.now());
        record2.setEventType(EventType.END_DRIVE);

        when(timeRecordDAO.findAll()).thenReturn(Arrays.asList(testTimeRecord, record2));

        List<TimeRecord> result = timeRecordService.getAllTimeRecords();

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Deve buscar registros por ID da jornada")
    void getTimeRecordsByJourneyId_Success() throws SQLException, BusinessException {
        when(journeyDAO.findById(1)).thenReturn(Optional.of(testJourney));
        when(timeRecordDAO.findByJourneyId(1)).thenReturn(Arrays.asList(testTimeRecord));

        List<TimeRecord> result = timeRecordService.getTimeRecordsByJourneyId(1);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar registros de jornada inexistente")
    void getTimeRecordsByJourneyId_JourneyNotFound() throws SQLException {
        when(journeyDAO.findById(999)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> timeRecordService.getTimeRecordsByJourneyId(999));
    }

    @Test
    @DisplayName("Deve buscar registros por ID do motorista")
    void getTimeRecordsByDriverId_Success() throws SQLException, BusinessException {
        when(driverDAO.findById(1)).thenReturn(Optional.of(testDriver));
        when(timeRecordDAO.findByDriverId(1)).thenReturn(Arrays.asList(testTimeRecord));

        List<TimeRecord> result = timeRecordService.getTimeRecordsByDriverId(1);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar registros de motorista inexistente")
    void getTimeRecordsByDriverId_DriverNotFound() throws SQLException {
        when(driverDAO.findById(999)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> timeRecordService.getTimeRecordsByDriverId(999));
    }

    @Test
    @DisplayName("Deve buscar registros por tipo de evento")
    void getTimeRecordsByEventType_Success() throws SQLException {
        when(timeRecordDAO.findByEventType(EventType.START_DRIVE)).thenReturn(Arrays.asList(testTimeRecord));

        List<TimeRecord> result = timeRecordService.getTimeRecordsByEventType(EventType.START_DRIVE);

        assertEquals(1, result.size());
        assertEquals(EventType.START_DRIVE, result.get(0).getEventType());
    }

    @Test
    @DisplayName("Deve buscar registros por intervalo de tempo")
    void getTimeRecordsByRecordTimeRange_Success() throws SQLException {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
        when(timeRecordDAO.findByRecordTimeRange(start, end)).thenReturn(Arrays.asList(testTimeRecord));

        List<TimeRecord> result = timeRecordService.getTimeRecordsByRecordTimeRange(start, end);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Deve buscar último registro por motorista e jornada")
    void getLatestTimeRecordByDriverAndJourney_Success() throws SQLException, BusinessException {
        when(driverDAO.findById(1)).thenReturn(Optional.of(testDriver));
        when(journeyDAO.findById(1)).thenReturn(Optional.of(testJourney));
        when(timeRecordDAO.findLatestByDriverIdAndJourneyId(1, 1)).thenReturn(Optional.of(testTimeRecord));

        Optional<TimeRecord> result = timeRecordService.getLatestTimeRecordByDriverAndJourney(1, 1);

        assertTrue(result.isPresent());
    }
}