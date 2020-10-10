package com.bureng.robocoinx.contract

interface ForgotPasswordContract {
    interface View{
        fun showSuccessMessage(message: String)
        fun showMessage(message: String)
        fun setCaptchaNet(captchaNet: String)
    }
    interface Presenter{
        fun reset(email: String, captchaNet: String, captchaResp: String, fingerprint: String)
        fun getCaptchaNet(fingerprint: String)
    }
}