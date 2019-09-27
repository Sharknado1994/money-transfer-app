package com.revolut.review.model;

import java.util.Date;

public class BankAccountFactory {
    private static BankAccountFactory REFERENCE = null;

    private BankAccountFactory() {
        //
    }

    public BankAccount buildAccount(String cardNum, Double balance, Date lastUpdatedDate) {
        return new BankAccount();
    }

    public static synchronized BankAccountFactory getInstance() {
        if (REFERENCE == null) {
            REFERENCE = new BankAccountFactory();
        }

        return REFERENCE;
    }
}
