package com.ai.groupchattest.presentation.utils

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.widget.Toast


inline fun <T, R> withNotNull(receiver: T?, block: T.() -> R): R? {
    return receiver?.block()
}

fun Context.getUriFromRaw(resId: Int): Uri {
    return Uri.parse("android.resource://$packageName/$resId")
}

private var toast: Toast? = null

fun Context.showToast(string: String) {
    Handler(Looper.getMainLooper()).post {
        toast?.cancel()
        toast = Toast.makeText(applicationContext, string, Toast.LENGTH_LONG)
        toast?.show()
    }
}