package com.revolut.review.service;

import com.revolut.review.exception.BankBusinessException;
import com.revolut.review.model.BankAccount;
import com.revolut.review.model.OperationResult;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public interface JDBCBankUtils {
    OperationResult execute(MoneyTransaction transaction) throws SQLException, BankBusinessException;

    Map<String, BankAccount> getAccounts(Connection connection, String src, String trg, boolean forUpdate) throws SQLException;

    BankAccount getAccount(Connection connection, String cardNumber) throws SQLException;

    int updateBalances(Connection connection, String cardNum, Double newBal) throws SQLException;
}
