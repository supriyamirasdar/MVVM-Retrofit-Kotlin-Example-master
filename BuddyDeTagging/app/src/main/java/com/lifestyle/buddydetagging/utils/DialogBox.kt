package com.lifestyle.buddydetagging.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.request.RequestOptions
import com.lifestyle.buddydetagging.R
import com.lifestyle.buddydetagging.view.splash.SplashActivity

object DialogBox {
    @JvmStatic
    fun showSimplgeDialog(context: Context?, title: String?, message: String?, clickListner: DialogSubmitListner) {
        val activity = context as Activity?
        val dialog = Dialog(activity!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.simple_dialog_box)
        val tvDialogTitle = dialog.findViewById<TextView>(R.id.tvDialogTitle)
        val tvDialogMsg = dialog.findViewById<TextView>(R.id.tvDialogMsg)
        val btnCancel = dialog.findViewById<Button>(R.id.btnCancel)
        val btnSubmit = dialog.findViewById<Button>(R.id.btnSubmit)
        if (title != null) tvDialogTitle.text = title else tvDialogTitle.visibility = View.GONE
        if (message != null) tvDialogMsg.text = message else tvDialogMsg.visibility = View.GONE
        btnCancel.setOnClickListener { v: View? ->
            dialog.dismiss()
            if (context is SplashActivity) context.finish()
        }
        btnSubmit.setOnClickListener { v: View? ->
            dialog.dismiss()
            clickListner.onclick()
        }
        dialog.show()
    }

    fun showAlertDialog(context: Context?, msg: String?, clickListner: DialogSubmitListner) {
        val alertDialog: AlertDialog.Builder? = context?.let { AlertDialog.Builder(it) }
        alertDialog?.setTitle("Alert")
        alertDialog?.setMessage(msg)
        alertDialog?.setPositiveButton("yes") { _, _ ->
            clickListner.onclick()
        }
        alertDialog?.setNegativeButton("No") { _, _ -> }
        val alert: AlertDialog? = alertDialog?.create()
        alert?.setCanceledOnTouchOutside(false)
        alert?.show()
    }


    fun showSorryDialog(activity: Activity) {
        val dialogBuilder = AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater
        @SuppressLint("InflateParams") val dialogView: View = inflater.inflate(R.layout.not_authorized_dialog, null)
        dialogBuilder.setView(dialogView)
        val button_ok = dialogView.findViewById<Button>(R.id.button_ok)
        val alertDialog = dialogBuilder.create()
        alertDialog.show()
        button_ok.setOnClickListener { v: View? -> alertDialog.dismiss() }
    }


    fun showSimpleDialog(activity: Activity) {
        val dialogBuilder = AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater
        @SuppressLint("InflateParams") val dialogView: View = inflater.inflate(R.layout.not_authorized_dialog, null)
        dialogBuilder.setView(dialogView)
        val button_ok = dialogView.findViewById<Button>(R.id.button_ok)
        val image = dialogView.findViewById<ImageView>(R.id.image)
        image.visibility = View.GONE
        val alertDialog = dialogBuilder.create()
        alertDialog.show()
        button_ok.setOnClickListener { v: View? -> alertDialog.dismiss() }
    }

    interface DialogSubmitListner {
        fun onclick()
    }



}