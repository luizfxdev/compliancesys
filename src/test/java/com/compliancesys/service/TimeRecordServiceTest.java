package com.compliancesys.service;

import com.compliancesys.dao.CompanyDAO;
import com.compliancesys.dao.DriverDAO;
import com.compliancesys.dao.JourneyDAO;
import com.compliancesys.dao.TimeRecordDAO;
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.Company;
import com.compliancesys.model.Driver;
import com.compliancesys.model.Journey;
import com.compliancesys.model.TimeRecord;
import com.compliancesys.service.impl.TimeRecordServiceImpl;
import com.compliancesys.util.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class TimeRecordServiceTest {

    @Mock
    private TimeRecordDAO timeRecordDAO;
    @Mock
    private DriverDAO driverDAO;
    @Mock
    private CompanyDAO companyDAO;
    @Mock
    private JourneyDAO journeyDAO;
    @Mock
    private Validator validator;

    @InjectMocks
    private TimeRecordServiceImpl timeRecordService;

    private Company testCompany;
    private Driver testDriver;
    private Journey testJourney;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Configurações padrão para o validator
        when(validator.isValidId(anyInt())).thenReturn(true);
        when(validator.isValidRecordType(anyString())).thenReturn(true);
        when(validator.isValidDateTime(any(LocalDateTime.class))).thenReturn(true);
        when(validator.isValidDescription(anyString())).thenReturn(true);
        when(validator.isValidDate(any(LocalDate.class))).thenReturn(true);

        // Configurações padrão para DAOs de dependência
        testCompany = new Company(1, "Empresa Teste", "12345678000190", "Rua Teste, 123", "11987654321", "teste@empresa.com", LocalDateTime.now(), LocalDateTime.now());
        testDriver = new Driver(1, 1, "Nome Motorista", "12345678901", "12345678901", "B", null, null, "motorista@email.com", "11987654321", "Endereço Motorista", LocalDateTime.now(), LocalDateTime.now());
        testJourney = new Journey(1, 1, 1, 1, "Origem", "Destino", LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1), "Concluída", LocalDateTime.now(), LocalDateTime.now());

        when(companyDAO.findById(anyInt())).thenReturn(Optional.of(testCompany));
        when(driverDAO.findById(anyInt())).thenReturn(Optional.of(testDriver));
        when(journeyDAO.findById(anyInt())).thenReturn(Optional.of(testJourney));

