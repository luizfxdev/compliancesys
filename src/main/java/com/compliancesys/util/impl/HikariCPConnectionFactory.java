package com.compliancesys.util.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.compliancesys.util.ConnectionFactory;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class HikariCPConnectionFactory implements ConnectionFactory {
    private static final Logger LOGGER = Logger.getLogger(HikariCPConnectionFactory.class.getName());
    private final HikariDataSource dataSource;

    public HikariCPConnectionFactory() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/compliancesys_db");
        config.setUsername("user");
        config.setPassword("password");

        config.setMaximumPoolSize(10);
        config.setMinimumIdle(5);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
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
