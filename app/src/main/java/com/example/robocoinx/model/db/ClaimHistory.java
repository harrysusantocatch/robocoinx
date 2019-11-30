package com.example.robocoinx.model.db;

public class ClaimHistory {

    public String id;
    public String date;
    public String claim;
    public String balance;
    public TransactionType type;
    public String name;

    public enum TransactionType{
        withdrawal, receive, deposit
    }

}
