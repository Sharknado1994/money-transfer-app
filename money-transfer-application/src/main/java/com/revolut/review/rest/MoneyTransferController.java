package com.revolut.review.rest;

import com.revolut.review.model.BankAccount;
import com.revolut.review.model.OperationResult;
import com.revolut.review.service.BankAccountRepository;
import com.revolut.review.service.MoneyTransferService;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.validation.Validated;
import io.reactivex.Maybe;
import lombok.extern.slf4j.Slf4j;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

import javax.inject.Inject;
import javax.validation.constraints.NotBlank;
import java.sql.SQLException;

@Controller()
@Validated
@Slf4j
public class MoneyTransferController {
    private final MoneyTransferService moneyTransferService;

    @Inject
    public MoneyTransferController(MoneyTransferService moneyTransferService) {
        this.moneyTransferService = moneyTransferService;
    }

    @Get(value = "/charge", produces = MediaType.TEXT_PLAIN)
    public Maybe<OperationResult> executeCharge(@NotBlank @QueryValue(value = "src") String src,
                                       @NotBlank @QueryValue(value = "trg") String trg,
                                       @NotBlank @QueryValue(value = "val") String val) {
        return Maybe.just(Tuples.of(src, trg, val))
                .map(params -> {
                    log.info("Input params {}", params);
                    return validateAndConvert(params);
                })
                .flatMap(convertedParams
                        -> moneyTransferService.executeCharge(convertedParams.getT1(),
                                                              convertedParams.getT2(),
                                                              convertedParams.getT3())
                                .doOnSubscribe(l -> log.debug("Starting to execute charge for params {}", convertedParams))
                                .doOnSuccess(operationResult -> log.info("Operation done with result {}", operationResult)));

    }

    protected Tuple3<String, String, Double> validateAndConvert(Tuple3<String, String, String> params) {
        String val = params.getT3();
        Double value = Double.parseDouble(val.replaceAll("[,]", "."));
        return Tuples.of(params.getT1(), params.getT2(), value);
    }
}
