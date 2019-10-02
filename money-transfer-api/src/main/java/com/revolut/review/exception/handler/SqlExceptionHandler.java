package com.revolut.review.exception.handler;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;

import javax.inject.Singleton;
import java.sql.SQLException;

@Produces
@Singleton
@Requires(classes = {SQLException.class, ExceptionHandler.class})
public class SqlExceptionHandler implements ExceptionHandler<SQLException, HttpResponse> {

    @Override
    public HttpResponse handle(HttpRequest request, SQLException exception) {
        return HttpResponse.status(HttpStatus.INTERNAL_SERVER_ERROR, "Something goes wrong, please contact administrator");
    }
}
