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
    void testRegisterTimeRecordInvalidData() throws SQLException, IllegalArgumentException {
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

        SQLException thrown = assertThrows(SQLException.class, ()TimeRecordServiceTest.java

