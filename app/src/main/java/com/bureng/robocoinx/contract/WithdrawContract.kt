package com.bureng.robocoinx.contract

import android.content.Context
import com.bureng.robocoinx.model.response.InitWithdrawResponse

interface WithdrawContract {
    interface View{
        fun setData(initWithdraw: InitWithdrawResponse)
        fun showMessage(message: String)
        fun showMessageSuccess(message: String)
    }
    interface Presenter{
        fun loadData(ctx: Context)
        fun withdraw(amount: String, address: String)
    }
}