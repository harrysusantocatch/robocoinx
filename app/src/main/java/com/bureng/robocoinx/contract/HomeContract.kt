package com.bureng.robocoinx.contract

import android.content.Context
import com.bureng.robocoinx.model.view.ProfileView

interface HomeContract {
    interface View {
        fun goToSplash()
        fun goProfile(profileView: ProfileView)
        fun reload(profileView: ProfileView)
        fun showMessage(message: String, type: Int)
        fun showProgressBar(rawLoading: Int?)
        fun hideProgressBar()
    }
    interface  Presenter{
        fun logout(ctx: Context)
        fun callRoll(ctx: Context)
        fun loadProfile(ctx: Context)
        fun reload(ctx: Context)
    }

}