package com.compliancesys.util;

import com.compliancesys.util.impl.ValidatorImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("Deve validar ID positivo com sucesso")
    void shouldValidatePositiveIdSuccessfully() {
        assertTrue(validator.isValidId(1));
        assertTrue(validator.isValidId(100));
    }

    @Test
    @DisplayName("Não deve validar ID zero ou negativo")
    void shouldNotValidateZeroOrNegativeId() {
        assertFalse(validator.isValidId(0));
        assertFalse(validator.isValidId(-1));
    }

    @Test
    @DisplayName("Deve validar nome não nulo e não vazio com sucesso")
    void shouldValidateNameSuccessfully() {
        assertTrue(validator.isValidName("Nome Teste"));
        assertTrue(validator.isValidName("João da Silva"));
    }

    @Test
    @DisplayName("Não deve validar nome nulo, vazio ou com apenas espaços")
    void shouldNotValidateNullEmptyOrBlankName() {
        assertFalse(validator.isValidName(null));
        assertFalse(validator.isValidName(""));
        assertFalse(validator.isValidName("   "));
    }

    @Test
    @DisplayName("Deve validar CPF válido com e sem formatação")
    void shouldValidateValidCpf() {
        assertTrue(validator.isValidCpf("11122233344"));
        assertTrue(validator.isValidCpf("111.222.333-44"));
    }

    @Test
    @DisplayName("Não deve validar CPF inválido")
    void shouldNotValidateInvalidCpf() {
        assertFalse(validator.isValidCpf("11122233300")); // Dígito verificador inválido
        assertFalse(validator.isValidCpf("11111111111")); // Todos os dígitos iguais
        assertFalse(validator.isValidCpf("123")); // Curto demais
        assertFalse(validator.isValidCpf(null));
        assertFalse(validator.isValidCpf(""));
        assertFalse(validator.isValidCpf("   "));
        assertFalse(validator.isValidCpf("111.222.333-4X")); // Caractere inválido
    }

    @Test
    @DisplayName("Deve validar CNH válida com e sem formatação")
    void shouldValidateValidCnh() {
        assertTrue(validator.isValidCnh("12345678901"));
        assertTrue(validator.isValidCnh("123456789-01"));
    }

    @Test
    @DisplayName("Não deve validar CNH inválida")
    void shouldNotValidateInvalidCnh() {
        assertFalse(validator.isValidCnh("1234567890")); // Curta demais
        assertFalse(validator.isValidCnh("123456789012")); // Longa demais
        assertFalse(validator.isValidCnh("ABC12345678")); // Caractere inválido
        assertFalse(validator.isValidCnh(null));
        assertFalse(validator.isValidCnh(""));
        assertFalse(validator.isValidCnh("   "));
    }

    @Test
    @DisplayName("Deve validar categoria de CNH válida")
    void shouldValidateValidCnhCategory() {
        assertTrue(validator.isValidCnhCategory("A"));
        assertTrue(validator.isValidCnhCategory("B"));
        assertTrue(validator.isValidCnhCategory("AB"));
        assertTrue(validator.isValidCnhCategory("C"));
        assertTrue(validator.isValidCnhCategory("D"));
        assertTrue(validator.isValidCnhCategory("E"));
    }

    @Test
    @DisplayName("Não deve validar categoria de CNH inválida")
    void shouldNotValidateInvalidCnhCategory() {
        assertFalse(validator.isValidCnhCategory("F"));
        assertFalse(validator.isValidCnhCategory("a")); // Case sensitive
        assertFalse(validator.isValidCnhCategory(null));
        assertFalse(validator.isValidCnhCategory(""));
        assertFalse(validator.isValidCnhCategory("   "));
    }

    @Test
    @DisplayName("Deve validar CNPJ válido com e sem formatação")
    void shouldValidateValidCnpj() {
        assertTrue(validator.isValidCnpj("12345678000190"));
        assertTrue(validator.isValidCnpj("12.345.678/0001-90"));
    }

    @Test
    @DisplayName("Não deve validar CNPJ inválido")
    void shouldNotValidateInvalidCnpj() {
        assertFalse(validator.isValidCnpj("12345678000100")); // Dígito verificador inválido
        assertFalse(validator.isValidCnpj("11111111111111")); // Todos os dígitos iguais
        assertFalse(validator.isValidCnpj("123")); // Curto demais
        assertFalse(validator.isValidCnpj(null));
        assertFalse(validator.isValidCnpj(""));
        assertFalse(validator.isValidCnpj("   "));
        assertFalse(validator.isValidCnpj("12.345.678/0001-9X")); // Caractere inválido
    }

    @Test
    @DisplayName("Deve validar placa Mercosul e antiga válidas")
    void shouldValidateValidPlate() {
        assertTrue(validator.isValidPlate("ABC1234")); // Padrão antigo
        assertTrue(validator.isValidPlate("ABC1D23")); // Padrão Mercosul
        assertTrue(validator.isValidPlate("BRA2E19")); // Outro Mercosul
    }

    @Test
    @DisplayName("Não deve validar placa inválida")
    void shouldNotValidateInvalidPlate() {
        assertFalse(validator.isValidPlate("ABC123")); // Curta demais
        assertFalse(validator.isValidPlate("ABC12345")); // Longa demais
        assertFalse(validator.isValidPlate("AB12345")); // Formato inválido
        assertFalse(validator.isValidPlate("1234567")); // Formato inválido
        assertFalse(validator.isValidPlate(null));
        assertFalse(validator.isValidPlate(""));
        assertFalse(validator.isValidPlate("   "));
    }

    @Test
    @DisplayName("Deve validar email válido")
    void shouldValidateValidEmail() {
        assertTrue(validator.isValidEmail("teste@example.com"));
        assertTrue(validator.isValidEmail("nome.sobrenome@dominio.com.br"));
    }

    @Test
    @DisplayName("Não deve validar email inválido")
    void shouldNotValidateInvalidEmail() {
        assertFalse(validator.isValidEmail("teste@example"));
        assertFalse(validator.isValidEmail("testeexample.com"));
        assertFalse(validator.isValidEmail("@example.com"));
        assertFalse(validator.isValidEmail("teste@.com"));
        assertFalse(validator.isValidEmail(null));
        assertFalse(validator.isValidEmail(""));
        assertFalse(validator.isValidEmail("   "));
    }

    @Test
    @DisplayName("Deve validar número de telefone válido")
    void shouldValidateValidPhoneNumber() {
        assertTrue(validator.isValidPhone("11987654321")); // Celular com DDD
        assertTrue(validator.isValidPhone("1132165498"));  // Fixo com DDD
        assertTrue(validator.isValidPhone("987654321"));   // Celular sem DDD (assumindo 9 dígitos)
        assertTrue(validator.isValidPhone("32165498"));    // Fixo sem DDD (assumindo 8 dígitos)
    }

    @Test
    @DisplayName("Não deve validar número de telefone inválido")
    void shouldNotValidateInvalidPhoneNumber() {
        assertFalse(validator.isValidPhone("123")); // Curto demais
        assertFalse(validator.isValidPhone("111234567890")); // Longo demais
        assertFalse(validator.isValidPhone("abcdefgh")); // Caracteres inválidos
        assertFalse(validator.isValidPhone(null));
        assertFalse(validator.isValidPhone(""));
        assertFalse(validator.isValidPhone("   "));
    }

    @Test
    @DisplayName("Deve validar data passada ou presente")
    void shouldValidatePastOrPresentDate() {
        assertTrue(validator.isPastOrPresentDate(LocalDate.now()));
        assertTrue(validator.isPastOrPresentDate(LocalDate.now().minusDays(1)));
    }

    @Test
    @DisplayName("Não deve validar data futura ou nula como passada ou presente")
    void shouldNotValidateFutureOrNullDateAsPastOrPresent() {
        assertFalse(validator.isPastOrPresentDate(LocalDate.now().plusDays(1)));
        assertFalse(validator.isPastOrPresentDate(null));
    }

    @Test
    @DisplayName("Deve validar data futura")
    void shouldValidateFutureDate() {
        assertTrue(validator.isFutureDate(LocalDate.now().plusDays(1)));
    }

    @Test
    @DisplayName("Não deve validar data passada, presente ou nula como futura")
    void shouldNotValidatePastPresentOrNullDateAsFuture() {
        assertFalse(validator.isFutureDate(LocalDate.now()));
        assertFalse(validator.isFutureDate(LocalDate.now().minusDays(1)));
        assertFalse(validator.isFutureDate(null));
    }

    @Test
    @DisplayName("Deve validar data passada")
    void shouldValidatePastDate() {
        assertTrue(validator.isPastDate(LocalDate.now().minusDays(1)));
    }

    @Test
    @DisplayName("Não deve validar data futura, presente ou nula como passada")
    void shouldNotValidateFuturePresentOrNullDateAsPast() {
        assertFalse(validator.isPastDate(LocalDate.now()));
        assertFalse(validator.isPastDate(LocalDate.now().plusDays(1)));
        assertFalse(validator.isPastDate(null));
    }

    @Test
    @DisplayName("Deve validar localização não nula e não vazia")
    void shouldValidateLocationSuccessfully() {
        assertTrue(validator.isValidLocation("Rua Exemplo, 123"));
        assertTrue(validator.isValidLocation("Coordenadas: -23.5, -46.6"));
    }

    @Test
    @DisplayName("Não deve validar localização nula, vazia ou com apenas espaços")
    void shouldNotValidateNullEmptyOrBlankLocation() {
        assertFalse(validator.isValidLocation(""));
        assertFalse(validator.isValidLocation(null));
        assertFalse(validator.isValidLocation("   "));
    }

    @Test
    @DisplayName("Deve validar endereço não nulo e não vazio")
    void shouldValidateAddressSuccessfully() {
        assertTrue(validator.isValidAddress("Avenida Principal, 456"));
        assertTrue(validator.isValidAddress("Travessa da Paz, S/N - Centro"));
    }

    @Test
    @DisplayName("Não deve validar endereço nulo, vazio ou com apenas espaços")
    void shouldNotValidateNullEmptyOrBlankAddress() {
        assertFalse(validator.isValidAddress(""));
        assertFalse(validator.isValidAddress(null));
        assertFalse(validator.isValidAddress("   "));
    }

    @Test
    @DisplayName("Deve validar LocalDateTime não nulo")
    void shouldValidateNonNullDateTime() {
        assertTrue(validator.isValidDateTime(LocalDateTime.now()));
        assertTrue(validator.isValidDateTime(LocalDateTime.now().minusDays(1)));
    }

    @Test
    @DisplayName("Não deve validar LocalDateTime nulo")
    void shouldNotValidateNullDateTime() {
        assertFalse(validator.isValidDateTime(null));
    }

    @Test
    @DisplayName("Deve validar que a duração está dentro do máximo permitido")
    void shouldValidateDurationWithinMax() {
        Duration max = Duration.ofHours(2);
        assertTrue(validator.isWithinMaxDuration(Duration.ofHours(1), max));
        assertTrue(validator.isWithinMaxDuration(Duration.ofHours(2), max));
    }

    @Test
    @DisplayName("Não deve validar que a duração está acima do máximo ou é nula")
    void shouldNotValidateDurationAboveMaxOrNull() {
        Duration max = Duration.ofHours(2);
        assertFalse(validator.isWithinMaxDuration(Duration.ofHours(3), max));
        assertFalse(validator.isWithinMaxDuration(null, max));
    }

    @Test
    @DisplayName("Deve validar que a duração está acima do mínimo permitido")
    void shouldValidateDurationAboveMin() {
        Duration min = Duration.ofMinutes(30);
        assertTrue(validator.isAboveMinDuration(Duration.ofHours(1), min));
        assertTrue(validator.isAboveMinDuration(Duration.ofMinutes(30), min));
    }

    @Test
    @DisplayName("Não deve validar que a duração está abaixo do mínimo ou é nula")
    void shouldNotValidateDurationBelowMinOrNull() {
        Duration min = Duration.ofMinutes(30);
        assertFalse(validator.isAboveMinDuration(Duration.ofMinutes(15), min));
        assertFalse(validator.isAboveMinDuration(null, min));
    }

    @Test
    @DisplayName("Deve validar tipo de registro de tempo válido")
    void shouldValidateValidRecordType() {
        assertTrue(validator.isValidRecordType("START_WORK"));
        assertTrue(validator.isValidRecordType("END_WORK"));
        assertTrue(validator.isValidRecordType("START_DRIVING"));
        assertTrue(validator.isValidRecordType("END_DRIVING"));
        assertTrue(validator.isValidRecordType("PAUSE"));
        assertTrue(validator.isValidRecordType("RESUME"));
    }

    @Test
    @DisplayName("Não deve validar tipo de registro de tempo inválido")
    void shouldNotValidateInvalidRecordType() {
        assertFalse(validator.isValidRecordType("INVALID_TYPE"));
        assertFalse(validator.isValidRecordType(null));
        assertFalse(validator.isValidRecordType(""));
        assertFalse(validator.isValidRecordType("   "));
    }

    @Test
    @DisplayName("Deve validar tipo de comunicação válido")
    void shouldValidateValidCommunicationType() {
        assertTrue(validator.isValidCommunicationType("CALL"));
        assertTrue(validator.isValidCommunicationType("SMS"));
        assertTrue(validator.isValidCommunicationType("DATA"));
    }

    @Test
    @DisplayName("Não deve validar tipo de comunicação inválido")
    void shouldNotValidateInvalidCommunicationType() {
        assertFalse(validator.isValidCommunicationType("EMAIL"));
        assertFalse(validator.isValidCommunicationType(null));
        assertFalse(validator.isValidCommunicationType(""));
        assertFalse(validator.isValidCommunicationType("   "));
    }

    @Test
    @DisplayName("Deve validar status válido")
    void shouldValidateValidStatus() {
        assertTrue(validator.isValidStatus("PENDING"));
        assertTrue(validator.isValidStatus("IN_PROGRESS"));
        assertTrue(validator.isValidStatus("COMPLETED"));
        assertTrue(validator.isValidStatus("CANCELLED"));
        assertTrue(validator.isValidStatus("APPROVED"));
        assertTrue(validator.isValidStatus("REJECTED"));
    }

    @Test
    @DisplayName("Não deve validar status inválido")
    void shouldNotValidateInvalidStatus() {
        assertFalse(validator.isValidStatus("UNKNOWN"));
        assertFalse(validator.isValidStatus(null));
        assertFalse(validator.isValidStatus(""));
        assertFalse(validator.isValidStatus("   "));
    }

    @Test
    @DisplayName("Deve validar descrição não nula e não vazia")
    void shouldValidateDescriptionSuccessfully() {
        assertTrue(validator.isValidDescription("Uma descrição qualquer."));
        assertTrue(validator.isValidDescription("Breve nota."));
    }

    @Test
    @DisplayName("Não deve validar descrição nula, vazia ou com apenas espaços")
    void shouldNotValidateNullEmptyOrBlankDescription() {
        assertFalse(validator.isValidDescription(null));
        assertFalse(validator.isValidDescription(""));
        assertFalse(validator.isValidDescription("   "));
    }

    @Test
    @DisplayName("Deve validar origem/destino não nulo e não vazio")
    void shouldValidateOriginDestinationSuccessfully() {
        assertTrue(validator.isValidOriginDestination("São Paulo"));
        assertTrue(validator.isValidOriginDestination("Rua das Flores, 100"));
    }

    @Test
    @DisplayName("Não deve validar origem/destino nulo, vazio ou com apenas espaços")
    void shouldNotValidateNullEmptyOrBlankOriginDestination() {
        assertFalse(validator.isValidOriginDestination(null));
        assertFalse(validator.isValidOriginDestination(""));
        assertFalse(validator.isValidOriginDestination("   "));
    }

    @Test
    @DisplayName("Deve validar marca não nula e não vazia")
    void shouldValidateBrandSuccessfully() {
        assertTrue(validator.isValidBrand("Ford"));
        assertTrue(validator.isValidBrand("Mercedes-Benz"));
    }

    @Test
    @DisplayName("Não deve validar marca nula, vazia ou com apenas espaços")
    void shouldNotValidateNullEmptyOrBlankBrand() {
        assertFalse(validator.isValidBrand(null));
        assertFalse(validator.isValidBrand(""));
        assertFalse(validator.isValidBrand("   "));
    }

    @Test
    @DisplayName("Deve validar modelo não nulo e não vazio")
    void shouldValidateModelSuccessfully() {
        assertTrue(validator.isValidModel("Focus"));
        assertTrue(validator.isValidModel("Actros 2651"));
    }

    @Test
    @DisplayName("Não deve validar modelo nulo, vazio ou com apenas espaços")
    void shouldNotValidateNullEmptyOrBlankModel() {
        assertFalse(validator.isValidModel(null));
        assertFalse(validator.isValidModel(""));
        assertFalse(validator.isValidModel("   "));
    }

    @Test
    @DisplayName("Deve validar ano válido (entre 1900 e ano atual + 1)")
    void shouldValidateValidYear() {
        int currentYear = LocalDate.now().getYear();
        assertTrue(validator.isValidYear(currentYear));
        assertTrue(validator.isValidYear(currentYear - 10));
        assertTrue(validator.isValidYear(currentYear + 1)); // Permite ano do modelo futuro próximo
        assertTrue(validator.isValidYear(1900));
    }

    @Test
    @DisplayName("Não deve validar ano inválido (muito antigo ou muito futuro)")
    void shouldNotValidateInvalidYear() {
        int currentYear = LocalDate.now().getYear();
        assertFalse(validator.isValidYear(1899)); // Muito antigo
        assertFalse(validator.isValidYear(currentYear + 2)); // Muito futuro
        assertFalse(validator.isValidYear(0));
        assertFalse(validator.isValidYear(-2020));
    }

    @Test
    @DisplayName("Deve validar que a primeira data é igual ou anterior à segunda")
    void shouldValidateIsSameOrBeforeDate() {
        LocalDate date1 = LocalDate.of(2023, 1, 1);
        LocalDate date2 = LocalDate.of(2023, 1, 1);
        LocalDate date3 = LocalDate.of(2023, 1, 2);

        assertTrue(validator.isSameOrBeforeDate(date1, date2));
        assertTrue(validator.isSameOrBeforeDate(date1, date3));
        assertFalse(validator.isSameOrBeforeDate(date3, date1));
    }

    @Test
    @DisplayName("Não deve validar isSameOrBeforeDate com datas nulas")
    void shouldNotValidateIsSameOrBeforeDateWithNullDates() {
        LocalDate date1 = LocalDate.of(2023, 1, 1);
        assertFalse(validator.isSameOrBeforeDate(null, date1));
        assertFalse(validator.isSameOrBeforeDate(date1, null));
        assertFalse(validator.isSameOrBeforeDate(null, null));
    }

    @Test
    @DisplayName("Deve validar que a primeira data é igual ou posterior à segunda")
    void shouldValidateIsSameOrAfterDate() {
        LocalDate date1 = LocalDate.of(2023, 1, 1);
        LocalDate date2 = LocalDate.of(2023, 1, 1);
        LocalDate date3 = LocalDate.of(2022, 12, 31);

        assertTrue(validator.isSameOrAfterDate(date1, date2));
        assertTrue(validator.isSameOrAfterDate(date1, date3));
        assertFalse(validator.isSameOrAfterDate(date3, date1));
    }

    @Test
    @DisplayName("Não deve validar isSameOrAfterDate com datas nulas")
    void shouldNotValidateIsSameOrAfterDateWithNullDates() {
        LocalDate date1 = LocalDate.of(2023, 1, 1);
        assertFalse(validator.isSameOrAfterDate(null, date1));
        assertFalse(validator.isSameOrAfterDate(date1, null));
        assertFalse(validator.isSameOrAfterDate(null, null));
    }

    @Test
    @DisplayName("Deve validar que o primeiro LocalDateTime é igual ou anterior ao segundo")
    void shouldValidateIsSameOrBeforeDateTime() {
        LocalDateTime dt1 = LocalDateTime.of(2023, 1, 1, 10, 0, 0);
        LocalDateTime dt2 = LocalDateTime.of(2023, 1, 1, 10, 0, 0);
        LocalDateTime dt3 = LocalDateTime.of(2023, 1, 1, 10, 0, 1);

        assertTrue(validator.isSameOrBeforeDateTime(dt1, dt2));
        assertTrue(validator.isSameOrBeforeDateTime(dt1, dt3));
        assertFalse(validator.isSameOrBeforeDateTime(dt3, dt1));
    }

    @Test
    @DisplayName("Não deve validar isSameOrBeforeDateTime com LocalDateTime nulos")
    void shouldNotValidateIsSameOrBeforeDateTimeWithNullDateTimes() {
        LocalDateTime dt1 = LocalDateTime.of(2023, 1, 1, 10, 0, 0);
        assertFalse(validator.isSameOrBeforeDateTime(null, dt1));
        assertFalse(validator.isSameOrBeforeDateTime(dt1, null));
        assertFalse(validator.isSameOrBeforeDateTime(null, null));
    }

    @Test
    @DisplayName("Deve validar que o primeiro LocalDateTime é igual ou posterior ao segundo")
    void shouldValidateIsSameOrAfterDateTime() {
        LocalDateTime dt1 = LocalDateTime.of(2023, 1, 1, 10, 0, 0);
        LocalDateTime dt2 = LocalDateTime.of(2023, 1, 1, 10, 0, 0);
        LocalDateTime dt3 = LocalDateTime.of(2022, 12, 31, 23, 59, 59);

        assertTrue(validator.isSameOrAfterDateTime(dt1, dt2));
        assertTrue(validator.isSameOrAfterDateTime(dt1, dt3));
        assertFalse(validator.isSameOrAfterDateTime(dt3, dt1));
    }

    @Test
    @DisplayName("Não deve validar isSameOrAfterDateTime com LocalDateTime nulos")
    void shouldNotValidateIsSameOrAfterDateTimeWithNullDateTimes() {
        LocalDateTime dt1 = LocalDateTime.of(2023, 1, 1, 10, 0, 0);
        assertFalse(validator.isSameOrAfterDateTime(null, dt1));
        assertFalse(validator.isSameOrAfterDateTime(dt1, null));
        assertFalse(validator.isSameOrAfterDateTime(null, null));
    }

    @Test
    @DisplayName("Deve validar que o primeiro LocalDateTime é estritamente anterior ao segundo")
    void shouldValidateIsBeforeDateTime() {
        LocalDateTime dt1 = LocalDateTime.of(2023, 1, 1, 10, 0, 0);
        LocalDateTime dt2 = LocalDateTime.of(2023, 1, 1, 10, 0, 1);

        assertTrue(validator.isBeforeDateTime(dt1, dt2));
        assertFalse(validator.isBeforeDateTime(dt2, dt1));
        assertFalse(validator.isBeforeDateTime(dt1, dt1)); // Não é estritamente antes
    }

    @Test
    @DisplayName("Não deve validar isBeforeDateTime com LocalDateTime nulos")
    void shouldNotValidateIsBeforeDateTimeWithNullDateTimes() {
        LocalDateTime dt1 = LocalDateTime.of(2023, 1, 1, 10, 0, 0);
        assertFalse(validator.isBeforeDateTime(null, dt1));
        assertFalse(validator.isBeforeDateTime(dt1, null));
        assertFalse(validator.isBeforeDateTime(null, null));
    }

    @Test
    @DisplayName("Deve validar que o primeiro LocalDateTime é estritamente posterior ao segundo")
    void shouldValidateIsAfterDateTime() {
        LocalDateTime dt1 = LocalDateTime.of(2023, 1, 1, 10, 0, 1);
        LocalDateTime dt2 = LocalDateTime.of(2023, 1, 1, 10, 0, 0);

        assertTrue(validator.isAfterDateTime(dt1, dt2));
        assertFalse(validator.isAfterDateTime(dt2, dt1));
        assertFalse(validator.isAfterDateTime(dt1, dt1)); // Não é estritamente depois
    }

    @Test
    @DisplayName("Não deve validar isAfterDateTime com LocalDateTime nulos")
    void shouldNotValidateIsAfterDateTimeWithNullDateTimes() {
        LocalDateTime dt1 = LocalDateTime.of(2023, 1, 1, 10, 0, 0);
        assertFalse(validator.isAfterDateTime(null, dt1));
        assertFalse(validator.isAfterDateTime(dt1, null));
        assertFalse(validator.isAfterDateTime(null, null));
    }
}
