package com.bureng.robocoinx.contract

import android.content.Context

interface HomeContract {
    interface View{
        fun goToSplash()
    }
    interface  Presenter{
        fun logout(ctx: Context)
        fun callRoll(ctx: Context)
    }

}