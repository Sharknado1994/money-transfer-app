package com.revolut.review.service.iat;

import com.revolut.review.model.BankAccount;
import com.revolut.review.model.OperationResult;
import com.revolut.review.service.dao.JDBCConnectionFactory;
import com.revolut.review.service.rest.MoneyTransferController;
import io.micronaut.context.ApplicationContext;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.test.annotation.MicronautTest;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.functions.Action;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Collectors;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@MicronautTest
@Slf4j
public class MoneyTransferIntegrationTest {

    static EmbeddedServer server;
    static MoneyTransferController controller;
    static DatabaseTestUtils databaseTestUtils;

    @BeforeClass
    public static void setup() {
        server = ApplicationContext.run(EmbeddedServer.class);
        controller = server.getApplicationContext().getBean(MoneyTransferController.class);
        JDBCConnectionFactory connectionFactory = server.getApplicationContext().getBean(JDBCConnectionFactory.class);
        databaseTestUtils = new DatabaseTestUtils(connectionFactory);
    }

    @Test
    public void test_runValidOneTransaction() throws Exception {
        String src = "1000";
        String trg = "1001";
        String value = "300";

        executeChargeWithChecking(src, trg, value, 1000d, 1000d, 700d, 1300d).subscribe();
    }

    @Test
    public void test_runValidOneTransactionValueLessThanZero() throws Exception {
        String src = "1002";
        String trg = "1003";
        String value = "-300.5";

        executeChargeWithChecking(src, trg, value, 1000d, 1000d, 1300.5d, 699.5d).subscribe();
    }

    /**
     * Two parallel transactions
     * - from 1004 -> 1005 300
     * - from 1005 -> 1004 200
     * Also this case can lead deadlock
     */
    @Test
    public void test_TwoConcurrentTransactionsToEachOther() throws InterruptedException, SQLException {
        String src = "1004";
        String trg = "1005";
        String value1 = "300";
        String value2 = "200";

        Thread firstTx = new Thread(() -> {
            controller.executeCharge(src, trg, value1).subscribe();
        });

        Thread secondTx = new Thread(() -> {
            controller.executeCharge(trg, src, value2).subscribe();
        });

        firstTx.start();
        secondTx.start();

        firstTx.join();
        secondTx.join();

        log.debug("Checking result of concurrent transactions");
        BankAccount srcAcc = databaseTestUtils.getAccountByCardNum(src);
        BankAccount trgAcc = databaseTestUtils.getAccountByCardNum(trg);

        assertThat(srcAcc.getBalance(), is(900d));
        assertThat(trgAcc.getBalance(), is(1100d));
    }

    @After
    public void cleanup() {
        server.stop();
    }

    private Maybe<OperationResult> executeChargeWithChecking(String src, String trg, String value,
                                                             Double beforeOpsBalanceSrc, Double beforeOpsBalanceTrg,
                                                             Double afterOpsBalanceSrc, Double afterOpsBalanceTrg) {
        return controller
                .executeCharge(src, trg, value)
                .doOnSubscribe(l -> {
                    log.debug("Executing");
                    BankAccount srcAcc = databaseTestUtils.getAccountByCardNum(src);
                    BankAccount trgAcc = databaseTestUtils.getAccountByCardNum(trg);

                    assertThat(srcAcc.getBalance(), is(beforeOpsBalanceSrc));
                    assertThat(trgAcc.getBalance(), is(beforeOpsBalanceTrg));
                })
                .doAfterSuccess(result -> {
                    log.debug("Executed");

                    BankAccount srcAcc = databaseTestUtils.getAccountByCardNum(src);
                    BankAccount trgAcc = databaseTestUtils.getAccountByCardNum(trg);

                    assertThat(srcAcc.getBalance(), is(afterOpsBalanceSrc));
                    assertThat(trgAcc.getBalance(), is(afterOpsBalanceTrg));
                });
    }

    private static class DatabaseTestUtils {
        private final JDBCConnectionFactory connectionFactory;

        private DatabaseTestUtils(JDBCConnectionFactory connectionFactory) {
            this.connectionFactory = connectionFactory;
        }

        public BankAccount getAccountByCardNum(String cardNum) throws SQLException {
            try (Connection connection = connectionFactory.getConnection()) {
                try {
                    String query = "select * from bank_accounts where card_number = ?";
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setString(1, cardNum);
                    BankAccount bankAccount = new BankAccount();
                    ResultSet resultSet = statement.executeQuery();
                    while (resultSet.next()) {
                        bankAccount.setCardNumber(resultSet.getString("card_number"));
                        bankAccount.setBalance(resultSet.getDouble("balance"));
                    }

                    log.debug("BankAccount {} retrieved", bankAccount);
                    return bankAccount;
                } catch (SQLException e) {
                    log.error("SQL Error {}", ExceptionUtils.getStackTrace(e));
                    connection.rollback();
                    throw e;
                }
            }
        }
    }
}
