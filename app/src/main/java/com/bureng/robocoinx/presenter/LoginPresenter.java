package com.bureng.robocoinx.presenter;

import android.content.Context;

import com.bureng.robocoinx.contract.LoginContract;
import com.bureng.robocoinx.model.request.SignUpRequest;
import com.bureng.robocoinx.model.view.ProfileView;
import com.bureng.robocoinx.utils.RoboHandler;

public class LoginPresenter implements LoginContract.Presenter {

    LoginContract.View view;
    public LoginPresenter(LoginContract.View _view){
        view = _view;
    }

    @Override
    public void login(Context ctx, String email, String password) {
        Object obj = RoboHandler.parsingLoginResponse(ctx, email, password);
        if(obj instanceof ProfileView){
            ProfileView profileView = (ProfileView) obj;
            view.goHome(profileView);
        }else if(obj instanceof String){
            String message = (String)obj;
            view.showMessage(message);
        }
    }

    @Override
    public void signUp(Context ctx, SignUpRequest request) {
        Object obj = RoboHandler.parsingSignUpResponse(ctx, request);
        if(obj instanceof ProfileView){
            ProfileView profileView = (ProfileView) obj;
            view.goHome(profileView);
        }else if(obj instanceof String){
            String message = (String)obj;
            view.showMessage(message);
            view.setCaptchaNet(RoboHandler.getCaptchaNet(request.fingerprint));
        }
    }

    @Override
    public void getCaptchaNet(String fingerprint) {
        view.setCaptchaNet(RoboHandler.getCaptchaNet(fingerprint));
    }
}
