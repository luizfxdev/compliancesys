package com.compliancesys.service;

import com.compliancesys.dao.JourneyDAO;
import com.compliancesys.dao.TimeRecordDAO;
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.Journey;
import com.compliancesys.model.TimeRecord;
import com.compliancesys.model.enums.ComplianceStatus;
import com.compliancesys.model.enums.EventType;
import com.compliancesys.service.impl.JourneyServiceImpl;
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
import static org.mockito.Mockito.*;

@DisplayName("Testes para JourneyService")
class JourneyServiceTest {

    @Mock
    private JourneyDAO journeyDAO;
    @Mock
    private TimeRecordDAO timeRecordDAO;
    @Mock
    private Validator validator;

    @InjectMocks
    private JourneyServiceImpl journeyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(validator.isValidId(anyInt())).thenReturn(true);
    }

    @Test
    @DisplayName("Deve criar uma jornada com sucesso")
    void shouldCreateJourneySuccessfully() throws BusinessException, SQLException {
        Journey journey = new Journey(1, LocalDate.now(), 480, 60, ComplianceStatus.CONFORME.name(), false);
        when(journeyDAO.create(any(Journey.class))).thenReturn(1);

        Journey createdJourney = journeyService.createJourney(journey);

        assertNotNull(createdJourney);
        assertEquals(1, createdJourney.getId());
        verify(journeyDAO, times(1)).create(journey);
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao criar jornada nula")
    void shouldThrowBusinessExceptionWhenCreatingNullJourney() {
        BusinessException exception = assertThrows(BusinessException.class, () ->
                journeyService.createJourney(null));
        assertEquals("Jornada não pode ser nula.", exception.getMessage());
        verify(journeyDAO, never()).create(any(Journey.class));
    }

    @Test
    @DisplayName("Deve retornar uma jornada por ID")
    void shouldReturnJourneyById() throws BusinessException, SQLException {
        int journeyId = 1;
        Journey expectedJourney = new Journey(journeyId, 1, LocalDate.now(), 480, 60, ComplianceStatus.CONFORME.name(), false, LocalDateTime.now(), LocalDateTime.now());
        when(journeyDAO.findById(journeyId)).thenReturn(Optional.of(expectedJourney));

        Optional<Journey> result = journeyService.getJourneyById(journeyId);

        assertTrue(result.isPresent());
        assertEquals(expectedJourney, result.get());
        verify(journeyDAO, times(1)).findById(journeyId);
    }

    @Test
    @DisplayName("Deve retornar Optional.empty se a jornada não for encontrada por ID")
    void shouldReturnEmptyOptionalWhenJourneyNotFoundById() throws BusinessException, SQLException {
        int journeyId = 1;
        when(journeyDAO.findById(journeyId)).thenReturn(Optional.empty());

        Optional<Journey> result = journeyService.getJourneyById(journeyId);

        assertFalse(result.isPresent());
        verify(journeyDAO, times(1)).findById(journeyId);
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao buscar jornada com ID inválido")
    void shouldThrowBusinessExceptionWhenGettingJourneyWithInvalidId() {
        when(validator.isValidId(anyInt())).thenReturn(false);
        BusinessException exception = assertThrows(BusinessException.class, () ->
                journeyService.getJourneyById(-1));
        assertEquals("ID da jornada inválido.", exception.getMessage());
        verify(journeyDAO, never()).findById(anyInt());
    }

    @Test
    @DisplayName("Deve retornar todas as jornadas")
    void shouldReturnAllJourneys() throws BusinessException, SQLException {
        Journey journey1 = new Journey(1, 1, LocalDate.now(), 480, 60, ComplianceStatus.CONFORME.name(), false, LocalDateTime.now(), LocalDateTime.now());
        Journey journey2 = new Journey(2, 2, LocalDate.now().plusDays(1), 400, 120, ComplianceStatus.NAO_CONFORME.name(), true, LocalDateTime.now(), LocalDateTime.now());
        List<Journey> expectedJourneys = Arrays.asList(journey1, journey2);
        when(journeyDAO.findAll()).thenReturn(expectedJourneys);

        List<Journey> result = journeyService.getAllJourneys();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedJourneys, result);
        verify(journeyDAO, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar lista vazia se não houver jornadas")
    void shouldReturnEmptyListIfNoJourneys() throws BusinessException, SQLException {
        when(journeyDAO.findAll()).thenReturn(Collections.emptyList());

        List<Journey> result = journeyService.getAllJourneys();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(journeyDAO, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao obter todas as jornadas com erro de SQL")
    void shouldThrowBusinessExceptionWhenGettingAllJourneysWithSqlError() throws SQLException {
        when(journeyDAO.findAll()).thenThrow(new SQLException("Erro ao buscar todas as jornadas"));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                journeyService.getAllJourneys());
        assertTrue(exception.getMessage().contains("Erro interno ao buscar todas as jornadas."));
        verify(journeyDAO, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve atualizar uma jornada com sucesso")
    void shouldUpdateJourneySuccessfully() throws BusinessException, SQLException {
        Journey updatedJourney = new Journey(1, 1, LocalDate.now(), 450, 90, ComplianceStatus.ALERTA.name(), false, LocalDateTime.now(), LocalDateTime.now());
        when(journeyDAO.update(any(Journey.class))).thenReturn(true);

        boolean result = journeyService.updateJourney(updatedJourney);

        assertTrue(result);
        verify(journeyDAO, times(1)).update(updatedJourney);
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao atualizar jornada nula")
    void shouldThrowBusinessExceptionWhenUpdatingNullJourney() {
        BusinessException exception = assertThrows(BusinessException.class, () ->
                journeyService.updateJourney(null));
        assertEquals("Jornada não pode ser nula.", exception.getMessage());
        verify(journeyDAO, never()).update(any(Journey.class));
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao atualizar jornada com ID inválido")
    void shouldThrowBusinessExceptionWhenUpdatingJourneyWithInvalidId() {
        when(validator.isValidId(anyInt())).thenReturn(false);
        Journey journey = new Journey(-1, 1, LocalDate.now(), 480, 60, ComplianceStatus.CONFORME.name(), false, LocalDateTime.now(), LocalDateTime.now());
        BusinessException exception = assertThrows(BusinessException.class, () ->
                journeyService.updateJourney(journey));
        assertEquals("ID da jornada inválido.", exception.getMessage());
        verify(journeyDAO, never()).update(any(Journey.class));
    }

    @Test
    @DisplayName("Deve deletar uma jornada com sucesso")
    void shouldDeleteJourneySuccessfully() throws BusinessException, SQLException {
        int journeyId = 1;
        when(journeyDAO.delete(journeyId)).thenReturn(true);

        boolean result = journeyService.deleteJourney(journeyId);

        assertTrue(result);
        verify(journeyDAO, times(1)).delete(journeyId);
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao deletar jornada com ID inválido")
    void shouldThrowBusinessExceptionWhenDeletingJourneyWithInvalidId() {
        when(validator.isValidId(anyInt())).thenReturn(false);
        BusinessException exception = assertThrows(BusinessException.class, () ->
                journeyService.deleteJourney(-1));
        assertEquals("ID da jornada inválido.", exception.getMessage());
        verify(journeyDAO, never()).delete(anyInt());
    }

    @Test
    @DisplayName("Deve calcular e auditar jornada com sucesso")
    void shouldCalculateAndAuditJourneySuccessfully() throws BusinessException, SQLException {
        int driverId = 1;
        LocalDate journeyDate = LocalDate.now();
        List<TimeRecord> timeRecords = Arrays.asList(
                new TimeRecord(1, driverId, 1, 1, LocalDateTime.now().minusHours(8), EventType.IN, "Inicio", "Notas", null, null),
                new TimeRecord(2, driverId, 1, 1, LocalDateTime.now().minusHours(4), EventType.OUT, "Parada", "Notas", null, null),
                new TimeRecord(3, driverId, 1, 1, LocalDateTime.now().minusHours(3), EventType.IN, "Retorno", "Notas", null, null),
                new TimeRecord(4, driverId, 1, 1, LocalDateTime.now(), EventType.OUT, "Fim", "Notas", null, null)
        );

        // Mock para findByDriverIdAndDate para simular que a jornada não existe e será criada
        when(journeyDAO.findByDriverIdAndDate(driverId, journeyDate)).thenReturn(Optional.empty());
        // Mock para create para retornar um ID para a nova jornada
        when(journeyDAO.create(any(Journey.class))).thenReturn(1);
        // Mock para update para simular a atualização da jornada
        when(journeyDAO.update(any(Journey.class))).thenReturn(true);

        Journey resultJourney = journeyService.calculateAndAuditJourney(driverId, timeRecords);

        assertNotNull(resultJourney);
        assertEquals(ComplianceStatus.CONFORME.name(), resultJourney.getComplianceStatus()); // Verifica o status de conformidade
        assertEquals(480, resultJourney.getTotalDrivingTimeMinutes()); // 8 horas de condução
        assertEquals(60, resultJourney.getTotalRestTimeMinutes()); // 1 hora de descanso
        assertFalse(resultJourney.isDailyLimitExceeded());
        verify(journeyDAO, times(1)).findByDriverIdAndDate(driverId, journeyDate);
        verify(journeyDAO, times(1)).create(any(Journey.class)); // Verifica que a jornada foi criada
        verify(journeyDAO, times(1)).update(any(Journey.class)); // Verifica que a jornada foi atualizada
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao calcular jornada com registros nulos")
    void shouldThrowBusinessExceptionWhenCalculatingJourneyWithNullRecords() {
        int driverId = 1;
        BusinessException exception = assertThrows(BusinessException.class, () ->
                journeyService.calculateAndAuditJourney(driverId, null));
        assertEquals("Registros de ponto não podem ser nulos ou vazios.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao calcular jornada com registros vazios")
    void shouldThrowBusinessExceptionWhenCalculatingJourneyWithEmptyRecords() {
        int driverId = 1;
        BusinessException exception = assertThrows(BusinessException.class, () ->
                journeyService.calculateAndAuditJourney(driverId, Collections.emptyList()));
        assertEquals("Registros de ponto não podem ser nulos ou vazios.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao calcular jornada com ID de motorista inválido")
    void shouldThrowBusinessExceptionWhenCalculatingJourneyWithInvalidDriverId() {
        when(validator.isValidId(anyInt())).thenReturn(false);
        int driverId = -1;
        List<TimeRecord> timeRecords = Arrays.asList(new TimeRecord());
        BusinessException exception = assertThrows(BusinessException.class, () ->
                journeyService.calculateAndAuditJourney(driverId, timeRecords));
        assertEquals("ID do motorista inválido.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao calcular jornada com erro de SQL")
    void shouldThrowBusinessExceptionWhenCalculatingJourneyWithSqlError() throws SQLException {
        int driverId = 1;
        LocalDate journeyDate = LocalDate.now();
        List<TimeRecord> timeRecords = Arrays.asList(
                new TimeRecord(1, driverId, 1, 1, LocalDateTime.now().minusHours(8), EventType.IN, "Inicio", "Notas", null, null)
        );

        when(journeyDAO.findByDriverIdAndDate(driverId, journeyDate)).thenThrow(new SQLException("Erro de DB"));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                journeyService.calculateAndAuditJourney(driverId, timeRecords));
        assertTrue(exception.getMessage().contains("Erro interno ao calcular e auditar jornada."));
        verify(journeyDAO, times(1)).findByDriverIdAndDate(driverId, journeyDate);
        verify(journeyDAO, never()).create(any(Journey.class));
        verify(journeyDAO, never()).update(any(Journey.class));
    }
}
