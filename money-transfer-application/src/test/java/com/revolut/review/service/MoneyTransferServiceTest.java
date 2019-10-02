package com.revolut.review.service;

import com.revolut.review.exception.CardsEqualException;
import com.revolut.review.exception.NotEnoughMoneyException;
import com.revolut.review.exception.NotValidCardException;
import com.revolut.review.model.BankAccount;
import com.revolut.review.model.OperationResult;
import com.revolut.review.properties.ConnectionProperties;
import com.revolut.review.service.dao.JDBCBankUtilsImpl;
import com.revolut.review.service.dao.JDBCConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@Slf4j
public class MoneyTransferServiceTest {

    @Mock
    JDBCBankUtilsImpl bankUtils;

    @InjectMocks
    MoneyTransferServiceImpl moneyTransferService;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void test_validOperation() throws Exception {
        String src = "1234";
        String trg = "1235";
        Double value = 300d;
        Double initBalance = 1000d;

        prepareMethodMocks(src, trg, initBalance);

        //Execute logic
        moneyTransferService.executeCharge(src, trg, value).blockingGet();
        //Check that update query was sent with valid parameters
        verify(bankUtils).getAccounts(any(), eq(src), eq(trg), eq(true));
        verify(bankUtils).updateBalances(any(), eq(src), eq(initBalance - value));
        verify(bankUtils).updateBalances(any(), eq(trg), eq(initBalance + value));
    }

    @Test
    public void test_validOperation_valueLessThanZero() throws Exception {
        String src = "1234";
        String trg = "1235";
        Double value = -300d;
        Double initBalance = 1000d;
        prepareMethodMocks(src, trg, initBalance);

        //Execute logic
        moneyTransferService.executeCharge(src, trg, value).blockingGet();
        //Check that update query was sent with valid parameters
        verify(bankUtils).getAccounts(any(), eq(trg), eq(src), eq(true));
        verify(bankUtils).updateBalances(any(), eq(src), eq(initBalance - value));
        verify(bankUtils).updateBalances(any(), eq(trg), eq(initBalance + value));
    }

    @Test(expected = NotEnoughMoneyException.class)
    public void test_notEnoughMoney() throws Exception {
        String src = "1234";
        String trg = "1235";
        Double value = 3000d;
        Double initBalance = 1000d;

        prepareMethodMocks(src, trg, initBalance);

        //Execute logic
        moneyTransferService.executeCharge(src, trg, value).blockingGet();
        //Check that update query was sent with valid parameters
    }

    @Test(expected = NotValidCardException.class)
    public void test_oneCardNotValid() throws Exception {
        String src = "12345";
        String trg = "1235";
        Double value = -300d;
        Double initBalance = 1000d;

        prepareMethodMocks(src, trg, initBalance);

        //Execute logic
        moneyTransferService.executeCharge(src, trg, value).blockingGet();
    }

    @Test(expected = CardsEqualException.class)
    public void test_cardsEqual() throws Exception {
        String src = "1235";
        String trg = "1235";
        Double value = -300d;
        Double initBalance = 1000d;

        prepareMethodMocks(src, trg, initBalance);

        //Execute logic
        moneyTransferService.executeCharge(src, trg, value).blockingGet();
    }

    protected void prepareMethodMocks(String src, String trg, Double initBalance) throws SQLException {
        BankAccount srcAcc = new BankAccount();
        srcAcc.setCardNumber(src);
        srcAcc.setBalance(initBalance);
        BankAccount trgAcc = new BankAccount();
        trgAcc.setBalance(initBalance);
        trgAcc.setCardNumber(trg);
        Map<String, BankAccount> accountsMap = new HashMap<String, BankAccount>() {{
            put(src, srcAcc);
            put(trg, trgAcc);
        }};

        if (src.length() != 4) {
            accountsMap.remove(src);
        }

        if (trg.length() != 4) {
            accountsMap.remove(trg);
        }

        when(bankUtils.getAccounts(any(), anyString(), anyString(), eq(true))).thenReturn(accountsMap);
        when(bankUtils.execute(any())).thenCallRealMethod();
        when(bankUtils.getConnectionFromPool()).thenReturn(mock(Connection.class));
    }
}
