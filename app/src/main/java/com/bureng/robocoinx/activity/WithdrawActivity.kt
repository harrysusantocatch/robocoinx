package com.bureng.robocoinx.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.text.HtmlCompat
import com.bureng.robocoinx.R
import com.bureng.robocoinx.contract.WithdrawContract
import com.bureng.robocoinx.model.common.DoAsync
import com.bureng.robocoinx.model.response.InitWithdrawResponse
import com.bureng.robocoinx.presenter.WithdrawPresenter
import kotlinx.android.synthetic.main.activity_withdraw.*
import java.math.BigDecimal


class WithdrawActivity : Activity(), View.OnClickListener, WithdrawContract.View{

    private lateinit var presenter: WithdrawContract.Presenter
    private var initFee: BigDecimal = BigDecimal.ZERO
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
        buttonWithdrawWD.setOnClickListener(this)
        imageBackWD.setOnClickListener(this)
        editTextAmount.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            @SuppressLint("SetTextI18n")
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    if(it.isNotEmpty()){
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
                else ->{}
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun setData(initWithdraw: InitWithdrawResponse) {
        runOnUiThread {
            val fee = initWithdraw.fee
            textViewBalanceWD.text = initWithdraw.balance
            labelFeesWD.text = "Transaction Fees: $fee BTC"
            initFee = fee.toBigDecimal()
        }
    }

    override fun showMessage(message: String) {
        runOnUiThread { Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show() }
    }

    override fun showMessageSuccess(message: String) {
        runOnUiThread {
            val positiveButtonClick = { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
            }

            val builder = AlertDialog.Builder(this, R.style.AlertDialogStyle)
            with(builder)
            {
                setTitle("Success!!")
                setMessage(HtmlCompat.fromHtml(message, HtmlCompat.FROM_HTML_MODE_COMPACT))
                setPositiveButton("OK", positiveButtonClick)
                setOnDismissListener {
                    startActivity(Intent(applicationContext, SplashActivity::class.java))
                }
                show()
            }
        }
    }

    override fun showProgressBar() {
        runOnUiThread {
            progressBarWD.visibility = View.VISIBLE
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }

    override fun hideProgressBar() {
        runOnUiThread {
            progressBarWD.visibility = View.GONE
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }

}
