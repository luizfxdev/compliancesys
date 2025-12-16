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
    boolean isValidTime(String time); 
    boolean isValidLocation(String location); 
    boolean isValidLatitude(Double latitude); 
    boolean isValidLongitude(Double longitude); 
    boolean isValidEmail(String email); 
    boolean isValidPhone(String phone); 
}
