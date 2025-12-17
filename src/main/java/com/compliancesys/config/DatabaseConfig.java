package com.compliancesys.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DatabaseConfig {
    private static final Logger LOGGER = Logger.getLogger(DatabaseConfig.class.getName());
    private static DatabaseConfig instance;
    private HikariDataSource dataSource;
    private Properties dbProperties;

    private DatabaseConfig() {
        loadProperties();
        initDataSource();
    }

    public static synchronized DatabaseConfig getInstance() {
        if (instance == null) {
            instance = new DatabaseConfig();
        }
        return instance;
    }

    private void loadProperties() {
        dbProperties = new Properties();
        String testPropertiesFile = "database.properties";
        String mainPropertiesFile = "database.properties";

        try (InputStream input = getClass().getClassLoader().getResourceAsStream(testPropertiesFile)) {
            if (input != null) {
                dbProperties.load(input);
                LOGGER.log(Level.INFO, "Propriedades do banco de dados carregadas de src/test/resources/{0}", testPropertiesFile);
                if (dbProperties.getProperty("db.url", "").contains("h2:mem")) {
                    LOGGER.log(Level.WARNING, "Atenção: O ambiente de teste está configurado para H2 em memória, mas o projeto usa PostgreSQL. Considere alinhar o banco de dados de teste.");
                }
                return;
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Não foi possível carregar as propriedades de teste de {0}. Tentando carregar de src/main/resources.", testPropertiesFile);
        }

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

    private void initDataSource() {
        if (dbProperties.isEmpty()) {
            LOGGER.log(Level.SEVERE, "Propriedades do banco de dados não foram carregadas. Não é possível inicializar o DataSource.");
            throw new IllegalStateException("Propriedades do banco de dados não carregadas.");
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dbProperties.getProperty("db.url"));
        config.setUsername(dbProperties.getProperty("db.username"));
        config.setPassword(dbProperties.getProperty("db.password"));
        config.setDriverClassName(dbProperties.getProperty("db.driver"));

        config.setMinimumIdle(Integer.parseInt(dbProperties.getProperty("db.hikari.minimumIdle", "5")));
        config.setMaximumPoolSize(Integer.parseInt(dbProperties.getProperty("db.hikari.maximumPoolSize", "10")));
        config.setConnectionTimeout(Long.parseLong(dbProperties.getProperty("db.hikari.connectionTimeout", "30000")));
        config.setIdleTimeout(Long.parseLong(dbProperties.getProperty("db.hikari.idleTimeout", "600000")));
        config.setMaxLifetime(Long.parseLong(dbProperties.getProperty("db.hikari.maxLifetime", "1800000")));

        config.setConnectionTestQuery("SELECT 1");

        try {
            dataSource = new HikariDataSource(config);
            LOGGER.log(Level.INFO, "HikariCP DataSource inicializado com sucesso para URL: {0}", config.getJdbcUrl());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao inicializar o HikariCP DataSource: " + e.getMessage(), e);
            throw new RuntimeException("Falha ao inicializar o pool de conexões do banco de dados.", e);
        }
    }

    public Connection getConnection() throws SQLException {
        if (dataSource == null) {
            LOGGER.log(Level.SEVERE, "DataSource não inicializado. Tentando inicializar novamente.");
            initDataSource();
            if (dataSource == null) {
                throw new SQLException("DataSource não pôde ser inicializado.");
            }
        }
        return dataSource.getConnection();
    }

    public void closeDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            LOGGER.log(Level.INFO, "HikariCP DataSource fechado.");
        }
    }

    public static synchronized void resetInstance() {
        if (instance != null) {
            instance.closeDataSource();
            instance = null;
            LOGGER.log(Level.INFO, "DatabaseConfig instance resetada.");
        }
    }
}
