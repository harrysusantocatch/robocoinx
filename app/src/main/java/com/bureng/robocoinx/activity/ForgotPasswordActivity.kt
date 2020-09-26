
package com.bureng.robocoinx.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import com.bureng.robocoinx.R
import com.bureng.robocoinx.contract.ForgotPasswordContract
import com.bureng.robocoinx.model.common.DoAsync
import com.bureng.robocoinx.model.db.Fingerprint
import com.bureng.robocoinx.model.request.SignUpRequest
import com.bureng.robocoinx.presenter.ForgotPasswordPresenter
import com.bureng.robocoinx.utils.CacheContext
import com.bureng.robocoinx.utils.StaticValues
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPasswordActivity : Activity(), View.OnClickListener, ForgotPasswordContract.View {

    private lateinit var presenter: ForgotPasswordContract.Presenter
    private lateinit var captchaNets: String
    private lateinit var fingerprint: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        setOnClickListener()
        presenter = ForgotPasswordPresenter( this)
        fingerprint = CacheContext(Fingerprint::class.java, applicationContext)[StaticValues.FINGERPRINT].fingerprint2
        DoAsync{
            presenter.getCaptchaNet(fingerprint)
        }.execute()
    }

    private fun setOnClickListener() {
        buttonLoginFP.setOnClickListener(this)
        buttonRefreshCaptchaFP.setOnClickListener(this)
        buttonResetFP.setOnClickListener(this)
    }

    override fun showMessage(message: String) {
        runOnUiThread { Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show() }
    }

    override fun goSplash(message: String) {
        runOnUiThread {
            Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
            val timer = object : CountDownTimer(2500, 1000){
                override fun onTick(millisUntilFinished: Long) {
                }
                override fun onFinish() {
                    startActivity(Intent(applicationContext, SplashActivity::class.java))
                }
            }
            timer.start()
        }
    }

    override fun setCaptchaNet(captchaNet: String) {
        runOnUiThread {
            captchaNets = captchaNet
            val path = "https://captchas.freebitco.in/cgi-bin/captcha_generator?client=freebitcoin&random=$captchaNet"
            Picasso.get().load(path).into(captchaFP)
        }
    }

    override fun onClick(v: View?) {
        v?.let {
            when(it.id){
                R.id.buttonLoginFP ->{
                    val signUpRequest = (intent.getSerializableExtra(StaticValues.SIGNUP_REQ) as SignUpRequest)
                    val loginIntent = Intent(applicationContext, LoginActivity::class.java)
                    loginIntent.putExtra(StaticValues.SIGNUP_REQ, signUpRequest)
                    startActivity(loginIntent)
                }
                R.id.buttonRefreshCaptchaFP ->{
                    editTextCaptchaFP.setText("")
                    DoAsync{
                        presenter.getCaptchaNet(fingerprint)
                    }.execute()
                }
                R.id.buttonResetFP ->{
                    val email = editTextEmailFP.text.toString()
                    val captchaResp = editTextCaptchaFP.text.toString()
                    DoAsync{
                        presenter.reset(email, captchaNets, captchaResp, fingerprint)
                    }.execute()
                }
                else->{}
            }
        }
    }
}