package com.bureng.robocoinx.presenter

import android.content.Context
import com.bureng.robocoinx.contract.TransactionContract
import com.bureng.robocoinx.repository.ClaimHistoryHandler

class TransactionPresenter(val view: TransactionContract.View) : TransactionContract.Presenter {
    override fun getTransactionList(ctx: Context) {
        val content = ClaimHistoryHandler.getInstance(ctx).claimHistories
        view.showTransactionList(content)
    }
}