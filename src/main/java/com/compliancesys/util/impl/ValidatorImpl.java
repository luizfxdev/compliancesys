package com.compliancesys.util.impl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.regex.Pattern;

import com.compliancesys.util.Validator;

public class ValidatorImpl implements Validator {

    // Regex para validar CPF (formato XXX.XXX.XXX-XX)
    private static final Pattern CPF_PATTERN = Pattern.compile("^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$");
    // Regex para validar CNPJ (formato XX.XXX.XXX/XXXX-XX)
    private static final Pattern CNPJ_PATTERN = Pattern.compile("^\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}$");
    // Regex para validar placa de veículo (formato AAA-0000 ou AAA0A00)
    private static final Pattern PLATE_PATTERN = Pattern.compile("^[A-Z]{3}[0-9]{4}$|^[A-Z]{3}[0-9][A-Z][0-9]{2}$"); // Padrão Mercosul e antigo
    // Regex para validar e-mail
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");
    // Regex para validar senha (mínimo 8 caracteres, pelo menos uma letra maiúscula, uma minúscula, um número e um caractere especial)
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");


    @Override
    public boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty() && name.trim().length() >= 2;
    }

    @Override
    public boolean isValidCpf(String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) {
            return false;
        }
        // Remove caracteres não numéricos para validação mais robusta, se necessário
        // String cleanCpf = cpf.replaceAll("[^0-9]", "");
        // return CPF_PATTERN.matcher(cleanCpf).matches() && isValidCpfLogic(cleanCpf); // Implementar lógica de validação de dígitos
        return CPF_PATTERN.matcher(cpf).matches(); // Apenas valida o formato por enquanto
    }

    @Override
    public boolean isValidLicenseNumber(String licenseNumber) {
        return licenseNumber != null && !licenseNumber.trim().isEmpty() && licenseNumber.trim().length() >= 5; // Exemplo: CNH tem 11 dígitos
    }

    @Override
    public boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    @Override
    public boolean isValidPassword(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }

    @Override
    public boolean isValidCnpj(String cnpj) { // ADICIONADO: Implementação para validar CNPJ
        if (cnpj == null || cnpj.trim().isEmpty()) {
            return false;
        }
        return CNPJ_PATTERN.matcher(cnpj).matches(); // Apenas valida o formato por enquanto
    }

    @Override
    public boolean isValidPlate(String plate) { // ADICIONADO: Implementação para validar placa
        if (plate == null || plate.trim().isEmpty()) {
            return false;
        }
        return PLATE_PATTERN.matcher(plate.toUpperCase()).matches(); // Valida o formato (considera maiúsculas)
    }

    @Override
    public boolean isPastOrPresentDate(LocalDate date) {
        return date != null && (date.isBefore(LocalDate.now()) || date.isEqual(LocalDate.now()));
    }

    @Override
    public boolean isValidTime(LocalTime time) {
        return time != null;
    }

    @Override
    public boolean isValidLocation(String location) {
        return location != null && !location.trim().isEmpty();
    }
}
