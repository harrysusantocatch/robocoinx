package com.bureng.robocoinx.presenter

import android.content.Context
import com.bureng.robocoinx.R
import com.bureng.robocoinx.contract.ManualRollContract
import com.bureng.robocoinx.model.view.ProfileView
import com.bureng.robocoinx.utils.RoboHandler

class ManualRollPresenter(val view: ManualRollContract.View) : ManualRollContract.Presenter {

    override fun loadHome(ctx: Context) {
        view.showProgressBar(R.raw.loading_crocket)
        val resp = RoboHandler.parsingHomeResponse(ctx)
        if (resp is ProfileView) {
            view.goHome(resp)
        } else {
            view.showMessage(resp as String, 2)
        }
        view.hideProgressBar()
    }


}