package com.compliancesys.service;

import com.compliancesys.model.ComplianceAudit;
import com.compliancesys.model.Driver;
import com.compliancesys.model.Journey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ComplianceServiceTest {

    private ComplianceService complianceService;

    @BeforeEach
    void setUp() {
        // Inicializa o mock do ComplianceService antes de cada teste
        complianceService = Mockito.mock(ComplianceService.class);
    }

    @Test
    void testPerformComplianceAuditSuccess() throws SQLException, IllegalArgumentException {
        int journeyId = 1;
        when(complianceService.performComplianceAudit(journeyId)).thenReturn(101); // Simula a criação de um ID de auditoria

        int auditId = complianceService.performComplianceAudit(journeyId);

        assertEquals(101, auditId);
        verify(complianceService, times(1)).performComplianceAudit(journeyId);
    }

    @Test
    void testPerformComplianceAuditInvalidJourney() throws SQLException, IllegalArgumentException {
        int invalidJourneyId = 999;
        // Simula a exceção IllegalArgumentException para jornada não encontrada ou inválida
        doThrow(new IllegalArgumentException("Jornada não encontrada ou inválida")).when(complianceService).performComplianceAudit(invalidJourneyId);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            complianceService.performComplianceAudit(invalidJourneyId);
        });

        assertEquals("Jornada não encontrada ou inválida", thrown.getMessage());
        verify(complianceService, times(1)).performComplianceAudit(invalidJourneyId);
    }

    @Test
    void testPerformComplianceAuditThrowsSQLException() throws SQLException, IllegalArgumentException {
        int journeyId = 1;
        // Simula a exceção SQLException em caso de erro no banco de dados
        doThrow(new SQLException("Erro de acesso ao banco de dados durante a auditoria")).when(complianceService).performComplianceAudit(journeyId);

        SQLException thrown = assertThrows(SQLException.class, () -> {
            complianceService.performComplianceAudit(journeyId);
        });

        assertEquals("Erro de acesso ao banco de dados durante a auditoria", thrown.getMessage());
        verify(complianceService, times(1)).performComplianceAudit(journeyId);
    }

    @Test
    void testGetComplianceAuditByIdFound() throws SQLException {
        ComplianceAudit expectedAudit = new ComplianceAudit(1, 10, LocalDateTime.now(), true, "OK");
        when(complianceService.getComplianceAuditById(1)).thenReturn(Optional.of(expectedAudit));

        Optional<ComplianceAudit> result = complianceService.getComplianceAuditById(1);

        assertTrue(result.isPresent());
        assertEquals(expectedAudit, result.get());
        verify(complianceService, times(1)).getComplianceAuditById(1);
    }

    @Test
    void testGetComplianceAuditByIdNotFound() throws SQLException {
        when(complianceService.getComplianceAuditById(99)).thenReturn(Optional.empty());

        Optional<ComplianceAudit> result = complianceService.getComplianceAuditById(99);

        assertFalse(result.isPresent());
        verify(complianceService, times(1)).getComplianceAuditById(99);
    }

    @Test
    void testGetComplianceAuditByIdThrowsSQLException() throws SQLException {
        when(complianceService.getComplianceAuditById(1)).thenThrow(new SQLException("Erro ao buscar auditoria por ID"));

        SQLException thrown = assertThrows(SQLException.class, () -> {
            complianceService.getComplianceAuditById(1);
        });

        assertEquals("Erro ao buscar auditoria por ID", thrown.getMessage());
        verify(complianceService, times(1)).getComplianceAuditById(1);
    }

    @Test
    void testGetAllComplianceAudits() throws SQLException {
        ComplianceAudit audit1 = new ComplianceAudit(1, 10, LocalDateTime.now(), true, "OK");
        ComplianceAudit audit2 = new ComplianceAudit(2, 11, LocalDateTime.now().plusDays(1), false, "Infração de tempo");
        List<ComplianceAudit> expectedList = Arrays.asList(audit1, audit2);

        when(complianceService.getAllComplianceAudits()).thenReturn(expectedList);

        List<ComplianceAudit> result = complianceService.getAllComplianceAudits();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedList, result);
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
        when(complianceService.getAllComplianceAudits()).thenThrow(new SQLException("Erro ao listar todas as auditorias"));

        SQLException thrown = assertThrows(SQLException.class, () -> {
            complianceService.getAllComplianceAudits();
        });

        assertEquals("Erro ao listar todas as auditorias", thrown.getMessage());
        verify(complianceService, times(1)).getAllComplianceAudits();
    }

    @Test
    void testGetComplianceAuditsByJourneyId() throws SQLException {
        ComplianceAudit audit1 = new ComplianceAudit(1, 10, LocalDateTime.now(), true, "OK");
        ComplianceAudit audit2 = new ComplianceAudit(3, 10, LocalDateTime.now().plusHours(2), false, "Excesso de velocidade");
        List<ComplianceAudit> expectedList = Arrays.asList(audit1, audit2);

        when(complianceService.getComplianceAuditsByJourneyId(10)).thenReturn(expectedList);

        List<ComplianceAudit> result = complianceService.getComplianceAuditsByJourneyId(10);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedList, result);
        verify(complianceService, times(1)).getComplianceAuditsByJourneyId(10);
    }

    @Test
    void testGetComplianceAuditsByJourneyIdNoAudits() throws SQLException {
        when(complianceService.getComplianceAuditsByJourneyId(99)).thenReturn(Collections.emptyList());

        List<ComplianceAudit> result = complianceService.getComplianceAuditsByJourneyId(99);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(complianceService, times(1)).getComplianceAuditsByJourneyId(99);
    }

    @Test
    void testGetComplianceAuditsByJourneyIdThrowsSQLException() throws SQLException {
        when(complianceService.getComplianceAuditsByJourneyId(10)).thenThrow(new SQLException("Erro ao buscar auditorias por jornada"));

        SQLException thrown = assertThrows(SQLException.class, () -> {
            complianceService.getComplianceAuditsByJourneyId(10);
        });

        assertEquals("Erro ao buscar auditorias por jornada", thrown.getMessage());
        verify(complianceService, times(1)).getComplianceAuditsByJourneyId(10);
    }

    @Test
    void testGenerateDriverComplianceReport() throws SQLException {
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 31);
        ComplianceAudit audit1 = new ComplianceAudit(1, 10, LocalDateTime.of(2025, 1, 15, 8, 0), true, "OK");
        ComplianceAudit audit2 = new ComplianceAudit(2, 11, LocalDateTime.of(2025, 1, 20, 10, 0), false, "Pausa insuficiente");
        List<ComplianceAudit> expectedReport = Arrays.asList(audit1, audit2);

        when(complianceService.generateDriverComplianceReport(1, startDate, endDate)).thenReturn(expectedReport);

        List<ComplianceAudit> result = complianceService.generateDriverComplianceReport(1, startDate, endDate);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedReport, result);
        verify(complianceService, times(1)).generateDriverComplianceReport(1, startDate, endDate);
    }

    @Test
    void testGenerateDriverComplianceReportEmpty() throws SQLException {
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 31);

        when(complianceService.generateDriverComplianceReport(1, startDate, endDate)).thenReturn(Collections.emptyList());

        List<ComplianceAudit> result = complianceService.generateDriverComplianceReport(1, startDate, endDate);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(complianceService, times(1)).generateDriverComplianceReport(1, startDate, endDate);
    }

    @Test
    void testGenerateDriverComplianceReportThrowsSQLException() throws SQLException {
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 31);

        when(complianceService.generateDriverComplianceReport(1, startDate, endDate)).thenThrow(new SQLException("Erro ao gerar relatório do motorista"));

        SQLException thrown = assertThrows(SQLException.class, () -> {
            complianceService.generateDriverComplianceReport(1, startDate, endDate);
        });

        assertEquals("Erro ao gerar relatório do motorista", thrown.getMessage());
        verify(complianceService, times(1)).generateDriverComplianceReport(1, startDate, endDate);
    }

    @Test
    void testGenerateOverallComplianceReport() throws SQLException {
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 31);
        ComplianceAudit audit1 = new ComplianceAudit(1, 10, LocalDateTime.of(2025, 1, 15, 8, 0), true, "OK");
        ComplianceAudit audit2 = new ComplianceAudit(2, 11, LocalDateTime.of(2025, 1, 20, 10, 0), false, "Pausa insuficiente");
        ComplianceAudit audit3 = new ComplianceAudit(3, 12, LocalDateTime.of(2025, 1, 25, 14, 0), true, "OK");
        List<ComplianceAudit> expectedReport = Arrays.asList(audit1, audit2, audit3);

        when(complianceService.generateOverallComplianceReport(startDate, endDate)).thenReturn(expectedReport);

        List<ComplianceAudit> result = complianceService.generateOverallComplianceReport(startDate, endDate);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(expectedReport, result);
        verify(complianceService, times(1)).generateOverallComplianceReport(startDate, endDate);
    }

    @Test
    void testGenerateOverallComplianceReportEmpty() throws SQLException {
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 31);

        when(complianceService.generateOverallComplianceReport(startDate, endDate)).thenReturn(Collections.emptyList());

        List<ComplianceAudit> result = complianceService.generateOverallComplianceReport(startDate, endDate);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(complianceService, times(1)).generateOverallComplianceReport(startDate, endDate);
    }

    @Test
    void testGenerateOverallComplianceReportThrowsSQLException() throws SQLException {
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 31);

        when(complianceService.generateOverallComplianceReport(startDate, endDate)).thenThrow(new SQLException("Erro ao gerar relatório geral"));

        SQLException thrown = assertThrows(SQLException.class, () -> {
            complianceService.generateOverallComplianceReport(startDate, endDate);
        });

        assertEquals("Erro ao gerar relatório geral", thrown.getMessage());
        verify(complianceService, times(1)).generateOverallComplianceReport(startDate, endDate);
    }
}
