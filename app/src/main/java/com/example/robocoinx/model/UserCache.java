package com.example.robocoinx.model;

public class UserCache {
    public String btcAddress;
    public String password;
    public String loginAuth;
    public String userID;

    public UserCache(String[] datas){
        setBtcAddress(datas[1]);
        setPassword(datas[2]);
        setUserID(datas[3]);
    }

    public String getBtcAddress() {
        return btcAddress;
    }

    public void setBtcAddress(String btcAddress) {
        this.btcAddress = btcAddress;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLoginAuth() {
        return loginAuth;
    }

    public void setLoginAuth(String loginAuth) {
        this.loginAuth = loginAuth;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
