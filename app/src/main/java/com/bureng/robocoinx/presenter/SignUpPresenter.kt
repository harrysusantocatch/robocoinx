package com.bureng.robocoinx.presenter

import android.app.Activity
import android.content.Context
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
        view.showProgressBar()
        val resp = RoboHandler.parsingSignUpResponse(ctx, request)
        view.hideProgressBar()
        if (resp is MessageResponse) {
            if (resp.code == "s") {
                auth = Firebase.auth
                auth.signInWithEmailAndPassword(StaticValues.FIREBASE_EMAIL, StaticValues.FIREBASE_PASS)
                        .addOnCompleteListener(act) { task ->
                            if (task.isSuccessful) {
                                RoboFirebaseHandler().saveUser(request.email, request.password)
                                view.showSuccessMessage(resp.message)
                            } else {
                                view.showMessage("Sorry, please try again later..")
                            }
                        }
            } else {
                view.showMessage(resp.message)
            }
        } else {
            view.showMessage(resp as String)
        }
    }

    override fun getCaptchaNet(fingerprint: String) {
        view.setCaptchaNet(RoboHandler.getCaptchaNet(fingerprint))
    }
}