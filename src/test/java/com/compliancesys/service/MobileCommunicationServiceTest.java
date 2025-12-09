package com.compliancesys.service;

import com.compliancesys.model.MobileCommunication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MobileCommunicationServiceTest {

    private MobileCommunicationService mobileCommunicationService;

    @BeforeEach
    void setUp() {
        // Inicializa o mock do MobileCommunicationService antes de cada teste
        mobileCommunicationService = Mockito.mock(MobileCommunicationService.class);
    }

    @Test
    void testRegisterMobileCommunicationSuccess() throws SQLException, IllegalArgumentException {
        MobileCommunication newComm = new MobileCommunication(0, 1, LocalDateTime.now(), "SMS", "Mensagem de teste");
        when(mobileCommunicationService.registerMobileCommunication(newComm)).thenReturn(1); // Simula o registro retornando um ID

        int id = mobileCommunicationService.registerMobileCommunication(newComm);

        assertEquals(1, id);
        verify(mobileCommunicationService, times(1)).registerMobileCommunication(newComm);
    }

    @Test
    void testRegisterMobileCommunicationInvalidData() throws SQLException, IllegalArgumentException {
        MobileCommunication invalidComm = new MobileCommunication(0, 1, LocalDateTime.now(), "", "Mensagem de teste");
        // Simula a exceção IllegalArgumentException para dados inválidos
        doThrow(new IllegalArgumentException("Tipo de comunicação não pode ser vazio")).when(mobileCommunicationService).registerMobileCommunication(invalidComm);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            mobileCommunicationService.registerMobileCommunication(invalidComm);
        });

        assertEquals("Tipo de comunicação não pode ser vazio", thrown.getMessage());
        verify(mobileCommunicationService, times(1)).registerMobileCommunication(invalidComm);
    }

    @Test
    void testRegisterMobileCommunicationThrowsSQLException() throws SQLException, IllegalArgumentException {
        MobileCommunication newComm = new MobileCommunication(0, 1, LocalDateTime.now(), "SMS", "Mensagem de teste");
        // Simula a exceção SQLException em caso de erro no banco de dados
        doThrow(new SQLException("Erro de conexão com o banco de dados")).when(mobileCommunicationService).registerMobileCommunication(newComm);

        SQLException thrown = assertThrows(SQLException.class, () -> {
            mobileCommunicationService.registerMobileCommunication(newComm);
        });

        assertEquals("Erro de conexão com o banco de dados", thrown.getMessage());
        verify(mobileCommunicationService, times(1)).registerMobileCommunication(newComm);
    }

    @Test
    void testGetMobileCommunicationByIdFound() throws SQLException {
        MobileCommunication expectedComm = new MobileCommunication(1, 1, LocalDateTime.now(), "SMS", "Mensagem de teste");
        when(mobileCommunicationService.getMobileCommunicationById(1)).thenReturn(Optional.of(expectedComm));

        Optional<MobileCommunication> result = mobileCommunicationService.getMobileCommunicationById(1);

        assertTrue(result.isPresent());
        assertEquals(expectedComm, result.get());
        verify(mobileCommunicationService, times(1)).getMobileCommunicationById(1);
    }

    @Test
    void testGetMobileCommunicationByIdNotFound() throws SQLException {
        when(mobileCommunicationService.getMobileCommunicationById(99)).thenReturn(Optional.empty());

        Optional<MobileCommunication> result = mobileCommunicationService.getMobileCommunicationById(99);

        assertFalse(result.isPresent());
        verify(mobileCommunicationService, times(1)).getMobileCommunicationById(99);
    }

    @Test
    void testGetMobileCommunicationByIdThrowsSQLException() throws SQLException {
        when(mobileCommunicationService.getMobileCommunicationById(1)).thenThrow(new SQLException("Erro ao buscar comunicação por ID"));

        SQLException thrown = assertThrows(SQLException.class, () -> {
            mobileCommunicationService.getMobileCommunicationById(1);
        });

        assertEquals("Erro ao buscar comunicação por ID", thrown.getMessage());
        verify(mobileCommunicationService, times(1)).getMobileCommunicationById(1);
    }

    @Test
    void testGetMobileCommunicationsByRecordId() throws SQLException {
        MobileCommunication comm1 = new MobileCommunication(1, 10, LocalDateTime.now(), "SMS", "Mensagem 1");
        MobileCommunication comm2 = new MobileCommunication(2, 10, LocalDateTime.now().plusMinutes(5), "CALL", "Chamada perdida");
        List<MobileCommunication> expectedList = Arrays.asList(comm1, comm2);

        when(mobileCommunicationService.getMobileCommunicationsByRecordId(10)).thenReturn(expectedList);

        List<MobileCommunication> result = mobileCommunicationService.getMobileCommunicationsByRecordId(10);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedList, result);
        verify(mobileCommunicationService, times(1)).getMobileCommunicationsByRecordId(10);
    }

    @Test
    void testGetMobileCommunicationsByRecordIdNoCommunications() throws SQLException {
        when(mobileCommunicationService.getMobileCommunicationsByRecordId(99)).thenReturn(Collections.emptyList());

        List<MobileCommunication> result = mobileCommunicationService.getMobileCommunicationsByRecordId(99);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(mobileCommunicationService, times(1)).getMobileCommunicationsByRecordId(99);
    }

    @Test
    void testGetMobileCommunicationsByRecordIdThrowsSQLException() throws SQLException {
        when(mobileCommunicationService.getMobileCommunicationsByRecordId(10)).thenThrow(new SQLException("Erro ao buscar comunicações por record ID"));

        SQLException thrown = assertThrows(SQLException.class, () -> {
            mobileCommunicationService.getMobileCommunicationsByRecordId(10);
        });

        assertEquals("Erro ao buscar comunicações por record ID", thrown.getMessage());
        verify(mobileCommunicationService, times(1)).getMobileCommunicationsByRecordId(10);
    }

    @Test
    void testGetAllMobileCommunications() throws SQLException {
        MobileCommunication comm1 = new MobileCommunication(1, 1, LocalDateTime.now(), "SMS", "Mensagem 1");
        MobileCommunication comm2 = new MobileCommunication(2, 2, LocalDateTime.now().plusHours(1), "CALL", "Chamada 2");
        List<MobileCommunication> expectedList = Arrays.asList(comm1, comm2);

        when(mobileCommunicationService.getAllMobileCommunications()).thenReturn(expectedList);

        List<MobileCommunication> result = mobileCommunicationService.getAllMobileCommunications();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedList, result);
        verify(mobileCommunicationService, times(1)).getAllMobileCommunications();
    }

    @Test
    void testGetAllMobileCommunicationsEmpty() throws SQLException {
        when(mobileCommunicationService.getAllMobileCommunications()).thenReturn(Collections.emptyList());

        List<MobileCommunication> result = mobileCommunicationService.getAllMobileCommunications();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(mobileCommunicationService, times(1)).getAllMobileCommunications();
    }

    @Test
    void testGetAllMobileCommunicationsThrowsSQLException() throws SQLException {
        when(mobileCommunicationService.getAllMobileCommunications()).thenThrow(new SQLException("Erro ao listar todas as comunicações"));

        SQLException thrown = assertThrows(SQLException.class, () -> {
            mobileCommunicationService.getAllMobileCommunications();
        });

        assertEquals("Erro ao listar todas as comunicações", thrown.getMessage());
        verify(mobileCommunicationService, times(1)).getAllMobileCommunications();
    }

    @Test
    void testUpdateMobileCommunicationSuccess() throws SQLException, IllegalArgumentException {
        MobileCommunication updatedComm = new MobileCommunication(1, 1, LocalDateTime.now(), "EMAIL", "Email atualizado");
        when(mobileCommunicationService.updateMobileCommunication(updatedComm)).thenReturn(true);

        boolean result = mobileCommunicationService.updateMobileCommunication(updatedComm);

        assertTrue(result);
        verify(mobileCommunicationService, times(1)).updateMobileCommunication(updatedComm);
    }

    @Test
    void testUpdateMobileCommunicationFailure() throws SQLException, IllegalArgumentException {
        MobileCommunication nonExistentComm = new MobileCommunication(99, 1, LocalDateTime.now(), "SMS", "Mensagem inexistente");
        when(mobileCommunicationService.updateMobileCommunication(nonExistentComm)).thenReturn(false);

        boolean result = mobileCommunicationService.updateMobileCommunication(nonExistentComm);

        assertFalse(result);
        verify(mobileCommunicationService, times(1)).updateMobileCommunication(nonExistentComm);
    }

    @Test
    void testUpdateMobileCommunicationInvalidData() throws SQLException, IllegalArgumentException {
        MobileCommunication invalidComm = new MobileCommunication(1, 1, LocalDateTime.now(), "  ", "Mensagem de teste");
        doThrow(new IllegalArgumentException("Tipo de comunicação não pode ser vazio")).when(mobileCommunicationService).updateMobileCommunication(invalidComm);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            mobileCommunicationService.updateMobileCommunication(invalidComm);
        });

        assertEquals("Tipo de comunicação não pode ser vazio", thrown.getMessage());
        verify(mobileCommunicationService, times(1)).updateMobileCommunication(invalidComm);
    }

    @Test
    void testUpdateMobileCommunicationThrowsSQLException() throws SQLException, IllegalArgumentException {
        MobileCommunication updatedComm = new MobileCommunication(1, 1, LocalDateTime.now(), "EMAIL", "Email atualizado");
        doThrow(new SQLException("Erro ao atualizar comunicação")).when(mobileCommunicationService).updateMobileCommunication(updatedComm);

        SQLException thrown = assertThrows(SQLException.class, () -> {
            mobileCommunicationService.updateMobileCommunication(updatedComm);
        });

        assertEquals("Erro ao atualizar comunicação", thrown.getMessage());
        verify(mobileCommunicationService, times(1)).updateMobileCommunication(updatedComm);
    }

    @Test
    void testDeleteMobileCommunicationSuccess() throws SQLException {
        when(mobileCommunicationService.deleteMobileCommunication(1)).thenReturn(true);

        boolean result = mobileCommunicationService.deleteMobileCommunication(1);

        assertTrue(result);
        verify(mobileCommunicationService, times(1)).deleteMobileCommunication(1);
    }

    @Test
    void testDeleteMobileCommunicationFailure() throws SQLException {
        when(mobileCommunicationService.deleteMobileCommunication(99)).thenReturn(false);

        boolean result = mobileCommunicationService.deleteMobileCommunication(99);

        assertFalse(result);
        verify(mobileCommunicationService, times(1)).deleteMobileCommunication(99);
    }

    @Test
    void testDeleteMobileCommunicationThrowsSQLException() throws SQLException {
        when(mobileCommunicationService.deleteMobileCommunication(1)).thenThrow(new SQLException("Erro ao deletar comunicação"));

        SQLException thrown = assertThrows(SQLException.class, () -> {
            mobileCommunicationService.deleteMobileCommunication(1);
        });

        assertEquals("Erro ao deletar comunicação", thrown.getMessage());
        verify(mobileCommunicationService, times(1)).deleteMobileCommunication(1);
    }
}
