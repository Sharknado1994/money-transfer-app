package com.revolut.review.service;

import com.revolut.review.model.OperationResult;

public interface MoneyTransferService {
    OperationResult executeCharge(String src, String trg, double value);
}
