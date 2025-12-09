package com.compliancesys.dao;

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

public class TimeRecordDAOTest {

    private TimeRecordDAO timeRecordDAO;

    @BeforeEach
    void setUp() {
        // Inicializa o mock do TimeRecordDAO antes de cada teste
        timeRecordDAO = Mockito.mock(TimeRecordDAO.class);
    }

    @Test
    void testInsert() throws SQLException {
        TimeRecord newRecord = new TimeRecord(0, 1, LocalDateTime.now(), "IN", "Location A");
        when(timeRecordDAO.insert(newRecord)).thenReturn(1); // Simula a inserção retornando um ID

        int id = timeRecordDAO.insert(newRecord);

        assertEquals(1, id);
        verify(timeRecordDAO, times(1)).insert(newRecord); // Verifica se o método insert foi chamado uma vez
    }

    @Test
    void testFindByIdFound() throws SQLException {
        TimeRecord expectedRecord = new TimeRecord(1, 1, LocalDateTime.now(), "IN", "Location A");
        when(timeRecordDAO.findById(1)).thenReturn(Optional.of(expectedRecord));

        Optional<TimeRecord> result = timeRecordDAO.findById(1);

        assertTrue(result.isPresent());
        assertEquals(expectedRecord, result.get());
        verify(timeRecordDAO, times(1)).findById(1);
    }

    @Test
    void testFindByIdNotFound() throws SQLException {
        when(timeRecordDAO.findById(99)).thenReturn(Optional.empty());

        Optional<TimeRecord> result = timeRecordDAO.findById(99);

        assertFalse(result.isPresent());
        verify(timeRecordDAO, times(1)).findById(99);
    }

    @Test
    void testFindAll() throws SQLException {
        TimeRecord record1 = new TimeRecord(1, 1, LocalDateTime.now(), "IN", "Location A");
        TimeRecord record2 = new TimeRecord(2, 2, LocalDateTime.now(), "OUT", "Location B");
        List<TimeRecord> expectedList = Arrays.asList(record1, record2);

        when(timeRecordDAO.findAll()).thenReturn(expectedList);

        List<TimeRecord> result = timeRecordDAO.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedList, result);
        verify(timeRecordDAO, times(1)).findAll();
    }

    @Test
    void testFindAllEmpty() throws SQLException {
        when(timeRecordDAO.findAll()).thenReturn(Collections.emptyList());

        List<TimeRecord> result = timeRecordDAO.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(timeRecordDAO, times(1)).findAll();
    }

    @Test
    void testFindByDriverId() throws SQLException {
        TimeRecord record1 = new TimeRecord(1, 1, LocalDateTime.now(), "IN", "Location A");
        TimeRecord record2 = new TimeRecord(3, 1, LocalDateTime.now().plusHours(1), "OUT", "Location A");
        List<TimeRecord> expectedList = Arrays.asList(record1, record2);

        when(timeRecordDAO.findByDriverId(1)).thenReturn(expectedList);

        List<TimeRecord> result = timeRecordDAO.findByDriverId(1);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedList, result);
        verify(timeRecordDAO, times(1)).findByDriverId(1);
    }

    @Test
    void testFindByDriverIdNoRecords() throws SQLException {
        when(timeRecordDAO.findByDriverId(99)).thenReturn(Collections.emptyList());

        List<TimeRecord> result = timeRecordDAO.findByDriverId(99);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(timeRecordDAO, times(1)).findByDriverId(99);
    }

    @Test
    void testFindByDriverIdAndDate() throws SQLException {
        LocalDate testDate = LocalDate.of(2025, 1, 15);
        TimeRecord record1 = new TimeRecord(1, 1, testDate.atTime(8, 0), "IN", "Location A");
        TimeRecord record2 = new TimeRecord(2, 1, testDate.atTime(17, 0), "OUT", "Location A");
        List<TimeRecord> expectedList = Arrays.asList(record1, record2);

        when(timeRecordDAO.findByDriverIdAndDate(1, testDate)).thenReturn(expectedList);

        List<TimeRecord> result = timeRecordDAO.findByDriverIdAndDate(1, testDate);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedList, result);
        verify(timeRecordDAO, times(1)).findByDriverIdAndDate(1, testDate);
    }

    @Test
    void testFindByDriverIdAndDateNoRecords() throws SQLException {
        LocalDate testDate = LocalDate.of(2025, 1, 15);
        when(timeRecordDAO.findByDriverIdAndDate(1, testDate)).thenReturn(Collections.emptyList());

        List<TimeRecord> result = timeRecordDAO.findByDriverIdAndDate(1, testDate);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(timeRecordDAO, times(1)).findByDriverIdAndDate(1, testDate);
    }

    @Test
    void testUpdateSuccess() throws SQLException {
        TimeRecord updatedRecord = new TimeRecord(1, 1, LocalDateTime.now(), "OUT", "Location C");
        when(timeRecordDAO.update(updatedRecord)).thenReturn(true);

        boolean result = timeRecordDAO.update(updatedRecord);

        assertTrue(result);
        verify(timeRecordDAO, times(1)).update(updatedRecord);
    }

    @Test
    void testUpdateFailure() throws SQLException {
        TimeRecord nonExistentRecord = new TimeRecord(99, 1, LocalDateTime.now(), "OUT", "Location C");
        when(timeRecordDAO.update(nonExistentRecord)).thenReturn(false);

        boolean result = timeRecordDAO.update(nonExistentRecord);

        assertFalse(result);
        verify(timeRecordDAO, times(1)).update(nonExistentRecord);
    }

    @Test
    void testDeleteSuccess() throws SQLException {
        when(timeRecordDAO.delete(1)).thenReturn(true);

        boolean result = timeRecordDAO.delete(1);

        assertTrue(result);
        verify(timeRecordDAO, times(1)).delete(1);
    }

    @Test
    void testDeleteFailure() throws SQLException {
        when(timeRecordDAO.delete(99)).thenReturn(false);

        boolean result = timeRecordDAO.delete(99);

        assertFalse(result);
        verify(timeRecordDAO, times(1)).delete(99);
    }

    @Test
    void testInsertThrowsSQLException() throws SQLException {
        TimeRecord newRecord = new TimeRecord(0, 1, LocalDateTime.now(), "IN", "Location A");
        when(timeRecordDAO.insert(newRecord)).thenThrow(new SQLException("Database error"));

        SQLException thrown = assertThrows(SQLException.class, () -> {
            timeRecordDAO.insert(newRecord);
        });

        assertEquals("Database error", thrown.getMessage());
        verify(timeRecordDAO, times(1)).insert(newRecord);
    }

    @Test
    void testFindByIdThrowsSQLException() throws SQLException {
        when(timeRecordDAO.findById(1)).thenThrow(new SQLException("Connection lost"));

        SQLException thrown = assertThrows(SQLException.class, () -> {
            timeRecordDAO.findById(1);
        });

        assertEquals("Connection lost", thrown.getMessage());
        verify(timeRecordDAO, times(1)).findById(1);
    }
}
