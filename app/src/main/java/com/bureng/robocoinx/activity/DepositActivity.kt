package com.bureng.robocoinx.activity

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
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
        btcAddress.text = depositAddress
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
