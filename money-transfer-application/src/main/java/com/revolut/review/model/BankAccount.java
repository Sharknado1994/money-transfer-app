package com.revolut.review.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@DatabaseTable(tableName = "bankAccounts")
public class BankAccount {
    @DatabaseField(id = true)
    private String id;
    @DatabaseField
    private String cardNumber;
    @DatabaseField
    private Double balance;
    @DatabaseField
    private Date lastUpdatedDate;
}
