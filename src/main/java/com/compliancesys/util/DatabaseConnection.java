package com.compliancesys.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe utilitária para gerenciar a conexão com o banco de dados.
 * Esta é uma implementação básica para fins de exemplo.
 * Em um ambiente de produção, considere usar um pool de conexões (ex: HikariCP, c3p0).
 */
public class DatabaseConnection {

    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/compliancesys_db"; // Substitua pelo seu URL
    private static final String DB_USER = "seu_usuario"; // Substitua pelo seu usuário
    private static final String DB_PASSWORD = "sua_senha"; // Substitua pela sua senha

    // Bloco estático para carregar o driver JDBC uma vez
    static {
        try {
            // Carrega o driver JDBC para PostgreSQL
            // Para H2, não é estritamente necessário Class.forName() em versões recentes,
            // mas é uma boa prática para garantir que o driver esteja disponível.
            Class.forName("org.postgresql.Driver");
            // Class.forName("org.h2.Driver"); // Opcional, mas bom para clareza se for usar H2
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Driver JDBC não encontrado.", e);
            throw new RuntimeException("Falha ao carregar o driver JDBC.", e);
        }
    }

    /**
     * Obtém uma nova conexão com o banco de dados principal (PostgreSQL).
     *
     * @return Uma instância de Connection.
     * @throws SQLException Se ocorrer um erro ao estabelecer a conexão.
     */
    public static Connection getConnection() throws SQLException {
        LOGGER.log(Level.INFO, "Tentando estabelecer conexão com o banco de dados principal...");
        try {
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            LOGGER.log(Level.INFO, "Conexão com o banco de dados principal estabelecida com sucesso.");
            return connection;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao obter conexão com o banco de dados principal: " + e.getMessage(), e);
            throw e; // Relança a exceção para ser tratada por quem chamou
        }
    }

    /**
     * Obtém uma nova conexão com um banco de dados H2 em memória para testes.
     *
     * @return Uma instância de Connection para o banco de dados de teste.
     * @throws SQLException Se ocorrer um erro ao estabelecer a conexão.
     */
    public static Connection getTestConnection() throws SQLException {
        LOGGER.log(Level.INFO, "Tentando estabelecer conexão com o banco de dados H2 em memória para testes...");
        // O H2 cria um banco de dados em memória se o URL começar com "jdbc:h2:mem:"
        // O ";DB_CLOSE_DELAY=-1" mantém o banco de dados aberto enquanto a JVM estiver ativa,
        // o que é útil para múltiplos testes na mesma sessão.
        String H2_URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
        String H2_USER = "sa";
        String H2_PASSWORD = ""; // Sem senha para testes em memória

        try {
            Connection connection = DriverManager.getConnection(H2_URL, H2_USER, H2_PASSWORD);
            LOGGER.log(Level.INFO, "Conexão com o banco de dados H2 estabelecida com sucesso para testes.");
            return connection;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao obter conexão com o banco de dados H2 para testes: " + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Fecha uma conexão com o banco de dados.
     *
     * @param connection A conexão a ser fechada.
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                LOGGER.log(Level.INFO, "Conexão com o banco de dados fechada com sucesso.");
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Erro ao fechar conexão com o banco de dados: " + e.getMessage(), e);
            }
        }
    }
}
