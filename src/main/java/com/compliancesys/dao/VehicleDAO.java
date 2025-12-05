package com.compliancesys.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.compliancesys.model.Vehicle;

/**
 * Interface para o Data Access Object (DAO) de Vehicle.
 * Define as operações CRUD e de busca específicas para veículos.
 */
public interface VehicleDAO {

    /**
     * Insere um novo veículo no banco de dados.
     * @param vehicle O objeto Vehicle a ser inserido.
     * @return O ID gerado para o novo veículo. // CORRIGIDO: Retorna int
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    int create(Vehicle vehicle) throws SQLException; // CORRIGIDO: Retorna int

    /**
     * Busca um veículo pelo seu ID.
     * @param id O ID do veículo.
     * @return Um Optional contendo o Vehicle se encontrado, ou um Optional vazio.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    Optional<Vehicle> findById(int id) throws SQLException;

    /**
     * Busca um veículo pela sua placa.
     * @param plate A placa do veículo.
     * @return Um Optional contendo o Vehicle se encontrado, ou um Optional vazio.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    Optional<Vehicle> findByPlate(String plate) throws SQLException;

    /**
     * Busca todos os veículos.
     * @return Uma lista de todos os veículos.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    List<Vehicle> findAll() throws SQLException;

    /**
     * Atualiza um veículo existente no banco de dados.
     * @param vehicle O objeto Vehicle com os dados atualizados.
     * @return true se o veículo foi atualizado com sucesso, false caso contrário.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    boolean update(Vehicle vehicle) throws SQLException;

    /**
     * Deleta um veículo pelo seu ID.
     * @param id O ID do veículo a ser deletado.
     * @return true se o veículo foi deletado com sucesso, false caso contrário.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    boolean delete(int id) throws SQLException;
}
