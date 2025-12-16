package com.compliancesys.util.impl;

import java.time.LocalDate;
import java.util.regex.Pattern;

import com.compliancesys.util.Validator;

public class ValidatorImpl implements Validator {

    // Regex para CPF (XXX.XXX.XXX-XX)
    private static final Pattern CPF_PATTERN = Pattern.compile("^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$");
    // Regex para CNPJ (XX.XXX.XXX/XXXX-XX)
    private static final Pattern CNPJ_PATTERN = Pattern.compile("^\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}$");
    // Regex para Placa (AAA-0000 ou AAA0A00)
    private static final Pattern PLATE_PATTERN = Pattern.compile("^[A-Z]{3}[0-9]{4}$|^[A-Z]{3}[0-9][A-Z][0-9]{2}$");
    // Regex para Email
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");
    // Regex para Telefone (formato simples, pode ser mais complexo dependendo da necessidade)
    private static final Pattern PHONE_PATTERN =
        Pattern.compile("^\\(?\\d{2}\\)?\\s?\\d{4,5}-?\\d{4}$");


    @Override
    public boolean isValidId(int id) {
        return id > 0;
    }

    @Override
    public boolean isValidCpf(String cpf) {
        if (cpf == null || cpf.isEmpty()) {
            return false;
        }
        return CPF_PATTERN.matcher(cpf).matches();
        // Implementação mais robusta de validação de CPF (dígitos verificadores) pode ser adicionada aqui.
    }

    @Override
    public boolean isValidCnpj(String cnpj) {
        if (cnpj == null || cnpj.isEmpty()) {
            return false;
        }
        return CNPJ_PATTERN.matcher(cnpj).matches();
        // Implementação mais robusta de validação de CNPJ (dígitos verificadores) pode ser adicionada aqui.
    }

    @Override
    public boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty() && name.length() >= 3;
    }

    @Override
    public boolean isValidLicenseNumber(String licenseNumber) {
        return licenseNumber != null && !licenseNumber.trim().isEmpty();
    }

    @Override
    public boolean isValidPlate(String plate) {
        if (plate == null || plate.isEmpty()) {
            return false;
        }
        return PLATE_PATTERN.matcher(plate).matches();
    }

    @Override
    public boolean isValidDate(LocalDate date) {
        return date != null && !date.isAfter(LocalDate.now()); // Exemplo: data não pode ser futura
    }

    @Override
    public boolean isValidTime(String time) {
        // Implementar validação de formato de tempo se necessário (ex: HH:mm:ss)
        return time != null && !time.trim().isEmpty(); // Validação básica
    }

    @Override
    public boolean isValidLocation(String location) {
        return location != null && !location.trim().isEmpty();
    }

    @Override
    public boolean isValidLatitude(Double latitude) {
        return latitude != null && latitude >= -90.0 && latitude <= 90.0;
    }

    @Override
    public boolean isValidLongitude(Double longitude) {
        return longitude != null && longitude >= -180.0 && longitude <= 180.0;
    }

    @Override
    public boolean isValidEmail(String email) { // ADICIONADO: Implementação do isValidEmail
        if (email == null || email.isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    @Override
    public boolean isValidPhone(String phone) { // ADICIONADO: Implementação do isValidPhone
        if (phone == null || phone.isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone).matches();
    }
}
