package com.bureng.robocoinx.utils.extension

import android.app.Activity
import android.app.Dialog
import android.widget.ImageView
import com.bureng.robocoinx.R

fun Activity.showMenuDialog() {
    val dialog = Dialog(this)
    dialog.setContentView(R.layout.menu_option)
    dialog.findViewById<ImageView>(R.id.imageClose)
//    val btnSignUp = dialog.findViewById<Button>(R.id.btnSignupDialog)
//    val btnLogin = dialog.findViewById<TextView>(R.id.txtLoginDialog)
//    btnSignUp.setOnClickListener {
//        val intent = Intent(this, SignUserNameActivity::class.java)
//        startActivity(intent)
//        dialog.dismiss()
//    }
//    btnLogin.setOnClickListener {
//        val intent = Intent(this, LoginActivity::class.java)
//        startActivity(intent)
//        dialog.dismiss()
//    }
    dialog.show()
}