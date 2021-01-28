package fr.yncrea.pyjabank.database.models;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.text.DecimalFormat;

@Entity (indices = {@Index(value = {"id"}, unique = true)})
public class Account {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String account_name;
    private double amount;
    private String iban;
    private String currency; //could have been enum

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setId(String id) {
        try {
            this.id = Integer.parseInt(id);
        }
        catch (Exception ignore) {
            this.id = -1;
        }
    }

    public String getAccount_name() {
        return account_name;
    }

    public void setAccount_name(String account_name) {
        this.account_name = account_name;
    }

    public double getAmount() {
        return amount;
    }

    public String getAmountStr() {
        return new DecimalFormat("0.00").format(amount);
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
