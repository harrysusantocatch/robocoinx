package com.bureng.robocoinx.contract

import android.content.Context
import com.bureng.robocoinx.model.request.SignUpRequest
import com.bureng.robocoinx.model.view.ProfileView

interface SignUpContract {
    interface View{
        fun showMessage(message: String)
        fun goHome(profileView: ProfileView)
        fun setCaptchaNet(captchaNet: String)
    }
    interface Presenter{
        fun signUp(ctx: Context, request: SignUpRequest)
        fun getCaptchaNet(fingerprint: String)
    }
}