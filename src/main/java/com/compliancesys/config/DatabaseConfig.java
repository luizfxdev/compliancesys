package com.compliancesys.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConfig {

    private static final Logger LOGGER = Logger.getLogger(DatabaseConfig.class.getName());
    private static final String DB_PROPERTIES_FILE = "database.properties";
    private static Properties properties;

    // --- ADICIONADO: Implementação do padrão Singleton ---
    private static DatabaseConfig instance; // Instância única
    private Connection connection; // Conexão para a instância Singleton

    // Construtor privado para evitar instanciação externa
    private DatabaseConfig() {
        loadProperties();
        // A conexão para a instância singleton será estabelecida sob demanda
    }

    // Método estático para obter a instância única
    public static synchronized DatabaseConfig getInstance() {
        if (instance == null) {
            instance = new DatabaseConfig();
        }
        return instance;
    }

    // Método para obter a conexão da instância Singleton
    public Connection getConnection() throws SQLException {
        if (this.connection == null || this.connection.isClosed()) {
            try {
                Class.forName(properties.getProperty("db.driver"));
                this.connection = DriverManager.getConnection(
                        properties.getProperty("db.url"),
                        properties.getProperty("db.username"),
                        properties.getProperty("db.password")
                );
                this.connection.setAutoCommit(false); // Gerenciamento manual de transações
                LOGGER.log(Level.FINE, "Conexão com o banco de dados estabelecida com sucesso pela instância Singleton.");
            } catch (ClassNotFoundException e) {
                LOGGER.log(Level.SEVERE, "Driver do banco de dados não encontrado: " + e.getMessage(), e);
                throw new SQLException("Driver do banco de dados não encontrado.", e);
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Erro ao obter conexão com o banco de dados pela instância Singleton: " + e.getMessage(), e);
                throw e;
            }
        }
        return this.connection;
    }

    // Método para fechar a conexão da instância Singleton
    public void closeConnection() {
        if (this.connection != null) {
            try {
                this.connection.close();
                LOGGER.log(Level.INFO, "Conexão com o banco de dados da instância Singleton fechada com sucesso.");
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Erro ao fechar conexão com o banco de dados da instância Singleton: " + e.getMessage(), e);
            } finally {
                this.connection = null; // Garante que uma nova conexão será criada na próxima vez
            }
        }
    }
    // --- FIM: Implementação do padrão Singleton ---


    private static void loadProperties() {
        if (properties == null) {
            properties = new Properties();
            try (InputStream input = DatabaseConfig.class.getClassLoader().getResourceAsStream(DB_PROPERTIES_FILE)) {
                if (input == null) {
                    LOGGER.log(Level.SEVERE, "Arquivo de propriedades do banco de dados '" + DB_PROPERTIES_FILE + "' não encontrado.");
                    throw new IOException("Arquivo de propriedades do banco de dados não encontrado.");
                }
                properties.load(input);
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "Erro ao carregar propriedades do banco de dados: " + ex.getMessage(), ex);
                throw new RuntimeException("Não foi possível carregar as propriedades do banco de dados.", ex);
            }
        }
    }

    // --- Métodos estáticos originais (para compatibilidade ou uso direto) ---
    // Note que estes métodos estáticos de conexão e fechamento são independentes da instância Singleton.
    // Se o seu código de teste usa DatabaseConnection.getTestConnection(), ele não usará o Singleton.
    // Se o seu código de teste usa DatabaseConfig.getInstance().getConnection(), ele usará o Singleton.
    // É importante ser consistente.

    /**
     * Obtém uma nova conexão com o banco de dados.
     * Este método não usa o padrão Singleton e sempre retorna uma nova conexão.
     * @return Uma nova conexão com o banco de dados.
     * @throws SQLException Se ocorrer um erro ao obter a conexão.
     */
    public static Connection getNewConnection() throws SQLException {
        loadProperties();
        try {
            Class.forName(properties.getProperty("db.driver"));
            Connection connection = DriverManager.getConnection(
                    properties.getProperty("db.url"),
                    properties.getProperty("db.username"),
                    properties.getProperty("db.password")
            );
            connection.setAutoCommit(false); // Gerenciamento manual de transações
            LOGGER.log(Level.FINE, "Conexão com o banco de dados estabelecida com sucesso.");
            return connection;
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Driver do banco de dados não encontrado: " + e.getMessage(), e);
            throw new SQLException("Driver do banco de dados não encontrado.", e);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao obter conexão com o banco de dados: " + e.getMessage(), e);
            throw e; // Relança a exceção para ser tratada por quem chamou
        }
    }

    /**
     * Fecha os recursos do banco de dados (Connection, Statement, ResultSet) de forma segura.
     * @param conn A conexão a ser fechada.
     * @param stmt O statement a ser fechado.
     * @param rs O result set a ser fechado.
     */
    public static void closeResources(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Erro ao fechar recursos do banco de dados.", ex);
        }
    }

    /**
     * Sobrecarga para fechar Connection e Statement.
     * @param conn A conexão a ser fechada.
     * @param stmt O statement a ser fechado.
     */
    public static void closeResources(Connection conn, Statement stmt) {
        closeResources(conn, stmt, null);
    }

    /**
     * Sobrecarga para fechar apenas a Connection.
     * @param conn A conexão a ser fechada.
     */
    public static void closeResources(Connection conn) {
        closeResources(conn, null, null);
    }
}
