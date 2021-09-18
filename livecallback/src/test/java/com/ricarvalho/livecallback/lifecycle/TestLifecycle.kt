package com.ricarvalho.livecallback.lifecycle

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlin.properties.Delegates

class TestLifecycle(initialState: State = State.INITIALIZED) : Lifecycle() {
    private val owner: Owner by lazy { Owner() }
    private val observers = mutableListOf<DefaultLifecycleObserver>()
    var state: State by Delegates.observable(initialState) { _, old, new ->
        observers.distinct().forEach {
            if (old < new) Event.upTo(new)?.notify(it)
            else if (old > new) Event.downTo(new)?.notify(it)
        }
    }

    override fun addObserver(observer: LifecycleObserver) {
        (observer as? DefaultLifecycleObserver)?.let { observers += it }
    }

    override fun removeObserver(observer: LifecycleObserver) {
        (observer as? DefaultLifecycleObserver)?.let { observers -= it }
    }

    override fun getCurrentState() = state

    private fun Event.notify(observer: DefaultLifecycleObserver) = observer.apply {
        when (this@notify) {
            Event.ON_CREATE -> onCreate(owner)
            Event.ON_START -> onStart(owner)
            Event.ON_RESUME -> onResume(owner)
            Event.ON_PAUSE -> onPause(owner)
            Event.ON_STOP -> onStop(owner)
            Event.ON_DESTROY -> onDestroy(owner)
            Event.ON_ANY -> Unit
        }
    }

    inner class Owner : LifecycleOwner {
        override fun getLifecycle() = this@TestLifecycle
    }
}
