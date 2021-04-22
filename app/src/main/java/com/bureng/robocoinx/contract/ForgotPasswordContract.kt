package com.bureng.robocoinx.contract

interface ForgotPasswordContract {
    interface View{
        fun showProgressBar(rawLoading: Int?)
        fun hideProgressBar()
        fun showMessage(message: String, type: Int)
        fun setCaptchaNet(captchaNet: String)
    }
    interface Presenter{
        fun reset(email: String, captchaNet: String, captchaResp: String, fingerprint: String)
        fun getCaptchaNet(fingerprint: String)
    }
}