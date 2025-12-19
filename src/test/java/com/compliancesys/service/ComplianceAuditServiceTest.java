package com.compliancesys.service;

import com.compliancesys.dao.ComplianceAuditDAO;
import com.compliancesys.dao.DriverDAO;
import com.compliancesys.dao.JourneyDAO;
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.ComplianceAudit;
import com.compliancesys.model.Driver;
import com.compliancesys.model.Journey;
import com.compliancesys.model.enums.ComplianceStatus;
import com.compliancesys.service.impl.ComplianceAuditServiceImpl;
import com.compliancesys.util.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComplianceAuditServiceTest {

    @Mock
    private ComplianceAuditDAO complianceAuditDAO;

    @Mock
    private JourneyDAO journeyDAO;

    @Mock
    private DriverDAO driverDAO;

    @Mock
    private Validator validator;

    @InjectMocks
    private ComplianceAuditServiceImpl complianceAuditService;

    private ComplianceAudit testAudit;
    private Journey testJourney;
    private Driver testDriver;

    @BeforeEach
    void setUp() {
        testDriver = new Driver();
        testDriver.setId(1);
        testDriver.setName("Motorista Teste");

        testJourney = new Journey();
        testJourney.setId(1);
        testJourney.setDriverId(1);
        testJourney.setJourneyDate(LocalDate.now());

        testAudit = new ComplianceAudit();
        testAudit.setId(1);
        testAudit.setJourneyId(1);
        testAudit.setDriverId(1);
        testAudit.setAuditDate(LocalDate.now());
        testAudit.setStatus(ComplianceStatus.PENDING);
        testAudit.setViolations("Nenhuma violação");
    }

    @Test
    @DisplayName("Deve criar auditoria com sucesso")
    void createAudit_Success() throws SQLException, BusinessException {
        doNothing().when(validator).validate(any(ComplianceAudit.class));
        when(journeyDAO.findById(1)).thenReturn(Optional.of(testJourney));
        when(driverDAO.findById(1)).thenReturn(Optional.of(testDriver));
        when(complianceAuditDAO.create(any(ComplianceAudit.class))).thenReturn(1);

        ComplianceAudit result = complianceAuditService.createAudit(testAudit);

        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(complianceAuditDAO).create(any(ComplianceAudit.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar auditoria com jornada inexistente")
    void createAudit_JourneyNotFound() throws SQLException {
        doNothing().when(validator).validate(any(ComplianceAudit.class));
        when(journeyDAO.findById(1)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> complianceAuditService.createAudit(testAudit));
        verify(complianceAuditDAO, never()).create(any(ComplianceAudit.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar auditoria com motorista inexistente")
    void createAudit_DriverNotFound() throws SQLException {
        doNothing().when(validator).validate(any(ComplianceAudit.class));
        when(journeyDAO.findById(1)).thenReturn(Optional.of(testJourney));
        when(driverDAO.findById(1)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> complianceAuditService.createAudit(testAudit));
        verify(complianceAuditDAO, never()).create(any(ComplianceAudit.class));
    }

    @Test
    @DisplayName("Deve atualizar auditoria com sucesso")
    void updateAudit_Success() throws SQLException, BusinessException {
        doNothing().when(validator).validate(any(ComplianceAudit.class));
        when(complianceAuditDAO.findById(1)).thenReturn(Optional.of(testAudit));
        when(journeyDAO.findById(1)).thenReturn(Optional.of(testJourney));
        when(driverDAO.findById(1)).thenReturn(Optional.of(testDriver));
        when(complianceAuditDAO.update(any(ComplianceAudit.class))).thenReturn(true);

        boolean result = complianceAuditService.updateAudit(testAudit);

        assertTrue(result);
        verify(complianceAuditDAO).update(any(ComplianceAudit.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar auditoria inexistente")
    void updateAudit_NotFound() throws SQLException {
        doNothing().when(validator).validate(any(ComplianceAudit.class));
        when(complianceAuditDAO.findById(1)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> complianceAuditService.updateAudit(testAudit));
        verify(complianceAuditDAO, never()).update(any(ComplianceAudit.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar auditoria com jornada inexistente")
    void updateAudit_JourneyNotFound() throws SQLException {
        doNothing().when(validator).validate(any(ComplianceAudit.class));
        when(complianceAuditDAO.findById(1)).thenReturn(Optional.of(testAudit));
        when(journeyDAO.findById(1)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> complianceAuditService.updateAudit(testAudit));
        verify(complianceAuditDAO, never()).update(any(ComplianceAudit.class));
    }

    @Test
    @DisplayName("Deve deletar auditoria com sucesso")
    void deleteAudit_Success() throws SQLException, BusinessException {
        when(complianceAuditDAO.findById(1)).thenReturn(Optional.of(testAudit));
        when(complianceAuditDAO.delete(1)).thenReturn(true);

        boolean result = complianceAuditService.deleteAudit(1);

        assertTrue(result);
        verify(complianceAuditDAO).delete(1);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar auditoria inexistente")
    void deleteAudit_NotFound() throws SQLException {
        when(complianceAuditDAO.findById(999)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> complianceAuditService.deleteAudit(999));
        verify(complianceAuditDAO, never()).delete(anyInt());
    }

    @Test
    @DisplayName("Deve buscar auditoria por ID")
    void getAuditById_Success() throws SQLException {
        when(complianceAuditDAO.findById(1)).thenReturn(Optional.of(testAudit));

        Optional<ComplianceAudit> result = complianceAuditService.getAuditById(1);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar auditoria inexistente")
    void getAuditById_NotFound() throws SQLException {
        when(complianceAuditDAO.findById(999)).thenReturn(Optional.empty());

        Optional<ComplianceAudit> result = complianceAuditService.getAuditById(999);

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Deve listar todas as auditorias")
    void getAllAudits_Success() throws SQLException {
        ComplianceAudit audit2 = new ComplianceAudit();
        audit2.setId(2);
        audit2.setJourneyId(2);

        when(complianceAuditDAO.findAll()).thenReturn(Arrays.asList(testAudit, audit2));

        List<ComplianceAudit> result = complianceAuditService.getAllAudits();

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há auditorias")
    void getAllAudits_Empty() throws SQLException {
        when(complianceAuditDAO.findAll()).thenReturn(new ArrayList<>());

        List<ComplianceAudit> result = complianceAuditService.getAllAudits();

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Deve buscar auditorias por ID da jornada")
    void getAuditsByJourneyId_Success() throws SQLException {
        when(complianceAuditDAO.findByJourneyId(1)).thenReturn(Arrays.asList(testAudit));

        List<ComplianceAudit> result = complianceAuditService.getAuditsByJourneyId(1);

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getJourneyId());
    }

    @Test
    @DisplayName("Deve buscar auditorias por ID do motorista")
    void getAuditsByDriverId_Success() throws SQLException {
        when(complianceAuditDAO.findByDriverId(1)).thenReturn(Arrays.asList(testAudit));

        List<ComplianceAudit> result = complianceAuditService.getAuditsByDriverId(1);

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getDriverId());
    }

    @Test
    @DisplayName("Deve buscar auditorias por intervalo de datas")
    void getAuditsByAuditDateRange_Success() throws SQLException {
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();

        when(complianceAuditDAO.findByAuditDateRange(startDate, endDate)).thenReturn(Arrays.asList(testAudit));

        List<ComplianceAudit> result = complianceAuditService.getAuditsByAuditDateRange(startDate, endDate);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Deve buscar auditorias por status")
    void getAuditsByStatus_Success() throws SQLException {
        when(complianceAuditDAO.findByStatus(ComplianceStatus.PENDING)).thenReturn(Arrays.asList(testAudit));

        List<ComplianceAudit> result = complianceAuditService.getAuditsByStatus(ComplianceStatus.PENDING);

        assertEquals(1, result.size());
        assertEquals(ComplianceStatus.PENDING, result.get(0).getStatus());
    }
}