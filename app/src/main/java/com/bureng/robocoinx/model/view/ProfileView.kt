package com.bureng.robocoinx.model.view

import android.content.Context
import com.bureng.robocoinx.model.db.ClaimHistory
import com.bureng.robocoinx.repository.ClaimHistoryHandler
import com.bureng.robocoinx.utils.extension.currentBalance
import com.bureng.robocoinx.utils.extension.saveCurrentBalance
import org.jsoup.nodes.Document
import java.io.Serializable
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class ProfileView(doc: Document, context: Context) : Serializable {
    @JvmField
    var userID: String
    @JvmField
    var balance: String
    @JvmField
    var rewardPoint: String
    @JvmField
    var nextRollTime: Int
    @JvmField
    var rpBonusTime: Int
    @JvmField
    var btcBonusTime: Int
    @JvmField
    var disableLottery: Boolean
    @JvmField
    var enableInterest: Boolean
    @JvmField
    var haveCaptcha: Boolean

    @JvmField
    var socketPass: String?

    @JvmField
    var socketId: String?

    @JvmField
    var depositAdress: String

    @JvmField
    var noCaptchaSpec: NoCaptchaSpec? = null

    @JvmField
    var email: String

    @JvmField
    var pointText: String? = null

    @JvmField
    var bonusText: String? = null
    var pendingWithdraws: ArrayList<PendingWithdraw> = arrayListOf()
    private fun getPendingWithdraws(doc: Document): ArrayList<PendingWithdraw> {
        val result = arrayListOf<PendingWithdraw>()
        val element = doc.getElementById("pending_payout_table")
        if (element != null) {
            val divElement =
                element.getElementsByClass("large-12 small-12 columns center lottery_winner_table_box_container effect2")
            val size = divElement.size
            if (size > 1) {
                for (i in 1 until size - 1) {
                    val divValue = divElement[i].getElementsByTag("div")
                    val pendingWithdraw = PendingWithdraw(divValue[0].text(), divValue[1].text())
                    result.add(pendingWithdraw)
                }
            }
        }
        return result
    }

    private fun checkCurrentBalance(newBalance: String, context: Context) {
        var currentBalance = context.currentBalance;
        if (!newBalance.equals(currentBalance, true)) {
            if (currentBalance.isNullOrEmpty()) currentBalance = "0"
            val amount = newBalance.toBigDecimal() - currentBalance.toBigDecimal()
            var type = ClaimHistory.TransactionType.lost.name
            var name = "Lose bitcoin"
            if (amount > BigDecimal.ZERO) {
                type = ClaimHistory.TransactionType.receive.name
                name = "Free bitcoin"
            }
            val currentTime = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())
            ClaimHistoryHandler.getInstance(context).insert(currentTime, name, type, amount.toPlainString(), newBalance)
            context.saveCurrentBalance(newBalance)
        }
    }
    private fun getEmailAddress(doc: Document): String {
        val element = doc.getElementById("contact_form_email")
        return element.`val`()
    }

    private fun getDepositAddress(doc: Document): String {
        val element = doc.getElementById("main_deposit_address")
        return element.`val`()
    }

    private fun getSocketId(doc: Document): String? {
        val scripts = doc.getElementsByTag("script")
        for (script in scripts) {
            val dataNodes = script.dataNodes()
            for (dataNode in dataNodes) {
                val data = dataNode.wholeData
                if (data.contains("socket_userid")) {
                    val dataSplit = data.split(";".toRegex()).toTypedArray()
                    for (datas in dataSplit) {
                        if (datas.contains("socket_userid")) {
                            val socketPassSplit = datas.split("=".toRegex()).toTypedArray()
                            return socketPassSplit[1].replace("'", "").trim()
                        }
                    }
                }
            }
        }
        return null
    }

    private fun getSocketPass(doc: Document): String? {
        val scripts = doc.getElementsByTag("script")
        for (script in scripts) {
            val dataNodes = script.dataNodes()
            for (dataNode in dataNodes) {
                val data = dataNode.wholeData
                if (data.contains("socket_password")) {
                    val dataSplit = data.split(";".toRegex()).toTypedArray()
                    for (datas in dataSplit) {
                        if (datas.contains("socket_password")) {
                            val socketPassSplit = datas.split("=".toRegex()).toTypedArray()
                            return socketPassSplit[1].replace("'", "").trim()
                        }
                    }
                }
            }
        }
        return null
    }

    private fun getCaptchaFlag(doc: Document): Boolean {
        val scripts = doc.getElementsByTag("script")
        for (script in scripts) {
            val dataNodes = script.dataNodes()
            for (dataNode in dataNodes) {
                val data = dataNode.wholeData
                if (data.contains("captcha_type")) {
                    val dataSplit = data.split(";".toRegex()).toTypedArray()
                    for (datas in dataSplit) {
                        if (datas.contains("captcha_type")) {
                            val tokenNameSplit = datas.split(" ".toRegex()).toTypedArray()
                            val findText = tokenNameSplit[4].replace("'", "")
                            if (findText.equals("11", ignoreCase = true)) return true
                        }
                    }
                }
            }
        }
        return false
    }

    private fun getEnableInterest(doc: Document): Boolean {
        val element = doc.getElementById("disable_interest_checkbox")
        val attributes = element.attributes()
        for ((key) in attributes) {
            if (key.equals("checked", ignoreCase = true)) {
                return false
            }
        }
        return true
    }

    private fun getDisableLottery(doc: Document): Boolean {
        val element = doc.getElementById("disable_lottery_checkbox")
        val attributes = element.attributes()
        for ((key) in attributes) {
            if (key.equals("checked", ignoreCase = true)) {
                return true
            }
        }
        return false
    }

    private fun getUserID(doc: Document): String {
        val elUserID = doc.getElementsByClass("left bold")
        return elUserID.text()
    }

    private fun getRPBonusCountDown(doc: Document): Int {
        val scripts = doc.getElementsByTag("script")
        for (script in scripts) {
            val dataNodes = script.dataNodes()
            for (dataNode in dataNodes) {
                val data = dataNode.wholeData
                if (data.contains("free_points")) {
                    val pattern = Pattern.compile("\"free_points\",[0-9]*")
                    val matcher = pattern.matcher(data)
                    if (matcher.find()) {
                        var findText = matcher.group()
                        findText = findText.replace("\"free_points\",", "")
                        return findText.toInt()
                    }
                }
            }
        }
        return 0
    }

    private fun getBTCBonusCountDown(doc: Document): Int {
        val scripts = doc.getElementsByTag("script")
        for (script in scripts) {
            val dataNodes = script.dataNodes()
            for (dataNode in dataNodes) {
                val data = dataNode.wholeData
                if (data.contains("fp_bonus")) {
                    val pattern = Pattern.compile("\"fp_bonus\",[0-9]*")
                    val matcher = pattern.matcher(data)
                    if (matcher.find()) {
                        var findText = matcher.group()
                        findText = findText.replace("\"fp_bonus\",", "")
                        return findText.toInt()
                    }
                }
            }
        }
        return 0
    }

    private fun getBalance(doc: Document): String {
        val elBalance = doc.getElementById("balance")
        return elBalance.text()
    }

    private fun getRewardPoint(doc: Document): String {
        val elRP = doc.getElementsByClass("reward_table_box br_0_0_5_5 user_reward_points font_bold")
        return elRP.text().replace(",", "")
    }

    private fun getNextRollTime(doc: Document): Int {
        val scripts = doc.getElementsByTag("script")
        for (script in scripts) {
            val dataNodes = script.dataNodes()
            for (dataNode in dataNodes) {
                val data = dataNode.wholeData
                if (data.contains("#time_remaining")) {
                    val matcher = extractCountDownRollTime(data)
                    if (matcher != null) return matcher.toInt()
                }
            }
        }
        return 0
    }

    private fun extractCountDownRollTime(data: String): String? {
        val array = data.split("#time_remaining".toRegex()).toTypedArray()
        val input = array[1]
        val pattern = Pattern.compile("\\+.[0-9]*.,")
        val matcher = pattern.matcher(input)
        return if (matcher.find()) {
            matcher.group().substring(1, matcher.group().length - 1)
        } else null
    }

    private fun getPointText(doc: Document): String? {
        val element = doc.getElementById("bonus_container_free_points")
        if (element != null) {
            val elSpan = element.getElementsByClass("free_play_bonus_box_span_large")
            if (elSpan.size > 0) {
                return elSpan[0].text()
            }
        }
        return null
    }

    private fun getBonusText(doc: Document): String? {
        val element = doc.getElementById("bonus_container_fp_bonus")
        if (element != null) {
            val elSpan = element.getElementsByClass("free_play_bonus_box_span_large")
            if (elSpan.size > 0) {
                return elSpan[0].text()
            }
        }
        return null
    }

    init {
        userID = getUserID(doc)
        balance = getBalance(doc)
        rewardPoint = getRewardPoint(doc)
        nextRollTime = getNextRollTime(doc)
        rpBonusTime = getRPBonusCountDown(doc)
        btcBonusTime = getBTCBonusCountDown(doc)
        disableLottery = getDisableLottery(doc)
        enableInterest = getEnableInterest(doc)
        haveCaptcha = getCaptchaFlag(doc)
        socketPass = getSocketPass(doc)
        socketId = getSocketId(doc)
        depositAdress = getDepositAddress(doc)
        email = getEmailAddress(doc)
        pointText = getPointText(doc)
        bonusText = getBonusText(doc)
        pendingWithdraws = getPendingWithdraws(doc)
//        checkCurrentBalance(balance, context)
    }
}