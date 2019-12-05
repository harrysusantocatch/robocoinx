package com.example.robocoinx.activity

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.robocoinx.R
import kotlinx.android.synthetic.main.activity_withdraw.*


class WithdrawActivity : Activity(), AdapterView.OnItemSelectedListener, View.OnClickListener{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_withdraw)
        setupView()
    }

    private fun setupView() {
        val list: MutableList<String> = ArrayList()
        list.add("Auto")
        list.add("Slow")
        list.add("Instant")
        val spinnerAdapter = ArrayAdapter<String>(this, R.layout.spinner_item, list)
        spinner.adapter = spinnerAdapter
        spinner.onItemSelectedListener = this
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.isEnabled = false

        btnChange.setOnClickListener(this)
        btnSave.setOnClickListener(this)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val textView = view as TextView
        val key = textView.text.toString()
        val time = getTime(key)
        val fee = getFee(key)
        cardTime.text = time
        cardFee.text = fee
        valueTime.setText(time)
        valueFee.setText(fee)
        val keyFirst = key.toCharArray()[0]
        val typeSecond = key.substring(1, key.length)
        firstType.text = keyFirst.toString()
        secondType.text = typeSecond
    }

    fun getTime(key: String): String{
        val takeTime : MutableMap<String, String> = mutableMapOf(
                "Auto" to "Every sunday",
                "Slow" to "6 - 24 hour",
                "Instant" to "~15 minute")
        return takeTime[key]!!
    }

    fun getFee(key: String): String{
        val fees : MutableMap<String, String> = mutableMapOf(
                "Auto" to "0.00000933",
                "Slow" to "0.00000933",
                "Instant" to "0.00003967")
        return fees[key]!!
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnChange -> stateChange()
            R.id.btnSave -> stateSave()
        }
    }

    private fun stateChange() {
        spinner.isEnabled = true
        spinner.isClickable = true
        spinner.setBackgroundResource(R.drawable.spinner_bg_edit)
        valueAddress.isEnabled = true
        valueAddress.setBackgroundResource(R.drawable.button_orange)
        valueAmount.isEnabled = true
        valueAmount.setBackgroundResource(R.drawable.button_orange)
        layoutAction2.visibility = View.GONE
        layoutAction1.visibility = View.VISIBLE
    }
    private fun stateSave() {
        spinner.isEnabled = false
        spinner.isClickable = false
        spinner.setBackgroundResource(R.drawable.spinner_bg)
        valueAddress.isEnabled = false
        valueAddress.setBackgroundResource(R.drawable.button_orange2)
        valueAmount.isEnabled = false
        valueAmount.isFocusable = false
        valueAmount.setBackgroundResource(R.drawable.button_orange2)
        layoutAction2.visibility = View.VISIBLE
        layoutAction1.visibility = View.GONE
    }
}
