package com.compliancesys.util;

import com.compliancesys.model.Company;
import com.compliancesys.model.Driver;
import com.compliancesys.model.MobileCommunication;
import com.compliancesys.util.impl.GsonUtilImpl;
import com.google.gson.JsonParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.StringReader;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class GsonUtilTest {

    private GsonUtil gsonUtil;

    @BeforeEach
    void setUp() {
        gsonUtil = new GsonUtilImpl();
    }

    @Test
    void testSerializeAndDeserializeCompany() {
        Company originalCompany = new Company(
                1,
                "Empresa Teste",
                "12345678000190",
                "contato@empresa.com",
                "11987654321",
                "Rua Teste, 123",
                LocalDateTime.of(2023, 1, 1, 10, 0, 0),
                LocalDateTime.of(2023, 1, 1, 10, 0, 0)
        );

        String json = gsonUtil.serialize(originalCompany);
        assertNotNull(json);
        assertTrue(json.contains("\"name\":\"Empresa Teste\""));
        assertTrue(json.contains("\"cnpj\":\"12345678000190\""));

        Company deserializedCompany = gsonUtil.deserialize(json, Company.class);
        assertNotNull(deserializedCompany);
        assertEquals(originalCompany.getId(), deserializedCompany.getId());
        assertEquals(originalCompany.getName(), deserializedCompany.getName());
        assertEquals(originalCompany.getCnpj(), deserializedCompany.getCnpj());
        assertEquals(originalCompany.getEmail(), deserializedCompany.getEmail());
        assertEquals(originalCompany.getPhone(), deserializedCompany.getPhone());
        assertEquals(originalCompany.getAddress(), deserializedCompany.getAddress());
        // Comparar LocalDateTime ignorando nanossegundos, pois Gson pode truncar
        assertEquals(originalCompany.getCreatedAt().withNano(0), deserializedCompany.getCreatedAt().withNano(0));
        assertEquals(originalCompany.getUpdatedAt().withNano(0), deserializedCompany.getUpdatedAt().withNano(0));
    }

    @Test
    void testSerializeAndDeserializeDriver() {
        Driver originalDriver = new Driver(
                1, // id
                1, // companyId
                "João Silva",
                "11122233344",
                "ABC12345678",
                "D",
                LocalDate.of(2028, 12, 31), // licenseExpiration
                LocalDate.of(1990, 5, 15), // birthDate
                "11987654321",
                "joao@example.com",
                LocalDateTime.of(2023, 1, 1, 10, 0, 0),
                LocalDateTime.of(2023, 1, 1, 10, 0, 0)
        );

        String json = gsonUtil.serialize(originalDriver);
        assertNotNull(json);
        assertTrue(json.contains("\"name\":\"João Silva\""));
        assertTrue(json.contains("\"cpf\":\"11122233344\""));

        Driver deserializedDriver = gsonUtil.deserialize(json, Driver.class);
        assertNotNull(deserializedDriver);
        assertEquals(originalDriver.getId(), deserializedDriver.getId());
        assertEquals(originalDriver.getName(), deserializedDriver.getName());
        assertEquals(originalDriver.getCpf(), deserializedDriver.getCpf());
        assertEquals(originalDriver.getLicenseNumber(), deserializedDriver.getLicenseNumber());
        assertEquals(originalDriver.getLicenseCategory(), deserializedDriver.getLicenseCategory());
        assertEquals(originalDriver.getLicenseExpiration(), deserializedDriver.getLicenseExpiration()); // Usar getLicenseExpiration()
        assertEquals(originalDriver.getBirthDate(), deserializedDriver.getBirthDate());
        assertEquals(originalDriver.getPhone(), deserializedDriver.getPhone());
        assertEquals(originalDriver.getEmail(), deserializedDriver.getEmail());
        assertEquals(originalDriver.getCreatedAt().withNano(0), deserializedDriver.getCreatedAt().withNano(0));
        assertEquals(originalDriver.getUpdatedAt().withNano(0), deserializedDriver.getUpdatedAt().withNano(0));
    }

    @Test
    void testSerializeAndDeserializeMobileCommunication() {
        MobileCommunication originalComm = new MobileCommunication(
                1, // id
                1, // driverId
                101, // recordId
                LocalDateTime.of(2023, 10, 26, 14, 30, 0), // timestamp
                -23.5505, // latitude
                -46.6333, // longitude
                LocalDateTime.of(2023, 10, 26, 14, 30, 15), // sendTimestamp
                true, // sendSuccess
                null, // errorMessage
                LocalDateTime.of(2023, 10, 26, 14, 30, 0),
                LocalDateTime.of(2023, 10, 26, 14, 30, 0)
        );

        String json = gsonUtil.serialize(originalComm);
        assertNotNull(json);
        assertTrue(json.contains("\"driverId\":1"));
        assertTrue(json.contains("\"latitude\":-23.5505"));

        MobileCommunication deserializedComm = gsonUtil.deserialize(json, MobileCommunication.class);
        assertNotNull(deserializedComm);
        assertEquals(originalComm.getId(), deserializedComm.getId());
        assertEquals(originalComm.getDriverId(), deserializedComm.getDriverId());
        assertEquals(originalComm.getRecordId(), deserializedComm.getRecordId());
        assertEquals(originalComm.getTimestamp().withNano(0), deserializedComm.getTimestamp().withNano(0));
        assertEquals(originalComm.getLatitude(), deserializedComm.getLatitude());
        assertEquals(originalComm.getLongitude(), deserializedComm.getLongitude());
        assertEquals(originalComm.getSendTimestamp().withNano(0), deserializedComm.getSendTimestamp().withNano(0));
        assertEquals(originalComm.isSendSuccess(), deserializedComm.isSendSuccess());
        assertEquals(originalComm.getErrorMessage(), deserializedComm.getErrorMessage());
        assertEquals(originalComm.getCreatedAt().withNano(0), deserializedComm.getCreatedAt().withNano(0));
        assertEquals(originalComm.getUpdatedAt().withNano(0), deserializedComm.getUpdatedAt().withNano(0));
    }

    @Test
    void testSerializeAndDeserializeLocalDateTime() {
        LocalDateTime originalDateTime = LocalDateTime.of(2023, 1, 2, 15, 30, 45, 123456789);
        String json = gsonUtil.serialize(originalDateTime);
        assertNotNull(json);
        assertTrue(json.contains("2023-01-02T15:30:45.123456789"));

        LocalDateTime deserializedDateTime = gsonUtil.deserialize(json, LocalDateTime.class);
        assertNotNull(deserializedDateTime);
        assertEquals(originalDateTime, deserializedDateTime);
    }

    @Test
    void testSerializeAndDeserializeLocalDate() {
        LocalDate originalDate = LocalDate.of(2023, 1, 2);
        String json = gsonUtil.serialize(originalDate);
        assertNotNull(json);
        assertTrue(json.contains("2023-01-02"));

        LocalDate deserializedDate = gsonUtil.deserialize(json, LocalDate.class);
        assertNotNull(deserializedDate);
        assertEquals(originalDate, deserializedDate);
    }

    @Test
    void testSerializeAndDeserializeDuration() {
        Duration originalDuration = Duration.ofHours(1).plusMinutes(30).plusSeconds(15);
        String json = gsonUtil.serialize(originalDuration);
        assertNotNull(json);
        assertTrue(json.contains("PT1H30M15S"));

        Duration deserializedDuration = gsonUtil.deserialize(json, Duration.class);
        assertNotNull(deserializedDuration);
        assertEquals(originalDuration, deserializedDuration);
    }

    @Test
    void testDeserializeInvalidJson() {
        String invalidJson = "{ \"id\": \"abc\" }";
        assertThrows(JsonParseException.class, () -> {
            gsonUtil.deserialize(invalidJson, Company.class);
        });
    }

    @Test
    void testDeserializeNullString() {
        Company deserializedCompany = gsonUtil.deserialize((String) null, Company.class);
        assertNull(deserializedCompany);
    }

    @Test
    void testDeserializeEmptyString() {
        Company deserializedCompany = gsonUtil.deserialize("", Company.class);
        assertNull(deserializedCompany);
    }

    @Test
    void testDeserializeWithBufferedReader() throws Exception {
        String json = "{\"id\":1,\"name\":\"Empresa Buffer\",\"cnpj\":\"11222333000144\",\"email\":\"buffer@test.com\",\"phone\":\"11900000000\",\"address\":\"Rua Buffer, 456\",\"createdAt\":\"2023-01-01T10:00:00\",\"updatedAt\":\"2023-01-01T10:00:00\"}";
        BufferedReader reader = new BufferedReader(new StringReader(json));

        Company deserializedCompany = gsonUtil.deserialize(reader, Company.class);
        assertNotNull(deserializedCompany);
        assertEquals(1, deserializedCompany.getId());
        assertEquals("Empresa Buffer", deserializedCompany.getName());
    }

    @Test
    void testDeserializeWithBufferedReaderNull() {
        assertThrows(NullPointerException.class, () -> { // BufferedReader nulo pode lançar NPE ou IllegalArgumentException dependendo da implementação
            gsonUtil.deserialize((BufferedReader) null, Company.class);
        });
    }
}
