package com.bureng.robocoinx.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.bureng.robocoinx.R
import com.bureng.robocoinx.contract.ManualRollContract
import com.bureng.robocoinx.model.common.DoAsync
import com.bureng.robocoinx.model.view.ProfileView
import com.bureng.robocoinx.presenter.ManualRollPresenter
import com.bureng.robocoinx.utils.FileManager
import com.bureng.robocoinx.utils.LoadingUtils
import com.bureng.robocoinx.utils.StaticValues
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_manual_roll.*
import java.util.*


class ManualRollActivity : Activity(), ManualRollContract.View {
    lateinit var presenter: ManualRollContract.Presenter

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual_roll)
        presenter = ManualRollPresenter(this)
        imageBack.setOnClickListener {
            DoAsync {
                presenter.loadHome(baseContext)
            }.execute()
        }
        webView.visibility = View.INVISIBLE
        var cookies: Map<String, String> = HashMap()
        if (FileManager.getInstance().fileExists(applicationContext, StaticValues.AUTH_COOKIES)) {
            val cookiesStr = FileManager.getInstance().readFile(applicationContext, StaticValues.AUTH_COOKIES)
            val type = object : TypeToken<Map<String?, String?>?>() {}.type
            cookies = Gson().fromJson<Map<String, String>>(cookiesStr, type)
        }
        var cookieString = ""
        for ((key, value) in cookies) {
            cookieString += "$key=$value;"
        }
        val cookiesList = cookieString.split(";")
        cookiesList.forEach { item ->
            CookieManager.getInstance().setCookie(StaticValues.BASE_URL, item)
        }

        webView.apply {
            settings.loadsImagesAutomatically = true
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.displayZoomControls = true
            settings.supportZoom()
            webViewClient = object : WebViewClient(){
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)

                    LoadingUtils.showDialog(this@ManualRollActivity, false, null)
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    LoadingUtils.hideDialog()
                    view?.loadUrl("javascript:(function(){" + "document.getElementById('bottom_user_ads_container').style.display ='none';" + "})()")
                    view?.loadUrl("javascript:(function(){" + "document.getElementById('deposit_promo_message_mobile').style.display ='none';" + "})()")
                    view?.loadUrl("javascript:(function(){" + "document.getElementsByTagName('header')[0].style.display ='none';" + "})()")
                    view?.loadUrl("javascript:(function(){" + "document.getElementsByClassName('large-12 fixed')[0].style.display ='none';" + "})()")
                    view?.loadUrl("javascript:(function(){" + "document.getElementsByClassName('daily_jackpot_main_container_div')[0].style.display ='none';" + "})()")
                    view?.loadUrl("javascript:(function(){" + "document.getElementsByClassName('close_daily_jackpot_main_container_div')[0].style.display ='none';" + "})()")
                    view?.loadUrl("javascript:(function(){" + "document.getElementsByClassName('cross_promo_msg_div')[0].style.display ='none';" + "})()")
                    view?.loadUrl("javascript:(function(){" + "document.getElementsByClassName('custom checkbox')[0].style.display ='none';" + "})()")
                    view?.loadUrl("javascript:(function(){" + "document.getElementById('deposit_withdraw_container').style.display ='none';" + "})()")
                    view?.loadUrl("javascript:(function(){" + "document.getElementById('free_play_payout_table').style.display ='none';" + "})()")
                    view?.loadUrl("javascript:(function(){" + "document.getElementById('free_play_sound').style.display ='none';" + "})()")
                    view?.loadUrl("javascript:(function(){" + "document.getElementById('test_sound').style.display ='none';" + "})()")
                    view?.loadUrl("javascript:(function(){" + "document.querySelector('wait').querySelector('p).style.display ='none';" + "})()")
                    view?.loadUrl("javascript:CloseDailyJPBanner()")
                    view?.loadUrl("javascript: window.callJS.setVisible()")
                }

                override fun onPageCommitVisible(view: WebView?, url: String?) {
                    super.onPageCommitVisible(view, url)

                    view?.loadUrl("javascript:(function(){" + "document.getElementsByClassName('daily_jackpot_main_container_div')[0].style.display ='none';" + "})()")
                    view?.loadUrl("javascript:(function(){" + "document.getElementsByClassName('close_daily_jackpot_main_container_div')[0].style.display ='none';" + "})()")

                    view?.loadUrl("javascript:CloseDailyJPBanner()")
                }
            }
        }
        webView.addJavascriptInterface(com.bureng.robocoinx.logic.JavascriptInterface(this, webView), "callJS")
        webView.loadUrl(StaticValues.BASE_URL)
    }

    override fun goHome(profileView: ProfileView) {
        val intent = Intent(baseContext, HomeActivity::class.java)
        intent.putExtra(StaticValues.PROFILE_VIEW, profileView)
        startActivity(intent)
    }

    override fun showMessage(message: String, type: Int) {
        runOnUiThread { Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show() }
    }

    override fun showProgressBar(rawLoading: Int?) {
        runOnUiThread { LoadingUtils.showDialog(this, false, rawLoading) }
    }

    override fun hideProgressBar() {
        runOnUiThread { LoadingUtils.hideDialog() }
    }

}