package com.bureng.robocoinx.model.view;

import java.io.Serializable;

public class NoCaptchaSpec implements Serializable {
    public String lottery;
    public String wager;
    public String jackpot;
    public String deposit;
    public NoCaptchaSpec(String lottery, String wager, String jackpot, String deposit) {
        this.lottery = lottery;
        this.wager = wager;
        this.jackpot = jackpot;
        this.deposit = deposit;
    }
}
