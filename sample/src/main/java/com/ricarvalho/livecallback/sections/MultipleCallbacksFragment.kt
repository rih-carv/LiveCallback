package com.ricarvalho.livecallback.sections

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.ricarvalho.livecallback.InputCallbackToken
import com.ricarvalho.livecallback.R
import com.ricarvalho.livecallback.databinding.FragmentMultipleCallbacksBinding
import com.ricarvalho.livecallback.sdk.LegacySdkWrapper
import com.ricarvalho.livecallback.sdk.LegacySdkWrapper.representation

class MultipleCallbacksFragment : Fragment() {
    private var toastCallbackToken: InputCallbackToken<Result<String>>? = null
    private var snackbarCallbackToken: InputCallbackToken<Result<String>>? = null
    private var textViewCallbackToken: InputCallbackToken<Result<String>>? = null
    private var binding: FragmentMultipleCallbacksBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentMultipleCallbacksBinding.inflate(inflater, container, false).also {
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
        toastCallbackToken = registerToastCallback()
        snackbarCallbackToken = registerSnackbarCallback()
        textViewCallbackToken = registerTextViewCallback()
    }

    private fun registerToastCallback() =
        LegacySdkWrapper.registry.register(viewLifecycleOwner.lifecycle) {
            Toast.makeText(context, it.representation, Toast.LENGTH_SHORT).show()
        }

    private fun registerSnackbarCallback() =
        LegacySdkWrapper.registry.register(viewLifecycleOwner.lifecycle) { result ->
            binding?.root?.let {
                Snackbar.make(it, result.representation, Snackbar.LENGTH_SHORT).show()
            }
        }

    private fun registerTextViewCallback() = LegacySdkWrapper.registry.register(
        viewLifecycleOwner.lifecycle,
        callback = this::updateResultText
    )

    private fun setupView() = binding?.apply {
        updateResultText(null)
        LegacySdkWrapper.progress.observe(viewLifecycleOwner) {
            progressBar.progress = it
        }
        clearButton.setOnClickListener {
            listOf(toastCheckbox, snackbarCheckbox, textViewCheckbox).forEach {
                it.isChecked = false
            }
            progressBar.progress = 0
            updateResultText(null)
            resultSelector.clearCheck()
        }
        triggerButton.setOnClickListener { triggerOperation() }
    }

    private fun updateResultText(result: Result<String>?) {
        binding?.resultTextView?.text = getString(R.string.result, result?.representation ?: "")
    }

    private fun FragmentMultipleCallbacksBinding.triggerOperation() {
        val callbacksToInvoke = listOfNotNull(
            toastCallbackToken.takeIf { toastCheckbox.isChecked },
            snackbarCallbackToken.takeIf { snackbarCheckbox.isChecked },
            textViewCallbackToken.takeIf { textViewCheckbox.isChecked }
        ).toTypedArray()

        if (resultSelector.checkedRadioButtonId != -1) {
            val success = successRadioButton.isChecked
            LegacySdkWrapper.perform(*callbacksToInvoke, expectedResult = success)
        } else LegacySdkWrapper.perform(*callbacksToInvoke)
    }
}
