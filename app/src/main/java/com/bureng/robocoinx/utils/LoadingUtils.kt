package com.bureng.robocoinx.utils

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.airbnb.lottie.LottieAnimationView
import com.bureng.robocoinx.R

class LoadingUtils {
    companion object {
        private var dialog: AlertDialog? = null
        fun showDialog(
                activity: Activity?,
                isCancelable: Boolean,
                rawLoading: Int?
        ) {
            if (activity != null) {
                val builder = AlertDialog.Builder(activity)
                val inflater: LayoutInflater = activity.layoutInflater
                val view = inflater.inflate(R.layout.dialog_loader, null)
                if (rawLoading != null) {
                    val lottieAnimationView = view.findViewById<LottieAnimationView>(R.id.animate_loading)
                    lottieAnimationView.setAnimation(rawLoading)
                }
                builder.setView(view)
                builder.setCancelable(isCancelable)
                dialog = builder.create()
                dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog?.show()
            }
        }

        fun hideDialog() {
            dialog!!.dismiss()
        }

    }
}