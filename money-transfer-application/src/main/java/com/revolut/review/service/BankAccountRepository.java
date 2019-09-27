package com.revolut.review.service;

import com.revolut.review.exception.BankBusinessException;
import com.revolut.review.model.BankAccount;
import io.reactivex.Maybe;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.sql.SQLException;

public interface BankAccountRepository {
    BankAccount getAccountByCardNum(String cardNum) throws SQLException, BankBusinessException;

    void updateAccounts(BankAccount src, BankAccount trg) throws Exception;
}
