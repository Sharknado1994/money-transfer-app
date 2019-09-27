package com.revolut.review.service;

import com.j256.ormlite.misc.TransactionManager;
import com.revolut.review.exception.BankBusinessException;
import com.revolut.review.model.BankAccount;
import com.revolut.review.model.OperationResult;
import io.reactivex.Maybe;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.SQLException;

@Singleton
@Slf4j
public class MoneyTransferServiceImpl implements MoneyTransferService {
    private final BankAccountRepository repository;

    @Inject
    public MoneyTransferServiceImpl(BankAccountRepository repository) {
        this.repository = repository;
    }

    @Override
    public Maybe<OperationResult> executeCharge(String src, String trg, Double value) throws SQLException {
        return Maybe.just(transactionLogic(src, trg, value));
    }

    protected OperationResult transactionLogic(final String src, final String trg, final Double value) throws SQLException, RuntimeException {
        return TransactionManager.callInTransaction(H2ConnectionFactory.getConnection(), () -> {
            log.info("Execution blocking logic for {} {} {}", src, trg, value);
            BankAccount srcAcc = repository.getAccountByCardNum(src);
            BankAccount trgAcc = repository.getAccountByCardNum(trg);
            return executeWithdrawal(srcAcc, trgAcc, value);
        });
    }

    @Override
    public OperationResult executeWithdrawal(BankAccount src, BankAccount trg, Double value) throws Exception {
        Double srcBalance = src.getBalance();
        if (srcBalance.compareTo(value) < 0) {
            throw new BankBusinessException("Not enough money on account " + src + " for charge execution");
        } else {
            src.setBalance(srcBalance - value);
            trg.setBalance(trg.getBalance() + value);
            repository.updateAccounts(src, trg);
            return new OperationResult("Charge from " + src.getCardNumber() + " to " + trg.getCardNumber() + " is done");
        }
    }
}
