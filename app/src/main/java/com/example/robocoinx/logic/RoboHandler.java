package com.example.robocoinx.logic;

import android.content.Context;
import android.content.res.Resources;

import com.example.robocoinx.R;
import com.example.robocoinx.model.ProfileView;
import com.example.robocoinx.model.StaticValues;
import com.example.robocoinx.model.UserCache;
import com.google.gson.Gson;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import io.webfolder.ui4j.api.browser.BrowserEngine;
import io.webfolder.ui4j.api.browser.BrowserFactory;
import io.webfolder.ui4j.api.browser.Page;

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
            long d1 = System.currentTimeMillis();
            if(getFirstResponse() != null) {
                baseCookies = getFirstResponse().cookies();
            }
            long d2 = System.currentTimeMillis();
            System.out.println("1.----------------= "+(d2-d1));
        }
        return baseCookies;
    }

    private static String csrfToken;
    public static String getCsrfToken(Context context){
        if(csrfToken == null){
            csrfToken = getBaseCookies().get("csrf_token");
            long d1 = System.currentTimeMillis();
            FileManager.getInstance().writeFile(context, StaticValues.CSRF_TOKEN, csrfToken);
            long d2 = System.currentTimeMillis();
            System.out.println("2.----------------= "+(d2-d1));

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
                    .header("x-csrf-token", csrfToken)
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

    public static Map<String, Object> parsingLoginResponse(Context context, String email, String password){
        Map<String, Object> result = new HashMap<>();
        Connection.Response loginResponse = getLoginResponse(email, password);
        if (loginResponse == null) return null;
        try {
            Document docLogin = loginResponse.parse();
            String contentBody = docLogin.body().html();
            String[] dataLogin = contentBody.split(":");
            if(dataLogin.length < 3) return null;
            UserCache userCache = new UserCache(dataLogin);
            Map<String, String> firstHomeCookies = setForFirstHomeCookies(baseCookies, userCache);
            Connection.Response homeResponse = getFirstHomeResponse(firstHomeCookies);
            if(homeResponse == null) return null;
            updateCsrfToken(context, homeResponse);
            String loginAuth = homeResponse.cookie("login_auth");
            userCache.setLoginAuth(loginAuth);
            ProfileView profileView = new ProfileView(homeResponse.parse());
            String userString = new Gson().toJson(userCache);
            FileManager.getInstance().writeFile(context, StaticValues.USER_CACHE, userString);
            result.put(StaticValues.USER_CACHE, userCache);
            result.put(StaticValues.PROFILE_VIEW, profileView);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void updateCsrfToken(Context context, Connection.Response response) {
        String csrfToken = response.cookie(StaticValues.CSRF_TOKEN);
        if(csrfToken != null){
            FileManager.getInstance().delete(context, StaticValues.CSRF_TOKEN);
            FileManager.getInstance().writeFile(context, StaticValues.CSRF_TOKEN, csrfToken);
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

    private static Connection.Response getRefreshHomeResponse(Map<String, String> cookies) throws IOException {
        Connection.Response response = Jsoup.connect(StaticValues.URL_HOME)
                .userAgent(StaticValues.USER_AGENT)
                .referrer("https://freebitco.in/?op=signup_page")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .header("Upgrade-Insecure-Requests", "1")
                .header("Host", "freebitco.in")
                .header("Connection", "Keep-Alive")
                .timeout(StaticValues.TIMEOUT)
                .method(Connection.Method.GET)
                .cookies(cookies)
                .execute();
        return response;
    }

    public static ProfileView parsingHomeResponse(Context context){
        String userString = FileManager.getInstance().readFile(context, StaticValues.USER_CACHE);
        UserCache userCache = new Gson().fromJson(userString, UserCache.class);
        Map<String, String> cookies = new HashMap<>();
        cookies.put("login_auth", userCache.getLoginAuth());
        cookies.put("btc_address",userCache.getBtcAddress());
        cookies.put("password",userCache.getPassword());
        cookies.put("have_account", "1");
        try {
            Connection.Response homeResponse = getRefreshHomeResponse(cookies);
            if(homeResponse == null) return null;
            updateCsrfToken(context, homeResponse);
            Document doc = homeResponse.parse();
            ProfileView profileView = new ProfileView(doc);
            // get fingerprint
            Element head = doc.head();
            head.appendElement("script")
                    .attr("type","text/javascript")
                    .appendChild(new TextNode("document.getElementById(\"unconfirmed_deposits_table_rows\").innerHTML = \"kampret\""));
            System.out.println(doc.html());
            return profileView;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void getValueJS() {
        BrowserEngine browser = BrowserFactory.getWebKit();
        Page page = browser.navigate(StaticValues.URL_BASE);
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
