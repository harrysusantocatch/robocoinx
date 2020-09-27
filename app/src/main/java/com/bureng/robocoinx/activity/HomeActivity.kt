package com.bureng.robocoinx.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.PowerManager
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.bureng.robocoinx.R
import com.bureng.robocoinx.adapter.TransactionAdapter
import com.bureng.robocoinx.contract.HomeContract
import com.bureng.robocoinx.model.db.ClaimHistory
import com.bureng.robocoinx.model.view.ProfileView
import com.bureng.robocoinx.presenter.HomePresenter
import com.bureng.robocoinx.service.BackgroundService
import com.bureng.robocoinx.utils.StaticValues
import kotlinx.android.synthetic.main.activity_home.*
import java.util.*
import java.util.concurrent.TimeUnit

class HomeActivity : Activity(), HomeContract.View, View.OnClickListener {
    private var isUp = false
    private lateinit var depositAddress: String
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
        presenter.getTransactionList(applicationContext)
        val running = isMyServiceRunning
        if (running) {
            buttonStart.visibility = View.GONE
            buttonStop.visibility = View.VISIBLE
        } else {
            buttonStart.visibility = View.VISIBLE
            buttonStop.visibility = View.GONE
        }
    }

    private fun setOnClickListener() {
        buttonStart.setOnClickListener(this)
        buttonStop.setOnClickListener(this)
        buttonPage.setOnClickListener(this)
        buttonSlideTransaction.setOnClickListener(this)
    }

    private fun setValueUI() {
        runOnUiThread {
            val pp = intent.getSerializableExtra(StaticValues.PROFILE_VIEW) as ProfileView
            depositAddress = pp.depositAdress
            textViewUserId.text = pp.userID
            textViewBalance.text = pp.balance
            textViewRP.text = pp.rewardPoint

            // show count down for Next Roll
            object : CountDownTimer((pp.nextRollTime * 1000).toLong(), 1000) {
                override fun onTick(millis: Long) {
                    val hms = String.format(Locale.US, "%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(millis) -
                                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                            TimeUnit.MILLISECONDS.toSeconds(millis) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)))
                    textViewnextRoll!!.text = hms
                }
                override fun onFinish() {
                    textViewnextRoll!!.setText(R.string.stop_count_hour)
                    launchManual()
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
                    textViewRPBonus.text = hms
                }
                override fun onFinish() {
                    textViewRPBonus.setText(R.string.stop_count_day)
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
                    textViewBTCBonus!!.text = hms
                }

                override fun onFinish() {
                    textViewBTCBonus!!.setText(R.string.stop_count_day)
                }
            }.start()

            var text = "AUTO"
            if (pp.haveCaptcha) text = "MANUAL (" + pp.noCaptchaSpec!!.lottery + ":" + pp.noCaptchaSpec!!.wager + ":" + pp.noCaptchaSpec!!.deposit + ")"
            textViewHaveCapcha.text = text
        }
    }

    private fun launchManual() {
        startActivity(Intent(this, ManualRollActivity::class.java))
    }

    @SuppressLint("BatteryLife")
    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == StaticValues.MY_IGNORE_OPTIMIZATION_REQUEST) {
            val pm = applicationContext.getSystemService(POWER_SERVICE) as PowerManager
            val isIgnoringBatteryOptimizations = pm.isIgnoringBatteryOptimizations(packageName)
            if (isIgnoringBatteryOptimizations) {
                // Ignoring battery optimization
                val intent = Intent(baseContext, BackgroundService::class.java)
                startService(intent)
                buttonStart!!.visibility = View.GONE
                buttonStop!!.visibility = View.VISIBLE
            } else {
                // Not ignoring battery optimization
                val intent = Intent()
                intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                intent.data = Uri.parse("package:" + applicationContext.packageName)
                startActivityForResult(intent, StaticValues.MY_IGNORE_OPTIMIZATION_REQUEST)
            }
        }
    }

    @SuppressLint("BatteryLife")
    override fun onClick(view: View) {
        when (view.id) {
            R.id.buttonStart -> {
                val packageName = applicationContext.packageName
                val pm = applicationContext.getSystemService(POWER_SERVICE) as PowerManager
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (pm.isIgnoringBatteryOptimizations(packageName)) {
                        val intent = Intent(baseContext, BackgroundService::class.java)
                        startService(intent)
                        buttonStart!!.visibility = View.GONE
                        buttonStop!!.visibility = View.VISIBLE
                    } else {
                        val intent = Intent()
                        intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                        intent.data = Uri.parse("package:$packageName")
                        startActivityForResult(intent, StaticValues.MY_IGNORE_OPTIMIZATION_REQUEST)
                    }
                } else {
                    val intent = Intent(baseContext, BackgroundService::class.java)
                    startService(intent)
                    buttonStart!!.visibility = View.GONE
                    buttonStop!!.visibility = View.VISIBLE
                }
            }
            R.id.buttonStop -> {
                stopService(Intent(baseContext, BackgroundService::class.java))
                buttonStart!!.visibility = View.VISIBLE
                buttonStop!!.visibility = View.GONE
            }
            R.id.buttonPage -> {
                val dialog = Dialog(this)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setContentView(R.layout.menu_option)
                dialog.window?.setLayout(this.resources.displayMetrics.widthPixels, ViewGroup.LayoutParams.WRAP_CONTENT)
                val closeDialog = dialog.findViewById<ImageView>(R.id.imageClose)
                val layoutWithdraw: ConstraintLayout = dialog.findViewById(R.id.layoutWithdraw)
                val layoutDeposit: ConstraintLayout = dialog.findViewById(R.id.layoutDeposit)
                val layoutNotify: ConstraintLayout = dialog.findViewById(R.id.layoutNotification)
                val btnLogout = dialog.findViewById<Button>(R.id.buttonLogout)
                closeDialog.setOnClickListener { dialog.dismiss() }
                layoutWithdraw.setOnClickListener {
                    dialog.dismiss()
                    startActivity(Intent(this, WithdrawActivity::class.java))
                }
                layoutDeposit.setOnClickListener {
                    dialog.dismiss()
                    val newIntent = Intent(this, DepositActivity::class.java)
                    newIntent.putExtra(StaticValues.DEPOSIT_ADDRESS, depositAddress)
                    startActivity(newIntent)
                }
                layoutNotify.setOnClickListener { dialog.dismiss() }
                btnLogout.setOnClickListener { presenter.logout(applicationContext) }
                dialog.show()
            }
            R.id.buttonSlideTransaction -> {
                if (isUp) {
                    slideDown()
                    buttonSlideTransaction!!.setImageResource(R.drawable.ic_up)
                } else {
                    slideUp()
                    buttonSlideTransaction!!.setImageResource(R.drawable.ic_down)
                }
                isUp = !isUp
            }
        }
    }

    private fun slideUp() {
        val layoutParams = headerTransaction.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.topToTop = findViewById<ConstraintLayout>(R.id.parent).id
    }

    private fun slideDown() {
        val set = ConstraintSet()
        val layout: ConstraintLayout = findViewById(R.id.parent)
        set.clone(layout)
        set.clear(R.id.headerTransaction, ConstraintSet.TOP)
        set.applyTo(layout)
        val layoutParams = headerTransaction.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.topToBottom = findViewById<View>(R.id.layoutTimeBonus).id
    }

    override fun showTransactionList(content: ArrayList<ClaimHistory>) {
        val adapter = TransactionAdapter(applicationContext, content)
        listTransaction.adapter = adapter
        if (content.size > 8) {
            buttonSlideTransaction.visibility = View.VISIBLE
        } else {
            buttonSlideTransaction.visibility = View.GONE
        }
    }

    override fun goToSplash() {
        val intent = Intent(applicationContext, SplashActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
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
    
    private fun isAccessibilitySettingsOn(mContext: Context): Boolean {
        var accessibilityEnabled = 0
        val service = packageName + "/" + BackgroundService::class.java.canonicalName
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.applicationContext.contentResolver,
                    Settings.Secure.ACCESSIBILITY_ENABLED)
            Log.v(TAG, "accessibilityEnabled = $accessibilityEnabled")
        } catch (e: SettingNotFoundException) {
            Log.e(TAG, "Error finding setting, default accessibility to not found: "+ e.message)
        }
        val mStringColonSplitter = TextUtils.SimpleStringSplitter(':')
        if (accessibilityEnabled == 1) {
            Log.v(TAG, "***ACCESSIBILITY IS ENABLED*** -----------------")
            val settingValue = Settings.Secure.getString(
                    mContext.applicationContext.contentResolver,
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue)
                while (mStringColonSplitter.hasNext()) {
                    val accessibilityService: String = mStringColonSplitter.next()
                    Log.v(TAG, "-------------- > accessibilityService :: $accessibilityService $service")
                    if (accessibilityService.equals(service, ignoreCase = true)) {
                        Log.v(TAG, "We've found the correct setting - accessibility is switched on!")
                        return true
                    }
                }
            }
        } else {
            Log.v(TAG, "***ACCESSIBILITY IS DISABLED***")
        }
        return false
    }
}