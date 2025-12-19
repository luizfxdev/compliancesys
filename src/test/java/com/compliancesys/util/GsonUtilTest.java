package com.compliancesys.util;

import java.io.StringReader;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.compliancesys.model.Driver;
import com.compliancesys.model.Journey;
import com.compliancesys.util.impl.GsonUtilImpl;
import com.google.gson.reflect.TypeToken;

class GsonUtilTest {

    private GsonUtil gsonUtil;

    @BeforeEach
    void setUp() {
        gsonUtil = new GsonUtilImpl();
    }

    // ==================== Testes de Serialização ====================

    @Test
    @DisplayName("Deve serializar objeto simples para JSON")
    void serialize_SimpleObject_ReturnsJson() {
        Driver driver = new Driver();
        driver.setId(1);
        driver.setName("João Silva");
        driver.setCpf("123.456.789-00");

        String json = gsonUtil.serialize(driver);

        assertNotNull(json);
        assertTrue(json.contains("\"id\": 1"));
        assertTrue(json.contains("\"name\": \"João Silva\""));
        assertTrue(json.contains("\"cpf\": \"123.456.789-00\""));
    }

    @Test
    @DisplayName("Deve serializar objeto com LocalDateTime")
    void serialize_ObjectWithLocalDateTime_ReturnsJson() {
        Driver driver = new Driver();
        driver.setId(1);
        driver.setCreatedAt(LocalDateTime.of(2024, 1, 15, 10, 30, 0));

        String json = gsonUtil.serialize(driver);

        assertNotNull(json);
        assertTrue(json.contains("2024-01-15T10:30:00"));
    }

    @Test
    @DisplayName("Deve serializar objeto com LocalDate")
    void serialize_ObjectWithLocalDate_ReturnsJson() {
        Journey journey = new Journey();
        journey.setId(1);
        journey.setJourneyDate(LocalDate.of(2024, 1, 15));

        String json = gsonUtil.serialize(journey);

        assertNotNull(json);
        assertTrue(json.contains("2024-01-15"));
    }

    @Test
    @DisplayName("Deve serializar lista de objetos")
    void serialize_ListOfObjects_ReturnsJsonArray() {
        Driver driver1 = new Driver();
        driver1.setId(1);
        driver1.setName("João");

        Driver driver2 = new Driver();
        driver2.setId(2);
        driver2.setName("Maria");

        List<Driver> drivers = Arrays.asList(driver1, driver2);

        String json = gsonUtil.serialize(drivers);

        assertNotNull(json);
        assertTrue(json.startsWith("["));
        assertTrue(json.endsWith("]"));
        assertTrue(json.contains("\"name\": \"João\""));
        assertTrue(json.contains("\"name\": \"Maria\""));
    }

    @Test
    @DisplayName("Deve serializar objeto nulo")
    void serialize_NullObject_ReturnsNullString() {
        String json = gsonUtil.serialize(null);
        assertEquals("null", json);
    }

    // ==================== Testes de Desserialização ====================

    @Test
    @DisplayName("Deve desserializar JSON para objeto")
    void deserialize_ValidJson_ReturnsObject() {
        String json = "{\"id\": 1, \"name\": \"João Silva\", \"cpf\": \"123.456.789-00\"}";

        Driver driver = gsonUtil.deserialize(json, Driver.class);

        assertNotNull(driver);
        assertEquals(1, driver.getId());
        assertEquals("João Silva", driver.getName());
        assertEquals("123.456.789-00", driver.getCpf());
    }

    @Test
    @DisplayName("Deve desserializar JSON com LocalDateTime")
    void deserialize_JsonWithLocalDateTime_ReturnsObject() {
        String json = "{\"id\": 1, \"createdAt\": \"2024-01-15T10:30:00\"}";

        Driver driver = gsonUtil.deserialize(json, Driver.class);

        assertNotNull(driver);
        assertEquals(LocalDateTime.of(2024, 1, 15, 10, 30, 0), driver.getCreatedAt());
    }

    @Test
    @DisplayName("Deve desserializar JSON com LocalDate")
    void deserialize_JsonWithLocalDate_ReturnsObject() {
        String json = "{\"id\": 1, \"journeyDate\": \"2024-01-15\"}";

        Journey journey = gsonUtil.deserialize(json, Journey.class);

        assertNotNull(journey);
        assertEquals(LocalDate.of(2024, 1, 15), journey.getJourneyDate());
    }

    @Test
    @DisplayName("Deve desserializar JSON array para lista")
    void deserialize_JsonArray_ReturnsList() {
        String json = "[{\"id\": 1, \"name\": \"João\"}, {\"id\": 2, \"name\": \"Maria\"}]";
        Type listType = new TypeToken<List<Driver>>(){}.getType();

        List<Driver> drivers = gsonUtil.deserialize(json, listType);

        assertNotNull(drivers);
        assertEquals(2, drivers.size());
        assertEquals("João", drivers.get(0).getName());
        assertEquals("Maria", drivers.get(1).getName());
    }

    @Test
    @DisplayName("Deve desserializar JSON de Reader")
    void deserialize_FromReader_ReturnsObject() throws Exception {
        String json = "{\"id\": 1, \"name\": \"João Silva\"}";
        StringReader reader = new StringReader(json);

        Driver driver = gsonUtil.deserialize(reader, Driver.class);

        assertNotNull(driver);
        assertEquals(1, driver.getId());
        assertEquals("João Silva", driver.getName());
    }

    @Test
    @DisplayName("Deve desserializar JSON de Reader com Type")
    void deserialize_FromReaderWithType_ReturnsList() throws Exception {
        String json = "[{\"id\": 1}, {\"id\": 2}]";
        StringReader reader = new StringReader(json);
        Type listType = new TypeToken<List<Driver>>(){}.getType();

        List<Driver> drivers = gsonUtil.deserialize(reader, listType);

        assertNotNull(drivers);
        assertEquals(2, drivers.size());
    }

    // ==================== Testes de Ida e Volta ====================

    @Test
    @DisplayName("Deve manter dados após serialização e desserialização")
    void roundTrip_SerializeAndDeserialize_MaintainsData() {
        Driver original = new Driver();
        original.setId(1);
        original.setName("João Silva");
        original.setCpf("123.456.789-00");
        original.setBirthDate(LocalDate.of(1985, 5, 15));
        original.setCreatedAt(LocalDateTime.of(2024, 1, 15, 10, 30, 0));

        String json = gsonUtil.serialize(original);
        Driver deserialized = gsonUtil.deserialize(json, Driver.class);

        assertEquals(original.getId(), deserialized.getId());
        assertEquals(original.getName(), deserialized.getName());
        assertEquals(original.getCpf(), deserialized.getCpf());
        assertEquals(original.getBirthDate(), deserialized.getBirthDate());
        assertEquals(original.getCreatedAt(), deserialized.getCreatedAt());
    }

    @Test
    @DisplayName("Deve manter lista após serialização e desserialização")
    void roundTrip_ListSerializeAndDeserialize_MaintainsData() {
        Driver driver1 = new Driver();
        driver1.setId(1);
        driver1.setName("João");

        Driver driver2 = new Driver();
        driver2.setId(2);
        driver2.setName("Maria");

        List<Driver> original = Arrays.asList(driver1, driver2);
        Type listType = new TypeToken<List<Driver>>(){}.getType();

        String json = gsonUtil.serialize(original);
        List<Driver> deserialized = gsonUtil.deserialize(json, listType);

        assertEquals(original.size(), deserialized.size());
        assertEquals(original.get(0).getName(), deserialized.get(0).getName());
        assertEquals(original.get(1).getName(), deserialized.get(1).getName());
    }
}