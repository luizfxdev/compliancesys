package com.compliancesys.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull; // Adicionado para clareza em alguns casos
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
        public boolean isWithinMaxDuration(Duration duration, Duration maxDuration) {
            if (duration == null || maxDuration == null || duration.isNegative() || maxDuration.isNegative()) {
                return false; // Duração ou limite nulo/negativo não é válido para esta verificação
            }
            return !duration.isAfter(maxDuration); // duration <= maxDuration
        }

        @Override
        public boolean isAboveMinDuration(Duration duration, Duration minDuration) {
            if (duration == null || minDuration == null || duration.isNegative() || minDuration.isNegative()) {
                return false; // Duração ou limite nulo/negativo não é válido para esta verificação
            }
            return !duration.isBefore(minDuration); // duration >= minDuration
        }
    }

    private TimeUtil timeUtil;

    @BeforeEach
    void setUp() {
        timeUtil = new TimeUtilImpl();
    }

    // Testes para isPositiveDuration
    @Test
    @DisplayName("Deve retornar true para duração positiva")
    void shouldReturnTrueForPositiveDuration() {
        assertTrue(timeUtil.isPositiveDuration(Duration.ofMinutes(1)));
        assertTrue(timeUtil.isPositiveDuration(Duration.ofHours(10)));
    }

    @Test
    @DisplayName("Deve retornar false para duração zero")
    void shouldReturnFalseForZeroDuration() {
        assertFalse(timeUtil.isPositiveDuration(Duration.ZERO));
    }

    @Test
    @DisplayName("Deve retornar false para duração negativa")
    void shouldReturnFalseForNegativeDuration() {
        assertFalse(timeUtil.isPositiveDuration(Duration.ofMinutes(-1)));
        assertFalse(timeUtil.isPositiveDuration(Duration.ofHours(-5)));
    }

    @Test
    @DisplayName("Deve retornar false para duração nula")
    void shouldReturnFalseForNullDuration() {
        assertFalse(timeUtil.isPositiveDuration(null));
    }

    // Testes para isChronological
    @Test
    @DisplayName("Deve retornar true para lista de timestamps em ordem cronológica")
    void shouldReturnTrueForChronologicalTimestamps() {
        List<LocalDateTime> chronologicalList = Arrays.asList(
                LocalDateTime.of(2023, 1, 1, 10, 0, 0),
                LocalDateTime.of(2023, 1, 1, 10, 0, 1),
                LocalDateTime.of(2023, 1, 1, 10, 0, 2)
        );
        assertTrue(timeUtil.isChronological(chronologicalList));
    }

    @Test
    @DisplayName("Deve retornar true para lista de timestamps com elementos iguais")
    void shouldReturnTrueForChronologicalTimestampsWithEquals() {
        List<LocalDateTime> chronologicalList = Arrays.asList(
                LocalDateTime.of(2023, 1, 1, 10, 0, 0),
                LocalDateTime.of(2023, 1, 1, 10, 0, 0),
                LocalDateTime.of(2023, 1, 1, 10, 0, 1)
        );
        assertTrue(timeUtil.isChronological(chronologicalList));
    }

    @Test
    @DisplayName("Deve retornar true para lista vazia")
    void shouldReturnTrueForEmptyList() {
        assertTrue(timeUtil.isChronological(Collections.emptyList()));
    }

    @Test
    @DisplayName("Deve retornar true para lista com um único elemento")
    void shouldReturnTrueForSingleElementList() {
        assertTrue(timeUtil.isChronological(Collections.singletonList(LocalDateTime.now())));
    }

    @Test
    @DisplayName("Deve retornar true para lista nula")
    void shouldReturnTrueForNullList() {
        assertTrue(timeUtil.isChronological(null));
    }

    @Test
    @DisplayName("Deve retornar false para lista de timestamps fora de ordem cronológica")
    void shouldReturnFalseForNonChronologicalTimestamps() {
        List<LocalDateTime> nonChronologicalList = Arrays.asList(
                LocalDateTime.of(2023, 1, 1, 10, 0, 0),
                LocalDateTime.of(2023, 1, 1, 9, 59, 59), // Fora de ordem
                LocalDateTime.of(2023, 1, 1, 10, 0, 2)
        );
        assertFalse(timeUtil.isChronological(nonChronologicalList));
    }

    @Test
    @DisplayName("Deve retornar false para lista com timestamp nulo no meio")
    void shouldReturnFalseForListWithNullTimestamp() {
        List<LocalDateTime> listWithNull = Arrays.asList(
                LocalDateTime.of(2023, 1, 1, 10, 0, 0),
                null,
                LocalDateTime.of(2023, 1, 1, 10, 0, 2)
        );
        assertFalse(timeUtil.isChronological(listWithNull));
    }

    @Test
    @DisplayName("Deve retornar false para lista com timestamp nulo no início")
    void shouldReturnFalseForListWithNullTimestampAtStart() {
        List<LocalDateTime> listWithNull = Arrays.asList(
                null,
                LocalDateTime.of(2023, 1, 1, 10, 0, 0),
                LocalDateTime.of(2023, 1, 1, 10, 0, 2)
        );
        assertFalse(timeUtil.isChronological(listWithNull));
    }

    @Test
    @DisplayName("Deve retornar false para lista com timestamp nulo no fim")
    void shouldReturnFalseForListWithNullTimestampAtEnd() {
        List<LocalDateTime> listWithNull = Arrays.asList(
                LocalDateTime.of(2023, 1, 1, 10, 0, 0),
                LocalDateTime.of(2023, 1, 1, 10, 0, 2),
                null
        );
        assertFalse(timeUtil.isChronological(listWithNull));
    }

    // Testes para isWithinMaxDuration
    @Test
    @DisplayName("Deve retornar true quando a duração está dentro do limite máximo")
    void shouldReturnTrueWhenDurationIsWithinMaxLimit() {
        Duration duration = Duration.ofHours(1);
        Duration maxDuration = Duration.ofHours(2);
        assertTrue(timeUtil.isWithinMaxDuration(duration, maxDuration));
    }

    @Test
    @DisplayName("Deve retornar true quando a duração é igual ao limite máximo")
    void shouldReturnTrueWhenDurationIsAtMaxLimit() {
        Duration duration = Duration.ofHours(2);
        Duration maxDuration = Duration.ofHours(2);
        assertTrue(timeUtil.isWithinMaxDuration(duration, maxDuration));
    }

    @Test
    @DisplayName("Deve retornar false quando a duração excede o limite máximo")
    void shouldReturnFalseWhenDurationExceedsMaxLimit() {
        Duration duration = Duration.ofHours(3);
        Duration maxDuration = Duration.ofHours(2);
        assertFalse(timeUtil.isWithinMaxDuration(duration, maxDuration));
    }

    @Test
    @DisplayName("Deve retornar false quando a duração é negativa para isWithinMaxDuration")
    void shouldReturnFalseWhenDurationIsNegativeForIsWithinMaxDuration() {
        Duration duration = Duration.ofHours(-1);
        Duration maxDuration = Duration.ofHours(2);
        assertFalse(timeUtil.isWithinMaxDuration(duration, maxDuration));
    }

    @Test
    @DisplayName("Deve retornar false quando o limite máximo é negativo para isWithinMaxDuration")
    void shouldReturnFalseWhenMaxDurationIsNegativeForIsWithinMaxDuration() {
        Duration duration = Duration.ofHours(1);
        Duration maxDuration = Duration.ofHours(-2);
        assertFalse(timeUtil.isWithinMaxDuration(duration, maxDuration));
    }

    @Test
    @DisplayName("Deve retornar false quando a duração é nula para isWithinMaxDuration")
    void shouldReturnFalseWhenDurationIsNullForIsWithinMaxDuration() {
        Duration maxDuration = Duration.ofHours(1);
        assertFalse(timeUtil.isWithinMaxDuration(null, maxDuration));
    }

    @Test
    @DisplayName("Deve retornar false quando o limite máximo é nulo para isWithinMaxDuration")
    void shouldReturnFalseWhenMaxDurationIsNullForIsWithinMaxDuration() {
        Duration duration = Duration.ofHours(1);
        assertFalse(timeUtil.isWithinMaxDuration(duration, null));
    }

    // Testes para isAboveMinDuration
    @Test
    @DisplayName("Deve retornar true quando a duração está acima do limite mínimo")
    void shouldReturnTrueWhenDurationIsAboveMinLimit() {
        Duration duration = Duration.ofHours(2);
        Duration minDuration = Duration.ofHours(1);
        assertTrue(timeUtil.isAboveMinDuration(duration, minDuration));
    }

    @Test
    @DisplayName("Deve retornar true quando a duração é igual ao limite mínimo")
    void shouldReturnTrueWhenDurationIsAtMinLimit() {
        Duration duration = Duration.ofHours(1);
        Duration minDuration = Duration.ofHours(1);
        assertTrue(timeUtil.isAboveMinDuration(duration, minDuration));
    }

    @Test
    @DisplayName("Deve retornar false quando a duração está abaixo do limite mínimo")
    void shouldReturnFalseWhenDurationIsBelowMinLimit() {
        Duration duration = Duration.ofMinutes(30);
        Duration minDuration = Duration.ofHours(1);
        assertFalse(timeUtil.isAboveMinDuration(duration, minDuration));
    }

    @Test
    @DisplayName("Deve retornar false quando a duração é negativa para isAboveMinDuration")
    void shouldReturnFalseWhenDurationIsNegativeForIsAboveMinDuration() {
        Duration duration = Duration.ofHours(-1);
        Duration minDuration = Duration.ofHours(1);
        assertFalse(timeUtil.isAboveMinDuration(duration, minDuration));
    }

    @Test
    @DisplayName("Deve retornar false quando o limite mínimo é negativo para isAboveMinDuration")
    void shouldReturnFalseWhenMinDurationIsNegativeForIsAboveMinDuration() {
        Duration duration = Duration.ofHours(1);
        Duration minDuration = Duration.ofHours(-1);
        assertFalse(timeUtil.isAboveMinDuration(duration, minDuration));
    }

    @Test
    @DisplayName("Deve retornar false quando a duração é nula para isAboveMinDuration")
    void shouldReturnFalseWhenDurationIsNullForIsAboveMinDuration() {
        Duration minDuration = Duration.ofHours(1);
        assertFalse(timeUtil.isAboveMinDuration(null, minDuration));
    }

    @Test
    @DisplayName("Deve retornar false quando o limite mínimo é nulo para isAboveMinDuration")
    void shouldReturnFalseWhenMinDurationIsNullForIsAboveMinDuration() {
        Duration duration = Duration.ofHours(1);
        assertFalse(timeUtil.isAboveMinDuration(duration, null));
    }
}
