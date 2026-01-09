// src/main/java/com/compliancesys/service/MobileCommunicationService.java
package com.compliancesys.service;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.MobileCommunication;
import com.compliancesys.model.enums.EventType;

public interface MobileCommunicationService {
    MobileCommunication registerMobileCommunication(MobileCommunication communication) throws BusinessException, SQLException;
    Optional<MobileCommunication> getMobileCommunicationById(int communicationId) throws BusinessException, SQLException;
    List<MobileCommunication> getAllMobileCommunications() throws BusinessException, SQLException;
    MobileCommunication updateMobileCommunication(MobileCommunication communication) throws BusinessException, SQLException;
    boolean deleteMobileCommunication(int communicationId) throws BusinessException, SQLException;
    List<MobileCommunication> getMobileCommunicationsByJourneyId(int journeyId) throws BusinessException, SQLException; // Adicionado
    List<MobileCommunication> getMobileCommunicationsByDriverId(int driverId) throws BusinessException, SQLException; // Adicionado
    List<MobileCommunication> getMobileCommunicationsByEventType(EventType eventType) throws BusinessException, SQLException; // Adicionado
    List<MobileCommunication> getMobileCommunicationsByDateTimeRange(LocalDateTime start, LocalDateTime end) throws BusinessException, SQLException; // Adicionado
}
