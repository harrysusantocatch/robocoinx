package com.bureng.robocoinx.contract

import android.content.Context
import com.bureng.robocoinx.model.db.ClaimHistory

interface TransactionContract {
    interface View {
        fun showTransactionList(content: ArrayList<ClaimHistory>)
    }

    interface Presenter {
        fun getTransactionList(context: Context)
    }

}