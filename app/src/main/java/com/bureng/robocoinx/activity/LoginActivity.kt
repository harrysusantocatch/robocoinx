package com.bureng.robocoinx.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import androidx.core.content.ContextCompat
import com.bureng.robocoinx.R
import com.bureng.robocoinx.contract.LoginContract
import com.bureng.robocoinx.model.common.DoAsync
import com.bureng.robocoinx.model.request.SignUpRequest
import com.bureng.robocoinx.model.view.ProfileView
import com.bureng.robocoinx.presenter.LoginPresenter
import com.bureng.robocoinx.utils.LoadingUtils
import com.bureng.robocoinx.utils.StaticValues
import com.bureng.robocoinx.utils.extension.showMessage
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : Activity(), LoginContract.View, View.OnClickListener {
    private var visibilityPass = false
    private lateinit var presenter: LoginContract.Presenter
    lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this, R.color.blackTwo)
        setContentView(R.layout.activity_login)
        presenter = LoginPresenter(this)
        database = Firebase.database.reference
        setOnClickListener()
    }

    private fun setOnClickListener() {
        btn_signUp.setOnClickListener(this)
        buttonLoginLG.setOnClickListener(this)
        textViewForgotPass.setOnClickListener(this)
        imageVisibilityPassLG.setOnClickListener(this)
    }

    override fun showMessage(message: String, type: Int) {
        runOnUiThread {
            applicationContext.showMessage(buttonLoginLG, message, type)
        }
    }

    override fun goHome(profileView: ProfileView) {
        Firebase.auth.signOut()
        val intent = Intent(baseContext, HomeActivity::class.java)
        intent.putExtra(StaticValues.PROFILE_VIEW, profileView)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onClick(view: View) {
        val email = editTextEmailLG.text.toString()
        val password = editTextPassLG.text.toString()
        when (view.id) {
            R.id.buttonLoginLG -> {
                DoAsync {
                    presenter.login(this, applicationContext, email, password)
                }.execute()
            }
            R.id.btn_signUp -> {
                val signUpRequest = (intent.getSerializableExtra(StaticValues.SIGNUP_REQ) as SignUpRequest)
                val intent = Intent(baseContext, SignUpActivity::class.java)
                intent.putExtra(StaticValues.SIGNUP_REQ, signUpRequest)
                startActivity(intent)
            }
            R.id.textViewForgotPass -> {
                val signUpRequest = (intent.getSerializableExtra(StaticValues.SIGNUP_REQ) as SignUpRequest)
                val intent = Intent(baseContext, ForgotPasswordActivity::class.java)
                intent.putExtra(StaticValues.SIGNUP_REQ, signUpRequest)
                startActivity(intent)
            }
            R.id.imageVisibilityPassLG -> {
                if(visibilityPass){
                    visibilityPass = false
                    imageVisibilityPassLG.setImageDrawable(getDrawable(R.drawable.ic_visibility_off))
                    editTextPassLG.transformationMethod = PasswordTransformationMethod()
                    editTextPassLG.setSelection(editTextPassLG.length())
                }else{
                    visibilityPass = true
                    imageVisibilityPassLG.setImageDrawable(getDrawable(R.drawable.ic_visibility_on))
                    editTextPassLG.transformationMethod = null
                    editTextPassLG.setSelection(editTextPassLG.length())
                }
            }
        }
    }

    override fun showProgressBar(rawLoading: Int?) {
        runOnUiThread {
            LoadingUtils.showDialog(this, false, rawLoading)
        }
    }

    override fun hideProgressBar() {
        runOnUiThread {
            LoadingUtils.hideDialog()
        }
    }

}