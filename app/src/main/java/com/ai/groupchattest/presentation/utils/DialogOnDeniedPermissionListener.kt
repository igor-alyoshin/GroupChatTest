package com.ai.groupchattest.presentation.utils

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.single.BasePermissionListener

open class DialogOnDeniedPermissionListener private constructor(
        private val context: Context,
        private val title: String,
        private val message: String,
        private val positiveButtonText: String,
        private val icon: Drawable?,
        private val successListener: (() -> Unit)?,
        private val errorListener: (() -> Unit)?
) : BasePermissionListener() {

    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
        successListener?.invoke()
    }

    override fun onPermissionDenied(response: PermissionDeniedResponse) {
        AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButtonText) { dialog, _ ->
                    errorListener?.invoke()
                    dialog.dismiss()
                }
                .setIcon(icon)
                .show()
    }

    /**
     * Builder class to configure the displayed dialog.
     * Non set fields will be initialized to an empty string.
     */
    class Builder private constructor(private val context: Context) {

        private var title: String? = null
        private var message: String? = null
        private var buttonText: String? = null
        private var icon: Drawable? = null
        private var successListener: (() -> Unit)? = null
        private var errorListener: (() -> Unit)? = null

        fun withTitle(title: String?): Builder {
            this.title = title
            return this
        }

        fun withTitle(@StringRes resId: Int): Builder {
            title = context.getString(resId)
            return this
        }

        fun withMessage(message: String?): Builder {
            this.message = message
            return this
        }

        fun withMessage(@StringRes resId: Int): Builder {
            message = context.getString(resId)
            return this
        }

        fun withButtonText(buttonText: String?): Builder {
            this.buttonText = buttonText
            return this
        }

        fun withButtonText(@StringRes resId: Int): Builder {
            buttonText = context.getString(resId)
            return this
        }

        fun withIcon(icon: Drawable?): Builder {
            this.icon = icon
            return this
        }

        fun withIcon(@DrawableRes resId: Int): Builder {
            icon = ContextCompat.getDrawable(context, resId)
            return this
        }

        fun withErrorListener(listener: () -> Unit): Builder {
            this.errorListener = listener
            return this
        }

        fun withSuccessListener(listener: () -> Unit): Builder {
            this.successListener = listener
            return this
        }

        fun build(): DialogOnDeniedPermissionListener {
            val title = title ?: ""
            val message = message ?: ""
            val buttonText = buttonText ?: context.resources.getString(android.R.string.ok)
            return DialogOnDeniedPermissionListener(
                    context, title, message, buttonText, icon, successListener, errorListener
            )
        }

        companion object {
            fun withContext(context: Context): Builder {
                return Builder(context)
            }
        }
    }
}