package com.compliancesys.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Singleton para gerenciar a configuração e o pool de conexões com o banco de dados
 * usando HikariCP.
 * Carrega as propriedades de conexão de um arquivo 'database.properties'.
 * Prioriza 'src/test/resources/database.properties' se existir e estiver em ambiente de teste.
 */
public class DatabaseConfig {
    private static final Logger LOGGER = Logger.getLogger(DatabaseConfig.class.getName());
    private static DatabaseConfig instance;
    private HikariDataSource dataSource;
    private Properties dbProperties;

    // Construtor privado para o padrão Singleton
    private DatabaseConfig() {
        loadProperties();
        initDataSource();
    }

    /**
     * Retorna a única instância de DatabaseConfig.
     *
     * @return A instância de DatabaseConfig.
     */
    public static synchronized DatabaseConfig getInstance() {
        if (instance == null) {
            instance = new DatabaseConfig();
        }
        return instance;
    }

    /**
     * Carrega as propriedades do banco de dados.
     * Tenta carregar de 'src/test/resources/database.properties' primeiro (para testes),
     * e se não encontrar, carrega de 'src/main/resources/database.properties'.
     */
    private void loadProperties() {
        dbProperties = new Properties();
        String testPropertiesFile = "database.properties"; // Nome do arquivo em src/test/resources
        String mainPropertiesFile = "database.properties"; // Nome do arquivo em src/main/resources

        // Tenta carregar o arquivo de propriedades de teste
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(testPropertiesFile)) {
            if (input != null) {
                dbProperties.load(input);
                LOGGER.log(Level.INFO, "Propriedades do banco de dados carregadas de src/test/resources/{0}", testPropertiesFile);
                // Verifica se as propriedades de teste são para H2 ou PostgreSQL
                if (dbProperties.getProperty("db.url", "").contains("h2:mem")) {
                    LOGGER.log(Level.WARNING, "Atenção: O ambiente de teste está configurado para H2 em memória, mas o projeto usa PostgreSQL. Considere alinhar o banco de dados de teste.");
                }
                return; // Se carregou de teste, não precisa carregar de main
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Não foi possível carregar as propriedades de teste de {0}. Tentando carregar de src/main/resources.", testPropertiesFile);
        }

        // Se não carregou de teste, tenta carregar o arquivo de propriedades principal
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(mainPropertiesFile)) {
            if (input != null) {
                dbProperties.load(input);
                LOGGER.log(Level.INFO, "Propriedades do banco de dados carregadas de src/main/resources/{0}", mainPropertiesFile);
            } else {
                LOGGER.log(Level.SEVERE, "Arquivo de propriedades do banco de dados '{0}' não encontrado em src/main/resources.", mainPropertiesFile);
                throw new RuntimeException("Arquivo de propriedades do banco de dados não encontrado.");
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erro ao carregar as propriedades do banco de dados: " + e.getMessage(), e);
            throw new RuntimeException("Erro ao carregar as propriedades do banco de dados.", e);
        }
    }

    /**
     * Inicializa o HikariDataSource com as propriedades carregadas.
     */
    private void initDataSource() {
        if (dbProperties.isEmpty()) {
            LOGGER.log(Level.SEVERE, "Propriedades do banco de dados não foram carregadas. Não é possível inicializar o DataSource.");
            throw new IllegalStateException("Propriedades do banco de dados não carregadas.");
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dbProperties.getProperty("db.url"));
        config.setUsername(dbProperties.getProperty("db.username"));
        config.setPassword(dbProperties.getProperty("db.password"));
        config.setDriverClassName(dbProperties.getProperty("db.driver")); // Adicionado para carregar o driver dinamicamente

        // Configurações do HikariCP (podem vir do .properties também)
        config.setMinimumIdle(Integer.parseInt(dbProperties.getProperty("db.hikari.minimumIdle", "5")));
        config.setMaximumPoolSize(Integer.parseInt(dbProperties.getProperty("db.hikari.maximumPoolSize", "10")));
        config.setConnectionTimeout(Long.parseLong(dbProperties.getProperty("db.hikari.connectionTimeout", "30000"))); // 30 segundos
        config.setIdleTimeout(Long.parseLong(dbProperties.getProperty("db.hikari.idleTimeout", "600000"))); // 10 minutos
        config.setMaxLifetime(Long.parseLong(dbProperties.getProperty("db.hikari.maxLifetime", "1800000"))); // 30 minutos

        // Adiciona um teste de conexão para verificar a validade
        config.setConnectionTestQuery("SELECT 1"); // Padrão para PostgreSQL

        try {
            dataSource = new HikariDataSource(config);
            LOGGER.log(Level.INFO, "HikariCP DataSource inicializado com sucesso para URL: {0}", config.getJdbcUrl());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao inicializar o HikariCP DataSource: " + e.getMessage(), e);
            throw new RuntimeException("Falha ao inicializar o pool de conexões do banco de dados.", e);
        }
    }

    /**
     * Obtém uma conexão do pool.
     *
     * @return Uma conexão JDBC.
     * @throws SQLException Se ocorrer um erro ao obter a conexão.
     */
    public Connection getConnection() throws SQLException {
        if (dataSource == null) {
            LOGGER.log(Level.SEVERE, "DataSource não inicializado. Tentando inicializar novamente.");
            initDataSource(); // Tenta reinicializar se for nulo (pode acontecer em cenários específicos)
            if (dataSource == null) {
                throw new SQLException("DataSource não pôde ser inicializado.");
            }
        }
        return dataSource.getConnection();
    }

    /**
     * Fecha o pool de conexões do HikariCP.
     * Deve ser chamado ao desligar a aplicação para liberar recursos.
     */
    public void closeDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            LOGGER.log(Level.INFO, "HikariCP DataSource fechado.");
        }
    }

    // Método para redefinir a instância para testes (NÃO USAR EM PRODUÇÃO)
    // Permite que os testes reinicializem o DatabaseConfig com diferentes propriedades.
    public static synchronized void resetInstance() {
        if (instance != null) {
            instance.closeDataSource();
            instance = null;
            LOGGER.log(Level.INFO, "DatabaseConfig instance resetada.");
        }
    }
}
