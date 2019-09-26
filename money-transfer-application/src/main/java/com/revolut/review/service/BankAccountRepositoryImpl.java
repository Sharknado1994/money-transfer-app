package com.revolut.review.service;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.revolut.review.model.BankAccount;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class BankAccountRepositoryImpl implements BankAccountRepository {
    private static final String DB_DRIVER = "org.h2.Driver";
    private static final String DB_CONNECTION = "jdbc:h2:mem:banking";
    private static final String DB_USER = "admin";
    private static final String DB_PASSWORD = "admin";
    private static ConnectionSource connectionSource
            =  new JdbcConnectionSource();


    @Override
    public BankAccount getAccountByCardNum(String cardNum) {
        return null;
    }

    @Override
    public BankAccount updateAccount(BankAccount account) {
        return null;
    }

    private static synchronized Connection getDBConnection() {
        Connection dbConnection = null;
        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        try {
            dbConnection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
            return dbConnection;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return dbConnection;
    }
}
