package com.example.robocoinx.model.request;

import android.content.Context;

import com.example.robocoinx.logic.CryptEx;
import com.example.robocoinx.logic.FileManager;
import com.example.robocoinx.logic.RoboHandler;
import com.example.robocoinx.model.StaticValues;

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

        op = StaticValues.OP;
        fingerprint = "3a9c7c414c5b85342084560cf69eec93"; // TODO
        fingerprint2 = "1457546166"; // TODO
        clientSeed = getClientSeed();
        pwc = "1"; // TODO
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
                    tokenName = tokenNameSplit[3].replace("'", "");

                    // last param
                    String varLastParam = dataSplit[2];
                    String[] lastParamSPlit = varLastParam.split(" ");
                    lastParam = lastParamSPlit[3].replace("'", "");

                    // token value
                    String varTokenValue = dataSplit[38];
                    String[] tokenValueSPlit = varTokenValue.split(" ");
                    tokenValue = tokenValueSPlit[3].replace("'", "");
                }
            }
        }
        lastParamValue = getLastParamValue();
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

    public String getLastParamValue() {
        if(lastParamValue == null){
            Connection.Response lastParamValueResponse = RoboHandler.getLastParamValueResponse(lastParam);
            lastParamValue = CryptEx.getSha256Hex(lastParamValueResponse.body());
        }
        return lastParamValue;
    }
}
