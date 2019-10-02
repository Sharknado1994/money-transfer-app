package com.revolut.review.service;

import com.revolut.review.exception.BankBusinessException;
import com.revolut.review.exception.CardsEqualException;
import com.revolut.review.exception.NotEnoughMoneyException;
import com.revolut.review.exception.NotValidCardException;
import com.revolut.review.model.BankAccount;
import com.revolut.review.model.OperationResult;
import io.reactivex.Maybe;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.SQLException;
import java.util.Map;

@Singleton
@Slf4j
public class MoneyTransferServiceImpl implements MoneyTransferService {
    private final JDBCBankUtils bankUtils;

    @Inject
    public MoneyTransferServiceImpl(JDBCBankUtils bankUtils) {
        this.bankUtils = bankUtils;
    }

    @Override
    public Maybe<OperationResult> executeCharge(String src, String trg, Double value) throws Exception {
        return Maybe.just(transactionLogic(src, trg, value));
    }

    @Override
    public Maybe<OperationResult> getBalance(String src) throws Exception {
        return Maybe.just(bankUtils.execute(new MoneyTransaction(src, null, null) {
            @Override
            public OperationResult executeWithinIsolatedTransaction() throws SQLException {
                BankAccount account = getAccount(this.getSrc());
                return new OperationResult(account.toString());
            }
        }));
    }

    @Override
    public boolean checkAccounts(BankAccount src, Double value) {
        return src.getBalance() >= value;
    }

    protected OperationResult transactionLogic(final String src, final String trg, final Double value) throws SQLException,  BankBusinessException {
        if (value < 0) {
            return bankUtils.execute(configureTransaction(trg, src, -value));
        } else {
            return bankUtils.execute(configureTransaction(src, trg, value));
        }
    }

    protected MoneyTransaction configureTransaction(final String src, final String trg, final Double value) {
        return new MoneyTransaction(src, trg, value) {
            @Override
            public OperationResult executeWithinIsolatedTransaction() throws SQLException {
                if (src.equals(trg)) {
                    throw new CardsEqualException("Source and target card's numbers are equal to each other");
                }
                Map<String, BankAccount> accounts = getAccounts(src, trg, true);
                if (accounts.get(src) == null) {
                    throw new NotValidCardException(String.format("Card %s has invalid format", src));
                }

                if (accounts.get(trg) == null) {
                    throw new NotValidCardException(String.format("Card %s has invalid format", trg));
                }
                BankAccount srcAcc = accounts.get(this.getSrc());
                BankAccount trgAcc = accounts.get(this.getTrg());
                boolean valid = checkAccounts(srcAcc, this.getVal());
                if (valid) {
                    log.info("Params before execution: src {} bal {} trg {} bal {} value {}", srcAcc.getCardNumber(), srcAcc.getBalance(), trgAcc.getCardNumber(),
                            trgAcc.getBalance(), value);
                    updateBalances(this.getSrc(), srcAcc.getBalance() - this.getVal());
                    updateBalances(this.getTrg(), trgAcc.getBalance() + this.getVal());
                    return new OperationResult(String.format("Operation result: charge from src %s to trg %s is done", src, trg));
                } else {
                    throw new NotEnoughMoneyException(String.format("Not enough money for executing charging [number= %s]", src));
                }
            }
        };
    }
}
