package com.compliancesys.service;

import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.MobileCommunication;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface MobileCommunicationService {

    MobileCommunication createMobileCommunication(MobileCommunication communication) throws BusinessException, SQLException;

    Optional<MobileCommunication> getMobileCommunicationById(int id) throws BusinessException, SQLException;

    List<MobileCommunication> getAllMobileCommunications() throws BusinessException, SQLException;

    // Métodos de busca específicos para o modelo MobileCommunication
    List<MobileCommunication> getMobileCommunicationsByDriverId(int driverId) throws BusinessException, SQLException;

    List<MobileCommunication> getMobileCommunicationsByRecordId(int recordId) throws BusinessException, SQLException;

    MobileCommunication updateMobileCommunication(MobileCommunication communication) throws BusinessException, SQLException;

    boolean deleteMobileCommunication(int id) throws BusinessException, SQLException;
}
