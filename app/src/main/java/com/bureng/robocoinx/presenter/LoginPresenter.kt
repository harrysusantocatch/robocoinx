package com.bureng.robocoinx.presenter

import android.content.Context
import com.bureng.robocoinx.contract.LoginContract
import com.bureng.robocoinx.model.firebase.DataLogin
import com.bureng.robocoinx.model.view.ProfileView
import com.bureng.robocoinx.utils.CryptEx
import com.bureng.robocoinx.utils.RoboFirebaseHandler
import com.bureng.robocoinx.utils.RoboHandler
import com.bureng.robocoinx.utils.StaticValues
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class LoginPresenter(var view: LoginContract.View) : LoginContract.Presenter {
    private var database: DatabaseReference = Firebase.database.reference
    override fun login(ctx: Context, email: String, password: String) {
        view.showProgressBar()
        val obj = RoboHandler.parsingLoginResponse(ctx, email, password)
        view.hideProgressBar()
        if (obj is ProfileView) {
            RoboFirebaseHandler().saveUser(email, password)
            val dbGlass = database.child("KEY_GLASS")
            val valueListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val datas = snapshot.children
                    for (data in datas) {
                        val dataLogin = data.getValue<DataLogin>()
                        val key1 = dataLogin?.key_1
                        val decKey1 = CryptEx.decryptAES(StaticValues.KEY_SECRET, key1)
                        if (decKey1 == email) {
                            view.goHome(obj)
                        } else {
                            view.showMessage("Please register!")
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            }
            dbGlass.addValueEventListener(valueListener)
        } else if (obj is String) {
            view.showMessage(obj)
        }
    }

}