package com.compliancesys.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.compliancesys.model.TimeRecord;
import com.compliancesys.model.enums.EventType;
import com.compliancesys.util.impl.TimeUtilImpl;

class TimeUtilTest {

    private TimeUtil timeUtil;
    private LocalDateTime baseTime;

    @BeforeEach
    void setUp() {
        timeUtil = new TimeUtilImpl();
        baseTime = LocalDateTime.of(2024, 1, 15, 8, 0, 0);
    }

    // ==================== Helper Methods ====================

    private TimeRecord createTimeRecord(EventType eventType, LocalDateTime recordTime) {
        TimeRecord record = new TimeRecord();
        record.setEventType(eventType);
        record.setRecordTime(recordTime);
        record.setDriverId(1);
        record.setJourneyId(1);
        return record;
    }

    // ==================== Testes de calculateTotalWorkDuration ====================

    @Test
    @DisplayName("Deve calcular duração total de trabalho corretamente")
    void calculateTotalWorkDuration_ValidRecords_ReturnsCorrectDuration() {
        List<TimeRecord> records = Arrays.asList(
            createTimeRecord(EventType.START_WORK, baseTime),
            createTimeRecord(EventType.END_WORK, baseTime.plusHours(4)),
            createTimeRecord(EventType.START_WORK, baseTime.plusHours(5)),
            createTimeRecord(EventType.END_WORK, baseTime.plusHours(8))
        );

        Duration result = timeUtil.calculateTotalWorkDuration(records);

        assertEquals(Duration.ofHours(7), result);
    }

    @Test
    @DisplayName("Deve retornar zero para lista vazia")
    void calculateTotalWorkDuration_EmptyList_ReturnsZero() {
        Duration result = timeUtil.calculateTotalWorkDuration(Collections.emptyList());
        assertEquals(Duration.ZERO, result);
    }

    @Test
    @DisplayName("Deve retornar zero para lista nula")
    void calculateTotalWorkDuration_NullList_ReturnsZero() {
        Duration result = timeUtil.calculateTotalWorkDuration(null);
        assertEquals(Duration.ZERO, result);
    }

    @Test
    @DisplayName("Deve calcular duração com START_DRIVE e END_DRIVE")
    void calculateTotalWorkDuration_DriveEvents_ReturnsCorrectDuration() {
        List<TimeRecord> records = Arrays.asList(
            createTimeRecord(EventType.START_DRIVE, baseTime),
            createTimeRecord(EventType.END_DRIVE, baseTime.plusHours(5))
        );

        Duration result = timeUtil.calculateTotalWorkDuration(records);

        assertEquals(Duration.ofHours(5), result);
    }

    // ==================== Testes de calculateTotalRestDuration ====================

    @Test
    @DisplayName("Deve calcular duração total de descanso corretamente")
    void calculateTotalRestDuration_ValidRecords_ReturnsCorrectDuration() {
        List<TimeRecord> records = Arrays.asList(
            createTimeRecord(EventType.START_REST, baseTime),
            createTimeRecord(EventType.END_REST, baseTime.plusMinutes(30)),
            createTimeRecord(EventType.START_REST, baseTime.plusHours(4)),
            createTimeRecord(EventType.END_REST, baseTime.plusHours(4).plusMinutes(30))
        );

        Duration result = timeUtil.calculateTotalRestDuration(records);

        assertEquals(Duration.ofHours(1), result);
    }

    @Test
    @DisplayName("Deve retornar zero para lista vazia de descanso")
    void calculateTotalRestDuration_EmptyList_ReturnsZero() {
        Duration result = timeUtil.calculateTotalRestDuration(Collections.emptyList());
        assertEquals(Duration.ZERO, result);
    }

    @Test
    @DisplayName("Deve retornar zero para lista nula de descanso")
    void calculateTotalRestDuration_NullList_ReturnsZero() {
        Duration result = timeUtil.calculateTotalRestDuration(null);
        assertEquals(Duration.ZERO, result);
    }

    // ==================== Testes de exceedsMaxContinuousDriving ====================

    @Test
    @DisplayName("Deve detectar direção contínua excedendo limite")
    void exceedsMaxContinuousDriving_ExceedsLimit_ReturnsTrue() {
        Duration maxDriving = Duration.ofHours(4);
        List<TimeRecord> records = Arrays.asList(
            createTimeRecord(EventType.START_DRIVE, baseTime),
            createTimeRecord(EventType.END_DRIVE, baseTime.plusHours(5))
        );

        boolean result = timeUtil.exceedsMaxContinuousDriving(records, maxDriving);

        assertTrue(result);
    }

    @Test
    @DisplayName("Deve retornar false quando direção está dentro do limite")
    void exceedsMaxContinuousDriving_WithinLimit_ReturnsFalse() {
        Duration maxDriving = Duration.ofHours(4);
        List<TimeRecord> records = Arrays.asList(
            createTimeRecord(EventType.START_DRIVE, baseTime),
            createTimeRecord(EventType.END_DRIVE, baseTime.plusHours(3))
        );

        boolean result = timeUtil.exceedsMaxContinuousDriving(records, maxDriving);

        assertFalse(result);
    }

    @Test
    @DisplayName("Deve retornar false para lista vazia")
    void exceedsMaxContinuousDriving_EmptyList_ReturnsFalse() {
        Duration maxDriving = Duration.ofHours(4);

        boolean result = timeUtil.exceedsMaxContinuousDriving(Collections.emptyList(), maxDriving);

        assertFalse(result);
    }

    // ==================== Testes de hasInsufficientInterJourneyRest ====================

    @Test
    @DisplayName("Deve detectar descanso interjornada insuficiente")
    void hasInsufficientInterJourneyRest_InsufficientRest_ReturnsTrue() {
        LocalDateTime lastExit = LocalDateTime.of(2024, 1, 15, 22, 0);
        LocalDateTime nextEntry = LocalDateTime.of(2024, 1, 16, 5, 0);
        Duration minRest = Duration.ofHours(11);

        boolean result = timeUtil.hasInsufficientInterJourneyRest(lastExit, nextEntry, minRest);

        assertTrue(result);
    }

    @Test
    @DisplayName("Deve retornar false quando descanso interjornada é suficiente")
    void hasInsufficientInterJourneyRest_SufficientRest_ReturnsFalse() {
        LocalDateTime lastExit = LocalDateTime.of(2024, 1, 15, 18, 0);
        LocalDateTime nextEntry = LocalDateTime.of(2024, 1, 16, 6, 0);
        Duration minRest = Duration.ofHours(11);

        boolean result = timeUtil.hasInsufficientInterJourneyRest(lastExit, nextEntry, minRest);

        assertFalse(result);
    }

    @Test
    @DisplayName("Deve retornar false para datas nulas")
    void hasInsufficientInterJourneyRest_NullDates_ReturnsFalse() {
        Duration minRest = Duration.ofHours(11);

        assertFalse(timeUtil.hasInsufficientInterJourneyRest(null, LocalDateTime.now(), minRest));
        assertFalse(timeUtil.hasInsufficientInterJourneyRest(LocalDateTime.now(), null, minRest));
    }

    // ==================== Testes de calculateDuration ====================

    @Test
    @DisplayName("Deve calcular duração entre dois horários")
    void calculateDuration_ValidTimes_ReturnsCorrectDuration() {
        LocalDateTime start = LocalDateTime.of(2024, 1, 15, 8, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 15, 12, 30);

        Duration result = timeUtil.calculateDuration(start, end);

        assertEquals(Duration.ofHours(4).plusMinutes(30), result);
    }

    @Test
    @DisplayName("Deve retornar zero quando fim é antes do início")
    void calculateDuration_EndBeforeStart_ReturnsZero() {
        LocalDateTime start = LocalDateTime.of(2024, 1, 15, 12, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 15, 8, 0);

        Duration result = timeUtil.calculateDuration(start, end);

        assertEquals(Duration.ZERO, result);
    }

    @Test
    @DisplayName("Deve retornar zero para valores nulos")
    void calculateDuration_NullValues_ReturnsZero() {
        assertEquals(Duration.ZERO, timeUtil.calculateDuration(null, LocalDateTime.now()));
        assertEquals(Duration.ZERO, timeUtil.calculateDuration(LocalDateTime.now(), null));
        assertEquals(Duration.ZERO, timeUtil.calculateDuration(null, null));
    }

    // ==================== Testes de calculateMaxContinuousDriving ====================

    @Test
    @DisplayName("Deve calcular máxima direção contínua corretamente")
    void calculateMaxContinuousDriving_ValidRecords_ReturnsMaxDuration() {
        List<TimeRecord> records = Arrays.asList(
            createTimeRecord(EventType.START_DRIVE, baseTime),
            createTimeRecord(EventType.END_DRIVE, baseTime.plusHours(2)),
            createTimeRecord(EventType.START_REST, baseTime.plusHours(2)),
            createTimeRecord(EventType.END_REST, baseTime.plusHours(2).plusMinutes(30)),
            createTimeRecord(EventType.START_DRIVE, baseTime.plusHours(3)),
            createTimeRecord(EventType.END_DRIVE, baseTime.plusHours(6))
        );

        Duration result = timeUtil.calculateMaxContinuousDriving(records);

        assertEquals(Duration.ofHours(3), result);
    }

    @Test
    @DisplayName("Deve retornar zero para lista vazia")
    void calculateMaxContinuousDriving_EmptyList_ReturnsZero() {
        Duration result = timeUtil.calculateMaxContinuousDriving(Collections.emptyList());
        assertEquals(Duration.ZERO, result);
    }

    // ==================== Testes de hasRestPeriodAfterDriving ====================

    @Test
    @DisplayName("Deve retornar true quando há período de descanso adequado")
    void hasRestPeriodAfterDriving_AdequateRest_ReturnsTrue() {
        Duration maxDriving = Duration.ofHours(4);
        List<TimeRecord> records = Arrays.asList(
            createTimeRecord(EventType.START_DRIVE, baseTime),
            createTimeRecord(EventType.END_DRIVE, baseTime.plusHours(4)),
            createTimeRecord(EventType.START_REST, baseTime.plusHours(4)),
            createTimeRecord(EventType.END_REST, baseTime.plusHours(4).plusMinutes(30))
        );

        boolean result = timeUtil.hasRestPeriodAfterDriving(records, maxDriving);

        assertTrue(result);
    }

    @Test
    @DisplayName("Deve retornar true para lista vazia")
    void hasRestPeriodAfterDriving_EmptyList_ReturnsTrue() {
        Duration maxDriving = Duration.ofHours(4);

        boolean result = timeUtil.hasRestPeriodAfterDriving(Collections.emptyList(), maxDriving);

        assertTrue(result);
    }

    // ==================== Testes de hasInsufficientIntraJourneyRest ====================

    @Test
    @DisplayName("Deve retornar false para lista vazia no descanso intrajornada")
    void hasInsufficientIntraJourneyRest_EmptyList_ReturnsFalse() {
        Duration minRest = Duration.ofMinutes(30);
        Duration maxDriving = Duration.ofHours(4);

        boolean result = timeUtil.hasInsufficientIntraJourneyRest(Collections.emptyList(), minRest, maxDriving);

        assertFalse(result);
    }

    @Test
    @DisplayName("Deve retornar false para lista nula no descanso intrajornada")
    void hasInsufficientIntraJourneyRest_NullList_ReturnsFalse() {
        Duration minRest = Duration.ofMinutes(30);
        Duration maxDriving = Duration.ofHours(4);

        boolean result = timeUtil.hasInsufficientIntraJourneyRest(null, minRest, maxDriving);

        assertFalse(result);
    }
}