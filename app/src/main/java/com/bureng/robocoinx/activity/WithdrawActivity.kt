package com.bureng.robocoinx.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.annotation.Nullable
import com.bureng.robocoinx.R
import com.bureng.robocoinx.contract.WithdrawContract
import com.bureng.robocoinx.model.common.DoAsync
import com.bureng.robocoinx.model.response.InitWithdrawResponse
import com.bureng.robocoinx.presenter.WithdrawPresenter
import com.bureng.robocoinx.utils.LoadingUtils
import com.bureng.robocoinx.utils.extension.showMessage
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_withdraw.*
import java.math.BigDecimal
import kotlin.math.floor


class WithdrawActivity : Activity(), View.OnClickListener, WithdrawContract.View{

    private lateinit var presenter: WithdrawContract.Presenter
    private var initFee: BigDecimal = BigDecimal.ZERO
    private var totalBalance: Double = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_withdraw)
        setOnListener()
        presenter = WithdrawPresenter(this)
        DoAsync{
            presenter.loadData(applicationContext)
        }.execute()
    }

    private fun setOnListener() {
        btnScanQR.setOnClickListener(this)
        btnMax.setOnClickListener(this)
        buttonWithdrawWD.setOnClickListener(this)
        imageBackWD.setOnClickListener(this)
        editTextAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            @SuppressLint("SetTextI18n")
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    if (it.isNotEmpty()) {
                        var total: BigDecimal = BigDecimal.ZERO
                        total += it.toString().toBigDecimal() + initFee
                        labelAmountDeducted.text = "Amount Deducted: $total BTC"
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onClick(v: View?) {
        v?.let {
            when(it.id){
                R.id.buttonWithdrawWD -> {
                    val amount = editTextAmount.text.toString()
                    val address = editTextBitcoinAddress.text.toString()
                    DoAsync {
                        presenter.withdraw(applicationContext, amount, address)
                    }.execute()
                }
                R.id.imageBackWD -> {
                    onBackPressed()
                }
                R.id.btnMax -> {
                    val value = totalBalance.minus(initFee.toDouble())
                    val valueDouble = floor(value * 100000000) / 100000000
                    val valueStr = valueDouble.toBigDecimal().toString()
                    editTextAmount.setText(valueStr)
                }
                R.id.btnScanQR -> {
                    val intentIntegrator = IntentIntegrator(this)
                    intentIntegrator.setPrompt("Scan QR Code")
                    intentIntegrator.setOrientationLocked(true)
                    intentIntegrator.initiateScan()
                }
                else -> {
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (intentResult != null) {
            if (intentResult.contents == null) {
                Toast.makeText(baseContext, "Cancelled", Toast.LENGTH_SHORT).show()
            } else {
                editTextBitcoinAddress.setText(intentResult.contents)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun setData(initWithdraw: InitWithdrawResponse) {
        runOnUiThread {
            val fee = initWithdraw.fee
            val balance = initWithdraw.balance
            textViewBalanceWD.text = balance
            labelFeesWD.text = "Transaction Fees: $fee BTC"
            initFee = fee.toBigDecimal()
            totalBalance = balance.toDouble()
        }
    }

    override fun showMessage(message: String, type: Int) {
        runOnUiThread { applicationContext.showMessage(buttonWithdrawWD, message, type) }
    }

    override fun showProgressBar(rawLoading: Int?) {
        runOnUiThread {
            LoadingUtils.showDialog(this, false, rawLoading)
        }
    }

    override fun hideProgressBar() {
        runOnUiThread {
            LoadingUtils.hideDialog()
        }
    }

}
