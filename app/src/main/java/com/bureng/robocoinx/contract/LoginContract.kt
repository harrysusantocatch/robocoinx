package com.bureng.robocoinx.contract

import android.content.Context
import com.bureng.robocoinx.model.view.ProfileView

interface LoginContract {
    interface View {
        fun showMessage(message: String)
        fun goHome(profileView: ProfileView)
        fun showProgressBar()
        fun hideProgressBar()
    }

    interface Presenter {
        fun login(ctx: Context, email: String, password: String)
    }
}