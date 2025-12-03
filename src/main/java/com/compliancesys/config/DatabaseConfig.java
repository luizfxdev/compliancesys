package com.compliancesys.config;

import java.io.IOException;       // Importa para lidar com exceções de I/O.
import java.io.InputStream;       // Importa para ler o arquivo de propriedades.
import java.sql.Connection;       // Importa para a interface Connection do JDBC.
import java.sql.DriverManager;    // Importa para gerenciar drivers JDBC.
import java.sql.SQLException;     // Importa para lidar com exceções SQL.
import java.util.Properties;      // Importa para carregar propriedades de um arquivo.
import java.util.logging.Level;   // Importa para níveis de log.
import java.util.logging.Logger;  // Importa para logging.

/**
 * Classe de configuração do banco de dados.
 * Carrega as propriedades de conexão e fornece uma conexão JDBC.
 * Implementa o padrão Singleton para garantir uma única instância.
 */
public class DatabaseConfig {
    private static final Logger LOGGER = Logger.getLogger(DatabaseConfig.class.getName()); // Logger para a classe.
    private static DatabaseConfig instance; // Instância única do Singleton.
    private Properties properties; // Propriedades de conexão do banco de dados.

    /**
     * Construtor privado para o Singleton.
     * Carrega as propriedades do arquivo 'database.properties'.
     */
    private DatabaseConfig() {
        properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                LOGGER.log(Level.SEVERE, "Arquivo 'database.properties' não encontrado no classpath.");
                throw new IOException("Arquivo 'database.properties' não encontrado.");
            }
            properties.load(input);
            // Carrega o driver JDBC.
            Class.forName(properties.getProperty("db.driver"));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erro ao carregar o arquivo de propriedades do banco de dados: " + e.getMessage(), e);
            throw new RuntimeException("Falha ao carregar a configuração do banco de dados.", e);
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Driver JDBC não encontrado: " + e.getMessage(), e);
            throw new RuntimeException("Falha ao carregar o driver JDBC.", e);
        }
    }

    /**
     * Retorna a instância única de DatabaseConfig (Singleton).
     * @return A instância de DatabaseConfig.
     */
    public static synchronized DatabaseConfig getInstance() {
        if (instance == null) {
            instance = new DatabaseConfig();
        }
        return instance;
    }

    /**
     * Obtém uma nova conexão com o banco de dados.
     * @return Uma conexão JDBC.
     * @throws SQLException Se ocorrer um erro ao obter a conexão.
     */
    public Connection getConnection() throws SQLException {
        String url = properties.getProperty("db.url");
        String user = properties.getProperty("db.username");
        String password = properties.getProperty("db.password");
        return DriverManager.getConnection(url, user, password);
    }

    /**
     * Fecha uma conexão com o banco de dados.
     * @param connection A conexão a ser fechada.
     */
    public void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Erro ao fechar a conexão com o banco de dados: " + e.getMessage(), e);
            }
        }
    }
}
