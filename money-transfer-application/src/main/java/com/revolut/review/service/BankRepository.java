package com.revolut.review.service;

import com.revolut.review.exception.BankBusinessException;
import com.revolut.review.model.OperationResult;

import java.sql.SQLException;

public interface BankRepository {
    OperationResult execute(String src, String trg, Double val) throws SQLException, ClassNotFoundException, BankBusinessException;
}
