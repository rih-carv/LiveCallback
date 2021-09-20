package com.ricarvalho.livecallback.lifecycle

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import org.junit.Assert
import org.junit.Test

class TestLifecycleTest {
    @Test
    fun `default initial state`() {
        val lifecycle = TestLifecycle()
        Assert.assertEquals(Lifecycle.State.INITIALIZED, lifecycle.currentState)
    }

    @Test
    fun `custom initial state`() {
        val lifecycle = TestLifecycle(Lifecycle.State.RESUMED)
        Assert.assertEquals(Lifecycle.State.RESUMED, lifecycle.currentState)
    }

    @Test
    fun `increasing state event`() {
        val lifecycle = TestLifecycle()
        var wasCalled = false

        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) { wasCalled = true }
        })
        lifecycle.state = Lifecycle.State.STARTED

        assert(wasCalled)
    }

    @Test
    fun `decreasing state event`() {
        val lifecycle = TestLifecycle(Lifecycle.State.RESUMED)
        var wasCalled = false

        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onPause(owner: LifecycleOwner) { wasCalled = true }
        })
        lifecycle.state = Lifecycle.State.STARTED

        assert(wasCalled)
    }

    @Test
    fun `same state event`() {
        val lifecycle = TestLifecycle(Lifecycle.State.STARTED)
        var wasNotCalled = true

        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) { wasNotCalled = false }
            override fun onPause(owner: LifecycleOwner) { wasNotCalled = false }
        })
        lifecycle.state = Lifecycle.State.STARTED

        assert(wasNotCalled)
    }

    @Test
    fun `event after observer removal`() {
        val lifecycle = TestLifecycle()
        var wasNotCalled = true

        val observer = object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                wasNotCalled = false
            }

            override fun onPause(owner: LifecycleOwner) {
                wasNotCalled = false
            }
        }
        lifecycle.addObserver(observer)
        lifecycle.removeObserver(observer)
        lifecycle.state = Lifecycle.State.STARTED

        assert(wasNotCalled)
    }
}
