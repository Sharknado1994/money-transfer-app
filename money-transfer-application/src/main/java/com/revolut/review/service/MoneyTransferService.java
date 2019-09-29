package com.revolut.review.service;

import com.revolut.review.model.BankAccount;
import com.revolut.review.model.OperationResult;
import io.reactivex.Maybe;
import reactor.core.publisher.Mono;

import java.sql.SQLException;

public interface MoneyTransferService {
    Maybe<OperationResult> executeCharge(String src, String trg, Double value) throws Exception;
}
