package com.revolut.review.exception;

public class BankBusinessException extends RuntimeException {
    private final String message;

    public BankBusinessException(String message) {
        this.message = message;
    }
}
