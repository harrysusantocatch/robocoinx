package com.bureng.robocoinx.presenter;

import android.content.Context;

import com.bureng.robocoinx.contract.SplashContract;
import com.bureng.robocoinx.utils.RoboHandler;
import com.bureng.robocoinx.model.common.UserCache;
import com.bureng.robocoinx.utils.CacheContext;
import com.bureng.robocoinx.utils.StaticValues;

public class SplashPresenter implements SplashContract.Presenter {

    private Context ctx;
    private SplashContract.View view;

    public SplashPresenter(Context _ctx, SplashContract.View _view){
        ctx = _ctx;
        view = _view;
    }

    @Override
    public boolean authorize() {
        UserCache userCache = new CacheContext<>(UserCache.class, ctx).get(StaticValues.USER_CACHE);
        if(userCache == null)
            return false;
        else
            return true;
    }

    @Override
    public Object getResponse() {
        if(authorize())
            return RoboHandler.parsingHomeResponse(ctx);
        else
            return RoboHandler.getSignUpRequest(ctx);
    }
}
