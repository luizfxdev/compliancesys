package com.compliancesys.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.TimeRecord;

public interface TimeRecordService {
    TimeRecord createTimeRecord(TimeRecord timeRecord) throws BusinessException;
    Optional<TimeRecord> getTimeRecordById(int id) throws BusinessException;
    List<TimeRecord> getAllTimeRecords() throws BusinessException;
    TimeRecord updateTimeRecord(TimeRecord timeRecord) throws BusinessException;
    boolean deleteTimeRecord(int id) throws BusinessException;

    // NOVO MÉTODO: Busca registros de ponto por ID do motorista
    List<TimeRecord> getTimeRecordsByDriverId(int driverId) throws BusinessException;

    // Busca registros de ponto de um motorista em uma data específica
    List<TimeRecord> getTimeRecordsByDriverIdAndDate(int driverId, LocalDate date) throws BusinessException;
}
