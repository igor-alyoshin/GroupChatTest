package com.ai.groupchattest.presentation.extensions

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.ai.groupchattest.presentation.common.RequestCodes.REQUEST_CODE_PERMISSION_SETTINGS


fun Activity.startApplicationDetailSettings() {
    startActivityForResult(
        Intent()
            .setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            .setData(Uri.fromParts("package", packageName, null)), REQUEST_CODE_PERMISSION_SETTINGS
    )
}