package com.compliancesys.util.impl;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.compliancesys.util.ConnectionFactory;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class HikariCPConnectionFactory implements ConnectionFactory {
    private static final Logger LOGGER = Logger.getLogger(HikariCPConnectionFactory.class.getName());
    private final HikariDataSource dataSource;

    public HikariCPConnectionFactory() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("database.properties")) {
            props.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar database.properties", e);
        }

        HikariConfig config = new HikariConfig();
        config.setDriverClassName(props.getProperty("db.driver"));
        config.setJdbcUrl(props.getProperty("db.url"));
        config.setUsername(props.getProperty("db.username"));
        config.setPassword(props.getProperty("db.password"));
        config.setMaximumPoolSize(Integer.parseInt(props.getProperty("db.hikari.maxPoolSize", "10")));
        config.setMinimumIdle(Integer.parseInt(props.getProperty("db.hikari.minIdle", "5")));
        config.setConnectionTimeout(Long.parseLong(props.getProperty("db.hikari.connectionTimeout", "30000")));
        config.setIdleTimeout(Long.parseLong(props.getProperty("db.hikari.idleTimeout", "600000")));
        config.setMaxLifetime(Long.parseLong(props.getProperty("db.hikari.maxLifetime", "1800000")));
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        
        dataSource = new HikariDataSource(config);
        LOGGER.log(Level.INFO, "HikariCP Connection Pool inicializado.");
    }

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void closeDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            LOGGER.log(Level.INFO, "HikariCP Connection Pool fechado.");
        }
    }
}