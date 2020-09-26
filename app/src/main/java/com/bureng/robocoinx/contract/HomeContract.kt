package com.bureng.robocoinx.contract

import android.content.Context
import com.bureng.robocoinx.model.db.ClaimHistory

interface HomeContract {
    interface View{
        fun showTransactionList(content: ArrayList<ClaimHistory>)
        fun goToSplash()
    }
    interface  Presenter{
        fun getTransactionList(context: Context)
        fun logout(ctx: Context)
    }

}