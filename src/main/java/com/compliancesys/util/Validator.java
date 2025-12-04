package com.compliancesys.util;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Interface para um serviço de validação genérico.
 * Define métodos para validar diferentes tipos de dados.
 */
public interface Validator {

    /**
     * Valida se uma string não é nula e não está vazia.
     * @param value A string a ser validada.
     * @return true se a string é válida, false caso contrário.
     */
    boolean isValidString(String value);

    /**
     * Valida se um número inteiro é positivo.
     * @param value O número inteiro a ser validado.
     * @return true se o número é positivo, false caso contrário.
     */
    boolean isPositive(int value);

    /**
     * Valida se um CPF é válido (formato e dígitos verificadores).
     * @param cpf O CPF a ser validado.
     * @return true se o CPF é válido, false caso contrário.
     */
    boolean isValidCpf(String cpf);

    /**
     * Valida se uma data não é nula e não está no futuro.
     * @param date A data a ser validada.
     * @return true se a data é válida, false caso contrário.
     */
    boolean isValidDate(LocalDate date);

    /**
     * Valida se um horário não é nulo.
     * @param time O horário a ser validado.
     * @return true se o horário é válido, false caso contrário.
     */
    boolean isValidTime(LocalTime time);

    /**
     * Valida se um objeto não é nulo.
     * @param object O objeto a ser validado.
     * @return true se o objeto não é nulo, false caso contrário.
     */
    boolean isNotNull(Object object);

    /**
     * Valida se um nome é válido (não nulo, não vazio e com caracteres alfabéticos/espaços).
     * @param name O nome a ser validado.
     * @return true se o nome é válido, false caso contrário.
     */
    boolean isValidName(String name); // NOVO MÉTODO

    /**
     * Valida se um número de licença (CNH) é válido (não nulo, não vazio e alfanumérico).
     * @param licenseNumber O número da CNH a ser validado.
     * @return true se o número da CNH é válido, false caso contrário.
     */
    boolean isValidLicenseNumber(String licenseNumber); // NOVO MÉTODO

    /**
     * Valida se uma localização (cidade, endereço) é válida (não nula, não vazia).
     * Pode ser expandido com regras mais específicas se necessário.
     * @param location A localização a ser validada.
     * @return true se a localização é válida, false caso contrário.
     */
    boolean isValidLocation(String location); // NOVO MÉTODO
}
