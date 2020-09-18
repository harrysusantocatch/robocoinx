package com.bureng.robocoinx.utils;

import com.bureng.robocoinx.model.request.RollRequest;
import com.bureng.robocoinx.model.request.SignUpRequest;

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
                    .header("Accept", "*/*")
                    .header("accept-encoding", "gzip, deflate, br")
                    .header("accept-language", "en-US,en;q=0.9")
//                    .header("content-length", "331")
                    .header("Origin", CryptEx.toBaseDecode(StaticValues.URL_KEY_A))
                    .referrer(CryptEx.toBaseDecode(StaticValues.URL_KEY_A))
                    .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                    .header("sec-fetch-mode", "cors")
                    .header("sec-fetch-site", "same-origin")
                    .userAgent(StaticValues.USER_AGENT)
                    .header("x-csrf-token", csrfToken)
                    .header("x-requested-with", "XMLHttpRequest")
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
//                    .data("g_recaptcha_response", rollRequest.gReCaptchaResponse)
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
                    .cookies(cookies) // cookies null error signup
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

    static Connection.Response getSignUpResponse(SignUpRequest request){
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
                    .data("botdetect_random", request.captchaNet)
                    .data("botdetect_response", request.captchaResp)
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
                    .ignoreContentType(true)
                    .execute();
        } catch (IOException e) {
            FileManager.getInstance().appendLog(e);
        }
        return response;
    }

    static Connection.Response setLottery(Map<String, String> cookies, String lottery){
        cookies.put("csrf_token", csrfToken);
        Connection.Response response = null;
        try {
            response = Jsoup.connect(CryptEx.toBaseDecode(StaticValues.URL_KEY_A)+
                    "/?op=purchase_lott_tickets&num="+lottery+"&csrf_token="+csrfToken)
                    .userAgent(StaticValues.USER_AGENT)
                    .timeout(StaticValues.TIMEOUT)
                    .method(Connection.Method.GET)
                    .referrer(CryptEx.toBaseDecode(StaticValues.URL_KEY_H))
                    .header("Accept", "*/*")
                    .header("x-csrf-token", csrfToken)
                    .header("x-requested-with", "XMLHttpRequest")
                    .header("sec-fetch-mode", "cors")
                    .header("sec-fetch-site", "same-origin")
                    .cookies(cookies)
                    .execute();
        }catch (IOException e){
            FileManager.getInstance().appendLog(e);
        }
        return response;
    }

    static Connection.Response getHiLo(Map<String, String> cookies, String hiLo, String clientSeed, String stake, double random){
        cookies.put("csrf_token", csrfToken);
        Connection.Response response = null;
        try {
            response = Jsoup.connect(CryptEx.toBaseDecode(StaticValues.URL_KEY_A)+
                    "/cgi-bin/bet.pl?m="+hiLo+"&client_seed="+clientSeed+"&jackpot=0&stake="+stake+"&multiplier=2.00" +
                    "&rand="+random+"&csrf_token="+csrfToken)
                    .userAgent(StaticValues.USER_AGENT)
                    .timeout(StaticValues.TIMEOUT)
                    .method(Connection.Method.GET)
                    .referrer(CryptEx.toBaseDecode(StaticValues.URL_KEY_H))
                    .header("Accept", "*/*")
                    .header("x-csrf-token", csrfToken)
                    .header("x-requested-with", "XMLHttpRequest")
                    .header("sec-fetch-mode", "cors")
                    .header("sec-fetch-site", "same-origin")
                    .cookies(cookies)
                    .execute();
        }catch (IOException e){
            FileManager.getInstance().appendLog(e);
        }
        return response;
    }

    static Connection.Response generateCaptchaNet(String fingerprint){
        Connection.Response response = null;
        try {
            response = Jsoup.connect(CryptEx.toBaseDecode(StaticValues.URL_KEY_A)+
                    "/cgi-bin/api.pl?op=generate_captchasnet&f="+fingerprint+"&csrf_token="+csrfToken)
                    .userAgent(StaticValues.USER_AGENT)
                    .timeout(StaticValues.TIMEOUT)
                    .method(Connection.Method.GET)
                    .referrer(CryptEx.toBaseDecode(StaticValues.URL_KEY_S))
                    .header("Accept", "*/*")
                    .header("x-csrf-token", csrfToken)
                    .header("x-requested-with", "XMLHttpRequest")
                    .header("sec-fetch-mode", "cors")
                    .header("sec-fetch-site", "same-origin")
                    .execute();
        }catch (IOException e){
            FileManager.getInstance().appendLog(e);
        }
        return response;
    }

    static Connection.Response botDetect(String captcha){
        Connection.Response response = null;
        try {
            response = Jsoup.connect("https://captchas.freebitco.in/botdetect/e/live/index.php?random="+captcha)
                    .userAgent(StaticValues.USER_AGENT)
                    .timeout(StaticValues.TIMEOUT)
                    .method(Connection.Method.GET)
                    .referrer(CryptEx.toBaseDecode(StaticValues.URL_KEY_S))
                    .header("Accept", "*/*")
                    .header("x-requested-with", "XMLHttpRequest")
                    .header("sec-fetch-mode", "no-cors")
                    .header("sec-fetch-site", "same-site")
                    .execute();
        }catch (IOException e){
            FileManager.getInstance().appendLog(e);
        }
        return response;
    }

    static Connection.Response getWithdrawResponse(Map<String, String> cookies, String amount, String withdrawAddress){
        Connection.Response response = null;
        try {
            response = Jsoup.connect(CryptEx.toBaseDecode(StaticValues.URL_KEY_B))
                    .header("Accept", "*/*")
                    .header("accept-encoding", "gzip, deflate, br")
                    .header("accept-language", "en-US,en;q=0.9")
//                    .header("content-length", "331")
                    .header("Origin", CryptEx.toBaseDecode(StaticValues.URL_KEY_A))
                    .referrer(CryptEx.toBaseDecode(StaticValues.URL_KEY_A))
                    .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                    .header("sec-fetch-mode", "cors")
                    .header("sec-fetch-site", "same-origin")
                    .userAgent(StaticValues.USER_AGENT)
                    .header("x-csrf-token", csrfToken)
                    .header("x-requested-with", "XMLHttpRequest")
                    .timeout(StaticValues.TIMEOUT)
                    .method(Connection.Method.POST)
                    .data("csrf_token", csrfToken)
                    .data("op", StaticValues.WITHDRAW)
                    .data("type", StaticValues.WITHDRAW_TYPE_SLOW)
                    .data("amount", amount)
                    .data("withdraw_address", withdrawAddress)
                    .cookies(cookies)
                    .execute();
        } catch (IOException e) {
            FileManager.getInstance().appendLog(e);
        }
        return response;
    }
}
