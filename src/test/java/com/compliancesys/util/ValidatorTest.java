package com.compliancesys.util;

import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.*;
import com.compliancesys.model.enums.ComplianceStatus;
import com.compliancesys.model.enums.EventType;
import com.compliancesys.util.impl.ValidatorImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ValidatorTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = new ValidatorImpl();
    }

    // ==================== Testes de ID ====================

    @Test
    @DisplayName("Deve validar ID positivo como válido")
    void isValidId_PositiveId_ReturnsTrue() {
        assertTrue(validator.isValidId(1));
        assertTrue(validator.isValidId(100));
        assertTrue(validator.isValidId(Integer.MAX_VALUE));
    }

    @Test
    @DisplayName("Deve invalidar ID zero ou negativo")
    void isValidId_ZeroOrNegative_ReturnsFalse() {
        assertFalse(validator.isValidId(0));
        assertFalse(validator.isValidId(-1));
        assertFalse(validator.isValidId(Integer.MIN_VALUE));
    }

    // ==================== Testes de CPF ====================

    @Test
    @DisplayName("Deve validar CPF com formato correto")
    void isValidCpf_ValidFormat_ReturnsTrue() {
        assertTrue(validator.isValidCpf("123.456.789-00"));
        assertTrue(validator.isValidCpf("000.000.000-00"));
    }

    @Test
    @DisplayName("Deve invalidar CPF com formato incorreto")
    void isValidCpf_InvalidFormat_ReturnsFalse() {
        assertFalse(validator.isValidCpf("12345678900"));
        assertFalse(validator.isValidCpf("123.456.789-0"));
        assertFalse(validator.isValidCpf("123-456-789-00"));
        assertFalse(validator.isValidCpf(null));
        assertFalse(validator.isValidCpf(""));
    }

    // ==================== Testes de CNPJ ====================

    @Test
    @DisplayName("Deve validar CNPJ com formato correto")
    void isValidCnpj_ValidFormat_ReturnsTrue() {
        assertTrue(validator.isValidCnpj("12.345.678/0001-90"));
        assertTrue(validator.isValidCnpj("00.000.000/0000-00"));
    }

    @Test
    @DisplayName("Deve invalidar CNPJ com formato incorreto")
    void isValidCnpj_InvalidFormat_ReturnsFalse() {
        assertFalse(validator.isValidCnpj("12345678000190"));
        assertFalse(validator.isValidCnpj("12.345.678/0001-9"));
        assertFalse(validator.isValidCnpj(null));
        assertFalse(validator.isValidCnpj(""));
    }

    // ==================== Testes de Nome ====================

    @Test
    @DisplayName("Deve validar nome com 3 ou mais caracteres")
    void isValidName_ValidName_ReturnsTrue() {
        assertTrue(validator.isValidName("João"));
        assertTrue(validator.isValidName("Ana"));
        assertTrue(validator.isValidName("Maria da Silva"));
    }

    @Test
    @DisplayName("Deve invalidar nome curto ou vazio")
    void isValidName_InvalidName_ReturnsFalse() {
        assertFalse(validator.isValidName("AB"));
        assertFalse(validator.isValidName(""));
        assertFalse(validator.isValidName("  "));
        assertFalse(validator.isValidName(null));
    }

    // ==================== Testes de Placa ====================

    @Test
    @DisplayName("Deve validar placa no formato antigo e Mercosul")
    void isValidPlate_ValidFormats_ReturnsTrue() {
        assertTrue(validator.isValidPlate("ABC1234"));
        assertTrue(validator.isValidPlate("ABC1D23"));
    }

    @Test
    @DisplayName("Deve invalidar placa com formato incorreto")
    void isValidPlate_InvalidFormat_ReturnsFalse() {
        assertFalse(validator.isValidPlate("ABC-1234"));
        assertFalse(validator.isValidPlate("abc1234"));
        assertFalse(validator.isValidPlate("ABCD123"));
        assertFalse(validator.isValidPlate(null));
        assertFalse(validator.isValidPlate(""));
    }

    // ==================== Testes de Data ====================

    @Test
    @DisplayName("Deve validar data no passado ou presente")
    void isValidDate_PastOrPresent_ReturnsTrue() {
        assertTrue(validator.isValidDate(LocalDate.now()));
        assertTrue(validator.isValidDate(LocalDate.now().minusDays(1)));
        assertTrue(validator.isValidDate(LocalDate.of(2000, 1, 1)));
    }

    @Test
    @DisplayName("Deve invalidar data futura ou nula")
    void isValidDate_FutureOrNull_ReturnsFalse() {
        assertFalse(validator.isValidDate(LocalDate.now().plusDays(1)));
        assertFalse(validator.isValidDate(null));
    }

    // ==================== Testes de Email ====================

    @Test
    @DisplayName("Deve validar email com formato correto")
    void isValidEmail_ValidFormat_ReturnsTrue() {
        assertTrue(validator.isValidEmail("teste@email.com"));
        assertTrue(validator.isValidEmail("usuario.nome@empresa.com.br"));
        assertTrue(validator.isValidEmail("user+tag@domain.org"));
    }

    @Test
    @DisplayName("Deve invalidar email com formato incorreto")
    void isValidEmail_InvalidFormat_ReturnsFalse() {
        assertFalse(validator.isValidEmail("email_invalido"));
        assertFalse(validator.isValidEmail("@email.com"));
        assertFalse(validator.isValidEmail("teste@"));
        assertFalse(validator.isValidEmail(null));
        assertFalse(validator.isValidEmail(""));
    }

    // ==================== Testes de Telefone ====================

    @Test
    @DisplayName("Deve validar telefone com formato correto")
    void isValidPhone_ValidFormat_ReturnsTrue() {
        assertTrue(validator.isValidPhone("11987654321"));
        assertTrue(validator.isValidPhone("(11)987654321"));
        assertTrue(validator.isValidPhone("(11) 98765-4321"));
    }

    @Test
    @DisplayName("Deve invalidar telefone com formato incorreto")
    void isValidPhone_InvalidFormat_ReturnsFalse() {
        assertFalse(validator.isValidPhone("123456"));
        assertFalse(validator.isValidPhone(null));
        assertFalse(validator.isValidPhone(""));
    }

    // ==================== Testes de Latitude/Longitude ====================

    @Test
    @DisplayName("Deve validar latitude dentro do intervalo")
    void isValidLatitude_ValidRange_ReturnsTrue() {
        assertTrue(validator.isValidLatitude(0.0));
        assertTrue(validator.isValidLatitude(-90.0));
        assertTrue(validator.isValidLatitude(90.0));
        assertTrue(validator.isValidLatitude(-23.5505));
    }

    @Test
    @DisplayName("Deve invalidar latitude fora do intervalo")
    void isValidLatitude_InvalidRange_ReturnsFalse() {
        assertFalse(validator.isValidLatitude(-90.1));
        assertFalse(validator.isValidLatitude(90.1));
        assertFalse(validator.isValidLatitude(null));
    }

    @Test
    @DisplayName("Deve validar longitude dentro do intervalo")
    void isValidLongitude_ValidRange_ReturnsTrue() {
        assertTrue(validator.isValidLongitude(0.0));
        assertTrue(validator.isValidLongitude(-180.0));
        assertTrue(validator.isValidLongitude(180.0));
        assertTrue(validator.isValidLongitude(-46.6333));
    }

    @Test
    @DisplayName("Deve invalidar longitude fora do intervalo")
    void isValidLongitude_InvalidRange_ReturnsFalse() {
        assertFalse(validator.isValidLongitude(-180.1));
        assertFalse(validator.isValidLongitude(180.1));
        assertFalse(validator.isValidLongitude(null));
    }

    // ==================== Testes de Validação de Driver ====================

    @Test
    @DisplayName("Deve validar Driver com dados corretos")
    void validateDriver_ValidDriver_NoException() {
        Driver driver = new Driver();
        driver.setName("João Silva");
        driver.setCpf("123.456.789-00");
        driver.setLicenseNumber("12345678901");
        driver.setBirthDate(LocalDate.of(1985, 5, 15));

        assertDoesNotThrow(() -> validator.validate(driver));
    }

    @Test
    @DisplayName("Deve lançar exceção para Driver nulo")
    void validateDriver_NullDriver_ThrowsException() {
        assertThrows(BusinessException.class, () -> validator.validate((Driver) null));
    }

    @Test
    @DisplayName("Deve lançar exceção para Driver com nome inválido")
    void validateDriver_InvalidName_ThrowsException() {
        Driver driver = new Driver();
        driver.setName("AB");
        driver.setCpf("123.456.789-00");
        driver.setLicenseNumber("12345678901");
        driver.setBirthDate(LocalDate.of(1985, 5, 15));

        assertThrows(BusinessException.class, () -> validator.validate(driver));
    }

    @Test
    @DisplayName("Deve lançar exceção para Driver menor de 18 anos")
    void validateDriver_Underage_ThrowsException() {
        Driver driver = new Driver();
        driver.setName("João Silva");
        driver.setCpf("123.456.789-00");
        driver.setLicenseNumber("12345678901");
        driver.setBirthDate(LocalDate.now().minusYears(17));

        assertThrows(BusinessException.class, () -> validator.validate(driver));
    }

    // ==================== Testes de Validação de Vehicle ====================

    @Test
    @DisplayName("Deve validar Vehicle com dados corretos")
    void validateVehicle_ValidVehicle_NoException() {
        Vehicle vehicle = new Vehicle();
        vehicle.setPlate("ABC1234");
        vehicle.setModel("FH 540");
        vehicle.setYear(2022);
        vehicle.setCompanyId(1);

        assertDoesNotThrow(() -> validator.validate(vehicle));
    }

    @Test
    @DisplayName("Deve lançar exceção para Vehicle nulo")
    void validateVehicle_NullVehicle_ThrowsException() {
        assertThrows(BusinessException.class, () -> validator.validate((Vehicle) null));
    }

    @Test
    @DisplayName("Deve lançar exceção para Vehicle com ano inválido")
    void validateVehicle_InvalidYear_ThrowsException() {
        Vehicle vehicle = new Vehicle();
        vehicle.setPlate("ABC1234");
        vehicle.setModel("FH 540");
        vehicle.setYear(1800);
        vehicle.setCompanyId(1);

        assertThrows(BusinessException.class, () -> validator.validate(vehicle));
    }

    // ==================== Testes de Validação de Company ====================

    @Test
    @DisplayName("Deve validar Company com dados corretos")
    void validateCompany_ValidCompany_NoException() {
        Company company = new Company();
        company.setCnpj("12.345.678/0001-90");
        company.setLegalName("Empresa Teste LTDA");

        assertDoesNotThrow(() -> validator.validate(company));
    }

    @Test
    @DisplayName("Deve lançar exceção para Company nula")
    void validateCompany_NullCompany_ThrowsException() {
        assertThrows(BusinessException.class, () -> validator.validate((Company) null));
    }

    // ==================== Testes de Validação de Journey ====================

    @Test
    @DisplayName("Deve validar Journey com dados corretos")
    void validateJourney_ValidJourney_NoException() {
        Journey journey = new Journey();
        journey.setDriverId(1);
        journey.setJourneyDate(LocalDate.now());

        assertDoesNotThrow(() -> validator.validate(journey));
    }

    @Test
    @DisplayName("Deve lançar exceção para Journey nula")
    void validateJourney_NullJourney_ThrowsException() {
        assertThrows(BusinessException.class, () -> validator.validate((Journey) null));
    }

    // ==================== Testes de Validação de TimeRecord ====================

    @Test
    @DisplayName("Deve validar TimeRecord com dados corretos")
    void validateTimeRecord_ValidTimeRecord_NoException() {
        TimeRecord timeRecord = new TimeRecord();
        timeRecord.setDriverId(1);
        timeRecord.setRecordTime(LocalDateTime.now());
        timeRecord.setEventType(EventType.START_DRIVE);

        assertDoesNotThrow(() -> validator.validate(timeRecord));
    }

    @Test
    @DisplayName("Deve lançar exceção para TimeRecord nulo")
    void validateTimeRecord_NullTimeRecord_ThrowsException() {
        assertThrows(BusinessException.class, () -> validator.validate((TimeRecord) null));
    }

    // ==================== Testes de Validação de ComplianceAudit ====================

    @Test
    @DisplayName("Deve validar ComplianceAudit com dados corretos")
    void validateComplianceAudit_ValidAudit_NoException() {
        ComplianceAudit audit = new ComplianceAudit();
        audit.setJourneyId(1);
        audit.setAuditDate(LocalDate.now());
        audit.setStatus(ComplianceStatus.PENDING);

        assertDoesNotThrow(() -> validator.validate(audit));
    }

    @Test
    @DisplayName("Deve lançar exceção para ComplianceAudit nulo")
    void validateComplianceAudit_NullAudit_ThrowsException() {
        assertThrows(BusinessException.class, () -> validator.validate((ComplianceAudit) null));
    }

    // ==================== Testes de Validação de MobileCommunication ====================

    @Test
    @DisplayName("Deve validar MobileCommunication com dados corretos")
    void validateMobileCommunication_ValidComm_NoException() {
        MobileCommunication comm = new MobileCommunication();
        comm.setDriverId(1);
        comm.setTimestamp(LocalDateTime.now());
        comm.setDeviceId("DEVICE-001");

        assertDoesNotThrow(() -> validator.validate(comm));
    }

    @Test
    @DisplayName("Deve lançar exceção para MobileCommunication nulo")
    void validateMobileCommunication_NullComm_ThrowsException() {
        assertThrows(BusinessException.class, () -> validator.validate((MobileCommunication) null));
    }
}