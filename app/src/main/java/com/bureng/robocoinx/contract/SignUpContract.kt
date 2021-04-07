package com.bureng.robocoinx.contract

import android.app.Activity
import android.content.Context
import com.bureng.robocoinx.model.request.SignUpRequest

interface SignUpContract {
    interface View{
        fun showSuccessMessage(message: String)
        fun showMessage(message: String)
        fun setCaptchaNet(captchaNet: String)
        fun showProgressBar()
        fun hideProgressBar()
    }
    interface Presenter{
        fun signUp(act: Activity, ctx: Context, request: SignUpRequest)
        fun getCaptchaNet(fingerprint: String)
    }
}