package com.ricarvalho.livecallback

import androidx.lifecycle.Lifecycle
import com.ricarvalho.livecallback.lifecycle.TestLifecycle
import org.junit.Assert.*
import org.junit.Test

class LiveCallbackContainerTest {
    private val callbacks = LiveCallbackContainer<String, String>()

    //region Behavior
    @Test
    fun `runs registered callback when called`() {
        var wasCalled = false

        callbacks.register(TestLifecycle(Lifecycle.State.STARTED)) {
            wasCalled = true
            ""
        }
        callbacks.invoke("")

        assert(wasCalled)
    }

    @Test
    fun `doesn't runs after lifecycle is destroyed`() {
        var timesCalled = 0
        val lifecycle = TestLifecycle(Lifecycle.State.STARTED)

        callbacks.register(lifecycle) {
            timesCalled++
            ""
        }
        callbacks.invoke("")
        lifecycle.state = Lifecycle.State.DESTROYED
        lifecycle.state = Lifecycle.State.STARTED
        callbacks.invoke("")

        assertEquals(1, timesCalled)
    }

    @Test
    fun `runs as many times as called`() {
        var timesCalled = 0

        callbacks.register(TestLifecycle(Lifecycle.State.STARTED)) {
            timesCalled++
            ""
        }
        callbacks.invoke("")
        callbacks.invoke("")

        assertEquals(2, timesCalled)
    }
    //endregion

    //region Input
    @Test
    fun `callback input`() {
        lateinit var receivedValue: String

        callbacks.register(TestLifecycle(Lifecycle.State.STARTED)) {
            receivedValue = it
            ""
        }
        val input = "input"
        callbacks.invoke(input)

        assertEquals(input, receivedValue)
    }

    @Test
    fun `callback input with callback registered many times`() {
        var concatenatedValues = ""

        val callback: (String) -> String = {
            concatenatedValues += it
            ""
        }

        callbacks.register(TestLifecycle(Lifecycle.State.STARTED), callback)
        callbacks.register(TestLifecycle(Lifecycle.State.STARTED), callback)

        val input = "input,"
        callbacks.invoke(input)

        assertEquals("input,input,", concatenatedValues)
    }

    @Test
    fun `callback input with many separate callbacks`() {
        lateinit var receivedValue1: String
        lateinit var receivedValue2: String

        callbacks.register(TestLifecycle(Lifecycle.State.STARTED)) {
            receivedValue1 = it
            ""
        }
        callbacks.register(TestLifecycle(Lifecycle.State.STARTED)) {
            receivedValue2 = it
            ""
        }

        val input = "input"
        callbacks.invoke(input)

        assertEquals(input, receivedValue1)
        assertEquals(input, receivedValue2)
    }
    //endregion

    //region Output
    @Test
    fun `callback output`() {
        val output = "output"

        callbacks.register(TestLifecycle(Lifecycle.State.STARTED)) { output }
        val receivedValues = callbacks.invoke("")

        assertEquals(output, receivedValues.single())
    }

    @Test
    fun `callback output after destroyed`() {
        val output = "output"
        val lifecycle = TestLifecycle(Lifecycle.State.STARTED)

        callbacks.register(lifecycle) { output }
        lifecycle.state = Lifecycle.State.DESTROYED
        val receivedValues = callbacks.invoke("")

        assertEquals(null, receivedValues.single())
    }

    @Test
    fun `callback output with callback registered many times`() {
        val output = "output"
        val callback: (String) -> String = { output }

        callbacks.register(TestLifecycle(Lifecycle.State.STARTED), callback)
        callbacks.register(TestLifecycle(Lifecycle.State.STARTED), callback)
        val receivedValues = callbacks.invoke("")

        assertEquals(2, receivedValues.size)
        assert(receivedValues.all { it == output })
    }

    @Test
    fun `callback output with many dynamic callbacks`() {
        val output1 = "output1"
        val output2 = "output2"
        fun callback(output: String): (String) -> String = { output }

        callbacks.register(TestLifecycle(Lifecycle.State.STARTED), callback(output1))
        callbacks.register(TestLifecycle(Lifecycle.State.STARTED), callback(output2))
        val receivedValues = callbacks.invoke("")

        assertEquals(2, receivedValues.size)
        assertEquals(output1, receivedValues.firstOrNull())
        assertEquals(output2, receivedValues.lastOrNull())
    }

    @Test
    fun `callback output with many separate callbacks`() {
        val output1 = "output1"
        val output2 = "output2"

        callbacks.register(TestLifecycle(Lifecycle.State.STARTED)) { output1 }
        callbacks.register(TestLifecycle(Lifecycle.State.STARTED)) { output2 }
        val receivedValues = callbacks.invoke("")

        assertEquals(2, receivedValues.size)
        assertEquals(output1, receivedValues.firstOrNull())
        assertEquals(output2, receivedValues.lastOrNull())
    }
    //endregion
}