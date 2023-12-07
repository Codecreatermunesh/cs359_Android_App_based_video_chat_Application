package com.munesh.videoconferencingtool.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import com.munesh.videoconferencingtool.R
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class LoadingDialog @Inject constructor(@ActivityContext val context: Context) {
    private lateinit var dialog: Dialog

    fun startLoading(){
        dialog = Dialog(context)
        dialog.setContentView(R.layout.loading_dialog)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }
    fun dismiss(){
        try {
            dialog.dismiss()
        }catch (e : Exception){
            Log.e("DialogDismissError",e.message.toString())
        }
    }
}