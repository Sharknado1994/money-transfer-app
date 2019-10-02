package com.revolut.review.exception;

public class NotEnoughMoneyException extends BankBusinessException {
    private static final long serialVersionUID = -1377701964205139503L;

    public NotEnoughMoneyException(String businessMessage) {
        super(businessMessage);
    }
}
