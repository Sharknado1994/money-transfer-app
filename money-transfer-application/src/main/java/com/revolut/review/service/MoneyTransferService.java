package com.revolut.review.service;

import com.revolut.review.model.BankAccount;
import com.revolut.review.model.OperationResult;

public interface MoneyTransferService {
    OperationResult executeCharge(String src, String trg, Double value);

    OperationResult executeWithdrawal(BankAccount src, BankAccount trg, Double value);
}
