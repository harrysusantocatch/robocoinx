package com.bureng.robocoinx.activity

import android.app.Activity
import android.os.Bundle
import android.view.View
import com.bureng.robocoinx.R
import com.bureng.robocoinx.adapter.TransactionAdapter
import com.bureng.robocoinx.contract.TransactionContract
import com.bureng.robocoinx.model.db.ClaimHistory
import com.bureng.robocoinx.presenter.TransactionPresenter
import kotlinx.android.synthetic.main.activity_history.*

class HistoryActivity : Activity(), TransactionContract.View {
    lateinit var presenter: TransactionContract.Presenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        presenter = TransactionPresenter(this)
        setupView()
    }

    private fun setupView() {
        presenter.getTransactionList(applicationContext)
        imageBack.setOnClickListener {
            this.onBackPressed()
        }
    }

    override fun showTransactionList(content: ArrayList<ClaimHistory>) {
        val adapter = TransactionAdapter(applicationContext, content)
        listTransaction.adapter = adapter
        if (content.size > 0) {
            layout_empty.visibility = View.GONE
        }
    }
}