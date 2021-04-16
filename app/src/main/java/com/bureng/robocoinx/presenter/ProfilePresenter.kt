package com.bureng.robocoinx.presenter

import android.content.Context
import com.bureng.robocoinx.contract.ProfileContract
import com.bureng.robocoinx.model.common.UserCache
import com.bureng.robocoinx.utils.CacheContext
import com.bureng.robocoinx.utils.StaticValues

class ProfilePresenter(private val view: ProfileContract.View) : ProfileContract.Presenter {
    override fun logout(ctx: Context) {
        CacheContext(UserCache::class.java, ctx).clear(StaticValues.USER_CACHE)
        view.goToSplash()
    }

}