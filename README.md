# money-transfer-app
For JUnit&Integration tests <br>
mvn clean install

For starting application after installation <br>
cd money-trasfer-application <br>
mvn exec:exec

#Features and Notes
Concurrency is implemented by 8 nio threads (RxJava) and row-level locking in H2 DB for parallel transactions around the same rows 
(please refer to <br><i>com.revolut.review.service.iat.MoneyTransferIntegrationTest.test_TwoConcurrentTransactionsToEachOther</i>)
<br>
<br>
Database pool is managed by Hikari (currently 100 connections)

When application is started, 1000 rows with cards (nums [1000; 1999]) are inserted