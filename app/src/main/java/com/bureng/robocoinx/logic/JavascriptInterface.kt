package com.bureng.robocoinx.logic

import android.app.Activity
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebView

class JavascriptInterface(private val activity: Activity, private val webView: WebView) {
    @JavascriptInterface
    fun setVisible(){
        activity.runOnUiThread {
            webView.visibility = View.VISIBLE
        }
    }
}