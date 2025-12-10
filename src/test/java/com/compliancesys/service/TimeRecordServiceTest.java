package com.compliancesys.service;

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
import com.compliancesys.model.TimeRecord;
import com.compliancesys.model.enums.EventType;

public class TimeRecordServiceTest {

    private TimeRecordService timeRecordService;

    @BeforeEach
    void setUp() {
        timeRecordService = Mockito.mock(TimeRecordService.class);
    }

    @Test
    void testRegisterTimeRecordSuccess() throws BusinessException {
        TimeRecord newRecord = new TimeRecord(0, 1, 1, 1, LocalDateTime.now(), EventType.IN, "Local A", "Entrada", null, null);
        TimeRecord expectedRecord = new TimeRecord(1, 1, 1, 1, LocalDateTime.now(), EventType.IN, "Local A", "Entrada", LocalDateTime.now(), LocalDateTime.now());

        when(timeRecordService.registerTimeRecord(any(TimeRecord.class))).thenReturn(expectedRecord);

        TimeRecord result = timeRecordService.registerTimeRecord(newRecord);

        assertNotNull(result);
        assertEquals(expectedRecord.getId(), result.getId());
        assertEquals(expectedRecord.getEventType(), result.getEventType());
        verify(timeRecordService, times(1)).registerTimeRecord(newRecord);
    }

    @Test
    void testRegisterTimeRecordInvalidDriverId() throws BusinessException {
        TimeRecord newRecord = new TimeRecord(0, -1, 1, 1, LocalDateTime.now(), EventType.IN, "Local A", "Entrada", null, null);

        doThrow(new BusinessException("ID do motorista inválido")).when(timeRecordService).registerTimeRecord(any(TimeRecord.class));

        BusinessException thrown = assertThrows(BusinessException.class, () -> {
            timeRecordService.registerTimeRecord(newRecord);
        });

        assertEquals("ID do motorista inválido", thrown.getMessage());
        verify(timeRecordService, times(1)).registerTimeRecord(newRecord);
    }

    @Test
    void testGetTimeRecordByIdFound() throws BusinessException {
        int recordId = 1;
        TimeRecord expectedRecord = new TimeRecord(recordId, 1, 1, 1, LocalDateTime.now(), EventType.IN, "Local A", "Entrada", LocalDateTime.now(), LocalDateTime.now());

        // Corrigido: Usar getTimeRecordById
        when(timeRecordService.getTimeRecordById(recordId)).thenReturn(Optional.of(expectedRecord));

        // Corrigido: Usar getTimeRecordById
        Optional<TimeRecord> result = timeRecordService.getTimeRecordById(recordId);

        assertTrue(result.isPresent());
        assertEquals(expectedRecord, result.get());
        // Corrigido: Usar getTimeRecordById
        verify(timeRecordService, times(1)).getTimeRecordById(recordId);
    }

    @Test
    void testGetTimeRecordByIdNotFound() throws BusinessException {
        int recordId = 99;
        // Corrigido: Usar getTimeRecordById
        when(timeRecordService.getTimeRecordById(recordId)).thenReturn(Optional.empty());

        // Corrigido: Usar getTimeRecordById
        Optional<TimeRecord> result = timeRecordService.getTimeRecordById(recordId);

        assertFalse(result.isPresent());
        // Corrigido: Usar getTimeRecordById
        verify(timeRecordService, times(1)).getTimeRecordById(recordId);
    }

    @Test
    void testGetTimeRecordByIdThrowsBusinessException() throws BusinessException {
        int recordId = 1;
        // Corrigido: Usar getTimeRecordById
        doThrow(new BusinessException("Erro de banco de dados")).when(timeRecordService).getTimeRecordById(recordId);

        BusinessException thrown = assertThrows(BusinessException.class, () -> {
            // Corrigido: Usar getTimeRecordById
            timeRecordService.getTimeRecordById(recordId);
        });

        assertEquals("Erro de banco de dados", thrown.getMessage());
        // Corrigido: Usar getTimeRecordById
        verify(timeRecordService, times(1)).getTimeRecordById(recordId);
    }

    @Test
    void testGetAllTimeRecords() throws BusinessException {
        TimeRecord record1 = new TimeRecord(1, 1, 1, 1, LocalDateTime.now(), EventType.IN, "Local A", "Entrada", LocalDateTime.now(), LocalDateTime.now());
        TimeRecord record2 = new TimeRecord(2, 1, 1, 1, LocalDateTime.now().plusHours(1), EventType.OUT, "Local B", "Saída", LocalDateTime.now(), LocalDateTime.now());
        List<TimeRecord> expectedRecords = Arrays.asList(record1, record2);

        when(timeRecordService.getAllTimeRecords()).thenReturn(expectedRecords);

        List<TimeRecord> result = timeRecordService.getAllTimeRecords();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedRecords, result);
        verify(timeRecordService, times(1)).getAllTimeRecords();
    }

    @Test
    void testGetAllTimeRecordsEmpty() throws BusinessException {
        when(timeRecordService.getAllTimeRecords()).thenReturn(Collections.emptyList());

        List<TimeRecord> result = timeRecordService.getAllTimeRecords();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(timeRecordService, times(1)).getAllTimeRecords();
    }

    @Test
    void testGetAllTimeRecordsThrowsBusinessException() throws BusinessException {
        doThrow(new BusinessException("Erro de banco de dados")).when(timeRecordService).getAllTimeRecords();

        BusinessException thrown = assertThrows(BusinessException.class, () -> {
            timeRecordService.getAllTimeRecords();
        });

        assertEquals("Erro de banco de dados", thrown.getMessage());
        verify(timeRecordService, times(1)).getAllTimeRecords();
    }

    @Test
    void testUpdateTimeRecordSuccess() throws BusinessException {
        TimeRecord existingRecord = new TimeRecord(1, 1, 1, 1, LocalDateTime.now(), EventType.IN, "Local A", "Entrada", LocalDateTime.now(), LocalDateTime.now());
        TimeRecord updatedRecord = new TimeRecord(1, 1, 1, 1, LocalDateTime.now().plusMinutes(30), EventType.IN, "Local A", "Entrada Atualizada", LocalDateTime.now(), LocalDateTime.now());

        when(timeRecordService.updateTimeRecord(any(TimeRecord.class))).thenReturn(true);

        boolean result = timeRecordService.updateTimeRecord(updatedRecord);

        assertTrue(result);
        verify(timeRecordService, times(1)).updateTimeRecord(updatedRecord);
    }

    @Test
    void testUpdateTimeRecordNotFound() throws BusinessException {
        TimeRecord nonExistentRecord = new TimeRecord(99, 1, 1, 1, LocalDateTime.now(), EventType.IN, "Local X", "Notas", null, null);

        when(timeRecordService.updateTimeRecord(any(TimeRecord.class))).thenReturn(false);

        boolean result = timeRecordService.updateTimeRecord(nonExistentRecord);

        assertFalse(result);
        verify(timeRecordService, times(1)).updateTimeRecord(nonExistentRecord);
    }

    @Test
    void testUpdateTimeRecordThrowsBusinessException() throws BusinessException {
        TimeRecord recordToUpdate = new TimeRecord(1, 1, 1, 1, LocalDateTime.now(), EventType.IN, "Local A", "Entrada", null, null);

        doThrow(new BusinessException("Erro de banco de dados")).when(timeRecordService).updateTimeRecord(any(TimeRecord.class));

        BusinessException thrown = assertThrows(BusinessException.class, () -> {
            timeRecordService.updateTimeRecord(recordToUpdate);
        });

        assertEquals("Erro de banco de dados", thrown.getMessage());
        verify(timeRecordService, times(1)).updateTimeRecord(recordToUpdate);
    }

    @Test
    void testDeleteTimeRecordSuccess() throws BusinessException {
        int recordId = 1;
        when(timeRecordService.deleteTimeRecord(recordId)).thenReturn(true);

        boolean deleted = timeRecordService.deleteTimeRecord(recordId);

        assertTrue(deleted);
        verify(timeRecordService, times(1)).deleteTimeRecord(recordId);
    }

    @Test
    void testDeleteTimeRecordNotFound() throws BusinessException {
        int recordId = 99;
        when(timeRecordService.deleteTimeRecord(recordId)).thenReturn(false);

        boolean deleted = timeRecordService.deleteTimeRecord(recordId);

        assertFalse(deleted);
        verify(timeRecordService, times(1)).deleteTimeRecord(recordId);
    }

    @Test
    void testDeleteTimeRecordThrowsBusinessException() throws BusinessException {
        int recordId = 1;
        doThrow(new BusinessException("Erro de banco de dados")).when(timeRecordService).deleteTimeRecord(recordId);

        BusinessException thrown = assertThrows(BusinessException.class, () -> {
            timeRecordService.deleteTimeRecord(recordId);
        });

        assertEquals("Erro de banco de dados", thrown.getMessage());
        verify(timeRecordService, times(1)).deleteTimeRecord(recordId);
    }

    @Test
    void testGetTimeRecordsByDriverId() throws BusinessException {
        int driverId = 1;
        // Corrigido: Usar o construtor correto de TimeRecord e EventType enum
        List<TimeRecord> expectedRecords = Arrays.asList(
                new TimeRecord(1, driverId, 1, 1, LocalDateTime.now(), EventType.IN, "Loc A", "Notas", null, null),
                new TimeRecord(2, driverId, 1, 1, LocalDateTime.now().plusHours(1), EventType.OUT, "Loc B", "Notas", null, null)
        );

        // Corrigido: Usar getTimeRecordsByDriverId
        when(timeRecordService.getTimeRecordsByDriverId(driverId)).thenReturn(expectedRecords);

        // Corrigido: Usar getTimeRecordsByDriverId
        List<TimeRecord> foundRecords = timeRecordService.getTimeRecordsByDriverId(driverId);

        assertNotNull(foundRecords);
        assertEquals(2, foundRecords.size());
        assertEquals(expectedRecords, foundRecords);
        // Corrigido: Usar getTimeRecordsByDriverId
        verify(timeRecordService, times(1)).getTimeRecordsByDriverId(driverId);
    }

    @Test
    void testGetTimeRecordsByDriverIdEmpty() throws BusinessException {
        int driverId = 99;
        // Corrigido: Usar getTimeRecordsByDriverId
        when(timeRecordService.getTimeRecordsByDriverId(driverId)).thenReturn(Collections.emptyList());

        // Corrigido: Usar getTimeRecordsByDriverId
        List<TimeRecord> foundRecords = timeRecordService.getTimeRecordsByDriverId(driverId);

        assertNotNull(foundRecords);
        assertTrue(foundRecords.isEmpty());
        // Corrigido: Usar getTimeRecordsByDriverId
        verify(timeRecordService, times(1)).getTimeRecordsByDriverId(driverId);
    }

    @Test
    void testGetTimeRecordsByDriverIdThrowsBusinessException() throws BusinessException {
        int driverId = 1;
        // Corrigido: Usar getTimeRecordsByDriverId
        doThrow(new BusinessException("Erro de banco de dados")).when(timeRecordService).getTimeRecordsByDriverId(driverId);

        BusinessException thrown = assertThrows(BusinessException.class, () -> {
            // Corrigido: Usar getTimeRecordsByDriverId
            timeRecordService.getTimeRecordsByDriverId(driverId);
        });

        assertEquals("Erro de banco de dados", thrown.getMessage());
        // Corrigido: Usar getTimeRecordsByDriverId
        verify(timeRecordService, times(1)).getTimeRecordsByDriverId(driverId);
    }

    @Test
    void testGetTimeRecordsByDriverIdAndDate() throws BusinessException {
        int driverId = 1;
        LocalDate date = LocalDate.now();
        // Corrigido: Usar o construtor correto de TimeRecord e EventType enum
        List<TimeRecord> expectedRecords = Arrays.asList(
                new TimeRecord(1, driverId, 1, 1, LocalDateTime.now(), EventType.IN, "Loc A", "Notas", null, null),
                new TimeRecord(2, driverId, 1, 1, LocalDateTime.now().plusHours(1), EventType.OUT, "Loc B", "Notas", null, null)
        );

        when(timeRecordService.getTimeRecordsByDriverIdAndDate(driverId, date)).thenReturn(expectedRecords);

        List<TimeRecord> foundRecords = timeRecordService.getTimeRecordsByDriverIdAndDate(driverId, date);

        assertNotNull(foundRecords);
        assertEquals(2, foundRecords.size());
        assertEquals(expectedRecords, foundRecords);
        verify(timeRecordService, times(1)).getTimeRecordsByDriverIdAndDate(driverId, date);
    }

    @Test
    void testGetTimeRecordsByDriverIdAndDateEmpty() throws BusinessException {
        int driverId = 99;
        LocalDate date = LocalDate.now();

        when(timeRecordService.getTimeRecordsByDriverIdAndDate(driverId, date)).thenReturn(Collections.emptyList());

        List<TimeRecord> foundRecords = timeRecordService.getTimeRecordsByDriverIdAndDate(driverId, date);

        assertNotNull(foundRecords);
        assertTrue(foundRecords.isEmpty());
        verify(timeRecordService, times(1)).getTimeRecordsByDriverIdAndDate(driverId, date);
    }

    @Test
    void testGetTimeRecordsByDriverIdAndDateThrowsBusinessException() throws BusinessException {
        int driverId = 1;
        LocalDate date = LocalDate.now();

        doThrow(new BusinessException("Erro de banco de dados")).when(timeRecordService).getTimeRecordsByDriverIdAndDate(driverId, date);

        BusinessException thrown = assertThrows(BusinessException.class, () -> {
            timeRecordService.getTimeRecordsByDriverIdAndDate(driverId, date);
        });

        assertEquals("Erro de banco de dados", thrown.getMessage());
        verify(timeRecordService, times(1)).getTimeRecordsByDriverIdAndDate(driverId, date);
    }
}
