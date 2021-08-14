package com.bureng.robocoinx.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bureng.robocoinx.logic.RedeemRP;
import com.bureng.robocoinx.model.common.UserCache;
import com.bureng.robocoinx.model.db.ClaimHistory;
import com.bureng.robocoinx.model.request.RollRequest;
import com.bureng.robocoinx.model.request.SignUpRequest;
import com.bureng.robocoinx.model.response.InitWithdrawResponse;
import com.bureng.robocoinx.model.response.MessageResponse;
import com.bureng.robocoinx.model.response.RollErrorResponse;
import com.bureng.robocoinx.model.response.RollSuccessResponse;
import com.bureng.robocoinx.model.view.NoCaptchaSpec;
import com.bureng.robocoinx.model.view.ProfileView;
import com.bureng.robocoinx.repository.ClaimHistoryHandler;
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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
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
            return new SignUpRequest(document, script);
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
            if (scriptData.contains(CryptEx.toBaseDecode(StaticValues.REGEX_SCRIPT_1)) ||
                    scriptData.contains(CryptEx.toBaseDecode(StaticValues.REGEX_SCRIPT_2)) ||
                    scriptData.contains(CryptEx.toBaseDecode(StaticValues.REGEX_SCRIPT_3)) ||
                    scriptData.contains(CryptEx.toBaseDecode(StaticValues.REGEX_SCRIPT_4)) ||
                    scriptData.contains(CryptEx.toBaseDecode(StaticValues.REGEX_SCRIPT_5))) {
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
            if(dataLogin.length < 3) return dataLogin[1];
            UserCache userCache = new UserCache(dataLogin);
            Map<String, String> firstHomeCookies = setForFirstHomeCookies(RoboBrowser.baseCookies, userCache);
            RoboBrowser.homeCookies = firstHomeCookies;
//            Connection.Response homeResponse = RoboBrowser.getFirstHomeResponse();
//            if(homeResponse == null) return StaticValues.ERROR_GENERAL;
//            updateCsrfToken(context, homeResponse);
//            userCache.loginAuth = homeResponse.cookie("login_auth");
//            ProfileView profileView = new ProfileView(homeResponse.parse(), context);
            new CacheContext<>(UserCache.class, context).save(userCache, StaticValues.USER_CACHE);
            Map<String, String> cookies = new HashMap<>();
            cookies.put("login_auth", userCache.loginAuth);
            cookies.put("btc_address",userCache.btcAddress);
            cookies.put("password",userCache.password);
            cookies.put("have_account", "1");
            FileManager.getInstance().writeFile(context, StaticValues.AUTH_COOKIES, new Gson().toJson(cookies));

//            if (setInterestAndLottery(profileView, cookies, homeResponse)) return StaticValues.ERROR_GENERAL;

            return StaticValues.PROFILE_VIEW;
        } catch (IOException e) {
            FileManager.getInstance().appendLog(e);
            return StaticValues.ERROR_GENERAL;
        }
    }

    public static String getCaptchaNet(String fingerprint){
        Connection.Response captchaNetRes = RoboBrowser.generateCaptchaNet(fingerprint);

        if(captchaNetRes == null) return null;
        else{
            RoboBrowser.botDetect(captchaNetRes.body());
            return captchaNetRes.body();
        }
    }
    public static Object parsingSignUpResponse(Context context, SignUpRequest request){
        Connection.Response signUpResponse = RoboBrowser.getSignUpResponse(request);
        if(signUpResponse == null) return StaticValues.ERROR_GENERAL;
        try {
            Document doc = signUpResponse.parse();
            String contentBody = doc.body().html();
            String[] result = contentBody.split(":");
//            String[] result = {"s", "registered"};
            if(result.length == 0) return StaticValues.ERROR_GENERAL;
            if(result[0].equalsIgnoreCase("s")){
                return new MessageResponse(result[0], "Registration Successful, Please Sign into access your account");
            }else if(result[0].equalsIgnoreCase("e")){
                String msg = result[1];
                String ms1 = "email";
                String ms2 = "exist";
                if(msg.contains(ms1) && msg.contains(ms2)){
                    msg = "Please use another email address";
                }
                return msg;
            }else return StaticValues.ERROR_GENERAL;
        }catch (Exception e){
            e.printStackTrace();
            FileManager.getInstance().appendLog(e);
            return StaticValues.ERROR_GENERAL;
        }
    }

    public static Object parsingResetPassword(String email, String captchaNet, String captchaResp, String fingerprint) {
        Connection.Response resetResponse = RoboBrowser.getResetResponse(email, captchaNet, captchaResp, fingerprint);
        if(resetResponse == null) return StaticValues.ERROR_GENERAL;
        try {
            Document doc = resetResponse.parse();
            String contentBody = doc.body().html();
            String[] result = contentBody.split(":");
            if(result.length == 0) return StaticValues.ERROR_GENERAL;
            if(result[0].equalsIgnoreCase("s")){
                return new MessageResponse(result[0],result[1]);
            }else if(result[0].equalsIgnoreCase("e")){
                return new MessageResponse(result[0],result[1]);
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
            ProfileView pp = new ProfileView(doc, context);
            if(pp.haveCaptcha){
//                resolveCaptchaWithPlay(context, pp, cookies);
//                context.stopService(new Intent(context.getApplicationContext(), BackgroundService.class));
                return new RollErrorResponse("resolve captcha", pp.nextRollTime);
            }
            if(pp.nextRollTime > 0) return new RollErrorResponse("",pp.nextRollTime);

            cookies.put("csrf_token", RoboBrowser.csrfToken);
            String cfduid = homeResponse.cookie("__cfduid");
            if(cfduid != null){
                cookies.put("__cfduid", cfduid);
            }
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
            String payoutStr = doc.getElementById("pending_payout_table").text(); // slow address amount, address amount dst
            String[] pendingPayoutStr = payoutStr.split(" ");
            ProfileView profileView = new ProfileView(doc, context);
            profileView.noCaptchaSpec = getCaptchaSpec(profileView);
//            if (setInterestAndLottery(profileView, cookies, homeResponse)) return StaticValues.ERROR_GENERAL;
            return profileView;
        } catch (IOException | JSONException e) {
            FileManager.getInstance().appendLog(e);
            return StaticValues.ERROR_GENERAL;
        }
    }

    private static void resolveCaptchaWithPlay(Context context, ProfileView profileView, Map<String, String> cookies) {
        try {
            DecimalFormat precision = new DecimalFormat("0.00000000");
            NoCaptchaSpec noCaptchaSpec = getCaptchaSpec(profileView);
            // this is spec
            double wagerSpec = Double.parseDouble(noCaptchaSpec.wager);
            int lotterySpecInt = Integer.parseInt(noCaptchaSpec.lottery);
            double lotterySpec = (double) lotterySpecInt / 100000000;

            double balance = Double.parseDouble(profileView.balance);
            double currentBalance = 0;
            currentBalance = currentBalance + balance;
            double minimumBalance = 0.00005000;
            double minimumLottery = 0.00000050;
            double minimumWager = balance * ((double) (8 / 100));
            if (balance > minimumBalance) {
                // resolve by lottery
                if (lotterySpec <= minimumLottery) {
                    boolean success = purchaseLottery(cookies, noCaptchaSpec.lottery);
                    if (success) {
                        double amount = Double.parseDouble(noCaptchaSpec.lottery) / 100000000;
                        String amountStr = precision.format(amount);
                        currentBalance = currentBalance - amount;
                        ClaimHistoryHandler.getInstance(context).insert(
                                new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date()),
                                "Resolve Network", ClaimHistory.TransactionType.lost.name(), amountStr,
                                precision.format(currentBalance));
                    } else {
                        FileManager.getInstance().appendLog("Resolve by lottery failed..");
                    }
                } else {
                    if (wagerSpec <= minimumWager) {
                        String clientSeed = getClientSeed();

                        int lose = 0;
                        boolean isLo = true;
                        boolean finish = false;
                        double additional = (double) 1 / 100000000;
                        double stake = (double) 2 / 100000000;

                        // this is comparable
                        double totalWager = 0;
                        double totalWinStake = 0;

                        while (totalWinStake < lotterySpec || !finish) {
                            String stakeStr = precision.format(stake).replace(",", ".");
                            if (lose == 10) {
                                finish = true;
                            } else {
                                if (isLo) {
                                    boolean win = isWinStakeLo(cookies, clientSeed, stakeStr);
                                    if (win) {
                                        currentBalance = currentBalance + stake;
                                        stake = (double) 2 / 100000000;
                                        isLo = false;
                                        lose = 0;
                                        ClaimHistoryHandler.getInstance(context).insert(
                                                new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date()),
                                                "Distribution Network", ClaimHistory.TransactionType.receive.name(), stakeStr,
                                                precision.format(currentBalance));
                                        if (totalWager >= wagerSpec) finish = true;
                                    } else {
                                        currentBalance = currentBalance - stake;
                                        stake = (stake * 2) + additional;
                                        lose++;
                                        ClaimHistoryHandler.getInstance(context).insert(
                                                new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date()),
                                                "Lost Network", ClaimHistory.TransactionType.lost.name(), stakeStr,
                                                precision.format(currentBalance));
                                    }
                                } else {
                                    boolean win = isWinStakeHi(cookies, clientSeed, stakeStr);
                                    if (win) {
                                        currentBalance = currentBalance + stake;
                                        stake = (double) 2 / 100000000;
                                        isLo = true;
                                        lose = 0;
                                        ClaimHistoryHandler.getInstance(context).insert(
                                                new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date()),
                                                "Distribution Network", ClaimHistory.TransactionType.receive.name(), stakeStr,
                                                precision.format(currentBalance));
                                        if (totalWager >= wagerSpec) finish = true;
                                    } else {
                                        currentBalance = currentBalance - stake;
                                        stake = (stake * 2) + additional;
                                        lose++;
                                        ClaimHistoryHandler.getInstance(context).insert(
                                                new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date()),
                                                "Lost Network", ClaimHistory.TransactionType.lost.name(), stakeStr,
                                                precision.format(currentBalance));
                                    }
                                }
                                // update spec
                                totalWinStake = currentBalance - balance;
                                if ((lotterySpec - totalWinStake) >= ((double) 20 / 100000000)) {
                                    if (totalWager >= wagerSpec / 4 || totalWinStake >= lotterySpec / 2) {
                                        Thread.sleep(60000);
                                        NoCaptchaSpec newCaptchaSpec = getCaptchaSpec(profileView);
                                        wagerSpec = Double.parseDouble(newCaptchaSpec.wager);
                                        int newLotterySpecInt = Integer.parseInt(newCaptchaSpec.lottery);
                                        lotterySpec = (double) newLotterySpecInt / 100000000;
                                        totalWager = totalWager - (wagerSpec / 4);
                                    }
                                }
                                totalWager += stake;
                                if (totalWager >= wagerSpec) finish = true;
                                if (totalWinStake >= lotterySpec) finish = true;
                            }
                        }
                        if (totalWager >= wagerSpec) return;
                        Thread.sleep(60000);
                        NoCaptchaSpec noCaptchaSpecNext = getCaptchaSpec(profileView);
                        boolean success = purchaseLottery(cookies, noCaptchaSpecNext.lottery);
                        if (success) {
                            double amount = Double.parseDouble(noCaptchaSpecNext.lottery) / 100000000;
                            String amountStr = precision.format(amount);
                            currentBalance = currentBalance - amount;
                            ClaimHistoryHandler.getInstance(context).insert(
                                    new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date()),
                                    "Resolve Network", ClaimHistory.TransactionType.lost.name(), amountStr,
                                    precision.format(currentBalance));
                        } else {
                            FileManager.getInstance().appendLog("Resolve by lottery failed... after wager");
                        }
                    } else {
                        FileManager.getInstance().appendLog("Spec wager too high...");
                    }
                }
            }
        } catch (JSONException | InterruptedException e) {
            FileManager.getInstance().appendLog(e);
        }
    }

    private static NoCaptchaSpec getCaptchaSpec(ProfileView profileView) throws JSONException {
        Connection.Response response = RoboBrowser.getUserStatistic(profileView.socketId, profileView.socketPass);
        String result = response.body();
        JSONObject root = new JSONObject(result);
        JSONObject noCaptchaSpecJsn = root.getJSONObject("no_captcha_gbr");
        return new NoCaptchaSpec(noCaptchaSpecJsn.getString("lottery_to_unblock").replace("-",""),
                noCaptchaSpecJsn.getString("wager_to_unblock").replace("-",""),
                noCaptchaSpecJsn.getString("jackpot_to_unblock").replace("-",""),
                noCaptchaSpecJsn.getString("deposit_to_unblock").replace("-",""));
    }

    private static boolean purchaseLottery(Map<String, String> cookies, String ticket) {
        Connection.Response res1 = RoboBrowser.setLottery(cookies,ticket);
        String result = res1.body();
        String[] resultArr  = result.split(":");
        return resultArr[0].equalsIgnoreCase("s");
    }

    private static boolean isWinStakeHi(Map<String, String> cookies, String clientSeed, String stakeStr) {
        double random = Math.random();
        Connection.Response resp = RoboBrowser.getHiLo(cookies,"hi", clientSeed, stakeStr, random);
        String result2 = resp.body();
        String[] result2Arr = result2.split(":");
        if(result2Arr[1].equalsIgnoreCase("w")){
            System.out.println("Win="+resp.url());
            return true;
        }else {
            return false;
        }
    }

    private static boolean isWinStakeLo(Map<String, String> cookies, String clientSeed, String stakeStr) {
        double random = Math.random();
        Connection.Response resp = RoboBrowser.getHiLo(cookies,"lo", clientSeed, stakeStr, random);
        String result2 = resp.body();
        String[] result2Arr = result2.split(":");
        if(result2Arr[1].equalsIgnoreCase("w")){
            System.out.println("Win="+resp.url());
            return true;
        }else {
            return false;
        }
    }

    private static String getClientSeed() {
        String charSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder randomString = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            int randomPoz = (int) Math.floor(Math.random() * charSet.length());
            randomString.append(charSet.substring(randomPoz, randomPoz + 1));
        }
        return randomString.toString();
    }

    private static int resolveCaptchaWithWinStake(Context context, ProfileView profileView, Map<String, String> cookies) {
        try {
            NoCaptchaSpec noCaptchaSpec = getCaptchaSpec(profileView);
            double balance = Double.parseDouble(profileView.balance);
            double currentBalance = balance;
            DecimalFormat precision = new DecimalFormat("0.00000000");
            String clientSeed = getClientSeed();
            Double[] stakeArr = {0.00000001, 0.00000003, 0.00000005, 0.00000010, 0.00000020,
                    0.00000040, 0.00000080, 0.00000160, 0.00000320, 0.00000640};
            double wagerSpec = Double.parseDouble(noCaptchaSpec.wager);
            int loterrySpec = Integer.parseInt(noCaptchaSpec.lottery);
            int lose = 0;
            int totalLose = 0;
            int maxLose = 0;
            double totalWager = 0;
            double totalWinStake = 0;
            boolean isLo = true;
            boolean finish = false;
            double targetWin = (double) 10 / 100000000;
            double stake = stakeArr[0];
            while (!finish) {
                String stakeStr = precision.format(stake).replace(",", ".");
                if (lose == 10) {
                    // reset
                    lose = 0;
                    stake = stakeArr[0];
                } else {
                    if (isLo) {
                        boolean win = isWinStakeLo(cookies, clientSeed, stakeStr);
                        if (win) {
                            currentBalance = currentBalance + stake;
                            totalWinStake += stake;
                            stake = stakeArr[0];
                            isLo = false;
                            lose = 0;
                            totalLose = 0;
//                            ClaimHistoryHandler.getInstance(context).insert(
//                                    new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date()),
//                                    "Distribution Network", ClaimHistory.TransactionType.receive.name(), stakeStr,
//                                    precision.format(currentBalance));
                        } else {
                            totalWinStake -= stake;
                            currentBalance = currentBalance - stake;
                            lose++;
                            totalLose++;
                            if(totalLose > maxLose){
                                maxLose = totalLose;
                            }
                            stake = stakeArr[lose];
//                            ClaimHistoryHandler.getInstance(context).insert(
//                                    new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date()),
//                                    "Lost Network", ClaimHistory.TransactionType.lost.name(), stakeStr,
//                                    precision.format(currentBalance));
                        }
                    } else {
                        boolean win = isWinStakeHi(cookies, clientSeed, stakeStr);
                        if (win) {
                            currentBalance = currentBalance + stake;
                            totalWinStake += stake;
                            stake = stakeArr[0];
                            isLo = true;
                            lose = 0;
                            totalLose = 0;
//                            ClaimHistoryHandler.getInstance(context).insert(
//                                    new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date()),
//                                    "Distribution Network", ClaimHistory.TransactionType.receive.name(), stakeStr,
//                                    precision.format(currentBalance));
                        } else {
                            currentBalance = currentBalance - stake;
                            totalWinStake -= stake;
                            lose++;
                            totalLose++;
                            if(totalLose > maxLose){
                                maxLose = totalLose;
                            }
                            stake = stakeArr[lose];
//                            ClaimHistoryHandler.getInstance(context).insert(
//                                    new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date()),
//                                    "Lost Network", ClaimHistory.TransactionType.lost.name(), stakeStr,
//                                    precision.format(currentBalance));
                        }
                    }
                    totalWager += stake;
                    if (totalWager >= wagerSpec) finish = true;
                    if (totalWinStake >= targetWin) finish = true;
                    if(totalWinStake <= -0.00000020){
                        targetWin = 0.00000005;
                    }
                }
            }
            if(targetWin > 0){
                ClaimHistoryHandler.getInstance(context).insert(
                        new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date()),
                        "Distribution Network", ClaimHistory.TransactionType.receive.name(), precision.format(targetWin),
                        precision.format(currentBalance));
            }else {
                ClaimHistoryHandler.getInstance(context).insert(
                        new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date()),
                        "Lost Network ("+maxLose+")", ClaimHistory.TransactionType.lost.name(), precision.format(targetWin),
                        precision.format(currentBalance));
            }
            if(loterrySpec >= 25) {
                if (totalWager >= wagerSpec) return 360000;
                if (totalWinStake >= targetWin) return 360000;
            }
            NoCaptchaSpec noCaptchaSpecNext = getCaptchaSpec(profileView);
            boolean success = purchaseLottery(cookies, noCaptchaSpecNext.lottery);
            if(success){
                double amount = Double.parseDouble(noCaptchaSpecNext.lottery)/100000000;
                String amountStr = precision.format(amount);
                currentBalance = currentBalance - amount;
                ClaimHistoryHandler.getInstance(context).insert(
                        new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date()),
                        "Resolve Network", ClaimHistory.TransactionType.lost.name(), amountStr,
                        precision.format(currentBalance));
                return 30000;
            }else return 300000;
        } catch (JSONException e) {
            FileManager.getInstance().appendLog(e);
            return 300000;
        }
    }

    private static boolean stakeHiLo(Map<String, String> cookies, String hilo, String clientSeed, String stakeStr, double random) {
        Connection.Response resp = RoboBrowser.getHiLo(cookies,"hi", clientSeed, stakeStr, random);
        String result2 = resp.body();
        String[] result2Arr = result2.split(":");
        if(result2Arr[1].equalsIgnoreCase("w")){
            System.out.println("Win="+resp.url());
            return true;
        }else {
            System.out.println("lose="+resp.url());
            return false;
        }
    }

    private static int resolveCaptchaWithTargetWager(Context context, ProfileView profileView, Map<String, String> cookies) {
        try {
            NoCaptchaSpec noCaptchaSpec = getCaptchaSpec(profileView);
            double wagerSpec = Double.parseDouble(noCaptchaSpec.wager);
            double targetWager = wagerSpec/10;
            double balance = Double.parseDouble(profileView.balance);
            double currentBalance = balance;
            DecimalFormat precision = new DecimalFormat("0.00000000");
            Double[] stakeArr = {0.00000001, 0.00000001, 0.00000002, 0.00000004, 0.00000008,
                    0.00000016, 0.00000032, 0.00000033, 0.00000034, 0.00000035};
            int lose = 0;
            double totalWager = 0;
            double totalWinStake = 0;
            boolean isLo = true;
            boolean finish = false;
            double stake = (double) 1 / 100000000;
            while (!finish) {
                String stakeStr = precision.format(stake).replace(",", ".");
                if (lose == 10) {
                    isWinStakeLo(cookies, getClientSeed(), precision.format(0.00000036).replace(",", "."));
                    finish = true; // TODO RESOLVE LOSE
                } else {
                    if (isLo) {
                        boolean win = isWinStakeLo(cookies, getClientSeed(), stakeStr);
                        if (win) {
                            currentBalance = currentBalance + stake;
                            totalWinStake += stake;
                            stake = stakeArr[0];
                            isLo = false;
                            lose = 0;
                            ClaimHistoryHandler.getInstance(context).insert(
                                    new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date()),
                                    "Distribution Network", ClaimHistory.TransactionType.receive.name(), stakeStr,
                                    precision.format(currentBalance));
                        } else {
                            totalWinStake -= stake;
                            currentBalance = currentBalance - stake;
                            stake = stakeArr[lose];
                            lose++;
                            ClaimHistoryHandler.getInstance(context).insert(
                                    new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date()),
                                    "Lost Network", ClaimHistory.TransactionType.lost.name(), stakeStr,
                                    precision.format(currentBalance));
                        }
                    } else {
                        boolean win = isWinStakeHi(cookies, getClientSeed(), stakeStr);
                        if (win) {
                            currentBalance = currentBalance + stake;
                            totalWinStake += stake;
                            stake = stakeArr[0];
                            isLo = true;
                            lose = 0;
                            ClaimHistoryHandler.getInstance(context).insert(
                                    new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date()),
                                    "Distribution Network", ClaimHistory.TransactionType.receive.name(), stakeStr,
                                    precision.format(currentBalance));
                        } else {
                            currentBalance = currentBalance - stake;
                            totalWinStake -= stake;
                            stake = stakeArr[lose];
                            lose++;
                            ClaimHistoryHandler.getInstance(context).insert(
                                    new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date()),
                                    "Lost Network", ClaimHistory.TransactionType.lost.name(), stakeStr,
                                    precision.format(currentBalance));
                        }
                    }
                    totalWager += stake;
                    if (totalWager >= targetWager && totalWinStake >= (-0.00000001)) finish = true;
                }
            }
            if(targetWager <=  0)
                return 30000;
            return 360000;
        } catch (JSONException e) {
            FileManager.getInstance().appendLog(e);
            return 300000;
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    public static void rollWithRP(Context ctx, String html){
        ((Activity)ctx).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                WebView webView = new WebView(ctx);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    webView.getSettings().setSafeBrowsingEnabled(false);
                }
                webView.getSettings().setJavaScriptEnabled(true);
                webView.setWebViewClient(new WebViewClient(){
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        view.loadUrl("(function() { $('#play_without_captchas_button').click(); $('#free_play_form_button').click(); })();");
                    }
                });

                webView.evaluateJavascript("(function() { $('#play_without_captchas_button').click(); $('#free_play_form_button').click(); })();",
                        new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String value) {
                                System.out.println("masuk");
                            }
                        });
                webView.loadDataWithBaseURL("blarg://ignored", html, "text/html", "UTF-8", "");
            }
        });
    }

    public static Object parsingHomeWithdrawResponse(Context context) {
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
            cookies.put("__cfduid", homeResponse.cookie("__cfduid"));
            FileManager.getInstance().writeFile(context, StaticValues.AUTH_COOKIES, new Gson().toJson(cookies));
            if(homeResponse == null) return StaticValues.ERROR_GENERAL;
            updateCsrfToken(context, homeResponse);
            Document doc = homeResponse.parse();
            String balance = doc.getElementById("balance").text();
            String fee = doc.getElementsByClass("manual_withdraw_fee").get(0).text();
            InitWithdrawResponse result = new InitWithdrawResponse(balance, fee);
            return result;
        } catch (IOException e) {
            FileManager.getInstance().appendLog(e);
            return StaticValues.ERROR_GENERAL;
        }
    }

    public static Object parsingWithdrawResponse(Context context, String amount, String address) {
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
            Connection.Response signUpResponse = RoboBrowser.getWithdrawResponse(cookies, amount, address);
            if(signUpResponse == null) return StaticValues.ERROR_GENERAL;
            Document doc = signUpResponse.parse();
            String contentBody = doc.body().html();
            String[] result = contentBody.split(":");
            if(result.length > 0) return new MessageResponse(result[0], result[1]);
            else return StaticValues.ERROR_GENERAL;
        }catch (Exception e){
            e.printStackTrace();
            FileManager.getInstance().appendLog(e);
            return StaticValues.ERROR_GENERAL;
        }
    }

    public static Object parsingChangePasswordResponse(Context context, String oldPassword, String newPassword, String repeatPassword) {
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
            Connection.Response signUpResponse = RoboBrowser.getChangePasswordResponse(cookies, oldPassword, newPassword, repeatPassword);
            if(signUpResponse == null) return StaticValues.ERROR_GENERAL;
            Document doc = signUpResponse.parse();
            String contentBody = doc.body().html();
            String[] result = contentBody.split(":");
            if(result.length > 0) return new MessageResponse(result[0], result[1]);
            else return StaticValues.ERROR_GENERAL;
        }catch (Exception e){
            e.printStackTrace();
            FileManager.getInstance().appendLog(e);
            return StaticValues.ERROR_GENERAL;
        }
    }
}
