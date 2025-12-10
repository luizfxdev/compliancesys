package com.compliancesys.util;

import com.compliancesys.util.impl.ValidatorImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class ValidatorTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = new ValidatorImpl();
    }

    @Test
    void testIsValidId() {
        assertTrue(validator.isValidId(1));
        assertFalse(validator.isValidId(0));
        assertFalse(validator.isValidId(-1));
    }

    @Test
    void testIsValidDriverId() {
        assertTrue(validator.isValidDriverId(1));
        assertFalse(validator.isValidDriverId(0));
        assertFalse(validator.isValidDriverId(-1));
    }

    @Test
    void testIsValidCompanyId() {
        assertTrue(validator.isValidCompanyId(1));
        assertFalse(validator.isValidCompanyId(0));
        assertFalse(validator.isValidCompanyId(-1));
    }

    @Test
    void testIsValidName() {
        assertTrue(validator.isValidName("João Silva"));
        assertFalse(validator.isValidName(""));
        assertFalse(validator.isValidName(null));
        assertFalse(validator.isValidName("   "));
    }

    @Test
    void testIsValidCpf() {
        assertTrue(validator.isValidCpf("123.456.789-01"));
        assertTrue(validator.isValidCpf("12345678901"));
        assertFalse(validator.isValidCpf("123.456.789-0")); // CPF inválido
        assertFalse(validator.isValidCpf(null));
        assertFalse(validator.isValidCpf(""));
    }

    @Test
    void testIsValidLicenseNumber() {
        assertTrue(validator.isValidLicenseNumber("ABC12345678"));
        assertFalse(validator.isValidLicenseNumber("ABC123")); // Curto demais
        assertFalse(validator.isValidLicenseNumber(null));
        assertFalse(validator.isValidLicenseNumber(""));
    }

    @Test
    void testIsValidCnpj() {
        assertTrue(validator.isValidCnpj("12.345.678/0001-90"));
        assertTrue(validator.isValidCnpj("12345678000190"));
        assertFalse(validator.isValidCnpj("12.345.678/0001-9")); // CNPJ inválido
        assertFalse(validator.isValidCnpj(null));
        assertFalse(validator.isValidCnpj(""));
    }

    @Test
    void testIsValidPlate() {
        assertTrue(validator.isValidPlate("ABC1234"));
        assertTrue(validator.isValidPlate("ABC1D23")); // Placa Mercosul
        assertFalse(validator.isValidPlate("ABC123")); // Curta demais
        assertFalse(validator.isValidPlate(null));
        assertFalse(validator.isValidPlate(""));
    }

    @Test
    void testIsValidEmail() {
        assertTrue(validator.isValidEmail("teste@example.com"));
        assertFalse(validator.isValidEmail("teste@example"));
        assertFalse(validator.isValidEmail("testeexample.com"));
        assertFalse(validator.isValidEmail(null));
        assertFalse(validator.isValidEmail(""));
    }

    @Test
    void testIsValidPhoneNumber() {
        assertTrue(validator.isValidPhoneNumber("11987654321"));
        assertTrue(validator.isValidPhoneNumber("1132165498"));
        assertFalse(validator.isValidPhoneNumber("123")); // Curto demais
        assertFalse(validator.isValidPhoneNumber(null));
        assertFalse(validator.isValidPhoneNumber(""));
    }

    @Test
    void testIsPastOrPresentDate() {
        assertTrue(validator.isPastOrPresentDate(LocalDate.now()));
        assertTrue(validator.isPastOrPresentDate(LocalDate.now().minusDays(1)));
        assertFalse(validator.isPastOrPresentDate(LocalDate.now().plusDays(1)));
        assertFalse(validator.isPastOrPresentDate(null));
    }

    @Test
    void testIsValidTime() {
        assertTrue(validator.isValidTime(LocalTime.now()));
        assertFalse(validator.isValidTime(null));
    }

    @Test
    void testIsValidLocation() {
        assertTrue(validator.isValidLocation("Rua Exemplo, 123"));
        assertFalse(validator.isValidLocation(""));
        assertFalse(validator.isValidLocation(null));
        assertFalse(validator.isValidLocation("   "));
    }

    @Test
    void testIsValidAddress() {
        assertTrue(validator.isValidAddress("Avenida Principal, 456"));
        assertFalse(validator.isValidAddress(""));
        assertFalse(validator.isValidAddress(null));
        assertFalse(validator.isValidAddress("   "));
    }

    @Test
    void testIsValidDateTime() {
        assertTrue(validator.isValidDateTime(LocalDateTime.now()));
        assertTrue(validator.isValidDateTime(LocalDateTime.now().minusDays(1)));
        assertFalse(validator.isValidDateTime(LocalDateTime.now().plusDays(1)));
        assertFalse(validator.isValidDateTime(null));
    }

    @Test
    void testIsWithinMaxDuration() {
        Duration max = Duration.ofHours(2);
        assertTrue(validator.isWithinMaxDuration(Duration.ofHours(1), max));
        assertTrue(validator.isWithinMaxDuration(Duration.ofHours(2), max));
        assertFalse(validator.isWithinMaxDuration(Duration.ofHours(3), max));
        assertFalse(validator.isWithinMaxDuration(null, max));
    }

    @Test
    void testIsAboveMinDuration() {
        Duration min = Duration.ofMinutes(30);
        assertTrue(validator.isAboveMinDuration(Duration.ofHours(1), min));
        assertTrue(validator.isAboveMinDuration(Duration.ofMinutes(30), min));
        assertFalse(validator.isAboveMinDuration(Duration.ofMinutes(15), min));
        assertFalse(validator.isAboveMinDuration(null, min));
    }
}
