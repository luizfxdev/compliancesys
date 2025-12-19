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
    private static final Pattern CPF_PATTERN = Pattern.compile("^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$");
    private static final Pattern CNPJ_PATTERN = Pattern.compile("^\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}$");
    private static final Pattern PLATE_PATTERN = Pattern.compile("^[A-Z]{3}[0-9]{4}$|^[A-Z]{3}[0-9][A-Z][0-9]{2}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\(?[1-9]{2}\\)?[\\s-]?[9]?\\d{4}[\\s-]?\\d{4}$");

    @Override
    public boolean isValidId(int id) {
        return id > 0;
    }

    @Override
    public boolean isValidCpf(String cpf) {
        return cpf != null && !cpf.isEmpty() && CPF_PATTERN.matcher(cpf).matches();
    }

    @Override
    public boolean isValidCnpj(String cnpj) {
        return cnpj != null && !cnpj.isEmpty() && CNPJ_PATTERN.matcher(cnpj).matches();
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
        return plate != null && !plate.isEmpty() && PLATE_PATTERN.matcher(plate).matches();
    }

    @Override
    public boolean isValidDate(LocalDate date) {
        return date != null && !date.isAfter(LocalDate.now());
    }

    @Override
    public boolean isValidTime(String time) {
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
        return email != null && !email.isEmpty() && EMAIL_PATTERN.matcher(email).matches();
    }

    @Override
    public boolean isValidPhone(String phone) {
        return phone != null && !phone.isEmpty() && PHONE_PATTERN.matcher(phone).matches();
    }

    @Override
    public void validate(Driver driver) throws BusinessException {
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
        if (driver.getBirthDate() == null || driver.getBirthDate().isAfter(LocalDate.now().minusYears(18))) {
            throw new BusinessException("Data de nascimento inválida. Motorista deve ter pelo menos 18 anos.");
        }
        if (driver.getPhone() != null && !driver.getPhone().isEmpty() && !isValidPhone(driver.getPhone())) {
            throw new BusinessException("Telefone do motorista inválido.");
        }
        if (driver.getEmail() != null && !driver.getEmail().isEmpty() && !isValidEmail(driver.getEmail())) {
            throw new BusinessException("Email do motorista inválido.");
        }
    }

    @Override
    public void validate(Vehicle vehicle) throws BusinessException {
        if (vehicle == null) {
            throw new BusinessException("Objeto Vehicle não pode ser nulo.");
        }
        if (!isValidPlate(vehicle.getPlate())) {
            throw new BusinessException("Placa do veículo inválida.");
        }
        if (!isValidName(vehicle.getModel())) {
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
    public void validate(Company company) throws BusinessException {
        if (company == null) {
            throw new BusinessException("Objeto Company não pode ser nulo.");
        }
        if (!isValidCnpj(company.getCnpj())) {
            throw new BusinessException("CNPJ da empresa inválido.");
        }
        if (!isValidName(company.getLegalName())) {
            throw new BusinessException("Razão social da empresa inválida.");
        }
    }

    @Override
    public void validate(Journey journey) throws BusinessException {
        if (journey == null) {
            throw new BusinessException("Objeto Journey não pode ser nulo.");
        }
        if (!isValidId(journey.getDriverId())) {
            throw new BusinessException("ID do motorista da jornada inválido.");
        }
        if (journey.getJourneyDate() == null) {
            throw new BusinessException("Data da jornada não pode ser nula.");
        }
    }

    @Override
    public void validate(TimeRecord timeRecord) throws BusinessException {
        if (timeRecord == null) {
            throw new BusinessException("Objeto TimeRecord não pode ser nulo.");
        }
        if (!isValidId(timeRecord.getDriverId())) {
            throw new BusinessException("ID do motorista do registro de tempo inválido.");
        }
        if (timeRecord.getRecordTime() == null) {
            throw new BusinessException("Hora do registro de tempo não pode ser nula.");
        }
        if (timeRecord.getEventType() == null) {
            throw new BusinessException("Tipo de evento do registro de tempo não pode ser nulo.");
        }
        if (timeRecord.getLatitude() != null && !isValidLatitude(timeRecord.getLatitude())) {
            throw new BusinessException("Latitude do registro de tempo inválida.");
        }
        if (timeRecord.getLongitude() != null && !isValidLongitude(timeRecord.getLongitude())) {
            throw new BusinessException("Longitude do registro de tempo inválida.");
        }
    }

    @Override
    public void validate(ComplianceAudit audit) throws BusinessException {
        if (audit == null) {
            throw new BusinessException("Objeto ComplianceAudit não pode ser nulo.");
        }
        if (!isValidId(audit.getJourneyId())) {
            throw new BusinessException("ID da jornada da auditoria inválido.");
        }
        if (audit.getAuditDate() == null) {
            throw new BusinessException("Data da auditoria não pode ser nula.");
        }
        if (audit.getStatus() == null) {
            throw new BusinessException("Status da auditoria não pode ser nulo.");
        }
    }

    @Override
    public void validate(MobileCommunication mobileCommunication) throws BusinessException {
        if (mobileCommunication == null) {
            throw new BusinessException("Objeto MobileCommunication não pode ser nulo.");
        }
        if (!isValidId(mobileCommunication.getDriverId())) {
            throw new BusinessException("ID do motorista da comunicação móvel inválido.");
        }
        if (mobileCommunication.getTimestamp() == null) {
            throw new BusinessException("Timestamp da comunicação móvel não pode ser nulo.");
        }
        if (mobileCommunication.getDeviceId() == null || mobileCommunication.getDeviceId().trim().isEmpty()) {
            throw new BusinessException("ID do dispositivo da comunicação móvel não pode ser nulo ou vazio.");
        }
        if (mobileCommunication.getLatitude() != null && !isValidLatitude(mobileCommunication.getLatitude())) {
            throw new BusinessException("Latitude da comunicação móvel inválida.");
        }
        if (mobileCommunication.getLongitude() != null && !isValidLongitude(mobileCommunication.getLongitude())) {
            throw new BusinessException("Longitude da comunicação móvel inválida.");
        }
    }
}