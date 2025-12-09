package com.compliancesys.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TimeUtilTest {

    // Implementação concreta simples da interface TimeUtil para fins de teste
    private static class TimeUtilImpl implements TimeUtil {

        @Override
        public boolean isPositiveDuration(Duration duration) {
            return duration != null && !duration.isNegative() && !duration.isZero();
        }

        @Override
        public boolean isChronological(List<LocalDateTime> timestamps) {
            if (timestamps == null || timestamps.size() <= 1) {
                return true; // Uma lista nula, vazia ou com um único elemento é considerada cronológica
            }
            for (int i = 0; i < timestamps.size() - 1; i++) {
                if (timestamps.get(i) == null || timestamps.get(i + 1) == null) {
                    return false; // Timestamps nulos invalidam a ordem
                }
                if (timestamps.get(i).isAfter(timestamps.get(i + 1))) {
                    return false; // Se um timestamp for depois do próximo, não é cronológico
                }
            }
            return true;
        }

        @Override
        public boolean isValidDateTime(LocalDateTime dateTime) {
            return dateTime != null && !dateTime.isAfter(LocalDateTime.now());
        }

        @Override
        public boolean isWithinMaxDuration(Duration duration, Duration maxDuration) {
            if (duration == null || maxDuration == null) {
                return false; // Duração ou duração máxima nula não é válida para comparação
            }
            return !duration.isNegative() && (duration.compareTo(maxDuration) <= 0);
        }

        @Override
        public boolean isAboveMinDuration(Duration duration, Duration minDuration) {
            if (duration == null || minDuration == null) {
                return false; // Duração ou duração mínima nula não é válida para comparação
            }
            return !duration.isNegative() && (duration.compareTo(minDuration) >= 0);
        }
    }

    private TimeUtil timeUtil;

    @BeforeEach
    void setUp() {
        // Inicializa a implementação concreta do TimeUtil antes de cada teste
        timeUtil = new TimeUtilImpl();
    }

    // Testes para isPositiveDuration
    @Test
    void testIsPositiveDurationWithPositiveDuration() {
        Duration duration = Duration.ofHours(1);
        assertTrue(timeUtil.isPositiveDuration(duration));
    }

    @Test
    void testIsPositiveDurationWithZeroDuration() {
        Duration duration = Duration.ZERO;
        assertFalse(timeUtil.isPositiveDuration(duration));
    }

    @Test
    void testIsPositiveDurationWithNegativeDuration() {
        Duration duration = Duration.ofHours(-1);
        assertFalse(timeUtil.isPositiveDuration(duration));
    }

    @Test
    void testIsPositiveDurationWithNullDuration() {
        assertFalse(timeUtil.isPositiveDuration(null));
    }

    // Testes para isChronological
    @Test
    void testIsChronologicalWithOrderedTimestamps() {
        List<LocalDateTime> timestamps = Arrays.asList(
                LocalDateTime.of(2023, 1, 1, 10, 0),
                LocalDateTime.of(2023, 1, 1, 11, 0),
                LocalDateTime.of(2023, 1, 1, 12, 0)
        );
        assertTrue(timeUtil.isChronological(timestamps));
    }

    @Test
    void testIsChronologicalWithSameTimestamps() {
        List<LocalDateTime> timestamps = Arrays.asList(
                LocalDateTime.of(2023, 1, 1, 10, 0),
                LocalDateTime.of(2023, 1, 1, 10, 0),
                LocalDateTime.of(2023, 1, 1, 11, 0)
        );
        assertTrue(timeUtil.isChronological(timestamps));
    }

    @Test
    void testIsChronologicalWithUnorderedTimestamps() {
        List<LocalDateTime> timestamps = Arrays.asList(
                LocalDateTime.of(2023, 1, 1, 10, 0),
                LocalDateTime.of(2023, 1, 1, 9, 0), // Fora de ordem
                LocalDateTime.of(2023, 1, 1, 12, 0)
        );
        assertFalse(timeUtil.isChronological(timestamps));
    }

    @Test
    void testIsChronologicalWithNullTimestamps() {
        assertTrue(timeUtil.isChronological(null));
    }

    @Test
    void testIsChronologicalWithEmptyList() {
        assertTrue(timeUtil.isChronological(Collections.emptyList()));
    }

    @Test
    void testIsChronologicalWithSingleElementList() {
        List<LocalDateTime> timestamps = Collections.singletonList(LocalDateTime.now());
        assertTrue(timeUtil.isChronological(timestamps));
    }

    @Test
    void testIsChronologicalWithListContainingNull() {
        List<LocalDateTime> timestamps = Arrays.asList(
                LocalDateTime.of(2023, 1, 1, 10, 0),
                null,
                LocalDateTime.of(2023, 1, 1, 12, 0)
        );
        assertFalse(timeUtil.isChronological(timestamps));
    }

    // Testes para isValidDateTime
    @Test
    void testIsValidDateTimeWithPastDateTime() {
        LocalDateTime pastDateTime = LocalDateTime.now().minusDays(1);
        assertTrue(timeUtil.isValidDateTime(pastDateTime));
    }

    @Test
    void testIsValidDateTimeWithCurrentDateTime() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        assertTrue(timeUtil.isValidDateTime(currentDateTime));
    }

    @Test
    void testIsValidDateTimeWithFutureDateTime() {
        LocalDateTime futureDateTime = LocalDateTime.now().plusDays(1);
        assertFalse(timeUtil.isValidDateTime(futureDateTime));
    }

    @Test
    void testIsValidDateTimeWithNullDateTime() {
        assertFalse(timeUtil.isValidDateTime(null));
    }

    // Testes para isWithinMaxDuration
    @Test
    void testIsWithinMaxDurationWhenWithinLimit() {
        Duration duration = Duration.ofHours(1);
        Duration maxDuration = Duration.ofHours(2);
        assertTrue(timeUtil.isWithinMaxDuration(duration, maxDuration));
    }

    @Test
    void testIsWithinMaxDurationWhenAtLimit() {
        Duration duration = Duration.ofHours(2);
        Duration maxDuration = Duration.ofHours(2);
        assertTrue(timeUtil.isWithinMaxDuration(duration, maxDuration));
    }

    @Test
    void testIsWithinMaxDurationWhenExceedsLimit() {
        Duration duration = Duration.ofHours(3);
        Duration maxDuration = Duration.ofHours(2);
        assertFalse(timeUtil.isWithinMaxDuration(duration, maxDuration));
    }

    @Test
    void testIsWithinMaxDurationWithNegativeDuration() {
        Duration duration = Duration.ofHours(-1);
        Duration maxDuration = Duration.ofHours(2);
        assertFalse(timeUtil.isWithinMaxDuration(duration, maxDuration));
    }

    @Test
    void testIsWithinMaxDurationWithNullDuration() {
        Duration maxDuration = Duration.ofHours(2);
        assertFalse(timeUtil.isWithinMaxDuration(null, maxDuration));
    }

    @Test
    void testIsWithinMaxDurationWithNullMaxDuration() {
        Duration duration = Duration.ofHours(1);
        assertFalse(timeUtil.isWithinMaxDuration(duration, null));
    }

    // Testes para isAboveMinDuration
    @Test
    void testIsAboveMinDurationWhenAboveLimit() {
        Duration duration = Duration.ofHours(2);
        Duration minDuration = Duration.ofHours(1);
        assertTrue(timeUtil.isAboveMinDuration(duration, minDuration));
    }

    @Test
    void testIsAboveMinDurationWhenAtLimit() {
        Duration duration = Duration.ofHours(1);
        Duration minDuration = Duration.ofHours(1);
        assertTrue(timeUtil.isAboveMinDuration(duration, minDuration));
    }

    @Test
    void testIsAboveMinDurationWhenBelowLimit() {
        Duration duration = Duration.ofMinutes(30);
        Duration minDuration = Duration.ofHours(1);
        assertFalse(timeUtil.isAboveMinDuration(duration, minDuration));
    }

    @Test
    void testIsAboveMinDurationWithNegativeDuration() {
        Duration duration = Duration.ofHours(-1);
        Duration minDuration = Duration.ofHours(1);
        assertFalse(timeUtil.isAboveMinDuration(duration, minDuration));
    }

    @Test
    void testIsAboveMinDurationWithNullDuration() {
        Duration minDuration = Duration.ofHours(1);
        assertFalse(timeUtil.isAboveMinDuration(null, minDuration));
    }

    @Test
    void testIsAboveMinDurationWithNullMinDuration() {
        Duration duration = Duration.ofHours(1);
        assertFalse(timeUtil.isAboveMinDuration(duration, null));
    }
}
