package com.ricarvalho.livecallback.sections

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ricarvalho.livecallback.InputCallbackToken
import com.ricarvalho.livecallback.databinding.FragmentLifecycleContainerBinding

class LifecycleContainerFragment : Fragment() {
    private val callbackToken: InputCallbackToken<Result<String>>? = null
    private var binding: FragmentLifecycleContainerBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentLifecycleContainerBinding.inflate(inflater, container, false).also {
        binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerCallbacks()
        setupView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun registerCallbacks() {}

    private fun setupView() {}
}
