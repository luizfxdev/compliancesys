// src/main/java/com/compliancesys/util/impl/GsonUtilImpl.java
package com.compliancesys.util.impl;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.compliancesys.util.GsonUtil; // Import adicionado
import com.google.gson.Gson;    // Import adicionado
import com.google.gson.GsonBuilder;         // Import adicionado
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

/**
 * Implementação de GsonUtil para serialização e desserialização de objetos Java para JSON
 * e vice-versa, com suporte a tipos do Java 8 Date and Time API (java.time).
 */
public class GsonUtilImpl implements GsonUtil {
    private final Gson gson;

    public GsonUtilImpl() {
        // Configura o Gson para lidar com LocalDateTime e LocalDate
        GsonBuilder gsonBuilder = new GsonBuilder()
                .setPrettyPrinting() // Para JSON formatado, útil para depuração
                .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) ->
                        new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) ->
                        LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (src, typeOfSrc, context) ->
                        new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE)))
                .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, typeOfT, context) ->
                        LocalDate.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE));
        this.gson = gsonBuilder.create();
    }

    @Override
    public String serialize(Object src) {
        return gson.toJson(src);
    }

    @Override
    public <T> T deserialize(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }

    @Override
    public <T> T deserialize(String json, Type typeOfT) {
        return gson.fromJson(json, typeOfT);
    }

    /**
     * Desserializa um objeto JSON a partir de um Reader.
     * @param reader O Reader contendo o JSON.
     * @param classOfT A classe do objeto a ser desserializado.
     * @param <T> O tipo do objeto.
     * @return O objeto desserializado.
     * @throws IOException Se ocorrer um erro de leitura.
     */
    public <T> T deserialize(Reader reader, Class<T> classOfT) throws IOException {
        // Gson pode ler diretamente de um Reader, não precisa de BufferedReader explicitamente
        return gson.fromJson(reader, classOfT);
    }

    /**
     * Desserializa um objeto JSON a partir de um Reader, usando um Type para tipos genéricos.
     * @param reader O Reader contendo o JSON.
     * @param typeOfT O Type do objeto a ser desserializado (útil para List<T>, Map<K,V>, etc.).
     * @param <T> O tipo do objeto.
     * @return O objeto desserializado.
     * @throws IOException Se ocorrer um erro de leitura.
     */
    public <T> T deserialize(Reader reader, Type typeOfT) throws IOException {
        return gson.fromJson(reader, typeOfT);
    }
}
