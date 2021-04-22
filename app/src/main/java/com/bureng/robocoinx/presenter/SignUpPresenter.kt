package com.bureng.robocoinx.presenter

import android.app.Activity
import android.content.Context
import com.bureng.robocoinx.R
import com.bureng.robocoinx.contract.SignUpContract
import com.bureng.robocoinx.model.request.SignUpRequest
import com.bureng.robocoinx.model.response.MessageResponse
import com.bureng.robocoinx.utils.RoboFirebaseHandler
import com.bureng.robocoinx.utils.RoboHandler
import com.bureng.robocoinx.utils.StaticValues
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpPresenter(private val view: SignUpContract.View) : SignUpContract.Presenter {
    private lateinit var auth: FirebaseAuth
    override fun signUp(act: Activity, ctx: Context, request: SignUpRequest) {
        view.showProgressBar(R.raw.loading_rocket)
        val resp = RoboHandler.parsingSignUpResponse(ctx, request)
        view.hideProgressBar()
        if (resp is MessageResponse) {
            if (resp.code == "s") {
                auth = Firebase.auth
                auth.signInWithEmailAndPassword(StaticValues.FIREBASE_EMAIL, StaticValues.FIREBASE_PASS)
                        .addOnCompleteListener(act) { task ->
                            if (task.isSuccessful) {
                                RoboFirebaseHandler().saveUser(request.email, request.password)
                                view.showMessage(resp.message, 1)
                            } else {
                                view.showMessage("Sorry, please try again later..", 2)
                            }
                        }
            } else {
                view.showMessage(resp.message, 2)
            }
        } else {
            view.showMessage(resp as String, 2)
        }
    }

    override fun getCaptchaNet(fingerprint: String) {
        view.setCaptchaNet(RoboHandler.getCaptchaNet(fingerprint))
    }

    private fun runDummySignUp() {
        view.showProgressBar(null)
        Thread.sleep(5000)
        view.hideProgressBar()
        view.showMessage("Sorry, please try again later..", 2)
//        view.showMessage("Selamat anda sudah terdaftar", 1)
    }
}