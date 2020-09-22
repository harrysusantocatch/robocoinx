package com.bureng.robocoinx.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bureng.robocoinx.R
import com.bureng.robocoinx.contract.SignUpContract
import com.bureng.robocoinx.model.common.DoAsync
import com.bureng.robocoinx.model.db.Fingerprint
import com.bureng.robocoinx.model.request.SignUpRequest
import com.bureng.robocoinx.model.view.ProfileView
import com.bureng.robocoinx.presenter.SignUpPresenter
import com.bureng.robocoinx.utils.CacheContext
import com.bureng.robocoinx.utils.StaticValues
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_signup.*

class SignUpActivity : AppCompatActivity(), View.OnClickListener, SignUpContract.View {
    private lateinit var presenter: SignUpContract.Presenter
    private lateinit var fingerprint: String
    private lateinit var captchaNets: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        setOnClickListener()
        presenter = SignUpPresenter(applicationContext, this)
        fingerprint = CacheContext(Fingerprint::class.java, applicationContext)[StaticValues.FINGERPRINT].fingerprint2
        DoAsync{
            presenter.getCaptchaNet(fingerprint)
        }.execute()
    }

    private fun setOnClickListener() {
        buttonLogin.setOnClickListener(this)
        buttonSignUp.setOnClickListener(this)
        buttonRefreshCaptcha.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        v?.let {
            when(it.id){
                R.id.buttonLogin -> {
                    val signUpRequest = (intent.getSerializableExtra(StaticValues.SIGNUP_REQ) as SignUpRequest)
                    val loginIntent = Intent(applicationContext, LoginActivity::class.java)
                    loginIntent.putExtra(StaticValues.SIGNUP_REQ, signUpRequest)
                    startActivity(loginIntent)
                }
                R.id.buttonSignUp -> {
                    DoAsync {
                        val email: String = editTextEmail.text.toString()
                        val password: String = editTextPass.text.toString()
                        val captcha: String = editTextCaptcha.text.toString()
                        val signUpRequest = (intent.getSerializableExtra(StaticValues.SIGNUP_REQ) as SignUpRequest)
                        signUpRequest.email = email
                        signUpRequest.password = password
                        signUpRequest.fingerprint = fingerprint
                        signUpRequest.captchaNet = captchaNets
                        signUpRequest.captchaResp = captcha
                        presenter.signUp(applicationContext, signUpRequest)
                    }.execute()
                }
                R.id.buttonRefreshCaptcha ->{
                    DoAsync{
                        editTextCaptcha.setText("")
                        presenter.getCaptchaNet(fingerprint)
                    }
                }else -> {}
            }
        }
    }

    @SuppressLint("ShowToast")
    override fun showMessage(message: String) {
        runOnUiThread { Toast.makeText(applicationContext, message, Toast.LENGTH_LONG) }
        DoAsync{
            presenter.getCaptchaNet(fingerprint)
        }
    }

    override fun goHome(profileView: ProfileView) {
        val homeIntent = Intent(applicationContext, HomeActivity::class.java)
        homeIntent.putExtra(StaticValues.PROFILE_VIEW, profileView)
        homeIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(homeIntent)
    }

    override fun setCaptchaNet(captchaNet: String) {
        captchaNets = captchaNet
        val path = "https://captchas.freebitco.in/botdetect/e/live/images/$captchaNet.jpeg"
        Picasso.get().load(path).into(captcha)
    }
}