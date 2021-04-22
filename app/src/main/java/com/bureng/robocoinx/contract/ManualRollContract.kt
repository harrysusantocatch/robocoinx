package com.bureng.robocoinx.contract

import android.content.Context
import com.bureng.robocoinx.model.view.ProfileView

interface ManualRollContract {
    interface View {
        fun goHome(profileView: ProfileView)
        fun showMessage(message: String, type: Int)
        fun showProgressBar(rawLoading: Int?)
        fun hideProgressBar()
    }

    interface Presenter {
        fun loadHome(ctx: Context)
    }

}