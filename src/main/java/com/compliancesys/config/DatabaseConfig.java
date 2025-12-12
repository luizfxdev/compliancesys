package com.compliancesys.config;

<<<<<<< Updated upstream
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Classe de configuração do banco de dados usando HikariCP como pool de conexões.
 * Implementa o padrão Singleton para garantir uma única instância do DataSource.
 * As configurações do banco de dados são carregadas de um arquivo de propriedades.
=======
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource; // Importe javax.sql.DataSource
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe de configuração do banco de dados para o sistema ComplianceSys.
 * Gerencia o carregamento das propriedades de conexão e o estabelecimento de conexões
 * utilizando HikariCP para pooling de conexões. Implementa o padrão Singleton.
>>>>>>> Stashed changes
 */
public class DatabaseConfig {

    private static final Logger LOGGER = Logger.getLogger(DatabaseConfig.class.getName());
<<<<<<< Updated upstream
    private static final String DB_PROPERTIES_FILE = "database.properties";

    private static DatabaseConfig instance;
    private final HikariDataSource dataSource;
    private static Properties properties; // Para carregar as propriedades do arquivo

    /**
     * Construtor privado para implementar Singleton.
     * Carrega as propriedades do arquivo e inicializa o HikariCP DataSource.
     */
    private DatabaseConfig() {
        loadProperties(); // Carrega as propriedades do arquivo

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(properties.getProperty("db.url"));
        config.setUsername(properties.getProperty("db.username"));
        config.setPassword(properties.getProperty("db.password"));
        config.setDriverClassName(properties.getProperty("db.driver")); // É bom definir o driver explicitamente

        // Configurações do pool (podem vir do arquivo de propriedades também)
        config.setMaximumPoolSize(Integer.parseInt(properties.getProperty("hikari.maximumPoolSize", "10")));
        config.setMinimumIdle(Integer.parseInt(properties.getProperty("hikari.minimumIdle", "2")));
        config.setConnectionTimeout(Long.parseLong(properties.getProperty("hikari.connectionTimeout", "30000")));
        config.setIdleTimeout(Long.parseLong(properties.getProperty("hikari.idleTimeout", "600000")));
        config.setMaxLifetime(Long.parseLong(properties.getProperty("hikari.maxLifetime", "1800000")));

        // Se você sempre quer autoCommit=false, configure aqui no pool
        config.setAutoCommit(Boolean.parseBoolean(properties.getProperty("hikari.autoCommit", "false")));

        // Propriedades adicionais do PostgreSQL (também podem vir do arquivo)
        // Note que as propriedades do DataSource podem ter nomes ligeiramente diferentes entre as versões do HikariCP
        // e drivers JDBC. As que você usou são geralmente compatíveis.
        config.addDataSourceProperty("cachePrepStmts", properties.getProperty("hikari.cachePrepStmts", "true"));
        config.addDataSourceProperty("prepStmtCacheSize", properties.getProperty("hikari.prepStmtCacheSize", "250"));
        config.addDataSourceProperty("prepStmtCacheSqlLimit", properties.getProperty("hikari.prepStmtCacheSqlLimit", "2048"));

        try {
            this.dataSource = new HikariDataSource(config);
            LOGGER.log(Level.INFO, "HikariCP DataSource inicializado com sucesso.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao inicializar HikariCP DataSource: " + e.getMessage(), e);
            throw new RuntimeException("Não foi possível inicializar o pool de conexões.", e);
=======
    private static final String PROPERTIES_FILE = "database.properties";

    // Instância única do DataSource (HikariDataSource)
    private static HikariDataSource dataSource;

    // Construtor privado para implementar o padrão Singleton
    private DatabaseConfig() {
        // Impede a instanciação externa
    }

    /**
     * Inicializa o HikariDataSource a partir das propriedades.
     * Este método deve ser chamado uma única vez, idealmente na inicialização da aplicação.
     */
    public static synchronized void initializeDataSource() {
        if (dataSource == null) {
            Properties dbProperties = new Properties();
            try (InputStream input = DatabaseConfig.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
                if (input == null) {
                    LOGGER.log(Level.SEVERE, "Desculpe, não foi possível encontrar " + PROPERTIES_FILE);
                    throw new IOException("Arquivo de propriedades do banco de dados não encontrado.");
                }
                dbProperties.load(input);

                HikariConfig config = new HikariConfig();
                config.setJdbcUrl(dbProperties.getProperty("db.url"));
                config.setUsername(dbProperties.getProperty("db.username"));
                config.setPassword(dbProperties.getProperty("db.password"));
                config.setDriverClassName(dbProperties.getProperty("db.driver")); // HikariCP precisa do driver

                // Configurações adicionais do HikariCP (ajuste conforme necessário)
                config.setMaximumPoolSize(Integer.parseInt(dbProperties.getProperty("db.hikari.maxPoolSize", "10")));
                config.setMinimumIdle(Integer.parseInt(dbProperties.getProperty("db.hikari.minIdle", "5")));
                config.setConnectionTimeout(Long.parseLong(dbProperties.getProperty("db.hikari.connectionTimeout", "30000"))); // 30 segundos
                config.setIdleTimeout(Long.parseLong(dbProperties.getProperty("db.hikari.idleTimeout", "600000"))); // 10 minutos
                config.setMaxLifetime(Long.parseLong(dbProperties.getProperty("db.hikari.maxLifetime", "1800000"))); // 30 minutos
                config.addDataSourceProperty("cachePrepStmts", "true");
                config.addDataSourceProperty("prepStmtCacheSize", "250");
                config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

                dataSource = new HikariDataSource(config);
                LOGGER.log(Level.INFO, "HikariCP DataSource inicializado com sucesso.");

            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "Erro ao carregar o arquivo de propriedades do banco de dados: " + PROPERTIES_FILE, ex);
                throw new RuntimeException("Falha ao inicializar o DataSource.", ex);
            } catch (Exception ex) { // Captura qualquer outra exceção durante a inicialização
                LOGGER.log(Level.SEVERE, "Erro inesperado ao inicializar o HikariCP DataSource.", ex);
                throw new RuntimeException("Falha inesperada ao inicializar o DataSource.", ex);
            }
>>>>>>> Stashed changes
        }
    }

    /**
<<<<<<< Updated upstream
     * Carrega as propriedades do arquivo database.properties.
     * Este método é estático e thread-safe para garantir que as propriedades sejam carregadas apenas uma vez.
     */
    private static void loadProperties() {
        if (properties == null) {
            synchronized (DatabaseConfig.class) { // Garante que apenas uma thread carregue as propriedades
                if (properties == null) {
                    properties = new Properties();
                    try (InputStream input = DatabaseConfig.class.getClassLoader().getResourceAsStream(DB_PROPERTIES_FILE)) {
                        if (input == null) {
                            LOGGER.log(Level.SEVERE, "Arquivo de propriedades do banco de dados '" + DB_PROPERTIES_FILE + "' não encontrado.");
                            throw new IOException("Arquivo de propriedades do banco de dados não encontrado.");
                        }
                        properties.load(input);
                        LOGGER.log(Level.INFO, "Propriedades do banco de dados carregadas com sucesso.");
                    } catch (IOException ex) {
                        LOGGER.log(Level.SEVERE, "Erro ao carregar propriedades do banco de dados: " + ex.getMessage(), ex);
                        throw new RuntimeException("Não foi possível carregar as propriedades do banco de dados.", ex);
                    }
                }
            }
        }
    }

    /**
     * Retorna a instância Singleton de DatabaseConfig.
     * @return A instância única de DatabaseConfig.
     */
    public static synchronized DatabaseConfig getInstance() {
        if (instance == null) {
            instance = new DatabaseConfig();
        }
        return instance;
    }

    /**
     * Obtém uma conexão do pool de conexões.
     * Esta conexão é "lógica" e será devolvida ao pool quando connection.close() for chamado.
     * @return Uma conexão com o banco de dados.
     * @throws SQLException Se não for possível obter uma conexão.
     */
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * Fecha o DataSource e libera todos os recursos do pool.
     * Deve ser chamado quando a aplicação for encerrada para evitar vazamento de recursos.
     */
    public void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            LOGGER.log(Level.INFO, "HikariCP DataSource fechado com sucesso.");
        }
    }

    /**
     * Verifica se o DataSource está fechado.
     * @return true se o DataSource está fechado, false caso contrário.
     */
    public boolean isClosed() {
        return dataSource == null || dataSource.isClosed();
    }

    /**
     * Fecha os recursos do banco de dados (Statement, ResultSet) de forma segura.
     * Com um pool de conexões, a Connection é devolvida ao pool e não deve ser fechada explicitamente aqui.
     * @param stmt O statement a ser fechado.
     * @param rs O result set a ser fechado.
     */
    public static void closeResources(Statement stmt, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Erro ao fechar recursos do banco de dados (Statement/ResultSet).", ex);
=======
     * Retorna a instância do HikariDataSource.
     * Garante que o DataSource seja inicializado antes de ser retornado.
     * @return A instância do DataSource.
     */
    public static DataSource getDataSource() {
        if (dataSource == null) {
            initializeDataSource(); // Garante que o DataSource seja inicializado se ainda não foi
>>>>>>> Stashed changes
        }
        return dataSource;
    }

    /**
<<<<<<< Updated upstream
     * Sobrecarga para fechar apenas Statement.
     * @param stmt O statement a ser fechado.
     */
    public static void closeResources(Statement stmt) {
        closeResources(stmt, null);
=======
     * Fecha o pool de conexões do HikariCP.
     * Deve ser chamado ao desligar a aplicação para liberar os recursos.
     */
    public static void shutdownDataSource() {
        if (dataSource != null) {
            dataSource.close();
            dataSource = null;
            LOGGER.log(Level.INFO, "HikariCP DataSource desligado.");
        }
>>>>>>> Stashed changes
    }
}
