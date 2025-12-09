package com.compliancesys.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.TimeRecord;

public interface TimeRecordService {
    // Renomeado de createTimeRecord para registerTimeRecord
    TimeRecord registerTimeRecord(TimeRecord timeRecord) throws BusinessException;
    Optional<TimeRecord> getTimeRecordById(int id) throws BusinessException;
    List<TimeRecord> getAllTimeRecords() throws BusinessException;
    // Alterado o tipo de retorno de TimeRecord para boolean
    boolean updateTimeRecord(TimeRecord timeRecord) throws BusinessException;
    boolean deleteTimeRecord(int id) throws BusinessException;

    List<TimeRecord> getTimeRecordsByDriverId(int driverId) throws BusinessException;
    List<TimeRecord> getTimeRecordsByDriverIdAndDate(int driverId, LocalDate date) throws BusinessException;
}
