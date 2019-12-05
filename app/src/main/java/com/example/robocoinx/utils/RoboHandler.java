package com.example.robocoinx.utils;

import android.content.Context;

import com.example.robocoinx.logic.RedeemRP;
import com.example.robocoinx.model.common.UserCache;
import com.example.robocoinx.model.request.RollRequest;
import com.example.robocoinx.model.request.SignupRequest;
import com.example.robocoinx.model.response.RollErrorResponse;
import com.example.robocoinx.model.response.RollSuccessResponse;
import com.example.robocoinx.model.view.NoCaptchaSpec;
import com.example.robocoinx.model.view.ProfileView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class RoboHandler {

    public static Object getSignUpRequest(Context context) {
        Connection.Response firstResponse = RoboBrowser.getFirstResponse();
        if(firstResponse == null) return StaticValues.ERROR_GENERAL;
        RoboBrowser.baseCookies = firstResponse.cookies();
        RoboBrowser.csrfToken = RoboBrowser.baseCookies.get("csrf_token");
        FileManager.getInstance().writeFile(context, StaticValues.CSRF_TOKEN, RoboBrowser.csrfToken);
        try {
            Document document = firstResponse.parse();
            String script = updateScript(document);
            return new SignupRequest(document, script);
        } catch (IOException e) {
            e.printStackTrace();
            FileManager.getInstance().appendLog(e);
            return StaticValues.ERROR_GENERAL;
        }
    }

    private static String updateScript(Document document) {
        StringBuilder stringBuilder = new StringBuilder();
        Elements scripts = document.getElementsByTag("script");
        for (Element script: scripts) {
            String scriptData = script.toString();
            if(scriptData.contains(StaticValues.REGEX_SCRIPT_1) ||
                    scriptData.contains(StaticValues.REGEX_SCRIPT_2) ||
                    scriptData.contains(StaticValues.REGEX_SCRIPT_3) ||
                    scriptData.contains(StaticValues.REGEX_SCRIPT_4)){
                stringBuilder.append(scriptData).append(System.lineSeparator());
            }
        }
        return  stringBuilder.toString();
    }



    public static Object parsingLoginResponse(Context context, String email, String password){
        Connection.Response loginResponse = RoboBrowser.getLoginResponse(email, password);
        if (loginResponse == null) return StaticValues.ERROR_GENERAL;
        try {
            Document docLogin = loginResponse.parse();
            String contentBody = docLogin.body().html();
            String[] dataLogin = contentBody.split(":");
            if(dataLogin.length < 3) return contentBody;
            UserCache userCache = new UserCache(dataLogin);
            Map<String, String> firstHomeCookies = setForFirstHomeCookies(RoboBrowser.baseCookies, userCache);
            Connection.Response homeResponse = RoboBrowser.getFirstHomeResponse(firstHomeCookies);
            if(homeResponse == null) return StaticValues.ERROR_GENERAL;
            updateCsrfToken(context, homeResponse);
            userCache.loginAuth = homeResponse.cookie("login_auth");
            ProfileView profileView = new ProfileView(homeResponse.parse());
            new CacheContext<>(UserCache.class, context).save(userCache, StaticValues.USER_CACHE);
            Map<String, String> cookies = new HashMap<>();
            cookies.put("login_auth", userCache.loginAuth);
            cookies.put("btc_address",userCache.btcAddress);
            cookies.put("password",userCache.password);
            cookies.put("have_account", "1");
            FileManager.getInstance().writeFile(context, StaticValues.AUTH_COOKIES, new Gson().toJson(cookies));

            if (setInterestAndLottery(profileView, cookies, homeResponse)) return StaticValues.ERROR_GENERAL;

            return profileView;
        } catch (IOException e) {
            FileManager.getInstance().appendLog(e);
            return StaticValues.ERROR_GENERAL;
        }
    }

    public static Object parsingSignUpResponse(Context context, SignupRequest request){
        Connection.Response signUpResponse = RoboBrowser.getSignUpResponse(request);
        if(signUpResponse == null) return StaticValues.ERROR_GENERAL;
        try {
            Document doc = signUpResponse.parse();
            String contentBody = doc.body().html();
            String[] result = contentBody.split(":");
            if(result.length == 0) return StaticValues.ERROR_GENERAL;
            if(result[0].equalsIgnoreCase("s")){
                return parsingLoginResponse(context, request.email, request.password);
            }else if(result[0].equalsIgnoreCase("e")){
                return result[1];
            }else return StaticValues.ERROR_GENERAL;
        }catch (Exception e){
            e.printStackTrace();
            FileManager.getInstance().appendLog(e);
            return StaticValues.ERROR_GENERAL;
        }
    }

    private static boolean setInterestAndLottery(ProfileView profileView, Map<String, String> authCookies, Connection.Response homeResponse){
        authCookies.put("csrf_token", RoboBrowser.csrfToken);
        authCookies.put("__cfduid", homeResponse.cookie("__cfduid"));
        if(!profileView.enableInterest){
            // enable
            Connection.Response response = RoboBrowser.getEnableInterestResponse(authCookies);
            if(response == null) return true;
            Document doc;
            try {
                doc = response.parse();
                String content = doc.body().html();
                if(content.toLowerCase().contains("successfully")) profileView.enableInterest = true;
            } catch (IOException e) {
                e.printStackTrace();
                FileManager.getInstance().appendLog(e);
            }
        }

        if(!profileView.disableLottery){
            // disable
            Connection.Response response = RoboBrowser.getDisableLotteryResponse(authCookies);
            if(response == null) return true;
            Document doc;
            try {
                doc = response.parse();
                String content = doc.body().html();
                if(content.toLowerCase().contains("successfully")) profileView.disableLottery = true;
            } catch (IOException e) {
                e.printStackTrace();
                FileManager.getInstance().appendLog(e);
            }
        }
        return false;
    }

    private static void updateCsrfToken(Context context, Connection.Response response) {
        String csrfTokenRes = response.cookie(StaticValues.CSRF_TOKEN);
        if(csrfTokenRes != null){
            RoboBrowser.csrfToken = csrfTokenRes;
            FileManager.getInstance().delete(context, StaticValues.CSRF_TOKEN);
            FileManager.getInstance().writeFile(context, StaticValues.CSRF_TOKEN, csrfTokenRes);
        }
    }

    private static Map<String, String> setForFirstHomeCookies(Map<String, String> baseCookies, UserCache userCache) {
        Map<String, String> firstHomeCookies = new HashMap<>();
        String btcAddress = userCache.btcAddress != null ? userCache.btcAddress : "";
        String passwordEncoded = userCache.password != null ? userCache.password : "";
        firstHomeCookies.put("__cfduid", baseCookies.get("__cfduid"));
        firstHomeCookies.put("btc_address",btcAddress);
        firstHomeCookies.put("password",passwordEncoded);
        firstHomeCookies.put("have_account", "1");
        return firstHomeCookies;
    }

    public static Object parsingRollResponse(Context context){
        String cookiesStr = FileManager.getInstance().readFile(context, StaticValues.AUTH_COOKIES);
        Type type = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> cookies = new Gson().fromJson(cookiesStr, type);
        try {
            Connection.Response homeResponse = RoboBrowser.getRefreshHomeResponse(cookies);
            if(homeResponse == null) return StaticValues.ERROR_GENERAL;
            updateCsrfToken(context, homeResponse);
            Document doc = homeResponse.parse();
            ProfileView pp = new ProfileView(doc);
            if(pp.haveCaptcha) return new RollErrorResponse("resolve captcha", 300000);
            if(pp.nextRollTime > 0) return new RollErrorResponse("",pp.nextRollTime);

            cookies.put("csrf_token", RoboBrowser.csrfToken);
            cookies.put("__cfduid", homeResponse.cookie("__cfduid"));
            cookies.put("mobile", "1");
            cookies.put("_gat", "1");
            cookies.put("hide_push_msg", "1");

            redeemPoint(cookies, pp);

            RollRequest rollRequest = new RollRequest(context,doc, pp.haveCaptcha);
            Connection.Response rollResponse = RoboBrowser.getRollResponse(cookies, rollRequest);
            if (rollResponse == null) return StaticValues.ERROR_GENERAL;
            Document docRoll = rollResponse.parse();
            String contentBody = docRoll.body().html();
            String[] dataRoll = contentBody.split(":");
            if(dataRoll.length == 0) return StaticValues.ERROR_GENERAL;
            if(dataRoll[0].equals("s")){
                return new RollSuccessResponse(dataRoll[2], dataRoll[3]);
            }else if(dataRoll[0].equals("e")){
                if(dataRoll.length == 2){
                    return new RollErrorResponse(dataRoll[1], 0);
                }else
                    return new RollErrorResponse(dataRoll[1], Integer.parseInt(dataRoll[2]));
            }else {
                return StaticValues.ERROR_GENERAL;
            }
        } catch (IOException e) {
            FileManager.getInstance().appendLog(e);
            return StaticValues.ERROR_GENERAL;
        }
    }

    private static void redeemPoint(Map<String, String> cookies, ProfileView pp) throws IOException {
        if(pp.rpBonusTime == 0){
            RedeemRP redeem = new RedeemRP(pp);
            if(redeem.idP != null){
                Connection.Response redeemResponse = RoboBrowser.getRedeemRPResponse(cookies, redeem.idP, redeem.pointP);
                if (redeemResponse != null){
                    Document document = redeemResponse.parse();
                    String content = document.body().html();
                    FileManager.getInstance().appendLog("redeem poin: "+content);
                }
            }
            if(redeem.idB != null){
                Connection.Response redeemResponse = RoboBrowser.getRedeemRPResponse(cookies, redeem.idB, redeem.pointB);
                if (redeemResponse != null){
                    Document document = redeemResponse.parse();
                    String content = document.body().html();
                    FileManager.getInstance().appendLog("redeem poin: "+content);
                }
            }
        }
    }

    public static Object parsingHomeResponse(Context context){
        UserCache userCache = new CacheContext<>(UserCache.class, context).get(StaticValues.USER_CACHE);
        Map<String, String> cookies = new HashMap<>();
        if(FileManager.getInstance().fileExists(context, StaticValues.AUTH_COOKIES)){
            String cookiesStr =  FileManager.getInstance().readFile(context, StaticValues.AUTH_COOKIES);
            Type type = new TypeToken<Map<String, String>>(){}.getType();
            cookies = new Gson().fromJson(cookiesStr, type);
        }else {
            cookies.put("login_auth", userCache.loginAuth);
            cookies.put("btc_address",userCache.btcAddress);
            cookies.put("password",userCache.password);
            cookies.put("have_account", "1");
            FileManager.getInstance().writeFile(context, StaticValues.AUTH_COOKIES, new Gson().toJson(cookies));
        }
        try {
            Connection.Response homeResponse = RoboBrowser.getRefreshHomeResponse(cookies);
            if(homeResponse == null) return StaticValues.ERROR_GENERAL;
            updateCsrfToken(context, homeResponse);
            Document doc = homeResponse.parse();
            ProfileView profileView = new ProfileView(doc);
            if(profileView.haveCaptcha){
                Connection.Response response = RoboBrowser.getUserStatistic(profileView.socketId, profileView.socketPass);
                String result = response.body();
                JSONObject root = new JSONObject(result);
                JSONObject noCaptchaSpecJsn = root.getJSONObject("no_captcha_gbr");
                NoCaptchaSpec noCaptchaSpec = new NoCaptchaSpec(noCaptchaSpecJsn.getString("lottery_to_unblock"),
                        noCaptchaSpecJsn.getString("wager_to_unblock"),
                        noCaptchaSpecJsn.getString("jackpot_to_unblock"),
                        noCaptchaSpecJsn.getString("deposit_to_unblock"));
            }
            if (setInterestAndLottery(profileView, cookies, homeResponse)) return StaticValues.ERROR_GENERAL;
            return profileView;
        } catch (JSONException | IOException e) {
            FileManager.getInstance().appendLog(e);
            return StaticValues.ERROR_GENERAL;
        }
    }
}
