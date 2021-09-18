package com.ricarvalho.livecallback.sections

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ricarvalho.livecallback.InputCallbackToken
import com.ricarvalho.livecallback.databinding.FragmentLifecycleSubjectBinding

class LifecycleSubjectFragment : Fragment() {
    private val callbackToken: InputCallbackToken<Result<String>>? = null
    private var binding: FragmentLifecycleSubjectBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentLifecycleSubjectBinding.inflate(inflater, container, false).also {
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
