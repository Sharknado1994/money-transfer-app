package com.revolut.review.dao;

import com.revolut.review.properties.ConnectionProperties;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.micronaut.context.annotation.Primary;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
@Singleton
public class JDBCConnectionFactory {
    private final HikariDataSource ds;


    @Inject
    public JDBCConnectionFactory(ConnectionProperties connectionProperties) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(connectionProperties.getDbUrl());
        config.setDriverClassName(connectionProperties.getDriverClassName());
        config.setIdleTimeout(connectionProperties.getIdleTime());
        config.setMaximumPoolSize(connectionProperties.getMaxPoolSize());
        ds = new HikariDataSource(config);
    }

    public synchronized Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}
