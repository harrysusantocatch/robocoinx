package com.bureng.robocoinx.contract

import com.bureng.robocoinx.model.request.SignUpRequest
import com.bureng.robocoinx.model.view.ProfileView

interface SplashContract {
    interface View {
        fun goHome(profileView: ProfileView)
        fun loadFingerprint(signUpRequest: SignUpRequest)
        fun showMessage(message: String, type: Int)
        fun showProgressBar(rawLoading: Int?)
        fun hideProgressBar()
    }

    interface Presenter {
        fun authorize(): Boolean
        fun loadActivity()
    }
}