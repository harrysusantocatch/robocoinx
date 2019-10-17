package com.example.robocoinx.model;

public class RollSuccessResponse {

    public String balance;
    public String claim;

    public RollSuccessResponse(String balance, String claim){
        this.balance = balance;
        this.claim = claim;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getClaim() {
        return claim;
    }

    public void setClaim(String claim) {
        this.claim = claim;
    }
}
