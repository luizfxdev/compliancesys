package com.compliancesys.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ValidatorTest {

    // Implementação concreta simples da interface Validator para fins de teste
    private static class ValidatorImpl implements Validator {

        // Regex para validar CPF (formato XXX.XXX.XXX-XX)
        private static final Pattern CPF_PATTERN = Pattern.compile("^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$");
        // Regex para validar nome (apenas letras, espaços, e alguns caracteres acentuados comuns)
        private static final Pattern NAME_PATTERN = Pattern.compile("^[\\p{L} .'-]+$");
        // Regex para validar CNH (alfanumérica, pode ter espaços ou hífens, mas não vazia)
        private static final Pattern LICENSE_NUMBER_PATTERN = Pattern.compile("^[a-zA-Z0-9\\s-]+$");


        @Override
        public boolean isValidString(String value) {
            return value != null && !value.trim().isEmpty();
        }

        @Override
        public boolean isPositive(int value) {
            return value > 0;
        }

        @Override
        public boolean isValidCpf(String cpf) {
            if (!isValidString(cpf)) {
                return false;
            }
            // Remove caracteres não numéricos para validação de dígitos
            String cleanCpf = cpf.replaceAll("[^0-9]", "");

            // Verifica o formato com regex (opcional, mas bom para consistência)
            if (!CPF_PATTERN.matcher(cpf).matches()) {
                return false;
            }

            // Verifica se tem 11 dígitos
            if (cleanCpf.length() != 11) {
                return false;
            }

            // Verifica CPFs com todos os dígitos iguais (considerados inválidos)
            if (cleanCpf.matches("(\\d)\\1{10}")) {
                return false;
            }

            // Validação do primeiro dígito verificador
            int sum = 0;
            for (int i = 0; i < 9; i++) {
                sum += (cleanCpf.charAt(i) - '0') * (10 - i);
            }
            int firstVerifier = 11 - (sum % 11);
            if (firstVerifier > 9) {
                firstVerifier = 0;
            }
            if ((cleanCpf.charAt(9) - '0') != firstVerifier) {
                return false;
            }

            // Validação do segundo dígito verificador
            sum = 0;
            for (int i = 0; i < 10; i++) {
                sum += (cleanCpf.charAt(i) - '0') * (11 - i);
            }
            int secondVerifier = 11 - (sum % 11);
            if (secondVerifier > 9) {
                secondVerifier = 0;
            }
            return (cleanCpf.charAt(10) - '0') == secondVerifier;
        }

        @Override
        public boolean isValidDate(LocalDate date) {
            return date != null && !date.isAfter(LocalDate.now());
        }

        @Override
        public boolean isValidTime(LocalTime time) {
            return time != null;
        }

        @Override
        public boolean isNotNull(Object object) {
            return object != null;
        }

        @Override
        public boolean isValidName(String name) {
            return isValidString(name) && NAME_PATTERN.matcher(name).matches();
        }

        @Override
        public boolean isValidLicenseNumber(String licenseNumber) {
            return isValidString(licenseNumber) && LICENSE_NUMBER_PATTERN.matcher(licenseNumber).matches();
        }

        @Override
        public boolean isValidLocation(String location) {
            return isValidString(location);
        }
    }

    private Validator validator;

    @BeforeEach
    void setUp() {
        // Inicializa a implementação concreta do Validator antes de cada teste
        validator = new ValidatorImpl();
    }

    // Testes para isValidString
    @Test
    void testIsValidStringWithValidValue() {
        assertTrue(validator.isValidString("Hello World"));
    }

    @Test
    void testIsValidStringWithEmptyValue() {
        assertFalse(validator.isValidString(""));
    }

    @Test
    void testIsValidStringWithBlankValue() {
        assertFalse(validator.isValidString("   "));
    }

    @Test
    void testIsValidStringWithNullValue() {
        assertFalse(validator.isValidString(null));
    }

    // Testes para isPositive
    @Test
    void testIsPositiveWithPositiveValue() {
        assertTrue(validator.isPositive(10));
    }

    @Test
    void testIsPositiveWithZeroValue() {
        assertFalse(validator.isPositive(0));
    }

    @Test
    void testIsPositiveWithNegativeValue() {
        assertFalse(validator.isPositive(-5));
    }

    // Testes para isValidCpf
    @Test
    void testIsValidCpfWithValidCpf() {
        // Um CPF válido real ou gerado corretamente para teste
        assertTrue(validator.isValidCpf("111.444.777-05"));
        assertTrue(validator.isValidCpf("000.000.000-00")); // Este é um caso especial que a validação padrão geralmente rejeita, mas a implementação aqui aceita se os dígitos verificadores forem calculados corretamente.
                                                          // A implementação acima foi ajustada para rejeitar CPFs com todos os dígitos iguais.
        assertFalse(validator.isValidCpf("000.000.000-00")); // Agora deve ser falso
        assertTrue(validator.isValidCpf("123.456.789-00")); // Exemplo de CPF válido
    }

    @Test
    void testIsValidCpfWithInvalidFormat() {
        assertFalse(validator.isValidCpf("12345678900")); // Sem pontos e hífen
        assertFalse(validator.isValidCpf("123.456.789.00")); // Formato incorreto
        assertFalse(validator.isValidCpf("abc.def.ghi-jk")); // Caracteres não numéricos
    }

    @Test
    void testIsValidCpfWithIncorrectLength() {
        assertFalse(validator.isValidCpf("123.456.789-0")); // Menos dígitos
        assertFalse(validator.isValidCpf("123.456.789-000")); // Mais dígitos
    }

    @Test
    void testIsValidCpfWithAllSameDigits() {
        assertFalse(validator.isValidCpf("111.111.111-11"));
        assertFalse(validator.isValidCpf("222.222.222-22"));
    }

    @Test
    void testIsValidCpfWithIncorrectVerifierDigits() {
        assertFalse(validator.isValidCpf("111.444.777-00")); // Dígito verificador incorreto
        assertFalse(validator.isValidCpf("123.456.789-10")); // Dígito verificador incorreto
    }

    @Test
    void testIsValidCpfWithNullOrEmpty() {
        assertFalse(validator.isValidCpf(null));
        assertFalse(validator.isValidCpf(""));
        assertFalse(validator.isValidCpf("   "));
    }

    // Testes para isValidDate
    @Test
    void testIsValidDateWithPastDate() {
        LocalDate pastDate = LocalDate.now().minusDays(1);
        assertTrue(validator.isValidDate(pastDate));
    }

    @Test
    void testIsValidDateWithCurrentDate() {
        LocalDate currentDate = LocalDate.now();
        assertTrue(validator.isValidDate(currentDate));
    }

    @Test
    void testIsValidDateWithFutureDate() {
        LocalDate futureDate = LocalDate.now().plusDays(1);
        assertFalse(validator.isValidDate(futureDate));
    }

    @Test
    void testIsValidDateWithNullDate() {
        assertFalse(validator.isValidDate(null));
    }

    // Testes para isValidTime
    @Test
    void testIsValidTimeWithValidTime() {
        LocalTime validTime = LocalTime.of(10, 30);
        assertTrue(validator.isValidTime(validTime));
    }

    @Test
    void testIsValidTimeWithNullTime() {
        assertFalse(validator.isValidTime(null));
    }

    // Testes para isNotNull
    @Test
    void testIsNotNullWithNonNullObject() {
        Object obj = new Object();
        assertTrue(validator.isNotNull(obj));
    }

    @Test
    void testIsNotNullWithNullObject() {
        Object obj = null;
        assertFalse(validator.isNotNull(obj));
    }

    // Testes para isValidName
    @Test
    void testIsValidNameWithValidName() {
        assertTrue(validator.isValidName("João Silva"));
        assertTrue(validator.isValidName("Maria da Conceição"));
        assertTrue(validator.isValidName("Luiz Fernando Jr."));
        assertTrue(validator.isValidName("José D'Ávila"));
        assertTrue(validator.isValidName("René Descartes"));
    }

    @Test
    void testIsValidNameWithInvalidCharacters() {
        assertFalse(validator.isValidName("João123"));
        assertFalse(validator.isValidName("Maria@Silva"));
        assertFalse(validator.isValidName("Nome Com Símbolo!"));
    }

    @Test
    void testIsValidNameWithEmptyOrNull() {
        assertFalse(validator.isValidName(""));
        assertFalse(validator.isValidName("   "));
        assertFalse(validator.isValidName(null));
    }

    // Testes para isValidLicenseNumber
    @Test
    void testIsValidLicenseNumberWithValidValue() {
        assertTrue(validator.isValidLicenseNumber("CNH123456789"));
        assertTrue(validator.isValidLicenseNumber("AB1234567"));
        assertTrue(validator.isValidLicenseNumber("12345678901"));
        assertTrue(validator.isValidLicenseNumber("CNH-123 456"));
    }

    @Test
    void testIsValidLicenseNumberWithInvalidCharacters() {
        assertFalse(validator.isValidLicenseNumber("CNH!@#"));
        assertFalse(validator.isValidLicenseNumber("CNH_123")); // Underscore não permitido na regex atual
    }

    @Test
    void testIsValidLicenseNumberWithEmptyOrNull() {
        assertFalse(validator.isValidLicenseNumber(""));
        assertFalse(validator.isValidLicenseNumber("   "));
        assertFalse(validator.isValidLicenseNumber(null));
    }

    // Testes para isValidLocation
    @Test
    void testIsValidLocationWithValidValue() {
        assertTrue(validator.isValidLocation("Rua das Flores, 123"));
        assertTrue(validator.isValidLocation("São Paulo"));
        assertTrue(validator.isValidLocation("Av. Principal - Centro"));
    }

    @Test
    void testIsValidLocationWithEmptyOrNull() {
        assertFalse(validator.isValidLocation(""));
        assertFalse(validator.isValidLocation("   "));
        assertFalse(validator.isValidLocation(null));
    }
}
