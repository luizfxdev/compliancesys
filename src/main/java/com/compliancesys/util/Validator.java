package com.compliancesys.util;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Interface para validação de dados de entrada em várias entidades do sistema.
 */
public interface Validator {

    /**
     * Valida um nome.
     * @param name O nome a ser validado.
     * @return true se o nome é válido, false caso contrário.
     */
    boolean isValidName(String name);

    /**
     * Valida um CPF.
     * @param cpf O CPF a ser validado.
     * @return true se o CPF é válido, false caso contrário.
     */
    boolean isValidCpf(String cpf);

    /**
     * Valida um número de licença (CNH).
     * @param licenseNumber O número da licença a ser validado.
     * @return true se o número da licença é válido, false caso contrário.
     */
    boolean isValidLicenseNumber(String licenseNumber);

    /**
     * Valida um endereço de e-mail.
     * @param email O e-mail a ser validado.
     * @return true se o e-mail é válido, false caso contrário.
     */
    boolean isValidEmail(String email);

    /**
     * Valida uma senha.
     * @param password A senha a ser validada.
     * @return true se a senha é válida, false caso contrário.
     */
    boolean isValidPassword(String password);

    /**
     * Valida um CNPJ.
     * @param cnpj O CNPJ a ser validado.
     * @return true se o CNPJ é válido, false caso contrário.
     */
    boolean isValidCnpj(String cnpj); // ADICIONADO: Método para validar CNPJ

    /**
     * Valida uma placa de veículo.
     * @param plate A placa a ser validada.
     * @return true se a placa é válida, false caso contrário.
     */
    boolean isValidPlate(String plate); // ADICIONADO: Método para validar placa

    /**
     * Valida se uma data está no passado ou é a data atual.
     * @param date A data a ser validada.
     * @return true se a data é válida, false caso contrário.
     */
    boolean isPastOrPresentDate(LocalDate date);

    /**
     * Valida se um horário é válido (não nulo).
     * @param time O horário a ser validado.
     * @return true se o horário é válido, false caso contrário.
     */
    boolean isValidTime(LocalTime time);

    /**
     * Valida uma localização (não nula e não vazia).
     * @param location A localização a ser validada.
     * @return true se a localização é válida, false caso contrário.
     */
    boolean isValidLocation(String location);
}
