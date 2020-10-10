package com.bureng.robocoinx.presenter

import android.content.Context
import com.bureng.robocoinx.contract.WithdrawContract
import com.bureng.robocoinx.model.response.InitWithdrawResponse
import com.bureng.robocoinx.model.response.MessageResponse
import com.bureng.robocoinx.utils.RoboHandler

class WithdrawPresenter(val view: WithdrawContract.View): WithdrawContract.Presenter {
    override fun loadData(ctx: Context) {
        view.showProgressBar()
        val response = RoboHandler.parsingHomeWithdrawResponse(ctx)
        view.hideProgressBar()
        if(response is InitWithdrawResponse){
            view.setData(response)
        }else{
            view.showMessage(response as String)
        }
    }

    override fun withdraw(ctx: Context, amount: String, address: String) {
        view.showProgressBar()
        val response = RoboHandler.parsingWithdrawResponse(ctx, amount, address)
        view.hideProgressBar()
        if(response is MessageResponse){
            if(response.code == "s"){
                view.showMessageSuccess(response.message)
            }else{
                view.showMessage(response.message)
            }
        }else{
            view.showMessage(response as String)
        }
    }
}