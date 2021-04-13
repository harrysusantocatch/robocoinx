package com.bureng.robocoinx.activity

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.widget.Toast
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.core.text.HtmlCompat
import com.bureng.robocoinx.R
import com.bureng.robocoinx.utils.StaticValues
import kotlinx.android.synthetic.main.activity_deposit.*


class DepositActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deposit)
        setupView()
    }

    private fun setupView() {
        val depositAddress = intent.extras?.get(StaticValues.DEPOSIT_ADDRESS) as String
        val depositAmount = intent.extras?.get(StaticValues.DEPOSIT_AMOUNT) as String
        btcAddress.text = depositAddress
        val html = "If you want to hold bitcoin for <b>automatic claim</b>, please deposit <b>$depositAmount</b> BTC"
        note_hold.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT)
        } else {
            HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY)
        }
        val qrgEncoder = QRGEncoder(depositAddress, null, QRGContents.Type.TEXT, 200)
        qrCodeImage.setImageBitmap(qrgEncoder.encodeAsBitmap())
        this.buttonCopy.setOnClickListener {
            val btcAddress = btcAddress.text.toString()
            val clipBoard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("address", btcAddress)
            clipBoard.setPrimaryClip(clipData)
            Toast.makeText(this, "Deposit address has been copied", Toast.LENGTH_LONG).show()
        }
        imageBack.setOnClickListener {
            this.onBackPressed()
        }
    }
}
