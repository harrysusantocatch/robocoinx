package com.example.robocoinx.model;

import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.Serializable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProfileView implements Serializable {

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
    }

    public String getUserID(Document doc) {
        Elements elUserID = doc.getElementsByClass("left bold");
        return elUserID.text();
    }

    public String getUserID() {
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
        Elements scripts = doc.getElementsByTag("script");
        for (Element script: scripts) {
            List<DataNode> dataNodes = script.dataNodes();
            for (DataNode dataNode : dataNodes){
                String data = dataNode.getWholeData();
                if(data.contains("free_points")){
                    Pattern pattern = Pattern.compile("[0-9]*.\\)");
                    Matcher matcher = pattern.matcher(data);
                    if(matcher.find()){
                        return Integer.parseInt(matcher.group().substring(0, matcher.group().length()-1));
                    }
                }
            }
        }
        return 0;
    }

    private int getBTCBonusCountDown(Document doc) {
        Elements scripts = doc.getElementsByTag("script");
        for (Element script: scripts) {
            List<DataNode> dataNodes = script.dataNodes();
            for (DataNode dataNode : dataNodes){
                String data = dataNode.getWholeData();
                if(data.contains("fp_bonus")){
                    Pattern pattern = Pattern.compile("[0-9]*.\\)");
                    Matcher matcher = pattern.matcher(data);
                    if(matcher.find()){
                        return Integer.parseInt(matcher.group().substring(0, matcher.group().length()-1));
                    }
                }
            }
        }
        return 0;
    }

    private String getBalance(Document doc) {
        Element elBalance = doc.getElementById("balance");
        return elBalance.text();
    }

    private String getRewardPoint(Document doc) {
        Elements elRP = doc.getElementsByClass("reward_table_box br_0_0_5_5 user_reward_points font_bold");
        return elRP.text().replace(",", "");
    }

    private int getNextRollTime(Document doc) {
        Elements scripts = doc.getElementsByTag("script");
        for (Element script: scripts) {
            List<DataNode> dataNodes = script.dataNodes();
            for (DataNode dataNode : dataNodes){
                String data = dataNode.getWholeData();
                if(data.contains("#time_remaining")){
                    String matcher = extractCountDownRollTime(data, "#time_remaining");
                    if (matcher != null) return (Integer.parseInt(matcher));
                }
            }
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
