// src/main/java/com/compliancesys/util/GsonUtil.java
package com.compliancesys.util;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;

/**
 * Interface para utilitários de serialização e desserialização usando Gson.
 * Centraliza a configuração e o uso do Gson para garantir consistência.
 */
public interface GsonUtil {

    /**
     * Serializa um objeto para uma string JSON.
     * @param <T> O tipo do objeto.
     * @param src O objeto a ser serializado.
     * @return A representação JSON do objeto.
     */
    public <T> String serialize(T src);

    /**
     * Desserializa uma string JSON para um objeto do tipo especificado.
     * @param <T> O tipo do objeto.
     * @param json A string JSON a ser desserializada.
     * @param classOfT A classe do objeto.
     * @return O objeto desserializado.
     */
    public <T> T deserialize(String json, Class<T> classOfT);

    /**
     * Desserializa uma string JSON para um objeto de um tipo complexo (ex: List<MyObject>).
     * @param <T> O tipo do objeto.
     * @param json A string JSON a ser desserializada.
     * @param typeOfT O tipo complexo do objeto (ex: new TypeToken<List<MyObject>>(){}.getType()).
     * @return O objeto desserializado.
     */
    public <T> T deserialize(String json, Type typeOfT);

    /**
     * Desserializa um Reader (ex: request.getReader()) para um objeto do tipo especificado.
     * @param <T> O tipo do objeto.
     * @param reader O Reader contendo a string JSON.
     * @param classOfT A classe do objeto.
     * @return O objeto desserializado.
     * @throws IOException Se ocorrer um erro de leitura.
     */
    public <T> T deserialize(Reader reader, Class<T> classOfT) throws IOException; // Adicionado

    /**
     * Desserializa um Reader (ex: request.getReader()) para um objeto de um tipo complexo.
     * @param <T> O tipo do objeto.
     * @param reader O Reader contendo a string JSON.
     * @param typeOfT O tipo complexo do objeto.
     * @return O objeto desserializado.
     * @throws IOException Se ocorrer um erro de leitura.
     */
    public <T> T deserialize(Reader reader, Type typeOfT) throws IOException; // Adicionado
}
