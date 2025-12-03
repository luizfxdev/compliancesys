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
}
