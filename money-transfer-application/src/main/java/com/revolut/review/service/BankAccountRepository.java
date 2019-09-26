package com.revolut.review.service;

import com.revolut.review.model.BankAccount;

public interface BankAccountRepository {
    BankAccount getAccountByCardNum(String cardNum);

    BankAccount updateAccount(BankAccount account);
}
