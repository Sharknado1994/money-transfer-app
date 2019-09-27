package com.revolut.review.utils;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.table.TableUtils;
import com.revolut.review.model.BankAccount;
import com.revolut.review.service.H2ConnectionFactory;
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

    @Inject
    public H2DBUtils() throws SQLException {
        this.accountDao = DaoManager.createDao(H2ConnectionFactory.getConnection(), BankAccount.class);
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
        TableUtils.createTable(H2ConnectionFactory.getConnection(), BankAccount.class);
    }
}
