package com.revolut.review.service;

import com.revolut.review.model.BankAccount;
import com.revolut.review.model.OperationResult;
import com.revolut.review.model.Result;

public class MoneyTransferServiceImpl implements MoneyTransferService {
    private final BankAccountRepository repository = new BankAccountRepositoryImpl();

    @Override
    public OperationResult executeCharge(String src, String trg, Double value) {
        BankAccount cardNum_1 = repository.getAccountByCardNum(src);
        BankAccount cardNum_2 = repository.getAccountByCardNum(trg);
        if (value < 0) {
            return executeWithdrawal(cardNum_2, cardNum_1, -value);
        } else {
            return executeWithdrawal(cardNum_1, cardNum_2, value);
        }
    }

    @Override
    public OperationResult executeWithdrawal(BankAccount src, BankAccount trg, Double value) {
        Double srcBalance = src.getBalance();
        if (srcBalance.compareTo(value) < 0) {
            Result result = Result.NOT_ENOUGH_MONEY;
            return new OperationResult("1234", result);
        } else {
            src.setBalance(srcBalance - value);
            trg.setBalance(trg.getBalance() + value);
            repository.updateAccount(src);
            repository.updateAccount(trg);
            return new OperationResult("1234", Result.CHARGE_DONE);
        }
    }
}
