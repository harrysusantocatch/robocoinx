package com.bureng.robocoinx.presenter

import android.app.Activity
import android.content.Context
import com.bureng.robocoinx.contract.LoginContract
import com.bureng.robocoinx.model.common.UserCache
import com.bureng.robocoinx.model.firebase.DataLogin
import com.bureng.robocoinx.model.view.ProfileView
import com.bureng.robocoinx.utils.CacheContext
import com.bureng.robocoinx.utils.CryptEx
import com.bureng.robocoinx.utils.RoboHandler
import com.bureng.robocoinx.utils.StaticValues
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class LoginPresenter(var view: LoginContract.View) : LoginContract.Presenter {
    private var database: DatabaseReference = Firebase.database.reference
    private lateinit var auth: FirebaseAuth
    override fun login(act: Activity, ctx: Context, email: String, password: String) {
        view.showProgressBar()
        val obj = RoboHandler.parsingLoginResponse(ctx, email, password)
        view.hideProgressBar()
        if (obj is ProfileView) {
            auth = Firebase.auth
            auth.signInWithEmailAndPassword(StaticValues.FIREBASE_EMAIL, StaticValues.FIREBASE_PASS)
                    .addOnCompleteListener(act) { task ->
                        if (task.isSuccessful) {
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
                                    CacheContext(UserCache::class.java, ctx).clear(StaticValues.USER_CACHE)
                                    view.showMessage("Please register! ${error.message}")
                                }
                            }
                            dbGlass.addValueEventListener(valueListener)
                        } else {
                            view.showMessage("Sorry, please try again later..")
                        }
                    }
        } else if (obj is String) {
            view.showMessage(obj)
        }
    }

}