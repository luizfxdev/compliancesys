// src/main/java/com/compliancesys/util/Validator.java
package com.compliancesys.util;

import java.time.LocalDate;

public interface Validator {
    boolean isValidId(int id);
    boolean isValidCpf(String cpf);
    boolean isValidCnpj(String cnpj);
    boolean isValidName(String name);
    boolean isValidLicenseNumber(String licenseNumber);
    boolean isValidPlate(String plate);
    boolean isValidDate(LocalDate date);
    boolean isValidTime(String time); // Se houver necessidade de validar strings de tempo
    boolean isValidLocation(String location); // Se houver necessidade de validar strings de localização
    boolean isValidLatitude(Double latitude); // Adicionado para MobileCommunication
    boolean isValidLongitude(Double longitude); // Adicionado para MobileCommunication
    boolean isValidEmail(String email); // ADICIONADO: Método isValidEmail
    boolean isValidPhone(String phone); // ADICIONADO: Método isValidPhone
}
