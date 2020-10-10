package com.bureng.robocoinx.presenter

import android.content.Context
import com.bureng.robocoinx.contract.HomeContract
import com.bureng.robocoinx.logic.NotificationRoll
import com.bureng.robocoinx.model.common.UserCache
import com.bureng.robocoinx.repository.ClaimHistoryHandler
import com.bureng.robocoinx.utils.CacheContext
import com.bureng.robocoinx.utils.StaticValues
import java.util.*

class HomePresenter(val view: HomeContract.View): HomeContract.Presenter {
    override fun getTransactionList(ctx: Context) {
        val content = ClaimHistoryHandler.getInstance(ctx).claimHistories
        view.showTransactionList(content)
    }

    override fun logout(ctx: Context) {
        CacheContext(UserCache::class.java, ctx).clear(StaticValues.USER_CACHE)
        view.goToSplash()
    }

    override fun callRoll(ctx: Context) {
        NotificationRoll(ctx)
        NotificationRoll.executeMainTask(ctx, Calendar.getInstance())
    }

}