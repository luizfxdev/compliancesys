package com.compliancesys.service;

import com.compliancesys.model.Vehicle;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Interface para a camada de serviço da entidade Vehicle.
 * Define as operações de negócio para gerenciar veículos.
 */
public interface VehicleService {

    /**
     * Registra um novo veículo no sistema.
     * Realiza validações de negócio antes de persistir o veículo.
     * @param vehicle Objeto Vehicle a ser registrado.
     * @return O ID do veículo registrado.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     * @throws IllegalArgumentException Se os dados do veículo forem inválidos.
     */
    int registerVehicle(Vehicle vehicle) throws SQLException, IllegalArgumentException;

    /**
     * Busca um veículo pelo seu ID.
     * @param vehicleId ID do veículo.
     * @return Um Optional contendo o Vehicle se encontrado, ou um Optional vazio.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    Optional<Vehicle> getVehicleById(int vehicleId) throws SQLException;

    /**
     * Busca um veículo pela sua placa.
     * @param plate Placa do veículo.
     * @return Um Optional contendo o Vehicle se encontrado, ou um Optional vazio.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    Optional<Vehicle> getVehicleByPlate(String plate) throws SQLException;

    /**
     * Retorna uma lista de todos os veículos registrados.
     * @return Uma lista de todos os veículos.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    List<Vehicle> getAllVehicles() throws SQLException;

    /**
     * Retorna uma lista de veículos associados a uma empresa.
     * @param companyId ID da empresa.
     * @return Uma lista de veículos da empresa.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    List<Vehicle> getVehiclesByCompanyId(int companyId) throws SQLException;

    /**
     * Atualiza as informações de um veículo existente.
     * Realiza validações de negócio antes de atualizar.
     * @param vehicle Objeto Vehicle com os dados atualizados.
     * @return true se o veículo foi atualizado com sucesso, false caso contrário.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     * @throws IllegalArgumentException Se os dados do veículo forem inválidos.
     */
    boolean updateVehicle(Vehicle vehicle) throws SQLException, IllegalArgumentException;

    /**
     * Remove um veículo do sistema pelo seu ID.
     * @param vehicleId ID do veículo a ser removido.
     * @return true se o veículo foi removido com sucesso, false caso contrário.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    boolean deleteVehicle(int vehicleId) throws SQLException;
}
