package com.bureng.robocoinx.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import com.bureng.robocoinx.R
import com.bureng.robocoinx.contract.ChangePasswordContract
import com.bureng.robocoinx.model.common.DoAsync
import com.bureng.robocoinx.presenter.ChangePasswordPresenter
import com.bureng.robocoinx.utils.LoadingUtils
import com.bureng.robocoinx.utils.extension.showMessage
import kotlinx.android.synthetic.main.activity_change_password.*

class ChangePasswordActivity : Activity(), View.OnClickListener, ChangePasswordContract.View {
    lateinit var presenter: ChangePasswordContract.Presenter
    private var visibilityPassOld = false
    private var visibilityPassNew = false
    private var visibilityPassConfirm = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        setOnCLickListener()
        presenter = ChangePasswordPresenter(this)
    }

    private fun setOnCLickListener() {
        buttonChangePassword.setOnClickListener(this)
        imageVisibilityOldPass.setOnClickListener(this)
        imageVisibilityNewPass.setOnClickListener(this)
        imageVisibilityConfirmPass.setOnClickListener(this)
        imageBackCP.setOnClickListener(this)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onClick(v: View?) {
        v?.let {
            when (it.id) {
                R.id.buttonChangePassword -> {
                    DoAsync {
                        presenter.changePassword(applicationContext, editTextOldPassword.text.toString(), editTextNewPassword.text.toString(), editTextConfirmPassword.text.toString())
                    }.execute()
                }
                R.id.imageVisibilityOldPass -> {
                    if (visibilityPassOld) {
                        visibilityPassOld = false
                        imageVisibilityOldPass.setImageDrawable(getDrawable(R.drawable.ic_visibility_off))
                        editTextOldPassword.transformationMethod = PasswordTransformationMethod()
                        editTextOldPassword.setSelection(editTextOldPassword.length())
                    } else {
                        visibilityPassOld = true
                        imageVisibilityOldPass.setImageDrawable(getDrawable(R.drawable.ic_visibility_on))
                        editTextOldPassword.transformationMethod = null
                        editTextOldPassword.setSelection(editTextOldPassword.length())
                    }
                }
                R.id.imageVisibilityNewPass -> {
                    if (visibilityPassNew) {
                        visibilityPassNew = false
                        imageVisibilityNewPass.setImageDrawable(getDrawable(R.drawable.ic_visibility_off))
                        editTextNewPassword.transformationMethod = PasswordTransformationMethod()
                        editTextNewPassword.setSelection(editTextNewPassword.length())
                    } else {
                        visibilityPassNew = true
                        imageVisibilityNewPass.setImageDrawable(getDrawable(R.drawable.ic_visibility_on))
                        editTextNewPassword.transformationMethod = null
                        editTextNewPassword.setSelection(editTextNewPassword.length())
                    }
                }
                R.id.imageVisibilityConfirmPass -> {
                    if (visibilityPassConfirm) {
                        visibilityPassConfirm = false
                        imageVisibilityConfirmPass.setImageDrawable(getDrawable(R.drawable.ic_visibility_off))
                        editTextConfirmPassword.transformationMethod = PasswordTransformationMethod()
                        editTextConfirmPassword.setSelection(editTextConfirmPassword.length())
                    } else {
                        visibilityPassConfirm = true
                        imageVisibilityConfirmPass.setImageDrawable(getDrawable(R.drawable.ic_visibility_on))
                        editTextConfirmPassword.transformationMethod = null
                        editTextConfirmPassword.setSelection(editTextConfirmPassword.length())
                    }
                }
                R.id.imageBackCP -> {
                    onBackPressed()
                }
                else -> {
                }
            }
        }
    }

    override fun showMessage(message: String, type: Int) {
        runOnUiThread {
            applicationContext.showMessage(buttonChangePassword, message, type)
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