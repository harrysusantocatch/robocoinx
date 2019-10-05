package com.example.robocoinx.model;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProfileView {

    public String userID;
    public String balance;
    public String rewardPoint;
    public int nextRollTime;
    public int rpBonusTime;
    public int btcBonusTime;
    public boolean hasCaptcha;

    public ProfileView(Document doc) {
        setUserID(getUserID(doc));
        setBalance(getBalance(doc));
        setRewardPoint(getRewardPoint(doc));
        setNextRollTime(getNextRollTime(doc));
        setRpBonusTime(getRPBonusCountDown(doc));
        setBtcBonusTime(getBTCBonusCountDown(doc));
        setHasCaptcha(false);
    }

    public String getUserID(Document doc) {
        // TODO
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getRewardPoint() {
        return rewardPoint;
    }

    public void setRewardPoint(String rewardPoint) {
        this.rewardPoint = rewardPoint;
    }

    public int getNextRollTime() {
        return nextRollTime;
    }

    public void setNextRollTime(int nextRollTime) {
        this.nextRollTime = nextRollTime;
    }

    public int getRpBonusTime() {
        return rpBonusTime;
    }

    public void setRpBonusTime(int rpBonusTime) {
        this.rpBonusTime = rpBonusTime;
    }

    public int getBtcBonusTime() {
        return btcBonusTime;
    }

    public void setBtcBonusTime(int btcBonusTime) {
        this.btcBonusTime = btcBonusTime;
    }

    public boolean isHasCaptcha() {
        return hasCaptcha;
    }

    public void setHasCaptcha(boolean hasCaptcha) {
        this.hasCaptcha = hasCaptcha;
    }

    private int getRPBonusCountDown(Document doc) {
        String[] split = doc.html().split("BonusEndCountdown\\(\"fp_bonus\",");
        if(split.length > 1){
            Pattern pattern = Pattern.compile("[0-9]*.\\)");
            Matcher matcher = pattern.matcher(split[1]);
            if(matcher.find()){
                return Integer.parseInt(matcher.group().substring(0, matcher.group().length()-1));
            }
        }
        return 0;
    }

    private int getBTCBonusCountDown(Document doc) {
        String[] split = doc.html().split("BonusEndCountdown\\(\"free_points\",");
        if(split.length > 1){
            Pattern pattern = Pattern.compile("[0-9]*.\\)");
            Matcher matcher = pattern.matcher(split[1]);
            if(matcher.find()){
                return Integer.parseInt(matcher.group().substring(0, matcher.group().length()-1));
            }
        }
        return 0;
    }

    private String getBalance(Document doc) {
        Element elBalance = doc.getElementById("balance");
        String balance = elBalance.text();
        return balance;
    }

    private String getRewardPoint(Document doc) {
        Elements elRP = doc.getElementsByClass("reward_table_box br_0_0_5_5 user_reward_points font_bold");
        String rp = elRP.text().replace(",", "");
        return rp;
    }

    private int getNextRollTime(Document doc) {
        String data = doc.html();
        if(data.contains("free_play_time_remaining")){
            String matcher = extractCountDownRollTime(data, "free_play_time_remaining");
            if (matcher != null) return (Integer.parseInt(matcher));
        }else if(data.contains("#time_remaining")){
            String matcher = extractCountDownRollTime(data, "#time_remaining");
            if (matcher != null) return (Integer.parseInt(matcher));
        }
        return 0;
    }

    private String extractCountDownRollTime(String data, String regex) {
        String[] array = data.split(regex);
        String input = array[1];
        Pattern pattern = Pattern.compile("\\+.[0-9]*.\\,");
        Matcher matcher = pattern.matcher(input);
        if(matcher.find()){
            return matcher.group().substring(1, matcher.group().length()-1);
        }
        return null;
    }

}
