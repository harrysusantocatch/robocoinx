package com.bureng.robocoinx.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.DialogInterface
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
import com.bureng.robocoinx.model.common.DoAsync
import com.bureng.robocoinx.model.db.ClaimHistory
import com.bureng.robocoinx.model.view.ProfileView
import com.bureng.robocoinx.presenter.HomePresenter
import com.bureng.robocoinx.service.BackgroundService
import com.bureng.robocoinx.utils.StaticValues
import kotlinx.android.synthetic.main.activity_home.*
import java.util.*
import java.util.concurrent.TimeUnit

class HomeActivity : Activity(), HomeContract.View, View.OnClickListener {
    private var runManual = false
    private var isUp = false
    private lateinit var depositAddress: String
    private lateinit var presenter: HomeContract.Presenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        presenter = HomePresenter(this)
        setView()

//        auth.createUserWithEmailAndPassword(StaticValues.FIREBASE_EMAIL, StaticValues.FIREBASE_PASS)
//                .addOnCompleteListener(this) { task ->
//                    if (task.isSuccessful) {
//                        // Sign in success, update UI with the signed-in user's information
//                        Log.d(TAG, "createUserWithEmail:success")
//                        val user = auth.currentUser
//                    } else {
//                        // If sign in fails, display a message to the user.
//                    }
//
//                }
//        auth.signInWithEmailAndPassword(StaticValues.FIREBASE_EMAIL, StaticValues.FIREBASE_PASS)
//                .addOnCompleteListener(this) { task ->
//                    if (task.isSuccessful) {
//                        Log.d(TAG, "signInWithEmail:success")
//                        val user = auth.currentUser
//                    } else {
//                        // If sign in fails, display a message to the user.
//                        Log.w(TAG, "signInWithEmail:failure", task.exception)
//                    }
//                }
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
        buttonCaptchaResolve.setOnClickListener(this)
        buttonDepositResolve.setOnClickListener(this)
    }

    private fun setValueUI() {
        runOnUiThread {
            val pp = intent.getSerializableExtra(StaticValues.PROFILE_VIEW) as ProfileView
            if (pp.haveCaptcha) runManual = true
            depositAddress = pp.depositAdress
            textViewUserId.text = "ID: ${pp.userID}"
            textViewBalance.text = pp.balance
            textViewRP.text = "Poin: ${pp.rewardPoint}"

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
                    if (runManual) showRunManualDialog()
                    else {
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
            if (pp.haveCaptcha) {
                constraintResolve.visibility = View.VISIBLE
                pp.noCaptchaSpec?.let {
                    buttonDepositResolve.text = "Deposit ${it.deposit}"
                }
//                text = "MANUAL (" + pp.noCaptchaSpec!!.lottery + ":" + pp.noCaptchaSpec!!.wager + ":" + pp.noCaptchaSpec!!.deposit + ")"
            } else {
                constraintResolve.visibility = View.GONE
            }
            bonusTextView.text = pp.bonusText
            pointTextView.text = pp.pointText
        }
    }

    private fun launchManual() {
        startActivity(Intent(this, ManualRollActivity::class.java))
    }

    @SuppressLint("BatteryLife")
    private fun startRollService(){
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

    override fun onClick(view: View) {
        when (view.id) {
            R.id.buttonStart -> {
                startRollService()
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
                val layoutNotify: ConstraintLayout = dialog.findViewById(R.id.layoutChangePassword)
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
                layoutNotify.setOnClickListener { startActivity(Intent(this, ChangePasswordActivity::class.java)) }
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
            R.id.buttonCaptchaResolve -> {
                launchManual()
            }
            R.id.buttonDepositResolve -> {
                val depositIntent = Intent(this, DepositActivity::class.java)
                depositIntent.putExtra(StaticValues.DEPOSIT_ADDRESS, depositAddress)
                startActivity(depositIntent)
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
//        layoutParams.topToBottom = findViewById<View>(R.id.layoutTimeBonus).id
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
        stopService(Intent(baseContext, BackgroundService::class.java))
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

    private fun showRunManualDialog() {
        val positiveButtonClick = { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
        }
        val builder = AlertDialog.Builder(this, R.style.AlertDialogStyle)
        with(builder)
        {
            setTitle("Run Manual")
            setMessage("Saat ini akun anda dalam posisi manual, " +
                    "Silahkan lakukan RESOLVE FREE BITCOIN yg ada pada halaman ini " +
                    "dengan melakukan Deposit atau Resolve Captcha. Terima kasih")
            setPositiveButton("OK", positiveButtonClick)
            setOnDismissListener {
                startActivity(Intent(applicationContext, SplashActivity::class.java))
            }
            show()
        }
    }
}