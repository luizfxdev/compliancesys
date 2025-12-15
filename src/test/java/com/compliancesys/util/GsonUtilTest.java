package com.compliancesys.util;

import com.compliancesys.model.Company;
import com.compliancesys.model.Driver;
import com.compliancesys.model.MobileCommunication;
import com.compliancesys.util.impl.GsonUtilImpl;
import com.google.gson.JsonParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.StringReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class GsonUtilTest {

    private GsonUtil gsonUtil;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    @BeforeEach
    void setUp() {
        gsonUtil = new GsonUtilImpl();
    }

    @Test
    @DisplayName("Deve serializar e deserializar um objeto Company corretamente")
    void testSerializeAndDeserializeCompany() {
        LocalDateTime now = LocalDateTime.of(2023, 1, 1, 10, 0, 0);
        Company originalCompany = new Company(
                1,
                "Empresa Teste",
                "12345678000190",
                "Rua Teste, 123", // Adicionado para consistência com o modelo
                "11987654321",    // Adicionado para consistência com o modelo
                "contato@empresa.com", // Adicionado para consistência com o modelo
                now,
                now
        );

        String json = gsonUtil.serialize(originalCompany);
        assertNotNull(json);
        assertTrue(json.contains("\"id\":1"));
        assertTrue(json.contains("\"name\":\"Empresa Teste\""));
        assertTrue(json.contains("\"cnpj\":\"12345678000190\""));
        assertTrue(json.contains("\"address\":\"Rua Teste, 123\""));
        assertTrue(json.contains("\"phone\":\"11987654321\""));
        assertTrue(json.contains("\"email\":\"contato@empresa.com\""));
        assertTrue(json.contains("\"createdAt\":\"" + now.format(DATE_TIME_FORMATTER) + "\""));
        assertTrue(json.contains("\"updatedAt\":\"" + now.format(DATE_TIME_FORMATTER) + "\""));

        Company deserializedCompany = gsonUtil.deserialize(json, Company.class);
        assertNotNull(deserializedCompany);
        assertEquals(originalCompany.getId(), deserializedCompany.getId());
        assertEquals(originalCompany.getName(), deserializedCompany.getName());
        assertEquals(originalCompany.getCnpj(), deserializedCompany.getCnpj());
        assertEquals(originalCompany.getAddress(), deserializedCompany.getAddress());
        assertEquals(originalCompany.getPhone(), deserializedCompany.getPhone());
        assertEquals(originalCompany.getEmail(), deserializedCompany.getEmail());
        // Comparar LocalDateTime ignorando nanossegundos, pois Gson pode truncar
        assertEquals(originalCompany.getCreatedAt().withNano(0), deserializedCompany.getCreatedAt().withNano(0));
        assertEquals(originalCompany.getUpdatedAt().withNano(0), deserializedCompany.getUpdatedAt().withNano(0));
    }

    @Test
    @DisplayName("Deve serializar e deserializar um objeto Driver corretamente")
    void testSerializeAndDeserializeDriver() {
        LocalDate cnhExpiration = LocalDate.of(2028, 12, 31);
        LocalDate birthDate = LocalDate.of(1990, 5, 15);
        LocalDateTime now = LocalDateTime.of(2023, 1, 1, 10, 0, 0);

        Driver originalDriver = new Driver(
                1, // id
                1, // companyId
                "João Silva",
                "11122233344",
                "ABC12345678",
                "D",
                cnhExpiration,
                birthDate,
                "joao@example.com", // Email
                "11987654321",      // Phone
                "Rua do Motorista, 456", // Address
                now,
                now
        );
        String json = gsonUtil.serialize(originalDriver);
        assertNotNull(json);
        assertTrue(json.contains("\"id\":1"));
        assertTrue(json.contains("\"companyId\":1"));
        assertTrue(json.contains("\"name\":\"João Silva\""));
        assertTrue(json.contains("\"cpf\":\"11122233344\""));
        assertTrue(json.contains("\"cnh\":\"ABC12345678\""));
        assertTrue(json.contains("\"cnhCategory\":\"D\""));
        assertTrue(json.contains("\"cnhExpiration\":\"" + cnhExpiration.format(DATE_FORMATTER) + "\""));
        assertTrue(json.contains("\"birthDate\":\"" + birthDate.format(DATE_FORMATTER) + "\""));
        assertTrue(json.contains("\"email\":\"joao@example.com\""));
        assertTrue(json.contains("\"phone\":\"11987654321\""));
        assertTrue(json.contains("\"address\":\"Rua do Motorista, 456\""));
        assertTrue(json.contains("\"createdAt\":\"" + now.format(DATE_TIME_FORMATTER) + "\""));
        assertTrue(json.contains("\"updatedAt\":\"" + now.format(DATE_TIME_FORMATTER) + "\""));

        Driver deserializedDriver = gsonUtil.deserialize(json, Driver.class);
        assertNotNull(deserializedDriver);
        assertEquals(originalDriver.getId(), deserializedDriver.getId());
        assertEquals(originalDriver.getCompanyId(), deserializedDriver.getCompanyId());
        assertEquals(originalDriver.getName(), deserializedDriver.getName());
        assertEquals(originalDriver.getCpf(), deserializedDriver.getCpf());
        assertEquals(originalDriver.getCnh(), deserializedDriver.getCnh());
        assertEquals(originalDriver.getCnhCategory(), deserializedDriver.getCnhCategory());
        assertEquals(originalDriver.getCnhExpiration(), deserializedDriver.getCnhExpiration());
        assertEquals(originalDriver.getBirthDate(), deserializedDriver.getBirthDate());
        assertEquals(originalDriver.getEmail(), deserializedDriver.getEmail());
        assertEquals(originalDriver.getPhone(), deserializedDriver.getPhone());
        assertEquals(originalDriver.getAddress(), deserializedDriver.getAddress());
        assertEquals(originalDriver.getCreatedAt().withNano(0), deserializedDriver.getCreatedAt().withNano(0));
        assertEquals(originalDriver.getUpdatedAt().withNano(0), deserializedDriver.getUpdatedAt().withNano(0));
    }

    @Test
    @DisplayName("Deve serializar e deserializar um objeto MobileCommunication corretamente")
    void testSerializeAndDeserializeMobileCommunication() {
        LocalDateTime startTime = LocalDateTime.of(2023, 10, 26, 14, 30, 0);
        LocalDateTime endTime = LocalDateTime.of(2023, 10, 26, 14, 35, 0);
        LocalDateTime now = LocalDateTime.of(2023, 1, 1, 10, 0, 0);

        MobileCommunication originalComm = new MobileCommunication(
                1, // id
                1, // driverId
                1, // companyId (Adicionado para consistência com o modelo)
                "CALL", // communicationType
                startTime,
                endTime,
                "11987654321", // sourceNumber
                "11998877665", // destinationNumber
                300, // durationSeconds
                "Localização A", // location
                now,
                now
        );
        String json = gsonUtil.serialize(originalComm);
        assertNotNull(json);
        assertTrue(json.contains("\"id\":1"));
        assertTrue(json.contains("\"driverId\":1"));
        assertTrue(json.contains("\"companyId\":1"));
        assertTrue(json.contains("\"communicationType\":\"CALL\""));
        assertTrue(json.contains("\"startTime\":\"" + startTime.format(DATE_TIME_FORMATTER) + "\""));
        assertTrue(json.contains("\"endTime\":\"" + endTime.format(DATE_TIME_FORMATTER) + "\""));
        assertTrue(json.contains("\"sourceNumber\":\"11987654321\""));
        assertTrue(json.contains("\"destinationNumber\":\"11998877665\""));
        assertTrue(json.contains("\"durationSeconds\":300"));
        assertTrue(json.contains("\"location\":\"Localização A\""));
        assertTrue(json.contains("\"createdAt\":\"" + now.format(DATE_TIME_FORMATTER) + "\""));
        assertTrue(json.contains("\"updatedAt\":\"" + now.format(DATE_TIME_FORMATTER) + "\""));

        MobileCommunication deserializedComm = gsonUtil.deserialize(json, MobileCommunication.class);
        assertNotNull(deserializedComm);
        assertEquals(originalComm.getId(), deserializedComm.getId());
        assertEquals(originalComm.getDriverId(), deserializedComm.getDriverId());
        assertEquals(originalComm.getCompanyId(), deserializedComm.getCompanyId());
        assertEquals(originalComm.getCommunicationType(), deserializedComm.getCommunicationType());
        assertEquals(originalComm.getStartTime().withNano(0), deserializedComm.getStartTime().withNano(0));
        assertEquals(originalComm.getEndTime().withNano(0), deserializedComm.getEndTime().withNano(0));
        assertEquals(originalComm.getSourceNumber(), deserializedComm.getSourceNumber());
        assertEquals(originalComm.getDestinationNumber(), deserializedComm.getDestinationNumber());
        assertEquals(originalComm.getDurationSeconds(), deserializedComm.getDurationSeconds());
        assertEquals(originalComm.getLocation(), deserializedComm.getLocation());
        assertEquals(originalComm.getCreatedAt().withNano(0), deserializedComm.getCreatedAt().withNano(0));
        assertEquals(originalComm.getUpdatedAt().withNano(0), deserializedComm.getUpdatedAt().withNano(0));
    }

    @Test
    @DisplayName("Deve retornar null ao deserializar uma string JSON nula ou vazia")
    void testDeserializeNullOrEmptyJson() {
        assertNull(gsonUtil.deserialize(null, Company.class));
        assertNull(gsonUtil.deserialize("", Company.class));
        assertNull(gsonUtil.deserialize("   ", Company.class));
    }

    @Test
    @DisplayName("Deve lançar JsonParseException ao deserializar uma string JSON inválida")
    void testDeserializeInvalidJson() {
        String invalidJson = "{ \"id\": 1, \"name\": \"Empresa Teste\", \"cnpj\": \"12345678000190\", \"createdAt\": \"invalid-date\" }";
        assertThrows(JsonParseException.class, () -> gsonUtil.deserialize(invalidJson, Company.class));

        String malformedJson = "{ \"id\": 1, \"name\": \"Empresa Teste\" "; // JSON incompleto
        assertThrows(JsonParseException.class, () -> gsonUtil.deserialize(malformedJson, Company.class));
    }

    @Test
    @DisplayName("Deve serializar um objeto nulo para 'null'")
    void testSerializeNullObject() {
        assertEquals("null", gsonUtil.serialize(null));
    }

    @Test
    @DisplayName("Deve deserializar de um BufferedReader corretamente")
    void testDeserializeFromBufferedReader() {
        LocalDateTime now = LocalDateTime.of(2023, 1, 1, 10, 0, 0);
        Company originalCompany = new Company(
                1, "Empresa BufferedReader", "98765432000111", "Rua Buffer, 789", "21998877665", "buffer@empresa.com", now, now
        );
        String json = gsonUtil.serialize(originalCompany);

        try (BufferedReader reader = new BufferedReader(new StringReader(json))) {
            Company deserializedCompany = gsonUtil.deserialize(reader, Company.class);
            assertNotNull(deserializedCompany);
            assertEquals(originalCompany.getId(), deserializedCompany.getId());
            assertEquals(originalCompany.getName(), deserializedCompany.getName());
            assertEquals(originalCompany.getCnpj(), deserializedCompany.getCnpj());
            assertEquals(originalCompany.getCreatedAt().withNano(0), deserializedCompany.getCreatedAt().withNano(0));
        } catch (Exception e) {
            fail("Exceção inesperada ao deserializar de BufferedReader: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Deve lançar JsonParseException ao deserializar BufferedReader com JSON inválido")
    void testDeserializeInvalidJsonFromBufferedReader() {
        String invalidJson = "{ \"id\": 1, \"name\": \"Empresa Teste\", \"createdAt\": \"invalid-date\" }";
        try (BufferedReader reader = new BufferedReader(new StringReader(invalidJson))) {
            assertThrows(JsonParseException.class, () -> gsonUtil.deserialize(reader, Company.class));
        } catch (Exception e) {
            fail("Exceção inesperada ao configurar BufferedReader para teste de JSON inválido: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Deve retornar null ao deserializar BufferedReader vazio ou nulo")
    void testDeserializeNullOrEmptyBufferedReader() {
        try (BufferedReader reader = new BufferedReader(new StringReader(""))) {
            assertNull(gsonUtil.deserialize(reader, Company.class));
        } catch (Exception e) {
            fail("Exceção inesperada ao deserializar de BufferedReader vazio: " + e.getMessage());
        }
        // Para BufferedReader nulo, o método deserialize provavelmente lançaria NullPointerException
        // se não houver tratamento interno, mas o teste aqui é para StringReader vazio.
    }
}
