package com.example.robocoinx.model;

public class UserProfileView {

    public String userID;
    public double balance;
    public int rewardPoint;
    public boolean hasCaptcha;
    public int lotteryTicket;
    public boolean isActiveLottery;
    public long nextRollTime;
    public long rewardPointTime;
    public long btcBonusTime;
    public long lotteryBonusTime;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public int getRewardPoint() {
        return rewardPoint;
    }

    public void setRewardPoint(int rewardPoint) {
        this.rewardPoint = rewardPoint;
    }

    public boolean isHasCaptcha() {
        return hasCaptcha;
    }

    public void setHasCaptcha(boolean hasCaptcha) {
        this.hasCaptcha = hasCaptcha;
    }

    public int getLotteryTicket() {
        return lotteryTicket;
    }

    public void setLotteryTicket(int lotteryTicket) {
        this.lotteryTicket = lotteryTicket;
    }

    public boolean isActiveLottery() {
        return isActiveLottery;
    }

    public void setActiveLottery(boolean activeLottery) {
        isActiveLottery = activeLottery;
    }

    public long getNextRollTime() {
        return nextRollTime;
    }

    public void setNextRollTime(long nextRollTime) {
        this.nextRollTime = nextRollTime;
    }

    public long getRewardPointTime() {
        return rewardPointTime;
    }

    public void setRewardPointTime(long rewardPointTime) {
        this.rewardPointTime = rewardPointTime;
    }

    public long getBtcBonusTime() {
        return btcBonusTime;
    }

    public void setBtcBonusTime(long btcBonusTime) {
        this.btcBonusTime = btcBonusTime;
    }

    public long getLotteryBonusTime() {
        return lotteryBonusTime;
    }

    public void setLotteryBonusTime(long lotteryBonusTime) {
        this.lotteryBonusTime = lotteryBonusTime;
    }
}
