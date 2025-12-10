package com.compliancesys.util;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Interface para validação de dados de entrada em várias entidades do sistema.
 */
public interface Validator {

    /**
     * Valida um ID genérico.
     * @param id O ID a ser validado.
     * @return true se o ID é válido (maior que zero), false caso contrário.
     */
    boolean isValidId(int id);

    /**
     * Valida um ID de motorista.
     * @param driverId O ID do motorista a ser validado.
     * @return true se o ID do motorista é válido (maior que zero), false caso contrário.
     */
    boolean isValidDriverId(int driverId);

    /**
     * Valida um ID de empresa.
     * @param companyId O ID da empresa a ser validado.
     * @return true se o ID da empresa é válido (maior que zero), false caso contrário.
     */
    boolean isValidCompanyId(int companyId);

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
     * Valida um CNPJ.
     * @param cnpj O CNPJ a ser validado.
     * @return true se o CNPJ é válido, false caso contrário.
     */
    boolean isValidCnpj(String cnpj);

    /**
     * Valida uma placa de veículo.
     * @param plate A placa a ser validada.
     * @return true se a placa é válida, false caso contrário.
     */
    boolean isValidPlate(String plate);

    /**
     * Valida um endereço de e-mail.
     * @param email O e-mail a ser validado.
     * @return true se o e-mail é válido, false caso contrário.
     */
    boolean isValidEmail(String email);

    /**
     * Valida um número de telefone.
     * @param phoneNumber O número de telefone a ser validado.
     * @return true se o número de telefone é válido, false caso contrário.
     */
    boolean isValidPhoneNumber(String phoneNumber);

    /**
     * Valida se uma data está no passado ou é a data atual.
     * @param date A data a ser validada.
     * @return true se a data é válida (passado ou presente), false caso contrário.
     */
    boolean isPastOrPresentDate(LocalDate date);

    /**
     * Valida se um horário é válido (não nulo).
     * @param time O horário a ser validado.
     * @return true se o horário é válido, false caso contrário.
     */
    boolean isValidTime(LocalTime time);

    /**
     * Valida uma localização (string).
     * @param location A localização a ser validada.
     * @return true se a localização é válida, false caso contrário.
     */
    boolean isValidLocation(String location);

    /**
     * Valida um endereço (string).
     * @param address O endereço a ser validado.
     * @return true se o endereço é válido, false caso contrário.
     */
    boolean isValidAddress(String address);

    /**
     * Valida se um LocalDateTime é válido (não nulo e não no futuro).
     * @param dateTime O LocalDateTime a ser validado.
     * @return true se o LocalDateTime é válido, false caso contrário.
     */
    boolean isValidDateTime(LocalDateTime dateTime);

    /**
     * Valida se um período de tempo está dentro de um limite máximo.
     * @param duration O período de tempo a ser verificado.
     * @param maxDuration O limite máximo permitido.
     * @return true se a duração é menor ou igual ao limite máximo, false caso contrário.
     */
    boolean isWithinMaxDuration(Duration duration, Duration maxDuration);

    /**
     * Valida se um período de tempo está acima de um limite mínimo.
     * @param duration O período de tempo a ser verificado.
     * @param minDuration O limite mínimo permitido.
     * @return true se a duração é maior ou igual ao limite mínimo, false caso contrário.
     */
    boolean isAboveMinDuration(Duration duration, Duration minDuration);
}
