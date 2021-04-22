package com.bureng.robocoinx.utils.extension

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import com.bureng.robocoinx.R
import com.bureng.robocoinx.utils.StaticValues
import com.google.android.material.snackbar.Snackbar

val Context.currentBalance: String?
    get() {
        val sharedPreferences = this.getSharedPreferences(StaticValues.CACHE, Context.MODE_PRIVATE)
        return sharedPreferences.getString(StaticValues.CURRENT_BALANCE, null) ?: return null
    }

fun Context.saveCurrentBalance(balance: String) {
    val sharedPreferences = this.getSharedPreferences(StaticValues.CACHE, Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString(StaticValues.CURRENT_BALANCE, balance)
    editor.apply()
}

fun Context.showMessage(view: View, message: String, type: Int) {
    var color = R.color.transacRed
    if (type == 1) color = R.color.colorBlueDark
    val snackBar = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE)
            .setAction("Ok") {}
            .setActionTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            .setTextColor(ContextCompat.getColor(applicationContext, R.color.greyWhite))
    snackBar.setBackgroundTint(ContextCompat.getColor(applicationContext, color))
    snackBar.show()
}