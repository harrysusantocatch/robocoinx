package com.bureng.robocoinx.contract;

import com.bureng.robocoinx.model.request.SignUpRequest;
import com.bureng.robocoinx.model.view.ProfileView;

public interface SplashContract {
    interface View{
        void goHome(ProfileView profileView);
        void loadFingerprint(SignUpRequest signupRequest);
        void showMessage(String message);
    }

    interface Presenter{
        boolean authorize();
        Object getResponse();
    }

}
