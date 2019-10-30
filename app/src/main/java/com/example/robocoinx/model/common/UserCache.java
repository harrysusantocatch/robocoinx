package com.example.robocoinx.model.common;

public class UserCache {
    public String btcAddress;
    public String password;
    public String loginAuth;
    public String userID;

    public UserCache(String[] datas){
        btcAddress = datas[1];
        password = datas[2];
        userID = datas[3];
    }
}
