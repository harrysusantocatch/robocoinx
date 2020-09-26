package com.bureng.robocoinx.presenter

import com.bureng.robocoinx.contract.ForgotPasswordContract
import com.bureng.robocoinx.model.response.MessageResponse
import com.bureng.robocoinx.utils.RoboHandler

class ForgotPasswordPresenter(val view: ForgotPasswordContract.View): ForgotPasswordContract.Presenter {
    override fun reset(email: String, captchaNet: String, captchaResp: String, fingerprint: String) {
        val resp = RoboHandler.parsingResetPassword(email, captchaNet, captchaResp, fingerprint)
        if (resp is MessageResponse){
            if(resp.code == "e"){
                view.showMessage(resp.message)
            }else{
                view.goSplash(resp.message)
            }
        }else{
            view.showMessage(resp as String)
        }
    }

    override fun getCaptchaNet(fingerprint: String) {
        view.setCaptchaNet(RoboHandler.getCaptchaNet(fingerprint))
    }
}