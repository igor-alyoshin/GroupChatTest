package com.ai.groupchattest.presentation.screens.start

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.ai.groupchattest.presentation.extensions.startApplicationDetailSettings
import com.ai.groupchattest.R
import com.ai.groupchattest.databinding.StartFragmentBinding
import com.ai.groupchattest.presentation.common.BaseFragment
import com.ai.groupchattest.presentation.utils.DialogOnDeniedPermissionListener
import com.ai.groupchattest.presentation.utils.withNotNull
import com.karumi.dexter.Dexter

class StartFragment : BaseFragment<StartFragmentBinding>() {

    override fun bind(inflater: LayoutInflater, container: ViewGroup?): StartFragmentBinding {
        return StartFragmentBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        withNotNull(binding) {
            start.setOnClickListener {
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
                    Dexter.withActivity(activity)
                        .withPermission(Manifest.permission.CAMERA)
                        .withListener(listener)
                        .check()
                }
            }
        }
    }
}
