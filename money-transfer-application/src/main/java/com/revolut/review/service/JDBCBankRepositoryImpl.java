package com.revolut.review.service;

import com.revolut.review.exception.BankBusinessException;
import com.revolut.review.model.BankAccount;
import com.revolut.review.model.OperationResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.inject.Singleton;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Singleton
public class JDBCBankRepositoryImpl implements BankRepository {
    static final String JDBC_DRIVER = "org.h2.Driver";
    private static final String DB_URL = "jdbc:h2:mem:bank;LOCK_TIMEOUT=60000;DB_CLOSE_ON_EXIT=TRUE";

    @Override
    public OperationResult execute(String src, String trg, Double val) throws SQLException, ClassNotFoundException, BankBusinessException {
        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            Class.forName(JDBC_DRIVER);
            connection.setAutoCommit(false);
            try  {
                Map<String, BankAccount> accounts = getAccounts(connection, src, trg, true);
                BankAccount srcAcc = accounts.get(src);
                BankAccount trgAcc = accounts.get(trg);
                boolean valid = checkAccounts(srcAcc, val);
                if (valid) {
                    log.info("Params before execution: src {} bal {} trg {} bal {}", srcAcc.getCardNumber(), srcAcc.getBalance(), trgAcc.getCardNumber(),
                            trgAcc.getBalance());
                    updateBalances(connection, src, srcAcc.getBalance() - val);
                    updateBalances(connection, trg, trgAcc.getBalance() + val);
                    connection.commit();
                    return new OperationResult(String.format("Operation result: charge from src %s to trg %s is done", src, trg));
                } else {
                    throw new BankBusinessException("Not enough money for executing charging [number= " + srcAcc.getCardNumber() + "]");
                }
            } catch (SQLException e) {
                connection.rollback();
                log.error("ERROR {}", ExceptionUtils.getStackTrace(e));
                throw e;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw e;
        }
    }

    protected boolean checkAccounts(BankAccount srcAcc, Double val) throws BankBusinessException{
        return srcAcc.getBalance() >= val;
    }

    //if forUpdate true - lock records until tx is not either commited or rollbacked
    protected Map<String, BankAccount> getAccounts(Connection connection, String src, String trg, boolean forUpdate) throws SQLException, BankBusinessException {
        String query = "";
        if (forUpdate) {
            query = "select * from bankAccounts where card_number = '?' or '?' FOR UPDATE";
        } else {
            query = "select * from bankAccounts where card_number = '?' or '?'";
        }
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(0, src);
        preparedStatement.setString(1, trg);
        log.trace("Query {}", query);
        ResultSet set = preparedStatement.executeQuery(query);
        final Map<String, BankAccount> accountMap = new HashMap<>();
        while (set.next()) {
            BankAccount account = new BankAccount();
            account.setCardNumber(set.getString("card_number"));
            account.setBalance(set.getDouble("balance"));
            accountMap.put(account.getCardNumber(), account);

        }
        if (accountMap.get(src) == null) {
            throw new BankBusinessException(String.format("Card %s has invalid format", src));
        }

        if (accountMap.get(trg) == null) {
            throw new BankBusinessException(String.format("Card %s has invalid format", trg));
        }
        return accountMap;
    }

    protected int updateBalances(Connection connection, String cardNum, Double newBal) throws SQLException {
        String query = "update bankAccounts set balance = ? where card_number = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setDouble(0, newBal);
        preparedStatement.setString(1, cardNum);
        return preparedStatement.executeUpdate(query);
    }
}
