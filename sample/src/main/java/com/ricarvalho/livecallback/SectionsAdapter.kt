package com.ricarvalho.livecallback

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ricarvalho.livecallback.sections.LifecycleContainerFragment
import com.ricarvalho.livecallback.sections.MultipleCallbacksFragment

class SectionsAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount() = 2

    override fun createFragment(position: Int) = when (position) {
        0 -> LifecycleContainerFragment()
        else -> MultipleCallbacksFragment()
    }
}
