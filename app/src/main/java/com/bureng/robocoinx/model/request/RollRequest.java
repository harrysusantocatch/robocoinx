package com.bureng.robocoinx.model.request;

import android.content.Context;

import com.bureng.robocoinx.model.db.Fingerprint;
import com.bureng.robocoinx.utils.CacheContext;
import com.bureng.robocoinx.utils.CryptEx;
import com.bureng.robocoinx.utils.RoboBrowser;
import com.bureng.robocoinx.utils.StaticValues;

import org.jsoup.Connection;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;

public class RollRequest {
    public String op;
    public String fingerprint;
    public String clientSeed;
    public String fingerprint2;
    public String pwc;
    public String tokenName;
    public String tokenValue;
    public String lastParam;
    public String lastParamValue;
    public String gReCaptchaResponse;

    public RollRequest(Context ctx, Document doc, boolean haveCaptcha){
        Fingerprint f = new CacheContext<>(Fingerprint.class, ctx)
                .get(StaticValues.FINGERPRINT);
        op = StaticValues.OP;
        fingerprint = f.fingerprint1;
        fingerprint2 = f.fingerprint2;
        clientSeed = getClientSeed();
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
        if (haveCaptcha) {
            pwc = "1";
            gReCaptchaResponse = getCaptchaResponse();
        }
        else {
            pwc = "1";
            gReCaptchaResponse = "";
        }
    }

    private String getCaptchaResponse() {
        // TODO
        return "";
    }

    private String getClientSeed() {
        if(clientSeed == null){
            String charSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
            StringBuilder randomString = new StringBuilder();
            for (int i = 0; i < 16; i++) {
                int randomPoz = (int) Math.floor(Math.random() * charSet.length());
                randomString.append(charSet.substring(randomPoz, randomPoz + 1));
            }
            clientSeed = randomString.toString();
        }
        return clientSeed;
    }

    private String getLastParamValue() {
        if(lastParamValue == null){
            Connection.Response lastParamValueResponse = RoboBrowser.getLastParamValueResponse(lastParam);
            lastParamValue = CryptEx.getSha256Hex(lastParamValueResponse.body());
        }
        return lastParamValue;
    }
}
