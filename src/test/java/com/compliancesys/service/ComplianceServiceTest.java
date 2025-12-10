package com.compliancesys.service;

import java.sql.SQLException;
import java.time.LocalDate; // Importar ComplianceReport
import java.time.LocalDateTime; // Importar ComplianceStatus
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    void testPerformComplianceAuditSuccess() throws SQLException {
        int journeyId = 10;
        // O método performComplianceAudit retorna um ComplianceAudit, não um int
        ComplianceAudit expectedAudit = new ComplianceAudit(journeyId, LocalDateTime.now(), ComplianceStatus.CONFORME, "Auditor Teste", "Auditoria OK");

        when(complianceService.performComplianceAudit(journeyId)).thenReturn(expectedAudit);

        ComplianceAudit resultAudit = complianceService.performComplianceAudit(journeyId);

        assertNotNull(resultAudit);
        assertEquals(expectedAudit.getJourneyId(), resultAudit.getJourneyId());
        assertEquals(expectedAudit.getComplianceStatus(), resultAudit.getComplianceStatus());
        verify(complianceService, times(1)).performComplianceAudit(journeyId);
    }

    @Test
    void testPerformComplianceAuditThrowsSQLException() throws SQLException {
        int journeyId = 10;
        doThrow(new SQLException("Erro ao realizar auditoria")).when(complianceService).performComplianceAudit(journeyId);

        SQLException thrown = assertThrows(SQLException.class, () -> {
            complianceService.performComplianceAudit(journeyId);
        });

        assertEquals("Erro ao realizar auditoria", thrown.getMessage());
        verify(complianceService, times(1)).performComplianceAudit(journeyId);
    }

    @Test
    void testGetComplianceAuditByIdFound() throws SQLException {
        int auditId = 1;
        ComplianceAudit expectedAudit = new ComplianceAudit(auditId, 10, LocalDateTime.now(), ComplianceStatus.CONFORME, "Auditor Teste", "Auditoria OK", LocalDateTime.now(), LocalDateTime.now());
        when(complianceService.getComplianceAuditById(auditId)).thenReturn(Optional.of(expectedAudit));

        Optional<ComplianceAudit> result = complianceService.getComplianceAuditById(auditId);

        assertTrue(result.isPresent());
        assertEquals(expectedAudit, result.get());
        verify(complianceService, times(1)).getComplianceAuditById(auditId);
    }

    @Test
    void testGetComplianceAuditByIdNotFound() throws SQLException {
        int auditId = 99;
        when(complianceService.getComplianceAuditById(auditId)).thenReturn(Optional.empty());

        Optional<ComplianceAudit> result = complianceService.getComplianceAuditById(auditId);

        assertFalse(result.isPresent());
        verify(complianceService, times(1)).getComplianceAuditById(auditId);
    }

    @Test
    void testGetComplianceAuditByIdThrowsSQLException() throws SQLException {
        int auditId = 1;
        doThrow(new SQLException("Erro ao buscar auditoria por ID")).when(complianceService).getComplianceAuditById(auditId);

        SQLException thrown = assertThrows(SQLException.class, () -> {
            complianceService.getComplianceAuditById(auditId);
        });

        assertEquals("Erro ao buscar auditoria por ID", thrown.getMessage());
        verify(complianceService, times(1)).getComplianceAuditById(auditId);
    }

    @Test
    void testGetAllComplianceAudits() throws SQLException {
        ComplianceAudit audit1 = new ComplianceAudit(1, 10, LocalDateTime.now(), ComplianceStatus.CONFORME, "Auditor A", "Notas A", LocalDateTime.now(), LocalDateTime.now());
        ComplianceAudit audit2 = new ComplianceAudit(2, 11, LocalDateTime.now().plusDays(1), ComplianceStatus.NAO_CONFORME, "Auditor B", "Notas B", LocalDateTime.now(), LocalDateTime.now());
        List<ComplianceAudit> expectedAudits = Arrays.asList(audit1, audit2);

        when(complianceService.getAllComplianceAudits()).thenReturn(expectedAudits);

        List<ComplianceAudit> result = complianceService.getAllComplianceAudits();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedAudits, result);
        verify(complianceService, times(1)).getAllComplianceAudits();
    }

    @Test
    void testGetAllComplianceAuditsEmpty() throws SQLException {
        when(complianceService.getAllComplianceAudits()).thenReturn(Collections.emptyList());

        List<ComplianceAudit> result = complianceService.getAllComplianceAudits();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(complianceService, times(1)).getAllComplianceAudits();
    }

    @Test
    void testGetAllComplianceAuditsThrowsSQLException() throws SQLException {
        doThrow(new SQLException("Erro ao buscar todas as auditorias")).when(complianceService).getAllComplianceAudits();

        SQLException thrown = assertThrows(SQLException.class, () -> {
            complianceService.getAllComplianceAudits();
        });

        assertEquals("Erro ao buscar todas as auditorias", thrown.getMessage());
        verify(complianceService, times(1)).getAllComplianceAudits();
    }

    @Test
    void testUpdateComplianceAuditSuccess() throws SQLException {
        ComplianceAudit existingAudit = new ComplianceAudit(1, 10, LocalDateTime.now(), ComplianceStatus.CONFORME, "Auditor Original", "Notas Originais", LocalDateTime.now(), LocalDateTime.now());
        ComplianceAudit updatedAudit = new ComplianceAudit(1, 10, LocalDateTime.now().plusHours(1), ComplianceStatus.ALERTA, "Auditor Atualizado", "Notas Atualizadas", LocalDateTime.now(), LocalDateTime.now());

        when(complianceService.updateComplianceAudit(updatedAudit)).thenReturn(updatedAudit);

        ComplianceAudit result = complianceService.updateComplianceAudit(updatedAudit);

        assertNotNull(result);
        assertEquals(updatedAudit, result);
        verify(complianceService, times(1)).updateComplianceAudit(updatedAudit);
    }

    @Test
    void testUpdateComplianceAuditNotFound() throws SQLException {
        ComplianceAudit nonExistentAudit = new ComplianceAudit(99, 10, LocalDateTime.now(), ComplianceStatus.CONFORME, "Auditor Teste", "Notas Teste", LocalDateTime.now(), LocalDateTime.now());
        when(complianceService.updateComplianceAudit(nonExistentAudit)).thenReturn(null); // Ou Optional.empty() se o método retornar Optional

        ComplianceAudit result = complianceService.updateComplianceAudit(nonExistentAudit);

        assertNull(result); // Ou assertFalse(result.isPresent())
        verify(complianceService, times(1)).updateComplianceAudit(nonExistentAudit);
    }

    @Test
    void testUpdateComplianceAuditThrowsSQLException() throws SQLException {
        ComplianceAudit auditToUpdate = new ComplianceAudit(1, 10, LocalDateTime.now(), ComplianceStatus.CONFORME, "Auditor Teste", "Notas Teste", LocalDateTime.now(), LocalDateTime.now());
        doThrow(new SQLException("Erro ao atualizar auditoria")).when(complianceService).updateComplianceAudit(auditToUpdate);

        SQLException thrown = assertThrows(SQLException.class, () -> {
            complianceService.updateComplianceAudit(auditToUpdate);
        });

        assertEquals("Erro ao atualizar auditoria", thrown.getMessage());
        verify(complianceService, times(1)).updateComplianceAudit(auditToUpdate);
    }

    @Test
    void testDeleteComplianceAuditSuccess() throws SQLException {
        int auditId = 1;
        when(complianceService.deleteComplianceAudit(auditId)).thenReturn(true);

        boolean deleted = complianceService.deleteComplianceAudit(auditId);

        assertTrue(deleted);
        verify(complianceService, times(1)).deleteComplianceAudit(auditId);
    }

    @Test
    void testDeleteComplianceAuditNotFound() throws SQLException {
        int auditId = 99;
        when(complianceService.deleteComplianceAudit(auditId)).thenReturn(false);

        boolean deleted = complianceService.deleteComplianceAudit(auditId);

        assertFalse(deleted);
        verify(complianceService, times(1)).deleteComplianceAudit(auditId);
    }

    @Test
    void testDeleteComplianceAuditThrowsSQLException() throws SQLException {
        int auditId = 1;
        doThrow(new SQLException("Erro ao deletar auditoria")).when(complianceService).deleteComplianceAudit(auditId);

        SQLException thrown = assertThrows(SQLException.class, () -> {
            complianceService.deleteComplianceAudit(auditId);
        });

        assertEquals("Erro ao deletar auditoria", thrown.getMessage());
        verify(complianceService, times(1)).deleteComplianceAudit(auditId);
    }

    @Test
    void testGenerateDriverComplianceReport() throws SQLException {
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 31);

        // Ajustado para usar o construtor completo de ComplianceReport
        // Parâmetros: reportName, generatedDate, driverId, driverName, startDate, endDate, totalAudits, compliantAudits, nonCompliantAudits, complianceRate, audits
        ComplianceReport expectedReport = new ComplianceReport(
                "Relatório de Conformidade do Motorista", // reportName
                LocalDate.now(),                          // generatedDate
                1,                                        // driverId
                "Nome do Motorista",                      // driverName
                startDate,                                // startDate
                endDate,                                  // endDate
                5,                                        // totalAudits
                2,                                        // compliantAudits
                3,                                        // nonCompliantAudits
                60.0,                                     // complianceRate
                Collections.emptyList()                   // audits (pode ser uma lista vazia ou mockada)
        );

        when(complianceService.generateDriverComplianceReport(1, startDate, endDate)).thenReturn(expectedReport);

        ComplianceReport resultReport = complianceService.generateDriverComplianceReport(1, startDate, endDate);

        assertNotNull(resultReport);
        assertEquals(expectedReport.getDriverId(), resultReport.getDriverId());
        assertEquals(expectedReport.getComplianceRate(), resultReport.getComplianceRate());
        verify(complianceService, times(1)).generateDriverComplianceReport(1, startDate, endDate);
    }

    @Test
    void testGenerateDriverComplianceReportThrowsSQLException() throws SQLException {
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 31);
        doThrow(new SQLException("Erro ao gerar relatório de conformidade do motorista")).when(complianceService).generateDriverComplianceReport(1, startDate, endDate);

        SQLException thrown = assertThrows(SQLException.class, () -> {
            complianceService.generateDriverComplianceReport(1, startDate, endDate);
        });

        assertEquals("Erro ao gerar relatório de conformidade do motorista", thrown.getMessage());
        verify(complianceService, times(1)).generateDriverComplianceReport(1, startDate, endDate);
    }

    @Test
    void testGenerateOverallComplianceReport() throws SQLException {
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 31);
        ComplianceAudit audit1 = new ComplianceAudit(1, 10, LocalDateTime.now(), ComplianceStatus.CONFORME, "Auditor A", "Notas A", LocalDateTime.now(), LocalDateTime.now());
        ComplianceAudit audit2 = new ComplianceAudit(2, 11, LocalDateTime.now().plusDays(1), ComplianceStatus.NAO_CONFORME, "Auditor B", "Notas B", LocalDateTime.now(), LocalDateTime.now());
        List<ComplianceAudit> expectedAudits = Arrays.asList(audit1, audit2);

        when(complianceService.generateOverallComplianceReport(startDate, endDate)).thenReturn(expectedAudits);

        List<ComplianceAudit> result = complianceService.generateOverallComplianceReport(startDate, endDate);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedAudits, result);
        verify(complianceService, times(1)).generateOverallComplianceReport(startDate, endDate);
    }

    @Test
    void testGenerateOverallComplianceReportThrowsSQLException() throws SQLException {
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 31);
        doThrow(new SQLException("Erro ao gerar relatório geral de conformidade")).when(complianceService).generateOverallComplianceReport(startDate, endDate);

        SQLException thrown = assertThrows(SQLException.class, () -> {
            complianceService.generateOverallComplianceReport(startDate, endDate);
        });

        assertEquals("Erro ao gerar relatório geral de conformidade", thrown.getMessage());
        verify(complianceService, times(1)).generateOverallComplianceReport(startDate, endDate);
    }
}
