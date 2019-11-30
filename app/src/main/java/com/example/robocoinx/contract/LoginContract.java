package com.example.robocoinx.contract;

import android.content.Context;

import com.example.robocoinx.model.request.SignupRequest;
import com.example.robocoinx.model.view.ProfileView;

public interface LoginContract {
    interface View{
        void showMessage(String message);
        void validateInput();
        void goHome(ProfileView profileView);
    }
    interface Presenter{
        void login(Context ctx, String email, String password);
        void signUp(Context ctx, SignupRequest request);
    }
}
