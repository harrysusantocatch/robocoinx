package com.example.robocoinx.model.view;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
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
    public boolean disableLottery;
    public boolean enableInterest;
    public boolean haveCaptcha;
    public String socketPass;
    public String socketId;

    public ProfileView(Document doc) {
        userID = getUserID(doc);
        balance = getBalance(doc);
        rewardPoint = getRewardPoint(doc);
        nextRollTime = getNextRollTime(doc);
        rpBonusTime = getRPBonusCountDown(doc);
        btcBonusTime = getBTCBonusCountDown(doc);
        disableLottery = getDisableLottery(doc);
        enableInterest = getEnableInterest(doc);
        haveCaptcha = getCaptchaFlag(doc);
        socketPass = getSocketPass(doc);
        socketId = getSocketId(doc);
    }

    private String getSocketId(Document doc) {
        Elements scripts = doc.getElementsByTag("script");
        for (Element script: scripts) {
            List<DataNode> dataNodes = script.dataNodes();
            for (DataNode dataNode : dataNodes){
                String data = dataNode.getWholeData();
                if(data.contains("socket_userid")){
                    String[] dataSplit = data.split(";");
                    for (String datas : dataSplit) {
                        if(datas.contains("socket_userid")){
                            String[] socketPassSplit = datas.split(" ");
                            String findText = socketPassSplit[3].replace("'", "");
                            return findText;
                        }
                    }
                }
            }
        }
        return null;
    }

    private String getSocketPass(Document doc) {
        Elements scripts = doc.getElementsByTag("script");
        for (Element script: scripts) {
            List<DataNode> dataNodes = script.dataNodes();
            for (DataNode dataNode : dataNodes){
                String data = dataNode.getWholeData();
                if(data.contains("socket_password")){
                    String[] dataSplit = data.split(";");
                    for (String datas : dataSplit) {
                        if(datas.contains("socket_password")){
                            String[] socketPassSplit = datas.split(" ");
                            String findText = socketPassSplit[3].replace("'", "");
                            return findText;
                        }
                    }
                }
            }
        }
        return null;
    }

    private boolean getCaptchaFlag(Document doc) {
        Elements scripts = doc.getElementsByTag("script");
        for (Element script: scripts) {
            List<DataNode> dataNodes = script.dataNodes();
            for (DataNode dataNode : dataNodes){
                String data = dataNode.getWholeData();
                if(data.contains("captcha_type")){
                    String[] dataSplit = data.split(";");
                    for (String datas : dataSplit) {
                        if(datas.contains("captcha_type")){
                            String[] tokenNameSplit = datas.split(" ");
                            String findText = tokenNameSplit[3].replace("'", "");
                            if(findText.equalsIgnoreCase("11"))
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean getEnableInterest(Document doc) {
        Element element = doc.getElementById("disable_interest_checkbox");
        Attributes attributes = element.attributes();
        for (Attribute atr : attributes ) {
            if(atr.getKey().equalsIgnoreCase("checked")){
                return false;
            }
        }
        return true;
    }

    private boolean getDisableLottery(Document doc) {
        Element element = doc.getElementById("disable_lottery_checkbox");
        Attributes attributes = element.attributes();
        for (Attribute atr : attributes ) {
            if(atr.getKey().equalsIgnoreCase("checked")){
                return true;
            }
        }
        return false;
    }

    private String getUserID(Document doc) {
        Elements elUserID = doc.getElementsByClass("left bold");
        return elUserID.text();
    }

    private int getRPBonusCountDown(Document doc) {
        Elements scripts = doc.getElementsByTag("script");
        for (Element script: scripts) {
            List<DataNode> dataNodes = script.dataNodes();
            for (DataNode dataNode : dataNodes){
                String data = dataNode.getWholeData();
                if(data.contains("free_points")){
                    Pattern pattern = Pattern.compile("\"free_points\",[0-9]*");
                    Matcher matcher = pattern.matcher(data);
                    if(matcher.find()){
                        String findText = matcher.group();
                        findText = findText.replace("\"free_points\",", "");
                        return Integer.parseInt(findText);
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
                    Pattern pattern = Pattern.compile("\"fp_bonus\",[0-9]*");
                    Matcher matcher = pattern.matcher(data);
                    if(matcher.find()){
                        String findText = matcher.group();
                        findText = findText.replace("\"fp_bonus\",", "");
                        return Integer.parseInt(findText);
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
                    String matcher = extractCountDownRollTime(data);
                    if (matcher != null) return (Integer.parseInt(matcher));
                }
            }
        }
        return 0;
    }

    private String extractCountDownRollTime(String data) {
        String[] array = data.split("#time_remaining");
        String input = array[1];
        Pattern pattern = Pattern.compile("\\+.[0-9]*.,");
        Matcher matcher = pattern.matcher(input);
        if(matcher.find()){
            return matcher.group().substring(1, matcher.group().length()-1);
        }
        return null;
    }

}
