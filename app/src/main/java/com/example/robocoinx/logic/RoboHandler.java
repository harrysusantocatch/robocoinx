package com.example.robocoinx.logic;

import android.content.Context;
import android.content.res.Resources;

import com.example.robocoinx.R;
import com.example.robocoinx.model.ProfileView;
import com.example.robocoinx.model.RollAttribute;
import com.example.robocoinx.model.RollErrorResponse;
import com.example.robocoinx.model.RollSuccessResponse;
import com.example.robocoinx.model.StaticValues;
import com.example.robocoinx.model.UserCache;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.webfolder.ui4j.api.browser.BrowserEngine;
import io.webfolder.ui4j.api.browser.BrowserFactory;
import io.webfolder.ui4j.api.browser.Page;

public class RoboHandler {

    private static Connection.Response getFirstResponse(){
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
//            FileManager.getInstance().appendLog(e);
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
    public static String getCsrfToken(Context context){
        if(csrfToken == null){
            csrfToken = getBaseCookies().get("csrf_token");
            FileManager.getInstance().writeFile(context, StaticValues.CSRF_TOKEN, csrfToken);

        }
        return csrfToken;
    }

    private static Connection.Response getLoginResponse(String email, String password){
        Connection.Response response = null;
        try {
            response = Jsoup.connect(CryptEx.toBaseDecode(StaticValues.URL_KEY_B))
                    .userAgent(StaticValues.USER_AGENT)
                    .header("Origin", CryptEx.toBaseDecode(StaticValues.URL_KEY_A))
                    .referrer(CryptEx.toBaseDecode(StaticValues.URL_KEY_S))
                    .header("Accept", "*/*")
                    .header("Accept-Language", "en-ID")
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
                    .cookies(getBaseCookies())
                    .execute();
        } catch (IOException e) {
//            FileManager.getInstance().appendLog(e);
        }
        return response;
    }

    public static Object parsingLoginResponse(Context context, String email, String password){
        Connection.Response loginResponse = getLoginResponse(email, password);
        if (loginResponse == null) return StaticValues.ERROR_GENERAL;
        try {
            Document docLogin = loginResponse.parse();
            String contentBody = docLogin.body().html();
            String[] dataLogin = contentBody.split(":");
            if(dataLogin.length < 3) return contentBody;
            UserCache userCache = new UserCache(dataLogin);
            Map<String, String> firstHomeCookies = setForFirstHomeCookies(baseCookies, userCache);
            Connection.Response homeResponse = getFirstHomeResponse(firstHomeCookies);
            if(homeResponse == null) return StaticValues.ERROR_GENERAL;
            updateCsrfToken(context, homeResponse);
            String loginAuth = homeResponse.cookie("login_auth");
            userCache.setLoginAuth(loginAuth);
            ProfileView profileView = new ProfileView(homeResponse.parse());
            String userString = new Gson().toJson(userCache);
            FileManager.getInstance().writeFile(context, StaticValues.USER_CACHE, userString);
            Map<String, String> cookies = new HashMap<>();
            cookies.put("login_auth", userCache.getLoginAuth());
            cookies.put("btc_address",userCache.getBtcAddress());
            cookies.put("password",userCache.getPassword());
            cookies.put("have_account", "1");
            FileManager.getInstance().writeFile(context, StaticValues.AUTH_COOKIES, new Gson().toJson(cookies));
            return profileView;
        } catch (IOException e) {
//            FileManager.getInstance().appendLog(e);
            return StaticValues.ERROR_GENERAL;
        }
    }

    private static void updateCsrfToken(Context context, Connection.Response response) {
        String csrfTokenRes = response.cookie(StaticValues.CSRF_TOKEN);
        if(csrfTokenRes != null){
            csrfToken = csrfTokenRes;
            FileManager.getInstance().delete(context, StaticValues.CSRF_TOKEN);
            FileManager.getInstance().writeFile(context, StaticValues.CSRF_TOKEN, csrfTokenRes);
        }
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

    private static Connection.Response getFirstHomeResponse(Map<String, String> cookies){
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
//            FileManager.getInstance().appendLog(e);
        }
        return response;
    }

    private static Connection.Response getRefreshHomeResponse(Map<String, String> cookies){
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
//            FileManager.getInstance().appendLog(e);
        }
        return response;
    }

    private static Connection.Response getRollResponse(Map<String, String> cookies, RollAttribute rollAttribute){
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
                    .data("fingerprint", rollAttribute.getFingerprint())
                    .data("client_seed", rollAttribute.getClientSeed())
                    .data("fingerprint2", rollAttribute.getFingerprint2())
                    .data("pwc", rollAttribute.getPwc())
                    .data(rollAttribute.getTokenName(), rollAttribute.getTokenValue())
                    .data(rollAttribute.getLastParam(), rollAttribute.getLastParamValue())
                    .data("g_recaptcha_response", "")
                    .cookies(cookies)
                    .execute();
        } catch (IOException e) {
//            FileManager.getInstance().appendLog(e);
        }
        return response;
    }

    private static int getNextRollTime(Document doc) {
        Elements scripts = doc.getElementsByTag("script");
        for (Element script: scripts) {
            List<DataNode> dataNodes = script.dataNodes();
            for (DataNode dataNode : dataNodes){
                String data = dataNode.getWholeData();
                if(data.contains("#time_remaining")){
                    String matcher = extractCountDownRollTime(data, "#time_remaining");
                    if (matcher != null) return (Integer.parseInt(matcher));
                }
            }
        }
        return 0;
    }

    private static String extractCountDownRollTime(String data, String regex) {
        String[] array = data.split(regex);
        String input = array[1];
        Pattern pattern = Pattern.compile("\\+.[0-9]*.\\,");
        Matcher matcher = pattern.matcher(input);
        if(matcher.find()){
            return matcher.group().substring(1, matcher.group().length()-1);
        }
        return null;
    }

    public static Object parsingRollResponse(Context context){
        String cookiesStr = FileManager.getInstance().readFile(context, StaticValues.AUTH_COOKIES);
        Type type = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> cookies = new Gson().fromJson(cookiesStr, type);
        try {
            Connection.Response homeResponse = getRefreshHomeResponse(cookies);
            if(homeResponse == null) return StaticValues.ERROR_GENERAL;
            updateCsrfToken(context, homeResponse);
            Document doc = homeResponse.parse();
            int nextRollTime = getNextRollTime(doc);
            if(nextRollTime > 0) return new RollErrorResponse("",nextRollTime);
            RollAttribute rollAttribute = new RollAttribute(context, doc);
            cookies.put("csrf_token", csrfToken);
            cookies.put("__cfduid", homeResponse.cookie("__cfduid"));
            Connection.Response rollResponse = getRollResponse(cookies, rollAttribute);
            if (rollResponse == null) return StaticValues.ERROR_GENERAL;
            Document docRoll = rollResponse.parse();
            String contentBody = docRoll.body().html();
            String[] dataRoll = contentBody.split(":");
            if(dataRoll.length == 0) return StaticValues.ERROR_GENERAL;
            if(dataRoll[0].equals("s")){
                return new RollSuccessResponse(dataRoll[2], dataRoll[3]);
            }else if(dataRoll[0].equals("e")){
                return new RollErrorResponse(dataRoll[1], Integer.parseInt(dataRoll[2]));
            }else {
                return StaticValues.ERROR_GENERAL;
            }
        } catch (IOException e) {
//            FileManager.getInstance().appendLog(e);
            return StaticValues.ERROR_GENERAL;
        }
    }

    public static Object parsingHomeResponse(Context context){
        String userString = FileManager.getInstance().readFile(context, StaticValues.USER_CACHE);
        UserCache userCache = new Gson().fromJson(userString, UserCache.class);
        Map<String, String> cookies = new HashMap<>();
        if(FileManager.getInstance().fileExists(context, StaticValues.AUTH_COOKIES)){
            String cookiesStr =  FileManager.getInstance().readFile(context, StaticValues.AUTH_COOKIES);
            Type type = new TypeToken<Map<String, String>>(){}.getType();
            cookies = new Gson().fromJson(cookiesStr, type);
        }else {
            cookies.put("login_auth", userCache.getLoginAuth());
            cookies.put("btc_address",userCache.getBtcAddress());
            cookies.put("password",userCache.getPassword());
            cookies.put("have_account", "1");
            FileManager.getInstance().writeFile(context, StaticValues.AUTH_COOKIES, new Gson().toJson(cookies));
        }

        try {
            Connection.Response homeResponse = getRefreshHomeResponse(cookies);
            if(homeResponse == null) return StaticValues.ERROR_GENERAL;
            updateCsrfToken(context, homeResponse);
            Document doc = homeResponse.parse();
            return new ProfileView(doc);
        } catch (IOException e) {
//            FileManager.getInstance().appendLog(e);
            return StaticValues.ERROR_GENERAL;
        }
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
//            FileManager.getInstance().appendLog(e);
        }
        return response;
    }


    /////////////////
    public static void getValueJS() {
        BrowserEngine browser = BrowserFactory.getWebKit();
        Page page = browser.navigate(CryptEx.toBaseDecode(StaticValues.URL_KEY_B));
        page.show();
        Object o1 = page.executeScript("$.fingerprint()");
        System.out.println(o1.toString());

        Object o2 = page.executeScript("new Fingerprint({canvas: true,screen_resolution: true,ie_activex: true}).get()");
        System.out.println(o2.toString());
    }

    public static String runScript(Context androidContextObject) {
        // Get the JavaScript in previous section
        try {

            Resources resources = androidContextObject.getResources();
            InputStream rawResource = resources.openRawResource(R.raw.config);


            Properties properties = new Properties();
            properties.load(rawResource);

            String source = properties.getProperty("jsExecute");
            String functionName = "getRhinoHello";
            Object[] functionParams = new Object[]{};
            // Every Rhino VM begins with the enter()
            // This Context is not Android's Context
            org.mozilla.javascript.Context rhino = org.mozilla.javascript.Context.enter();

            // Turn off optimization to make Rhino Android compatible
            rhino.setOptimizationLevel(-1);

            Scriptable scope = rhino.initStandardObjects();

            // This line set the javaContext variable in JavaScript
            //ScriptableObject.putProperty(scope, "javaContext", org.mozilla.javascript.Context.javaToJS(androidContextObject, scope));

            // Note the forth argument is 1, which means the JavaScript source has
            // been compressed to only one line using something like YUI
            rhino.evaluateString(scope, source, "JavaScript", 1, null);

            // We get the hello function defined in JavaScript
            Object obj = scope.get(functionName, scope);

            if (obj instanceof Function) {
                Function function = (Function) obj;
                // Call the hello function with params
                Object result = function.call(rhino, scope, scope, functionParams);
                // After the hello function is invoked, you will see logcat output

                // Finally we want to print the result of hello function
                String response = org.mozilla.javascript.Context.toString(result);
                return response;
            } else {
                return null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            // We must exit the Rhino VM
            org.mozilla.javascript.Context.exit();
        }

        return null;
    }
}
