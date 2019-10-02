package com.revolut.review.service;

import com.revolut.review.model.BankAccount;
import com.revolut.review.model.OperationResult;
import io.reactivex.Maybe;

public interface MoneyTransferService {
    Maybe<OperationResult> executeCharge(String src, String trg, Double value) throws Exception;

    Maybe<OperationResult> getBalance(String src) throws Exception;

    boolean checkAccounts(BankAccount src, Double value);
}
