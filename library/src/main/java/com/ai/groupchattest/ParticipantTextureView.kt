package com.ai.groupchattest

import android.content.Context
import android.graphics.SurfaceTexture
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaPlayer.OnBufferingUpdateListener
import android.media.MediaPlayer.OnCompletionListener
import android.media.MediaPlayer.OnPreparedListener
import android.media.MediaPlayer.OnVideoSizeChangedListener
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.MediaController.MediaPlayerControl
import java.io.IOException

class ParticipantTextureView(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
    TextureView(context, attrs, defStyle) {

    private val preparedListener = OnPreparedListener { mp ->
        currentState = STATE_PREPARED
        canSeekForward = true
        canSeekBack = canSeekForward
        canPause = canSeekBack
        onPreparedListener?.onPrepared(mp)
        videoWidth = mp.videoWidth
        videoHeight = mp.videoHeight
        if (videoWidth != 0 && videoHeight != 0) {
            surfaceTexture.setDefaultBufferSize(videoWidth, videoHeight)
        }
        if (targetState == STATE_PLAYING) start()
    }

    private val completionListener = OnCompletionListener {
        currentState = STATE_PLAYBACK_COMPLETED
        targetState = STATE_PLAYBACK_COMPLETED
    }

    private val errorListener = MediaPlayer.OnErrorListener { mp, framework_err, impl_err ->
        Log.d(TAG, "Error: $framework_err,$impl_err")
        currentState = STATE_ERROR
        targetState = STATE_ERROR
        true
    }

    private val surfaceTextureListener = object : SurfaceTextureListener {
        override fun onSurfaceTextureSizeChanged(texture: SurfaceTexture, width: Int, height: Int) {
            val isValidState = targetState == STATE_PLAYING
            val hasValidSize = width > 0 && height > 0
            if (mp != null && isValidState && hasValidSize) start()
        }

        override fun onSurfaceTextureAvailable(texture: SurfaceTexture, width: Int, height: Int) {
            surface = Surface(texture)
            openVideo()
        }

        override fun onSurfaceTextureDestroyed(texture: SurfaceTexture): Boolean {
            surface?.release()
            surface = null
            release(true)
            return true
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
        }
    }

    private val sizeChangedListener = OnVideoSizeChangedListener { _, width, height ->
        videoWidth = width
        videoHeight = height
        if (width > 0 && height > 0) {
            surfaceTexture.setDefaultBufferSize(width, height)
            requestLayout()
        }
    }

    private val isInPlaybackState get() = mp != null && currentState > STATE_PREPARING

    private var uri: Uri? = null
    private var headers: Map<String, String>? = null

    private var currentState = STATE_IDLE
    private var targetState = STATE_IDLE

    private var surface: Surface? = null
    private var mp: MediaPlayer? = null
    private var videoWidth = 0
    private var videoHeight = 0
    private var onPreparedListener: OnPreparedListener? = null
    private var canPause = false
    private var canSeekBack = false
    private var canSeekForward = false

    init {
        setSurfaceTextureListener(surfaceTextureListener)
    }

    fun start() {
        if (isInPlaybackState) {
            mp?.start()
            currentState = STATE_PLAYING
        }
        targetState = STATE_PLAYING
    }

    fun setVideoURI(uri: Uri, headers: Map<String, String>? = null) {
        this.uri = uri
        this.headers = headers
        openVideo()
        requestLayout()
        invalidate()
    }

    fun setOnPreparedListener(listener: OnPreparedListener?) {
        onPreparedListener = listener
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var width = View.getDefaultSize(videoWidth, widthMeasureSpec)
        var height = View.getDefaultSize(videoHeight, heightMeasureSpec)
        if (videoWidth > 0 && videoHeight > 0) {
            val widthSpecMode = MeasureSpec.getMode(widthMeasureSpec)
            val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
            val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)
            val heightSpecSize = MeasureSpec.getSize(heightMeasureSpec)
            if (widthSpecMode == MeasureSpec.EXACTLY && heightSpecMode == MeasureSpec.EXACTLY) {
                width = widthSpecSize
                height = heightSpecSize
                if (videoWidth * height < width * videoHeight) {
                    height = width * videoHeight / videoWidth
                    translationY = (heightSpecSize - height) / 2f
                } else if (videoWidth * height > width * videoHeight) {
                    width = height * videoWidth / videoHeight
                    translationX = (widthSpecSize - width) / 2f
                }
            } else if (widthSpecMode == MeasureSpec.EXACTLY) {
                width = widthSpecSize
                height = width * videoHeight / videoWidth
                if (heightSpecMode == MeasureSpec.AT_MOST && height > heightSpecSize) {
                    translationY = (heightSpecSize - height) / 2f
                }
            } else if (heightSpecMode == MeasureSpec.EXACTLY) {
                height = heightSpecSize
                width = height * videoWidth / videoHeight
                if (widthSpecMode == MeasureSpec.AT_MOST && width > widthSpecSize) {
                    translationX = (widthSpecSize - width) / 2f
                }
            } else {
                width = videoWidth
                height = videoHeight
                if (heightSpecMode == MeasureSpec.AT_MOST && height > heightSpecSize) {
                    translationY = (heightSpecSize - height) / 2f
                }
                if (widthSpecMode == MeasureSpec.AT_MOST && width > widthSpecSize) {
                    translationX = (widthSpecSize - width) / 2f
                }
            }
        }
        setMeasuredDimension(width, height)
    }

    private fun openVideo() {
        val uri = uri ?: return
        val surface = surface ?: return
        release(false)
        try {
            mp = MediaPlayer().apply {
                setOnPreparedListener(preparedListener)
                setOnVideoSizeChangedListener(sizeChangedListener)
                setOnCompletionListener(completionListener)
                setOnErrorListener(errorListener)
                setDataSource(context.applicationContext, uri, headers)
                setSurface(surface)
                setScreenOnWhilePlaying(true)
                prepareAsync()
                currentState = STATE_PREPARING
            }
        } catch (e: IOException) {
            Log.w(TAG, "Unable to open content: $uri", e)
            currentState = STATE_ERROR
            targetState = STATE_ERROR
            errorListener.onError(mp, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0)
        } catch (ex: IllegalArgumentException) {
            Log.w(TAG, "Unable to open content: $uri", ex)
            currentState = STATE_ERROR
            targetState = STATE_ERROR
            errorListener.onError(mp, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0)
        }
    }

    private fun release(clearTargetState: Boolean) {
        mp?.reset()
        mp?.release()
        mp = null
        currentState = STATE_IDLE
        if (clearTargetState) targetState = STATE_IDLE
    }

    companion object {
        private val TAG = ParticipantTextureView::class.java.simpleName

        private const val STATE_ERROR = -1
        private const val STATE_IDLE = 0
        private const val STATE_PREPARING = 1
        private const val STATE_PREPARED = 2
        private const val STATE_PLAYING = 3
        private const val STATE_PLAYBACK_COMPLETED = 4
    }
}