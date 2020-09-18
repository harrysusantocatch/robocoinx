package com.bureng.robocoinx.utils.extension

import android.content.Context
import com.bureng.robocoinx.utils.StaticValues

val Context.currentBalance: String?
    get() {
        val sharedPreferences = this.getSharedPreferences(StaticValues.CACHE, Context.MODE_PRIVATE)
        return sharedPreferences.getString(StaticValues.CURRENT_BALANCE, null) ?: return null
    }

fun Context.saveCurrentBalance(balance: String){
    val sharedPreferences = this.getSharedPreferences(StaticValues.CACHE, Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString(StaticValues.CURRENT_BALANCE, balance)
    editor.apply()
}