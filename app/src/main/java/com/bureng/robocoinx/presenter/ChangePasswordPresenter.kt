package com.bureng.robocoinx.presenter

import android.content.Context
import com.bureng.robocoinx.contract.ChangePasswordContract
import com.bureng.robocoinx.model.response.MessageResponse
import com.bureng.robocoinx.utils.RoboHandler

class ChangePasswordPresenter(val view: ChangePasswordContract.View) : ChangePasswordContract.Presenter {
    override fun changePassword(ctx: Context, oldPassword: String, newPassword: String, repeatPassword: String) {
        view.showProgressBar()
        val response = RoboHandler.parsingChangePasswordResponse(ctx, oldPassword, newPassword, repeatPassword)
        view.hideProgressBar()
        if (response is MessageResponse) {
            if (response.code == "s")
                view.showSuccessMessage(response.message)
            else
                view.showMessage(response.message)

        } else {
            view.showMessage(response as String)
        }
    }
}