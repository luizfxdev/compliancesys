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
import static org.mockito.ArgumentMatchers.*;
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
        // Configurações padrão para o validator, para evitar BusinessException em testes que não focam na validação
        when(validator.isValidId(anyInt())).thenReturn(true);
        when(validator.isValidDriverId(anyInt())).thenReturn(true);
        when(validator.isValidRecordId(anyInt())).thenReturn(true);
        when(validator.isValidTimestamp(any(LocalDateTime.class))).thenReturn(true);
        when(validator.isValidLatitude(anyDouble())).thenReturn(true); // Assumindo que isValidLatitude existe
        when(validator.isValidLongitude(anyDouble())).thenReturn(true); // Assumindo que isValidLongitude existe
        when(validator.isValidBoolean(anyBoolean())).thenReturn(true);
        when(validator.isValidString(anyString())).thenReturn(true); // Para errorMessage
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
        MobileCommunication createdComm = new MobileCommunication(
                1, // id
                1, // driverId
                1, // recordId
                LocalDateTime.now(), // timestamp
                -23.5505, // latitude
                -46.6333, // longitude
                LocalDateTime.now(), // sendTimestamp
                true, // sendSuccess
                null, // errorMessage
                LocalDateTime.now(), // createdAt
                LocalDateTime.now() // updatedAt
        );

        when(mobileCommunicationDAO.create(any(MobileCommunication.class))).thenReturn(createdComm.getId());
        when(mobileCommunicationDAO.findById(createdComm.getId())).thenReturn(Optional.of(createdComm));

        MobileCommunication registeredComm = mobileCommunicationService.createMobileCommunication(newComm);

        assertNotNull(registeredComm);
        assertEquals(createdComm.getId(), registeredComm.getId());
        verify(mobileCommunicationDAO, times(1)).create(any(MobileCommunication.class));
    }

    @Test
    void testCreateMobileCommunicationInvalidDriverId() {
        MobileCommunication newComm = new MobileCommunication(
                -1, // driverId inválido
                1, // recordId
                LocalDateTime.now(), // timestamp
                -23.5505, // latitude
                -46.6333, // longitude
                LocalDateTime.now(), // sendTimestamp
                true, // sendSuccess
                null // errorMessage
        );
        when(validator.isValidDriverId(anyInt())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            mobileCommunicationService.createMobileCommunication(newComm);
        });
        assertEquals("ID do motorista inválido.", exception.getMessage());
        verify(mobileCommunicationDAO, never()).create(any(MobileCommunication.class));
    }

    @Test
    void testGetMobileCommunicationByIdFound() throws BusinessException, SQLException {
        int commId = 1;
        MobileCommunication expectedComm = new MobileCommunication(
                commId, // id
                1, // driverId
                1, // recordId
                LocalDateTime.now(), // timestamp
                -23.5505, // latitude
                -46.6333, // longitude
                LocalDateTime.now(), // sendTimestamp
                true, // sendSuccess
                null, // errorMessage
                LocalDateTime.now(), // createdAt
                LocalDateTime.now() // updatedAt
        );

        when(mobileCommunicationDAO.findById(commId)).thenReturn(Optional.of(expectedComm));

        Optional<MobileCommunication> foundComm = mobileCommunicationService.getMobileCommunicationById(commId);

        assertTrue(foundComm.isPresent());
        assertEquals(expectedComm.getId(), foundComm.get().getId());
        verify(mobileCommunicationDAO, times(1)).findById(commId);
    }

    @Test
    void testGetMobileCommunicationByIdNotFound() throws BusinessException, SQLException {
        int commId = 99;
        when(mobileCommunicationDAO.findById(commId)).thenReturn(Optional.empty());

        Optional<MobileCommunication> foundComm = mobileCommunicationService.getMobileCommunicationById(commId);

        assertFalse(foundComm.isPresent());
        verify(mobileCommunicationDAO, times(1)).findById(commId);
    }

    @Test
    void testGetAllMobileCommunications() throws BusinessException, SQLException {
        MobileCommunication comm1 = new MobileCommunication(
                1, 1, 1, LocalDateTime.now(), -23.5505, -46.6333, LocalDateTime.now(), true, null, LocalDateTime.now(), LocalDateTime.now());
        MobileCommunication comm2 = new MobileCommunication(
                2, 2, 2, LocalDateTime.now(), -23.5600, -46.6400, LocalDateTime.now(), false, "Error", LocalDateTime.now(), LocalDateTime.now());
        List<MobileCommunication> expectedComms = Arrays.asList(comm1, comm2);

        when(mobileCommunicationDAO.findAll()).thenReturn(expectedComms);

        List<MobileCommunication> actualComms = mobileCommunicationService.getAllMobileCommunications();

        assertNotNull(actualComms);
        assertEquals(2, actualComms.size());
        assertEquals(expectedComms, actualComms);
        verify(mobileCommunicationDAO, times(1)).findAll();
    }

    @Test
    void testGetAllMobileCommunicationsEmpty() throws BusinessException, SQLException {
        when(mobileCommunicationDAO.findAll()).thenReturn(Collections.emptyList());

        List<MobileCommunication> actualComms = mobileCommunicationService.getAllMobileCommunications();

        assertNotNull(actualComms);
        assertTrue(actualComms.isEmpty());
        verify(mobileCommunicationDAO, times(1)).findAll();
    }

    @Test
    void testUpdateMobileCommunicationSuccess() throws BusinessException, SQLException {
        int commId = 1;
        MobileCommunication existingComm = new MobileCommunication(
                commId, 1, 1, LocalDateTime.now(), -23.5505, -46.6333, LocalDateTime.now(), true, null, LocalDateTime.now(), LocalDateTime.now());
        MobileCommunication updatedComm = new MobileCommunication(
                commId, 1, 1, LocalDateTime.now().plusHours(1), -23.6000, -46.7000, LocalDateTime.now().plusHours(1), false, "Updated Error", LocalDateTime.now(), LocalDateTime.now());

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
                commId, 1, 1, LocalDateTime.now(), -23.6000, -46.7000, LocalDateTime.now(), false, "Updated Error", LocalDateTime.now(), LocalDateTime.now());

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
        when(mobileCommunicationDAO.findById(commId)).thenReturn(Optional.of(new MobileCommunication()));
        when(mobileCommunicationDAO.delete(commId)).thenReturn(true);

        mobileCommunicationService.deleteMobileCommunication(commId);

        verify(mobileCommunicationDAO, times(1)).findById(commId);
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
        verify(mobileCommunicationDAO, times(1)).findById(commId);
        verify(mobileCommunicationDAO, never()).delete(anyInt());
    }
}
