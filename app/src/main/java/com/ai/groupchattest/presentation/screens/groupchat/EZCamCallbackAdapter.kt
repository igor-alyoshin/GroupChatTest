package com.ai.groupchattest.presentation.screens.groupchat

import android.media.Image
import me.aflak.ezcam.EZCamCallback


open class EZCamCallbackAdapter : EZCamCallback {
    override fun onCameraDisconnected() {
    }

    override fun onCameraReady() {
    }

    override fun onError(message: String) {
    }

    override fun onPicture(image: Image) {
    }
}