package com.compliancesys.dao;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.compliancesys.model.Driver;

public interface DriverDAO {
    int create(Driver driver) throws SQLException;
    Optional<Driver> findById(int id) throws SQLException;
    List<Driver> findAll() throws SQLException;
    boolean update(Driver driver) throws SQLException;
    boolean delete(int id) throws SQLException;
    Optional<Driver> findByCpf(String cpf) throws SQLException;
    Optional<Driver> findByLicenseNumber(String licenseNumber) throws SQLException;
    List<Driver> findByCompanyId(int companyId) throws SQLException;
    List<Driver> findByName(String name) throws SQLException;

    // Métodos que estavam faltando ou com problemas na interface
    List<Driver> findByLicenseCategory(String licenseCategory) throws SQLException;
    List<Driver> findByLicenseExpirationBefore(LocalDate date) throws SQLException;
    List<Driver> findByBirthDateBetween(LocalDate startDate, LocalDate endDate) throws SQLException;
    Optional<Driver> findByPhone(String phone) throws SQLException;
    Optional<Driver> findByEmail(String email) throws SQLException;

    // Métodos default para compatibilidade com a camada de serviço (se necessário)
    default Optional<Driver> getDriverByCpf(String cpf) throws SQLException {
        return findByCpf(cpf);
    }

    default Optional<Driver> getDriverByLicenseNumber(String licenseNumber) throws SQLException {
        return findByLicenseNumber(licenseNumber);
    }

    default List<Driver> getDriversByCompanyId(int companyId) throws SQLException {
        return findByCompanyId(companyId);
    }

    default List<Driver> getDriversByName(String name) throws SQLException {
        return findByName(name);
    }

    default List<Driver> getDriversByLicenseCategory(String licenseCategory) throws SQLException {
        return findByLicenseCategory(licenseCategory);
    }

    default List<Driver> getDriversByLicenseExpirationBefore(LocalDate date) throws SQLException {
        return findByLicenseExpirationBefore(date);
    }

    default List<Driver> getDriversByBirthDateBetween(LocalDate startDate, LocalDate endDate) throws SQLException {
        return findByBirthDateBetween(startDate, endDate);
    }

    default Optional<Driver> getDriverByPhone(String phone) throws SQLException {
        return findByPhone(phone);
    }

    default Optional<Driver> getDriverByEmail(String email) throws SQLException {
        return findByEmail(email);
    }
}
