package com.revolut.review.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public abstract class BankBusinessException extends RuntimeException {
    private final String businessMessage;
}
