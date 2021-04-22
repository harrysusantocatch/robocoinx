package com.bureng.robocoinx.presenter

import android.content.Context
import com.bureng.robocoinx.R
import com.bureng.robocoinx.contract.HomeContract
import com.bureng.robocoinx.logic.NotificationRoll
import com.bureng.robocoinx.model.common.UserCache
import com.bureng.robocoinx.model.view.ProfileView
import com.bureng.robocoinx.utils.CacheContext
import com.bureng.robocoinx.utils.RoboHandler
import com.bureng.robocoinx.utils.StaticValues
import java.util.*

class HomePresenter(val view: HomeContract.View): HomeContract.Presenter {

    override fun logout(ctx: Context) {
        CacheContext(UserCache::class.java, ctx).clear(StaticValues.USER_CACHE)
        view.goToSplash()
    }

    override fun callRoll(ctx: Context) {
        NotificationRoll(ctx)
        NotificationRoll.executeMainTask(ctx, Calendar.getInstance())
    }

    override fun loadProfile(ctx: Context) {
        view.showProgressBar(R.raw.loading_monkey)
        val resp = RoboHandler.parsingHomeResponse(ctx)
        if (resp is ProfileView) {
            view.goProfile(resp)
        } else {
            view.showMessage(resp as String, 2)
        }
        view.hideProgressBar()
    }

    override fun reload(ctx: Context) {
        view.showProgressBar(R.raw.loading_rocket)
        val resp = RoboHandler.parsingHomeResponse(ctx)
        if (resp is ProfileView) {
            view.reload(resp)
        } else {
            view.showMessage(resp as String, 2)
        }
        view.hideProgressBar()
    }


}