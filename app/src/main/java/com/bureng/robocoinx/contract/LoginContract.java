package com.bureng.robocoinx.contract;

import android.content.Context;

import com.bureng.robocoinx.model.request.SignUpRequest;
import com.bureng.robocoinx.model.view.ProfileView;

public interface LoginContract {
    interface View{
        void showMessage(String message);
        void validateInput();
        void goHome(ProfileView profileView);
        void setCaptchaNet(String captchaNet);
    }
    interface Presenter{
        void login(Context ctx, String email, String password);
        void signUp(Context ctx, SignUpRequest request);
        void getCaptchaNet(String fingerprint);
    }
}
