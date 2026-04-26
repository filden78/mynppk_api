package ru.filden.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class dbConnection {
    private static final Logger logger = LoggerFactory.getLogger(dbConnection.class);
    private static volatile dbConnection instance;
    private HikariDataSource dataSource;
    private HikariConfig hikariConfig;

    private dbConnection(HikariConfig config){
        this.hikariConfig =config;
        initializeDataSource();
    }

    public static dbConnection getInstance(HikariConfig config) {
        if (instance == null) {
            synchronized (dbConnection.class) {
                if (instance == null) {
                    instance = new dbConnection(config);
                }
            }
        }
        return instance;
    }

    private void initializeDataSource() {
        try {
            configureMSSQLProperties();

            this.dataSource = new HikariDataSource(hikariConfig);
            logger.info("HikariCP connection pool initialized successfully");
            logger.info("Configuration: URL={}, MaxPoolSize={}, MinIdle={}",
                    hikariConfig.getJdbcUrl(),
                    hikariConfig.getMaximumPoolSize(),
                    hikariConfig.getMinimumIdle());

        } catch (Exception e) {
            logger.error("Failed to initialize database connection pool", e);
            throw new RuntimeException("Database connection pool initialization failed", e);
        }
    }


    private void configureMSSQLProperties() {
        if (!hikariConfig.getDataSourceProperties().containsKey("sendStringParametersAsUnicode")) {
            hikariConfig.addDataSourceProperty("sendStringParametersAsUnicode", "false");
        }

        if (!hikariConfig.getDataSourceProperties().containsKey("encrypt")) {
            hikariConfig.addDataSourceProperty("encrypt", "true");
        }

        if (!hikariConfig.getDataSourceProperties().containsKey("trustServerCertificate")) {
            hikariConfig.addDataSourceProperty("trustServerCertificate", "true");
        }

        if (hikariConfig.getConnectionTimeout() == 30000) {
            hikariConfig.setConnectionTimeout(30000);
        }

        if (hikariConfig.getIdleTimeout() == 600000) {
            hikariConfig.setIdleTimeout(600000);
        }

        if (hikariConfig.getMaxLifetime() == 1800000) {
            hikariConfig.setMaxLifetime(1800000);
        }

        hikariConfig.setConnectionTestQuery("SELECT 1");
        hikariConfig.setValidationTimeout(5000);

        hikariConfig.setLeakDetectionThreshold(60000);
    }
    public Connection getConnection() throws SQLException {
        try {
            Connection conn = dataSource.getConnection();
            logger.debug("Connection acquired from pool. Active connections: {}",
                    dataSource.getHikariPoolMXBean().getActiveConnections());
            return conn;
        } catch (SQLException e) {
            logger.error("Failed to get connection from pool", e);
            throw new SQLException("Cannot acquire database connection", e);
        }
    }

    public Connection getConnection(long timeoutMillis) throws SQLException {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            logger.error("Failed to get connection from pool within timeout", e);
            throw new SQLException("Cannot acquire database connection within " + timeoutMillis + "ms", e);
        }
    }

    public void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            logger.info("Closing database connection pool");
            dataSource.close();
            logger.info("Database connection pool closed");
        }
    }

    public boolean isClosed() {
        return dataSource == null || dataSource.isClosed();
    }
}
