package com.compliancesys.util;

import java.time.LocalDate;

import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.Company;
import com.compliancesys.model.ComplianceAudit;
import com.compliancesys.model.Driver;
import com.compliancesys.model.Journey;
import com.compliancesys.model.MobileCommunication;
import com.compliancesys.model.TimeRecord; // Import adicionado
import com.compliancesys.model.Vehicle;

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

    // Métodos de validação de modelos
    void validate(Driver driver) throws BusinessException;
    void validate(Company company) throws BusinessException;
    void validate(Journey journey) throws BusinessException;
    void validate(MobileCommunication mobileCommunication) throws BusinessException;
    void validate(ComplianceAudit audit) throws BusinessException;
    void validate(TimeRecord timeRecord) throws BusinessException;
    void validate(Vehicle vehicle) throws BusinessException; 
}
