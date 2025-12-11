package com.compliancesys.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import com.compliancesys.model.TimeRecord; // Importar TimeRecord

/**
 * Interface para validações específicas de objetos java.time.
 * Contém métodos para validar períodos de tempo e sequências de eventos.
 */
public interface TimeUtil {

    /**
     * Valida se um período de tempo (Duration) é positivo.
     * @param duration O período de tempo a ser validado.
     * @return true se a duração é positiva, false caso contrário.
     */
    boolean isPositiveDuration(Duration duration);

    /**
     * Valida se uma lista de LocalDateTime está em ordem cronológica.
     * @param timestamps A lista de timestamps a ser validada.
     * @return true se a lista está em ordem cronológica, false caso contrário.
     */
    boolean isChronological(List<LocalDateTime> timestamps);

    /**
     * Valida se um LocalDateTime é válido (não nulo e não no futuro).
     * @param dateTime O LocalDateTime a ser validado.
     * @return true se o LocalDateTime é válido, false caso contrário.
     */
    boolean isValidDateTime(LocalDateTime dateTime);

    /**
     * Verifica se uma duração está dentro de uma duração máxima permitida.
     * @param duration A duração a ser verificada.
     * @param maxDuration A duração máxima permitida.
     * @return true se a duração é menor ou igual à duração máxima, false caso contrário.
     */
    boolean isWithinMaxDuration(Duration duration, Duration maxDuration);

    /**
     * Verifica se uma duração está acima de uma duração mínima permitida.
     * @param duration A duração a ser verificada.
     * @param minDuration A duração mínima permitida.
     * @return true se a duração é maior ou igual à duração mínima, false caso contrário.
     */
    boolean isAboveMinDuration(Duration duration, Duration minDuration);

    /**
     * Calcula o tempo total de condução a partir de uma lista de registros de ponto.
     * Assume que os registros de ponto são pares de entrada/saída.
     * @param timeRecords Lista de registros de ponto.
     * @return O tempo total de condução em minutos.
     */
    long calculateTotalDrivingTime(List<TimeRecord> timeRecords);

    /**
     * Calcula o tempo total de descanso a partir de uma lista de registros de ponto.
     * Assume que os registros de ponto são pares de entrada/saída.
     * @param timeRecords Lista de registros de ponto.
     * @return O tempo total de descanso em minutos.
     */
    long calculateTotalRestTime(List<TimeRecord> timeRecords);
}
