package com.compliancesys.util;

import java.lang.reflect.Type;

/**
 * Interface para utilitários de serialização e desserialização usando Gson.
 * Centraliza a configuração e o uso do Gson para garantir consistência.
 */
public interface GsonUtil {

    /**
     * Serializa um objeto para uma string JSON.
     *
     * @param src O objeto a ser serializado.
     * @return Uma string JSON representando o objeto.
     */
    String serialize(Object src);

    /**
     * Desserializa uma string JSON para um objeto do tipo especificado.
     *
     * @param json A string JSON a ser desserializada.
     * @param classOfT A classe do objeto para o qual o JSON deve ser desserializado.
     * @param <T> O tipo do objeto.
     * @return Uma instância do tipo T a partir do JSON.
     */
    <T> T deserialize(String json, Class<T> classOfT);

    /**
     * Desserializa uma string JSON para um objeto de um tipo genérico especificado.
     * Útil para tipos complexos como List<MyObject>.
     *
     * @param json A string JSON a ser desserializada.
     * @param typeOfT O tipo genérico do objeto para o qual o JSON deve ser desserializado.
     * @param <T> O tipo do objeto.
     * @return Uma instância do tipo T a partir do JSON.
     */
    <T> T deserialize(String json, Type typeOfT);
}
