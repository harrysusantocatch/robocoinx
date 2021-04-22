package com.bureng.robocoinx.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bureng.robocoinx.R
import kotlinx.android.synthetic.main.activity_trading_view.*

class TradingViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trading_view)
        tradingView.loadUrl("https://s.tradingview.com/widgetembed/?frameElementId=tradingview_245e7&symbol=BTCUSDT&interval=D&hidesidetoolbar=0&symboledit=1&saveimage=1&toolbarbg=F1F3F6&studies=%5B%5D&hideideas=1&theme=Light&style=1&timezone=Etc%2FUTC&studies_overrides=%7B%7D&overrides=%7B%7D&enabled_features=%5B%5D&disabled_features=%5B%5D&locale=en&utm_source=coinmarketcap.com&utm_medium=widget&utm_campaign=chart&utm_term=BTCUSDT")
    }
}