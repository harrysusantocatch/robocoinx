package com.example.robocoinx.contract;

import com.example.robocoinx.model.request.SignupRequest;
import com.example.robocoinx.model.view.ProfileView;

public interface SplashContract {
    interface View{
        void goHome(ProfileView profileView);
        void loadFingerprint(SignupRequest signupRequest);
        void showMessage(String message);
    }

    interface Presenter{
        boolean authorize();
        Object getResponse();
    }

}
