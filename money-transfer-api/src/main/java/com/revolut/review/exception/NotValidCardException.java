package com.revolut.review.exception;

public class NotValidCardException extends BankBusinessException {
    private static final long serialVersionUID = 1696436715069031875L;

    public NotValidCardException(String businessMessage) {
        super(businessMessage);
    }
}
