package com.bl2617.tamperrecovery.utils

import android.util.Log

object LogUtil {

    const val TAG = "TamperRecovery"

    fun i(tag: String = TAG, msg: String) {
        Log.i(tag, msg)
    }

    fun d(tag: String = TAG, msg: String) {
        Log.d(tag, msg)
    }

    fun e(tag: String = TAG, msg: String) {
        Log.e(tag, msg)
    }

    fun w(tag: String = TAG, msg: String) {
        Log.w(tag, msg)
    }
}