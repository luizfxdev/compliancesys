package com.compliancesys.service;

import com.compliancesys.dao.CompanyDAO;
import com.compliancesys.dao.DriverDAO;
import com.compliancesys.dao.MobileCommunicationDAO;
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.Company;
import com.compliancesys.model.Driver;
import com.compliancesys.model.MobileCommunication;
import com.compliancesys.service.impl.MobileCommunicationServiceImpl;
import com.compliancesys.util.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class MobileCommunicationServiceTest {

    @Mock
    private MobileCommunicationDAO mobileCommunicationDAO;
    @Mock
    private DriverDAO driverDAO;
    @Mock
    private CompanyDAO companyDAO;
    @Mock
    private Validator validator;

    @InjectMocks
    private MobileCommunicationServiceImpl mobileCommunicationService;

    private Company testCompany;
    private Driver testDriver;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Configurações padrão para o validator
        when(validator.isValidId(anyInt())).thenReturn(true);
        when(validator.isValidCommunicationType(anyString())).thenReturn(true);
        when(validator.isValidDateTime(any(LocalDateTime.class))).thenReturn(true);
        when(validator.isValidPhone(anyString())).thenReturn(true);
        when(validator.isValidLocation(anyString())).thenReturn(true);

        // Configurações padrão para DAOs de dependência
        testCompany = new Company(1, "Empresa Teste", "12345678000190", "Rua Teste, 123", "11987654321", "teste@empresa.com", LocalDateTime.now(), LocalDateTime.now());
        testDriver = new Driver(1, 1, "Nome Motorista", "12345678901", "12345678901", "B", null, null, "motorista@email.com", "11987654321", "Endereço Motorista", LocalDateTime.now(), LocalDateTime.now());

        when(companyDAO.findById(anyInt())).thenReturn(Optional.of(testCompany));
        when(driverDAO.findById(anyInt())).thenReturn(Optional.of(testDriver));
    }

    // Construtor auxiliar para criar MobileCommunication de forma mais consistente nos testes
    private MobileCommunication createTestCommunication(int id, int driverId, int companyId, String communicationType,
                                                        LocalDateTime startTime, LocalDateTime endTime, String sourceNumber,
                                                        String destinationNumber, int durationSeconds, String location) {
        return new MobileCommunication(id, driverId, companyId, communicationType, startTime, endTime, sourceNumber,
                destinationNumber, durationSeconds, location, LocalDateTime.now(), LocalDateTime.now());
    }

    private MobileCommunication createTestCommunication(int driverId, int companyId, String communicationType,
                                                        LocalDateTime startTime, LocalDateTime endTime, String sourceNumber,
                                                        String destinationNumber, int durationSeconds, String location) {
        return new MobileCommunication(0, driverId, companyId, communicationType, startTime, endTime, sourceNumber,
                destinationNumber, durationSeconds, location, null, null);
    }

    @Test
    @DisplayName("Deve registrar uma nova comunicação móvel com sucesso")
    void shouldRegisterMobileCommunicationSuccessfully() throws SQLException, BusinessException {
        MobileCommunication newCommunication = createTestCommunication(testDriver.getId(), testCompany.getId(), "CALL",
                LocalDateTime.now().minusMinutes(10), LocalDateTime.now().minusMinutes(5), "11987654321",
                "11998877665", 300, "Localização A");
        MobileCommunication communicationWithId = createTestCommunication(1, testDriver.getId(), testCompany.getId(), "CALL",
                LocalDateTime.now().minusMinutes(10), LocalDateTime.now().minusMinutes(5), "11987654321",
                "11998877665", 300, "Localização A");

        when(mobileCommunicationDAO.create(any(MobileCommunication.class))).thenReturn(1);
        when(mobileCommunicationDAO.findById(1)).thenReturn(Optional.of(communicationWithId));

        MobileCommunication registeredCommunication = mobileCommunicationService.registerMobileCommunication(newCommunication);

        assertNotNull(registeredCommunication);
        assertEquals(1, registeredCommunication.getId());
        assertEquals(newCommunication.getCommunicationType(), registeredCommunication.getCommunicationType());
        verify(validator, times(1)).isValidId(newCommunication.getDriverId());
        verify(validator, times(1)).isValidId(newCommunication.getCompanyId());
        verify(validator, times(1)).isValidCommunicationType(newCommunication.getCommunicationType());
        verify(validator, times(1)).isValidDateTime(newCommunication.getStartTime());
        verify(validator, times(1)).isValidDateTime(newCommunication.getEndTime());
        verify(validator, times(1)).isValidPhone(newCommunication.getSourceNumber());
        verify(validator, times(1)).isValidPhone(newCommunication.getDestinationNumber());
        verify(validator, times(1)).isValidLocation(newCommunication.getLocation());
        verify(driverDAO, times(1)).findById(newCommunication.getDriverId());
        verify(companyDAO, times(1)).findById(newCommunication.getCompanyId());
        verify(mobileCommunicationDAO, times(1)).create(any(MobileCommunication.class));
    }

    @Test
    @DisplayName("Não deve registrar comunicação com ID de motorista inválido")
    void shouldNotRegisterCommunicationWithInvalidDriverId() {
        MobileCommunication newCommunication = createTestCommunication(-1, testCompany.getId(), "CALL",
                LocalDateTime.now().minusMinutes(10), LocalDateTime.now().minusMinutes(5), "11987654321",
                "11998877665", 300, "Localização A");

        when(validator.isValidId(newCommunication.getDriverId())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                mobileCommunicationService.registerMobileCommunication(newCommunication));

        assertEquals("ID do motorista inválido.", exception.getMessage());
        verify(validator, times(1)).isValidId(newCommunication.getDriverId());
        verify(driverDAO, never()).findById(anyInt());
        verify(mobileCommunicationDAO, never()).create(any(MobileCommunication.class));
    }

    @Test
    @DisplayName("Não deve registrar comunicação se motorista não existir")
    void shouldNotRegisterCommunicationIfDriverNotFound() throws SQLException {
        MobileCommunication newCommunication = createTestCommunication(99, testCompany.getId(), "CALL",
                LocalDateTime.now().minusMinutes(10), LocalDateTime.now().minusMinutes(5), "11987654321",
                "11998877665", 300, "Localização A");

        when(driverDAO.findById(newCommunication.getDriverId())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                mobileCommunicationService.registerMobileCommunication(newCommunication));

        assertEquals("Motorista não encontrado.", exception.getMessage());
        verify(driverDAO, times(1)).findById(newCommunication.getDriverId());
        verify(mobileCommunicationDAO, never()).create(any(MobileCommunication.class));
    }

    @Test
    @DisplayName("Não deve registrar comunicação com ID de empresa inválido")
    void shouldNotRegisterCommunicationWithInvalidCompanyId() {
        MobileCommunication newCommunication = createTestCommunication(testDriver.getId(), -1, "CALL",
                LocalDateTime.now().minusMinutes(10), LocalDateTime.now().minusMinutes(5), "11987654321",
                "11998877665", 300, "Localização A");

        when(validator.isValidId(newCommunication.getCompanyId())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                mobileCommunicationService.registerMobileCommunication(newCommunication));

        assertEquals("ID da empresa inválido.", exception.getMessage());
        verify(validator, times(1)).isValidId(newCommunication.getCompanyId());
        verify(companyDAO, never()).findById(anyInt());
        verify(mobileCommunicationDAO, never()).create(any(MobileCommunication.class));
    }

    @Test
    @DisplayName("Não deve registrar comunicação se empresa não existir")
    void shouldNotRegisterCommunicationIfCompanyNotFound() throws SQLException {
        MobileCommunication newCommunication = createTestCommunication(testDriver.getId(), 99, "CALL",
                LocalDateTime.now().minusMinutes(10), LocalDateTime.now().minusMinutes(5), "11987654321",
                "11998877665", 300, "Localização A");

        when(companyDAO.findById(newCommunication.getCompanyId())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                mobileCommunicationService.registerMobileCommunication(newCommunication));

        assertEquals("Empresa não encontrada.", exception.getMessage());
        verify(companyDAO, times(1)).findById(newCommunication.getCompanyId());
        verify(mobileCommunicationDAO, never()).create(any(MobileCommunication.class));
    }

    @Test
    @DisplayName("Não deve registrar comunicação com tipo inválido")
    void shouldNotRegisterCommunicationWithInvalidType() {
        MobileCommunication newCommunication = createTestCommunication(testDriver.getId(), testCompany.getId(), "INVALID_TYPE",
                LocalDateTime.now().minusMinutes(10), LocalDateTime.now().minusMinutes(5), "11987654321",
                "11998877665", 300, "Localização A");

        when(validator.isValidCommunicationType(newCommunication.getCommunicationType())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                mobileCommunicationService.registerMobileCommunication(newCommunication));

        assertEquals("Tipo de comunicação inválido.", exception.getMessage());
        verify(validator, times(1)).isValidCommunicationType(newCommunication.getCommunicationType());
        verify(mobileCommunicationDAO, never()).create(any(MobileCommunication.class));
    }

    @Test
    @DisplayName("Não deve registrar comunicação com hora de início inválida")
    void shouldNotRegisterCommunicationWithInvalidStartTime() {
        MobileCommunication newCommunication = createTestCommunication(testDriver.getId(), testCompany.getId(), "CALL",
                null, LocalDateTime.now().minusMinutes(5), "11987654321",
                "11998877665", 300, "Localização A");

        when(validator.isValidDateTime(newCommunication.getStartTime())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                mobileCommunicationService.registerMobileCommunication(newCommunication));

        assertEquals("Hora de início da comunicação inválida.", exception.getMessage());
        verify(validator, times(1)).isValidDateTime(newCommunication.getStartTime());
        verify(mobileCommunicationDAO, never()).create(any(MobileCommunication.class));
    }

    @Test
    @DisplayName("Não deve registrar comunicação com hora de fim inválida")
    void shouldNotRegisterCommunicationWithInvalidEndTime() {
        MobileCommunication newCommunication = createTestCommunication(testDriver.getId(), testCompany.getId(), "CALL",
                LocalDateTime.now().minusMinutes(10), null, "11987654321",
                "11998877665", 300, "Localização A");

        when(validator.isValidDateTime(newCommunication.getEndTime())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                mobileCommunicationService.registerMobileCommunication(newCommunication));

        assertEquals("Hora de fim da comunicação inválida.", exception.getMessage());
        verify(validator, times(1)).isValidDateTime(newCommunication.getEndTime());
        verify(mobileCommunicationDAO, never()).create(any(MobileCommunication.class));
    }

    @Test
    @DisplayName("Não deve registrar comunicação se hora de início for posterior à hora de fim")
    void shouldNotRegisterCommunicationIfStartTimeAfterEndTime() {
        MobileCommunication newCommunication = createTestCommunication(testDriver.getId(), testCompany.getId(), "CALL",
                LocalDateTime.now(), LocalDateTime.now().minusMinutes(5), "11987654321",
                "11998877665", 300, "Localização A");

        BusinessException exception = assertThrows(BusinessException.class, () ->
                mobileCommunicationService.registerMobileCommunication(newCommunication));

        assertEquals("A hora de início não pode ser posterior à hora de fim.", exception.getMessage());
        verify(mobileCommunicationDAO, never()).create(any(MobileCommunication.class));
    }

    @Test
    @DisplayName("Não deve registrar comunicação com número de origem inválido")
    void shouldNotRegisterCommunicationWithInvalidSourceNumber() {
        MobileCommunication newCommunication = createTestCommunication(testDriver.getId(), testCompany.getId(), "CALL",
                LocalDateTime.now().minusMinutes(10), LocalDateTime.now().minusMinutes(5), "invalid-phone",
                "11998877665", 300, "Localização A");

        when(validator.isValidPhone(newCommunication.getSourceNumber())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                mobileCommunicationService.registerMobileCommunication(newCommunication));

        assertEquals("Número de origem inválido.", exception.getMessage());
        verify(validator, times(1)).isValidPhone(newCommunication.getSourceNumber());
        verify(mobileCommunicationDAO, never()).create(any(MobileCommunication.class));
    }

    @Test
    @DisplayName("Não deve registrar comunicação com número de destino inválido")
    void shouldNotRegisterCommunicationWithInvalidDestinationNumber() {
        MobileCommunication newCommunication = createTestCommunication(testDriver.getId(), testCompany.getId(), "CALL",
                LocalDateTime.now().minusMinutes(10), LocalDateTime.now().minusMinutes(5), "11987654321",
                "invalid-phone", 300, "Localização A");

        when(validator.isValidPhone(newCommunication.getDestinationNumber())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                mobileCommunicationService.registerMobileCommunication(newCommunication));

        assertEquals("Número de destino inválido.", exception.getMessage());
        verify(validator, times(1)).isValidPhone(newCommunication.getDestinationNumber());
        verify(mobileCommunicationDAO, never()).create(any(MobileCommunication.class));
    }

    @Test
    @DisplayName("Não deve registrar comunicação com localização inválida")
    void shouldNotRegisterCommunicationWithInvalidLocation() {
        MobileCommunication newCommunication = createTestCommunication(testDriver.getId(), testCompany.getId(), "CALL",
                LocalDateTime.now().minusMinutes(10), LocalDateTime.now().minusMinutes(5), "11987654321",
                "11998877665", 300, ""); // Localização vazia

        when(validator.isValidLocation(newCommunication.getLocation())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                mobileCommunicationService.registerMobileCommunication(newCommunication));

        assertEquals("Localização inválida.", exception.getMessage());
        verify(validator, times(1)).isValidLocation(newCommunication.getLocation());
        verify(mobileCommunicationDAO, never()).create(any(MobileCommunication.class));
    }

    @Test
    @DisplayName("Deve retornar comunicação por ID quando encontrada")
    void shouldReturnCommunicationByIdWhenFound() throws SQLException, BusinessException {
        int communicationId = 1;
        MobileCommunication expectedCommunication = createTestCommunication(communicationId, testDriver.getId(), testCompany.getId(), "SMS",
                LocalDateTime.now().minusMinutes(10), LocalDateTime.now().minusMinutes(5), "11987654321",
                "11998877665", 0, "Localização B");

        when(mobileCommunicationDAO.findById(communicationId)).thenReturn(Optional.of(expectedCommunication));

        Optional<MobileCommunication> result = mobileCommunicationService.getMobileCommunicationById(communicationId);

        assertTrue(result.isPresent());
        assertEquals(expectedCommunication, result.get());
        verify(validator, times(1)).isValidId(communicationId);
        verify(mobileCommunicationDAO, times(1)).findById(communicationId);
    }

    @Test
    @DisplayName("Deve retornar Optional vazio por ID quando não encontrada")
    void shouldReturnEmptyOptionalByIdWhenNotFound() throws SQLException, BusinessException {
        int communicationId = 99;

        when(mobileCommunicationDAO.findById(communicationId)).thenReturn(Optional.empty());

        Optional<MobileCommunication> result = mobileCommunicationService.getMobileCommunicationById(communicationId);

        assertFalse(result.isPresent());
        verify(validator, times(1)).isValidId(communicationId);
        verify(mobileCommunicationDAO, times(1)).findById(communicationId);
    }

    @Test
    @DisplayName("Não deve buscar comunicação com ID inválido")
    void shouldNotGetCommunicationWithInvalidId() {
        int communicationId = -1;

        when(validator.isValidId(anyInt())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                mobileCommunicationService.getMobileCommunicationById(communicationId));

        assertEquals("ID da comunicação móvel inválido.", exception.getMessage());
        verify(validator, times(1)).isValidId(communicationId);
        verify(mobileCommunicationDAO, never()).findById(anyInt());
    }

    @Test
    @DisplayName("Deve retornar todas as comunicações móveis")
    void shouldReturnAllMobileCommunications() throws SQLException, BusinessException {
        List<MobileCommunication> expectedCommunications = Arrays.asList(
                createTestCommunication(1, testDriver.getId(), testCompany.getId(), "CALL", LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1), "111", "222", 100, "Loc A"),
                createTestCommunication(2, testDriver.getId(), testCompany.getId(), "SMS", LocalDateTime.now().minusHours(3), LocalDateTime.now().minusHours(2), "333", "444", 0, "Loc B")
        );

        when(mobileCommunicationDAO.findAll()).thenReturn(expectedCommunications);

        List<MobileCommunication> result = mobileCommunicationService.getAllMobileCommunications();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedCommunications, result);
        verify(mobileCommunicationDAO, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve atualizar uma comunicação móvel existente com sucesso")
    void shouldUpdateExistingMobileCommunicationSuccessfully() throws SQLException, BusinessException {
        MobileCommunication existingCommunication = createTestCommunication(1, testDriver.getId(), testCompany.getId(), "CALL",
                LocalDateTime.now().minusMinutes(10), LocalDateTime.now().minusMinutes(5), "11987654321",
                "11998877665", 300, "Localização A");
        MobileCommunication updatedCommunication = createTestCommunication(1, testDriver.getId(), testCompany.getId(), "SMS",
                LocalDateTime.now().minusMinutes(12), LocalDateTime.now().minusMinutes(7), "11987654321",
                "11998877665", 200, "Localização C");

        when(mobileCommunicationDAO.findById(updatedCommunication.getId())).thenReturn(Optional.of(existingCommunication));
        when(mobileCommunicationDAO.update(any(MobileCommunication.class))).thenReturn(true);

        boolean result = mobileCommunicationService.updateMobileCommunication(updatedCommunication);

        assertTrue(result);
        verify(validator, times(1)).isValidId(updatedCommunication.getId());
        verify(validator, times(1)).isValidId(updatedCommunication.getDriverId());
        verify(validator, times(1)).isValidId(updatedCommunication.getCompanyId());
        verify(validator, times(1)).isValidCommunicationType(updatedCommunication.getCommunicationType());
        verify(validator, times(1)).isValidDateTime(updatedCommunication.getStartTime());
        verify(validator, times(1)).isValidDateTime(updatedCommunication.getEndTime());
        verify(validator, times(1)).isValidPhone(updatedCommunication.getSourceNumber());
        verify(validator, times(1)).isValidPhone(updatedCommunication.getDestinationNumber());
        verify(validator, times(1)).isValidLocation(updatedCommunication.getLocation());
        verify(driverDAO, times(1)).findById(updatedCommunication.getDriverId());
        verify(companyDAO, times(1)).findById(updatedCommunication.getCompanyId());
        verify(mobileCommunicationDAO, times(1)).findById(updatedCommunication.getId());
        verify(mobileCommunicationDAO, times(1)).update(any(MobileCommunication.class));
    }

    @Test
    @DisplayName("Não deve atualizar comunicação com ID inválido")
    void shouldNotUpdateCommunicationWithInvalidId() {
        MobileCommunication updatedCommunication = createTestCommunication(-1, testDriver.getId(), testCompany.getId(), "CALL",
                LocalDateTime.now().minusMinutes(10), LocalDateTime.now().minusMinutes(5), "11987654321",
                "11998877665", 300, "Localização A");

        when(validator.isValidId(anyInt())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                mobileCommunicationService.updateMobileCommunication(updatedCommunication));

        assertEquals("ID da comunicação móvel inválido.", exception.getMessage());
        verify(validator, times(1)).isValidId(updatedCommunication.getId());
        verify(mobileCommunicationDAO, never()).findById(anyInt());
        verify(mobileCommunicationDAO, never()).update(any(MobileCommunication.class));
    }

    @Test
    @DisplayName("Não deve atualizar comunicação que não existe")
    void shouldNotUpdateNonExistentCommunication() throws SQLException {
        MobileCommunication nonExistentCommunication = createTestCommunication(99, testDriver.getId(), testCompany.getId(), "CALL",
                LocalDateTime.now().minusMinutes(10), LocalDateTime.now().minusMinutes(5), "11987654321",
                "11998877665", 300, "Localização A");

        when(mobileCommunicationDAO.findById(nonExistentCommunication.getId())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                mobileCommunicationService.updateMobileCommunication(nonExistentCommunication));

        assertEquals("Comunicação móvel não encontrada para atualização.", exception.getMessage());
        verify(validator, times(1)).isValidId(nonExistentCommunication.getId());
        verify(mobileCommunicationDAO, times(1)).findById(nonExistentCommunication.getId());
        verify(mobileCommunicationDAO, never()).update(any(MobileCommunication.class));
    }

    @Test
    @DisplayName("Não deve atualizar comunicação com ID de motorista inválido")
    void shouldNotUpdateCommunicationWithInvalidDriverId() {
        MobileCommunication existingCommunication = createTestCommunication(1, testDriver.getId(), testCompany.getId(), "CALL",
                LocalDateTime.now().minusMinutes(10), LocalDateTime.now().minusMinutes(5), "11987654321",
                "11998877665", 300, "Localização A");
        MobileCommunication updatedCommunication = createTestCommunication(1, -1, testCompany.getId(), "CALL",
                LocalDateTime.now().minusMinutes(10), LocalDateTime.now().minusMinutes(5), "11987654321",
                "11998877665", 300, "Localização A");

        when(validator.isValidId(updatedCommunication.getDriverId())).thenReturn(false);
        when(mobileCommunicationDAO.findById(updatedCommunication.getId())).thenReturn(Optional.of(existingCommunication)); // Mock para passar a primeira validação

        BusinessException exception = assertThrows(BusinessException.class, () ->
                mobileCommunicationService.updateMobileCommunication(updatedCommunication));

        assertEquals("ID do motorista inválido.", exception.getMessage());
        verify(validator, times(1)).isValidId(updatedCommunication.getDriverId());
        verify(driverDAO, never()).findById(anyInt());
        verify(mobileCommunicationDAO, never()).update(any(MobileCommunication.class));
    }

    @Test
    @DisplayName("Não deve atualizar comunicação se motorista não existir")
    void shouldNotUpdateCommunicationIfDriverNotFound() throws SQLException {
        MobileCommunication existingCommunication = createTestCommunication(1, testDriver.getId(), testCompany.getId(), "CALL",
                LocalDateTime.now().minusMinutes(10), LocalDateTime.now().minusMinutes(5), "11987654321",
                "11998877665", 300, "Localização A");
        MobileCommunication updatedCommunication = createTestCommunication(1, 99, testCompany.getId(), "CALL",
                LocalDateTime.now().minusMinutes(10), LocalDateTime.now().minusMinutes(5), "11987654321",
                "11998877665", 300, "Localização A");

        when(mobileCommunicationDAO.findById(updatedCommunication.getId())).thenReturn(Optional.of(existingCommunication));
        when(driverDAO.findById(updatedCommunication.getDriverId())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                mobileCommunicationService.updateMobileCommunication(updatedCommunication));

        assertEquals("Motorista não encontrado.", exception.getMessage());
        verify(driverDAO, times(1)).findById(updatedCommunication.getDriverId());
        verify(mobileCommunicationDAO, never()).update(any(MobileCommunication.class));
    }

    @Test
    @DisplayName("Não deve atualizar comunicação com ID de empresa inválido")
    void shouldNotUpdateCommunicationWithInvalidCompanyId() {
        MobileCommunication existingCommunication = createTestCommunication(1, testDriver.getId(), testCompany.getId(), "CALL",
                LocalDateTime.now().minusMinutes(10), LocalDateTime.now().minusMinutes(5), "11987654321",
                "11998877665", 300, "Localização A");
        MobileCommunication updatedCommunication = createTestCommunication(1, testDriver.getId(), -1, "CALL",
                LocalDateTime.now().minusMinutes(10), LocalDateTime.now().minusMinutes(5), "11987654321",
                "11998877665", 300, "Localização A");

        when(validator.isValidId(updatedCommunication.getCompanyId())).thenReturn(false);
        when(mobileCommunicationDAO.findById(updatedCommunication.getId())).thenReturn(Optional.of(existingCommunication)); // Mock para passar a primeira validação

        BusinessException exception = assertThrows(BusinessException.class, () ->
                mobileCommunicationService.updateMobileCommunication(updatedCommunication));

        assertEquals("ID da empresa inválido.", exception.getMessage());
        verify(validator, times(1)).isValidId(updatedCommunication.getCompanyId());
        verify(companyDAO, never()).findById(anyInt());
        verify(mobileCommunicationDAO, never()).update(any(MobileCommunication.class));
    }

    @Test
    @DisplayName("Não deve atualizar comunicação se empresa não existir")
    void shouldNotUpdateCommunicationIfCompanyNotFound() throws SQLException {
        MobileCommunication existingCommunication = createTestCommunication(1, testDriver.getId(), testCompany.getId(), "CALL",
                LocalDateTime.now().minusMinutes(10), LocalDateTime.now().minusMinutes(5), "11987654321",
                "11998877665", 300, "Localização A");
        MobileCommunication updatedCommunication = createTestCommunication(1, testDriver.getId(), 99, "CALL",
                LocalDateTime.now().minusMinutes(10), LocalDateTime.now().minusMinutes(5), "11987654321",
                "11998877665", 300, "Localização A");

        when(mobileCommunicationDAO.findById(updatedCommunication.getId())).thenReturn(Optional.of(existingCommunication));
        when(companyDAO.findById(updatedCommunication.getCompanyId())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                mobileCommunicationService.updateMobileCommunication(updatedCommunication));

        assertEquals("Empresa não encontrada.", exception.getMessage());
        verify(companyDAO, times(1)).findById(updatedCommunication.getCompanyId());
        verify(mobileCommunicationDAO, never()).update(any(MobileCommunication.class));
    }

    @Test
    @DisplayName("Não deve atualizar comunicação com tipo inválido")
    void shouldNotUpdateCommunicationWithInvalidType() {
        MobileCommunication existingCommunication = createTestCommunication(1, testDriver.getId(), testCompany.getId(), "CALL",
                LocalDateTime.now().minusMinutes(10), LocalDateTime.now().minusMinutes(5), "11987654321",
                "11998877665", 300, "Localização A");
        MobileCommunication updatedCommunication = createTestCommunication(1, testDriver.getId(), testCompany.getId(), "INVALID_TYPE",
                LocalDateTime.now().minusMinutes(10), LocalDateTime.now().minusMinutes(5), "11987654321",
                "11998877665", 300, "Localização A");

        when(validator.isValidCommunicationType(updatedCommunication.getCommunicationType())).thenReturn(false);
        when(mobileCommunicationDAO.findById(updatedCommunication.getId())).thenReturn(Optional.of(existingCommunication)); // Mock para passar a primeira validação

        BusinessException exception = assertThrows(BusinessException.class, () ->
                mobileCommunicationService.updateMobileCommunication(updatedCommunication));

        assertEquals("Tipo de comunicação inválido.", exception.getMessage());
        verify(validator, times(1)).isValidCommunicationType(updatedCommunication.getCommunicationType());
        verify(mobileCommunicationDAO, never()).update(any(MobileCommunication.class));
    }

    @Test
    @DisplayName("Não deve atualizar comunicação com hora de início inválida")
    void shouldNotUpdateCommunicationWithInvalidStartTime() {
        MobileCommunication existingCommunication = createTestCommunication(1, testDriver.getId(), testCompany.getId(), "CALL",
                LocalDateTime.now().minusMinutes(10), LocalDateTime.now().minusMinutes(5), "11987654321",
                "11998877665", 300, "Localização A");
        MobileCommunication updatedCommunication = createTestCommunication(1, testDriver.getId(), testCompany.getId(), "CALL",
                null, LocalDateTime.now().minusMinutes(5), "11987654321",
                "11998877665", 300, "Localização A");

        when(validator.isValidDateTime(updatedCommunication.getStartTime())).thenReturn(false);
        when(mobileCommunicationDAO.findById(updatedCommunication.getId())).thenReturn(Optional.of(existingCommunication)); // Mock para passar a primeira validação

        BusinessException exception = assertThrows(BusinessException.class, () ->
                mobileCommunicationService.updateMobileCommunication(updatedCommunication));

        assertEquals("Hora de início da comunicação inválida.", exception.getMessage());
        verify(validator, times(1)).isValidDateTime(updatedCommunication.getStartTime());
        verify(mobileCommunicationDAO, never()).update(any(MobileCommunication.class));
    }

    @Test
    @DisplayName("Não deve atualizar comunicação com hora de fim inválida")
    void shouldNotUpdateCommunicationWithInvalidEndTime() {
        MobileCommunication existingCommunication = createTestCommunication(1, testDriver.getId(), testCompany.getId(), "CALL",
                LocalDateTime.now().minusMinutes(10), LocalDateTime.now().minusMinutes(5), "11987654321",
                "11998877665", 300, "Localização A");
        MobileCommunication updatedCommunication = createTestCommunication(1, testDriver.getId(), testCompany.getId(), "CALL",
                LocalDateTime.now().minusMinutes(10), null, "11987654321",
                "11998877665", 300, "Localização A");

        when(validator.isValidDateTime(updatedCommunication.getEndTime())).thenReturn(false);
        when(mobileCommunicationDAO.findById(updatedCommunication.getId())).thenReturn(Optional.of(existingCommunication)); // Mock para passar a primeira validação

        BusinessException exception = assertThrows(BusinessException.class, () ->
                mobileCommunicationService.updateMobileCommunication(updatedCommunication));

        assertEquals("Hora de fim da comunicação inválida.", exception.getMessage());
        verify(validator, times(1)).isValidDateTime(updatedCommunication.getEndTime());
        verify(mobileCommunicationDAO, never()).update(any(MobileCommunication.class));
    }

    @Test
    @DisplayName("Não deve atualizar comunicação se hora de início for posterior à hora de fim")
    void shouldNotUpdateCommunicationIfStartTimeAfterEndTime() throws SQLException {
        MobileCommunication existingCommunication = createTestCommunication(1, testDriver.getId(), testCompany.getId(), "CALL",
                LocalDateTime.now().minusMinutes(10), LocalDateTime.now().minusMinutes(5), "11987654321",
                "11998877665", 300, "Localização A");
        MobileCommunication updatedCommunication = createTestCommunication(1, testDriver.getId(), testCompany.getId(), "CALL",
                LocalDateTime.now(), LocalDateTime.now().minusMinutes(5), "11987654321",
                "11998877665", 300, "Localização A");

        when(mobileCommunicationDAO.findById(updatedCommunication.getId())).thenReturn(Optional.of(existingCommunication)); // Mock para passar a primeira validação

        BusinessException exception = assertThrows(BusinessException.class, () ->
                mobileCommunicationService.updateMobileCommunication(updatedCommunication));

        assertEquals("A hora de início não pode ser posterior à hora de fim.", exception.getMessage());
        verify(mobileCommunicationDAO, never()).update(any(MobileCommunication.class));
    }

    @Test
    @DisplayName("Não deve atualizar comunicação com número de origem inválido")
    void shouldNotUpdateCommunicationWithInvalidSourceNumber() {
        MobileCommunication existingCommunication = createTestCommunication(1, testDriver.getId(), testCompany.getId(), "CALL",
                LocalDateTime.now().minusMinutes(10), LocalDateTime.now().minusMinutes(5), "11987654321",
                "11998877665", 300, "Localização A");
        MobileCommunication updatedCommunication = createTestCommunication(1, testDriver.getId(), testCompany.getId(), "CALL",
                LocalDateTime.now().minusMinutes(10), LocalDateTime.now().minusMinutes(5), "invalid-phone",
                "11998877665", 300, "Localização A");

        when(validator.isValidPhone(updatedCommunication.getSourceNumber())).thenReturn(false);
        when(mobileCommunicationDAO.findById(updatedCommunication.getId())).thenReturn(Optional.of(existingCommunication)); // Mock para passar a primeira validação

        BusinessException exception = assertThrows(BusinessException.class, () ->
                mobileCommunicationService.updateMobileCommunication(updatedCommunication));

        assertEquals("Número de origem inválido.", exception.getMessage());
        verify(validator, times(1)).isValidPhone(updatedCommunication.getSourceNumber());
        verify(mobileCommunicationDAO, never()).update(any(MobileCommunication.class));
    }

    @Test
    @DisplayName("Não deve atualizar comunicação com número de destino inválido")
    void shouldNotUpdateCommunicationWithInvalidDestinationNumber() {
        MobileCommunication existingCommunication = createTestCommunication(1, testDriver.getId(), testCompany.getId(), "CALL",
                LocalDateTime.now().minusMinutes(10), LocalDateTime.now().minusMinutes(5), "11987654321",
                "11998877665", 300, "Localização A");
        MobileCommunication updatedCommunication = createTestCommunication(1, testDriver.getId(), testCompany.getId(), "CALL",
                LocalDateTime.now().minusMinutes(10), LocalDateTime.now().minusMinutes(5), "11987654321",
                "invalid-phone", 300, "Localização A");

        when(validator.isValidPhone(updatedCommunication.getDestinationNumber())).thenReturn(false);
        when(mobileCommunicationDAO.findById(updatedCommunication.getId())).thenReturn(Optional.of(existingCommunication)); // Mock para passar a primeira validação

        BusinessException exception = assertThrows(BusinessException.class, () ->
                mobileCommunicationService.updateMobileCommunication(updatedCommunication));

        assertEquals("Número de destino inválido.", exception.getMessage());
        verify(validator, times(1)).isValidPhone(updatedCommunication.getDestinationNumber());
        verify(mobileCommunicationDAO, never()).update(any(MobileCommunication.class));
    }

    @Test
    @DisplayName("Não deve atualizar comunicação com localização inválida")
    void shouldNotUpdateCommunicationWithInvalidLocation() {
        MobileCommunication existingCommunication = createTestCommunication(1, testDriver.getId(), testCompany.getId(), "CALL",
                LocalDateTime.now().minusMinutes(10), LocalDateTime.now().minusMinutes(5), "11987654321",
                "11998877665", 300, "Localização A");
        MobileCommunication updatedCommunication = createTestCommunication(1, testDriver.getId(), testCompany.getId(), "CALL",
                LocalDateTime.now().minusMinutes(10), LocalDateTime.now().minusMinutes(5), "11987654321",
                "11998877665", 300, ""); // Localização vazia

        when(validator.isValidLocation(updatedCommunication.getLocation())).thenReturn(false);
        when(mobileCommunicationDAO.findById(updatedCommunication.getId())).thenReturn(Optional.of(existingCommunication)); // Mock para passar a primeira validação

        BusinessException exception = assertThrows(BusinessException.class, () ->
                mobileCommunicationService.updateMobileCommunication(updatedCommunication));

        assertEquals("Localização inválida.", exception.getMessage());
        verify(validator, times(1)).isValidLocation(updatedCommunication.getLocation());
        verify(mobileCommunicationDAO, never()).update(any(MobileCommunication.class));
    }

    @Test
    @DisplayName("Deve deletar uma comunicação móvel existente com sucesso")
    void shouldDeleteExistingMobileCommunicationSuccessfully() throws SQLException, BusinessException {
        int communicationId = 1;
        MobileCommunication communicationToDelete = createTestCommunication(communicationId, testDriver.getId(), testCompany.getId(), "CALL",
                LocalDateTime.now().minusMinutes(10), LocalDateTime.now().minusMinutes(5), "11987654321",
                "11998877665", 300, "Localização A");

        when(mobileCommunicationDAO.findById(communicationId)).thenReturn(Optional.of(communicationToDelete));
        when(mobileCommunicationDAO.delete(communicationId)).thenReturn(true);

        boolean result = mobileCommunicationService.deleteMobileCommunication(communicationId);

        assertTrue(result);
        verify(validator, times(1)).isValidId(communicationId);
        verify(mobileCommunicationDAO, times(1)).findById(communicationId);
        verify(mobileCommunicationDAO, times(1)).delete(communicationId);
    }

    @Test
    @DisplayName("Não deve deletar comunicação com ID inválido")
    void shouldNotDeleteCommunicationWithInvalidId() {
        int communicationId = -1;

        when(validator.isValidId(anyInt())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                mobileCommunicationService.deleteMobileCommunication(communicationId));

        assertEquals("ID da comunicação móvel inválido.", exception.getMessage());
        verify(validator, times(1)).isValidId(communicationId);
        verify(mobileCommunicationDAO, never()).findById(anyInt());
        verify(mobileCommunicationDAO, never()).delete(anyInt());
    }

    @Test
    @DisplayName("Não deve deletar comunicação que não existe")
    void shouldNotDeleteNonExistentCommunication() throws SQLException {
        int communicationId = 99;

        when(mobileCommunicationDAO.findById(communicationId)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                mobileCommunicationService.deleteMobileCommunication(communicationId));

        assertEquals("Comunicação móvel não encontrada para exclusão.", exception.getMessage());
        verify(validator, times(1)).isValidId(communicationId);
        verify(mobileCommunicationDAO, times(1)).findById(communicationId);
        verify(mobileCommunicationDAO, never()).delete(anyInt());
    }

    @Test
    @DisplayName("Deve lançar BusinessException em caso de erro de SQL na criação")
    void shouldThrowBusinessExceptionOnSqlErrorDuringCreate() throws SQLException {
        MobileCommunication newCommunication = createTestCommunication(testDriver.getId(), testCompany.getId(), "CALL",
                LocalDateTime.now().minusMinutes(10), LocalDateTime.now().minusMinutes(5), "11987654321",
                "11998877665", 300, "Localização A");

        when(mobileCommunicationDAO.create(any(MobileCommunication.class))).thenThrow(new SQLException("Erro de DB na criação"));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                mobileCommunicationService.registerMobileCommunication(newCommunication));

        assertTrue(exception.getMessage().contains("Erro interno ao registrar comunicação móvel."));
        verify(mobileCommunicationDAO, times(1)).create(any(MobileCommunication.class));
    }

    @Test
    @DisplayName("Deve lançar BusinessException em caso de erro de SQL na busca por ID")
    void shouldThrowBusinessExceptionOnSqlErrorDuringGetById() throws SQLException {
        int communicationId = 1;

        when(mobileCommunicationDAO.findById(anyInt())).thenThrow(new SQLException("Erro de DB na busca"));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                mobileCommunicationService.getMobileCommunicationById(communicationId));

        assertTrue(exception.getMessage().contains("Erro interno ao buscar comunicação móvel por ID."));
        verify(mobileCommunicationDAO, times(1)).findById(communicationId);
    }

    @Test
    @DisplayName("Deve lançar BusinessException em caso de erro de SQL na busca de todos")
    void shouldThrowBusinessExceptionOnSqlErrorDuringGetAll() throws SQLException {
        when(mobileCommunicationDAO.findAll()).thenThrow(new SQLException("Erro de DB na busca de todos"));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                mobileCommunicationService.getAllMobileCommunications());

        assertTrue(exception.getMessage().contains("Erro interno ao buscar todas as comunicações móveis."));
        verify(mobileCommunicationDAO, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve lançar BusinessException em caso de erro de SQL na atualização")
    void shouldThrowBusinessExceptionOnSqlErrorDuringUpdate() throws SQLException {
        MobileCommunication existingCommunication = createTestCommunication(1, testDriver.getId(), testCompany.getId(), "CALL",
                LocalDateTime.now().minusMinutes(10), LocalDateTime.now().minusMinutes(5), "11987654321",
                "11998877665", 300, "Localização A");
        MobileCommunication updatedCommunication = createTestCommunication(1, testDriver.getId(), testCompany.getId(), "SMS",
                LocalDateTime.now().minusMinutes(12), LocalDateTime.now().minusMinutes(7), "11987654321",
                "11998877665", 200, "Localização C");

        when(mobileCommunicationDAO.findById(updatedCommunication.getId())).thenReturn(Optional.of(existingCommunication));
        when(mobileCommunicationDAO.update(any(MobileCommunication.class))).thenThrow(new SQLException("Erro de DB na atualização"));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                mobileCommunicationService.updateMobileCommunication(updatedCommunication));

        assertTrue(exception.getMessage().contains("Erro interno ao atualizar comunicação móvel."));
        verify(mobileCommunicationDAO, times(1)).update(any(MobileCommunication.class));
    }

    @Test
    @DisplayName("Deve lançar BusinessException em caso de erro de SQL na exclusão")
    void shouldThrowBusinessExceptionOnSqlErrorDuringDelete() throws SQLException {
        int communicationId = 1;
        MobileCommunication communicationToDelete = createTestCommunication(communicationId, testDriver.getId(), testCompany.getId(), "CALL",
                LocalDateTime.now().minusMinutes(10), LocalDateTime.now().minusMinutes(5), "11987654321",
                "11998877665", 300, "Localização A");

        when(mobileCommunicationDAO.findById(communicationId)).thenReturn(Optional.of(communicationToDelete));
        when(mobileCommunicationDAO.delete(communicationId)).thenThrow(new SQLException("Erro de DB na exclusão"));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                mobileCommunicationService.deleteMobileCommunication(communicationId));

        assertTrue(exception.getMessage().contains("Erro interno ao deletar comunicação móvel."));
        verify(mobileCommunicationDAO, times(1)).delete(communicationId);
    }

    @Test
    @DisplayName("Deve retornar comunicações por ID do motorista")
    void shouldReturnCommunicationsByDriverId() throws SQLException, BusinessException {
        int driverId = testDriver.getId();
        List<MobileCommunication> expectedCommunications = Arrays.asList(
                createTestCommunication(1, driverId, testCompany.getId(), "CALL", LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1), "111", "222", 100, "Loc A"),
                createTestCommunication(2, driverId, testCompany.getId(), "SMS", LocalDateTime.now().minusHours(3), LocalDateTime.now().minusHours(2), "333", "444", 0, "Loc B")
        );

        when(mobileCommunicationDAO.findByDriverId(driverId)).thenReturn(expectedCommunications);

        List<MobileCommunication> result = mobileCommunicationService.getMobileCommunicationsByDriverId(driverId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedCommunications, result);
        verify(validator, times(1)).isValidId(driverId);
        verify(mobileCommunicationDAO, times(1)).findByDriverId(driverId);
    }

    @Test
    @DisplayName("Não deve buscar comunicações por ID de motorista inválido")
    void shouldNotGetCommunicationsWithInvalidDriverId() {
        int driverId = -1;

        when(validator.isValidId(driverId)).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                mobileCommunicationService.getMobileCommunicationsByDriverId(driverId));

        assertEquals("ID do motorista inválido.", exception.getMessage());
        verify(validator, times(1)).isValidId(driverId);
        verify(mobileCommunicationDAO, never()).findByDriverId(anyInt());
    }

    @Test
    @DisplayName("Deve retornar comunicações por ID da empresa")
    void shouldReturnCommunicationsByCompanyId() throws SQLException, BusinessException {
        int companyId = testCompany.getId();
        List<MobileCommunication> expectedCommunications = Arrays.asList(
                createTestCommunication(1, testDriver.getId(), companyId, "CALL", LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1), "111", "222", 100, "Loc A"),
                createTestCommunication(2, testDriver.getId(), companyId, "SMS", LocalDateTime.now().minusHours(3), LocalDateTime.now().minusHours(2), "333", "444", 0, "Loc B")
        );

        when(mobileCommunicationDAO.findByCompanyId(companyId)).thenReturn(expectedCommunications);

        List<MobileCommunication> result = mobileCommunicationService.getMobileCommunicationsByCompanyId(companyId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedCommunications, result);
        verify(validator, times(1)).isValidId(companyId);
        verify(mobileCommunicationDAO, times(1)).findByCompanyId(companyId);
    }

    @Test
    @DisplayName("Não deve buscar comunicações por ID de empresa inválido")
    void shouldNotGetCommunicationsWithInvalidCompanyId() {
        int companyId = -1;

        when(validator.isValidId(companyId)).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                mobileCommunicationService.getMobileCommunicationsByCompanyId(companyId));

        assertEquals("ID da empresa inválido.", exception.getMessage());
        verify(validator, times(1)).isValidId(companyId);
        verify(mobileCommunicationDAO, never()).findByCompanyId(anyInt());
    }

    @Test
    @DisplayName("Deve retornar comunicações por tipo")
    void shouldReturnCommunicationsByType() throws SQLException, BusinessException {
        String type = "CALL";
        List<MobileCommunication> expectedCommunications = Arrays.asList(
                createTestCommunication(1, testDriver.getId(), testCompany.getId(), type, LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1), "111", "222", 100, "Loc A"),
                createTestCommunication(2, testDriver.getId(), testCompany.getId(), "SMS", LocalDateTime.now().minusHours(3), LocalDateTime.now().minusHours(2), "333", "444", 0, "Loc B")
        );

        when(mobileCommunicationDAO.findByCommunicationType(type)).thenReturn(Arrays.asList(expectedCommunications.get(0)));

        List<MobileCommunication> result = mobileCommunicationService.getMobileCommunicationsByType(type);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expectedCommunications.get(0), result.get(0));
        verify(validator, times(1)).isValidCommunicationType(type);
        verify(mobileCommunicationDAO, times(1)).findByCommunicationType(type);
    }

    @Test
    @DisplayName("Não deve buscar comunicações com tipo inválido")
    void shouldNotGetCommunicationsWithInvalidType() {
        String type = "INVALID_TYPE";

        when(validator.isValidCommunicationType(type)).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                mobileCommunicationService.getMobileCommunicationsByType(type));

        assertEquals("Tipo de comunicação inválido.", exception.getMessage());
        verify(validator, times(1)).isValidCommunicationType(type);
        verify(mobileCommunicationDAO, never()).findByCommunicationType(anyString());
    }

    @Test
    @DisplayName("Deve retornar comunicações por período de hora de início")
    void shouldReturnCommunicationsByStartTimeBetween() throws SQLException, BusinessException {
        LocalDateTime startRange = LocalDateTime.now().minusDays(5);
        LocalDateTime endRange = LocalDateTime.now();
        List<MobileCommunication> expectedCommunications = Arrays.asList(
                createTestCommunication(1, testDriver.getId(), testCompany.getId(), "CALL", LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), "111", "222", 100, "Loc A"),
                createTestCommunication(2, testDriver.getId(), testCompany.getId(), "SMS", LocalDateTime.now().minusDays(4), LocalDateTime.now().minusDays(3), "333", "444", 0, "Loc B")
        );

        when(mobileCommunicationDAO.findByStartTimeBetween(startRange, endRange)).thenReturn(expectedCommunications);

        List<MobileCommunication> result = mobileCommunicationService.getMobileCommunicationsByStartTimeBetween(startRange, endRange);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedCommunications, result);
        verify(validator, times(1)).isValidDateTime(startRange);
        verify(validator, times(1)).isValidDateTime(endRange);
        verify(mobileCommunicationDAO, times(1)).findByStartTimeBetween(startRange, endRange);
    }

    @Test
    @DisplayName("Não deve buscar comunicações com hora de início de range inválida")
    void shouldNotGetCommunicationsWithInvalidStartRangeTime() {
        LocalDateTime startRange = null;
        LocalDateTime endRange = LocalDateTime.now();

        when(validator.isValidDateTime(startRange)).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                mobileCommunicationService.getMobileCommunicationsByStartTimeBetween(startRange, endRange));

        assertEquals("Hora de início do período inválida.", exception.getMessage());
        verify(validator, times(1)).isValidDateTime(startRange);
        verify(mobileCommunicationDAO, never()).findByStartTimeBetween(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Não deve buscar comunicações com hora de fim de range inválida")
    void shouldNotGetCommunicationsWithInvalidEndRangeTime() {
        LocalDateTime startRange = LocalDateTime.now();
        LocalDateTime endRange = null;

        when(validator.isValidDateTime(endRange)).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                mobileCommunicationService.getMobileCommunicationsByStartTimeBetween(startRange, endRange));

        assertEquals("Hora de fim do período inválida.", exception.getMessage());
        verify(validator, times(1)).isValidDateTime(endRange);
        verify(mobileCommunicationDAO, never()).findByStartTimeBetween(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Não deve buscar comunicações se hora de início do range for posterior à hora de fim do range")
    void shouldNotGetCommunicationsIfStartRangeTimeAfterEndRangeTime() {
        LocalDateTime startRange = LocalDateTime.now();
        LocalDateTime endRange = LocalDateTime.now().minusHours(1);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                mobileCommunicationService.getMobileCommunicationsByStartTimeBetween(startRange, endRange));

        assertEquals("A hora de início do período não pode ser posterior à hora de fim do período.", exception.getMessage());
        verify(mobileCommunicationDAO, never()).findByStartTimeBetween(any(LocalDateTime.class), any(LocalDateTime.class));
    }
}
