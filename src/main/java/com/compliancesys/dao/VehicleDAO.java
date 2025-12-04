package com.compliancesys.dao;

import com.compliancesys.model.Vehicle;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Interface para a camada de acesso a dados (DAO) da entidade Vehicle.
 * Define as operações CRUD e de busca específicas para veículos.
 */
public interface VehicleDAO {

    /**
     * Cria um novo veículo no banco de dados.
     * @param vehicle Objeto Vehicle a ser criado.
     * @return O ID do veículo criado, ou -1 se a criação falhar.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    int create(Vehicle vehicle) throws SQLException;

    /**
     * Busca um veículo pelo seu ID.
     * @param id ID do veículo.
     * @return Um Optional contendo o Vehicle se encontrado, ou um Optional vazio.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    Optional<Vehicle> findById(int id) throws SQLException;

    /**
     * Busca um veículo pela sua placa.
     * @param plate Placa do veículo.
     * @return Um Optional contendo o Vehicle se encontrado, ou um Optional vazio.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    Optional<Vehicle> findByPlate(String plate) throws SQLException;

    /**
     * Retorna uma lista de todos os veículos registrados.
     * @return Uma lista de todos os veículos.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    List<Vehicle> findAll() throws SQLException;

    /**
     * Retorna uma lista de veículos associados a uma empresa específica.
     * @param companyId ID da empresa.
     * @return Uma lista de veículos da empresa.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    List<Vehicle> findByCompanyId(int companyId) throws SQLException;

    /**
     * Atualiza as informações de um veículo existente.
     * @param vehicle Objeto Vehicle com os dados atualizados.
     * @return true se o veículo foi atualizado com sucesso, false caso contrário.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    boolean update(Vehicle vehicle) throws SQLException;

    /**
     * Remove um veículo do banco de dados pelo seu ID.
     * @param id ID do veículo a ser removido.
     * @return true se o veículo foi removido com sucesso, false caso contrário.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    boolean delete(int id) throws SQLException;
}
