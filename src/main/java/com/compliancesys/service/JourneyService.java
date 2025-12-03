package com.compliancesys.service;

import com.compliancesys.model.Journey; // Garante que o POJO Journey está sendo importado.
import com.compliancesys.model.TimeRecord; // Garante que o POJO TimeRecord está sendo importado.
import java.sql.SQLException;           // Garante que SQLException está sendo importado.
import java.time.LocalDate;             // Garante que LocalDate está sendo importado.
import java.util.List;                  // Garante que List está sendo importado.
import java.util.Optional;              // Garante que Optional está sendo importado.

/**
 * Interface para a camada de serviço da entidade Journey.
 * Define as operações de negócio para gerenciar jornadas de motoristas,
 * incluindo regras da Lei 13.103 e cálculos de jornada.
 */
public interface JourneyService {

    /**
     * Cria uma nova jornada no sistema.
     * @param journey Objeto Journey a ser criado.
     * @return O ID da jornada criada.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     * @throws IllegalArgumentException Se os dados da jornada forem inválidos.
     */
    int createJourney(Journey journey) throws SQLException, IllegalArgumentException;

    /**
     * Busca uma jornada pelo seu ID.
     * @param journeyId ID da jornada.
     * @return Um Optional contendo a Journey se encontrada, ou um Optional vazio.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    Optional<Journey> getJourneyById(int journeyId) throws SQLException;

    /**
     * Busca uma jornada pelo ID do motorista e data da jornada.
     * @param driverId ID do motorista.
     * @param journeyDate Data da jornada.
     * @return Um Optional contendo a Journey se encontrada, ou um Optional vazio.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    Optional<Journey> getJourneyByDriverIdAndDate(int driverId, LocalDate journeyDate) throws SQLException;

    /**
     * Retorna uma lista de todas as jornadas registradas.
     * @return Uma lista de todas as jornadas.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    List<Journey> getAllJourneys() throws SQLException;

    /**
     * Retorna uma lista de todas as jornadas de um motorista específico.
     * @param driverId ID do motorista.
     * @return Uma lista de jornadas para o motorista especificado.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    List<Journey> getJourneysByDriverId(int driverId) throws SQLException;

    /**
     * Atualiza as informações de uma jornada existente.
     * @param journey Objeto Journey com os dados atualizados.
     * @return true se a jornada foi atualizada com sucesso, false caso contrário.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     * @throws IllegalArgumentException Se os dados da jornada forem inválidos.
     */
    boolean updateJourney(Journey journey) throws SQLException, IllegalArgumentException;

    /**
     * Remove uma jornada do sistema pelo seu ID.
     * @param journeyId ID da jornada a ser removida.
     * @return true se a jornada foi removida com sucesso, false caso contrário.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    boolean deleteJourney(int journeyId) throws SQLException;

    /**
     * Calcula e audita a conformidade de uma jornada com base nos registros de ponto.
     * Este método aplica as regras de negócio da Lei 13.103.
     * @param journeyId ID da jornada a ser auditada.
     * @param timeRecords Lista de registros de ponto associados à jornada.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados durante a auditoria.
     */
    void calculateAndAuditJourney(int journeyId, List<TimeRecord> timeRecords) throws SQLException;
}
