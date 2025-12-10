package com.compliancesys.util.impl;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.regex.Pattern;

import com.compliancesys.util.Validator;

public class ValidatorImpl implements Validator {

    // Regex para CPF: XXX.XXX.XXX-XX
    private static final String CPF_REGEX = "^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$";
    // Regex para CNPJ: XX.XXX.XXX/XXXX-XX
    private static final String CNPJ_REGEX = "^\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}$";
    // Regex para Placa (Mercosul ou Antiga): AAA0A00 ou AAA0000
    private static final String PLATE_REGEX = "^[A-Z]{3}\\d[A-Z0-9]\\d{2}$|^[A-Z]{3}\\d{4}$";
    // Regex para nomes: apenas letras, espaços, acentos e apóstrofos
    private static final String NAME_REGEX = "^[\\p{L} .'-]+$"; // \\p{L} para qualquer letra Unicode
    // Regex para localização/endereço: permite letras, números, espaços e alguns caracteres especiais comuns
    private static final String LOCATION_ADDRESS_REGEX = "^[\\p{L}0-9 .,\\-/#&()_+|$;':\"\\\\|<>/?]*$"; // Ajustado para incluir mais caracteres comuns em endereços
    // Regex para e-mail: padrão mais comum e robusto
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
    // Regex para número de telefone (Brasil): aceita formatos comuns com ou sem parênteses, espaços e hífen
    // Exemplos: (XX) XXXXX-XXXX, XX XXXXX-XXXX, XXXXX-XXXX, XXXXXXXXXXX, XXXXXXXXX
   private static final String PHONE_NUMBER_REGEX = "^\\d{8}$";




    private static final Pattern CPF_PATTERN = Pattern.compile(CPF_REGEX);
    private static final Pattern CNPJ_PATTERN = Pattern.compile(CNPJ_REGEX);
    private static final Pattern PLATE_PATTERN = Pattern.compile(PLATE_REGEX);
    private static final Pattern NAME_PATTERN = Pattern.compile(NAME_REGEX);
    private static final Pattern LOCATION_ADDRESS_PATTERN = Pattern.compile(LOCATION_ADDRESS_REGEX);
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile(PHONE_NUMBER_REGEX);

    @Override
    public boolean isValidId(int id) {
        return id > 0;
    }

    @Override
    public boolean isValidDriverId(int driverId) {
        return driverId > 0;
    }

    @Override
    public boolean isValidCompanyId(int companyId) {
        return companyId > 0;
    }

    @Override
    public boolean isValidName(String name) {
        return name != null && NAME_PATTERN.matcher(name).matches();
    }

    @Override
    public boolean isValidCpf(String cpf) {
        return cpf != null && CPF_PATTERN.matcher(cpf).matches();
    }

    @Override
    public boolean isValidLicenseNumber(String licenseNumber) {
        // Assumindo que licenseNumber é um CPF ou outro formato específico.
        // Por enquanto, uma validação básica. Pode ser ajustado conforme a regra de negócio.
        return licenseNumber != null && !licenseNumber.trim().isEmpty();
    }

    @Override
    public boolean isValidCnpj(String cnpj) {
        return cnpj != null && CNPJ_PATTERN.matcher(cnpj).matches();
    }

    @Override
    public boolean isValidPlate(String plate) {
        return plate != null && PLATE_PATTERN.matcher(plate).matches();
    }

    @Override
    public boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    @Override
    public boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber != null && PHONE_NUMBER_PATTERN.matcher(phoneNumber).matches();
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
        return location != null && !location.trim().isEmpty() && location.length() >= 3
                && LOCATION_ADDRESS_PATTERN.matcher(location).matches();
    }

    @Override
    public boolean isValidAddress(String address) {
        return address != null && !address.trim().isEmpty() && address.length() >= 5
                && LOCATION_ADDRESS_PATTERN.matcher(address).matches(); // Adicionado validação de regex para consistência
    }

    @Override
    public boolean isValidDateTime(LocalDateTime dateTime) {
        return dateTime != null && !dateTime.isAfter(LocalDateTime.now());
    }

    @Override
    public boolean isWithinMaxDuration(Duration duration, Duration maxDuration) {
        return duration != null && maxDuration != null && !duration.isNegative() && !maxDuration.isNegative() && (duration.compareTo(maxDuration) <= 0);
    }

    @Override
    public boolean isAboveMinDuration(Duration duration, Duration minDuration) {
        return duration != null && minDuration != null && !duration.isNegative() && !minDuration.isNegative() && (duration.compareTo(minDuration) >= 0);
    }
}
