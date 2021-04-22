package com.bureng.robocoinx.contract

import android.content.Context

interface ChangePasswordContract {
    interface View {
        fun showMessage(message: String, type: Int)
        fun showProgressBar(rawLoading: Int?)
        fun hideProgressBar()
    }

    interface Presenter {
        fun changePassword(context: Context, oldPassword: String, newPassword: String, repeatPassword: String)
    }
}