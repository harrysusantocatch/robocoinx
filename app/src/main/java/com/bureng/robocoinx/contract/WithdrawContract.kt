package com.bureng.robocoinx.contract

import android.content.Context
import com.bureng.robocoinx.model.response.InitWithdrawResponse

interface WithdrawContract {
    interface View{
        fun setData(initWithdraw: InitWithdrawResponse)
        fun showMessage(message: String, type: Int)
        fun showProgressBar(rawLoading: Int?)
        fun hideProgressBar()
    }
    interface Presenter{
        fun loadData(ctx: Context)
        fun withdraw(ctx: Context, amount: String, address: String)
    }
}