package com.revolut.review.model;

import lombok.Getter;

@Getter
public enum Result {
    CHARGE_DONE("Operation is sucessfully executed [card1={}, card2={}, value={}]"),
    NOT_ENOUGH_MONEY("Not enought money on card={} for charge execution"),
    FAILED("Operation can't be done");

    private final String message;

    Result(String message) {
        this.message = message;
    }

}
