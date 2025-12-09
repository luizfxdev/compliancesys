package com.compliancesys.util;

import com.compliancesys.model.Company;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GsonUtilTest {

    // Implementação concreta simples da interface GsonUtil para fins de teste
    private static class GsonUtilImpl implements GsonUtil {
        private final Gson gson;

        public GsonUtilImpl() {
            this.gson = new GsonBuilder().setPrettyPrinting().create();
        }

        @Override
        public <T> String serialize(T object) {
            return gson.toJson(object);
        }

        @Override
        public <T> T deserialize(String json, Class<T> type) {
            return gson.fromJson(json, type);
        }
    }

    private GsonUtil gsonUtil;

    @BeforeEach
    void setUp() {
        // Inicializa a implementação concreta do GsonUtil antes de cada teste
        gsonUtil = new GsonUtilImpl();
    }

    @Test
    void testSerializeObjectToJson() {
        Company company = new Company(1, "Empresa Teste", "12.345.678/0001-90", "Rua A, 123", "Cidade X", "SP", "12345-678", "empresa@teste.com");
        String json = gsonUtil.serialize(company);

        assertNotNull(json);
        assertTrue(json.contains("\"id\": 1"));
        assertTrue(json.contains("\"name\": \"Empresa Teste\""));
        assertTrue(json.contains("\"cnpj\": \"12.345.678/0001-90\""));
        assertTrue(json.contains("\"address\": \"Rua A, 123\""));
        assertTrue(json.contains("\"city\": \"Cidade X\""));
        assertTrue(json.contains("\"state\": \"SP\""));
        assertTrue(json.contains("\"zipCode\": \"12345-678\""));
        assertTrue(json.contains("\"email\": \"empresa@teste.com\""));
    }

    @Test
    void testDeserializeJsonToObject() {
        String json = "{\"id\": 2, \"name\": \"Outra Empresa\", \"cnpj\": \"98.765.432/0001-10\", \"address\": \"Av. B, 456\", \"city\": \"Cidade Y\", \"state\": \"RJ\", \"zipCode\": \"98765-432\", \"email\": \"outra@empresa.com\"}";
        Company company = gsonUtil.deserialize(json, Company.class);

        assertNotNull(company);
        assertEquals(2, company.getId());
        assertEquals("Outra Empresa", company.getName());
        assertEquals("98.765.432/0001-10", company.getCnpj());
        assertEquals("Av. B, 456", company.getAddress());
        assertEquals("Cidade Y", company.getCity());
        assertEquals("RJ", company.getState());
        assertEquals("98765-432", company.getZipCode());
        assertEquals("outra@empresa.com", company.getEmail());
    }

    @Test
    void testSerializeNullObject() {
        String json = gsonUtil.serialize(null);
        assertEquals("null", json);
    }

    @Test
    void testDeserializeNullJson() {
        Company company = gsonUtil.deserialize(null, Company.class);
        assertNull(company);
    }

    @Test
    void testDeserializeEmptyJson() {
        Company company = gsonUtil.deserialize("{}", Company.class);
        assertNotNull(company);
        // Dependendo do construtor padrão do Company, os campos podem ser nulos ou valores padrão
        assertEquals(0, company.getId()); // Assumindo 0 para int padrão
        assertNull(company.getName());
        // ... e assim por diante para outros campos
    }

    @Test
    void testDeserializeInvalidJson() {
        String invalidJson = "{ \"id\": 1, \"name\": \"Empresa Teste\", \"cnpj\": \"invalid-cnpj\" "; // JSON malformado
        // Gson geralmente lança JsonSyntaxException para JSON malformado.
        // Aqui, estamos testando a interface, então esperamos que a implementação subjacente (Gson) lide com isso.
        // Para um teste mais robusto, poderíamos esperar uma exceção específica do Gson.
        assertThrows(com.google.gson.JsonSyntaxException.class, () -> {
            gsonUtil.deserialize(invalidJson, Company.class);
        });
    }

    @Test
    void testSerializeAndDeserializeRoundtrip() {
        Company originalCompany = new Company(3, "Empresa Original", "33.333.333/0001-33", "Rua C, 789", "Cidade Z", "PR", "33333-333", "original@empresa.com");
        String json = gsonUtil.serialize(originalCompany);
        Company deserializedCompany = gsonUtil.deserialize(json, Company.class);

        assertNotNull(deserializedCompany);
        assertEquals(originalCompany.getId(), deserializedCompany.getId());
        assertEquals(originalCompany.getName(), deserializedCompany.getName());
        assertEquals(originalCompany.getCnpj(), deserializedCompany.getCnpj());
        assertEquals(originalCompany.getAddress(), deserializedCompany.getAddress());
        assertEquals(originalCompany.getCity(), deserializedCompany.getCity());
        assertEquals(originalCompany.getState(), deserializedCompany.getState());
        assertEquals(originalCompany.getZipCode(), deserializedCompany.getZipCode());
        assertEquals(originalCompany.getEmail(), deserializedCompany.getEmail());
    }

    // Exemplo de um POJO simples para testar serialização/desserialização
    private static class SimplePojo {
        private String name;
        private int value;

        public SimplePojo(String name, int value) {
            this.name = name;
            this.value = value;
        }

        // Getters e Setters (ou Lombok)
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getValue() { return value; }
        public void setValue(int value) { this.value = value; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SimplePojo that = (SimplePojo) o;
            return value == that.value &&
                   java.util.Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(name, value);
        }
    }

    @Test
    void testSerializeSimplePojo() {
        SimplePojo pojo = new SimplePojo("Test Pojo", 123);
        String json = gsonUtil.serialize(pojo);

        assertNotNull(json);
        assertTrue(json.contains("\"name\": \"Test Pojo\""));
        assertTrue(json.contains("\"value\": 123"));
    }

    @Test
    void testDeserializeSimplePojo() {
        String json = "{\"name\":\"Another Pojo\",\"value\":456}";
        SimplePojo pojo = gsonUtil.deserialize(json, SimplePojo.class);

        assertNotNull(pojo);
        assertEquals("Another Pojo", pojo.getName());
        assertEquals(456, pojo.getValue());
    }
}
