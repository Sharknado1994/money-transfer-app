package com.revolut.review.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@DatabaseTable(tableName = "bank_accounts")
public class BankAccount {
    @DatabaseField(generatedId = true)
    private UUID id;
    @DatabaseField(columnName = "card_number")
    private String cardNumber;
    @DatabaseField
    private Double balance;
}
