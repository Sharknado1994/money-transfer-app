package com.revolut.review.service;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

public class H2ConnectionFactory {
    private static final String DATABASE_URL = "jdbc:h2:mem:bank";
    private static ConnectionSource CONNECTION_SOURCE = null;

    public static synchronized ConnectionSource getConnection() throws SQLException {
        if (CONNECTION_SOURCE == null) {
            CONNECTION_SOURCE = new JdbcConnectionSource(DATABASE_URL);
        }

        return CONNECTION_SOURCE;
    }
}
