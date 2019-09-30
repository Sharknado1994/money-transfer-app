package com.revolut.review.dao;

import com.revolut.review.exception.BankBusinessException;
import com.revolut.review.model.BankAccount;
import com.revolut.review.model.OperationResult;
import com.revolut.review.properties.ConnectionProperties;
import com.revolut.review.service.JDBCBankUtils;
import com.revolut.review.service.MoneyTransaction;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Singleton
public class JDBCBankUtilsImpl implements JDBCBankUtils {
    private final JDBCConnectionFactory connectionFactory;
    private final ConnectionProperties connectionProperties;

    @Inject
    public JDBCBankUtilsImpl(JDBCConnectionFactory connectionFactory,
                             ConnectionProperties connectionProperties) {
        this.connectionFactory = connectionFactory;
        this.connectionProperties = connectionProperties;
    }

    @Override
    public OperationResult execute(MoneyTransaction transaction) throws SQLException, BankBusinessException {
        try (Connection connection = connectionFactory.getConnection()) {
            connection.setAutoCommit(false);
            try  {
                transaction.setConnection(connection);
                transaction.setBankRepository(this);
                OperationResult result = transaction.executeWithinIsolatedTransaction();
                connection.commit();
                return result;
            } catch (SQLException e) {
                connection.rollback();
                log.error("ERROR {}", ExceptionUtils.getStackTrace(e));
                throw e;
            }
        }
    }

    //if forUpdate true - lock records until tx is not either commited or rollbacked
    @Override
    public Map<String, BankAccount> getAccounts(Connection connection, String src, String trg, boolean forUpdate) throws SQLException, BankBusinessException {
        String query = "";
        String tableName = connectionProperties.getTableName();
        if (forUpdate) {
            query = String.format("select * from %s where card_number = ? or card_number = ? FOR UPDATE", tableName);
        } else {
            query = String.format("select * from %s where card_number = ? or card_number = ?", tableName);
        }
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, src);
        preparedStatement.setString(2, trg);
        log.info("Query {}", query);
        ResultSet set = preparedStatement.executeQuery();
        final Map<String, BankAccount> accountMap = new HashMap<>();
        while (set.next()) {
            BankAccount account = new BankAccount();
            account.setCardNumber(set.getString("card_number"));
            account.setBalance(set.getDouble("balance"));
            accountMap.put(account.getCardNumber(), account);
        }
        return accountMap;
    }

    @Override
    public BankAccount getAccount(Connection connection, String cardNumber) throws SQLException {
        String query = "";
        String tableName = connectionProperties.getTableName();
        query = String.format("select * from %s where card_number = ?", tableName);
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, cardNumber);
        log.info("Query {}", query);
        ResultSet set = preparedStatement.executeQuery();
        final BankAccount result = new BankAccount();
        while (set.next()) {
            result.setCardNumber(set.getString("card_number"));
            result.setBalance(set.getDouble("balance"));
        }
        return result;
    }

    @Override
    public int updateBalances(Connection connection, String cardNum, Double newBal) throws SQLException {
        String tableName = connectionProperties.getTableName();
        String query = String.format("update %s set balance = ? where card_number = ?", tableName);
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setDouble(1, newBal);
        preparedStatement.setString(2, cardNum);
        return preparedStatement.executeUpdate();
    }
}
