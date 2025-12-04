package com.compliancesys.service;

import com.compliancesys.dao.ComplianceAuditDAO;
import com.compliancesys.dao.JourneyDAO;
import com.compliancesys.dao.TimeRecordDAO;
import com.compliancesys.model.ComplianceAudit;
import com.compliancesys.model.ComplianceStatus;
import com.compliancesys.model.EventType;
import com.compliancesys.model.Journey;
import com.compliancesys.model.TimeRecord;
import com.compliancesys.service.impl.JourneyServiceImpl;
import com.compliancesys.util.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith; // Importação necessária para @ExtendWith
import org.mockito.InjectMocks; // Importação necessária para @InjectMocks
import org.mockito.Mock;     // Importação necessária para @Mock
import org.mockito.junit.jupiter.MockitoExtension; // Importação necessária para MockitoExtension

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*; // Importação estática para any(), argThat()
import static org.mockito.Mockito.*;         // Importação estática para when(), verify(), never(), reset()

/**
 * Testes unitários para JourneyServiceImpl utilizando Mockito para isolar dependências.
 */
@ExtendWith(MockitoExtension.class) // Habilita a integração JUnit 5 com Mockito
@DisplayName("JourneyService Unit Tests")
public class JourneyServiceTest {

    @Mock // Cria um mock para JourneyDAO
    private JourneyDAO journeyDAO;
    @Mock // Cria um mock para TimeRecordDAO
    private TimeRecordDAO timeRecordDAO;
    @Mock // Cria um mock para ComplianceAuditDAO
    private ComplianceAuditDAO complianceAuditDAO;
    @Mock // Cria um mock para Validator
    private Validator validator;

    @InjectMocks // Injeta os mocks acima nesta instância de JourneyServiceImpl
    private JourneyServiceImpl journeyService;

    private Journey sampleJourney;
    private List<TimeRecord> sampleTimeRecords;

    @BeforeEach
    void setup() {
        // Resetar mocks e configurar dados de teste antes de cada teste
        reset(journeyDAO, timeRecordDAO, complianceAuditDAO, validator);

        sampleJourney = new Journey(
                1, // ID
                101, // driverId
                LocalDate.now(),
                "Local A",
                "Local B",
                0, // totalDrivingTimeMinutes
                0, // totalBreakTimeMinutes
                ComplianceStatus.PENDENTE // complianceStatus
        );

        sampleTimeRecords = Arrays.asList(
                new TimeRecord(1, 101, LocalDateTime.now().withHour(8).withMinute(0).withSecond(0).withNano(0), EventType.INICIO_JORNADA),
                new TimeRecord(2, 101, LocalDateTime.now().withHour(12).withMinute(0).withSecond(0).withNano(0), EventType.PAUSA),
                new TimeRecord(3, 101, LocalDateTime.now().withHour(13).withMinute(0).withSecond(0).withNano(0), EventType.RETORNO_PAUSA),
                new TimeRecord(4, 101, LocalDateTime.now().withHour(17).withMinute(0).withSecond(0).withNano(0), EventType.FIM_JORNADA)
        );

        // Configurações padrão para o Validator
        when(validator.isValidLocation(anyString())).thenReturn(true);
        when(validator.isPositive(anyInt())).thenReturn(true);
        when(validator.isNotNull(any())).thenReturn(true); // Adicionado para cobrir isNotNull
        when(validator.isValidDate(any(LocalDate.class))).thenReturn(true); // Adicionado para cobrir isValidDate
    }

    // --- Testes para createJourney ---
    @Test
    @DisplayName("Deve criar uma jornada com sucesso")
    void testCreateJourneySuccess() throws SQLException {
        when(journeyDAO.insert(any(Journey.class))).thenReturn(1); // Simula a inserção retornando ID 1
        when(journeyDAO.findByDriverIdAndDate(anyInt(), any(LocalDate.class))).thenReturn(Collections.emptyList());

        int id = journeyService.createJourney(sampleJourney);

        assertEquals(1, id);
        verify(journeyDAO, times(1)).insert(any(Journey.class)); // Verifica se o método insert foi chamado
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException se a jornada for nula ao criar")
    void testCreateJourneyNull() {
        when(validator.isNotNull(null)).thenReturn(false); // Mock para o caso de objeto nulo
        assertThrows(IllegalArgumentException.class, () -> journeyService.createJourney(null));
        verify(journeyDAO, never()).insert(any(Journey.class));
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException se o driverId for inválido ao criar")
    void testCreateJourneyInvalidDriverId() {
        when(validator.isPositive(0)).thenReturn(false); // Mock para o caso de ID inválido
        sampleJourney.setDriverId(0);
        assertThrows(IllegalArgumentException.class, () -> journeyService.createJourney(sampleJourney));
        verify(journeyDAO, never()).insert(any(Journey.class));
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException se a data da jornada for futura ao criar")
    void testCreateJourneyFutureDate() {
        when(validator.isValidDate(any(LocalDate.class))).thenReturn(false); // Mock para data inválida
        sampleJourney.setJourneyDate(LocalDate.now().plusDays(1));
        assertThrows(IllegalArgumentException.class, () -> journeyService.createJourney(sampleJourney));
        verify(journeyDAO, never()).insert(any(Journey.class));
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException se já existir jornada para o motorista na data")
    void testCreateJourneyAlreadyExists() throws SQLException {
        when(journeyDAO.findByDriverIdAndDate(anyInt(), any(LocalDate.class))).thenReturn(Arrays.asList(sampleJourney));
        assertThrows(IllegalArgumentException.class, () -> journeyService.createJourney(sampleJourney));
        verify(journeyDAO, never()).insert(any(Journey.class));
    }

    // --- Testes para getJourneyById ---
    @Test
    @DisplayName("Deve retornar uma jornada pelo ID")
    void testGetJourneyByIdSuccess() throws SQLException {
        when(journeyDAO.findById(1)).thenReturn(Optional.of(sampleJourney));

        Optional<Journey> result = journeyService.getJourneyById(1);

        assertTrue(result.isPresent());
        assertEquals(sampleJourney, result.get());
        verify(journeyDAO, times(1)).findById(1);
    }

    @Test
    @DisplayName("Deve retornar Optional.empty para ID de jornada inexistente")
    void testGetJourneyByIdNotFound() throws SQLException {
        when(journeyDAO.findById(999)).thenReturn(Optional.empty());

        Optional<Journey> result = journeyService.getJourneyById(999);

        assertFalse(result.isPresent());
        verify(journeyDAO, times(1)).findById(999);
    }

    // --- Testes para getAllJourneys ---
    @Test
    @DisplayName("Deve retornar todas as jornadas")
    void testGetAllJourneys() throws SQLException {
        when(journeyDAO.findAll()).thenReturn(Arrays.asList(sampleJourney, new Journey(2, 102, LocalDate.now(), "C", "D", 0, 0, ComplianceStatus.CONFORME)));

        List<Journey> result = journeyService.getAllJourneys();

        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        verify(journeyDAO, times(1)).findAll();
    }

    // --- Testes para getJourneysByDriverId ---
    @Test
    @DisplayName("Deve retornar jornadas por ID do motorista")
    void testGetJourneysByDriverId() throws SQLException {
        when(journeyDAO.findByDriverId(101)).thenReturn(Arrays.asList(sampleJourney));

        List<Journey> result = journeyService.getJourneysByDriverId(101);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(101, result.get(0).getDriverId());
        verify(journeyDAO, times(1)).findByDriverId(101);
    }

    // --- Testes para updateJourney ---
    @Test
    @DisplayName("Deve atualizar uma jornada com sucesso")
    void testUpdateJourneySuccess() throws SQLException {
        sampleJourney.setStartLocation("Novo Local");
        when(journeyDAO.update(any(Journey.class))).thenReturn(true);
        when(journeyDAO.findById(sampleJourney.getId())).thenReturn(Optional.of(sampleJourney)); // Mock para findById para validação interna

        boolean result = journeyService.updateJourney(sampleJourney);

        assertTrue(result);
        verify(journeyDAO, times(1)).update(any(Journey.class));
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException se a jornada for nula ao atualizar")
    void testUpdateJourneyNull() {
        when(validator.isNotNull(null)).thenReturn(false); // Mock para o caso de objeto nulo
        assertThrows(IllegalArgumentException.class, () -> journeyService.updateJourney(null));
        verify(journeyDAO, never()).update(any(Journey.class));
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException se o ID da jornada for inválido ao atualizar")
    void testUpdateJourneyInvalidId() {
        when(validator.isPositive(0)).thenReturn(false); // Mock para o caso de ID inválido
        sampleJourney.setId(0);
        assertThrows(IllegalArgumentException.class, () -> journeyService.updateJourney(sampleJourney));
        verify(journeyDAO, never()).update(any(Journey.class));
    }

    // --- Testes para deleteJourney ---
    @Test
    @DisplayName("Deve deletar uma jornada com sucesso")
    void testDeleteJourneySuccess() throws SQLException {
        when(journeyDAO.delete(1)).thenReturn(true);

        boolean result = journeyService.deleteJourney(1);

        assertTrue(result);
        verify(journeyDAO, times(1)).delete(1);
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException se o ID da jornada for inválido ao deletar")
    void testDeleteJourneyInvalidId() {
        when(validator.isPositive(0)).thenReturn(false); // Mock para o caso de ID inválido
        assertThrows(IllegalArgumentException.class, () -> journeyService.deleteJourney(0));
        verify(journeyDAO, never()).delete(anyInt());
    }

    // --- Testes para calculateAndAuditJourney ---
    @Test
    @DisplayName("Deve calcular e auditar uma jornada em conformidade")
    void testCalculateAndAuditJourneyConform() throws SQLException {
        // Ajusta os registros para serem conformes (4h de direção, 1h de pausa)
        sampleTimeRecords = Arrays.asList(
                new TimeRecord(1, 101, LocalDateTime.now().withHour(8).withMinute(0), EventType.INICIO_JORNADA),
                new TimeRecord(2, 101, LocalDateTime.now().withHour(12).withMinute(0), EventType.PAUSA), // 4h direção
                new TimeRecord(3, 101, LocalDateTime.now().withHour(13).withMinute(0), EventType.RETORNO_PAUSA), // 1h pausa
                new TimeRecord(4, 101, LocalDateTime.now().withHour(16).withMinute(0), EventType.FIM_JORNADA) // 3h direção
        );
        // Total: 7h direção, 1h pausa. Conforme.

        when(journeyDAO.findById(sampleJourney.getId())).thenReturn(Optional.of(sampleJourney));
        when(journeyDAO.update(any(Journey.class))).thenReturn(true);
        when(complianceAuditDAO.insert(any(ComplianceAudit.class))).thenReturn(1); // Retorna um ID de auditoria

        journeyService.calculateAndAuditJourney(sampleJourney.getId(), sampleTimeRecords);

        verify(journeyDAO, times(1)).findById(sampleJourney.getId());
        verify(journeyDAO, times(1)).update(argThat(j ->
                j.getTotalDrivingTimeMinutes() == 420 && // 7 horas * 60 minutos
                j.getTotalBreakTimeMinutes() == 60 &&    // 1 hora * 60 minutos
                j.getComplianceStatus() == ComplianceStatus.CONFORME
        ));
        verify(complianceAuditDAO, times(1)).insert(argThat(audit ->
                audit.getJourneyId() == sampleJourney.getId() &&
                audit.getStatus() == ComplianceStatus.CONFORME
        ));
    }

    @Test
    @DisplayName("Deve calcular e auditar uma jornada não conforme por excesso de direção")
    void testCalculateAndAuditJourneyNonConformExcessDriving() throws SQLException {
        // Ajusta os registros para serem não conformes (9h de direção)
        sampleTimeRecords = Arrays.asList(
                new TimeRecord(1, 101, LocalDateTime.now().withHour(8).withMinute(0), EventType.INICIO_JORNADA),
                new TimeRecord(2, 101, LocalDateTime.now().withHour(17).withMinute(0), EventType.FIM_JORNADA) // 9h direção
        );
        // Total: 9h direção. Não conforme (limite 8h).

        when(journeyDAO.findById(sampleJourney.getId())).thenReturn(Optional.of(sampleJourney));
        when(journeyDAO.update(any(Journey.class))).thenReturn(true);
        when(complianceAuditDAO.insert(any(ComplianceAudit.class))).thenReturn(1);

        journeyService.calculateAndAuditJourney(sampleJourney.getId(), sampleTimeRecords);

        verify(journeyDAO, times(1)).findById(sampleJourney.getId());
        verify(journeyDAO, times(1)).update(argThat(j ->
                j.getTotalDrivingTimeMinutes() == 540 && // 9 horas * 60 minutos
                j.getTotalBreakTimeMinutes() == 0 &&
                j.getComplianceStatus() == ComplianceStatus.NAO_CONFORME
        ));
        verify(complianceAuditDAO, times(1)).insert(argThat(audit ->
                audit.getJourneyId() == sampleJourney.getId() &&
                audit.getStatus() == ComplianceStatus.NAO_CONFORME &&
                audit.getDetails().contains("Excesso de tempo de direção")
        ));
    }

    @Test
    @DisplayName("Deve calcular e auditar uma jornada com alerta por descanso insuficiente")
    void testCalculateAndAuditJourneyAlertInsufficientBreak() throws SQLException {
        // Ajusta os registros para serem com alerta (6h de direção, 15min de pausa)
        sampleTimeRecords = Arrays.asList(
                new TimeRecord(1, 101, LocalDateTime.now().withHour(8).withMinute(0), EventType.INICIO_JORNADA),
                new TimeRecord(2, 101, LocalDateTime.now().withHour(14).withMinute(0), EventType.PAUSA), // 6h direção
                new TimeRecord(3, 101, LocalDateTime.now().withHour(14).withMinute(15), EventType.RETORNO_PAUSA), // 15min pausa
                new TimeRecord(4, 101, LocalDateTime.now().withHour(16).withMinute(0), EventType.FIM_JORNADA) // 2h direção
        );
        // Total: 8h direção, 15min pausa. Alerta (descanso insuficiente para 8h de direção).

        when(journeyDAO.findById(sampleJourney.getId())).thenReturn(Optional.of(sampleJourney));
        when(journeyDAO.update(any(Journey.class))).thenReturn(true);
        when(complianceAuditDAO.insert(any(ComplianceAudit.class))).thenReturn(1);

        journeyService.calculateAndAuditJourney(sampleJourney.getId(), sampleTimeRecords);

        verify(journeyDAO, times(1)).findById(sampleJourney.getId());
        verify(journeyDAO, times(1)).update(argThat(j ->
                j.getTotalDrivingTimeMinutes() == 480 && // 8 horas * 60 minutos
                j.getTotalBreakTimeMinutes() == 15 &&
                j.getComplianceStatus() == ComplianceStatus.ALERTA
        ));
        verify(complianceAuditDAO, times(1)).insert(argThat(audit ->
                audit.getJourneyId() == sampleJourney.getId() &&
                audit.getStatus() == ComplianceStatus.ALERTA &&
                audit.getDetails().contains("Tempo de descanso insuficiente")
        ));
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException se a jornada não for encontrada para auditoria")
    void testCalculateAndAuditJourneyNotFound() {
        when(journeyDAO.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> journeyService.calculateAndAuditJourney(999, sampleTimeRecords));

        verify(journeyDAO, never()).update(any(Journey.class));
        verify(complianceAuditDAO, never()).insert(any(ComplianceAudit.class));
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException se os registros de ponto forem nulos ou vazios para auditoria")
    void testCalculateAndAuditJourneyEmptyTimeRecords() {
        when(journeyDAO.findById(sampleJourney.getId())).thenReturn(Optional.of(sampleJourney));
        when(validator.isNotNull(any())).thenReturn(false); // Mock para o caso de lista nula ou vazia

        assertThrows(IllegalArgumentException.class, () -> journeyService.calculateAndAuditJourney(sampleJourney.getId(), null));
        assertThrows(IllegalArgumentException.class, () -> journeyService.calculateAndAuditJourney(sampleJourney.getId(), Collections.emptyList()));

        verify(journeyDAO, never()).update(any(Journey.class));
        verify(complianceAuditDAO, never()).insert(any(ComplianceAudit.class));
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException se os registros de ponto não estiverem em ordem cronológica")
    void testCalculateAndAuditJourneyNonChronologicalTimeRecords() {
        List<TimeRecord> nonChronologicalRecords = Arrays.asList(
                new TimeRecord(1, 101, LocalDateTime.now().withHour(10), EventType.INICIO_JORNADA),
                new TimeRecord(2, 101, LocalDateTime.now().withHour(8), EventType.FIM_JORNADA) // Ordem errada
        );
        when(journeyDAO.findById(sampleJourney.getId())).thenReturn(Optional.of(sampleJourney));
        // Para este teste, não precisamos mockar o validator.isChronological, pois a lógica está no serviço.
        // O serviço irá ordenar e depois verificar a cronologia.

        assertThrows(IllegalArgumentException.class, () -> journeyService.calculateAndAuditJourney(sampleJourney.getId(), nonChronologicalRecords));

        verify(journeyDAO, never()).update(any(Journey.class));
        verify(complianceAuditDAO, never()).insert(any(ComplianceAudit.class));
    }
}
