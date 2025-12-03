package com.compliancesys.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Interface para validações específicas de objetos java.time.
 * Contém métodos para validar períodos de tempo e sequências de eventos.
 */
public interface TimeValidator {

    /**
     * Valida se um período de tempo (Duration) é positivo.
     * @param duration O período de tempo a ser validado.
     * @return true se a duração é positiva, false caso contrário.
     */
    boolean isPositiveDuration(Duration duration);

    /**
     * Valida se uma lista de LocalDateTime está em ordem cronológica.
     * @param timestamps A lista de LocalDateTime a ser validada.
     * @return true se os timestamps estão em ordem cronológica, false caso contrário.
     */
    boolean isChronological(List<LocalDateTime> timestamps);

    /**
     * Valida se um LocalDateTime não é nulo e não está no futuro.
     * @param dateTime O LocalDateTime a ser validado.
     * @return true se o LocalDateTime é válido, false caso contrário.
     */
    boolean isValidDateTime(LocalDateTime dateTime);

    /**
     * Valida se um período de tempo está dentro de um limite máximo.
     * @param duration O período de tempo a ser verificado.
     * @param maxDuration O limite máximo permitido.
     * @return true se a duração é menor ou igual ao limite máximo, false caso contrário.
     */
    boolean isWithinMaxDuration(Duration duration, Duration maxDuration);

    /**
     * Valida se um período de tempo está acima de um limite mínimo.
     * @param duration O período de tempo a ser verificado.
     * @param minDuration O limite mínimo permitido.
     * @return true se a duração é maior ou igual ao limite mínimo, false caso contrário.
     */
    boolean isAboveMinDuration(Duration duration, Duration minDuration);
}
