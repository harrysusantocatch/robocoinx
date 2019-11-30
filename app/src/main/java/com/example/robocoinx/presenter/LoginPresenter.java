package com.example.robocoinx.presenter;

import android.content.Context;

import com.example.robocoinx.contract.LoginContract;
import com.example.robocoinx.model.request.SignupRequest;
import com.example.robocoinx.model.view.ProfileView;
import com.example.robocoinx.utils.RoboHandler;

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
    public void signUp(Context ctx, SignupRequest request) {
        Object obj = RoboHandler.parsingSignUpResponse(ctx, request);
        if(obj instanceof ProfileView){
            ProfileView profileView = (ProfileView) obj;
            view.goHome(profileView);
        }else if(obj instanceof String){
            String message = (String)obj;
            view.showMessage(message);
        }
    }
}
