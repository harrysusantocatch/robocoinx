package com.example.robocoinx.logic;

import com.example.robocoinx.model.StaticValues;
import com.example.robocoinx.model.UserCache;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;
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
            UserCache userCache = null;
            if(response != null){
                String data = doc.body().html();
                if(response != null || data.length() > 0) {
                    String[] respArray = data.split(":");
                    if(respArray.length > 2){
                        userCache = new UserCache(respArray);
                        Map<String, String> firstHomeCookies = setForFirstHomeCookies(baseCookies, userCache);
                        Connection.Response homeResponse = getFirstHomeResponse(firstHomeCookies);
                        String loginAuth = homeResponse.cookie("login_auth");
                        userCache.setLoginAuth(loginAuth);
                        System.out.println("========== home loginAuth======== " +loginAuth);
                        System.out.println("====================home page================");
                        System.out.println(homeResponse.parse().html());
                        System.out.println("====================home page================");
                        Cache.getInstance().getLru().put(StaticValues.USER_CACHE, userCache);
                    }
                }
                return userCache;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    private static Map<String, String> setForFirstHomeCookies(Map<String, String> baseCookies, UserCache userCache) {
        Map<String, String> firstHomeCookies = new HashMap<>();
        String btcAddress = userCache.getBtcAddress() != null ? userCache.getBtcAddress() : "";
        String passwordEncoded = userCache.getPassword() != null ? userCache.getPassword() : "";
        firstHomeCookies.put("__cfduid", baseCookies.get("__cfduid"));
        firstHomeCookies.put("btc_address",btcAddress);
        firstHomeCookies.put("password",passwordEncoded);
        firstHomeCookies.put("have_account", "1");
        return firstHomeCookies;
    }

    private static Connection.Response getFirstHomeResponse(Map<String, String> cookies) throws IOException {
        Connection.Response response = Jsoup.connect(StaticValues.URL_HOME)
                .userAgent(StaticValues.USER_AGENT)
                .referrer("https://freebitco.in/?op=signup_page")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .header("Accept-Language", "en-ID")
                .header("Upgrade-Insecure-Requests", "1")
//                .header("Accept-Encoding", "gzip, deflate, br") // jadi html di encoded
                .header("Host", "freebitco.in")
                .header("Connection", "Keep-Alive")
                .timeout(StaticValues.TIMEOUT)
                .method(Connection.Method.GET)
                .cookies(cookies)
                .execute();
        return response;
    }
}
