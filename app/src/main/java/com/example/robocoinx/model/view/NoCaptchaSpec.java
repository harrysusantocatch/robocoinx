package com.example.robocoinx.model.view;

public class NoCaptchaSpec {
    private String lottery;
    private String wager;
    private String jackpot;
    private String deposit;
    public NoCaptchaSpec(String lottery, String wager, String jackpot, String deposit) {
        this.lottery = lottery;
        this.wager = wager;
        this.jackpot = jackpot;
        this.deposit = deposit;
    }
}
