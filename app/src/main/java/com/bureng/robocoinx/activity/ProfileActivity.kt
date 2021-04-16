package com.bureng.robocoinx.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.bureng.robocoinx.R
import com.bureng.robocoinx.contract.ProfileContract
import com.bureng.robocoinx.model.view.ProfileView
import com.bureng.robocoinx.presenter.ProfilePresenter
import com.bureng.robocoinx.service.BackgroundService
import com.bureng.robocoinx.utils.StaticValues
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : Activity(), View.OnClickListener, ProfileContract.View {
    lateinit var presenter: ProfileContract.Presenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        presenter = ProfilePresenter(this)
        setOnClickListener()
        setValueUI()
    }

    private fun setOnClickListener() {
        layout_logout.setOnClickListener(this)
        layout_change_password.setOnClickListener(this)
        imageBack.setOnClickListener(this)
    }

    private fun setValueUI() {
        runOnUiThread {
            val pp = intent.getSerializableExtra(StaticValues.PROFILE_VIEW) as ProfileView
            value_email.text = pp.email
            if (pp.haveCaptcha) value_claim_type.text = "Manual"
            if (pp.rpBonusTime > 0) value_reward_point_status.text = "ON"
            if (pp.btcBonusTime > 0) value_extra_bonus_status.text = "ON"
            value_reward_point_count.text = pp.rewardPoint
            value_contract_id.text = "# ${pp.userID}"
            value_balance.text = pp.balance
        }
    }

    override fun onClick(v: View?) {
        v?.let {
            when (it.id) {
                R.id.layout_logout -> {
                    presenter.logout(applicationContext)
                }
                R.id.layout_change_password -> {
                    startActivity(Intent(this, ChangePasswordActivity::class.java))
                }
                R.id.imageBack -> {
                    this.onBackPressed()
                }
            }
        }
    }

    override fun goToSplash() {
        stopService(Intent(baseContext, BackgroundService::class.java))
        val intent = Intent(applicationContext, SplashActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

}