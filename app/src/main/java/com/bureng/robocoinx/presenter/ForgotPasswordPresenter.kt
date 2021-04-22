package com.bureng.robocoinx.presenter

import com.bureng.robocoinx.R
import com.bureng.robocoinx.contract.ForgotPasswordContract
import com.bureng.robocoinx.model.response.MessageResponse
import com.bureng.robocoinx.utils.RoboHandler

class ForgotPasswordPresenter(val view: ForgotPasswordContract.View): ForgotPasswordContract.Presenter {
    override fun reset(email: String, captchaNet: String, captchaResp: String, fingerprint: String) {
        view.showProgressBar(R.raw.loading_crocket)
        val resp = RoboHandler.parsingResetPassword(email, captchaNet, captchaResp, fingerprint)
        if (resp is MessageResponse){
            if(resp.code == "e"){
                view.showMessage(resp.message, 2)
            }else{
                view.showMessage(resp.message, 1)
            }
        }else{
            view.showMessage(resp as String, 2)
        }
    }

    override fun getCaptchaNet(fingerprint: String) {
        view.setCaptchaNet(RoboHandler.getCaptchaNet(fingerprint))
    }
}