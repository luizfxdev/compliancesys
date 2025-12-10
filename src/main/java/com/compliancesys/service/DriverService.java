package com.compliancesys.service;

import java.sql.SQLException;
import java.time.LocalDate; // Import adicionado
import java.util.List;
import java.util.Optional;

import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.Driver;

/**
 * Interface para serviços relacionados a motoristas.
 * Define as operações de negócio para gerenciar motoristas.
 */
public interface DriverService {

    /**
     * Registra um novo motorista no sistema.
     *
     * @param driver O objeto Driver a ser registrado.
     * @return O objeto Driver recém-criado, com o ID gerado.
     * @throws BusinessException Se houver uma regra de negócio violada (ex: CPF duplicado, dados inválidos).
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    // Alterado de registerDriver para createDriver para corresponder aos testes
    Driver createDriver(Driver driver) throws BusinessException, SQLException;

    /**
     * Busca um motorista pelo seu ID.
     *
     * @param driverId O ID do motorista.
     * @return Um Optional contendo o Driver se encontrado, ou um Optional vazio.
     * @throws BusinessException Se o ID for inválido.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    Optional<Driver> getDriverById(int driverId) throws BusinessException, SQLException;

    /**
     * Busca um motorista pelo seu CPF.
     *
     * @param cpf O CPF do motorista.
     * @return Um Optional contendo o Driver se encontrado, ou um Optional vazio.
     * @throws BusinessException Se o CPF for inválido.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    Optional<Driver> getDriverByCpf(String cpf) throws BusinessException, SQLException;

    /**
     * Busca todos os motoristas.
     *
     * @return Uma lista de todos os motoristas.
     * @throws BusinessException Se ocorrer um erro de negócio.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    List<Driver> getAllDrivers() throws BusinessException, SQLException;

    /**
     * Atualiza os dados de um motorista existente.
     *
     * @param driver O objeto Driver com os dados atualizados.
     * @return O objeto Driver atualizado.
     * @throws BusinessException Se houver uma regra de negócio violada ou o motorista não for encontrado.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    // Alterado o retorno de boolean para Driver para corresponder aos testes
    Driver updateDriver(Driver driver) throws BusinessException, SQLException;

    /**
     * Exclui um motorista pelo seu ID.
     *
     * @param driverId O ID do motorista a ser excluído.
     * @return true se o motorista foi excluído com sucesso, false caso contrário.
     * @throws BusinessException Se o ID for inválido ou o motorista não puder ser excluído.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    boolean deleteDriver(int driverId) throws BusinessException, SQLException;

    // Métodos adicionais que os testes ou outras partes do sistema podem precisar
    Optional<Driver> getDriverByLicenseNumber(String licenseNumber) throws BusinessException, SQLException;
    List<Driver> getDriversByCompanyId(int companyId) throws BusinessException, SQLException;
    List<Driver> getDriversByName(String name) throws BusinessException, SQLException;
    List<Driver> getDriversByLicenseCategory(String licenseCategory) throws BusinessException, SQLException;
    List<Driver> getDriversByLicenseExpirationBefore(LocalDate date) throws BusinessException, SQLException;
    List<Driver> getDriversByBirthDateBetween(LocalDate startDate, LocalDate endDate) throws BusinessException, SQLException;
    Optional<Driver> getDriverByPhone(String phone) throws BusinessException, SQLException;
    Optional<Driver> getDriverByEmail(String email) throws BusinessException, SQLException;
}
