# money-transfer-app
For JUnit&Integration tests <br>
mvn clean install

For starting application <br>
cd money-trasfer-application <br>
mvn exec:exec

#Features
Concurrency is implemented by 8 nio threads (RxJava) and row-level locking in H2 DB for parallel transactions on the same rows 
(please refer to <br><i>com.revolut.review.service.iat.MoneyTransferIntegrationTest.test_TwoConcurrentTransactionsToEachOther</i>)
<br>
<br>
Database pool is managed by Hikari (currently 100 connections)