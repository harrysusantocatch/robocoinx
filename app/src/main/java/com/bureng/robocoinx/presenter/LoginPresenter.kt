package com.bureng.robocoinx.presenter

import android.content.Context
import com.bureng.robocoinx.contract.LoginContract
import com.bureng.robocoinx.model.view.ProfileView
import com.bureng.robocoinx.utils.RoboHandler

class LoginPresenter(var view: LoginContract.View) : LoginContract.Presenter {
    override fun login(ctx: Context, email: String, password: String) {
        if (email == "") {
            view.showMessage("email cannot be empty")
        } else if (password == "") {
            view.showMessage("password cannot be empty")
        }else{
            view.showProgressBar()
            val obj = RoboHandler.parsingLoginResponse(ctx, email, password)
            view.hideProgressBar()
            if (obj is ProfileView) {
                view.goHome(obj)
            } else if (obj is String) {
                view.showMessage(obj)
            }
        }
    }

}