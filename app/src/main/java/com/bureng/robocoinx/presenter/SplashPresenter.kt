package com.bureng.robocoinx.presenter

import android.content.Context
import com.bureng.robocoinx.contract.SplashContract
import com.bureng.robocoinx.model.common.UserCache
import com.bureng.robocoinx.model.request.SignUpRequest
import com.bureng.robocoinx.model.view.ProfileView
import com.bureng.robocoinx.utils.CacheContext
import com.bureng.robocoinx.utils.RoboHandler
import com.bureng.robocoinx.utils.StaticValues

class SplashPresenter(private val ctx: Context, private val view: SplashContract.View) : SplashContract.Presenter {
    override fun authorize(): Boolean {
        val userCache = CacheContext(UserCache::class.java, ctx)[StaticValues.USER_CACHE]
        return userCache != null
    }
    override fun loadActivity() {
        if (authorize()){
            val resp = RoboHandler.parsingHomeResponse(ctx)
            view.goHome(resp as ProfileView)
        }else{
            val resp = RoboHandler.getSignUpRequest(ctx)
            view.loadFingerprint(resp as SignUpRequest)
        }
    }
}