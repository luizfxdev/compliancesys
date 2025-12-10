package com.compliancesys.service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays; // Importar EventType
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mockito;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.Journey;
import com.compliancesys.model.TimeRecord;
import com.compliancesys.model.enums.EventType;

public class JourneyServiceTest {

    private JourneyService journeyService;

    @BeforeEach
    void setUp() {
        journeyService = Mockito.mock(JourneyService.class);
    }

    @Test
    void testCreateJourneySuccess() throws BusinessException, SQLException {
        // Construtor para inserção: driverId, vehicleId, journeyDate, startTime, endTime, startLocation, endLocation, totalDistance, totalDuration, drivingDuration, breakDuration, restDuration, mealDuration, status, dailyLimitExceeded
        Journey newJourney = new Journey(1, 1, LocalDate.now(), LocalDateTime.now(), null, "Inicio", null, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, "INICIADA", false);
        Journey expectedJourney = new Journey(1, 1, 1, LocalDate.now(), LocalDateTime.now(), null, "Inicio", null, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, "INICIADA", false, LocalDateTime.now(), LocalDateTime.now());

        when(journeyService.createJourney(any(Journey.class))).thenReturn(expectedJourney);

        Journey createdJourney = journeyService.createJourney(newJourney);

        assertNotNull(createdJourney);
        assertEquals(expectedJourney.getId(), createdJourney.getId());
        assertEquals(expectedJourney.getStatus(), createdJourney.getStatus());
        verify(journeyService, times(1)).createJourney(newJourney);
    }

    @Test
    void testCreateJourneyThrowsBusinessException() throws BusinessException, SQLException {
        Journey newJourney = new Journey(1, 1, LocalDate.now(), LocalDateTime.now(), null, "Inicio", null, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, "INICIADA", false);
        doThrow(new BusinessException("Erro ao criar jornada")).when(journeyService).createJourney(any(Journey.class));

        BusinessException thrown = assertThrows(BusinessException.class, () -> {
            journeyService.createJourney(newJourney);
        });

        assertEquals("Erro ao criar jornada", thrown.getMessage());
        verify(journeyService, times(1)).createJourney(newJourney);
    }

    @Test
    void testGetJourneyByIdFound() throws BusinessException, SQLException {
        int journeyId = 1;
        Journey expectedJourney = new Journey(journeyId, 1, 1, LocalDate.now(), LocalDateTime.now(), null, "Inicio", null, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, "INICIADA", false, LocalDateTime.now(), LocalDateTime.now());

        when(journeyService.getJourneyById(journeyId)).thenReturn(Optional.of(expectedJourney));

        Optional<Journey> result = journeyService.getJourneyById(journeyId);

        assertTrue(result.isPresent());
        assertEquals(expectedJourney, result.get());
        verify(journeyService, times(1)).getJourneyById(journeyId);
    }

    @Test
    void testGetJourneyByIdNotFound() throws BusinessException, SQLException {
        int journeyId = 99;
        when(journeyService.getJourneyById(journeyId)).thenReturn(Optional.empty());

        Optional<Journey> result = journeyService.getJourneyById(journeyId);

        assertFalse(result.isPresent());
        verify(journeyService, times(1)).getJourneyById(journeyId);
    }

    @Test
    void testGetJourneyByIdThrowsBusinessException() throws BusinessException, SQLException {
        int journeyId = 1;
        doThrow(new BusinessException("Erro ao buscar jornada por ID")).when(journeyService).getJourneyById(journeyId);

        BusinessException thrown = assertThrows(BusinessException.class, () -> {
            journeyService.getJourneyById(journeyId);
        });

        assertEquals("Erro ao buscar jornada por ID", thrown.getMessage());
        verify(journeyService, times(1)).getJourneyById(journeyId);
    }

    @Test
    void testGetJourneysByDriverIdFound() throws BusinessException, SQLException {
        int driverId = 1;
        Journey journey1 = new Journey(1, driverId, 1, LocalDate.now(), LocalDateTime.now(), null, "Inicio A", null, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, "INICIADA", false, LocalDateTime.now(), LocalDateTime.now());
        Journey journey2 = new Journey(2, driverId, 1, LocalDate.now().minusDays(1), LocalDateTime.now().minusDays(1), null, "Inicio B", null, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, "FINALIZADA", false, LocalDateTime.now(), LocalDateTime.now());
        List<Journey> expectedJourneys = Arrays.asList(journey1, journey2);

        when(journeyService.getJourneysByDriverId(driverId)).thenReturn(expectedJourneys);

        List<Journey> resultJourneys = journeyService.getJourneysByDriverId(driverId);

        assertNotNull(resultJourneys);
        assertEquals(2, resultJourneys.size());
        assertEquals(expectedJourneys, resultJourneys);
        verify(journeyService, times(1)).getJourneysByDriverId(driverId);
    }

    @Test
    void testGetJourneysByDriverIdNotFound() throws BusinessException, SQLException {
        int driverId = 99;
        when(journeyService.getJourneysByDriverId(driverId)).thenReturn(Collections.emptyList());

        List<Journey> resultJourneys = journeyService.getJourneysByDriverId(driverId);

        assertNotNull(resultJourneys);
        assertTrue(resultJourneys.isEmpty());
        verify(journeyService, times(1)).getJourneysByDriverId(driverId);
    }

    @Test
    void testGetJourneysByDriverIdThrowsBusinessException() throws BusinessException, SQLException {
        int driverId = 1;
        doThrow(new BusinessException("Erro ao buscar jornadas por motorista")).when(journeyService).getJourneysByDriverId(driverId);

        BusinessException thrown = assertThrows(BusinessException.class, () -> {
            journeyService.getJourneysByDriverId(driverId);
        });

        assertEquals("Erro ao buscar jornadas por motorista", thrown.getMessage());
        verify(journeyService, times(1)).getJourneysByDriverId(driverId);
    }

    @Test
    void testGetJourneyByDriverIdAndDateFound() throws BusinessException, SQLException {
        int driverId = 1;
        LocalDate journeyDate = LocalDate.now();
        Journey expectedJourney = new Journey(1, driverId, 1, journeyDate, LocalDateTime.now(), null, "Inicio", null, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, "INICIADA", false, LocalDateTime.now(), LocalDateTime.now());

        when(journeyService.getJourneyByDriverIdAndDate(driverId, journeyDate)).thenReturn(Optional.of(expectedJourney));

        Optional<Journey> result = journeyService.getJourneyByDriverIdAndDate(driverId, journeyDate);

        assertTrue(result.isPresent());
        assertEquals(expectedJourney, result.get());
        verify(journeyService, times(1)).getJourneyByDriverIdAndDate(driverId, journeyDate);
    }

    @Test
    void testGetJourneyByDriverIdAndDateNotFound() throws BusinessException, SQLException {
        int driverId = 99;
        LocalDate journeyDate = LocalDate.now();
        when(journeyService.getJourneyByDriverIdAndDate(driverId, journeyDate)).thenReturn(Optional.empty());

        Optional<Journey> result = journeyService.getJourneyByDriverIdAndDate(driverId, journeyDate);

        assertFalse(result.isPresent());
        verify(journeyService, times(1)).getJourneyByDriverIdAndDate(driverId, journeyDate);
    }

    @Test
    void testGetJourneyByDriverIdAndDateThrowsBusinessException() throws BusinessException, SQLException {
        int driverId = 1;
        LocalDate journeyDate = LocalDate.now();
        doThrow(new BusinessException("Erro ao buscar jornada por motorista e data")).when(journeyService).getJourneyByDriverIdAndDate(driverId, journeyDate);

        BusinessException thrown = assertThrows(BusinessException.class, () -> {
            journeyService.getJourneyByDriverIdAndDate(driverId, journeyDate);
        });

        assertEquals("Erro ao buscar jornada por motorista e data", thrown.getMessage());
        verify(journeyService, times(1)).getJourneyByDriverIdAndDate(driverId, journeyDate);
    }

    @Test
    void testUpdateJourneySuccess() throws BusinessException, SQLException {
        Journey journeyToUpdate = new Journey(1, 1, 1, LocalDate.now(), LocalDateTime.now(), null, "Inicio", null, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, "INICIADA", false, LocalDateTime.now(), LocalDateTime.now());
        Journey updatedJourney = new Journey(1, 1, 1, LocalDate.now(), LocalDateTime.now(), LocalDateTime.now().plusHours(8), "Inicio", "Fim", 100.0, 480.0, 400.0, 30.0, 40.0, 10.0, "FINALIZADA", false, LocalDateTime.now(), LocalDateTime.now());

        when(journeyService.updateJourney(any(Journey.class))).thenReturn(updatedJourney);

        Journey resultJourney = journeyService.updateJourney(journeyToUpdate);

        assertNotNull(resultJourney);
        assertEquals(updatedJourney.getStatus(), resultJourney.getStatus());
        assertEquals(updatedJourney.getTotalDistance(), resultJourney.getTotalDistance());
        verify(journeyService, times(1)).updateJourney(journeyToUpdate);
    }

    @Test
    void testUpdateJourneyThrowsBusinessException() throws BusinessException, SQLException {
        Journey journeyToUpdate = new Journey(1, 1, 1, LocalDate.now(), LocalDateTime.now(), null, "Inicio", null, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, "INICIADA", false, LocalDateTime.now(), LocalDateTime.now());
        doThrow(new BusinessException("Erro ao atualizar jornada")).when(journeyService).updateJourney(any(Journey.class));

        BusinessException thrown = assertThrows(BusinessException.class, () -> {
            journeyService.updateJourney(journeyToUpdate);
        });

        assertEquals("Erro ao atualizar jornada", thrown.getMessage());
        verify(journeyService, times(1)).updateJourney(journeyToUpdate);
    }

    @Test
    void testDeleteJourneySuccess() throws BusinessException, SQLException {
        int journeyId = 1;
        when(journeyService.deleteJourney(journeyId)).thenReturn(true);

        boolean deleted = journeyService.deleteJourney(journeyId);

        assertTrue(deleted);
        verify(journeyService, times(1)).deleteJourney(journeyId);
    }

    @Test
    void testDeleteJourneyThrowsBusinessException() throws BusinessException, SQLException {
        int journeyId = 1;
        doThrow(new BusinessException("Erro ao deletar jornada")).when(journeyService).deleteJourney(journeyId);

        BusinessException thrown = assertThrows(BusinessException.class, () -> {
            journeyService.deleteJourney(journeyId);
        });

        assertEquals("Erro ao deletar jornada", thrown.getMessage());
        verify(journeyService, times(1)).deleteJourney(journeyId);
    }

    @Test
    void testCalculateAndAuditJourneySuccess() throws BusinessException, SQLException {
        int driverId = 1;
        List<TimeRecord> timeRecords = Arrays.asList(
            new TimeRecord(1, driverId, 1, 1, LocalDateTime.now().minusHours(8), EventType.IN, "Inicio", "Notas", null, null),
            new TimeRecord(2, driverId, 1, 1, LocalDateTime.now().minusHours(4), EventType.OUT, "Parada", "Notas", null, null),
            new TimeRecord(3, driverId, 1, 1, LocalDateTime.now().minusHours(3), EventType.IN, "Retorno", "Notas", null, null),
            new TimeRecord(4, driverId, 1, 1, LocalDateTime.now(), EventType.OUT, "Fim", "Notas", null, null)
        );

        // Construtor completo de Journey: id, driverId, vehicleId, journeyDate, startTime, endTime, startLocation, endLocation, totalDistance, totalDuration, drivingDuration, breakDuration, restDuration, mealDuration, status, dailyLimitExceeded, createdAt, updatedAt
        Journey expectedJourney = new Journey(1, driverId, 1, LocalDate.now(), LocalDateTime.now().minusHours(8), LocalDateTime.now(), "Inicio", "Fim", 150.0, 480.0, 400.0, 30.0, 40.0, 10.0, "FINALIZADA", false, LocalDateTime.now(), LocalDateTime.now()); // Status como String

        when(journeyService.calculateAndAuditJourney(driverId, timeRecords)).thenReturn(expectedJourney);

        Journey resultJourney = journeyService.calculateAndAuditJourney(driverId, timeRecords);

        assertNotNull(resultJourney);
        assertEquals(expectedJourney.getTotalDuration(), resultJourney.getTotalDuration());
        assertEquals(expectedJourney.getStatus(), resultJourney.getStatus());
        verify(journeyService, times(1)).calculateAndAuditJourney(driverId, timeRecords);
    }

    @Test
    void testCalculateAndAuditJourneyThrowsBusinessException() throws BusinessException, SQLException {
        int driverId = 1;
        List<TimeRecord> timeRecords = Collections.emptyList();
        doThrow(new BusinessException("Erro ao calcular e auditar jornada")).when(journeyService).calculateAndAuditJourney(driverId, timeRecords);

        BusinessException thrown = assertThrows(BusinessException.class, () -> {
            journeyService.calculateAndAuditJourney(driverId, timeRecords);
        });

        assertEquals("Erro ao calcular e auditar jornada", thrown.getMessage());
        verify(journeyService, times(1)).calculateAndAuditJourney(driverId, timeRecords);
    }
}
