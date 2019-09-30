package com.revolut.review.dao;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.revolut.review.model.BankAccount;
import com.revolut.review.properties.ConnectionProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.SQLException;
import java.util.Collection;

@Slf4j
@Singleton
public class H2DBUtils {
    private final Dao<BankAccount, String> accountDao;
    private final ConnectionSource connectionSource;

    @Inject
    public H2DBUtils(ConnectionProperties connectionProperties) throws SQLException {
        this.connectionSource = new JdbcConnectionSource(connectionProperties.getDbUrl());
        this.accountDao = DaoManager.createDao(connectionSource, BankAccount.class);
    }

    public void bulkCreateAccounts(Collection<BankAccount> bankAccounts) throws Exception {
        accountDao.callBatchTasks(() -> {
            bankAccounts.forEach(acc -> {
                try {
                    accountDao.createIfNotExists(acc);
                } catch (SQLException e) {
                    log.error("Error while inserting data {}", ExceptionUtils.getStackTrace(e));
                }
            });
            return null;
        });
    }

    public void createBankAccountTable() throws SQLException {
        TableUtils.createTable(connectionSource, BankAccount.class);
    }

}
