package com.revolut.review.exception;

public class CardsEqualException extends BankBusinessException {
    private static final long serialVersionUID = 1540869454153683630L;

    public CardsEqualException(String businessMessage) {
        super(businessMessage);
    }
}
