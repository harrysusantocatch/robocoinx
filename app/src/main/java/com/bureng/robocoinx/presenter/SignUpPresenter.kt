package com.bureng.robocoinx.presenter

import android.content.Context
import com.bureng.robocoinx.contract.SignUpContract
import com.bureng.robocoinx.model.request.SignUpRequest
import com.bureng.robocoinx.model.view.ProfileView
import com.bureng.robocoinx.utils.RoboHandler

class SignUpPresenter(private val ctx: Context, private val view: SignUpContract.View): SignUpContract.Presenter {
    override fun signUp(ctx: Context, request: SignUpRequest) {
        val resp = RoboHandler.parsingSignUpResponse(ctx, request)
        if (resp is ProfileView){
            view.goHome(resp)
        }else{
            view.showMessage(resp as String)
        }
    }

    override fun getCaptchaNet(fingerprint: String) {
        view.setCaptchaNet(RoboHandler.getCaptchaNet(fingerprint))
    }
}