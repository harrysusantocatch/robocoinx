package com.bureng.robocoinx.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import androidx.core.content.ContextCompat
import com.bureng.robocoinx.R
import com.bureng.robocoinx.contract.SignUpContract
import com.bureng.robocoinx.model.common.DoAsync
import com.bureng.robocoinx.model.db.Fingerprint
import com.bureng.robocoinx.model.request.SignUpRequest
import com.bureng.robocoinx.presenter.SignUpPresenter
import com.bureng.robocoinx.utils.CacheContext
import com.bureng.robocoinx.utils.LoadingUtils
import com.bureng.robocoinx.utils.StaticValues
import com.bureng.robocoinx.utils.extension.showMessage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_signup.*

class SignUpActivity : Activity(), View.OnClickListener, SignUpContract.View {
    private var visibilityPass = false
    private lateinit var presenter: SignUpContract.Presenter
    private lateinit var fingerprint: String
    private lateinit var captchaNets: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this, R.color.blackTwo)
        setContentView(R.layout.activity_signup)
        setOnClickListener()
        presenter = SignUpPresenter(this)
        fingerprint = CacheContext(Fingerprint::class.java, applicationContext)[StaticValues.FINGERPRINT].fingerprint2
        DoAsync{
            presenter.getCaptchaNet(fingerprint)
        }.execute()
    }

    private fun setOnClickListener() {
        btn_signIn.setOnClickListener(this)
        buttonSignUpSG.setOnClickListener(this)
        buttonRefreshCaptcha.setOnClickListener(this)
        imageVisibilityPassSG.setOnClickListener(this)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onClick(v: View?) {
        v?.let {
            when(it.id){
                R.id.btn_signIn -> {
                    val signUpRequest = (intent.getSerializableExtra(StaticValues.SIGNUP_REQ) as SignUpRequest)
                    val loginIntent = Intent(applicationContext, LoginActivity::class.java)
                    loginIntent.putExtra(StaticValues.SIGNUP_REQ, signUpRequest)
                    startActivity(loginIntent)
                }
                R.id.buttonSignUpSG -> {
                    DoAsync {
                        val email: String = editTextEmailSG.text.toString()
                        val password: String = editTextPassSG.text.toString()
                        val captcha: String = editTextCaptcha.text.toString()
                        val signUpRequest = (intent.getSerializableExtra(StaticValues.SIGNUP_REQ) as SignUpRequest)
                        signUpRequest.email = email
                        signUpRequest.password = password
                        signUpRequest.fingerprint = fingerprint
                        signUpRequest.captchaNet = captchaNets
                        signUpRequest.captchaResp = captcha
                        presenter.signUp(this, applicationContext, signUpRequest)
                    }.execute()
                }
                R.id.buttonRefreshCaptcha ->{
                    editTextCaptcha.setText("")
                    DoAsync{
                        presenter.getCaptchaNet(fingerprint)
                    }.execute()
                }
                R.id.imageVisibilityPassSG -> {
                    if (visibilityPass) {
                        visibilityPass = false
                        imageVisibilityPassSG.setImageDrawable(getDrawable(R.drawable.ic_visibility_off))
                        editTextPassSG.transformationMethod = PasswordTransformationMethod()
                        editTextPassSG.setSelection(editTextPassSG.length())
                    } else {
                        visibilityPass = true
                        imageVisibilityPassSG.setImageDrawable(getDrawable(R.drawable.ic_visibility_on))
                        editTextPassSG.transformationMethod = null
                        editTextPassSG.setSelection(editTextPassSG.length())
                    }
                }
                else -> {
                }
            }
        }
    }

    @SuppressLint("ShowToast")
    override fun showMessage(message: String, type: Int) {
        runOnUiThread {
            applicationContext.showMessage(buttonSignUpSG, message, type)
        }
        DoAsync {
            presenter.getCaptchaNet(fingerprint)
        }.execute()
    }

    override fun setCaptchaNet(captchaNet: String) {
        runOnUiThread {
            captchaNets = captchaNet
            val path = "https://captchas.freebitco.in/botdetect/e/live/images/$captchaNet.jpeg"
            Picasso.get().load(path).into(captcha)
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