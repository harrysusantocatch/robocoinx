package com.example.robocoinx.model;

import android.content.Context;

import com.example.robocoinx.logic.CryptEx;
import com.example.robocoinx.logic.FileManager;
import com.example.robocoinx.logic.RoboHandler;

import org.jsoup.Connection;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RollAttribute {
    public String op;
    public String fingerprint;
    public String clientSeed;
    public String fingerprint2;
    public String pwc;
    public String tokenName;
    public String tokenValue;
    public String lastParam;
    public String lastParamValue;

    public RollAttribute(Context context, Document doc){

        setOp(StaticValues.OP);
        setFingerprint(""); // TODO
        setFingerprint2(""); // TODO
        setClientSeed(getClientSeed());
        setPwc("1"); // TODO
        Elements scripts = doc.getElementsByTag("script");
        for (Element script: scripts) {
            List<DataNode> dataNodes = script.dataNodes();
            for (DataNode dataNode : dataNodes){
                String data = dataNode.getWholeData();
                if(data.contains("token_name")){
                    // token name
                    String[] dataSplit = data.split(";");
                    String varTokenName = dataSplit[1];
                    String[] tokenNameSplit = varTokenName.split(" ");
                    setTokenName(tokenNameSplit[3].replace("'", ""));

                    // last param
                    String varLastParam = dataSplit[2];
                    String[] lastParamSPlit = varLastParam.split(" ");
                    setLastParam(lastParamSPlit[3].replace("'", ""));

                }
                if(data.contains("#"+getTokenName())){
                    // token value
                    String regex = "\\(\"#"+getTokenName()+"\"\\).val\\(\"\\d*:[A-Za-z0-9]*";
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(data);
                    if(matcher.find()){
                        String tokenValueTemp = matcher.group();
                        String strReplace = "(\"#"+tokenValueTemp+"\").val(\"";
                        setTokenValue(tokenValueTemp.replace(strReplace, ""));
                    }
                }
            }
        }
        setLastParamValue(getLastParamValue());
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    public String getClientSeed() {
        if(clientSeed == null){
            String charSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
            String randomString = "";
            for (int i = 0; i < 16; i++) {
                int randomPoz = (int) Math.floor(Math.random() * charSet.length());
                randomString += charSet.substring(randomPoz, randomPoz + 1);
            }
            clientSeed = randomString;
        }
        return clientSeed;
    }

    public void setClientSeed(String clientSeed) {
        this.clientSeed = clientSeed;
    }

    public String getFingerprint2() {
        return fingerprint2;
    }

    public void setFingerprint2(String fingerprint2) {
        this.fingerprint2 = fingerprint2;
    }

    public String getPwc() {
        return pwc;
    }

    public void setPwc(String pwc) {
        this.pwc = pwc;
    }

    public String getTokenName() {
        return tokenName;
    }

    public void setTokenName(String tokenName) {
        this.tokenName = tokenName;
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public void setTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }

    public String getLastParam() {
        return lastParam;
    }

    public void setLastParam(String lastParam) {
        this.lastParam = lastParam;
    }

    public String getLastParamValue() {
        if(lastParamValue == null){
            Connection.Response lastParamValueResponse = RoboHandler.getLastParamValueResponse(lastParam);
            lastParamValue = CryptEx.getSha256Hex(lastParamValueResponse.body());
        }
        return lastParamValue;
    }

    public void setLastParamValue(String lastParamValue) {
        this.lastParamValue = lastParamValue;
    }

}
