package com.example.robocoinx.model;

public class RollErrorResponse {
    public String message;
    public int countDown;

    public  RollErrorResponse(String message, int countDown){
        this.message = message;
        this.countDown = countDown;
    }
}
