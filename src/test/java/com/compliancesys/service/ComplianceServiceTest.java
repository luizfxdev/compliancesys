package com.compliancesys.service;

import com.compliancesys.dao.ComplianceAuditDAO;
import com.compliancesys.dao.CompanyDAO;
import com.compliancesys.dao.DriverDAO;
import com.compliancesys.dao.JourneyDAO;
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.ComplianceAudit;
import com.compliancesys.model.Company;
import com.compliancesys.model.Driver;
import com.compliancesys.model.Journey;
import com.compliancesys.service.impl.ComplianceAuditServiceImpl;
import com.compliancesys.util.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ComplianceAuditServiceTest {

    @Mock
    private ComplianceAuditDAO complianceAuditDAO;
    @Mock
    private CompanyDAO companyDAO; // Mock para verificar existência de Company
    @Mock
    private DriverDAO driverDAO;   // Mock para verificar existência de Driver
    @Mock
    private JourneyDAO journeyDAO; // Mock para verificar existência de Journey
    @Mock
    private Validator validator;

    @InjectMocks
    private ComplianceAuditServiceImpl complianceAuditService;

    private Company testCompany;
    private Driver testDriver;
    private Journey testJourney;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Configurações padrão para o validator
        when(validator.isValidId(anyInt())).thenReturn(true);
        when(validator.isValidDateTime(any(LocalDateTime.class))).thenReturn(true);
        when(validator.isValidRecordType(anyString())).thenReturn(true); // Usado para auditType
        when(validator.isValidStatus(anyString())).thenReturn(true); // Usado para status

        // Configurações padrão para DAOs de dependência
        testCompany = new Company(1, "Empresa Teste", "12345678000190", "Rua Teste, 123", "11987654321", "teste@empresa.com", LocalDateTime.now(), LocalDateTime.now());
        testDriver = new Driver(1, 1, "Nome Motorista", "12345678901", "12345678901", "B", LocalDate.now().plusYears(5), LocalDate.now().minusYears(30), "motorista@email.com", "11987654321", "Endereço Motorista", LocalDateTime.now(), LocalDateTime.now());
        testJourney = new Journey(1, 1, 1, 1, "Origem", "Destino", LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1), "Concluída", LocalDateTime.now(), LocalDateTime.now());

        when(companyDAO.findById(anyInt())).thenReturn(Optional.of(testCompany));
        when(driverDAO.findById(anyInt())).thenReturn(Optional.of(testDriver));
        when(journeyDAO.findById(anyInt())).thenReturn(Optional.of(testJourney));
    }

    // Construtor auxiliar para criar ComplianceAudit de forma mais consistente nos testes
    private ComplianceAudit createTestAudit(int id, int journeyId, int driverId, int companyId, LocalDateTime auditStart, LocalDateTime auditEnd, String auditType, String description, String status) {
        return new ComplianceAudit(id, journeyId, driverId, companyId, auditStart, auditEnd, auditType, description, status, LocalDateTime.now(), LocalDateTime.now());
    }

    private ComplianceAudit createTestAudit(int journeyId, int driverId, int companyId, LocalDateTime auditStart, LocalDateTime auditEnd, String auditType, String description, String status) {
        return new ComplianceAudit(0, journeyId, driverId, companyId, auditStart, auditEnd, auditType, description, status, null, null);
    }

    @Test
    @DisplayName("Deve criar uma nova auditoria de conformidade com sucesso")
    void shouldCreateComplianceAuditSuccessfully() throws SQLException, BusinessException {
        ComplianceAudit newAudit = createTestAudit(testJourney.getId(), testDriver.getId(), testCompany.getId(),
                LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1), "INSPECTION", "Auditoria de rotina", "PENDING");
        ComplianceAudit auditWithId = createTestAudit(1, testJourney.getId(), testDriver.getId(), testCompany.getId(),
                LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1), "INSPECTION", "Auditoria de rotina", "PENDING");

        when(complianceAuditDAO.create(any(ComplianceAudit.class))).thenReturn(1);
        when(complianceAuditDAO.findById(1)).thenReturn(Optional.of(auditWithId));

        ComplianceAudit createdAudit = complianceAuditService.createComplianceAudit(newAudit);

        assertNotNull(createdAudit);
        assertEquals(1, createdAudit.getId());
        assertEquals(newAudit.getAuditType(), createdAudit.getAuditType());
        verify(validator, times(1)).isValidId(newAudit.getJourneyId());
        verify(validator, times(1)).isValidId(newAudit.getDriverId());
        verify(validator, times(1)).isValidId(newAudit.getCompanyId());
        verify(validator, times(1)).isValidDateTime(newAudit.getAuditStart());
        verify(validator, times(1)).isValidDateTime(newAudit.getAuditEnd());
        verify(validator, times(1)).isValidRecordType(newAudit.getAuditType()); // Reutilizando para auditType
        verify(validator, times(1)).isValidStatus(newAudit.getStatus());
        verify(companyDAO, times(1)).findById(newAudit.getCompanyId());
        verify(driverDAO, times(1)).findById(newAudit.getDriverId());
        verify(journeyDAO, times(1)).findById(newAudit.getJourneyId());
        verify(complianceAuditDAO, times(1)).create(any(ComplianceAudit.class));
    }

    @Test
    @DisplayName("Não deve criar auditoria com ID de jornada inválido")
    void shouldNotCreateAuditWithInvalidJourneyId() {
        ComplianceAudit newAudit = createTestAudit(-1, testDriver.getId(), testCompany.getId(),
                LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1), "INSPECTION", "Auditoria de rotina", "PENDING");

        when(validator.isValidId(newAudit.getJourneyId())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                complianceAuditService.createComplianceAudit(newAudit));

        assertEquals("ID da jornada inválido.", exception.getMessage());
        verify(validator, times(1)).isValidId(newAudit.getJourneyId());
        verify(journeyDAO, never()).findById(anyInt());
        verify(complianceAuditDAO, never()).create(any(ComplianceAudit.class));
    }

    @Test
    @DisplayName("Não deve criar auditoria se jornada não existir")
    void shouldNotCreateAuditIfJourneyNotFound() throws SQLException {
        ComplianceAudit newAudit = createTestAudit(99, testDriver.getId(), testCompany.getId(),
                LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1), "INSPECTION", "Auditoria de rotina", "PENDING");

        when(journeyDAO.findById(newAudit.getJourneyId())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                complianceAuditService.createComplianceAudit(newAudit));

        assertEquals("Jornada não encontrada.", exception.getMessage());
        verify(journeyDAO, times(1)).findById(newAudit.getJourneyId());
        verify(complianceAuditDAO, never()).create(any(ComplianceAudit.class));
    }

    @Test
    @DisplayName("Não deve criar auditoria com ID de motorista inválido")
    void shouldNotCreateAuditWithInvalidDriverId() {
        ComplianceAudit newAudit = createTestAudit(testJourney.getId(), -1, testCompany.getId(),
                LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1), "INSPECTION", "Auditoria de rotina", "PENDING");

        when(validator.isValidId(newAudit.getDriverId())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                complianceAuditService.createComplianceAudit(newAudit));

        assertEquals("ID do motorista inválido.", exception.getMessage());
        verify(validator, times(1)).isValidId(newAudit.getDriverId());
        verify(driverDAO, never()).findById(anyInt());
        verify(complianceAuditDAO, never()).create(any(ComplianceAudit.class));
    }

    @Test
    @DisplayName("Não deve criar auditoria se motorista não existir")
    void shouldNotCreateAuditIfDriverNotFound() throws SQLException {
        ComplianceAudit newAudit = createTestAudit(testJourney.getId(), 99, testCompany.getId(),
                LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1), "INSPECTION", "Auditoria de rotina", "PENDING");

        when(driverDAO.findById(newAudit.getDriverId())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                complianceAuditService.createComplianceAudit(newAudit));

        assertEquals("Motorista não encontrado.", exception.getMessage());
        verify(driverDAO, times(1)).findById(newAudit.getDriverId());
        verify(complianceAuditDAO, never()).create(any(ComplianceAudit.class));
    }

    @Test
    @DisplayName("Não deve criar auditoria com ID de empresa inválido")
    void shouldNotCreateAuditWithInvalidCompanyId() {
        ComplianceAudit newAudit = createTestAudit(testJourney.getId(), testDriver.getId(), -1,
                LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1), "INSPECTION", "Auditoria de rotina", "PENDING");

        when(validator.isValidId(newAudit.getCompanyId())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                complianceAuditService.createComplianceAudit(newAudit));

        assertEquals("ID da empresa inválido.", exception.getMessage());
        verify(validator, times(1)).isValidId(newAudit.getCompanyId());
        verify(companyDAO, never()).findById(anyInt());
        verify(complianceAuditDAO, never()).create(any(ComplianceAudit.class));
    }

    @Test
    @DisplayName("Não deve criar auditoria se empresa não existir")
    void shouldNotCreateAuditIfCompanyNotFound() throws SQLException {
        ComplianceAudit newAudit = createTestAudit(testJourney.getId(), testDriver.getId(), 99,
                LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1), "INSPECTION", "Auditoria de rotina", "PENDING");

        when(companyDAO.findById(newAudit.getCompanyId())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                complianceAuditService.createComplianceAudit(newAudit));

        assertEquals("Empresa não encontrada.", exception.getMessage());
        verify(companyDAO, times(1)).findById(newAudit.getCompanyId());
        verify(complianceAuditDAO, never()).create(any(ComplianceAudit.class));
    }

    @Test
    @DisplayName("Não deve criar auditoria com data de início inválida")
    void shouldNotCreateAuditWithInvalidAuditStart() {
        ComplianceAudit newAudit = createTestAudit(testJourney.getId(), testDriver.getId(), testCompany.getId(),
                null, LocalDateTime.now().minusHours(1), "INSPECTION", "Auditoria de rotina", "PENDING");

        when(validator.isValidDateTime(newAudit.getAuditStart())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                complianceAuditService.createComplianceAudit(newAudit));

        assertEquals("Data/hora de início da auditoria inválida.", exception.getMessage());
        verify(validator, times(1)).isValidDateTime(newAudit.getAuditStart());
        verify(complianceAuditDAO, never()).create(any(ComplianceAudit.class));
    }

    @Test
    @DisplayName("Não deve criar auditoria com data de fim inválida")
    void shouldNotCreateAuditWithInvalidAuditEnd() {
        ComplianceAudit newAudit = createTestAudit(testJourney.getId(), testDriver.getId(), testCompany.getId(),
                LocalDateTime.now().minusHours(2), null, "INSPECTION", "Auditoria de rotina", "PENDING");

        when(validator.isValidDateTime(newAudit.getAuditEnd())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                complianceAuditService.createComplianceAudit(newAudit));

        assertEquals("Data/hora de fim da auditoria inválida.", exception.getMessage());
        verify(validator, times(1)).isValidDateTime(newAudit.getAuditEnd());
        verify(complianceAuditDAO, never()).create(any(ComplianceAudit.class));
    }

    @Test
    @DisplayName("Não deve criar auditoria se data de início for posterior à data de fim")
    void shouldNotCreateAuditIfAuditStartAfterAuditEnd() {
        ComplianceAudit newAudit = createTestAudit(testJourney.getId(), testDriver.getId(), testCompany.getId(),
                LocalDateTime.now().minusHours(1), LocalDateTime.now().minusHours(2), "INSPECTION", "Auditoria de rotina", "PENDING");

        BusinessException exception = assertThrows(BusinessException.class, () ->
                complianceAuditService.createComplianceAudit(newAudit));

        assertEquals("A data/hora de início da auditoria não pode ser posterior à data/hora de fim.", exception.getMessage());
        verify(complianceAuditDAO, never()).create(any(ComplianceAudit.class));
    }

    @Test
    @DisplayName("Não deve criar auditoria com tipo inválido")
    void shouldNotCreateAuditWithInvalidAuditType() {
        ComplianceAudit newAudit = createTestAudit(testJourney.getId(), testDriver.getId(), testCompany.getId(),
                LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1), "INVALID_TYPE", "Auditoria de rotina", "PENDING");

        when(validator.isValidRecordType(newAudit.getAuditType())).thenReturn(false); // Reutilizando para auditType

        BusinessException exception = assertThrows(BusinessException.class, () ->
                complianceAuditService.createComplianceAudit(newAudit));

        assertEquals("Tipo de auditoria inválido.", exception.getMessage());
        verify(validator, times(1)).isValidRecordType(newAudit.getAuditType());
        verify(complianceAuditDAO, never()).create(any(ComplianceAudit.class));
    }

    @Test
    @DisplayName("Não deve criar auditoria com status inválido")
    void shouldNotCreateAuditWithInvalidStatus() {
        ComplianceAudit newAudit = createTestAudit(testJourney.getId(), testDriver.getId(), testCompany.getId(),
                LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1), "INSPECTION", "Auditoria de rotina", "INVALID_STATUS");

        when(validator.isValidStatus(newAudit.getStatus())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                complianceAuditService.createComplianceAudit(newAudit));

        assertEquals("Status da auditoria inválido.", exception.getMessage());
        verify(validator, times(1)).isValidStatus(newAudit.getStatus());
        verify(complianceAuditDAO, never()).create(any(ComplianceAudit.class));
    }

    @Test
    @DisplayName("Deve retornar auditoria por ID quando encontrada")
    void shouldReturnAuditByIdWhenFound() throws SQLException, BusinessException {
        int auditId = 1;
        ComplianceAudit expectedAudit = createTestAudit(auditId, testJourney.getId(), testDriver.getId(), testCompany.getId(),
                LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1), "INSPECTION", "Desc", "PENDING");

        when(complianceAuditDAO.findById(auditId)).thenReturn(Optional.of(expectedAudit));

        Optional<ComplianceAudit> result = complianceAuditService.getComplianceAuditById(auditId);

        assertTrue(result.isPresent());
        assertEquals(expectedAudit, result.get());
        verify(validator, times(1)).isValidId(auditId);
        verify(complianceAuditDAO, times(1)).findById(auditId);
    }

    @Test
    @DisplayName("Deve retornar Optional vazio por ID quando não encontrada")
    void shouldReturnEmptyOptionalByIdWhenNotFound() throws SQLException, BusinessException {
        int auditId = 99;

        when(complianceAuditDAO.findById(auditId)).thenReturn(Optional.empty());

        Optional<ComplianceAudit> result = complianceAuditService.getComplianceAuditById(auditId);

        assertFalse(result.isPresent());
        verify(validator, times(1)).isValidId(auditId);
        verify(complianceAuditDAO, times(1)).findById(auditId);
    }

    @Test
    @DisplayName("Não deve buscar auditoria com ID inválido")
    void shouldNotGetAuditWithInvalidId() {
        int auditId = -1;

        when(validator.isValidId(auditId)).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                complianceAuditService.getComplianceAuditById(auditId));

        assertEquals("ID da auditoria inválido.", exception.getMessage());
        verify(validator, times(1)).isValidId(auditId);
        verify(complianceAuditDAO, never()).findById(anyInt());
    }

    @Test
    @DisplayName("Deve retornar todas as auditorias")
    void shouldReturnAllAudits() throws SQLException, BusinessException {
        List<ComplianceAudit> expectedAudits = Arrays.asList(
                createTestAudit(1, testJourney.getId(), testDriver.getId(), testCompany.getId(), LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), "INSPECTION", "Desc1", "PENDING"),
                createTestAudit(2, testJourney.getId(), testDriver.getId(), testCompany.getId(), LocalDateTime.now().minusDays(1), LocalDateTime.now(), "REVIEW", "Desc2", "APPROVED")
        );

        when(complianceAuditDAO.findAll()).thenReturn(expectedAudits);

        List<ComplianceAudit> result = complianceAuditService.getAllComplianceAudits();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedAudits, result);
        verify(complianceAuditDAO, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve atualizar uma auditoria existente com sucesso")
    void shouldUpdateExistingAuditSuccessfully() throws SQLException, BusinessException {
        ComplianceAudit existingAudit = createTestAudit(1, testJourney.getId(), testDriver.getId(), testCompany.getId(),
                LocalDateTime.now().minusHours(3), LocalDateTime.now().minusHours(2), "INSPECTION", "Desc Antiga", "PENDING");
        ComplianceAudit updatedAudit = createTestAudit(1, testJourney.getId(), testDriver.getId(), testCompany.getId(),
                LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1), "REVIEW", "Desc Nova", "APPROVED");

        when(complianceAuditDAO.findById(updatedAudit.getId())).thenReturn(Optional.of(existingAudit));
        when(complianceAuditDAO.update(any(ComplianceAudit.class))).thenReturn(true);

        boolean result = complianceAuditService.updateComplianceAudit(updatedAudit);

        assertTrue(result);
        verify(validator, times(1)).isValidId(updatedAudit.getId());
        verify(validator, times(1)).isValidId(updatedAudit.getJourneyId());
        verify(validator, times(1)).isValidId(updatedAudit.getDriverId());
        verify(validator, times(1)).isValidId(updatedAudit.getCompanyId());
        verify(validator, times(1)).isValidDateTime(updatedAudit.getAuditStart());
        verify(validator, times(1)).isValidDateTime(updatedAudit.getAuditEnd());
        verify(validator, times(1)).isValidRecordType(updatedAudit.getAuditType());
        verify(validator, times(1)).isValidStatus(updatedAudit.getStatus());
        verify(companyDAO, times(1)).findById(updatedAudit.getCompanyId());
        verify(driverDAO, times(1)).findById(updatedAudit.getDriverId());
        verify(journeyDAO, times(1)).findById(updatedAudit.getJourneyId());
        verify(complianceAuditDAO, times(1)).findById(updatedAudit.getId());
        verify(complianceAuditDAO, times(1)).update(any(ComplianceAudit.class));
    }

    @Test
    @DisplayName("Não deve atualizar auditoria com ID inválido")
    void shouldNotUpdateAuditWithInvalidId() {
        ComplianceAudit updatedAudit = createTestAudit(-1, testJourney.getId(), testDriver.getId(), testCompany.getId(),
                LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1), "REVIEW", "Desc Nova", "APPROVED");

        when(validator.isValidId(updatedAudit.getId())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                complianceAuditService.updateComplianceAudit(updatedAudit));

        assertEquals("ID da auditoria inválido.", exception.getMessage());
        verify(validator, times(1)).isValidId(updatedAudit.getId());
        verify(complianceAuditDAO, never()).findById(anyInt());
        verify(complianceAuditDAO, never()).update(any(ComplianceAudit.class));
    }

    @Test
    @DisplayName("Não deve atualizar auditoria se não encontrada")
    void shouldNotUpdateAuditIfNotFound() throws SQLException {
        ComplianceAudit updatedAudit = createTestAudit(99, testJourney.getId(), testDriver.getId(), testCompany.getId(),
                LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1), "REVIEW", "Desc Nova", "APPROVED");

        when(complianceAuditDAO.findById(updatedAudit.getId())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                complianceAuditService.updateComplianceAudit(updatedAudit));

        assertEquals("Auditoria não encontrada para atualização.", exception.getMessage());
        verify(complianceAuditDAO, times(1)).findById(updatedAudit.getId());
        verify(complianceAuditDAO, never()).update(any(ComplianceAudit.class));
    }

    @Test
    @DisplayName("Não deve atualizar auditoria com ID de jornada inválido")
    void shouldNotUpdateAuditWithInvalidJourneyId() {
        ComplianceAudit existingAudit = createTestAudit(1, testJourney.getId(), testDriver.getId(), testCompany.getId(),
                LocalDateTime.now().minusHours(3), LocalDateTime.now().minusHours(2), "INSPECTION", "Desc Antiga", "PENDING");
        ComplianceAudit updatedAudit = createTestAudit(1, -1, testDriver.getId(), testCompany.getId(),
                LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1), "REVIEW", "Desc Nova", "APPROVED");

        when(complianceAuditDAO.findById(updatedAudit.getId())).thenReturn(Optional.of(existingAudit));
        when(validator.isValidId(updatedAudit.getJourneyId())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                complianceAuditService.updateComplianceAudit(updatedAudit));

        assertEquals("ID da jornada inválido.", exception.getMessage());
        verify(validator, times(1)).isValidId(updatedAudit.getJourneyId());
        verify(journeyDAO, never()).findById(anyInt());
        verify(complianceAuditDAO, never()).update(any(ComplianceAudit.class));
    }

    @Test
    @DisplayName("Não deve atualizar auditoria se jornada de atualização não existir")
    void shouldNotUpdateAuditIfUpdateJourneyNotFound() throws SQLException {
        ComplianceAudit existingAudit = createTestAudit(1, testJourney.getId(), testDriver.getId(), testCompany.getId(),
                LocalDateTime.now().minusHours(3), LocalDateTime.now().minusHours(2), "INSPECTION", "Desc Antiga", "PENDING");
        ComplianceAudit updatedAudit = createTestAudit(1, 99, testDriver.getId(), testCompany.getId(),
                LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1), "REVIEW", "Desc Nova", "APPROVED");

        when(complianceAuditDAO.findById(updatedAudit.getId())).thenReturn(Optional.of(existingAudit));
        when(journeyDAO.findById(updatedAudit.getJourneyId())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                complianceAuditService.updateComplianceAudit(updatedAudit));

        assertEquals("Jornada não encontrada.", exception.getMessage());
        verify(journeyDAO, times(1)).findById(updatedAudit.getJourneyId());
        verify(complianceAuditDAO, never()).update(any(ComplianceAudit.class));
    }

    // Testes para outros campos inválidos na atualização (driverId, companyId, auditStart, auditEnd, auditType, status)
    // Seguem o mesmo padrão dos testes de criação, mas com a verificação adicional de findById(auditId)

    @Test
    @DisplayName("Não deve atualizar auditoria com data de início inválida")
    void shouldNotUpdateAuditWithInvalidAuditStart() {
        ComplianceAudit existingAudit = createTestAudit(1, testJourney.getId(), testDriver.getId(), testCompany.getId(),
                LocalDateTime.now().minusHours(3), LocalDateTime.now().minusHours(2), "INSPECTION", "Desc Antiga", "PENDING");
        ComplianceAudit updatedAudit = createTestAudit(1, testJourney.getId(), testDriver.getId(), testCompany.getId(),
                null, LocalDateTime.now().minusHours(1), "REVIEW", "Desc Nova", "APPROVED");

        when(complianceAuditDAO.findById(updatedAudit.getId())).thenReturn(Optional.of(existingAudit));
        when(validator.isValidDateTime(updatedAudit.getAuditStart())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                complianceAuditService.updateComplianceAudit(updatedAudit));

        assertEquals("Data/hora de início da auditoria inválida.", exception.getMessage());
        verify(validator, times(1)).isValidDateTime(updatedAudit.getAuditStart());
        verify(complianceAuditDAO, never()).update(any(ComplianceAudit.class));
    }

    @Test
    @DisplayName("Não deve atualizar auditoria se data de início for posterior à data de fim")
    void shouldNotUpdateAuditIfAuditStartAfterAuditEnd() {
        ComplianceAudit existingAudit = createTestAudit(1, testJourney.getId(), testDriver.getId(), testCompany.getId(),
                LocalDateTime.now().minusHours(3), LocalDateTime.now().minusHours(2), "INSPECTION", "Desc Antiga", "PENDING");
        ComplianceAudit updatedAudit = createTestAudit(1, testJourney.getId(), testDriver.getId(), testCompany.getId(),
                LocalDateTime.now().minusHours(1), LocalDateTime.now().minusHours(2), "REVIEW", "Desc Nova", "APPROVED");

        when(complianceAuditDAO.findById(updatedAudit.getId())).thenReturn(Optional.of(existingAudit));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                complianceAuditService.updateComplianceAudit(updatedAudit));

        assertEquals("A data/hora de início da auditoria não pode ser posterior à data/hora de fim.", exception.getMessage());
        verify(complianceAuditDAO, never()).update(any(ComplianceAudit.class));
    }

    @Test
    @DisplayName("Deve deletar uma auditoria existente com sucesso")
    void shouldDeleteExistingAuditSuccessfully() throws SQLException, BusinessException {
        int auditId = 1;
        ComplianceAudit auditToDelete = createTestAudit(auditId, testJourney.getId(), testDriver.getId(), testCompany.getId(),
                LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1), "INSPECTION", "Desc", "PENDING");

        when(complianceAuditDAO.findById(auditId)).thenReturn(Optional.of(auditToDelete));
        when(complianceAuditDAO.delete(auditId)).thenReturn(true);

        boolean result = complianceAuditService.deleteComplianceAudit(auditId);

        assertTrue(result);
        verify(validator, times(1)).isValidId(auditId);
        verify(complianceAuditDAO, times(1)).findById(auditId);
        verify(complianceAuditDAO, times(1)).delete(auditId);
    }

    @Test
    @DisplayName("Não deve deletar auditoria com ID inválido")
    void shouldNotDeleteAuditWithInvalidId() {
        int auditId = -1;

        when(validator.isValidId(auditId)).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                complianceAuditService.deleteComplianceAudit(auditId));

        assertEquals("ID da auditoria inválido.", exception.getMessage());
        verify(validator, times(1)).isValidId(auditId);
        verify(complianceAuditDAO, never()).findById(anyInt());
        verify(complianceAuditDAO, never()).delete(anyInt());
    }

    @Test
    @DisplayName("Não deve deletar auditoria se não encontrada")
    void shouldNotDeleteAuditIfNotFound() throws SQLException {
        int auditId = 99;

        when(complianceAuditDAO.findById(auditId)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                complianceAuditService.deleteComplianceAudit(auditId));

        assertEquals("Auditoria não encontrada para exclusão.", exception.getMessage());
        verify(complianceAuditDAO, times(1)).findById(auditId);
        verify(complianceAuditDAO, never()).delete(anyInt());
    }

    @Test
    @DisplayName("Deve lançar BusinessException em caso de erro de SQL na criação")
    void shouldThrowBusinessExceptionOnSqlErrorDuringCreate() throws SQLException {
        ComplianceAudit newAudit = createTestAudit(testJourney.getId(), testDriver.getId(), testCompany.getId(),
                LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1), "INSPECTION", "Auditoria de rotina", "PENDING");

        when(complianceAuditDAO.create(any(ComplianceAudit.class))).thenThrow(new SQLException("Erro de DB na criação"));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                complianceAuditService.createComplianceAudit(newAudit));

        assertTrue(exception.getMessage().contains("Erro interno ao criar auditoria de conformidade."));
        verify(complianceAuditDAO, times(1)).create(any(ComplianceAudit.class));
    }

    @Test
    @DisplayName("Deve lançar BusinessException em caso de erro de SQL na busca por ID")
    void shouldThrowBusinessExceptionOnSqlErrorDuringGetById() throws SQLException {
        int auditId = 1;

        when(complianceAuditDAO.findById(anyInt())).thenThrow(new SQLException("Erro de DB na busca"));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                complianceAuditService.getComplianceAuditById(auditId));

        assertTrue(exception.getMessage().contains("Erro interno ao buscar auditoria de conformidade por ID."));
        verify(complianceAuditDAO, times(1)).findById(auditId);
    }

    @Test
    @DisplayName("Deve lançar BusinessException em caso de erro de SQL na busca de todos")
    void shouldThrowBusinessExceptionOnSqlErrorDuringGetAll() throws SQLException {
        when(complianceAuditDAO.findAll()).thenThrow(new SQLException("Erro de DB na busca de todos"));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                complianceAuditService.getAllComplianceAudits());

        assertTrue(exception.getMessage().contains("Erro interno ao buscar todas as auditorias de conformidade."));
        verify(complianceAuditDAO, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve lançar BusinessException em caso de erro de SQL na atualização")
    void shouldThrowBusinessExceptionOnSqlErrorDuringUpdate() throws SQLException {
        ComplianceAudit existingAudit = createTestAudit(1, testJourney.getId(), testDriver.getId(), testCompany.getId(),
                LocalDateTime.now().minusHours(3), LocalDateTime.now().minusHours(2), "INSPECTION", "Desc Antiga", "PENDING");
        ComplianceAudit updatedAudit = createTestAudit(1, testJourney.getId(), testDriver.getId(), testCompany.getId(),
                LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1), "REVIEW", "Desc Nova", "APPROVED");

        when(complianceAuditDAO.findById(updatedAudit.getId())).thenReturn(Optional.of(existingAudit));
        when(complianceAuditDAO.update(any(ComplianceAudit.class))).thenThrow(new SQLException("Erro de DB na atualização"));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                complianceAuditService.updateComplianceAudit(updatedAudit));

        assertTrue(exception.getMessage().contains("Erro interno ao atualizar auditoria de conformidade."));
        verify(complianceAuditDAO, times(1)).update(any(ComplianceAudit.class));
    }

    @Test
    @DisplayName("Deve lançar BusinessException em caso de erro de SQL na exclusão")
    void shouldThrowBusinessExceptionOnSqlErrorDuringDelete() throws SQLException {
        int auditId = 1;
        ComplianceAudit auditToDelete = createTestAudit(auditId, testJourney.getId(), testDriver.getId(), testCompany.getId(),
                LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1), "INSPECTION", "Desc", "PENDING");

        when(complianceAuditDAO.findById(auditId)).thenReturn(Optional.of(auditToDelete));
        when(complianceAuditDAO.delete(auditId)).thenThrow(new SQLException("Erro de DB na exclusão"));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                complianceAuditService.deleteComplianceAudit(auditId));

        assertTrue(exception.getMessage().contains("Erro interno ao deletar auditoria de conformidade."));
        verify(complianceAuditDAO, times(1)).delete(auditId);
    }

    @Test
    @DisplayName("Deve retornar auditorias por ID da jornada")
    void shouldReturnAuditsByJourneyId() throws SQLException, BusinessException {
        int journeyId = testJourney.getId();
        List<ComplianceAudit> expectedAudits = Arrays.asList(
                createTestAudit(1, journeyId, testDriver.getId(), testCompany.getId(), LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), "INSPECTION", "Desc1", "PENDING"),
                createTestAudit(2, journeyId, testDriver.getId(), testCompany.getId(), LocalDateTime.now().minusDays(1), LocalDateTime.now(), "REVIEW", "Desc2", "APPROVED")
        );

        when(complianceAuditDAO.findByJourneyId(journeyId)).thenReturn(expectedAudits);

        List<ComplianceAudit> result = complianceAuditService.getComplianceAuditsByJourneyId(journeyId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedAudits, result);
        verify(validator, times(1)).isValidId(journeyId);
        verify(complianceAuditDAO, times(1)).findByJourneyId(journeyId);
    }

    @Test
    @DisplayName("Não deve buscar auditorias por ID de jornada inválido")
    void shouldNotGetAuditsWithInvalidJourneyId() {
        int journeyId = -1;

        when(validator.isValidId(journeyId)).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                complianceAuditService.getComplianceAuditsByJourneyId(journeyId));

        assertEquals("ID da jornada inválido.", exception.getMessage());
        verify(validator, times(1)).isValidId(journeyId);
        verify(complianceAuditDAO, never()).findByJourneyId(anyInt());
    }

    @Test
    @DisplayName("Deve retornar auditorias por ID do motorista")
    void shouldReturnAuditsByDriverId() throws SQLException, BusinessException {
        int driverId = testDriver.getId();
        List<ComplianceAudit> expectedAudits = Arrays.asList(
                createTestAudit(1, testJourney.getId(), driverId, testCompany.getId(), LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), "INSPECTION", "Desc1", "PENDING"),
                createTestAudit(2, testJourney.getId(), driverId, testCompany.getId(), LocalDateTime.now().minusDays(1), LocalDateTime.now(), "REVIEW", "Desc2", "APPROVED")
        );

        when(complianceAuditDAO.findByDriverId(driverId)).thenReturn(expectedAudits);

        List<ComplianceAudit> result = complianceAuditService.getComplianceAuditsByDriverId(driverId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedAudits, result);
        verify(validator, times(1)).isValidId(driverId);
        verify(complianceAuditDAO, times(1)).findByDriverId(driverId);
    }

    @Test
    @DisplayName("Não deve buscar auditorias por ID de motorista inválido")
    void shouldNotGetAuditsWithInvalidDriverId() {
        int driverId = -1;

        when(validator.isValidId(driverId)).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                complianceAuditService.getComplianceAuditsByDriverId(driverId));

        assertEquals("ID do motorista inválido.", exception.getMessage());
        verify(validator, times(1)).isValidId(driverId);
        verify(complianceAuditDAO, never()).findByDriverId(anyInt());
    }

    @Test
    @DisplayName("Deve retornar auditorias por ID da empresa")
    void shouldReturnAuditsByCompanyId() throws SQLException, BusinessException {
        int companyId = testCompany.getId();
        List<ComplianceAudit> expectedAudits = Arrays.asList(
                createTestAudit(1, testJourney.getId(), testDriver.getId(), companyId, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), "INSPECTION", "Desc1", "PENDING"),
                createTestAudit(2, testJourney.getId(), testDriver.getId(), companyId, LocalDateTime.now().minusDays(1), LocalDateTime.now(), "REVIEW", "Desc2", "APPROVED")
        );

        when(complianceAuditDAO.findByCompanyId(companyId)).thenReturn(expectedAudits);

        List<ComplianceAudit> result = complianceAuditService.getComplianceAuditsByCompanyId(companyId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedAudits, result);
        verify(validator, times(1)).isValidId(companyId);
        verify(complianceAuditDAO, times(1)).findByCompanyId(companyId);
    }

    @Test
    @DisplayName("Não deve buscar auditorias por ID de empresa inválido")
    void shouldNotGetAuditsWithInvalidCompanyId() {
        int companyId = -1;

        when(validator.isValidId(companyId)).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                complianceAuditService.getComplianceAuditsByCompanyId(companyId));

        assertEquals("ID da empresa inválido.", exception.getMessage());
        verify(validator, times(1)).isValidId(companyId);
        verify(complianceAuditDAO, never()).findByCompanyId(anyInt());
    }

    @Test
    @DisplayName("Deve retornar auditorias por status")
    void shouldReturnAuditsByStatus() throws SQLException, BusinessException {
        String status = "PENDING";
        List<ComplianceAudit> expectedAudits = Arrays.asList(
                createTestAudit(1, testJourney.getId(), testDriver.getId(), testCompany.getId(), LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), "INSPECTION", "Desc1", status),
                createTestAudit(2, testJourney.getId(), testDriver.getId(), testCompany.getId(), LocalDateTime.now().minusDays(1), LocalDateTime.now(), "REVIEW", "Desc2", "APPROVED")
        );

        when(complianceAuditDAO.findByStatus(status)).thenReturn(Arrays.asList(expectedAudits.get(0)));

        List<ComplianceAudit> result = complianceAuditService.getComplianceAuditsByStatus(status);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expectedAudits.get(0), result.get(0));
        verify(validator, times(1)).isValidStatus(status);
        verify(complianceAuditDAO, times(1)).findByStatus(status);
    }

    @Test
    @DisplayName("Não deve buscar auditorias com status inválido")
    void shouldNotGetAuditsWithInvalidStatus() {
        String status = "INVALID_STATUS";

        when(validator.isValidStatus(status)).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                complianceAuditService.getComplianceAuditsByStatus(status));

        assertEquals("Status da auditoria inválido.", exception.getMessage());
        verify(validator, times(1)).isValidStatus(status);
        verify(complianceAuditDAO, never()).findByStatus(anyString());
    }

    @Test
    @DisplayName("Deve retornar auditorias por período de data")
    void shouldReturnAuditsByAuditDateBetween() throws SQLException, BusinessException {
        LocalDate startDate = LocalDate.now().minusDays(5);
        LocalDate endDate = LocalDate.now();
        List<ComplianceAudit> expectedAudits = Arrays.asList(
                createTestAudit(1, testJourney.getId(), testDriver.getId(), testCompany.getId(), LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), "INSPECTION", "Desc1", "PENDING"),
                createTestAudit(2, testJourney.getId(), testDriver.getId(), testCompany.getId(), LocalDateTime.now().minusDays(4), LocalDateTime.now().minusDays(3), "REVIEW", "Desc2", "APPROVED")
        );

        when(complianceAuditDAO.findByAuditDateBetween(startDate, endDate)).thenReturn(expectedAudits);

        List<ComplianceAudit> result = complianceAuditService.getComplianceAuditsByAuditDateBetween(startDate, endDate);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedAudits, result);
        verify(validator, times(1)).isValidDate(startDate);
        verify(validator, times(1)).isValidDate(endDate);
        verify(complianceAuditDAO, times(1)).findByAuditDateBetween(startDate, endDate);
    }

    @Test
    @DisplayName("Não deve buscar auditorias com data de início inválida")
    void shouldNotGetAuditsWithInvalidStartDate() {
        LocalDate startDate = null;
        LocalDate endDate = LocalDate.now();

        when(validator.isValidDate(startDate)).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                complianceAuditService.getComplianceAuditsByAuditDateBetween(startDate, endDate));

        assertEquals("Data de início inválida.", exception.getMessage());
        verify(validator, times(1)).isValidDate(startDate);
        verify(complianceAuditDAO, never()).findByAuditDateBetween(any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    @DisplayName("Não deve buscar auditorias com data de fim inválida")
    void shouldNotGetAuditsWithInvalidEndDate() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = null;

        when(validator.isValidDate(endDate)).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                complianceAuditService.getComplianceAuditsByAuditDateBetween(startDate, endDate));

        assertEquals("Data de fim inválida.", exception.getMessage());
        verify(validator, times(1)).isValidDate(endDate);
        verify(complianceAuditDAO, never()).findByAuditDateBetween(any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    @DisplayName("Não deve buscar auditorias se data de início for posterior à data de fim")
    void shouldNotGetAuditsIfStartDateAfterEndDate() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().minusDays(1);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                complianceAuditService.getComplianceAuditsByAuditDateBetween(startDate, endDate));

        assertEquals("A data de início não pode ser posterior à data de fim.", exception.getMessage());
        verify(complianceAuditDAO, never()).findByAuditDateBetween(any(LocalDate.class), any(LocalDate.class));
    }
}
