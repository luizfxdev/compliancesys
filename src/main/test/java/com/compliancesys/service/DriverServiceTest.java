package com.compliancesys.service;

import com.compliancesys.dao.DriverDAO;
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.Driver;
import com.compliancesys.service.impl.DriverServiceImpl;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Classe de teste para DriverService.
 * Utiliza Mockito para simular a camada DAO e testar a lógica de negócio do serviço.
 */
public class DriverServiceTest {

    @Mock
    private DriverDAO driverDAO; // Mock da interface DAO

    @InjectMocks
    private DriverServiceImpl driverService; // Injeta o mock no serviço

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Inicializa os mocks antes de cada teste
    }

    // --- Testes para createDriver ---

    @Test
    @DisplayName("1. Deve criar um novo motorista com sucesso")
    void testCreateDriverSuccess() throws SQLException, BusinessException {
        Driver newDriver = new Driver(0, "João Silva", "12345678901", "ABC12345678", "D", LocalDate.now().plusYears(5), "11987654321", "joao@email.com", null, null);

        // Configura o mock para simular que o CPF não existe e a criação retorna um ID
        when(driverDAO.findByCpf(anyString())).thenReturn(Optional.empty());
        when(driverDAO.create(any(Driver.class))).thenReturn(1); // Simula que a criação retorna o ID 1

        Driver createdDriver = driverService.createDriver(newDriver);

        assertNotNull(createdDriver);
        assertEquals(1, createdDriver.getId()); // Verifica se o ID foi setado
        assertEquals("João Silva", createdDriver.getName());
        assertNotNull(createdDriver.getCreatedAt());
        assertNotNull(createdDriver.getUpdatedAt());

        // Verifica se os métodos do DAO foram chamados corretamente
        verify(driverDAO, times(1)).findByCpf("12345678901");
        verify(driverDAO, times(1)).create(any(Driver.class));
    }

    @Test
    @DisplayName("2. Deve lançar BusinessException ao tentar criar motorista com nome vazio")
    void testCreateDriverEmptyName() {
        Driver newDriver = new Driver(0, "", "12345678901", "ABC12345678", "D", LocalDate.now().plusYears(5), "11987654321", "joao@email.com", null, null);

        BusinessException thrown = assertThrows(BusinessException.class, () -> driverService.createDriver(newDriver));
        assertEquals("O nome do motorista não pode ser vazio.", thrown.getMessage());
        verify(driverDAO, never()).create(any(Driver.class)); // Garante que o DAO não foi chamado
    }

    @Test
    @DisplayName("3. Deve lançar BusinessException ao tentar criar motorista com CPF vazio")
    void testCreateDriverEmptyCpf() {
        Driver newDriver = new Driver(0, "João Silva", "", "ABC12345678", "D", LocalDate.now().plusYears(5), "11987654321", "joao@email.com", null, null);

        BusinessException thrown = assertThrows(BusinessException.class, () -> driverService.createDriver(newDriver));
        assertEquals("O CPF do motorista não pode ser vazio.", thrown.getMessage());
        verify(driverDAO, never()).create(any(Driver.class));
    }

    @Test
    @DisplayName("4. Deve lançar BusinessException ao tentar criar motorista com CPF inválido")
    void testCreateDriverInvalidCpf() {
        Driver newDriver = new Driver(0, "João Silva", "123", "ABC12345678", "D", LocalDate.now().plusYears(5), "11987654321", "joao@email.com", null, null);

        BusinessException thrown = assertThrows(BusinessException.class, () -> driverService.createDriver(newDriver));
        assertEquals("O CPF deve conter 11 dígitos numéricos.", thrown.getMessage());
        verify(driverDAO, never()).create(any(Driver.class));
    }

    @Test
    @DisplayName("5. Deve lançar BusinessException ao tentar criar motorista com número de licença vazio")
    void testCreateDriverEmptyLicenseNumber() {
        Driver newDriver = new Driver(0, "João Silva", "12345678901", "", "D", LocalDate.now().plusYears(5), "11987654321", "joao@email.com", null, null);

        BusinessException thrown = assertThrows(BusinessException.class, () -> driverService.createDriver(newDriver));
        assertEquals("O número da licença não pode ser vazio.", thrown.getMessage());
        verify(driverDAO, never()).create(any(Driver.class));
    }

    @Test
    @DisplayName("6. Deve lançar BusinessException ao tentar criar motorista com categoria de licença vazia")
    void testCreateDriverEmptyLicenseCategory() {
        Driver newDriver = new Driver(0, "João Silva", "12345678901", "ABC12345678", "", LocalDate.now().plusYears(5), "11987654321", "joao@email.com", null, null);

        BusinessException thrown = assertThrows(BusinessException.class, () -> driverService.createDriver(newDriver));
        assertEquals("A categoria da licença não pode ser vazia.", thrown.getMessage());
        verify(driverDAO, never()).create(any(Driver.class));
    }

    @Test
    @DisplayName("7. Deve lançar BusinessException ao tentar criar motorista com data de expiração da licença nula")
    void testCreateDriverNullLicenseExpirationDate() {
        Driver newDriver = new Driver(0, "João Silva", "12345678901", "ABC12345678", "D", null, "11987654321", "joao@email.com", null, null);

        BusinessException thrown = assertThrows(BusinessException.class, () -> driverService.createDriver(newDriver));
        assertEquals("A data de expiração da licença é obrigatória.", thrown.getMessage());
        verify(driverDAO, never()).create(any(Driver.class));
    }

    @Test
    @DisplayName("8. Deve lançar BusinessException ao tentar criar motorista com licença expirada")
    void testCreateDriverExpiredLicense() {
        Driver newDriver = new Driver(0, "João Silva", "12345678901", "ABC12345678", "D", LocalDate.now().minusDays(1), "11987654321", "joao@email.com", null, null);

        BusinessException thrown = assertThrows(BusinessException.class, () -> driverService.createDriver(newDriver));
        assertEquals("A licença do motorista já está expirada.", thrown.getMessage());
        verify(driverDAO, never()).create(any(Driver.class));
    }

    @Test
    @DisplayName("9. Deve lançar BusinessException ao tentar criar motorista com CPF já existente")
    void testCreateDriverDuplicateCpf() throws SQLException {
        Driver existingDriver = new Driver(1, "Maria Existente", "12345678901", "DEF98765432", "C", LocalDate.now().plusYears(3), "11999999999", "maria@email.com", LocalDateTime.now(), LocalDateTime.now());
        Driver newDriver = new Driver(0, "João Silva", "12345678901", "ABC12345678", "D", LocalDate.now().plusYears(5), "11987654321", "joao@email.com", null, null);

        // Configura o mock para simular que o CPF já existe
        when(driverDAO.findByCpf(anyString())).thenReturn(Optional.of(existingDriver));

        BusinessException thrown = assertThrows(BusinessException.class, () -> driverService.createDriver(newDriver));
        assertEquals("Já existe um motorista cadastrado com este CPF: 12345678901", thrown.getMessage());
        verify(driverDAO, times(1)).findByCpf("12345678901");
        verify(driverDAO, never()).create(any(Driver.class));
    }

    @Test
    @DisplayName("10. Deve lançar BusinessException em caso de erro de SQL na criação")
    void testCreateDriverSQLException() throws SQLException {
        Driver newDriver = new Driver(0, "João Silva", "12345678901", "ABC12345678", "D", LocalDate.now().plusYears(5), "11987654321", "joao@email.com", null, null);

        when(driverDAO.findByCpf(anyString())).thenReturn(Optional.empty());
        when(driverDAO.create(any(Driver.class))).thenThrow(new SQLException("Erro de conexão com o DB"));

        BusinessException thrown = assertThrows(BusinessException.class, () -> driverService.createDriver(newDriver));
        assertTrue(thrown.getMessage().contains("Erro interno ao criar o motorista."));
        assertTrue(thrown.getCause() instanceof SQLException);
        verify(driverDAO, times(1)).findByCpf("12345678901");
        verify(driverDAO, times(1)).create(any(Driver.class));
    }

    // --- Testes para getDriverById ---

    @Test
    @DisplayName("11. Deve buscar um motorista pelo ID com sucesso")
    void testGetDriverByIdSuccess() throws SQLException, BusinessException {
        Driver existingDriver = new Driver(1, "João Silva", "12345678901", "ABC12345678", "D", LocalDate.now().plusYears(5), "11987654321", "joao@email.com", LocalDateTime.now(), LocalDateTime.now());

        when(driverDAO.findById(1)).thenReturn(Optional.of(existingDriver));

        Driver foundDriver = driverService.getDriverById(1);

        assertNotNull(foundDriver);
        assertEquals(existingDriver.getId(), foundDriver.getId());
        assertEquals(existingDriver.getName(), foundDriver.getName());
        verify(driverDAO, times(1)).findById(1);
    }

    @Test
    @DisplayName("12. Deve lançar BusinessException ao buscar motorista com ID não existente")
    void testGetDriverByIdNotFound() throws SQLException {
        when(driverDAO.findById(anyInt())).thenReturn(Optional.empty());

        BusinessException thrown = assertThrows(BusinessException.class, () -> driverService.getDriverById(999));
        assertEquals("Motorista com ID 999 não encontrado.", thrown.getMessage());
        verify(driverDAO, times(1)).findById(999);
    }

    @Test
    @DisplayName("13. Deve lançar BusinessException em caso de erro de SQL na busca por ID")
    void testGetDriverByIdSQLException() throws SQLException {
        when(driverDAO.findById(anyInt())).thenThrow(new SQLException("Erro de DB"));

        BusinessException thrown = assertThrows(BusinessException.class, () -> driverService.getDriverById(1));
        assertTrue(thrown.getMessage().contains("Erro interno ao buscar o motorista."));
        assertTrue(thrown.getCause() instanceof SQLException);
        verify(driverDAO, times(1)).findById(1);
    }

    // --- Testes para getAllDrivers ---

    @Test
    @DisplayName("14. Deve buscar todos os motoristas com sucesso")
    void testGetAllDriversSuccess() throws SQLException, BusinessException {
        Driver driver1 = new Driver(1, "João Silva", "11122233344", "ABC12345678", "D", LocalDate.now().plusYears(5), "11987654321", "joao@email.com", LocalDateTime.now(), LocalDateTime.now());
        Driver driver2 = new Driver(2, "Maria Souza", "22233344455", "DEF98765432", "C", LocalDate.now().plusYears(3), "11998765432", "maria@email.com", LocalDateTime.now(), LocalDateTime.now());
        List<Driver> drivers = Arrays.asList(driver1, driver2);

        when(driverDAO.findAll()).thenReturn(drivers);

        List<Driver> foundDrivers = driverService.getAllDrivers();

        assertNotNull(foundDrivers);
        assertEquals(2, foundDrivers.size());
        assertEquals("João Silva", foundDrivers.get(0).getName());
        verify(driverDAO, times(1)).findAll();
    }

    @Test
    @DisplayName("15. Deve retornar lista vazia se não houver motoristas")
    void testGetAllDriversEmpty() throws SQLException, BusinessException {
        when(driverDAO.findAll()).thenReturn(Arrays.asList());

        List<Driver> foundDrivers = driverService.getAllDrivers();

        assertNotNull(foundDrivers);
        assertTrue(foundDrivers.isEmpty());
        verify(driverDAO, times(1)).findAll();
    }

    @Test
    @DisplayName("16. Deve lançar BusinessException em caso de erro de SQL na busca de todos os motoristas")
    void testGetAllDriversSQLException() throws SQLException {
        when(driverDAO.findAll()).thenThrow(new SQLException("Erro de DB"));

        BusinessException thrown = assertThrows(BusinessException.class, () -> driverService.getAllDrivers());
        assertTrue(thrown.getMessage().contains("Erro interno ao buscar todos os motoristas."));
        assertTrue(thrown.getCause() instanceof SQLException);
        verify(driverDAO, times(1)).findAll();
    }

    // --- Testes para updateDriver ---

    @Test
    @DisplayName("17. Deve atualizar um motorista existente com sucesso")
    void testUpdateDriverSuccess() throws SQLException, BusinessException {
        Driver existingDriver = new Driver(1, "João Antigo", "12345678901", "ABC12345678", "D", LocalDate.now().plusYears(5), "11987654321", "antigo@email.com", LocalDateTime.now().minusDays(1), LocalDateTime.now().minusDays(1));
        Driver updatedDriver = new Driver(1, "João Novo", "12345678901", "XYZ98765432", "E", LocalDate.now().plusYears(10), "11998877665", "novo@email.com", null, null);

        when(driverDAO.findById(1)).thenReturn(Optional.of(existingDriver));
        when(driverDAO.findByCpf(anyString())).thenReturn(Optional.of(existingDriver)); // CPF é o mesmo, então encontra o próprio motorista
        when(driverDAO.update(any(Driver.class))).thenReturn(true);

        Driver resultDriver = driverService.updateDriver(updatedDriver);

        assertNotNull(resultDriver);
        assertEquals(updatedDriver.getName(), resultDriver.getName());
        assertEquals(updatedDriver.getLicenseNumber(), resultDriver.getLicenseNumber());
        assertTrue(resultDriver.getUpdatedAt().isAfter(existingDriver.getUpdatedAt())); // Verifica se a data de atualização mudou
        assertEquals(existingDriver.getCreatedAt(), resultDriver.getCreatedAt()); // Data de criação deve ser mantida

        verify(driverDAO, times(1)).findById(1);
        verify(driverDAO, times(1)).findByCpf("12345678901");
        verify(driverDAO, times(1)).update(any(Driver.class));
    }

    @Test
    @DisplayName("18. Deve lançar BusinessException ao tentar atualizar motorista com ID não existente")
    void testUpdateDriverNotFound() throws SQLException {
        Driver updatedDriver = new Driver(999, "Motorista Falso", "12345678901", "ABC12345678", "D", LocalDate.now().plusYears(5), "11987654321", "fake@email.com", null, null);

        when(driverDAO.findById(anyInt())).thenReturn(Optional.empty());

        BusinessException thrown = assertThrows(BusinessException.class, () -> driverService.updateDriver(updatedDriver));
        assertEquals("Motorista com ID 999 não encontrado para atualização.", thrown.getMessage());
        verify(driverDAO, times(1)).findById(999);
        verify(driverDAO, never()).update(any(Driver.class));
    }

    @Test
    @DisplayName("19. Deve lançar BusinessException ao tentar atualizar motorista com nome vazio")
    void testUpdateDriverEmptyName() throws SQLException {
        Driver existingDriver = new Driver(1, "João Antigo", "12345678901", "ABC12345678", "D", LocalDate.now().plusYears(5), "11987654321", "antigo@email.com", LocalDateTime.now().minusDays(1), LocalDateTime.now().minusDays(1));
        Driver updatedDriver = new Driver(1, "", "12345678901", "XYZ98765432", "E", LocalDate.now().plusYears(10), "11998877665", "novo@email.com", null, null);

        when(driverDAO.findById(1)).thenReturn(Optional.of(existingDriver));

        BusinessException thrown = assertThrows(BusinessException.class, () -> driverService.updateDriver(updatedDriver));
        assertEquals("O nome do motorista não pode ser vazio.", thrown.getMessage());
        verify(driverDAO, times(1)).findById(1);
        verify(driverDAO, never()).update(any(Driver.class));
    }

    @Test
    @DisplayName("20. Deve lançar BusinessException ao tentar atualizar motorista com CPF duplicado de outro")
    void testUpdateDriverDuplicateCpfConflict() throws SQLException {
        Driver existingDriver1 = new Driver(1, "Motorista 1", "11122233344", "LIC1", "B", LocalDate.now().plusYears(1), "1", "m1@m.com", LocalDateTime.now(), LocalDateTime.now());
        Driver existingDriver2 = new Driver(2, "Motorista 2", "22233344455", "LIC2", "C", LocalDate.now().plusYears(2), "2", "m2@m.com", LocalDateTime.now(), LocalDateTime.now());

        Driver updatedDriver2 = new Driver(2, "Motorista 2 Atualizado", "11122233344", "LIC2 Novo", "D", LocalDate.now().plusYears(3), "2", "m2@m.com", null, null); // Tenta usar CPF do Motorista 1

        when(driverDAO.findById(2)).thenReturn(Optional.of(existingDriver2));
        when(driverDAO.findByCpf("11122233344")).thenReturn(Optional.of(existingDriver1)); // Encontra o Motorista 1 com o CPF

        BusinessException thrown = assertThrows(BusinessException.class, () -> driverService.updateDriver(updatedDriver2));
        assertEquals("Já existe outro motorista cadastrado com este CPF: 11122233344", thrown.getMessage());
        verify(driverDAO, times(1)).findById(2);
        verify(driverDAO, times(1)).findByCpf("11122233344");
        verify(driverDAO, never()).update(any(Driver.class));
    }

    @Test
    @DisplayName("21. Deve lançar BusinessException em caso de erro de SQL na atualização")
    void testUpdateDriverSQLException() throws SQLException {
        Driver existingDriver = new Driver(1, "João Antigo", "12345678901", "ABC12345678", "D", LocalDate.now().plusYears(5), "11987654321", "antigo@email.com", LocalDateTime.now().minusDays(1), LocalDateTime.now().minusDays(1));
        Driver updatedDriver = new Driver(1, "João Novo", "12345678901", "XYZ98765432", "E", LocalDate.now().plusYears(10), "11998877665", "novo@email.com", null, null);

        when(driverDAO.findById(1)).thenReturn(Optional.of(existingDriver));
        when(driverDAO.findByCpf(anyString())).thenReturn(Optional.of(existingDriver));
        when(driverDAO.update(any(Driver.class))).thenThrow(new SQLException("Erro de DB na atualização"));

        BusinessException thrown = assertThrows(BusinessException.class, () -> driverService.updateDriver(updatedDriver));
        assertTrue(thrown.getMessage().contains("Erro interno ao atualizar o motorista."));
        assertTrue(thrown.getCause() instanceof SQLException);
        verify(driverDAO, times(1)).findById(1);
        verify(driverDAO, times(1)).findByCpf("12345678901");
        verify(driverDAO, times(1)).update(any(Driver.class));
    }

    // --- Testes para deleteDriver ---

    @Test
    @DisplayName("22. Deve deletar um motorista existente com sucesso")
    void testDeleteDriverSuccess() throws SQLException, BusinessException {
        Driver existingDriver = new Driver(1, "Motorista para Deletar", "11122233344", "LICDEL", "B", LocalDate.now().plusYears(1), "1", "del@del.com", LocalDateTime.now(), LocalDateTime.now());

        when(driverDAO.findById(1)).thenReturn(Optional.of(existingDriver)); // Motorista existe
        when(driverDAO.delete(1)).thenReturn(true);

        boolean deleted = driverService.deleteDriver(1);

        assertTrue(deleted);
        verify(driverDAO, times(1)).findById(1);
        verify(driverDAO, times(1)).delete(1);
    }

    @Test
    @DisplayName("23. Deve lançar BusinessException ao tentar deletar motorista com ID <= 0")
    void testDeleteDriverInvalidId() {
        BusinessException thrown = assertThrows(BusinessException.class, () -> driverService.deleteDriver(0));
        assertEquals("O ID do motorista deve ser um valor positivo para exclusão.", thrown.getMessage());
        verify(driverDAO, never()).delete(anyInt());
    }

    @Test
    @DisplayName("24. Deve lançar BusinessException ao tentar deletar motorista com ID não existente")
    void testDeleteDriverNotFound() throws SQLException {
        when(driverDAO.findById(anyInt())).thenReturn(Optional.empty()); // Motorista não existe

        BusinessException thrown = assertThrows(BusinessException.class, () -> driverService.deleteDriver(999));
        assertEquals("Motorista com ID 999 não encontrado para exclusão.", thrown.getMessage());
        verify(driverDAO, times(1)).findById(999);
        verify(driverDAO, never()).delete(anyInt());
    }

    @Test
    @DisplayName("25. Deve lançar BusinessException em caso de erro de SQL na exclusão")
    void testDeleteDriverSQLException() throws SQLException {
        Driver existingDriver = new Driver(1, "Motorista para Deletar", "11122233344", "LICDEL", "B", LocalDate.now().plusYears(1), "1", "del@del.com", LocalDateTime.now(), LocalDateTime.now());

        when(driverDAO.findById(1)).thenReturn(Optional.of(existingDriver));
        when(driverDAO.delete(1)).thenThrow(new SQLException("Erro de DB na exclusão"));

        BusinessException thrown = assertThrows(BusinessException.class, () -> driverService.deleteDriver(1));
        assertTrue(thrown.getMessage().contains("Erro interno ao deletar o motorista."));
        assertTrue(thrown.getCause() instanceof SQLException);
        verify(driverDAO, times(1)).findById(1);
        verify(driverDAO, times(1)).delete(1);
    }
}
