package com.compliancesys.service;

import com.compliancesys.model.TimeRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TimeRecordServiceTest {

    private TimeRecordService timeRecordService;

    @BeforeEach
    void setUp() {
        // Inicializa o mock do TimeRecordService antes de cada teste
        timeRecordService = Mockito.mock(TimeRecordService.class);
    }

    @Test
    void testRegisterTimeRecordSuccess() throws SQLException, IllegalArgumentException {
        TimeRecord newRecord = new TimeRecord(0, 1, LocalDateTime.now(), "IN", "Location A");
        when(timeRecordService.registerTimeRecord(newRecord)).thenReturn(1); // Simula o registro retornando um ID

        int id = timeRecordService.registerTimeRecord(newRecord);

        assertEquals(1, id);
        verify(timeRecordService, times(1)).registerTimeRecord(newRecord);
    }

    @Test
    void testRegisterTimeRecordInvalidInput() throws SQLException, IllegalArgumentException {
        TimeRecord invalidRecord = new TimeRecord(0, 1, null, "IN", "Location A"); // Data/Hora nula
        // Simula a exceção IllegalArgumentException para dados inválidos
        doThrow(new IllegalArgumentException("Timestamp não pode ser nulo")).when(timeRecordService).registerTimeRecord(invalidRecord);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            timeRecordService.registerTimeRecord(invalidRecord);
        });

        assertEquals("Timestamp não pode ser nulo", thrown.getMessage());
        verify(timeRecordService, times(1)).registerTimeRecord(invalidRecord);
    }

    @Test
    void testRegisterTimeRecordThrowsSQLException() throws SQLException, IllegalArgumentException {
        TimeRecord newRecord = new TimeRecord(0, 1, LocalDateTime.now(), "IN", "Location A");
        // Simula a exceção SQLException em caso de erro no banco de dados
        doThrow(new SQLException("Erro de conexão com o banco de dados")).when(timeRecordService).registerTimeRecord(newRecord);

        // AQUI ESTAVA O ERRO! A linha abaixo foi corrigida.
        SQLException thrown = assertThrows(SQLException.class, () -> {
            timeRecordService.registerTimeRecord(newRecord);
        });

        assertEquals("Erro de conexão com o banco de dados", thrown.getMessage());
        verify(timeRecordService, times(1)).registerTimeRecord(newRecord);
    }

    // --- Métodos de teste para findById ---
    @Test
    void testFindByIdSuccess() throws SQLException {
        TimeRecord expectedRecord = new TimeRecord(1, 1, LocalDateTime.now(), "IN", "Location A");
        when(timeRecordService.findById(1)).thenReturn(Optional.of(expectedRecord));

        Optional<TimeRecord> foundRecord = timeRecordService.findById(1);

        assertTrue(foundRecord.isPresent());
        assertEquals(expectedRecord, foundRecord.get());
        verify(timeRecordService, times(1)).findById(1);
    }

    @Test
    void testFindByIdNotFound() throws SQLException {
        when(timeRecordService.findById(99)).thenReturn(Optional.empty());

        Optional<TimeRecord> foundRecord = timeRecordService.findById(99);

        assertFalse(foundRecord.isPresent());
        verify(timeRecordService, times(1)).findById(99);
    }

    @Test
    void testFindByIdThrowsSQLException() throws SQLException {
        doThrow(new SQLException("Erro de banco de dados")).when(timeRecordService).findById(1);

        SQLException thrown = assertThrows(SQLException.class, () -> {
            timeRecordService.findById(1);
        });

        assertEquals("Erro de banco de dados", thrown.getMessage());
        verify(timeRecordService, times(1)).findById(1);
    }

    // --- Métodos de teste para findByDriverId ---
    @Test
    void testFindByDriverIdSuccess() throws SQLException {
        List<TimeRecord> expectedRecords = Arrays.asList(
                new TimeRecord(1, 1, LocalDateTime.now(), "IN", "Loc A"),
                new TimeRecord(2, 1, LocalDateTime.now().plusHours(1), "OUT", "Loc B")
        );
        when(timeRecordService.findByDriverId(1)).thenReturn(expectedRecords);

        List<TimeRecord> foundRecords = timeRecordService.findByDriverId(1);

        assertNotNull(foundRecords);
        assertFalse(foundRecords.isEmpty());
        assertEquals(2, foundRecords.size());
        assertEquals(expectedRecords, foundRecords);
        verify(timeRecordService, times(1)).findByDriverId(1);
    }

    @Test
    void testFindByDriverIdNotFound() throws SQLException {
        when(timeRecordService.findByDriverId(99)).thenReturn(Collections.emptyList());

        List<TimeRecord> foundRecords = timeRecordService.findByDriverId(99);

        assertNotNull(foundRecords);
        assertTrue(foundRecords.isEmpty());
        verify(timeRecordService, times(1)).findByDriverId(99);
    }

    @Test
    void testFindByDriverIdThrowsSQLException() throws SQLException {
        doThrow(new SQLException("Erro de banco de dados")).when(timeRecordService).findByDriverId(1);

        SQLException thrown = assertThrows(SQLException.class, () -> {
            timeRecordService.findByDriverId(1);
        });

        assertEquals("Erro de banco de dados", thrown.getMessage());
        verify(timeRecordService, times(1)).findByDriverId(1);
    }

    // --- Métodos de teste para updateTimeRecord ---
    @Test
    void testUpdateTimeRecordSuccess() throws SQLException, IllegalArgumentException {
        TimeRecord updatedRecord = new TimeRecord(1, 1, LocalDateTime.now(), "OUT", "Location C");
        when(timeRecordService.updateTimeRecord(updatedRecord)).thenReturn(true);

        boolean result = timeRecordService.updateTimeRecord(updatedRecord);

        assertTrue(result);
        verify(timeRecordService, times(1)).updateTimeRecord(updatedRecord);
    }

    @Test
    void testUpdateTimeRecordNotFound() throws SQLException, IllegalArgumentException {
        TimeRecord nonExistentRecord = new TimeRecord(99, 1, LocalDateTime.now(), "OUT", "Location D");
        when(timeRecordService.updateTimeRecord(nonExistentRecord)).thenReturn(false);

        boolean result = timeRecordService.updateTimeRecord(nonExistentRecord);

        assertFalse(result);
        verify(timeRecordService, times(1)).updateTimeRecord(nonExistentRecord);
    }

    @Test
    void testUpdateTimeRecordInvalidInput() throws SQLException, IllegalArgumentException {
        TimeRecord invalidRecord = new TimeRecord(1, 1, null, "OUT", "Location E");
        doThrow(new IllegalArgumentException("Timestamp não pode ser nulo")).when(timeRecordService).updateTimeRecord(invalidRecord);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            timeRecordService.updateTimeRecord(invalidRecord);
        });

        assertEquals("Timestamp não pode ser nulo", thrown.getMessage());
        verify(timeRecordService, times(1)).updateTimeRecord(invalidRecord);
    }

    @Test
    void testUpdateTimeRecordThrowsSQLException() throws SQLException, IllegalArgumentException {
        TimeRecord recordToUpdate = new TimeRecord(1, 1, LocalDateTime.now(), "OUT", "Location F");
        doThrow(new SQLException("Erro de banco de dados")).when(timeRecordService).updateTimeRecord(recordToUpdate);

        SQLException thrown = assertThrows(SQLException.class, () -> {
            timeRecordService.updateTimeRecord(recordToUpdate);
        });

        assertEquals("Erro de banco de dados", thrown.getMessage());
        verify(timeRecordService, times(1)).updateTimeRecord(recordToUpdate);
    }

    // --- Métodos de teste para deleteTimeRecord ---
    @Test
    void testDeleteTimeRecordSuccess() throws SQLException {
        when(timeRecordService.deleteTimeRecord(1)).thenReturn(true);

        boolean result = timeRecordService.deleteTimeRecord(1);

        assertTrue(result);
        verify(timeRecordService, times(1)).deleteTimeRecord(1);
    }

    @Test
    void testDeleteTimeRecordNotFound() throws SQLException {
        when(timeRecordService.deleteTimeRecord(99)).thenReturn(false);

        boolean result = timeRecordService.deleteTimeRecord(99);

        assertFalse(result);
        verify(timeRecordService, times(1)).deleteTimeRecord(99);
    }

    @Test
    void testDeleteTimeRecordThrowsSQLException() throws SQLException {
        doThrow(new SQLException("Erro de banco de dados")).when(timeRecordService).deleteTimeRecord(1);

        SQLException thrown = assertThrows(SQLException.class, () -> {
            timeRecordService.deleteTimeRecord(1);
        });

        assertEquals("Erro de banco de dados", thrown.getMessage());
        verify(timeRecordService, times(1)).deleteTimeRecord(1);
    }
}
