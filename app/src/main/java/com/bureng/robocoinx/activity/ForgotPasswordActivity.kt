
package com.bureng.robocoinx.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.bureng.robocoinx.R
import com.bureng.robocoinx.contract.ForgotPasswordContract
import com.bureng.robocoinx.model.common.DoAsync
import com.bureng.robocoinx.model.db.Fingerprint
import com.bureng.robocoinx.model.request.SignUpRequest
import com.bureng.robocoinx.presenter.ForgotPasswordPresenter
import com.bureng.robocoinx.utils.CacheContext
import com.bureng.robocoinx.utils.LoadingUtils
import com.bureng.robocoinx.utils.StaticValues
import com.bureng.robocoinx.utils.extension.showMessage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPasswordActivity : Activity(), View.OnClickListener, ForgotPasswordContract.View {

    private lateinit var presenter: ForgotPasswordContract.Presenter
    private lateinit var captchaNets: String
    private lateinit var fingerprint: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this, R.color.blackTwo)
        setContentView(R.layout.activity_forgot_password)
        setOnClickListener()
        presenter = ForgotPasswordPresenter( this)
        fingerprint = CacheContext(Fingerprint::class.java, applicationContext)[StaticValues.FINGERPRINT].fingerprint2
        DoAsync{
            presenter.getCaptchaNet(fingerprint)
        }.execute()
    }

    private fun setOnClickListener() {
        imageBack.setOnClickListener(this)
        buttonRefreshCaptchaFP.setOnClickListener(this)
        buttonResetFP.setOnClickListener(this)
    }

    override fun showProgressBar(rawLoading: Int?) {
        runOnUiThread { LoadingUtils.showDialog(this, false, rawLoading) }
    }

    override fun hideProgressBar() {
        runOnUiThread { LoadingUtils.hideDialog() }
    }

    override fun showMessage(message: String, type: Int) {
        runOnUiThread { applicationContext.showMessage(buttonResetFP, message, type) }
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
                R.id.imageBack -> {
                    val signUpRequest = (intent.getSerializableExtra(StaticValues.SIGNUP_REQ) as SignUpRequest)
                    val loginIntent = Intent(applicationContext, LoginActivity::class.java)
                    loginIntent.putExtra(StaticValues.SIGNUP_REQ, signUpRequest)
                    startActivity(loginIntent)
                }
                R.id.buttonRefreshCaptchaFP -> {
                    editTextCaptchaFP.setText("")
                    DoAsync {
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