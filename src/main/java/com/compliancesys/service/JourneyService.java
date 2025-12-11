package com.compliancesys.service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.Journey;
import com.compliancesys.model.TimeRecord;

/**
 * Interface para serviços relacionados a jornadas de trabalho.
 * Define as operações de negócio para gerenciar jornadas.
 */
public interface JourneyService {

    /**
     * Cria uma nova jornada no sistema.
     *
     * @param journey O objeto Journey a ser criado.
     * @return O objeto Journey criado, com o ID gerado.
     * @throws BusinessException Se houver uma regra de negócio violada.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    Journey createJourney(Journey journey) throws BusinessException, SQLException;

    /**
     * Busca uma jornada pelo seu ID.
     *
     * @param journeyId O ID da jornada.
     * @return Um Optional contendo a Journey se encontrada, ou um Optional vazio.
     * @throws BusinessException Se o ID for inválido.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    Optional<Journey> getJourneyById(int journeyId) throws BusinessException, SQLException;

    /**
     * Busca todas as jornadas.
     *
     * @return Uma lista de todas as jornadas.
     * @throws BusinessException Se ocorrer um erro de negócio.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    List<Journey> getAllJourneys() throws BusinessException, SQLException;

    /**
     * Busca jornadas por ID do motorista.
     *
     * @param driverId O ID do motorista.
     * @return Uma lista de jornadas associadas ao motorista.
     * @throws BusinessException Se o ID do motorista for inválido.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    List<Journey> getJourneysByDriverId(int driverId) throws BusinessException, SQLException;

    /**
     * Busca uma jornada específica de um motorista em uma determinada data.
     *
     * @param driverId O ID do motorista.
     * @param journeyDate A data da jornada.
     * @return Um Optional contendo a Journey se encontrada, ou um Optional vazio.
     * @throws BusinessException Se os parâmetros forem inválidos.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    Optional<Journey> getJourneyByDriverIdAndDate(int driverId, LocalDate journeyDate) throws BusinessException, SQLException;

    /**
     * Atualiza os dados de uma jornada existente.
     *
     * @param journey O objeto Journey com os dados atualizados.
     * @return O objeto Journey atualizado.
     * @throws BusinessException Se houver uma regra de negócio violada ou a jornada não for encontrada.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    Journey updateJourney(Journey journey) throws BusinessException, SQLException;

    /**
     * Deleta uma jornada pelo seu ID.
     *
     * @param journeyId O ID da jornada a ser deletada.
     * @return true se a jornada foi deletada com sucesso, false caso contrário.
     * @throws BusinessException Se o ID for inválido ou a jornada não for encontrada.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    boolean deleteJourney(int journeyId) throws BusinessException, SQLException;

    /**
     * Calcula e audita a jornada de um motorista com base em seus registros de ponto.
     *
     * @param driverId O ID do motorista.
     * @param timeRecords A lista de registros de ponto do motorista para a jornada.
     * @return O objeto Journey calculado e auditado.
     * @throws BusinessException Se os dados de entrada forem inválidos ou houver um erro de negócio.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    Journey calculateAndAuditJourney(int driverId, List<TimeRecord> timeRecords) throws BusinessException, SQLException;
}
