package com.compliancesys.service;

import com.compliancesys.dao.CompanyDAO;
import com.compliancesys.dao.DriverDAO;
import com.compliancesys.dao.JourneyDAO;
import com.compliancesys.dao.VehicleDAO;
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.Company;
import com.compliancesys.model.Driver;
import com.compliancesys.model.Journey;
import com.compliancesys.model.Vehicle;
import com.compliancesys.service.impl.JourneyServiceImpl;
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

class JourneyServiceTest {

    @Mock
    private JourneyDAO journeyDAO;
    @Mock
    private DriverDAO driverDAO;
    @Mock
    private VehicleDAO vehicleDAO;
    @Mock
    private CompanyDAO companyDAO;
    @Mock
    private Validator validator;

    @InjectMocks
    private JourneyServiceImpl journeyService;

    private Company testCompany;
    private Driver testDriver;
    private Vehicle testVehicle;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Configurações padrão para o validator
        when(validator.isValidId(anyInt())).thenReturn(true);
        when(validator.isValidDateTime(any(LocalDateTime.class))).thenReturn(true);
        when(validator.isValidOriginDestination(anyString())).thenReturn(true);
        when(validator.isValidStatus(anyString())).thenReturn(true);
        when(validator.isValidDate(any(LocalDate.class))).thenReturn(true);

        // Configurações padrão para DAOs de dependência
        testCompany = new Company(1, "Empresa Teste", "12345678000190", "Rua Teste, 123", "11987654321", "teste@empresa.com", LocalDateTime.now(), LocalDateTime.now());
        testDriver = new Driver(1, 1, "Nome Motorista", "12345678901", "12345678901", "B", LocalDate.now().plusYears(5), LocalDate.now().minusYears(30), "motorista@email.com", "11987654321", "Endereço Motorista", LocalDateTime.now(), LocalDateTime.now());
        testVehicle = new Vehicle(1, "ABC1234", "MarcaX", "ModeloY", 2020, 1, LocalDateTime.now(), LocalDateTime.now());

        when(companyDAO.findById(anyInt())).thenReturn(Optional.of(testCompany));
        when(driverDAO.findById(anyInt())).thenReturn(Optional.of(testDriver));
        when(vehicleDAO.findById(anyInt())).thenReturn(Optional.of(testVehicle));
    }

    // Construtor auxiliar para criar Journey de forma mais consistente nos testes
    private Journey createTestJourney(int id, int driverId, int vehicleId, int companyId, String origin, String destination,
                                      LocalDateTime startTime, LocalDateTime endTime, String status) {
        return new Journey(id, driverId, vehicleId, companyId, origin, destination, startTime, endTime, status, LocalDateTime.now(), LocalDateTime.now());
    }

    private Journey createTestJourney(int driverId, int vehicleId, int companyId, String origin, String destination,
                                      LocalDateTime startTime, LocalDateTime endTime, String status) {
        return new Journey(0, driverId, vehicleId, companyId, origin, destination, startTime, endTime, status, null, null);
    }

    @Test
    @DisplayName("Deve criar uma nova jornada com sucesso")
    void shouldCreateJourneySuccessfully() throws SQLException, BusinessException {
        Journey newJourney = createTestJourney(testDriver.getId(), testVehicle.getId(), testCompany.getId(),
                "Origem A", "Destino B", LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1), "IN_PROGRESS");
        Journey journeyWithId = createTestJourney(1, testDriver.getId(), testVehicle.getId(), testCompany.getId(),
                "Origem A", "Destino B", LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1), "IN_PROGRESS");

        when(journeyDAO.create(any(Journey.class))).thenReturn(1);
        when(journeyDAO.findById(1)).thenReturn(Optional.of(journeyWithId));

        Journey createdJourney = journeyService.createJourney(newJourney);

        assertNotNull(createdJourney);
        assertEquals(1, createdJourney.getId());
        assertEquals(newJourney.getOrigin(), createdJourney.getOrigin());
        verify(validator, times(1)).isValidId(newJourney.getDriverId());
        verify(validator, times(1)).isValidId(newJourney.getVehicleId());
        verify(validator, times(1)).isValidId(newJourney.getCompanyId());
        verify(validator, times(1)).isValidOriginDestination(newJourney.getOrigin());
        verify(validator, times(1)).isValidOriginDestination(newJourney.getDestination());
        verify(validator, times(1)).isValidDateTime(newJourney.getStartTime());
        verify(validator, times(1)).isValidDateTime(newJourney.getEndTime());
        verify(validator, times(1)).isValidStatus(newJourney.getStatus());
        verify(driverDAO, times(1)).findById(newJourney.getDriverId());
        verify(vehicleDAO, times(1)).findById(newJourney.getVehicleId());
        verify(companyDAO, times(1)).findById(newJourney.getCompanyId());
        verify(journeyDAO, times(1)).create(any(Journey.class));
    }

    @Test
    @DisplayName("Não deve criar jornada com ID de motorista inválido")
    void shouldNotCreateJourneyWithInvalidDriverId() {
        Journey newJourney = createTestJourney(-1, testVehicle.getId(), testCompany.getId(),
                "Origem A", "Destino B", LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1), "IN_PROGRESS");

        when(validator.isValidId(newJourney.getDriverId())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                journeyService.createJourney(newJourney));

        assertEquals("ID do motorista inválido.", exception.getMessage());
        verify(validator, times(1)).isValidId(newJourney.getDriverId());
        verify(driverDAO, never()).findById(anyInt());
        verify(journeyDAO, never()).create(any(Journey.class));
    }

    @Test
    @DisplayName("Não deve criar jornada se motorista não existir")
    void shouldNotCreateJourneyIfDriverNotFound() throws SQLException {
        Journey newJourney = createTestJourney(99, testVehicle.getId(), testCompany.getId(),
                "Origem A", "Destino B", LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1), "IN_PROGRESS");

        when(driverDAO.findById(newJourney.getDriverId())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                journeyService.createJourney(newJourney));

        assertEquals("Motorista não encontrado.", exception.getMessage());
        verify(driverDAO, times(1)).findById(newJourney.getDriverId());
        verify(journeyDAO, never()).create(any(Journey.class));
    }

    @Test
    @DisplayName("Não deve criar jornada com ID de veículo inválido")
    void shouldNotCreateJourneyWithInvalidVehicleId() {
        Journey newJourney = createTestJourney(testDriver.getId(), -1, testCompany.getId(),
                "Origem A", "Destino B", LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1), "IN_PROGRESS");

        when(validator.isValidId(newJourney.getVehicleId())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                journeyService.createJourney(newJourney));

        assertEquals("ID do veículo inválido.", exception.getMessage());
        verify(validator, times(1)).isValidId(newJourney.getVehicleId());
        verify(vehicleDAO, never()).findById(anyInt());
        verify(journeyDAO, never()).create(any(Journey.class));
    }

    @Test
    @DisplayName("Não deve criar jornada se veículo não existir")
    void shouldNotCreateJourneyIfVehicleNotFound() throws SQLException {
        Journey newJourney = createTestJourney(testDriver.getId(), 99, testCompany.getId(),
                "Origem A", "Destino B", LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1), "IN_PROGRESS");

        when(vehicleDAO.findById(newJourney.getVehicleId())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                journeyService.createJourney(newJourney));

        assertEquals("Veículo não encontrado.", exception.getMessage());
        verify(vehicleDAO, times(1)).findById(newJourney.getVehicleId());
        verify(journeyDAO, never()).create(any(Journey.class));
    }

    @Test
    @DisplayName("Não deve criar jornada com ID de empresa inválido")
    void shouldNotCreateJourneyWithInvalidCompanyId() {
        Journey newJourney = createTestJourney(testDriver.getId(), testVehicle.getId(), -1,
                "Origem A", "Destino B", LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1), "IN_PROGRESS");

        when(validator.isValidId(newJourney.getCompanyId())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                journeyService.createJourney(newJourney));

        assertEquals("ID da empresa inválido.", exception.getMessage());
        verify(validator, times(1)).isValidId(newJourney.getCompanyId());
        verify(companyDAO, never()).findById(anyInt());
        verify(journeyDAO, never()).create(any(Journey.class));
    }

    @Test
    @DisplayName("Não deve criar jornada se empresa não existir")
    void shouldNotCreateJourneyIfCompanyNotFound() throws SQLException {
        Journey newJourney = createTestJourney(testDriver.getId(), testVehicle.getId(), 99,
                "Origem A", "Destino B", LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1), "IN_PROGRESS");

        when(companyDAO.findById(newJourney.getCompanyId())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                journeyService.createJourney(newJourney));

        assertEquals("Empresa não encontrada.", exception.getMessage());
        verify(companyDAO, times(1)).findById(newJourney.getCompanyId());
        verify(journeyDAO, never()).create(any(Journey.class));
    }

    @Test
    @DisplayName("Não deve criar jornada com origem inválida")
    void shouldNotCreateJourneyWithInvalidOrigin() {
        Journey newJourney = createTestJourney(testDriver.getId(), testVehicle.getId(), testCompany.getId(),
                "", "Destino B", LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1), "IN_PROGRESS");

        when(validator.isValidOriginDestination(newJourney.getOrigin())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                journeyService.createJourney(newJourney));

        assertEquals("Origem da jornada inválida.", exception.getMessage());
        verify(validator, times(1)).isValidOriginDestination(newJourney.getOrigin());
        verify(journeyDAO, never()).create(any(Journey.class));
    }

    @Test
    @DisplayName("Não deve criar jornada com destino inválido")
    void shouldNotCreateJourneyWithInvalidDestination() {
        Journey newJourney = createTestJourney(testDriver.getId(), testVehicle.getId(), testCompany.getId(),
                "Origem A", "", LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1), "IN_PROGRESS");

        when(validator.isValidOriginDestination(newJourney.getDestination())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                journeyService.createJourney(newJourney));

        assertEquals("Destino da jornada inválido.", exception.getMessage());
        verify(validator, times(1)).isValidOriginDestination(newJourney.getDestination());
        verify(journeyDAO, never()).create(any(Journey.class));
    }

    @Test
    @DisplayName("Não deve criar jornada com hora de início inválida")
    void shouldNotCreateJourneyWithInvalidStartTime() {
        Journey newJourney = createTestJourney(testDriver.getId(), testVehicle.getId(), testCompany.getId(),
                "Origem A", "Destino B", null, LocalDateTime.now().minusHours(1), "IN_PROGRESS");

        when(validator.isValidDateTime(newJourney.getStartTime())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                journeyService.createJourney(newJourney));

        assertEquals("Hora de início da jornada inválida.", exception.getMessage());
        verify(validator, times(1)).isValidDateTime(newJourney.getStartTime());
        verify(journeyDAO, never()).create(any(Journey.class));
    }

    @Test
    @DisplayName("Não deve criar jornada com hora de fim inválida")
    void shouldNotCreateJourneyWithInvalidEndTime() {
        Journey newJourney = createTestJourney(testDriver.getId(), testVehicle.getId(), testCompany.getId(),
                "Origem A", "Destino B", LocalDateTime.now().minusHours(2), null, "IN_PROGRESS");

        when(validator.isValidDateTime(newJourney.getEndTime())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                journeyService.createJourney(newJourney));

        assertEquals("Hora de fim da jornada inválida.", exception.getMessage());
        verify(validator, times(1)).isValidDateTime(newJourney.getEndTime());
        verify(journeyDAO, never()).create(any(Journey.class));
    }

    @Test
    @DisplayName("Não deve criar jornada se hora de início for posterior à hora de fim")
    void shouldNotCreateJourneyIfStartTimeAfterEndTime() {
        Journey newJourney = createTestJourney(testDriver.getId(), testVehicle.getId(), testCompany.getId(),
                "Origem A", "Destino B", LocalDateTime.now(), LocalDateTime.now().minusHours(1), "IN_PROGRESS");

        BusinessException exception = assertThrows(BusinessException.class, () ->
                journeyService.createJourney(newJourney));

        assertEquals("A hora de início não pode ser posterior à hora de fim.", exception.getMessage());
        verify(journeyDAO, never()).create(any(Journey.class));
    }

    @Test
    @DisplayName("Não deve criar jornada com status inválido")
    void shouldNotCreateJourneyWithInvalidStatus() {
        Journey newJourney = createTestJourney(testDriver.getId(), testVehicle.getId(), testCompany.getId(),
                "Origem A", "Destino B", LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1), "INVALID_STATUS");

        when(validator.isValidStatus(newJourney.getStatus())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                journeyService.createJourney(newJourney));

        assertEquals("Status da jornada inválido.", exception.getMessage());
        verify(validator, times(1)).isValidStatus(newJourney.getStatus());
        verify(journeyDAO, never()).create(any(Journey.class));
    }

    @Test
    @DisplayName("Deve retornar jornada por ID quando encontrada")
    void shouldReturnJourneyByIdWhenFound() throws SQLException, BusinessException {
        int journeyId = 1;
        Journey expectedJourney = createTestJourney(journeyId, testDriver.getId(), testVehicle.getId(), testCompany.getId(),
                "Origem A", "Destino B", LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1), "COMPLETED");

        when(journeyDAO.findById(journeyId)).thenReturn(Optional.of(expectedJourney));

        Optional<Journey> result = journeyService.getJourneyById(journeyId);

        assertTrue(result.isPresent());
        assertEquals(expectedJourney, result.get());
        verify(validator, times(1)).isValidId(journeyId);
        verify(journeyDAO, times(1)).findById(journeyId);
    }

    @Test
    @DisplayName("Deve retornar Optional vazio por ID quando não encontrada")
    void shouldReturnEmptyOptionalByIdWhenNotFound() throws SQLException, BusinessException {
        int journeyId = 99;

        when(journeyDAO.findById(journeyId)).thenReturn(Optional.empty());

        Optional<Journey> result = journeyService.getJourneyById(journeyId);

        assertFalse(result.isPresent());
        verify(validator, times(1)).isValidId(journeyId);
        verify(journeyDAO, times(1)).findById(journeyId);
    }

    @Test
    @DisplayName("Não deve buscar jornada com ID inválido")
    void shouldNotGetJourneyWithInvalidId() {
        int journeyId = -1;

        when(validator.isValidId(anyInt())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                journeyService.getJourneyById(journeyId));

        assertEquals("ID da jornada inválido.", exception.getMessage());
        verify(validator, times(1)).isValidId(journeyId);
        verify(journeyDAO, never()).findById(anyInt());
    }

    @Test
    @DisplayName("Deve retornar todas as jornadas")
    void shouldReturnAllJourneys() throws SQLException, BusinessException {
        List<Journey> expectedJourneys = Arrays.asList(
                createTestJourney(1, testDriver.getId(), testVehicle.getId(), testCompany.getId(), "O1", "D1", LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), "COMPLETED"),
                createTestJourney(2, testDriver.getId(), testVehicle.getId(), testCompany.getId(), "O2", "D2", LocalDateTime.now().minusDays(1), LocalDateTime.now(), "IN_PROGRESS")
        );

        when(journeyDAO.findAll()).thenReturn(expectedJourneys);

        List<Journey> result = journeyService.getAllJourneys();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedJourneys, result);
        verify(journeyDAO, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve atualizar uma jornada existente com sucesso")
    void shouldUpdateExistingJourneySuccessfully() throws SQLException, BusinessException {
        Journey existingJourney = createTestJourney(1, testDriver.getId(), testVehicle.getId(), testCompany.getId(),
                "Origem Antiga", "Destino Antigo", LocalDateTime.now().minusHours(5), LocalDateTime.now().minusHours(4), "IN_PROGRESS");
        Journey updatedJourney = createTestJourney(1, testDriver.getId(), testVehicle.getId(), testCompany.getId(),
                "Origem Nova", "Destino Novo", LocalDateTime.now().minusHours(3), LocalDateTime.now().minusHours(2), "COMPLETED");

        when(journeyDAO.findById(updatedJourney.getId())).thenReturn(Optional.of(existingJourney));
        when(journeyDAO.update(any(Journey.class))).thenReturn(true);

        boolean result = journeyService.updateJourney(updatedJourney);

        assertTrue(result);
        verify(validator, times(1)).isValidId(updatedJourney.getId());
        verify(validator, times(1)).isValidId(updatedJourney.getDriverId());
        verify(validator, times(1)).isValidId(updatedJourney.getVehicleId());
        verify(validator, times(1)).isValidId(updatedJourney.getCompanyId());
        verify(validator, times(1)).isValidOriginDestination(updatedJourney.getOrigin());
        verify(validator, times(1)).isValidOriginDestination(updatedJourney.getDestination());
        verify(validator, times(1)).isValidDateTime(updatedJourney.getStartTime());
        verify(validator, times(1)).isValidDateTime(updatedJourney.getEndTime());
        verify(validator, times(1)).isValidStatus(updatedJourney.getStatus());
        verify(journeyDAO, times(1)).findById(updatedJourney.getId());
        verify(driverDAO, times(1)).findById(updatedJourney.getDriverId());
        verify(vehicleDAO, times(1)).findById(updatedJourney.getVehicleId());
        verify(companyDAO, times(1)).findById(updatedJourney.getCompanyId());
        verify(journeyDAO, times(1)).update(any(Journey.class));
    }

    @Test
    @DisplayName("Não deve atualizar jornada com ID inválido")
    void shouldNotUpdateJourneyWithInvalidId() {
        Journey updatedJourney = createTestJourney(-1, testDriver.getId(), testVehicle.getId(), testCompany.getId(),
                "Origem Nova", "Destino Novo", LocalDateTime.now().minusHours(3), LocalDateTime.now().minusHours(2), "COMPLETED");

        when(validator.isValidId(anyInt())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                journeyService.updateJourney(updatedJourney));

        assertEquals("ID da jornada inválido.", exception.getMessage());
        verify(validator, times(1)).isValidId(updatedJourney.getId());
        verify(journeyDAO, never()).findById(anyInt());
        verify(journeyDAO, never()).update(any(Journey.class));
    }

    @Test
    @DisplayName("Não deve atualizar jornada que não existe")
    void shouldNotUpdateNonExistentJourney() throws SQLException {
        Journey nonExistentJourney = createTestJourney(99, testDriver.getId(), testVehicle.getId(), testCompany.getId(),
                "Origem Nova", "Destino Novo", LocalDateTime.now().minusHours(3), LocalDateTime.now().minusHours(2), "COMPLETED");

        when(journeyDAO.findById(nonExistentJourney.getId())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                journeyService.updateJourney(nonExistentJourney));

        assertEquals("Jornada não encontrada para atualização.", exception.getMessage());
        verify(journeyDAO, times(1)).findById(nonExistentJourney.getId());
        verify(journeyDAO, never()).update(any(Journey.class));
    }

    @Test
    @DisplayName("Não deve atualizar jornada com ID de motorista inválido")
    void shouldNotUpdateJourneyWithInvalidDriverId() throws SQLException {
        Journey existingJourney = createTestJourney(1, testDriver.getId(), testVehicle.getId(), testCompany.getId(),
                "Origem Antiga", "Destino Antigo", LocalDateTime.now().minusHours(5), LocalDateTime.now().minusHours(4), "IN_PROGRESS");
        Journey updatedJourney = createTestJourney(1, -1, testVehicle.getId(), testCompany.getId(),
                "Origem Nova", "Destino Novo", LocalDateTime.now().minusHours(3), LocalDateTime.now().minusHours(2), "COMPLETED");

        when(journeyDAO.findById(updatedJourney.getId())).thenReturn(Optional.of(existingJourney));
        when(validator.isValidId(updatedJourney.getDriverId())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                journeyService.updateJourney(updatedJourney));

        assertEquals("ID do motorista inválido.", exception.getMessage());
        verify(validator, times(1)).isValidId(updatedJourney.getDriverId());
        verify(driverDAO, never()).findById(anyInt());
        verify(journeyDAO, never()).update(any(Journey.class));
    }

    @Test
    @DisplayName("Não deve atualizar jornada se motorista não existir")
    void shouldNotUpdateJourneyIfDriverNotFound() throws SQLException {
        Journey existingJourney = createTestJourney(1, testDriver.getId(), testVehicle.getId(), testCompany.getId(),
                "Origem Antiga", "Destino Antigo", LocalDateTime.now().minusHours(5), LocalDateTime.now().minusHours(4), "IN_PROGRESS");
        Journey updatedJourney = createTestJourney(1, 99, testVehicle.getId(), testCompany.getId(),
                "Origem Nova", "Destino Novo", LocalDateTime.now().minusHours(3), LocalDateTime.now().minusHours(2), "COMPLETED");

        when(journeyDAO.findById(updatedJourney.getId())).thenReturn(Optional.of(existingJourney));
        when(driverDAO.findById(updatedJourney.getDriverId())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                journeyService.updateJourney(updatedJourney));

        assertEquals("Motorista não encontrado.", exception.getMessage());
        verify(driverDAO, times(1)).findById(updatedJourney.getDriverId());
        verify(journeyDAO, never()).update(any(Journey.class));
    }

    @Test
    @DisplayName("Não deve atualizar jornada com ID de veículo inválido")
    void shouldNotUpdateJourneyWithInvalidVehicleId() throws SQLException {
        Journey existingJourney = createTestJourney(1, testDriver.getId(), testVehicle.getId(), testCompany.getId(),
                "Origem Antiga", "Destino Antigo", LocalDateTime.now().minusHours(5), LocalDateTime.now().minusHours(4), "IN_PROGRESS");
        Journey updatedJourney = createTestJourney(1, testDriver.getId(), -1, testCompany.getId(),
                "Origem Nova", "Destino Novo", LocalDateTime.now().minusHours(3), LocalDateTime.now().minusHours(2), "COMPLETED");

        when(journeyDAO.findById(updatedJourney.getId())).thenReturn(Optional.of(existingJourney));
        when(validator.isValidId(updatedJourney.getVehicleId())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                journeyService.updateJourney(updatedJourney));

        assertEquals("ID do veículo inválido.", exception.getMessage());
        verify(validator, times(1)).isValidId(updatedJourney.getVehicleId());
        verify(vehicleDAO, never()).findById(anyInt());
        verify(journeyDAO, never()).update(any(Journey.class));
    }

    @Test
    @DisplayName("Não deve atualizar jornada se veículo não existir")
    void shouldNotUpdateJourneyIfVehicleNotFound() throws SQLException {
        Journey existingJourney = createTestJourney(1, testDriver.getId(), testVehicle.getId(), testCompany.getId(),
                "Origem Antiga", "Destino Antigo", LocalDateTime.now().minusHours(5), LocalDateTime.now().minusHours(4), "IN_PROGRESS");
        Journey updatedJourney = createTestJourney(1, testDriver.getId(), 99, testCompany.getId(),
                "Origem Nova", "Destino Novo", LocalDateTime.now().minusHours(3), LocalDateTime.now().minusHours(2), "COMPLETED");

        when(journeyDAO.findById(updatedJourney.getId())).thenReturn(Optional.of(existingJourney));
        when(vehicleDAO.findById(updatedJourney.getVehicleId())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                journeyService.updateJourney(updatedJourney));

        assertEquals("Veículo não encontrado.", exception.getMessage());
        verify(vehicleDAO, times(1)).findById(updatedJourney.getVehicleId());
        verify(journeyDAO, never()).update(any(Journey.class));
    }

    @Test
    @DisplayName("Não deve atualizar jornada com ID de empresa inválido")
    void shouldNotUpdateJourneyWithInvalidCompanyId() throws SQLException {
        Journey existingJourney = createTestJourney(1, testDriver.getId(), testVehicle.getId(), testCompany.getId(),
                "Origem Antiga", "Destino Antigo", LocalDateTime.now().minusHours(5), LocalDateTime.now().minusHours(4), "IN_PROGRESS");
        Journey updatedJourney = createTestJourney(1, testDriver.getId(), testVehicle.getId(), -1,
                "Origem Nova", "Destino Novo", LocalDateTime.now().minusHours(3), LocalDateTime.now().minusHours(2), "COMPLETED");

        when(journeyDAO.findById(updatedJourney.getId())).thenReturn(Optional.of(existingJourney));
        when(validator.isValidId(updatedJourney.getCompanyId())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                journeyService.updateJourney(updatedJourney));

        assertEquals("ID da empresa inválido.", exception.getMessage());
        verify(validator, times(1)).isValidId(updatedJourney.getCompanyId());
        verify(companyDAO, never()).findById(anyInt());
        verify(journeyDAO, never()).update(any(Journey.class));
    }

    @Test
    @DisplayName("Não deve atualizar jornada se empresa não existir")
    void shouldNotUpdateJourneyIfCompanyNotFound() throws SQLException {
        Journey existingJourney = createTestJourney(1, testDriver.getId(), testVehicle.getId(), testCompany.getId(),
                "Origem Antiga", "Destino Antigo", LocalDateTime.now().minusHours(5), LocalDateTime.now().minusHours(4), "IN_PROGRESS");
        Journey updatedJourney = createTestJourney(1, testDriver.getId(), testVehicle.getId(), 99,
                "Origem Nova", "Destino Novo", LocalDateTime.now().minusHours(3), LocalDateTime.now().minusHours(2), "COMPLETED");

        when(journeyDAO.findById(updatedJourney.getId())).thenReturn(Optional.of(existingJourney));
        when(companyDAO.findById(updatedJourney.getCompanyId())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                journeyService.updateJourney(updatedJourney));

        assertEquals("Empresa não encontrada.", exception.getMessage());
        verify(companyDAO, times(1)).findById(updatedJourney.getCompanyId());
        verify(journeyDAO, never()).update(any(Journey.class));
    }

    @Test
    @DisplayName("Não deve atualizar jornada com origem inválida")
    void shouldNotUpdateJourneyWithInvalidOrigin() throws SQLException {
        Journey existingJourney = createTestJourney(1, testDriver.getId(), testVehicle.getId(), testCompany.getId(),
                "Origem Antiga", "Destino Antigo", LocalDateTime.now().minusHours(5), LocalDateTime.now().minusHours(4), "IN_PROGRESS");
        Journey updatedJourney = createTestJourney(1, testDriver.getId(), testVehicle.getId(), testCompany.getId(),
                "", "Destino Novo", LocalDateTime.now().minusHours(3), LocalDateTime.now().minusHours(2), "COMPLETED");

        when(journeyDAO.findById(updatedJourney.getId())).thenReturn(Optional.of(existingJourney));
        when(validator.isValidOriginDestination(updatedJourney.getOrigin())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                journeyService.updateJourney(updatedJourney));

        assertEquals("Origem da jornada inválida.", exception.getMessage());
        verify(validator, times(1)).isValidOriginDestination(updatedJourney.getOrigin());
        verify(journeyDAO, never()).update(any(Journey.class));
    }

    @Test
    @DisplayName("Não deve atualizar jornada com destino inválido")
    void shouldNotUpdateJourneyWithInvalidDestination() throws SQLException {
        Journey existingJourney = createTestJourney(1, testDriver.getId(), testVehicle.getId(), testCompany.getId(),
                "Origem Antiga", "Destino Antigo", LocalDateTime.now().minusHours(5), LocalDateTime.now().minusHours(4), "IN_PROGRESS");
        Journey updatedJourney = createTestJourney(1, testDriver.getId(), testVehicle.getId(), testCompany.getId(),
                "Origem Nova", "", LocalDateTime.now().minusHours(3), LocalDateTime.now().minusHours(2), "COMPLETED");

        when(journeyDAO.findById(updatedJourney.getId())).thenReturn(Optional.of(existingJourney));
        when(validator.isValidOriginDestination(updatedJourney.getDestination())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                journeyService.updateJourney(updatedJourney));

        assertEquals("Destino da jornada inválido.", exception.getMessage());
        verify(validator, times(1)).isValidOriginDestination(updatedJourney.getDestination());
        verify(journeyDAO, never()).update(any(Journey.class));
    }

    @Test
    @DisplayName("Não deve atualizar jornada com hora de início inválida")
    void shouldNotUpdateJourneyWithInvalidStartTime() throws SQLException {
        Journey existingJourney = createTestJourney(1, testDriver.getId(), testVehicle.getId(), testCompany.getId(),
                "Origem Antiga", "Destino Antigo", LocalDateTime.now().minusHours(5), LocalDateTime.now().minusHours(4), "IN_PROGRESS");
        Journey updatedJourney = createTestJourney(1, testDriver.getId(), testVehicle.getId(), testCompany.getId(),
                "Origem Nova", "Destino Novo", null, LocalDateTime.now().minusHours(2), "COMPLETED");

        when(journeyDAO.findById(updatedJourney.getId())).thenReturn(Optional.of(existingJourney));
        when(validator.isValidDateTime(updatedJourney.getStartTime())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                journeyService.updateJourney(updatedJourney));

        assertEquals("Hora de início da jornada inválida.", exception.getMessage());
        verify(validator, times(1)).isValidDateTime(updatedJourney.getStartTime());
        verify(journeyDAO, never()).update(any(Journey.class));
    }

    @Test
    @DisplayName("Não deve atualizar jornada com hora de fim inválida")
    void shouldNotUpdateJourneyWithInvalidEndTime() throws SQLException {
        Journey existingJourney = createTestJourney(1, testDriver.getId(), testVehicle.getId(), testCompany.getId(),
                "Origem Antiga", "Destino Antigo", LocalDateTime.now().minusHours(5), LocalDateTime.now().minusHours(4), "IN_PROGRESS");
        Journey updatedJourney = createTestJourney(1, testDriver.getId(), testVehicle.getId(), testCompany.getId(),
                "Origem Nova", "Destino Novo", LocalDateTime.now().minusHours(3), null, "COMPLETED");

        when(journeyDAO.findById(updatedJourney.getId())).thenReturn(Optional.of(existingJourney));
        when(validator.isValidDateTime(updatedJourney.getEndTime())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                journeyService.updateJourney(updatedJourney));

        assertEquals("Hora de fim da jornada inválida.", exception.getMessage());
        verify(validator, times(1)).isValidDateTime(updatedJourney.getEndTime());
        verify(journeyDAO, never()).update(any(Journey.class));
    }

    @Test
    @DisplayName("Não deve atualizar jornada se hora de início for posterior à hora de fim")
    void shouldNotUpdateJourneyIfStartTimeAfterEndTime() throws SQLException {
        Journey existingJourney = createTestJourney(1, testDriver.getId(), testVehicle.getId(), testCompany.getId(),
                "Origem Antiga", "Destino Antigo", LocalDateTime.now().minusHours(5), LocalDateTime.now().minusHours(4), "IN_PROGRESS");
        Journey updatedJourney = createTestJourney(1, testDriver.getId(), testVehicle.getId(), testCompany.getId(),
                "Origem Nova", "Destino Novo", LocalDateTime.now(), LocalDateTime.now().minusHours(1), "COMPLETED");

        when(journeyDAO.findById(updatedJourney.getId())).thenReturn(Optional.of(existingJourney));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                journeyService.updateJourney(updatedJourney));

        assertEquals("A hora de início não pode ser posterior à hora de fim.", exception.getMessage());
        verify(journeyDAO, never()).update(any(Journey.class));
    }

    @Test
    @DisplayName("Não deve atualizar jornada com status inválido")
    void shouldNotUpdateJourneyWithInvalidStatus() throws SQLException {
        Journey existingJourney = createTestJourney(1, testDriver.getId(), testVehicle.getId(), testCompany.getId(),
                "Origem Antiga", "Destino Antigo", LocalDateTime.now().minusHours(5), LocalDateTime.now().minusHours(4), "IN_PROGRESS");
        Journey updatedJourney = createTestJourney(1, testDriver.getId(), testVehicle.getId(), testCompany.getId(),
                "Origem Nova", "Destino Novo", LocalDateTime.now().minusHours(3), LocalDateTime.now().minusHours(2), "INVALID_STATUS");

        when(journeyDAO.findById(updatedJourney.getId())).thenReturn(Optional.of(existingJourney));
        when(validator.isValidStatus(updatedJourney.getStatus())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                journeyService.updateJourney(updatedJourney));

        assertEquals("Status da jornada inválido.", exception.getMessage());
        verify(validator, times(1)).isValidStatus(updatedJourney.getStatus());
        verify(journeyDAO, never()).update(any(Journey.class));
    }

    @Test
    @DisplayName("Deve deletar uma jornada existente com sucesso")
    void shouldDeleteExistingJourneySuccessfully() throws SQLException, BusinessException {
        int journeyId = 1;
        Journey journeyToDelete = createTestJourney(journeyId, testDriver.getId(), testVehicle.getId(), testCompany.getId(),
                "Origem para Deletar", "Destino para Deletar", LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1), "COMPLETED");

        when(journeyDAO.findById(journeyId)).thenReturn(Optional.of(journeyToDelete));
        when(journeyDAO.delete(journeyId)).thenReturn(true);

        boolean result = journeyService.deleteJourney(journeyId);

        assertTrue(result);
        verify(validator, times(1)).isValidId(journeyId);
        verify(journeyDAO, times(1)).findById(journeyId);
        verify(journeyDAO, times(1)).delete(journeyId);
    }

    @Test
    @DisplayName("Não deve deletar jornada com ID inválido")
    void shouldNotDeleteJourneyWithInvalidId() {
        int journeyId = -1;

        when(validator.isValidId(anyInt())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                journeyService.deleteJourney(journeyId));

        assertEquals("ID da jornada inválido.", exception.getMessage());
        verify(validator, times(1)).isValidId(journeyId);
        verify(journeyDAO, never()).findById(anyInt());
        verify(journeyDAO, never()).delete(anyInt());
    }

    @Test
    @DisplayName("Não deve deletar jornada que não existe")
    void shouldNotDeleteNonExistentJourney() throws SQLException {
        int journeyId = 99;

        when(journeyDAO.findById(journeyId)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                journeyService.deleteJourney(journeyId));

        assertEquals("Jornada não encontrada para exclusão.", exception.getMessage());
        verify(validator, times(1)).isValidId(journeyId);
        verify(journeyDAO, times(1)).findById(journeyId);
        verify(journeyDAO, never()).delete(anyInt());
    }

    @Test
    @DisplayName("Deve lançar BusinessException em caso de erro de SQL na criação")
    void shouldThrowBusinessExceptionOnSqlErrorDuringCreate() throws SQLException {
        Journey newJourney = createTestJourney(testDriver.getId(), testVehicle.getId(), testCompany.getId(),
                "Origem A", "Destino B", LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1), "IN_PROGRESS");

        when(journeyDAO.create(any(Journey.class))).thenThrow(new SQLException("Erro de DB na criação"));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                journeyService.createJourney(newJourney));

        assertTrue(exception.getMessage().contains("Erro interno ao criar jornada."));
        verify(journeyDAO, times(1)).create(any(Journey.class));
    }

    @Test
    @DisplayName("Deve lançar BusinessException em caso de erro de SQL na busca por ID")
    void shouldThrowBusinessExceptionOnSqlErrorDuringGetById() throws SQLException {
        int journeyId = 1;

        when(journeyDAO.findById(anyInt())).thenThrow(new SQLException("Erro de DB na busca"));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                journeyService.getJourneyById(journeyId));

        assertTrue(exception.getMessage().contains("Erro interno ao buscar jornada por ID."));
        verify(journeyDAO, times(1)).findById(journeyId);
    }

    @Test
    @DisplayName("Deve lançar BusinessException em caso de erro de SQL na busca de todos")
    void shouldThrowBusinessExceptionOnSqlErrorDuringGetAll() throws SQLException {
        when(journeyDAO.findAll()).thenThrow(new SQLException("Erro de DB na busca de todos"));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                journeyService.getAllJourneys());

        assertTrue(exception.getMessage().contains("Erro interno ao buscar todas as jornadas."));
        verify(journeyDAO, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve lançar BusinessException em caso de erro de SQL na atualização")
    void shouldThrowBusinessExceptionOnSqlErrorDuringUpdate() throws SQLException {
        Journey existingJourney = createTestJourney(1, testDriver.getId(), testVehicle.getId(), testCompany.getId(),
                "Origem Antiga", "Destino Antigo", LocalDateTime.now().minusHours(5), LocalDateTime.now().minusHours(4), "IN_PROGRESS");
        Journey updatedJourney = createTestJourney(1, testDriver.getId(), testVehicle.getId(), testCompany.getId(),
                "Origem Nova", "Destino Novo", LocalDateTime.now().minusHours(3), LocalDateTime.now().minusHours(2), "COMPLETED");

        when(journeyDAO.findById(updatedJourney.getId())).thenReturn(Optional.of(existingJourney));
        when(journeyDAO.update(any(Journey.class))).thenThrow(new SQLException("Erro de DB na atualização"));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                journeyService.updateJourney(updatedJourney));

        assertTrue(exception.getMessage().contains("Erro interno ao atualizar jornada."));
        verify(journeyDAO, times(1)).update(any(Journey.class));
    }

    @Test
    @DisplayName("Deve lançar BusinessException em caso de erro de SQL na exclusão")
    void shouldThrowBusinessExceptionOnSqlErrorDuringDelete() throws SQLException {
        int journeyId = 1;
        Journey journeyToDelete = createTestJourney(journeyId, testDriver.getId(), testVehicle.getId(), testCompany.getId(),
                "Origem para Deletar", "Destino para Deletar", LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1), "COMPLETED");

        when(journeyDAO.findById(journeyId)).thenReturn(Optional.of(journeyToDelete));
        when(journeyDAO.delete(journeyId)).thenThrow(new SQLException("Erro de DB na exclusão"));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                journeyService.deleteJourney(journeyId));

        assertTrue(exception.getMessage().contains("Erro interno ao deletar jornada."));
        verify(journeyDAO, times(1)).delete(journeyId);
    }

    @Test
    @DisplayName("Deve retornar jornadas por ID do motorista")
    void shouldReturnJourneysByDriverId() throws SQLException, BusinessException {
        int driverId = testDriver.getId();
        List<Journey> expectedJourneys = Arrays.asList(
                createTestJourney(1, driverId, testVehicle.getId(), testCompany.getId(), "O1", "D1", LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), "COMPLETED"),
                createTestJourney(2, driverId, testVehicle.getId(), testCompany.getId(), "O2", "D2", LocalDateTime.now().minusDays(1), LocalDateTime.now(), "IN_PROGRESS")
        );

        when(journeyDAO.findByDriverId(driverId)).thenReturn(expectedJourneys);

        List<Journey> result = journeyService.getJourneysByDriverId(driverId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedJourneys, result);
        verify(validator, times(1)).isValidId(driverId);
        verify(journeyDAO, times(1)).findByDriverId(driverId);
    }

    @Test
    @DisplayName("Não deve buscar jornadas por ID de motorista inválido")
    void shouldNotGetJourneysWithInvalidDriverId() {
        int driverId = -1;

        when(validator.isValidId(driverId)).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                journeyService.getJourneysByDriverId(driverId));

        assertEquals("ID do motorista inválido.", exception.getMessage());
        verify(validator, times(1)).isValidId(driverId);
        verify(journeyDAO, never()).findByDriverId(anyInt());
    }

    @Test
    @DisplayName("Deve retornar jornadas por ID do veículo")
    void shouldReturnJourneysByVehicleId() throws SQLException, BusinessException {
        int vehicleId = testVehicle.getId();
        List<Journey> expectedJourneys = Arrays.asList(
                createTestJourney(1, testDriver.getId(), vehicleId, testCompany.getId(), "O1", "D1", LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), "COMPLETED"),
                createTestJourney(2, testDriver.getId(), vehicleId, testCompany.getId(), "O2", "D2", LocalDateTime.now().minusDays(1), LocalDateTime.now(), "IN_PROGRESS")
        );

        when(journeyDAO.findByVehicleId(vehicleId)).thenReturn(expectedJourneys);

        List<Journey> result = journeyService.getJourneysByVehicleId(vehicleId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedJourneys, result);
        verify(validator, times(1)).isValidId(vehicleId);
        verify(journeyDAO, times(1)).findByVehicleId(vehicleId);
    }

    @Test
    @DisplayName("Não deve buscar jornadas por ID de veículo inválido")
    void shouldNotGetJourneysWithInvalidVehicleId() {
        int vehicleId = -1;

        when(validator.isValidId(vehicleId)).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                journeyService.getJourneysByVehicleId(vehicleId));

        assertEquals("ID do veículo inválido.", exception.getMessage());
        verify(validator, times(1)).isValidId(vehicleId);
        verify(journeyDAO, never()).findByVehicleId(anyInt());
    }

    @Test
    @DisplayName("Deve retornar jornadas por ID da empresa")
    void shouldReturnJourneysByCompanyId() throws SQLException, BusinessException {
        int companyId = testCompany.getId();
        List<Journey> expectedJourneys = Arrays.asList(
                createTestJourney(1, testDriver.getId(), testVehicle.getId(), companyId, "O1", "D1", LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), "COMPLETED"),
                createTestJourney(2, testDriver.getId(), testVehicle.getId(), companyId, "O2", "D2", LocalDateTime.now().minusDays(1), LocalDateTime.now(), "IN_PROGRESS")
        );

        when(journeyDAO.findByCompanyId(companyId)).thenReturn(expectedJourneys);

        List<Journey> result = journeyService.getJourneysByCompanyId(companyId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedJourneys, result);
        verify(validator, times(1)).isValidId(companyId);
        verify(journeyDAO, times(1)).findByCompanyId(companyId);
    }

    @Test
    @DisplayName("Não deve buscar jornadas por ID de empresa inválido")
    void shouldNotGetJourneysWithInvalidCompanyId() {
        int companyId = -1;

        when(validator.isValidId(companyId)).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                journeyService.getJourneysByCompanyId(companyId));

        assertEquals("ID da empresa inválido.", exception.getMessage());
        verify(validator, times(1)).isValidId(companyId);
        verify(journeyDAO, never()).findByCompanyId(anyInt());
    }

    @Test
    @DisplayName("Deve retornar jornadas por status")
    void shouldReturnJourneysByStatus() throws SQLException, BusinessException {
        String status = "IN_PROGRESS";
        List<Journey> expectedJourneys = Arrays.asList(
                createTestJourney(1, testDriver.getId(), testVehicle.getId(), testCompany.getId(), "O1", "D1", LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), status),
                createTestJourney(2, testDriver.getId(), testVehicle.getId(), testCompany.getId(), "O2", "D2", LocalDateTime.now().minusDays(1), LocalDateTime.now(), "COMPLETED")
        );

        when(journeyDAO.findByStatus(status)).thenReturn(Arrays.asList(expectedJourneys.get(0)));

        List<Journey> result = journeyService.getJourneysByStatus(status);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expectedJourneys.get(0), result.get(0));
        verify(validator, times(1)).isValidStatus(status);
        verify(journeyDAO, times(1)).findByStatus(status);
    }

    @Test
    @DisplayName("Não deve buscar jornadas com status inválido")
    void shouldNotGetJourneysWithInvalidStatus() {
        String status = "INVALID_STATUS";

        when(validator.isValidStatus(status)).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                journeyService.getJourneysByStatus(status));

        assertEquals("Status da jornada inválido.", exception.getMessage());
        verify(validator, times(1)).isValidStatus(status);
        verify(journeyDAO, never()).findByStatus(anyString());
    }

    @Test
    @DisplayName("Deve retornar jornadas por período de hora de início")
    void shouldReturnJourneysByStartTimeBetween() throws SQLException, BusinessException {
        LocalDateTime startRange = LocalDateTime.now().minusDays(5);
        LocalDateTime endRange = LocalDateTime.now();
        List<Journey> expectedJourneys = Arrays.asList(
                createTestJourney(1, testDriver.getId(), testVehicle.getId(), testCompany.getId(), "O1", "D1", LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), "COMPLETED"),
                createTestJourney(2, testDriver.getId(), testVehicle.getId(), testCompany.getId(), "O2", "D2", LocalDateTime.now().minusDays(4), LocalDateTime.now().minusDays(3), "IN_PROGRESS")
        );

        when(journeyDAO.findByStartTimeBetween(startRange, endRange)).thenReturn(expectedJourneys);

        List<Journey> result = journeyService.getJourneysByStartTimeBetween(startRange, endRange);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedJourneys, result);
        verify(validator, times(1)).isValidDateTime(startRange);
        verify(validator, times(1)).isValidDateTime(endRange);
        verify(journeyDAO, times(1)).findByStartTimeBetween(startRange, endRange);
    }

    @Test
    @DisplayName("Não deve buscar jornadas com hora de início de range inválida")
    void shouldNotGetJourneysWithInvalidStartRangeTime() {
        LocalDateTime startRange = null;
        LocalDateTime endRange = LocalDateTime.now();

        when(validator.isValidDateTime(startRange)).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                journeyService.getJourneysByStartTimeBetween(startRange, endRange));

        assertEquals("Hora de início do período inválida.", exception.getMessage());
        verify(validator, times(1)).isValidDateTime(startRange);
        verify(journeyDAO, never()).findByStartTimeBetween(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Não deve buscar jornadas com hora de fim de range inválida")
    void shouldNotGetJourneysWithInvalidEndRangeTime() {
        LocalDateTime startRange = LocalDateTime.now();
        LocalDateTime endRange = null;

        when(validator.isValidDateTime(endRange)).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                journeyService.getJourneysByStartTimeBetween(startRange, endRange));

        assertEquals("Hora de fim do período inválida.", exception.getMessage());
        verify(validator, times(1)).isValidDateTime(endRange);
        verify(journeyDAO, never()).findByStartTimeBetween(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Não deve buscar jornadas se hora de início do range for posterior à hora de fim do range")
    void shouldNotGetJourneysIfStartRangeTimeAfterEndRangeTime() {
        LocalDateTime startRange = LocalDateTime.now();
        LocalDateTime endRange = LocalDateTime.now().minusHours(1);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                journeyService.getJourneysByStartTimeBetween(startRange, endRange));

        assertEquals("A hora de início do período não pode ser posterior à hora de fim do período.", exception.getMessage());
        verify(journeyDAO, never()).findByStartTimeBetween(any(LocalDateTime.class), any(LocalDateTime.class));
    }
}
