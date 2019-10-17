package com.example.robocoinx.model;

public class RollErrorResponse {
    public String message;
    public int countDown;

    public  RollErrorResponse(String message, int countDown){
        this.message = message;
        this.countDown = countDown;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCountDown() {
        return countDown;
    }

    public void setCountDown(int countDown) {
        this.countDown = countDown;
    }
}
