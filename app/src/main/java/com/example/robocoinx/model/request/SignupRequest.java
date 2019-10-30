package com.example.robocoinx.model.request;

import com.example.robocoinx.utils.StaticValues;

import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.Serializable;
import java.util.List;

public class SignupRequest implements Serializable {
    public String op;
    public String btcAddress;
    public String password;
    public String email;
    public String fingerprint;
    public String referrer;
    public String tag;
    public String token;
    public String script;

    public SignupRequest(Document doc, String sc){
        op = StaticValues.OP_SIGNUP;
        token = getToken(doc);
        script = sc;
        fingerprint = ""; // todo
        referrer = ""; // todo
    }

    private String getToken(Document doc) {
        Elements scripts = doc.getElementsByTag("script");
        for (Element script: scripts) {
            List<DataNode> dataNodes = script.dataNodes();
            for (DataNode dataNode : dataNodes){
                String data = dataNode.getWholeData();
                if(data.contains("signup_token")){
                    // token name
                    String[] dataSplit = data.split(";");
                    for (String datas : dataSplit) {
                        if(datas.contains("signup_token")){
                            String[] tokenNameSplit = datas.split(" ");
                            return tokenNameSplit[3].replace("'", "");
                        }
                    }
                }
            }
        }
        return null;
    }
}
