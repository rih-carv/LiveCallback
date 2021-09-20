package com.ricarvalho.livecallback.sections

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.ricarvalho.livecallback.R
import com.ricarvalho.livecallback.databinding.FragmentLifecycleContainerBinding
import com.ricarvalho.livecallback.sdk.LegacySdkWrapper

class LifecycleContainerFragment : Fragment() {
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
        setupView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun setupView() = binding?.apply {
        LegacySdkWrapper.progress.observe(viewLifecycleOwner) {
            progressBar.progress = it
        }
        destroyButton.setOnClickListener {
            childFragmentManager.beginTransaction()
                .remove(childFragmentManager.fragments.single())
                .commit()
        }
        stopButton.setOnClickListener {
            childFragmentManager.beginTransaction()
                .setMaxLifecycle(childFragmentManager.fragments.single(), Lifecycle.State.CREATED)
                .commit()
        }
        replaceButton.setOnClickListener {
            childFragmentManager.beginTransaction()
                .replace(R.id.subjectFragmentContainer, LifecycleSubjectFragment())
                .commit()
        }
    }
}
