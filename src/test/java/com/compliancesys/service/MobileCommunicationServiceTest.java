package com.compliancesys.service;

import com.compliancesys.dao.MobileCommunicationDAO;
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.MobileCommunication;
import com.compliancesys.service.impl.MobileCommunicationServiceImpl;
import com.compliancesys.util.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class MobileCommunicationServiceTest {

    @Mock
    private MobileCommunicationDAO mobileCommunicationDAO;

    @Mock
    private Validator validator;

    @InjectMocks
    private MobileCommunicationServiceImpl mobileCommunicationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Configura o validator para sempre retornar true para validações básicas nos testes
        when(validator.isValidId(anyInt())).thenReturn(true);
        when(validator.isValidDateTime(any(LocalDateTime.class))).thenReturn(true);
        when(validator.isValidLatitude(anyDouble())).thenReturn(true);
        when(validator.isValidLongitude(anyDouble())).thenReturn(true);
        when(validator.isValidSignalStrength(anyInt())).thenReturn(true);
        when(validator.isValidBatteryLevel(anyInt())).thenReturn(true);
    }

    @Test
    void testCreateMobileCommunicationSuccess() throws BusinessException, SQLException {
        MobileCommunication newComm = new MobileCommunication(
                1, // driverId
                1, // recordId
                LocalDateTime.now(), // timestamp
                -23.5505, // latitude
                -46.6333, // longitude
                LocalDateTime.now(), // sendTimestamp
                true, // sendSuccess
                null // errorMessage
        );
        int generatedId = 1;
        MobileCommunication createdComm = new MobileCommunication(
                generatedId,
                newComm.getDriverId(),
                newComm.getRecordId(),
                newComm.getTimestamp(),
                newComm.getLatitude(),
                newComm.getLongitude(),
                newComm.getSendTimestamp(),
                newComm.isSendSuccess(),
                newComm.getErrorMessage(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(mobileCommunicationDAO.create(any(MobileCommunication.class))).thenReturn(generatedId);
        when(mobileCommunicationDAO.findById(generatedId)).thenReturn(Optional.of(createdComm));

        MobileCommunication result = mobileCommunicationService.createMobileCommunication(newComm);

        assertNotNull(result);
        assertEquals(generatedId, result.getId());
        verify(mobileCommunicationDAO, times(1)).create(any(MobileCommunication.class));
        verify(mobileCommunicationDAO, times(1)).findById(generatedId);
    }

    @Test
    void testCreateMobileCommunicationInvalidData() {
        MobileCommunication invalidComm = new MobileCommunication(
                0, // driverId inválido
                1,
                LocalDateTime.now(),
                -23.5505,
                -46.6333,
                LocalDateTime.now(),
                true,
                null
        );
        when(validator.isValidId(0)).thenReturn(false); // Simula validação falha

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            mobileCommunicationService.createMobileCommunication(invalidComm);
        });
        assertEquals("Dados da comunicação móvel inválidos.", exception.getMessage());
        verify(mobileCommunicationDAO, never()).create(any(MobileCommunication.class));
    }

    @Test
    void testGetMobileCommunicationByIdFound() throws BusinessException, SQLException {
        int commId = 1;
        MobileCommunication expectedComm = new MobileCommunication(
                commId, 1, 1, LocalDateTime.now(), -23.5505, -46.6333, LocalDateTime.now(), true, null, LocalDateTime.now(), LocalDateTime.now()
        );
        when(mobileCommunicationDAO.findById(commId)).thenReturn(Optional.of(expectedComm));

        Optional<MobileCommunication> result = mobileCommunicationService.getMobileCommunicationById(commId);

        assertTrue(result.isPresent());
        assertEquals(expectedComm, result.get());
        verify(mobileCommunicationDAO, times(1)).findById(commId);
    }

    @Test
    void testGetMobileCommunicationByIdNotFound() throws BusinessException, SQLException {
        int commId = 99;
        when(mobileCommunicationDAO.findById(commId)).thenReturn(Optional.empty());

        Optional<MobileCommunication> result = mobileCommunicationService.getMobileCommunicationById(commId);

        assertFalse(result.isPresent());
        verify(mobileCommunicationDAO, times(1)).findById(commId);
    }

    @Test
    void testGetAllMobileCommunications() throws BusinessException, SQLException {
        List<MobileCommunication> expectedComms = Arrays.asList(
                new MobileCommunication(1, 1, 1, LocalDateTime.now(), -23.5505, -46.6333, LocalDateTime.now(), true, null, LocalDateTime.now(), LocalDateTime.now()),
                new MobileCommunication(2, 2, 2, LocalDateTime.now(), -23.5505, -46.6333, LocalDateTime.now(), true, null, LocalDateTime.now(), LocalDateTime.now())
        );
        when(mobileCommunicationDAO.findAll()).thenReturn(expectedComms);

        List<MobileCommunication> result = mobileCommunicationService.getAllMobileCommunications();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(mobileCommunicationDAO, times(1)).findAll();
    }

    @Test
    void testGetMobileCommunicationsByDriverId() throws BusinessException, SQLException {
        int driverId = 1;
        List<MobileCommunication> expectedComms = Arrays.asList(
                new MobileCommunication(1, driverId, 1, LocalDateTime.now(), -23.5505, -46.6333, LocalDateTime.now(), true, null, LocalDateTime.now(), LocalDateTime.now()),
                new MobileCommunication(2, driverId, 2, LocalDateTime.now(), -23.5505, -46.6333, LocalDateTime.now(), true, null, LocalDateTime.now(), LocalDateTime.now())
        );
        when(mobileCommunicationDAO.findByDriverId(driverId)).thenReturn(expectedComms);

        List<MobileCommunication> result = mobileCommunicationService.getMobileCommunicationsByDriverId(driverId);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(mobileCommunicationDAO, times(1)).findByDriverId(driverId);
    }

    @Test
    void testGetMobileCommunicationsByRecordId() throws BusinessException, SQLException {
        int recordId = 1;
        List<MobileCommunication> expectedComms = Arrays.asList(
                new MobileCommunication(1, 1, recordId, LocalDateTime.now(), -23.5505, -46.6333, LocalDateTime.now(), true, null, LocalDateTime.now(), LocalDateTime.now()),
                new MobileCommunication(2, 2, recordId, LocalDateTime.now(), -23.5505, -46.6333, LocalDateTime.now(), true, null, LocalDateTime.now(), LocalDateTime.now())
        );
        when(mobileCommunicationDAO.findByRecordId(recordId)).thenReturn(expectedComms);

        List<MobileCommunication> result = mobileCommunicationService.getMobileCommunicationsByRecordId(recordId);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(mobileCommunicationDAO, times(1)).findByRecordId(recordId);
    }

    @Test
    void testUpdateMobileCommunicationSuccess() throws BusinessException, SQLException {
        int commId = 1;
        MobileCommunication existingComm = new MobileCommunication(
                commId, 1, 1, LocalDateTime.now(), -23.5505, -46.6333, LocalDateTime.now(), true, null, LocalDateTime.now(), LocalDateTime.now()
        );
        MobileCommunication updatedComm = new MobileCommunication(
                commId, 1, 1, LocalDateTime.now().plusHours(1), -23.6000, -46.7000, LocalDateTime.now().plusHours(1), false, "Erro Teste", LocalDateTime.now(), LocalDateTime.now()
        );

        when(mobileCommunicationDAO.findById(commId)).thenReturn(Optional.of(existingComm));
        when(mobileCommunicationDAO.update(any(MobileCommunication.class))).thenReturn(true);

        MobileCommunication result = mobileCommunicationService.updateMobileCommunication(updatedComm);

        assertNotNull(result);
        assertEquals(updatedComm.getLatitude(), result.getLatitude());
        assertEquals(updatedComm.getErrorMessage(), result.getErrorMessage());
        verify(mobileCommunicationDAO, times(1)).findById(commId);
        verify(mobileCommunicationDAO, times(1)).update(any(MobileCommunication.class));
    }

    @Test
    void testUpdateMobileCommunicationNotFound() throws BusinessException, SQLException {
        int commId = 99;
        MobileCommunication updatedComm = new MobileCommunication(
                commId, 1, 1, LocalDateTime.now(), -23.5505, -46.6333, LocalDateTime.now(), true, null, LocalDateTime.now(), LocalDateTime.now()
        );

        when(mobileCommunicationDAO.findById(commId)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            mobileCommunicationService.updateMobileCommunication(updatedComm);
        });
        assertEquals("Comunicação móvel não encontrada para atualização.", exception.getMessage());
        verify(mobileCommunicationDAO, times(1)).findById(commId);
        verify(mobileCommunicationDAO, never()).update(any(MobileCommunication.class));
    }

    @Test
    void testDeleteMobileCommunicationSuccess() throws BusinessException, SQLException {
        int commId = 1;
        when(mobileCommunicationDAO.findById(commId)).thenReturn(Optional.of(
                new MobileCommunication(commId, 1, 1, LocalDateTime.now(), -23.5505, -46.6333, LocalDateTime.now(), true, null, LocalDateTime.now(), LocalDateTime.now())
        )); // Mock para simular que existe
        when(mobileCommunicationDAO.delete(commId)).thenReturn(true);

        boolean result = mobileCommunicationService.deleteMobileCommunication(commId);

        assertTrue(result);
        verify(mobileCommunicationDAO, times(1)).delete(commId);
    }

    @Test
    void testDeleteMobileCommunicationNotFound() throws BusinessException, SQLException {
        int commId = 99;
        when(mobileCommunicationDAO.findById(commId)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            mobileCommunicationService.deleteMobileCommunication(commId);
        });
        assertEquals("Comunicação móvel não encontrada para exclusão.", exception.getMessage());
        verify(mobileCommunicationDAO, never()).delete(anyInt());
    }
}
