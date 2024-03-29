package com.revolut.review.service;


import com.revolut.review.model.BankAccount;
import com.revolut.review.service.dao.H2DBUtils;
import io.micronaut.runtime.Micronaut;
import io.micronaut.runtime.event.annotation.EventListener;
import io.micronaut.runtime.server.event.ServerStartupEvent;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class Application {
    private final H2DBUtils h2DBUtils;

    @Inject
    public Application(H2DBUtils h2DBUtils) {
        this.h2DBUtils = h2DBUtils;
    }

    public static void main(String... args) {
        Micronaut.run(Application.class);
    }

    @EventListener
    public void onStartup(ServerStartupEvent event) throws Exception {
        //loads 1000 accounts in H2
        log.info("Creating bank_accounts table");
        h2DBUtils.createBankAccountTable();
        log.info("bank_accounts table is created");
        Collection<BankAccount> accounts = IntStream.range(1000, 2000).mapToObj(i -> {
            String cardNum = String.valueOf(i);
            Double balance = 1000.0;
            BankAccount acc = new BankAccount();
            acc.setBalance(balance);
            acc.setCardNumber(cardNum);
            return acc;
        }).collect(Collectors.toList());
        log.info("Inserting accounts");
        h2DBUtils.bulkCreateAccounts(accounts);
        log.info("Accounts are inserted");
    }
}
