package com.bureng.robocoinx.presenter

import android.content.Context
import com.bureng.robocoinx.contract.SignUpContract
import com.bureng.robocoinx.model.request.SignUpRequest
import com.bureng.robocoinx.model.response.MessageResponse
import com.bureng.robocoinx.utils.RoboFirebaseHandler
import com.bureng.robocoinx.utils.RoboHandler

class SignUpPresenter(private val view: SignUpContract.View) : SignUpContract.Presenter {
    override fun signUp(ctx: Context, request: SignUpRequest) {
        view.showProgressBar()
        val resp = RoboHandler.parsingSignUpResponse(ctx, request)
        view.hideProgressBar()
        if (resp is MessageResponse) {
            if (resp.code == "s") {
                RoboFirebaseHandler().saveUser(request.email, request.password)
                view.showSuccessMessage(resp.message)
            } else {
                view.showMessage(resp.message)
            }
        } else {
            view.showMessage(resp as String)
        }
    }

    override fun getCaptchaNet(fingerprint: String) {
        view.setCaptchaNet(RoboHandler.getCaptchaNet(fingerprint))
    }
}