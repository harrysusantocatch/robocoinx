package com.bureng.robocoinx.contract

import android.content.Context

interface ProfileContract {
    interface View {
        fun goToSplash()
    }

    interface Presenter {
        fun logout(ctx: Context)
    }
}