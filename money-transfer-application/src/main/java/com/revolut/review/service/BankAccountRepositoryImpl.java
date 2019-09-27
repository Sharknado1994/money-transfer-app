package com.revolut.review.service;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.Where;
import com.revolut.review.exception.BankBusinessException;
import com.revolut.review.model.BankAccount;
import io.reactivex.Maybe;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@Singleton
@Slf4j
public class BankAccountRepositoryImpl implements BankAccountRepository {
    private final Dao<BankAccount, String> accountDao;

    @Inject
    public BankAccountRepositoryImpl() throws SQLException {
        log.info("Creating DAO...");
        this.accountDao = DaoManager.createDao(H2ConnectionFactory.getConnection(), BankAccount.class);
    }

    @Override
    public BankAccount getAccountByCardNum(String cardNum) throws SQLException, BankBusinessException {
        final PreparedQuery<BankAccount> preparedQuery = prepareSelectForCardNum(cardNum);
        log.trace("Executing statement {}", preparedQuery);
        List<BankAccount> accounts = accountDao.query(preparedQuery);
        if (accounts.isEmpty()) {
            throw new BankBusinessException("CardNum " + cardNum + " is not valid");
        }
        return accounts.iterator().next();
    }

    @Override
    public void updateAccounts(BankAccount src, BankAccount trg) throws Exception {
        accountDao.callBatchTasks(() -> {
            int resultSrc = accountDao.update(src);
            int resultTrg = accountDao.update(trg);
            log.info("Update results [src = {}; result ={}] [trg = {}; result={}]",
                    src.getCardNumber(), resultSrc, trg.getCardNumber(), resultTrg);
            return "";
        });
    }

    protected PreparedQuery<BankAccount> prepareSelectForCardNum(String cardNum) throws SQLException {
        QueryBuilder<BankAccount, String> builder = accountDao.queryBuilder();
        Where<BankAccount, String> where = builder.where();
        SelectArg selectArg = new SelectArg();
        selectArg.setValue(cardNum);
        where.eq("card_number", selectArg);
        return builder.prepare();
    }


}
