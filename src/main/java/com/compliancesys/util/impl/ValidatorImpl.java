package com.compliancesys.util.impl;

import java.time.LocalDate;
import java.util.regex.Pattern;

import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.Company;
import com.compliancesys.model.ComplianceAudit;
import com.compliancesys.model.Driver;
import com.compliancesys.model.Journey;
import com.compliancesys.model.MobileCommunication;
import com.compliancesys.model.TimeRecord;
import com.compliancesys.model.Vehicle;
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
        Pattern.compile("^\\(?[1-9]{2}\\)?[\\s-]?[9]?\\d{4}[\\s-]?" +
                    "\\d{4}$");

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
        // Implementação básica, pode ser mais robusta
        return time != null && time.matches("^([01]\\d|2[0-3]):([0-5]\\d)$");
    }

    @Override
    public boolean isValidLocation(String location) {
        return location != null && !location.trim().isEmpty() && location.length() >= 3;
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
    public boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    @Override
    public boolean isValidPhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone).matches();
    }

    // ADICIONADO: Implementações dos métodos de validação de objetos
    @Override
    public void validate(Driver driver) {
        if (driver == null) {
            throw new BusinessException("Objeto Driver não pode ser nulo.");
        }
        if (!isValidName(driver.getName())) {
            throw new BusinessException("Nome do motorista inválido.");
        }
        if (!isValidCpf(driver.getCpf())) {
            throw new BusinessException("CPF do motorista inválido.");
        }
        if (!isValidLicenseNumber(driver.getLicenseNumber())) {
            throw new BusinessException("Número da licença do motorista inválido.");
        }
        if (driver.getLicenseExpiration() == null || driver.getLicenseExpiration().isBefore(LocalDate.now())) {
            throw new BusinessException("Data de expiração da licença inválida ou expirada.");
        }
        if (driver.getBirthDate() == null || driver.getBirthDate().isAfter(LocalDate.now().minusYears(18))) {
            throw new BusinessException("Data de nascimento inválida. Motorista deve ter pelo menos 18 anos.");
        }
        if (!isValidPhone(driver.getPhone())) {
            throw new BusinessException("Telefone do motorista inválido.");
        }
        if (!isValidEmail(driver.getEmail())) {
            throw new BusinessException("Email do motorista inválido.");
        }
        if (!isValidId(driver.getCompanyId())) {
            throw new BusinessException("ID da empresa do motorista inválido.");
        }
    }

    @Override
    public void validate(Vehicle vehicle) {
        if (vehicle == null) {
            throw new BusinessException("Objeto Vehicle não pode ser nulo.");
        }
        if (!isValidPlate(vehicle.getPlate())) {
            throw new BusinessException("Placa do veículo inválida.");
        }
        if (!isValidName(vehicle.getManufacturer())) { // Reutilizando isValidName para fabricante
            throw new BusinessException("Fabricante do veículo inválido.");
        }
        if (!isValidName(vehicle.getModel())) { // Reutilizando isValidName para modelo
            throw new BusinessException("Modelo do veículo inválido.");
        }
        if (vehicle.getYear() <= 1900 || vehicle.getYear() > LocalDate.now().getYear() + 1) {
            throw new BusinessException("Ano do veículo inválido.");
        }
        if (!isValidId(vehicle.getCompanyId())) {
            throw new BusinessException("ID da empresa do veículo inválido.");
        }
    }

    @Override
    public void validate(Company company) {
        if (company == null) {
            throw new BusinessException("Objeto Company não pode ser nulo.");
        }
        if (!isValidCnpj(company.getCnpj())) {
            throw new BusinessException("CNPJ da empresa inválido.");
        }
        if (!isValidName(company.getLegalName())) {
            throw new BusinessException("Razão social da empresa inválida.");
        }
        if (!isValidName(company.getTradingName())) {
            throw new BusinessException("Nome fantasia da empresa inválido.");
        }
    }

    @Override
    public void validate(Journey journey) {
        if (journey == null) {
            throw new BusinessException("Objeto Journey não pode ser nulo.");
        }
        if (!isValidId(journey.getDriverId())) {
            throw new BusinessException("ID do motorista da jornada inválido.");
        }
        if (!isValidId(journey.getVehicleId())) {
            throw new BusinessException("ID do veículo da jornada inválido.");
        }
        if (!isValidId(journey.getCompanyId())) {
            throw new BusinessException("ID da empresa da jornada inválido.");
        }
        if (journey.getJourneyDate() == null || journey.getJourneyDate().isAfter(LocalDate.now())) {
            throw new BusinessException("Data da jornada inválida ou futura.");
        }
        if (!isValidLocation(journey.getStartLocation())) {
            throw new BusinessException("Local de início da jornada inválido.");
        }
        // Outras validações de tempo, status, etc., podem ser adicionadas aqui
    }

    @Override
    public void validate(TimeRecord timeRecord) {
        if (timeRecord == null) {
            throw new BusinessException("Objeto TimeRecord não pode ser nulo.");
        }
        if (!isValidId(timeRecord.getDriverId())) {
            throw new BusinessException("ID do motorista do registro de tempo inválido.");
        }
        if (!isValidId(timeRecord.getJourneyId())) {
            throw new BusinessException("ID da jornada do registro de tempo inválido.");
        }
        if (timeRecord.getRecordTime() == null) {
            throw new BusinessException("Hora do registro de tempo não pode ser nula.");
        }
        if (timeRecord.getEventType() == null) {
            throw new BusinessException("Tipo de evento do registro de tempo não pode ser nulo.");
        }
        if (!isValidLocation(timeRecord.getLocation())) {
            throw new BusinessException("Localização do registro de tempo inválida.");
        }
        if (timeRecord.getLatitude() != null && !isValidLatitude(timeRecord.getLatitude())) {
            throw new BusinessException("Latitude do registro de tempo inválida.");
        }
        if (timeRecord.getLongitude() != null && !isValidLongitude(timeRecord.getLongitude())) {
            throw new BusinessException("Longitude do registro de tempo inválida.");
        }
    }

    @Override
    public void validate(ComplianceAudit audit) {
        if (audit == null) {
            throw new BusinessException("Objeto ComplianceAudit não pode ser nulo.");
        }
        if (!isValidId(audit.getJourneyId())) {
            throw new BusinessException("ID da jornada da auditoria inválido.");
        }
        if (!isValidId(audit.getDriverId())) {
            throw new BusinessException("ID do motorista da auditoria inválido.");
        }
        if (audit.getAuditDate() == null || audit.getAuditDate().isAfter(LocalDate.now())) {
            throw new BusinessException("Data da auditoria inválida ou futura.");
        }
        if (audit.getStatus() == null) {
            throw new BusinessException("Status da auditoria não pode ser nulo.");
        }
        if (!isValidName(audit.getAuditorName())) {
            throw new BusinessException("Nome do auditor inválido.");
        }
        // Validações para violations, durations podem ser adicionadas aqui
    }

    @Override
    public void validate(MobileCommunication mobileCommunication) {
        if (mobileCommunication == null) {
            throw new BusinessException("Objeto MobileCommunication não pode ser nulo.");
        }
        if (!isValidId(mobileCommunication.getDriverId())) {
            throw new BusinessException("ID do motorista da comunicação móvel inválido.");
        }
        if (!isValidId(mobileCommunication.getJourneyId())) {
            throw new BusinessException("ID da jornada da comunicação móvel inválido.");
        }
        if (mobileCommunication.getTimestamp() == null) {
            throw new BusinessException("Timestamp da comunicação móvel não pode ser nulo.");
        }
        if (mobileCommunication.getEventType() == null) {
            throw new BusinessException("Tipo de evento da comunicação móvel não pode ser nulo.");
        }
        if (mobileCommunication.getLatitude() != null && !isValidLatitude(mobileCommunication.getLatitude())) {
            throw new BusinessException("Latitude da comunicação móvel inválida.");
        }
        if (mobileCommunication.getLongitude() != null && !isValidLongitude(mobileCommunication.getLongitude())) {
            throw new BusinessException("Longitude da comunicação móvel inválida.");
        }
        if (mobileCommunication.getDeviceId() == null || mobileCommunication.getDeviceId().trim().isEmpty()) {
            throw new BusinessException("ID do dispositivo da comunicação móvel não pode ser nulo ou vazio.");
        }
        // Validações para signalStrength e batteryLevel podem ser adicionadas aqui
    }
}
