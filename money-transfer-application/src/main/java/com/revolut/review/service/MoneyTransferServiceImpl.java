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
    private final BankRepository bankRepository;

    @Inject
    public MoneyTransferServiceImpl(BankRepository bankRepository) {
        this.bankRepository = bankRepository;
    }

    @Override
    public Maybe<OperationResult> executeCharge(String src, String trg, Double value) throws Exception {
        return Maybe.just(transactionLogic(src, trg, value));
    }

    protected OperationResult transactionLogic(final String src, final String trg, final Double value) throws SQLException, ClassNotFoundException, BankBusinessException {
        if (value < 0) {
            return bankRepository.execute(trg, src, -value);
        } else {
            return bankRepository.execute(src, trg, value);
        }
    }
}
