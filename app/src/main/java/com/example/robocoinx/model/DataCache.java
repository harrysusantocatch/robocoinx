package com.example.robocoinx.model;

public class DataCache {
    public String csrfToken;
    public String cfduid;
    public int haveAccount;
    public String loginAuth;
    public String btcAddress;
    public String password;
    public String gat;

    public String getCsrfToken() {
        return csrfToken;
    }

    public void setCsrfToken(String csrfToken) {
        this.csrfToken = csrfToken;
    }

    public String getCfduid() {
        return cfduid;
    }

    public void setCfduid(String cfduid) {
        this.cfduid = cfduid;
    }

    public int getHaveAccount() {
        return haveAccount;
    }

    public void setHaveAccount(int haveAccount) {
        this.haveAccount = haveAccount;
    }

    public String getLoginAuth() {
        return loginAuth;
    }

    public void setLoginAuth(String loginAuth) {
        this.loginAuth = loginAuth;
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

    public String getGat() {
        return gat;
    }

    public void setGat(String gat) {
        this.gat = gat;
    }
}
