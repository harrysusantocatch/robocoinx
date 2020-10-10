package com.bureng.robocoinx.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.bureng.robocoinx.R
import com.bureng.robocoinx.contract.SplashContract
import com.bureng.robocoinx.model.common.DoAsync
import com.bureng.robocoinx.model.db.Fingerprint
import com.bureng.robocoinx.model.request.SignUpRequest
import com.bureng.robocoinx.model.view.ProfileView
import com.bureng.robocoinx.presenter.SplashPresenter
import com.bureng.robocoinx.utils.CacheContext
import com.bureng.robocoinx.utils.FileManager
import com.bureng.robocoinx.utils.StaticValues
import kotlinx.android.synthetic.main.splash_main.*
import org.jsoup.Jsoup

class SplashActivity : Activity(), SplashContract.View {
    private lateinit var presenter: SplashContract.Presenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_main)
        if (ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 10)
        presenter = SplashPresenter(this, this)
        DoAsync{
            presenter.loadActivity()
        }.execute()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 10) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                FileManager.getInstance().appendLog("first....")
            }
        }
    }

    override fun goHome(profileView: ProfileView) {
        val intent = Intent(baseContext, HomeActivity::class.java)
        intent.putExtra(StaticValues.PROFILE_VIEW, profileView)
        startActivity(intent)
    }

    override fun showMessage(message: String?) {
        runOnUiThread { Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()}
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun loadFingerprint(signUpRequest: SignUpRequest) {
        val html: String
        try {
            val inputStream = assets.open("finger.html")
            val document = Jsoup.parse(inputStream, "UTF-8", "")
            val doc = document.html()
            html = doc.replace("[code]", signUpRequest.script)
            runOnUiThread {
                val webView = findViewById<WebView>(R.id.webviewx)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    webView.settings.safeBrowsingEnabled = false
                }
                webView.settings.javaScriptEnabled = true
                webView.addJavascriptInterface(WebAppInterface(this, signUpRequest), "Android")
                webView.loadDataWithBaseURL("blarg://ignored", html, "text/html", "UTF-8", "")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            FileManager.getInstance().appendLog(e)
        }
    }

    override fun showProgressBar() {
        runOnUiThread {
            progressBarSP.visibility = View.VISIBLE
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }

    override fun hideProgressBar() {
        runOnUiThread {
            progressBarSP.visibility = View.GONE
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }

    inner class WebAppInterface internal constructor(var context: Context, private var signUpRequest: SignUpRequest) {
        @JavascriptInterface
        fun getFingerprint(data: String) {
            if (!data.contains("error")) {
                val split = data.split(":".toRegex()).toTypedArray()
                val fingerprint = Fingerprint(split[0], split[1])
                CacheContext(Fingerprint::class.java, context).save(fingerprint, StaticValues.FINGERPRINT)
                goToLogin(context, signUpRequest)
            }
        }

        private fun goToLogin(context: Context, signUpRequest: SignUpRequest) {
            val intent = Intent(context, SignUpActivity::class.java)
            intent.putExtra(StaticValues.SIGNUP_REQ, signUpRequest)
            context.startActivity(intent)
        }
    }
}