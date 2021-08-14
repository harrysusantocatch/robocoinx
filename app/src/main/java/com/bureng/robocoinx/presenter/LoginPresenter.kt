package com.bureng.robocoinx.presenter

import android.app.Activity
import android.content.Context
import com.bureng.robocoinx.R
import com.bureng.robocoinx.contract.LoginContract
import com.bureng.robocoinx.model.common.UserCache
import com.bureng.robocoinx.model.firebase.DataLogin
import com.bureng.robocoinx.model.view.ProfileView
import com.bureng.robocoinx.utils.*
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
        view.showProgressBar(R.raw.loading_rocket)
        val resp = RoboHandler.parsingLoginResponse(ctx, email, password)
        view.hideProgressBar()
        if (resp.equals(StaticValues.PROFILE_VIEW)) {
            auth = Firebase.auth
            auth.signInWithEmailAndPassword(StaticValues.FIREBASE_EMAIL, StaticValues.FIREBASE_PASS)
                    .addOnCompleteListener(act) { task ->
                        if (task.isSuccessful) {
//                            RoboFirebaseHandler().saveUser("harry.susanto.catch@gmail.com", "setrets")
                            val dbGlass = database.child("KEY_GLASS")
                            val valueListener = object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val datas = snapshot.children
                                    var exist = false
                                    for (data in datas) {
                                        val dataLogin = data.getValue<DataLogin>()
                                        val key1 = dataLogin?.key_1
                                        val decKey1 = CryptEx.decryptAES(StaticValues.KEY_SECRET, key1)
                                        if (decKey1 == email) {
                                            exist = true
                                            break
                                        }
                                    }
                                    if (exist) {
                                        view.flagHome()
                                    } else {
                                        CacheContext(UserCache::class.java, ctx).clear(StaticValues.USER_CACHE)
                                        view.showMessage("Please register!", 2)
                                    }
                                }
                                override fun onCancelled(error: DatabaseError) {
                                    CacheContext(UserCache::class.java, ctx).clear(StaticValues.USER_CACHE)
                                    view.showMessage("Please register! ${error.message}", 2)
                                }
                            }
                            dbGlass.addValueEventListener(valueListener)
                        } else {
                            view.showMessage("Sorry, please try again later..", 2)
                        }
                    }
        } else {
            view.showMessage(resp as String, 2)
        }
    }

    override fun getHomeResponse(ctx: Context) {
        view.showProgressBar(R.raw.loading_rocket)
        val resp = RoboHandler.parsingHomeResponse(ctx)
        view.hideProgressBar()
        view.goHome(resp as ProfileView)
    }
}