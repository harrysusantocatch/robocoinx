package com.bureng.robocoinx.contract

import android.app.Activity
import android.content.Context
import com.bureng.robocoinx.model.view.ProfileView

interface LoginContract {
    interface View {
        fun showMessage(message: String, type: Int)
        fun flagHome()
        fun goHome(profileView: ProfileView)
        fun showProgressBar(rawLoading: Int?)
        fun hideProgressBar()
    }

    interface Presenter {
        fun login(act: Activity, ctx: Context, email: String, password: String)
        fun getHomeResponse(ctx: Context)
    }
}