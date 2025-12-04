package com.compliancesys.util;

/**
 * Interface para um serviço de serialização e desserialização usando Gson.
 * Define métodos genéricos para converter objetos Java para JSON e JSON para objetos Java.
 */
public interface GsonUtil {

    /**
     * Serializa um objeto Java para uma string JSON.
     * @param <T> O tipo do objeto a ser serializado.
     * @param object O objeto a ser serializado.
     * @return Uma string JSON representando o objeto.
     */
    <T> String serialize(T object);

    /**
     * Desserializa uma string JSON para um objeto Java do tipo especificado.
     * @param <T> O tipo do objeto para o qual a string JSON será desserializada.
     * @param json A string JSON a ser desserializada.
     * @param type A classe do tipo para o qual a string JSON será desserializada.
     * @return Um objeto Java do tipo especificado.
     */
    <T> T deserialize(String json, Class<T> type);
}
