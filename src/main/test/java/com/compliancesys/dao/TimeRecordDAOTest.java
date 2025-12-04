package com.compliancesys.dao;

import com.compliancesys.config.DatabaseConfig;
import com.compliancesys.dao.impl.TimeRecordDAOImpl;
import com.compliancesys.model.TimeRecord;
import com.compliancesys.model.enums.EventType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de integração para TimeRecordDAOImpl.
 * Requer um banco de dados PostgreSQL em execução e configurado em src/test/resources/database.properties.
 */
class TimeRecordDAOTest {

    private TimeRecordDAO timeRecordDAO;
    private Connection connection;

    // IDs de entidades relacionadas para testes
    private int testDriverId;
    private int testVehicleId;

    @BeforeEach
    void setUp() throws SQLException {
        // Garante que a configuração do banco de dados esteja carregada
        DatabaseConfig.getInstance();
        connection = DatabaseConfig.getInstance().getConnection();
        connection.setAutoCommit(false); // Inicia uma transação para cada teste

        timeRecordDAO = new TimeRecordDAOImpl(connection);

        // Prepara dados de motorista e veículo para as chaves estrangeiras
        testDriverId = insertTestDriver();
        testVehicleId = insertTestVehicle();

        // Limpa a tabela time_records antes de cada teste
        clearTable("time_records");
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (connection != null) {
            connection.rollback(); // Reverte a transação após cada teste
            connection.setAutoCommit(true);
            connection.close();
        }
        // Limpa as tabelas de suporte também
        clearTable("time_records");
        clearTable("drivers");
        clearTable("vehicles");
    }

    private int insertTestDriver() throws SQLException {
        String sql = "INSERT INTO drivers (name, cpf, license_number, birth_date, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "Motorista Teste");
            stmt.setString(2, "111.111.111-11");
            stmt.setString(3, "12345678901");
            stmt.setDate(4, java.sql.Date.valueOf("1980-01-01"));
            stmt.setTimestamp(5, java.sql.Timestamp.valueOf(LocalDateTime.now()));
            stmt.setTimestamp(6, java.sql.Timestamp.valueOf(LocalDateTime.now()));
            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }
        throw new SQLException("Falha ao inserir motorista de teste.");
    }

    private int insertTestVehicle() throws SQLException {
        String sql = "INSERT INTO vehicles (plate, model, year, created_at, updated_at) VALUES (?, ?, ?, ?, ?) RETURNING id";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "ABC1234");
            stmt.setString(2, "Caminhão Teste");
            stmt.setInt(3, 2020);
            stmt.setTimestamp(4, java.sql.Timestamp.valueOf(LocalDateTime.now()));
            stmt.setTimestamp(5, java.sql.Timestamp.valueOf(LocalDateTime.now()));
            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }
        throw new SQLException("Falha ao inserir veículo de teste.");
    }

    private void clearTable(String tableName) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM " + tableName)) {
            stmt.executeUpdate();
        }
    }

    @Test
    @DisplayName("Deve adicionar um novo registro de ponto")
    void testAddTimeRecord() {
        TimeRecord timeRecord = new TimeRecord(testDriverId, testVehicleId, EventType.INICIO_JORNADA, LocalDateTime.now());
        TimeRecord addedRecord = timeRecordDAO.add(timeRecord);

        assertNotNull(addedRecord, "O registro de ponto adicionado não deve ser nulo.");
        assertTrue(addedRecord.getId() > 0, "O ID do registro de ponto deve ser maior que 0.");
        assertEquals(timeRecord.getDriverId(), addedRecord.getDriverId());
        assertEquals(timeRecord.getVehicleId(), addedRecord.getVehicleId());
        assertEquals(timeRecord.getEventType(), addedRecord.getEventType());
        // Comparar LocalDateTime com uma pequena tolerância devido a diferenças de precisão no DB
        assertTrue(timeRecord.getRecordTime().isEqual(addedRecord.getRecordTime()) ||
                   timeRecord.getRecordTime().plusNanos(999_999_999).isAfter(addedRecord.getRecordTime()) &&
                   timeRecord.getRecordTime().minusNanos(999_999_999).isBefore(addedRecord.getRecordTime()),
                   "Os timestamps devem ser aproximadamente iguais.");
        assertNotNull(addedRecord.getCreatedAt(), "O createdAt não deve ser nulo.");
        assertNotNull(addedRecord.getUpdatedAt(), "O updatedAt não deve ser nulo.");
    }

    @Test
    @DisplayName("Deve encontrar um registro de ponto pelo ID")
    void testFindTimeRecordById() {
        TimeRecord timeRecord = new TimeRecord(testDriverId, testVehicleId, EventType.FIM_JORNADA, LocalDateTime.now().minusHours(1));
        TimeRecord addedRecord = timeRecordDAO.add(timeRecord);

        Optional<TimeRecord> foundRecord = timeRecordDAO.findById(addedRecord.getId());

        assertTrue(foundRecord.isPresent(), "O registro de ponto deve ser encontrado.");
        assertEquals(addedRecord.getId(), foundRecord.get().getId());
        assertEquals(addedRecord.getDriverId(), foundRecord.get().getDriverId());
        assertEquals(addedRecord.getVehicleId(), foundRecord.get().getVehicleId());
        assertEquals(addedRecord.getEventType(), foundRecord.get().getEventType());
    }

    @Test
    @DisplayName("Não deve encontrar um registro de ponto com ID inexistente")
    void testFindTimeRecordByIdNotFound() {
        Optional<TimeRecord> foundRecord = timeRecordDAO.findById(99999);
        assertFalse(foundRecord.isPresent(), "Nenhum registro de ponto deve ser encontrado para um ID inexistente.");
    }

    @Test
    @DisplayName("Deve atualizar um registro de ponto existente")
    void testUpdateTimeRecord() {
        TimeRecord timeRecord = new TimeRecord(testDriverId, testVehicleId, EventType.INICIO_DESCANSO, LocalDateTime.now().minusHours(2));
        TimeRecord addedRecord = timeRecordDAO.add(timeRecord);

        addedRecord.setEventType(EventType.FIM_DESCANSO);
        addedRecord.setRecordTime(LocalDateTime.now());
        addedRecord.setVehicleId(testVehicleId); // Mantém o mesmo veículo ou altera para outro válido

        TimeRecord updatedRecord = timeRecordDAO.update(addedRecord);

        assertNotNull(updatedRecord, "O registro de ponto atualizado não deve ser nulo.");
        assertEquals(addedRecord.getId(), updatedRecord.getId());
        assertEquals(EventType.FIM_DESCANSO, updatedRecord.getEventType());
        assertTrue(addedRecord.getRecordTime().isEqual(updatedRecord.getRecordTime()) ||
                   addedRecord.getRecordTime().plusNanos(999_999_999).isAfter(updatedRecord.getRecordTime()) &&
                   addedRecord.getRecordTime().minusNanos(999_999_999).isBefore(updatedRecord.getRecordTime()),
                   "Os timestamps devem ser aproximadamente iguais após a atualização.");
        assertTrue(updatedRecord.getUpdatedAt().isAfter(updatedRecord.getCreatedAt()), "O updatedAt deve ser posterior ao createdAt após a atualização.");
    }

    @Test
    @DisplayName("Deve deletar um registro de ponto existente")
    void testDeleteTimeRecord() {
        TimeRecord timeRecord = new TimeRecord(testDriverId, testVehicleId, EventType.ESPERA, LocalDateTime.now().minusMinutes(30));
        TimeRecord addedRecord = timeRecordDAO.add(timeRecord);

        boolean deleted = timeRecordDAO.delete(addedRecord.getId());
        assertTrue(deleted, "O registro de ponto deve ser deletado com sucesso.");

        Optional<TimeRecord> foundRecord = timeRecordDAO.findById(addedRecord.getId());
        assertFalse(foundRecord.isPresent(), "O registro de ponto não deve ser encontrado após a exclusão.");
    }

    @Test
    @DisplayName("Não deve deletar um registro de ponto inexistente")
    void testDeleteNonExistentTimeRecord() {
        boolean deleted = timeRecordDAO.delete(99999);
        assertFalse(deleted, "Não deve ser possível deletar um registro de ponto inexistente.");
    }

    @Test
    @DisplayName("Deve encontrar todos os registros de ponto para um motorista em uma data específica")
    void testFindByDriverIdAndDate() {
        LocalDateTime date = LocalDateTime.of(2025, 12, 4, 10, 0, 0);
        timeRecordDAO.add(new TimeRecord(testDriverId, testVehicleId, EventType.INICIO_JORNADA, date));
        timeRecordDAO.add(new TimeRecord(testDriverId, testVehicleId, EventType.INICIO_DIRECAO, date.plusHours(1)));
        timeRecordDAO.add(new TimeRecord(testDriverId, testVehicleId, EventType.FIM_JORNADA, date.plusHours(8)));
        // Adiciona um registro para outro motorista para garantir que não seja retornado
        int otherDriverId = 0;
        try {
            otherDriverId = insertTestDriver();
        } catch (SQLException e) {
            fail("Falha ao inserir outro motorista de teste: " + e.getMessage());
        }
        timeRecordDAO.add(new TimeRecord(otherDriverId, testVehicleId, EventType.INICIO_JORNADA, date));


        List<TimeRecord> records = timeRecordDAO.findByDriverIdAndDate(testDriverId, date.toLocalDate());

        assertNotNull(records, "A lista de registros não deve ser nula.");
        assertEquals(3, records.size(), "Deve retornar 3 registros para o motorista e data especificados.");
        assertTrue(records.stream().allMatch(r -> r.getDriverId() == testDriverId && r.getRecordTime().toLocalDate().isEqual(date.toLocalDate())));
    }

    @Test
    @DisplayName("Deve retornar uma lista vazia se não houver registros para o motorista e data")
    void testFindByDriverIdAndDateNoRecords() {
        List<TimeRecord> records = timeRecordDAO.findByDriverIdAndDate(testDriverId, LocalDateTime.now().toLocalDate());
        assertNotNull(records, "A lista de registros não deve ser nula.");
        assertTrue(records.isEmpty(), "A lista de registros deve estar vazia.");
    }
}
