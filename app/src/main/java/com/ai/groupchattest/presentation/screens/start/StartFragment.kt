package com.ai.groupchattest.presentation.screens.start

import android.Manifest.permission.CAMERA
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ai.groupchattest.R
import com.ai.groupchattest.presentation.extensions.startApplicationDetailSettings
import com.ai.groupchattest.presentation.utils.DialogOnDeniedPermissionListener
import com.ai.groupchattest.presentation.utils.withNotNull
import com.karumi.dexter.Dexter
import kotlinx.android.synthetic.main.start_fragment.view.*

class StartFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, state: Bundle?): View {
        return inflater.inflate(R.layout.start_fragment, parent, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.start.setOnClickListener {
            withNotNull(activity) {
                val listener =
                    DialogOnDeniedPermissionListener.Builder
                        .withContext(this)
                        .withTitle(R.string.warning)
                        .withMessage(R.string.permission_not_enabled_description)
                        .withErrorListener { startApplicationDetailSettings() }
                        .withSuccessListener {
                            findNavController().navigate(R.id.groupChatFragment)
                        }
                        .build()
                Dexter.withActivity(this)
                    .withPermission(CAMERA)
                    .withListener(listener)
                    .check()
            }
        }
    }
}
