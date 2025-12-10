package com.compliancesys.dao;

import com.compliancesys.config.DatabaseConfig;
import com.compliancesys.dao.impl.TimeRecordDAOImpl;
import com.compliancesys.model.TimeRecord;
import com.compliancesys.model.enums.EventType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*; // Importar Mockito para 'when' e 'any'

class TimeRecordDAOTest {

    private Connection connection;
    private TimeRecordDAO timeRecordDAO;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DatabaseConfig.getInstance().getConnection();
        connection.setAutoCommit(false); // Inicia transação para rollback
        timeRecordDAO = new TimeRecordDAOImpl(connection);
        // Limpa a tabela antes de cada teste, se necessário
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM time_records");
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.rollback(); // Desfaz todas as operações do teste
        connection.close();
    }

    @Test
    void testCreateTimeRecord() throws SQLException {
        // Construtor completo do TimeRecord, ajustado para o que você provavelmente tem
        TimeRecord newRecord = new TimeRecord(0, 1, 1, 1, LocalDateTime.now(), EventType.IN, "Location A", "Notes A",
                LocalDateTime.now(), LocalDateTime.now());

        int id = timeRecordDAO.create(newRecord); // Chamada correta para 'create'

        assertTrue(id > 0);
        Optional<TimeRecord> foundRecord = timeRecordDAO.findById(id);
        assertTrue(foundRecord.isPresent());
        assertEquals(newRecord.getLocation(), foundRecord.get().getLocation());
    }

    @Test
    void testFindById() throws SQLException {
        TimeRecord newRecord = new TimeRecord(0, 1, 1, 1, LocalDateTime.now(), EventType.OUT, "Location B", "Notes B",
                LocalDateTime.now(), LocalDateTime.now());
        int id = timeRecordDAO.create(newRecord);

        Optional<TimeRecord> foundRecord = timeRecordDAO.findById(id);

        assertTrue(foundRecord.isPresent());
        assertEquals(id, foundRecord.get().getId());
    }

    @Test
    void testFindAll() throws SQLException {
        timeRecordDAO.create(new TimeRecord(0, 1, 1, 1, LocalDateTime.now().minusHours(2), EventType.IN, "Loc 1", "N1", LocalDateTime.now(), LocalDateTime.now()));
        timeRecordDAO.create(new TimeRecord(0, 1, 1, 1, LocalDateTime.now().minusHours(1), EventType.OUT, "Loc 2", "N2", LocalDateTime.now(), LocalDateTime.now()));

        List<TimeRecord> records = timeRecordDAO.findAll();

        assertFalse(records.isEmpty());
        assertEquals(2, records.size());
    }

    @Test
    void testUpdateTimeRecord() throws SQLException {
        TimeRecord record = new TimeRecord(0, 1, 1, 1, LocalDateTime.now(), EventType.IN, "Old Loc", "Old Notes",
                LocalDateTime.now(), LocalDateTime.now());
        int id = timeRecordDAO.create(record);

        record.setId(id);
        record.setLocation("New Loc");
        record.setNotes("New Notes");
        record.setUpdatedAt(LocalDateTime.now());

        boolean updated = timeRecordDAO.update(record);

        assertTrue(updated);
        Optional<TimeRecord> foundRecord = timeRecordDAO.findById(id);
        assertTrue(foundRecord.isPresent());
        assertEquals("New Loc", foundRecord.get().getLocation());
        assertEquals("New Notes", foundRecord.get().getNotes());
    }

    @Test
    void testDeleteTimeRecord() throws SQLException {
        TimeRecord record = new TimeRecord(0, 1, 1, 1, LocalDateTime.now(), EventType.IN, "Loc to Delete", "Notes to Delete",
                LocalDateTime.now(), LocalDateTime.now());
        int id = timeRecordDAO.create(record);

        boolean deleted = timeRecordDAO.delete(id);

        assertTrue(deleted);
        Optional<TimeRecord> foundRecord = timeRecordDAO.findById(id);
        assertFalse(foundRecord.isPresent());
    }

    @Test
    void testFindByDriverId() throws SQLException {
        int testDriverId = 10;
        timeRecordDAO.create(new TimeRecord(0, testDriverId, 1, 1, LocalDateTime.now().minusHours(3), EventType.IN, "Loc D1", "N D1", LocalDateTime.now(), LocalDateTime.now()));
        timeRecordDAO.create(new TimeRecord(0, testDriverId, 1, 1, LocalDateTime.now().minusHours(2), EventType.OUT, "Loc D2", "N D2", LocalDateTime.now(), LocalDateTime.now()));
        timeRecordDAO.create(new TimeRecord(0, 99, 1, 1, LocalDateTime.now().minusHours(1), EventType.IN, "Loc Other", "N Other", LocalDateTime.now(), LocalDateTime.now()));

        List<TimeRecord> records = timeRecordDAO.findByDriverId(testDriverId);

        assertFalse(records.isEmpty());
        assertEquals(2, records.size());
        assertTrue(records.stream().allMatch(r -> r.getDriverId() == testDriverId));
    }

    @Test
    void testFindByDriverIdAndDate() throws SQLException {
        int testDriverId = 10;
        LocalDate testDate = LocalDate.now();
        timeRecordDAO.create(new TimeRecord(0, testDriverId, 1, 1, testDate.atTime(8, 0), EventType.IN, "Loc D&D1", "N D&D1", LocalDateTime.now(), LocalDateTime.now()));
        timeRecordDAO.create(new TimeRecord(0, testDriverId, 1, 1, testDate.atTime(17, 0), EventType.OUT, "Loc D&D2", "N D&D2", LocalDateTime.now(), LocalDateTime.now()));
        timeRecordDAO.create(new TimeRecord(0, testDriverId, 1, 1, testDate.plusDays(1).atTime(8, 0), EventType.IN, "Loc D&D3", "N D&D3", LocalDateTime.now(), LocalDateTime.now()));

        List<TimeRecord> records = timeRecordDAO.findByDriverIdAndDate(testDriverId, testDate);

        assertFalse(records.isEmpty());
        assertEquals(2, records.size());
        assertTrue(records.stream().allMatch(r -> r.getDriverId() == testDriverId && r.getRecordTime().toLocalDate().equals(testDate)));
    }

    @Test
    void testFindByDriverIdAndRecordTimeAndEventType() throws SQLException {
        int testDriverId = 10;
        LocalDateTime testRecordTime = LocalDateTime.now().withNano(0); // Ignorar nanos para comparação
        String testEventType = EventType.IN.name();
        timeRecordDAO.create(new TimeRecord(0, testDriverId, 1, 1, testRecordTime, EventType.IN, "Loc Unique", "N Unique", LocalDateTime.now(), LocalDateTime.now()));

        Optional<TimeRecord> foundRecord = timeRecordDAO.findByDriverIdAndRecordTimeAndEventType(testDriverId, testRecordTime, testEventType);

        assertTrue(foundRecord.isPresent());
        assertEquals(testDriverId, foundRecord.get().getDriverId());
        assertEquals(testRecordTime, foundRecord.get().getRecordTime().withNano(0));
        assertEquals(testEventType, foundRecord.get().getEventType().name());
    }

    @Test
    void testFindByJourneyId() throws SQLException {
        // Este teste depende de como você associa TimeRecords a Journeys.
        // A implementação atual de findByJourneyId no TimeRecordDAOImpl usa um JOIN complexo.
        // Para simplificar o teste, vamos criar um cenário onde o JOIN funcionaria.
        // Isso pode exigir a criação de um Journey e um Driver de teste também.

        // Supondo que você tenha um JourneyDAO e DriverDAO para criar dados de teste
        // Para este teste, vamos simular a lógica do JOIN.
        int testDriverId = 1;
        int testJourneyId = 1; // ID de uma jornada existente
        LocalDate journeyDate = LocalDate.now();

        // Crie um driver e uma jornada para que o JOIN funcione
        // (Isso é um mock, na vida real você usaria os DAOs reais para criar)
        // Driver driver = new Driver(testDriverId, ...);
        // Journey journey = new Journey(testJourneyId, testDriverId, ..., journeyDate, ...);

        // Crie TimeRecords que correspondem à lógica do JOIN
        timeRecordDAO.create(new TimeRecord(0, testDriverId, 1, 1, journeyDate.atTime(8, 0), EventType.IN, "Loc J1", "N J1", LocalDateTime.now(), LocalDateTime.now()));
        timeRecordDAO.create(new TimeRecord(0, testDriverId, 1, 1, journeyDate.atTime(17, 0), EventType.OUT, "Loc J2", "N J2", LocalDateTime.now(), LocalDateTime.now()));
        timeRecordDAO.create(new TimeRecord(0, testDriverId, 1, 1, journeyDate.plusDays(1).atTime(8, 0), EventType.IN, "Loc J3", "N J3", LocalDateTime.now(), LocalDateTime.now())); // Fora da data da jornada

        // A query findByJourneyId usa o driver_id e journey_date para encontrar os time_records.
        // Para que este teste funcione, precisamos que o JourneyDAOImpl.findByJourneyId
        // realmente encontre a jornada com o journeyId e use seu driver_id e journey_date.
        // Como não temos um mock de JourneyDAO aqui, o teste é um pouco limitado.
        // Se a query no TimeRecordDAOImpl.findByJourneyId estiver correta,
        // e houver uma jornada com ID 1, driver_id 1 e journey_date = LocalDate.now(),
        // então este teste deve passar.

        // Para este exemplo, vamos assumir que a query está correta e que existe uma jornada
        // com journeyId=1, driver_id=1 e journey_date=LocalDate.now() no banco de dados de teste.
        List<TimeRecord> records = timeRecordDAO.findByJourneyId(testJourneyId);

        assertFalse(records.isEmpty());
        assertEquals(2, records.size()); // Espera-se 2 registros para a jornada com ID 1 e data atual
        assertTrue(records.stream().allMatch(r -> r.getDriverId() == testDriverId && r.getRecordTime().toLocalDate().equals(journeyDate)));
    }
}
