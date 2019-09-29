package com.revolut.review.exception.handler;

import com.revolut.review.exception.BankBusinessException;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;

import javax.inject.Singleton;

@Produces
@Singleton
@Requires(classes = {BankBusinessException.class, ExceptionHandler.class})
public class BankBusinessExceptionHandler implements ExceptionHandler<BankBusinessException, HttpResponse> {

    @Override
    public HttpResponse handle(HttpRequest request, BankBusinessException exception) {
        return HttpResponse.status(HttpStatus.UNPROCESSABLE_ENTITY, exception.getBusinessMessage()).body(exception.getBusinessMessage());
    }
}
