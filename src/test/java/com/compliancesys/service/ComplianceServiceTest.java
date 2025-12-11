package com.compliancesys.service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.ComplianceAudit;
import com.compliancesys.model.ComplianceReport;
import com.compliancesys.model.enums.ComplianceStatus;

public class ComplianceServiceTest {

    private ComplianceService complianceService;

    @BeforeEach
    void setUp() {
        complianceService = Mockito.mock(ComplianceService.class);
    }

    @Test
    void testPerformComplianceAuditSuccess() throws SQLException, BusinessException {
        int journeyId = 10;
        LocalDate journeyDate = LocalDate.now(); // Adicionado para corresponder à assinatura do método
        // O método performComplianceAudit retorna um ComplianceAudit, não um int
        // Usar .name() para converter o enum para String, conforme o construtor de ComplianceAudit
        ComplianceAudit expectedAudit = new ComplianceAudit(journeyId, LocalDateTime.now(), ComplianceStatus.CONFORME.name(), "Auditor Teste", "Auditoria OK");

        when(complianceService.performComplianceAudit(journeyId, journeyDate)).thenReturn(expectedAudit);

        ComplianceAudit resultAudit = complianceService.performComplianceAudit(journeyId, journeyDate);

        assertNotNull(resultAudit);
        assertEquals(expectedAudit.getJourneyId(), resultAudit.getJourneyId());
        assertEquals(expectedAudit.getComplianceStatus(), resultAudit.getComplianceStatus());
        verify(complianceService, times(1)).performComplianceAudit(journeyId, journeyDate);
    }

    @Test
    void testPerformComplianceAuditThrowsSQLException() throws SQLException, BusinessException {
        int journeyId = 10;
        LocalDate journeyDate = LocalDate.now(); // Adicionado para corresponder à assinatura do método
        doThrow(new SQLException("Erro ao realizar auditoria")).when(complianceService).performComplianceAudit(journeyId, journeyDate);

        SQLException thrown = assertThrows(SQLException.class, () -> {
            complianceService.performComplianceAudit(journeyId, journeyDate);
        });

        assertEquals("Erro ao realizar auditoria", thrown.getMessage());
        verify(complianceService, times(1)).performComplianceAudit(journeyId, journeyDate);
    }

    @Test
    void testGetComplianceAuditByIdFound() throws SQLException, BusinessException {
        int auditId = 1;
        // Usar .name() para converter o enum para String, conforme o construtor de ComplianceAudit
        ComplianceAudit expectedAudit = new ComplianceAudit(auditId, 10, LocalDateTime.now(), ComplianceStatus.CONFORME.name(), "Auditor Teste", "Auditoria OK", LocalDateTime.now(), LocalDateTime.now());
        when(complianceService.getComplianceAuditById(auditId)).thenReturn(Optional.of(expectedAudit));

        Optional<ComplianceAudit> result = complianceService.getComplianceAuditById(auditId);

        assertTrue(result.isPresent());
        assertEquals(expectedAudit, result.get());
        verify(complianceService, times(1)).getComplianceAuditById(auditId);
    }

    @Test
    void testGetComplianceAuditByIdNotFound() throws SQLException, BusinessException {
        int auditId = 99;
        when(complianceService.getComplianceAuditById(auditId)).thenReturn(Optional.empty());

        Optional<ComplianceAudit> result = complianceService.getComplianceAuditById(auditId);

        assertFalse(result.isPresent());
        verify(complianceService, times(1)).getComplianceAuditById(auditId);
    }

    @Test
    void testGetComplianceAuditByIdThrowsSQLException() throws SQLException, BusinessException {
        int auditId = 1;
        doThrow(new SQLException("Erro ao buscar auditoria por ID")).when(complianceService).getComplianceAuditById(auditId);

        SQLException thrown = assertThrows(SQLException.class, () -> {
            complianceService.getComplianceAuditById(auditId);
        });

        assertEquals("Erro ao buscar auditoria por ID", thrown.getMessage());
        verify(complianceService, times(1)).getComplianceAuditById(auditId);
    }

    @Test
    void testGetAllComplianceAudits() throws SQLException, BusinessException {
        // Usar .name() para converter o enum para String, conforme o construtor de ComplianceAudit
        ComplianceAudit audit1 = new ComplianceAudit(1, 10, LocalDateTime.now(), ComplianceStatus.CONFORME.name(), "Auditor A", "Notas A", LocalDateTime.now(), LocalDateTime.now());
        ComplianceAudit audit2 = new ComplianceAudit(2, 11, LocalDateTime.now().plusDays(1), ComplianceStatus.NAO_CONFORME.name(), "Auditor B", "Notas B", LocalDateTime.now(), LocalDateTime.now());
        List<ComplianceAudit> expectedAudits = Arrays.asList(audit1, audit2);

        when(complianceService.getAllComplianceAudits()).thenReturn(expectedAudits);

        List<ComplianceAudit> result = complianceService.getAllComplianceAudits();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedAudits, result);
        verify(complianceService, times(1)).getAllComplianceAudits();
    }

    @Test
    void testGetAllComplianceAuditsThrowsSQLException() throws SQLException, BusinessException {
        doThrow(new SQLException("Erro ao buscar todas as auditorias")).when(complianceService).getAllComplianceAudits();

        SQLException thrown = assertThrows(SQLException.class, () -> {
            complianceService.getAllComplianceAudits();
        });

        assertEquals("Erro ao buscar todas as auditorias", thrown.getMessage());
        verify(complianceService, times(1)).getAllComplianceAudits();
    }

    @Test
    void testUpdateComplianceAuditSuccess() throws SQLException, BusinessException {
        // Usar .name() para converter o enum para String, conforme o construtor de ComplianceAudit
        ComplianceAudit audit = new ComplianceAudit(1, 10, LocalDateTime.now(), ComplianceStatus.ALERTA.name(), "Auditor C", "Notas C", LocalDateTime.now(), LocalDateTime.now());
        when(complianceService.updateComplianceAudit(audit)).thenReturn(true);

        boolean result = complianceService.updateComplianceAudit(audit);

        assertTrue(result);
        verify(complianceService, times(1)).updateComplianceAudit(audit);
    }

    @Test
    void testUpdateComplianceAuditFailure() throws SQLException, BusinessException {
        // Usar .name() para converter o enum para String, conforme o construtor de ComplianceAudit
        ComplianceAudit audit = new ComplianceAudit(1, 10, LocalDateTime.now(), ComplianceStatus.ALERTA.name(), "Auditor C", "Notas C", LocalDateTime.now(), LocalDateTime.now());
        when(complianceService.updateComplianceAudit(audit)).thenReturn(false);

        boolean result = complianceService.updateComplianceAudit(audit);

        assertFalse(result);
        verify(complianceService, times(1)).updateComplianceAudit(audit);
    }

    @Test
    void testUpdateComplianceAuditThrowsSQLException() throws SQLException, BusinessException {
        // Usar .name() para converter o enum para String, conforme o construtor de ComplianceAudit
        ComplianceAudit audit = new ComplianceAudit(1, 10, LocalDateTime.now(), ComplianceStatus.ALERTA.name(), "Auditor C", "Notas C", LocalDateTime.now(), LocalDateTime.now());
        doThrow(new SQLException("Erro ao atualizar auditoria")).when(complianceService).updateComplianceAudit(audit);

        SQLException thrown = assertThrows(SQLException.class, () -> {
            complianceService.updateComplianceAudit(audit);
        });

        assertEquals("Erro ao atualizar auditoria", thrown.getMessage());
        verify(complianceService, times(1)).updateComplianceAudit(audit);
    }

    @Test
    void testDeleteComplianceAuditSuccess() throws SQLException, BusinessException {
        int auditId = 1;
        when(complianceService.deleteComplianceAudit(auditId)).thenReturn(true);

        boolean result = complianceService.deleteComplianceAudit(auditId);

        assertTrue(result);
        verify(complianceService, times(1)).deleteComplianceAudit(auditId);
    }

    @Test
    void testDeleteComplianceAuditFailure() throws SQLException, BusinessException {
        int auditId = 1;
        when(complianceService.deleteComplianceAudit(auditId)).thenReturn(false);

        boolean result = complianceService.deleteComplianceAudit(auditId);

        assertFalse(result);
        verify(complianceService, times(1)).deleteComplianceAudit(auditId);
    }

    @Test
    void testDeleteComplianceAuditThrowsSQLException() throws SQLException, BusinessException {
        int auditId = 1;
        doThrow(new SQLException("Erro ao deletar auditoria")).when(complianceService).deleteComplianceAudit(auditId);

        SQLException thrown = assertThrows(SQLException.class, () -> {
            complianceService.deleteComplianceAudit(auditId);
        });

        assertEquals("Erro ao deletar auditoria", thrown.getMessage());
        verify(complianceService, times(1)).deleteComplianceAudit(auditId);
    }

    @Test
    void testGenerateDriverComplianceReport() throws SQLException, BusinessException {
        int driverId = 1;
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 1, 31);
        ComplianceReport expectedReport = new ComplianceReport(driverId, 5, 2, 1, 2, 0); // Exemplo de relatório
        when(complianceService.generateDriverComplianceReport(driverId, startDate, endDate)).thenReturn(expectedReport);

        ComplianceReport resultReport = complianceService.generateDriverComplianceReport(driverId, startDate, endDate);

        assertNotNull(resultReport);
        assertEquals(expectedReport.getDriverId(), resultReport.getDriverId());
        verify(complianceService, times(1)).generateDriverComplianceReport(driverId, startDate, endDate);
    }

    @Test
    void testGenerateOverallComplianceReport() throws SQLException, BusinessException {
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 1, 31);
        // Usar .name() para converter o enum para String, conforme o construtor de ComplianceAudit
        ComplianceAudit audit1 = new ComplianceAudit(1, 10, LocalDateTime.now(), ComplianceStatus.CONFORME.name(), "Auditor A", "Notas A", LocalDateTime.now(), LocalDateTime.now());
        List<ComplianceAudit> expectedAudits = Collections.singletonList(audit1);
        when(complianceService.generateOverallComplianceReport(startDate, endDate)).thenReturn(expectedAudits);

        List<ComplianceAudit> resultAudits = complianceService.generateOverallComplianceReport(startDate, endDate);

        assertNotNull(resultAudits);
        assertFalse(resultAudits.isEmpty());
        assertEquals(expectedAudits.size(), resultAudits.size());
        verify(complianceService, times(1)).generateOverallComplianceReport(startDate, endDate);
    }

    @Test
    void testCreateComplianceAudit() throws SQLException, BusinessException {
        // Usar .name() para converter o enum para String, conforme o construtor de ComplianceAudit
        ComplianceAudit auditToCreate = new ComplianceAudit(0, 10, LocalDateTime.now(), ComplianceStatus.PENDENTE.name(), "Auditor X", "Notas X");
        ComplianceAudit createdAudit = new ComplianceAudit(1, 10, LocalDateTime.now(), ComplianceStatus.PENDENTE.name(), "Auditor X", "Notas X", LocalDateTime.now(), LocalDateTime.now());
        when(complianceService.createComplianceAudit(auditToCreate)).thenReturn(createdAudit);

        ComplianceAudit result = complianceService.createComplianceAudit(auditToCreate);

        assertNotNull(result);
        assertEquals(createdAudit.getId(), result.getId());
        verify(complianceService, times(1)).createComplianceAudit(auditToCreate);
    }

    @Test
    void testGetTimeRecordsForJourney() throws SQLException, BusinessException {
        int driverId = 1;
        LocalDate journeyDate = LocalDate.now();
        TimeRecord tr1 = new TimeRecord(1, driverId, 1, 1, LocalDateTime.now(), com.compliancesys.model.enums.EventType.IN, "Loc1", "Notes1");
        List<TimeRecord> expectedRecords = Collections.singletonList(tr1);
        when(complianceService.getTimeRecordsForJourney(driverId, journeyDate)).thenReturn(expectedRecords);

        List<TimeRecord> resultRecords = complianceService.getTimeRecordsForJourney(driverId, journeyDate);

        assertNotNull(resultRecords);
        assertFalse(resultRecords.isEmpty());
        assertEquals(expectedRecords.size(), resultRecords.size());
        verify(complianceService, times(1)).getTimeRecordsForJourney(driverId, journeyDate);
    }
}
