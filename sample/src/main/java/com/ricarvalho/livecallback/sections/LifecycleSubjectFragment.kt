package com.ricarvalho.livecallback.sections

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ricarvalho.livecallback.InputCallbackToken
import com.ricarvalho.livecallback.R
import com.ricarvalho.livecallback.databinding.FragmentLifecycleSubjectBinding
import com.ricarvalho.livecallback.sdk.LegacySdkWrapper
import com.ricarvalho.livecallback.sdk.LegacySdkWrapper.representation

class LifecycleSubjectFragment : Fragment() {
    private var onlyWhenActiveCallbackToken: InputCallbackToken<Result<String>>? = null
    private var alsoWhenStoppedCallbackToken: InputCallbackToken<Result<String>>? = null
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

    private fun registerCallbacks() {
        onlyWhenActiveCallbackToken = LegacySdkWrapper.registry.register(
            viewLifecycleOwner.lifecycle,
            callback = this::updateResultText
        )
        alsoWhenStoppedCallbackToken = LegacySdkWrapper.registry.register(lifecycle, true) {
            Toast.makeText(context, it.representation, Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateResultText(result: Result<String>?) {
        binding?.resultTextView?.text = getString(R.string.result, result?.representation ?: "")
    }

    private fun setupView() = binding?.apply {
        val instance = Integer.toHexString(this@LifecycleSubjectFragment.hashCode()).uppercase()
        instanceTextView.text = getString(R.string.instance, instance)
        updateResultText(null)
        triggerButton.setOnClickListener {
            LegacySdkWrapper.perform(
                onlyWhenActiveCallbackToken ?: return@setOnClickListener,
                alsoWhenStoppedCallbackToken ?: return@setOnClickListener,
                customMessage = "invoked from fragment instance 0x$instance"
            )
        }
    }
}
