package com.ai.groupchattest.presentation.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController


abstract class BaseFragment<T : ViewDataBinding> : Fragment() {

    protected var binding: T? = null

    abstract fun bind(inflater: LayoutInflater, container: ViewGroup?): T

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = bind(inflater, container)
        binding?.lifecycleOwner = viewLifecycleOwner
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}