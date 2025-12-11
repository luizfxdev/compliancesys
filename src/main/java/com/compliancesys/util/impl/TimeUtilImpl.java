package com.compliancesys.util.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import com.compliancesys.model.TimeRecord; // Importar TimeRecord
import com.compliancesys.util.TimeUtil;

/**
 * Implementação da interface TimeUtil para validações específicas de objetos java.time.
 */
public class TimeUtilImpl implements TimeUtil {

    @Override
    public boolean isPositiveDuration(Duration duration) {
        return duration != null && !duration.isNegative() && !duration.isZero();
    }

    @Override
    public boolean isChronological(List<LocalDateTime> timestamps) {
        if (timestamps == null || timestamps.size() < 2) {
            return true; // Uma lista vazia ou com um único elemento é considerada cronológica
        }
        for (int i = 0; i < timestamps.size() - 1; i++) {
            if (timestamps.get(i) == null || timestamps.get(i + 1) == null) {
                // Decida como lidar com nulos: pode ser um erro ou ignorar/tratar como não cronológico
                return false; // Assumindo que nulos quebram a ordem cronológica
            }
            if (timestamps.get(i).isAfter(timestamps.get(i + 1))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isValidDateTime(LocalDateTime dateTime) {
        // Um LocalDateTime é válido se não for nulo e não estiver no futuro.
        // Se a intenção é permitir datas futuras, ajuste esta lógica.
        return dateTime != null && !dateTime.isAfter(LocalDateTime.now());
    }

    @Override
    public boolean isWithinMaxDuration(Duration duration, Duration maxDuration) {
        if (duration == null || maxDuration == null) {
            return false; // Ou lance uma IllegalArgumentException
        }
        return !duration.isNegative() && (duration.compareTo(maxDuration) <= 0);
    }

    @Override
    public boolean isAboveMinDuration(Duration duration, Duration minDuration) {
        if (duration == null || minDuration == null) {
            return false; // Ou lance uma IllegalArgumentException
        }
        return !duration.isNegative() && (duration.compareTo(minDuration) >= 0);
    }

    // --- NOVOS MÉTODOS PARA CÁLCULO DE TEMPO ---

    @Override
    public long calculateTotalDrivingTime(List<TimeRecord> timeRecords) {
        long totalDrivingMinutes = 0;
        // Assumindo que timeRecords são pares de entrada/saída
        for (int i = 0; i < timeRecords.size(); i += 2) {
            if (i + 1 < timeRecords.size()) {
                LocalDateTime start = timeRecords.get(i).getRecordTime();
                LocalDateTime end = timeRecords.get(i + 1).getRecordTime();
                if (start != null && end != null && end.isAfter(start)) {
                    totalDrivingMinutes += Duration.between(start, end).toMinutes();
                }
            }
        }
        return totalDrivingMinutes;
    }

    @Override
    public long calculateTotalRestTime(List<TimeRecord> timeRecords) {
        long totalRestMinutes = 0;
        // Assumindo que timeRecords são pares de entrada/saída e que o descanso ocorre entre as jornadas
        // ou entre o fim de uma jornada e o início da próxima (se houver)
        for (int i = 1; i < timeRecords.size(); i += 2) {
            if (i + 1 < timeRecords.size()) {
                LocalDateTime endDriving = timeRecords.get(i).getRecordTime();
                LocalDateTime startNextDriving = timeRecords.get(i + 1).getRecordTime();
                if (endDriving != null && startNextDriving != null && startNextDriving.isAfter(endDriving)) {
                    totalRestMinutes += Duration.between(endDriving, startNextDriving).toMinutes();
                }
            }
        }
        return totalRestMinutes;
    }
}
