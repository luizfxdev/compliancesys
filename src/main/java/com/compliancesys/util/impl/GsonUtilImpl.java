// src/main/java/com/compliancesys/util/impl/GsonUtilImpl.java
package com.compliancesys.util.impl;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.compliancesys.util.GsonUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer; // Importar DateTimeFormatter

/**
 * Implementação da interface GsonUtil para serialização e desserialização usando Gson.
 * Configura o Gson para lidar com tipos específicos como LocalDateTime, LocalDate e Duration.
 */
public class GsonUtilImpl implements GsonUtil {

    private final Gson gson;

    public GsonUtilImpl() {
        // Define um formato padrão para LocalDateTime e LocalDate
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;

        this.gson = new GsonBuilder()
                // Adaptador para LocalDateTime
                .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) ->
                        new JsonPrimitive(src.format(dateTimeFormatter)))
                .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) ->
                        LocalDateTime.parse(json.getAsString(), dateTimeFormatter))

                // Adaptador para LocalDate
                .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (src, typeOfSrc, context) ->
                        new JsonPrimitive(src.format(dateFormatter)))
                .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, typeOfT, context) ->
                        LocalDate.parse(json.getAsString(), dateFormatter))

                // Adaptador para Duration (serializa para segundos, desserializa de segundos)
                .registerTypeAdapter(Duration.class, (JsonSerializer<Duration>) (src, typeOfSrc, context) ->
                        new JsonPrimitive(src.getSeconds()))
                .registerTypeAdapter(Duration.class, (JsonDeserializer<Duration>) (json, typeOfT, context) ->
                        Duration.ofSeconds(json.getAsLong()))
                .setPrettyPrinting() // Para formatar o JSON de saída de forma legível
                .create();
    }

    @Override
    public String serialize(Object obj) {
        return gson.toJson(obj);
    }

    @Override
    public <T> T deserialize(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }

    @Override
    public <T> T deserialize(String json, Type typeOfT) {
        return gson.fromJson(json, typeOfT);
    }

    // Adicionado o método deserialize que aceita um Reader, conforme a interface GsonUtil
    @Override
    public <T> T deserialize(Reader reader, Class<T> classOfT) throws IOException {
        // O Gson pode ler diretamente de um Reader
        return gson.fromJson(reader, classOfT);
    }

    // Adicionado o método deserialize que aceita um Reader e um Type, conforme a interface GsonUtil
    @Override
    public <T> T deserialize(Reader reader, Type typeOfT) throws IOException {
        // O Gson pode ler diretamente de um Reader
        return gson.fromJson(reader, typeOfT);
    }
}
