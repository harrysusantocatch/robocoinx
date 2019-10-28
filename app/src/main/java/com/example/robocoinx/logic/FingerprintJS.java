package com.example.robocoinx.logic;

import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.example.robocoinx.activity.LoginActivity;
import com.example.robocoinx.model.StaticValues;
import com.example.robocoinx.model.db.Fingerprint;
import com.example.robocoinx.model.request.SignupRequest;

public class FingerprintJS {

    Context context;
    SignupRequest signupRequest;

    public FingerprintJS(Context cx, SignupRequest sign){
        context = cx;
        signupRequest = sign;
    }

    @JavascriptInterface
    public void getFingerprint(String data) {
        if(!data.contains("error")){
            String[] split = data.split(":");
            Fingerprint fingerprint = new Fingerprint(split[0], split[1]);
            FingerprintHandler.getInstance(context).insert(fingerprint);
            goToLogin(context, signupRequest);
        }
    }

    private void goToLogin(Context context, SignupRequest signupRequest) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(StaticValues.SIGNUP_REQ, signupRequest);
        context.startActivity(intent);
    }
}
