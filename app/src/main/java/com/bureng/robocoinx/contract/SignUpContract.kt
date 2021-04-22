package com.bureng.robocoinx.contract

import android.app.Activity
import android.content.Context
import com.bureng.robocoinx.model.request.SignUpRequest

interface SignUpContract {
    interface View{
        fun showMessage(message: String, type: Int)
        fun setCaptchaNet(captchaNet: String)
        fun showProgressBar(rawLoading: Int?)
        fun hideProgressBar()
    }
    interface Presenter{
        fun signUp(act: Activity, ctx: Context, request: SignUpRequest)
        fun getCaptchaNet(fingerprint: String)
    }
}