package com.munesh.videoconferencingtool.utils

import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout

object Extensions {

    fun Fragment.toast(msg: String){
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    fun View.gone() {
        visibility =View.GONE
    }
    fun View.visible() {
        visibility =View.VISIBLE
    }
    fun View.invisible() {
        visibility =View.INVISIBLE
    }

    fun TextInputLayout.getText(errorMsg:String) : String? {
        val text = editText?.text.toString().trim()
        return if (text.isEmpty()){
            isErrorEnabled = true
            error = errorMsg
            null
        }else{
            isErrorEnabled = false
            error = null
            text
        }
    }

    fun TextInputLayout.getMobile():String?{
        val text = editText?.text.toString()
        return if (text.isEmpty()){
            isErrorEnabled = true
            error = "Please enter your mobile number"
            null
        }else if (text.length != 10){
            isErrorEnabled = true
            error = "Please enter valid mobile number"
            null
        }else{
            isErrorEnabled = false
            error = null
            text
        }
    }

    fun TextInputLayout.getEmail():String?{
        val text = editText?.text.toString()
        return if (text.isEmpty()){
            isErrorEnabled = true
            error = "Please enter your Email Address number"
            null
        }else if (!Patterns.EMAIL_ADDRESS.matcher(text).matches()){
            isErrorEnabled = true
            error = "Please enter valid email address"
            null
        }else{
            isErrorEnabled = false
            error = null
            text
        }
    }

    fun TextInputLayout.getAadhaarNo():String?{
        val text = editText?.text.toString()
        return if (text.isEmpty()){
            isErrorEnabled = true
            error = "Please enter your Aadhaar number"
            null
        }else if (text.length != 12){
            isErrorEnabled = true
            error = "Please enter valid Aadhaar number"
            null
        }else{
            isErrorEnabled = false
            error = null
            text
        }
    }
    fun TextInputLayout.getPanNo():String?{
        val text = editText?.text.toString()
        return if (text.isEmpty()){
            isErrorEnabled = true
            error = "Please enter your Pan number"
            null
        }else if (text.length != 10){
            isErrorEnabled = true
            error = "Please enter valid Pan number"
            null
        }else{
            isErrorEnabled = false
            error = null
            text
        }
    }


}