package com.bureng.robocoinx.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.bureng.robocoinx.R
import com.bureng.robocoinx.contract.LoginContract
import com.bureng.robocoinx.model.common.DoAsync
import com.bureng.robocoinx.model.request.SignUpRequest
import com.bureng.robocoinx.model.view.ProfileView
import com.bureng.robocoinx.presenter.LoginPresenter
import com.bureng.robocoinx.utils.StaticValues
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
        setContentView(R.layout.activity_login)
        presenter = LoginPresenter(this)
        database = Firebase.database.reference
        setOnClickListener()
    }

    private fun setOnClickListener() {
        buttonSignUpLG.setOnClickListener(this)
        buttonLoginLG.setOnClickListener(this)
        textViewForgotPass.setOnClickListener(this)
        imageVisibilityPassLG.setOnClickListener(this)
    }

    override fun showMessage(message: String) {
        runOnUiThread { Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show() }
    }

    override fun goHome(profileView: ProfileView) {

        val intent = Intent(baseContext, HomeActivity::class.java)
        intent.putExtra(StaticValues.PROFILE_VIEW, profileView)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onClick(view: View) {
        val email = editTextEmailLG.text.toString()
        val password = editTextPassLG.text.toString()
        when (view.id) {
            R.id.buttonLoginLG -> {
                DoAsync{
                    presenter.login(applicationContext, email, password)
                }.execute()
            }
            R.id.buttonSignUpLG -> {
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

    override fun showProgressBar() {
        runOnUiThread {
            progressBarLG.visibility = View.VISIBLE
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }

    override fun hideProgressBar() {
        runOnUiThread {
            progressBarLG.visibility = View.GONE
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }

}