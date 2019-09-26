package com.revolut.review.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@ToString
@Getter
public class OperationResult {
    private final String transactionId;
    private final Result result;
}
