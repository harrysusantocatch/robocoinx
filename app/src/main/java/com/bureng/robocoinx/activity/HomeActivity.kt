package com.bureng.robocoinx.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.PowerManager
import android.provider.Settings
import android.text.Html
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.text.HtmlCompat
import com.bureng.robocoinx.R
import com.bureng.robocoinx.contract.HomeContract
import com.bureng.robocoinx.model.common.DoAsync
import com.bureng.robocoinx.model.view.ProfileView
import com.bureng.robocoinx.presenter.HomePresenter
import com.bureng.robocoinx.service.BackgroundService
import com.bureng.robocoinx.utils.HtmlTagHandler
import com.bureng.robocoinx.utils.LoadingUtils
import com.bureng.robocoinx.utils.StaticValues
import com.bureng.robocoinx.utils.extension.showMessage
import kotlinx.android.synthetic.main.activity_home.*
import java.util.*
import java.util.concurrent.TimeUnit

class HomeActivity : Activity(), HomeContract.View, View.OnClickListener {
    private var runManual = false
    private lateinit var depositAddress: String
    private lateinit var depositAmount: String
    private lateinit var presenter: HomeContract.Presenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        presenter = HomePresenter(this)
        setView()
    }

    private fun setView() {
        setOnClickListener()
        setValueUI()
        val running = isMyServiceRunning
        if (running) {
            btnStart.visibility = View.GONE
            btnStop.visibility = View.VISIBLE
            label_info_service.text = "Service is running"
        } else {
            btnStart.visibility = View.VISIBLE
            btnStop.visibility = View.GONE
        }
    }

    private fun setOnClickListener() {
        buttonRefresh.setOnClickListener(this)
        btnStart.setOnClickListener(this)
        btnStop.setOnClickListener(this)
        buttonPage.setOnClickListener(this)
        btnWithdraw.setOnClickListener(this)
        btnDeposit.setOnClickListener(this)
        btnTransaction.setOnClickListener(this)
        btnHold.setOnClickListener(this)
        btnManualClaim.setOnClickListener(this)
    }

    private fun setValueUI() {
        runOnUiThread {
            val pp = intent.getSerializableExtra(StaticValues.PROFILE_VIEW) as ProfileView
            if (pp.haveCaptcha) runManual = true
            depositAddress = pp.depositAdress
            val userIdValue = "#${pp.userID}"
            value_user_id.text = userIdValue
            value_balance.text = pp.balance
            value_reward_point.text = pp.rewardPoint

            // show count down for Next Roll
            object : CountDownTimer((pp.nextRollTime * 1000).toLong(), 1000) {
                override fun onTick(millis: Long) {
                    val hms = String.format(Locale.US, "%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(millis) -
                                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                            TimeUnit.MILLISECONDS.toSeconds(millis) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)))
                    value_next_bitcoin!!.text = hms
                }
                override fun onFinish() {
                    value_next_bitcoin!!.setText(R.string.stop_count_hour)
                    if (runManual) {
                        layout_manual_claim.visibility = View.VISIBLE
                    } else {
                        DoAsync {
                            presenter.callRoll(applicationContext)
                        }.execute()
                    }
                }
            }.start()

            // show count down for reward point bonus
            object : CountDownTimer((pp.rpBonusTime * 1000).toLong(), 1000) {
                override fun onTick(millis: Long) {
                    val hms = String.format(Locale.US, "%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                            TimeUnit.MILLISECONDS.toMinutes(millis) -
                                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                            TimeUnit.MILLISECONDS.toSeconds(millis) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)))
                    value_reward_point_counter.text = hms
                }
                override fun onFinish() {
                    value_reward_point_counter.setText(R.string.stop_count_day)
                }
            }.start()

            // show count down for btc bonus
            object : CountDownTimer((pp.btcBonusTime * 1000).toLong(), 1000) {
                override fun onTick(millis: Long) {
                    val hms = String.format(Locale.US, "%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                            TimeUnit.MILLISECONDS.toMinutes(millis) -
                                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                            TimeUnit.MILLISECONDS.toSeconds(millis) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)))
                    value_extra_bonus!!.text = hms
                }

                override fun onFinish() {
                    value_extra_bonus!!.setText(R.string.stop_count_day)
                }
            }.start()

            if (pp.haveCaptcha) {
                if (pp.nextRollTime > 0) {
                    layout_manual_claim.visibility = View.GONE
                } else {
                    layout_manual_claim.visibility = View.VISIBLE
                }
                pp.noCaptchaSpec?.let {
                    val depositDesc = "Only by holding bitcoin, you can make automatic claims. the amount of bitcoins to hold is ${it.deposit}"
                    depositAmount = it.deposit
                    label_desc_how_work.text = depositDesc
                }
            } else {
                depositAmount = "0"
                layout_info_automatic_claim.visibility = View.VISIBLE
                layout_manual_claim.visibility = View.GONE
            }
            val html = getString(R.string.desc_advantage_automatic)
            desc_advantage_automatic.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT, null, HtmlTagHandler())
            } else {
                HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY, null, HtmlTagHandler())
            }
//            bonusTextView.text = pp.bonusText
//            pointTextView.text = pp.pointText
        }
    }

    private fun launchManual() {
        startActivity(Intent(this, ManualRollActivity::class.java))
    }

    @SuppressLint("BatteryLife")
    private fun startRollService(){
        label_info_service.text = "Service is running"
        val packageName = applicationContext.packageName
        val pm = applicationContext.getSystemService(POWER_SERVICE) as PowerManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (pm.isIgnoringBatteryOptimizations(packageName)) {
                val intent = Intent(baseContext, BackgroundService::class.java)
                startService(intent)
                btnStart!!.visibility = View.GONE
                btnStop!!.visibility = View.VISIBLE
            } else {
                val intent = Intent()
                intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                intent.data = Uri.parse("package:$packageName")
                startActivityForResult(intent, StaticValues.MY_IGNORE_OPTIMIZATION_REQUEST)
            }
        } else {
            val intent = Intent(baseContext, BackgroundService::class.java)
            startService(intent)
            btnStart!!.visibility = View.GONE
            btnStop!!.visibility = View.VISIBLE
        }

    }

    @SuppressLint("BatteryLife")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == StaticValues.MY_IGNORE_OPTIMIZATION_REQUEST) {
            val pm = applicationContext.getSystemService(POWER_SERVICE) as PowerManager
            val isIgnoringBatteryOptimizations = pm.isIgnoringBatteryOptimizations(packageName)
            if (isIgnoringBatteryOptimizations) {
                // Ignoring battery optimization
                val intent = Intent(baseContext, BackgroundService::class.java)
                startService(intent)
                btnStart!!.visibility = View.GONE
                btnStop!!.visibility = View.VISIBLE
            } else {
                // Not ignoring battery optimization
                val intent = Intent()
                intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                intent.data = Uri.parse("package:" + applicationContext.packageName)
                startActivityForResult(intent, StaticValues.MY_IGNORE_OPTIMIZATION_REQUEST)
            }
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btnStart -> {
//                DoAsync {
//                    NotificationRoll(applicationContext)
//                    NotificationRoll.executeMainTask(applicationContext, Calendar.getInstance())
//                }.execute()
                startRollService()
            }
            R.id.btnStop -> {
                stopService(Intent(baseContext, BackgroundService::class.java))
                btnStart!!.visibility = View.VISIBLE
                btnStop!!.visibility = View.GONE
                label_info_service.text = "Click START to run claim service"
            }
            R.id.buttonPage -> {
                DoAsync {
                    presenter.loadProfile(applicationContext)
                }.execute()
//                val dialog = Dialog(this)
//                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//                dialog.setContentView(R.layout.menu_option)
//                dialog.window?.setLayout(this.resources.displayMetrics.widthPixels, ViewGroup.LayoutParams.WRAP_CONTENT)
//                val closeDialog = dialog.findViewById<ImageView>(R.id.imageClose)
//                val layoutWithdraw: ConstraintLayout = dialog.findViewById(R.id.layoutWithdraw)
//                val layoutDeposit: ConstraintLayout = dialog.findViewById(R.id.layoutDeposit)
//                val layoutNotify: ConstraintLayout = dialog.findViewById(R.id.layoutChangePassword)
//                val btnLogout = dialog.findViewById<Button>(R.id.buttonLogout)
//                closeDialog.setOnClickListener { dialog.dismiss() }
//                layoutWithdraw.setOnClickListener {
//                    dialog.dismiss()
//                    startActivity(Intent(this, WithdrawActivity::class.java))
//                }
//                layoutDeposit.setOnClickListener {
//                    dialog.dismiss()
//                    val newIntent = Intent(this, DepositActivity::class.java)
//                    newIntent.putExtra(StaticValues.DEPOSIT_ADDRESS, depositAddress)
//                    startActivity(newIntent)
//                }
//                layoutNotify.setOnClickListener { startActivity(Intent(this, ChangePasswordActivity::class.java)) }
//                btnLogout.setOnClickListener { presenter.logout(applicationContext) }
//                dialog.show()
            }
            R.id.btnManualClaim -> {
                launchManual()
            }
            R.id.btnDeposit -> {
                val depositIntent = Intent(this, DepositActivity::class.java)
                depositIntent.putExtra(StaticValues.DEPOSIT_ADDRESS, depositAddress)
                depositIntent.putExtra(StaticValues.DEPOSIT_AMOUNT, depositAmount)
                startActivity(depositIntent)
            }
            R.id.btnWithdraw -> {
                startActivity(Intent(this, WithdrawActivity::class.java))
            }
            R.id.btnHold -> {
                val depositIntent = Intent(this, DepositActivity::class.java)
                depositIntent.putExtra(StaticValues.DEPOSIT_ADDRESS, depositAddress)
                depositIntent.putExtra(StaticValues.DEPOSIT_AMOUNT, depositAmount)
                startActivity(depositIntent)
            }
            R.id.btnTransaction -> {
                startActivity(Intent(this, HistoryActivity::class.java))
            }
            R.id.buttonRefresh -> {
                DoAsync {
                    presenter.reload(baseContext)
                }.execute()
            }
        }
    }

    override fun goToSplash() {
        stopService(Intent(baseContext, BackgroundService::class.java))
        val intent = Intent(applicationContext, SplashActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun goProfile(profileView: ProfileView) {
        val intent = Intent(baseContext, ProfileActivity::class.java)
        intent.putExtra(StaticValues.PROFILE_VIEW, profileView)
        startActivity(intent)
    }

    override fun reload(profileView: ProfileView) {
        val intent = Intent(applicationContext, HomeActivity::class.java)
        intent.putExtra(StaticValues.PROFILE_VIEW, profileView)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
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

    override fun showMessage(message: String, type: Int) {
        runOnUiThread {
            applicationContext.showMessage(buttonPage, message, type)
        }
    }

    @Suppress("DEPRECATION")
    private val isMyServiceRunning: Boolean
        get() {
            val manager = (getSystemService(ACTIVITY_SERVICE) as ActivityManager)
            for (service in manager.getRunningServices(Int.MAX_VALUE)) {
                if (BackgroundService::class.java.name == service.service.className) {
                    return true
                }
            }
            return false
        }

}