package com.bureng.robocoinx.presenter

import android.content.Context
import com.bureng.robocoinx.R
import com.bureng.robocoinx.contract.ChangePasswordContract
import com.bureng.robocoinx.model.common.UserCache
import com.bureng.robocoinx.model.response.MessageResponse
import com.bureng.robocoinx.utils.CacheContext
import com.bureng.robocoinx.utils.RoboHandler
import com.bureng.robocoinx.utils.StaticValues

class ChangePasswordPresenter(val view: ChangePasswordContract.View) : ChangePasswordContract.Presenter {
    override fun changePassword(ctx: Context, oldPassword: String, newPassword: String, repeatPassword: String) {
        view.showProgressBar(R.raw.loading_crocket)
        val response = RoboHandler.parsingChangePasswordResponse(ctx, oldPassword, newPassword, repeatPassword)
        view.hideProgressBar()
        if (response is MessageResponse) {
            if (response.code == "s") {
                CacheContext(UserCache::class.java, ctx).clear(StaticValues.USER_CACHE)
                view.showMessage(response.message, 1)
            } else
                view.showMessage(response.message, 2)

        } else {
            view.showMessage(response as String, 2)
        }
    }
}