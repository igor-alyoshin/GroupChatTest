package com.ai.groupchattest.presentation.screens.groupchat

import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ai.groupchattest.GroupChatView
import com.ai.groupchattest.R
import com.ai.groupchattest.presentation.utils.getUriFromRaw
import com.ai.groupchattest.presentation.utils.showToast
import com.ai.groupchattest.presentation.utils.withNotNull
import kotlinx.android.synthetic.main.group_chat_fragment.view.*
import me.aflak.ezcam.EZCam
import kotlin.random.Random


class GroupChatFragment : Fragment() {

    private var camera: EZCam? = null

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, state: Bundle?): View? {
        return inflater.inflate(R.layout.group_chat_fragment, parent, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(view) {
            var idCounter = 0
            group_chat.addSelfUser(idCounter++)
            val random = Random(System.currentTimeMillis())
            add_participant.setOnClickListener {
                group_chat.addNewRandomUser(random, idCounter++)
                context?.showToast(idCounter.toString())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        camera?.close()
    }

    private fun GroupChatView.addSelfUser(id: Int) {
        val textureView =
            addParticipant(id, 200, 200, isSelf = true)
        if (textureView != null) {
            camera = EZCam(context).apply {
                selectCamera(camerasList[CameraCharacteristics.LENS_FACING_FRONT])
                setCameraCallback(object : EZCamCallbackAdapter() {
                    override fun onCameraReady() {
                        startPreview()
                    }
                })
                open(CameraDevice.TEMPLATE_PREVIEW, textureView)
            }
        }
    }

    private fun GroupChatView.addNewRandomUser(r: Random, id: Int) {
        val x = r.nextInt(0, measuredWidth)
        val y = r.nextInt(0, measuredHeight)
        withNotNull(addParticipant(id, x, y)) {
            setOnPreparedListener(MediaPlayer.OnPreparedListener { it.isLooping = true })
            setVideoURI(context.getUriFromRaw(R.raw.test))
            start()
        }
    }
}
