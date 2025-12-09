package com.compliancesys.service;

import com.compliancesys.dao.DriverDAO;
import com.compliancesys.dao.JourneyDAO;
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.Driver;
import com.compliancesys.model.Journey;
import com.compliancesys.service.impl.JourneyServiceImpl;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * Classe de teste para JourneyService.
 * Utiliza Mockito para simular as camadas DAO e testar a lógica de negócio do serviço.
 */
public class JourneyServiceTest {

    @Mock
    private JourneyDAO journeyDAO; // Mock da interface JourneyDAO

    @Mock
    private DriverDAO driverDAO; // Mock da interface DriverDAO, necessário para validações

    @InjectMocks
    private JourneyServiceImpl journeyService; // Injeta os mocks no serviço

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Inicializa os mocks antes de cada teste
    }

    // --- Testes para createJourney ---

    @Test
    @DisplayName("1. Deve criar uma nova jornada com sucesso")
    void testCreateJourneySuccess() throws SQLException, BusinessException {
        Journey newJourney = new Journey(0, 1, LocalDate.now(), "Rua A", "Av. B", 50.0, 1.5, null, null);
        Driver existingDriver = new Driver(1, "Motorista Teste", "11122233344", "LIC123", "B", LocalDate.now().plusYears(1), "999", "m@t.com", LocalDateTime.now(), LocalDateTime.now());

        // Configura os mocks
        when(driverDAO.findById(anyInt())).thenReturn(Optional.of(existingDriver)); // Motorista existe
        when(journeyDAO.findByDriverIdAndDate(anyInt(), any(LocalDate.class))).thenReturn(Optional.empty()); // Nenhuma jornada para o motorista na data
        when(journeyDAO.create(any(Journey.class))).thenReturn(1); // Criação retorna ID 1

        Journey createdJourney = journeyService.createJourney(newJourney);

        assertNotNull(createdJourney);
        assertEquals(1, createdJourney.getId());
        assertEquals(newJourney.getStartLocation(), createdJourney.getStartLocation());
        assertNotNull(createdJourney.getCreatedAt());
        assertNotNull(createdJourney.getUpdatedAt());

        // Verifica se os métodos do DAO foram chamados corretamente
        verify(driverDAO, times(1)).findById(1);
        verify(journeyDAO, times(1)).findByDriverIdAndDate(1, LocalDate.now());
        verify(journeyDAO, times(1)).create(any(Journey.class));
    }

    @Test
    @DisplayName("2. Deve lançar BusinessException ao tentar criar jornada com ID de motorista inválido")
    void testCreateJourneyInvalidDriverId() {
        Journey newJourney = new Journey(0, 0, LocalDate.now(), "Rua A", "Av. B", 50.0, 1.5, null, null);

        BusinessException thrown = assertThrows(BusinessException.class, () -> journeyService.createJourney(newJourney));
        assertEquals("O ID do motorista é obrigatório e deve ser um valor positivo.", thrown.getMessage());
        verify(driverDAO, never()).findById(anyInt());
        verify(journeyDAO, never()).create(any(Journey.class));
    }

    @Test
    @DisplayName("3. Deve lançar BusinessException ao tentar criar jornada com data de jornada nula")
    void testCreateJourneyNullJourneyDate() {
        Journey newJourney = new Journey(0, 1, null, "Rua A", "Av. B", 50.0, 1.5, null, null);

        BusinessException thrown = assertThrows(BusinessException.class, () -> journeyService.createJourney(newJourney));
        assertEquals("A data da jornada é obrigatória.", thrown.getMessage());
        verify(driverDAO, never()).findById(anyInt());
        verify(journeyDAO, never()).create(any(Journey.class));
    }

    @Test
    @DisplayName("4. Deve lançar BusinessException ao tentar criar jornada com localização de início vazia")
    void testCreateJourneyEmptyStartLocation() {
        Journey newJourney = new Journey(0, 1, LocalDate.now(), "", "Av. B", 50.0, 1.5, null, null);

        BusinessException thrown = assertThrows(BusinessException.class, () -> journeyService.createJourney(newJourney));
        assertEquals("A localização de início da jornada não pode ser vazia.", thrown.getMessage());
        verify(driverDAO, never()).findById(anyInt());
        verify(journeyDAO, never()).create(any(Journey.class));
    }

    @Test
    @DisplayName("5. Deve lançar BusinessException ao tentar criar jornada com localização de fim vazia")
    void testCreateJourneyEmptyEndLocation() {
        Journey newJourney = new Journey(0, 1, LocalDate.now(), "Rua A", "", 50.0, 1.5, null, null);

        BusinessException thrown = assertThrows(BusinessException.class, () -> journeyService.createJourney(newJourney));
        assertEquals("A localização de fim da jornada não pode ser vazia.", thrown.getMessage());
        verify(driverDAO, never()).findById(anyInt());
        verify(journeyDAO, never()).create(any(Journey.class));
    }

    @Test
    @DisplayName("6. Deve lançar BusinessException ao tentar criar jornada para motorista não existente")
    void testCreateJourneyDriverNotFound() throws SQLException {
        Journey newJourney = new Journey(0, 999, LocalDate.now(), "Rua A", "Av. B", 50.0, 1.5, null, null);

        when(driverDAO.findById(anyInt())).thenReturn(Optional.empty()); // Motorista não existe

        BusinessException thrown = assertThrows(BusinessException.class, () -> journeyService.createJourney(newJourney));
        assertEquals("Motorista com ID 999 não encontrado. Não é possível criar a jornada.", thrown.getMessage());
        verify(driverDAO, times(1)).findById(999);
        verify(journeyDAO, never()).create(any(Journey.class));
    }

    @Test
    @DisplayName("7. Deve lançar BusinessException ao tentar criar jornada para motorista que já tem jornada na mesma data")
    void testCreateJourneyDuplicateDriverAndDate() throws SQLException {
        Journey existingJourney = new Journey(1, 1, LocalDate.now(), "Rua X", "Av. Y", 60.0, 2.0, LocalDateTime.now(), LocalDateTime.now());
        Journey newJourney = new Journey(0, 1, LocalDate.now(), "Rua A", "Av. B", 50.0, 1.5, null, null);
        Driver existingDriver = new Driver(1, "Motorista Teste", "11122233344", "LIC123", "B", LocalDate.now().plusYears(1), "999", "m@t.com", LocalDateTime.now(), LocalDateTime.now());

        when(driverDAO.findById(anyInt())).thenReturn(Optional.of(existingDriver));
        when(journeyDAO.findByDriverIdAndDate(anyInt(), any(LocalDate.class))).thenReturn(Optional.of(existingJourney)); // Já existe jornada

        BusinessException thrown = assertThrows(BusinessException.class, () -> journeyService.createJourney(newJourney));
        assertEquals("Já existe uma jornada para o motorista com ID 1 na data " + LocalDate.now() + ".", thrown.getMessage());
        verify(driverDAO, times(1)).findById(1);
        verify(journeyDAO, times(1)).findByDriverIdAndDate(1, LocalDate.now());
        verify(journeyDAO, never()).create(any(Journey.class));
    }

    @Test
    @DisplayName("8. Deve lançar BusinessException em caso de erro de SQL na criação")
    void testCreateJourneySQLException() throws SQLException {
        Journey newJourney = new Journey(0, 1, LocalDate.now(), "Rua A", "Av. B", 50.0, 1.5, null, null);
        Driver existingDriver = new Driver(1, "Motorista Teste", "11122233344", "LIC123", "B", LocalDate.now().plusYears(1), "999", "m@t.com", LocalDateTime.now(), LocalDateTime.now());

        when(driverDAO.findById(anyInt())).thenReturn(Optional.of(existingDriver));
        when(journeyDAO.findByDriverIdAndDate(anyInt(), any(LocalDate.class))).thenReturn(Optional.empty());
        when(journeyDAO.create(any(Journey.class))).thenThrow(new SQLException("Erro de conexão com o DB"));

        BusinessException thrown = assertThrows(BusinessException.class, () -> journeyService.createJourney(newJourney));
        assertTrue(thrown.getMessage().contains("Erro interno ao criar a jornada."));
        assertTrue(thrown.getCause() instanceof SQLException);
        verify(driverDAO, times(1)).findById(1);
        verify(journeyDAO, times(1)).findByDriverIdAndDate(1, LocalDate.now());
        verify(journeyDAO, times(1)).create(any(Journey.class));
    }

    // --- Testes para getJourneyById ---

    @Test
    @DisplayName("9. Deve buscar uma jornada pelo ID com sucesso")
    void testGetJourneyByIdSuccess() throws SQLException, BusinessException {
        Journey existingJourney = new Journey(1, 1, LocalDate.now(), "Rua A", "Av. B", 50.0, 1.5, LocalDateTime.now(), LocalDateTime.now());

        when(journeyDAO.findById(1)).thenReturn(Optional.of(existingJourney));

        Journey foundJourney = journeyService.getJourneyById(1);

        assertNotNull(foundJourney);
        assertEquals(existingJourney.getId(), foundJourney.getId());
        assertEquals(existingJourney.getStartLocation(), foundJourney.getStartLocation());
        verify(journeyDAO, times(1)).findById(1);
    }

    @Test
    @DisplayName("10. Deve lançar BusinessException ao buscar jornada com ID não existente")
    void testGetJourneyByIdNotFound() throws SQLException {
        when(journeyDAO.findById(anyInt())).thenReturn(Optional.empty());

        BusinessException thrown = assertThrows(BusinessException.class, () -> journeyService.getJourneyById(999));
        assertEquals("Jornada com ID 999 não encontrada.", thrown.getMessage());
        verify(journeyDAO, times(1)).findById(999);
    }

    @Test
    @DisplayName("11. Deve lançar BusinessException em caso de erro de SQL na busca por ID")
    void testGetJourneyByIdSQLException() throws SQLException {
        when(journeyDAO.findById(anyInt())).thenThrow(new SQLException("Erro de DB"));

        BusinessException thrown = assertThrows(BusinessException.class, () -> journeyService.getJourneyById(1));
        assertTrue(thrown.getMessage().contains("Erro interno ao buscar a jornada."));
        assertTrue(thrown.getCause() instanceof SQLException);
        verify(journeyDAO, times(1)).findById(1);
    }

    // --- Testes para getAllJourneys ---

    @Test
    @DisplayName("12. Deve buscar todas as jornadas com sucesso")
    void testGetAllJourneysSuccess() throws SQLException, BusinessException {
        Journey journey1 = new Journey(1, 1, LocalDate.now(), "Rua A", "Av. B", 50.0, 1.5, LocalDateTime.now(), LocalDateTime.now());
        Journey journey2 = new Journey(2, 2, LocalDate.now().plusDays(1), "Rua C", "Av. D", 70.0, 2.0, LocalDateTime.now(), LocalDateTime.now());
        List<Journey> journeys = Arrays.asList(journey1, journey2);

        when(journeyDAO.findAll()).thenReturn(journeys);

        List<Journey> foundJourneys = journeyService.getAllJourneys();

        assertNotNull(foundJourneys);
        assertEquals(2, foundJourneys.size());
        assertEquals("Rua A", foundJourneys.get(0).getStartLocation());
        verify(journeyDAO, times(1)).findAll();
    }

    @Test
    @DisplayName("13. Deve retornar lista vazia se não houver jornadas")
    void testGetAllJourneysEmpty() throws SQLException, BusinessException {
        when(journeyDAO.findAll()).thenReturn(Arrays.asList());

        List<Journey> foundJourneys = journeyService.getAllJourneys();

        assertNotNull(foundJourneys);
        assertTrue(foundJourneys.isEmpty());
        verify(journeyDAO, times(1)).findAll();
    }

    @Test
    @DisplayName("14. Deve lançar BusinessException em caso de erro de SQL na busca de todas as jornadas")
    void testGetAllJourneysSQLException() throws SQLException {
        when(journeyDAO.findAll()).thenThrow(new SQLException("Erro de DB"));

        BusinessException thrown = assertThrows(BusinessException.class, () -> journeyService.getAllJourneys());
        assertTrue(thrown.getMessage().contains("Erro interno ao buscar todas as jornadas."));
        assertTrue(thrown.getCause() instanceof SQLException);
        verify(journeyDAO, times(1)).findAll();
    }

    // --- Testes para getJourneysByDriverId ---

    @Test
    @DisplayName("15. Deve buscar jornadas por ID de motorista com sucesso")
    void testGetJourneysByDriverIdSuccess() throws SQLException, BusinessException {
        Journey journey1 = new Journey(1, 1, LocalDate.now(), "Rua A", "Av. B", 50.0, 1.5, LocalDateTime.now(), LocalDateTime.now());
        Journey journey2 = new Journey(2, 1, LocalDate.now().plusDays(1), "Rua C", "Av. D", 70.0, 2.0, LocalDateTime.now(), LocalDateTime.now());
        List<Journey> journeys = Arrays.asList(journey1, journey2);

        when(journeyDAO.findByDriverId(1)).thenReturn(journeys);

        List<Journey> foundJourneys = journeyService.getJourneysByDriverId(1);

        assertNotNull(foundJourneys);
        assertEquals(2, foundJourneys.size());
        assertTrue(foundJourneys.stream().allMatch(j -> j.getDriverId() == 1));
        verify(journeyDAO, times(1)).findByDriverId(1);
    }

    @Test
    @DisplayName("16. Deve retornar lista vazia se não houver jornadas para o motorista")
    void testGetJourneysByDriverIdEmpty() throws SQLException, BusinessException {
        when(journeyDAO.findByDriverId(anyInt())).thenReturn(Arrays.asList());

        List<Journey> foundJourneys = journeyService.getJourneysByDriverId(1);

        assertNotNull(foundJourneys);
        assertTrue(foundJourneys.isEmpty());
        verify(journeyDAO, times(1)).findByDriverId(1);
    }

    @Test
    @DisplayName("17. Deve lançar BusinessException em caso de erro de SQL na busca por ID de motorista")
    void testGetJourneysByDriverIdSQLException() throws SQLException {
        when(journeyDAO.findByDriverId(anyInt())).thenThrow(new SQLException("Erro de DB"));

        BusinessException thrown = assertThrows(BusinessException.class, () -> journeyService.getJourneysByDriverId(1));
        assertTrue(thrown.getMessage().contains("Erro interno ao buscar jornadas por ID de motorista."));
        assertTrue(thrown.getCause() instanceof SQLException);
        verify(journeyDAO, times(1)).findByDriverId(1);
    }

    // --- Testes para updateJourney ---

    @Test
    @DisplayName("18. Deve atualizar uma jornada existente com sucesso")
    void testUpdateJourneySuccess() throws SQLException, BusinessException {
        Journey existingJourney = new Journey(1, 1, LocalDate.now().minusDays(1), "Origem Antiga", "Destino Antigo", 60.0, 1.8, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(2));
        Journey updatedJourney = new Journey(1, 1, LocalDate.now(), "Nova Origem", "Novo Destino", 65.0, 2.0, null, null);
        Driver existingDriver = new Driver(1, "Motorista Teste", "11122233344", "LIC123", "B", LocalDate.now().plusYears(1), "999", "m@t.com", LocalDateTime.now(), LocalDateTime.now());

        when(journeyDAO.findById(1)).thenReturn(Optional.of(existingJourney));
        when(driverDAO.findById(1)).thenReturn(Optional.of(existingDriver));
        // Simula que não há outra jornada para o mesmo motorista na nova data (se a data for alterada)
        when(journeyDAO.findByDriverIdAndDate(anyInt(), any(LocalDate.class))).thenReturn(Optional.empty());
        when(journeyDAO.update(any(Journey.class))).thenReturn(true);

        Journey resultJourney = journeyService.updateJourney(updatedJourney);

        assertNotNull(resultJourney);
        assertEquals(updatedJourney.getStartLocation(), resultJourney.getStartLocation());
        assertEquals(updatedJourney.getEndLocation(), resultJourney.getEndLocation());
        assertTrue(resultJourney.getUpdatedAt().isAfter(existingJourney.getUpdatedAt()));
        assertEquals(existingJourney.getCreatedAt(), resultJourney.getCreatedAt());

        verify(journeyDAO, times(1)).findById(1);
        verify(driverDAO, times(1)).findById(1);
        verify(journeyDAO, times(1)).findByDriverIdAndDate(1, updatedJourney.getJourneyDate());
        verify(journeyDAO, times(1)).update(any(Journey.class));
    }

    @Test
    @DisplayName("19. Deve lançar BusinessException ao tentar atualizar jornada com ID não existente")
    void testUpdateJourneyNotFound() throws SQLException {
        Journey updatedJourney = new Journey(999, 1, LocalDate.now(), "Origem Falsa", "Destino Falso", 100.0, 2.0, null, null);

        when(journeyDAO.findById(anyInt())).thenReturn(Optional.empty());

        BusinessException thrown = assertThrows(BusinessException.class, () -> journeyService.updateJourney(updatedJourney));
        assertEquals("Jornada com ID 999 não encontrada para atualização.", thrown.getMessage());
        verify(journeyDAO, times(1)).findById(999);
        verify(driverDAO, never()).findById(anyInt());
        verify(journeyDAO, never()).update(any(Journey.class));
    }

    @Test
    @DisplayName("20. Deve lançar BusinessException ao tentar atualizar jornada para motorista não existente")
    void testUpdateJourneyDriverNotFound() throws SQLException {
        Journey existingJourney = new Journey(1, 1, LocalDate.now().minusDays(1), "Origem Antiga", "Destino Antigo", 60.0, 1.8, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(2));
        Journey updatedJourney = new Journey(1, 999, LocalDate.now(), "Nova Origem", "Novo Destino", 65.0, 2.0, null, null);

        when(journeyDAO.findById(1)).thenReturn(Optional.of(existingJourney));
        when(driverDAO.findById(anyInt())).thenReturn(Optional.empty()); // Motorista não existe

        BusinessException thrown = assertThrows(BusinessException.class, () -> journeyService.updateJourney(updatedJourney));
        assertEquals("Motorista com ID 999 não encontrado. Não é possível atualizar a jornada.", thrown.getMessage());
        verify(journeyDAO, times(1)).findById(1);
        verify(driverDAO, times(1)).findById(999);
        verify(journeyDAO, never()).update(any(Journey.class));
    }

    @Test
    @DisplayName("21. Deve lançar BusinessException ao tentar atualizar jornada para motorista que já tem jornada na nova data (se diferente)")
    void testUpdateJourneyDuplicateDriverAndDateConflict() throws SQLException {
        LocalDate conflictDate = LocalDate.now().plusDays(5);
        Journey existingJourneyForUpdate = new Journey(1, 1, LocalDate.now(), "Origem Original", "Destino Original", 10.0, 0.5, LocalDateTime.now(), LocalDateTime.now());
        Journey existingJourneyWithConflict = new Journey(2, 1, conflictDate, "Origem Conflito", "Destino Conflito", 20.0, 1.0, LocalDateTime.now(), LocalDateTime.now());
        Journey updatedJourney = new Journey(1, 1, conflictDate, "Nova Origem", "Novo Destino", 15.0, 0.8, null, null); // Tenta mudar para data de existingJourneyWithConflict
        Driver existingDriver = new Driver(1, "Motorista Teste", "11122233344", "LIC123", "B", LocalDate.now().plusYears(1), "999", "m@t.com", LocalDateTime.now(), LocalDateTime.now());

        when(journeyDAO.findById(1)).thenReturn(Optional.of(existingJourneyForUpdate));
        when(driverDAO.findById(1)).thenReturn(Optional.of(existingDriver));
        // Simula que já existe uma jornada para o mesmo motorista na nova data, e não é a própria jornada que está sendo atualizada
        when(journeyDAO.findByDriverIdAndDate(1, conflictDate)).thenReturn(Optional.of(existingJourneyWithConflict));

        BusinessException thrown = assertThrows(BusinessException.class, () -> journeyService.updateJourney(updatedJourney));
        assertEquals("Já existe outra jornada para o motorista com ID 1 na data " + conflictDate + ".", thrown.getMessage());
        verify(journeyDAO, times(1)).findById(1);
        verify(driverDAO, times(1)).findById(1);
        verify(journeyDAO, times(1)).findByDriverIdAndDate(1, conflictDate);
        verify(journeyDAO, never()).update(any(Journey.class));
    }

    @Test
    @DisplayName("22. Deve lançar BusinessException em caso de erro de SQL na atualização")
    void testUpdateJourneySQLException() throws SQLException {
        Journey existingJourney = new Journey(1, 1, LocalDate.now().minusDays(1), "Origem Antiga", "Destino Antigo", 60.0, 1.8, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(2));
        Journey updatedJourney = new Journey(1, 1, LocalDate.now(), "Nova Origem", "Novo Destino", 65.0, 2.0, null, null);
        Driver existingDriver = new Driver(1, "Motorista Teste", "11122233344", "LIC123", "B", LocalDate.now().plusYears(1), "999", "m@t.com", LocalDateTime.now(), LocalDateTime.now());

        when(journeyDAO.findById(1)).thenReturn(Optional.of(existingJourney));
        when(driverDAO.findById(1)).thenReturn(Optional.of(existingDriver));
        when(journeyDAO.findByDriverIdAndDate(anyInt(), any(LocalDate.class))).thenReturn(Optional.empty());
        when(journeyDAO.update(any(Journey.class))).thenThrow(new SQLException("Erro de DB na atualização"));

        BusinessException thrown = assertThrows(BusinessException.class, () -> journeyService.updateJourney(updatedJourney));
        assertTrue(thrown.getMessage().contains("Erro interno ao atualizar a jornada."));
        assertTrue(thrown.getCause() instanceof SQLException);
        verify(journeyDAO, times(1)).findById(1);
        verify(driverDAO, times(1)).findById(1);
        verify(journeyDAO, times(1)).findByDriverIdAndDate(1, updatedJourney.getJourneyDate());
        verify(journeyDAO, times(1)).update(any(Journey.class));
    }

    // --- Testes para deleteJourney ---

    @Test
    @DisplayName("23. Deve deletar uma jornada existente com sucesso")
    void testDeleteJourneySuccess() throws SQLException, BusinessException {
        Journey existingJourney = new Journey(1, 1, LocalDate.now(), "Origem para Deletar", "Destino para Deletar", 80.0, 2.2, LocalDateTime.now(), LocalDateTime.now());

        when(journeyDAO.findById(1)).thenReturn(Optional.of(existingJourney));
        when(journeyDAO.delete(1)).thenReturn(true);

        boolean deleted = journeyService.deleteJourney(1);

        assertTrue(deleted);
        verify(journeyDAO, times(1)).findById(1);
        verify(journeyDAO, times(1)).delete(1);
    }

    @Test
    @DisplayName("24. Deve lançar BusinessException ao tentar deletar jornada com ID <= 0")
    void testDeleteJourneyInvalidId() {
        BusinessException thrown = assertThrows(BusinessException.class, () -> journeyService.deleteJourney(0));
        assertEquals("O ID da jornada deve ser um valor positivo para exclusão.", thrown.getMessage());
        verify(journeyDAO, never()).delete(anyInt());
    }

    @Test
    @DisplayName("25. Deve lançar BusinessException ao tentar deletar jornada com ID não existente")
    void testDeleteJourneyNotFound() throws SQLException {
        when(journeyDAO.findById(anyInt())).thenReturn(Optional.empty());

        BusinessException thrown = assertThrows(BusinessException.class, () -> journeyService.deleteJourney(999));
        assertEquals("Jornada com ID 999 não encontrada para exclusão.", thrown.getMessage());
        verify(journeyDAO, times(1)).findById(999);
        verify(journeyDAO, never()).delete(anyInt());
    }

    @Test
    @DisplayName("26. Deve lançar BusinessException em caso de erro de SQL na exclusão")
    void testDeleteJourneySQLException() throws SQLException {
        Journey existingJourney = new Journey(1, 1, LocalDate.now(), "Origem para Deletar", "Destino para Deletar", 80.0, 2.2, LocalDateTime.now(), LocalDateTime.now());

        when(journeyDAO.findById(1)).thenReturn(Optional.of(existingJourney));
        when(journeyDAO.delete(1)).thenThrow(new SQLException("Erro de DB na exclusão"));

        BusinessException thrown = assertThrows(BusinessException.class, () -> journeyService.deleteJourney(1));
        assertTrue(thrown.getMessage().contains("Erro interno ao deletar a jornada."));
        assertTrue(thrown.getCause() instanceof SQLException);
        verify(journeyDAO, times(1)).findById(1);
        verify(journeyDAO, times(1)).delete(1);
    }
}
