package com.revolut.review.service;

import com.revolut.review.model.BankAccount;
import com.revolut.review.model.OperationResult;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public abstract class MoneyTransaction {
    private final String src;
    private final String trg;
    private final Double val;
    private Connection connection;
    private JDBCBankUtils bankUtils;

    protected MoneyTransaction(String src, String trg, Double val) {
        this.src = src;
        this.trg = trg;
        this.val = val;
    }

    public abstract OperationResult executeWithinIsolatedTransaction() throws SQLException;

    public Map<String, BankAccount> getAccounts (String src, String trg, boolean forUpdate) throws SQLException {
        return bankUtils.getAccounts(this.connection, src, trg, forUpdate);
    }

    public BankAccount getAccount(String cardNum) throws SQLException {
        return bankUtils.getAccount(this.connection, cardNum);
    }

    public int updateBalances(String cardNum, Double newBal) throws SQLException {
        return bankUtils.updateBalances(this.connection, cardNum, newBal);
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public String getSrc() {
        return src;
    }

    public String getTrg() {
        return trg;
    }

    public Double getVal() {
        return val;
    }

    public void setBankRepository(JDBCBankUtils bankUtils) {
        this.bankUtils = bankUtils;
    }
}
