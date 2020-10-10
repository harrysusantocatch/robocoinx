package com.bureng.robocoinx.contract

import android.content.Context

interface ChangePasswordContract {
    interface View {
        fun showMessage(message: String)
        fun showSuccessMessage(message: String)
        fun showProgressBar()
        fun hideProgressBar()
    }

    interface Presenter {
        fun changePassword(context: Context, oldPassword: String, newPassword: String, repeatPassword: String)
    }
}