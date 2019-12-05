package com.example.robocoinx.utils;

import com.example.robocoinx.model.request.RollRequest;
import com.example.robocoinx.model.request.SignupRequest;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.Map;

public class RoboBrowser {

    static Map<String, String> baseCookies;
    static String csrfToken;

    static Connection.Response getFirstResponse(){
        Connection.Response response = null;
        try {
            response = Jsoup.connect(CryptEx.toBaseDecode(StaticValues.URL_KEY_B))
                    .userAgent(StaticValues.USER_AGENT)
                    .header("Access-Control-Allow-Credentials", "true")
                    .header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept")
                    .timeout(StaticValues.TIMEOUT)
                    .method(Connection.Method.GET)
                    .execute();
        } catch (IOException e) {
            FileManager.getInstance().appendLog(e);
        }
        return  response;
    }

    static Connection.Response getLoginResponse(String email, String password){
        Connection.Response response = null;
        try {
            response = Jsoup.connect(CryptEx.toBaseDecode(StaticValues.URL_KEY_B))
                    .userAgent(StaticValues.USER_AGENT)
                    .header("Origin", CryptEx.toBaseDecode(StaticValues.URL_KEY_A))
                    .referrer(CryptEx.toBaseDecode(StaticValues.URL_KEY_S))
                    .header("Accept", "*/*")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("x-csrf-token", csrfToken)
                    .header("Host", CryptEx.toBaseDecode(StaticValues.URL_KEY_O))
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
                    .cookies(baseCookies)
                    .execute();
        } catch (IOException e) {
            FileManager.getInstance().appendLog(e);
        }
        return response;
    }

    static Connection.Response getFirstHomeResponse(Map<String, String> cookies){
        Connection.Response response = null;
        try {
            response = Jsoup.connect(CryptEx.toBaseDecode(StaticValues.URL_KEY_H))
                    .userAgent(StaticValues.USER_AGENT)
                    .referrer(CryptEx.toBaseDecode(StaticValues.URL_KEY_S))
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .header("Accept-Language", "en-ID")
                    .header("Upgrade-Insecure-Requests", "1")
                    //                .header("Accept-Encoding", "gzip, deflate, br") // jadi html di encoded
                    .header("Host", CryptEx.toBaseDecode(StaticValues.URL_KEY_O))
                    .header("Connection", "Keep-Alive")
                    .timeout(StaticValues.TIMEOUT)
                    .method(Connection.Method.GET)
                    .cookies(cookies)
                    .execute();
        } catch (IOException e) {
            FileManager.getInstance().appendLog(e);
        }
        return response;
    }

    static Connection.Response getRollResponse(Map<String, String> cookies, RollRequest rollRequest){
        Connection.Response response = null;
        try {
            response = Jsoup.connect(CryptEx.toBaseDecode(StaticValues.URL_KEY_B))
                    .userAgent(StaticValues.USER_AGENT)
                    .header("Origin", CryptEx.toBaseDecode(StaticValues.URL_KEY_A))
                    .referrer(CryptEx.toBaseDecode(StaticValues.URL_KEY_A))
                    .header("Accept", "*/*")
                    .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                    .header("x-csrf-token", csrfToken)
                    .header("x-requested-with", "XMLHttpRequest")
                    .header("sec-fetch-mode", "cors")
                    .header("sec-fetch-site", "same-origin")
                    .timeout(StaticValues.TIMEOUT)
                    .method(Connection.Method.POST)
                    .data("csrf_token", csrfToken)
                    .data("op", StaticValues.OP)
                    .data("fingerprint", rollRequest.fingerprint)
                    .data("client_seed", rollRequest.clientSeed)
                    .data("fingerprint2", rollRequest.fingerprint2)
                    .data("pwc", rollRequest.pwc)
                    .data(rollRequest.tokenName, rollRequest.tokenValue)
                    .data(rollRequest.lastParam, rollRequest.lastParamValue)
                    .data("g_recaptcha_response", rollRequest.gReCaptchaResponse)
                    .cookies(cookies)
                    .execute();
        } catch (IOException e) {
            FileManager.getInstance().appendLog(e);
        }
        return response;
    }

    public static Connection.Response getLastParamValueResponse(String lastParam){
        String url = CryptEx.toBaseDecode(StaticValues.URL_KEY_C) + lastParam + "&csrf_token=" + csrfToken;
        Connection.Response response = null;
        try {
            response = Jsoup.connect(url)
                    .userAgent(StaticValues.USER_AGENT)
                    .header("Access-Control-Allow-Credentials", "true")
                    .header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept")
                    .timeout(StaticValues.TIMEOUT)
                    .method(Connection.Method.GET)
                    .execute();
        } catch (IOException e) {
            FileManager.getInstance().appendLog(e);
        }
        return response;
    }

    static Connection.Response getEnableInterestResponse(Map<String, String> cookies){
        Connection.Response response = null;
        try {
            response = Jsoup.connect(CryptEx.toBaseDecode(StaticValues.URL_KEY_IN)+"&csrf_token="+csrfToken)
                    .userAgent(StaticValues.USER_AGENT)
                    .referrer(CryptEx.toBaseDecode(StaticValues.URL_KEY_H))
                    .header("Accept", "*/*")
                    .header("x-csrf-token", csrfToken)
                    .header("x-requested-with", "XMLHttpRequest")
                    .header("sec-fetch-mode", "cors")
                    .header("sec-fetch-site", "same-origin")
                    .timeout(StaticValues.TIMEOUT)
                    .method(Connection.Method.GET)
                    .cookies(cookies)
                    .execute();
        } catch (IOException e) {
            FileManager.getInstance().appendLog(e);
        }
        return  response;
    }

    static Connection.Response getDisableLotteryResponse(Map<String, String> cookies){
        Connection.Response response = null;
        try {
            response = Jsoup.connect(CryptEx.toBaseDecode(StaticValues.URL_KEY_LT)+"&csrf_token="+csrfToken)
                    .userAgent(StaticValues.USER_AGENT)
                    .referrer(CryptEx.toBaseDecode(StaticValues.URL_KEY_H))
                    .header("Accept", "*/*")
                    .header("x-csrf-token", csrfToken)
                    .header("x-requested-with", "XMLHttpRequest")
                    .header("sec-fetch-mode", "cors")
                    .header("sec-fetch-site", "same-origin")
                    .timeout(StaticValues.TIMEOUT)
                    .method(Connection.Method.GET)
                    .cookies(cookies)
                    .execute();
        } catch (IOException e) {
            FileManager.getInstance().appendLog(e);
        }
        return  response;
    }

    static Connection.Response getUserStatistic(String socketId, String socketPass){
        Connection.Response response = null;
        try {
            response = Jsoup.connect(CryptEx.toBaseDecode(StaticValues.URL_KEY_A)+"/stats_new_private/?u="+socketId+"&p="+socketPass+"&f=user_stats&csrf_token="+csrfToken)
                    .userAgent(StaticValues.USER_AGENT)
                    .referrer(CryptEx.toBaseDecode(StaticValues.URL_KEY_H))
                    .header("Accept", "*/*")
                    .header("Content-Type", "application/json")
                    .ignoreContentType(true)
                    .header("x-csrf-token", csrfToken)
                    .header("x-requested-with", "XMLHttpRequest")
                    .header("sec-fetch-mode", "cors")
                    .header("sec-fetch-site", "same-origin")
                    .timeout(StaticValues.TIMEOUT)
                    .method(Connection.Method.GET)
                    .execute();
        }catch (IOException e) {
            FileManager.getInstance().appendLog(e);
        }
        return response;
    }

    static Connection.Response getRedeemRPResponse(Map<String, String> cookies, String id, String point){
        Connection.Response response = null;
        try {
            response = Jsoup.connect(CryptEx.toBaseDecode(StaticValues.URL_KEY_A)+"?op=redeem_rewards&id=" + id + "&points=" + point)
                    .userAgent(StaticValues.USER_AGENT)
                    .referrer(CryptEx.toBaseDecode(StaticValues.URL_KEY_H))
                    .header("Accept", "*/*")
                    .header("x-csrf-token", csrfToken)
                    .header("x-requested-with", "XMLHttpRequest")
                    .header("sec-fetch-mode", "cors")
                    .header("sec-fetch-site", "same-origin")
                    .timeout(StaticValues.TIMEOUT)
                    .method(Connection.Method.GET)
                    .cookies(cookies)
                    .execute();
        } catch (IOException e) {
            FileManager.getInstance().appendLog(e);
        }
        return  response;
    }

    static Connection.Response getSignUpResponse(SignupRequest request){
        Connection.Response response = null;
        try {
            response = Jsoup.connect(CryptEx.toBaseDecode(StaticValues.URL_KEY_B))
                    .userAgent(StaticValues.USER_AGENT)
                    .header("Origin", CryptEx.toBaseDecode(StaticValues.URL_KEY_A))
                    .referrer(CryptEx.toBaseDecode(StaticValues.URL_KEY_S))
                    .header("Accept", "*/*")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("x-csrf-token", csrfToken)
                    .header("Host", CryptEx.toBaseDecode(StaticValues.URL_KEY_O))
                    .header("Connection", "Keep-Alive")
                    .header("Access-Control-Allow-Credentials", "true")
                    .header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept")
                    .timeout(StaticValues.TIMEOUT)
                    .method(Connection.Method.POST)
                    .data("op", request.op)
                    .data("btc_address", request.btcAddress)
                    .data("password", request.password)
                    .data("email", request.email)
                    .data("fingerprint", request.fingerprint)
                    .data("referrer", request.referrer)
                    .data("tag", request.tag)
                    .data("token", request.token)
                    .cookies(baseCookies)
                    .execute();
        } catch (IOException e) {
            FileManager.getInstance().appendLog(e);
        }
        return response;
    }

    static Connection.Response getRefreshHomeResponse(Map<String, String> cookies){
        Connection.Response response = null;
        try {
            response = Jsoup.connect(CryptEx.toBaseDecode(StaticValues.URL_KEY_H))
                    .userAgent(StaticValues.USER_AGENT)
                    .referrer(CryptEx.toBaseDecode(StaticValues.URL_KEY_S))
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .header("Upgrade-Insecure-Requests", "1")
                    .header("Host", CryptEx.toBaseDecode(StaticValues.URL_KEY_O))
                    .header("Connection", "Keep-Alive")
                    .timeout(StaticValues.TIMEOUT)
                    .method(Connection.Method.GET)
                    .cookies(cookies)
                    .execute();
        } catch (IOException e) {
            FileManager.getInstance().appendLog(e);
        }
        return response;
    }
}
