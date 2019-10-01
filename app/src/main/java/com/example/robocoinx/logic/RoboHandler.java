package com.example.robocoinx.logic;

import com.example.robocoinx.model.StaticValues;
import com.example.robocoinx.model.UserCache;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Map;

public class RoboHandler {

    private static Connection.Response getFirstResponse(){
        Connection.Response response = null;
        try {
            response = Jsoup.connect(StaticValues.URL_BASE)
                    .userAgent(StaticValues.USER_AGENT)
                    .header("Access-Control-Allow-Credentials", "true")
                    .header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept")
                    .timeout(StaticValues.TIMEOUT)
                    .method(Connection.Method.GET)
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  response;
    }

    private static Map<String, String> baseCookies;
    private static Map<String, String> getBaseCookies(){
        if(baseCookies == null) {
            if(getFirstResponse() != null) {
                baseCookies = getFirstResponse().cookies();
            }
        }
        return baseCookies;
    }

    private static String csrfToken;
    public static String getCsrfToken(){
        if(csrfToken == null){
            csrfToken = getBaseCookies().get("csrf_token");
            Cache.getInstance().getLru().put(StaticValues.CSRF_TOKEN, csrfToken);
        }
        return csrfToken;
    }

    private static Connection.Response getLoginResponse(String email, String password){
        Connection.Response response = null;
        try {
            response = Jsoup.connect(StaticValues.URL_BASE)
                    .userAgent(StaticValues.USER_AGENT)
                    .header("Origin", "https://freebitco.in")
                    .referrer("https://freebitco.in/?op=signup_page")
                    .header("Accept", "*/*")
                    .header("Accept-Language", "en-ID")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("x-csrf-token", getCsrfToken())
    //                .header("X-Requested-With", "XMLHttpRequest")
    //                .header("Accept-Encoding", "gzip, deflate, br") jadi html di encoded
                    .header("Host", "freebitco.in")
                    .header("Connection", "Keep-Alive")
                    .header("Access-Control-Allow-Credentials", "true")
                    .header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept")
                    .timeout(StaticValues.TIMEOUT)
                    .method(Connection.Method.POST)
                    .data("csrf_token", csrfToken)
                    .data("op", "login_new")
                    .data("btc_address", email)
                    .data("password", password)
                    .data("tfa_code", "")
                    .cookies(getBaseCookies())
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public static UserCache parsingLoginResponse(String email, String password){
        Connection.Response response = getLoginResponse(email, password);
        Document doc;
        try {
            doc = response.parse();
            if(response != null){
                UserCache userCache = new UserCache(doc.body().html());
                Cache.getInstance().getLru().put(StaticValues.USER_CACHE, userCache);
                return userCache;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

}
