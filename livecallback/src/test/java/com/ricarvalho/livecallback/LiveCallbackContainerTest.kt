package com.ricarvalho.livecallback

import androidx.lifecycle.Lifecycle.State.*
import com.ricarvalho.livecallback.lifecycle.TestLifecycle
import org.junit.Assert.*
import org.junit.Test

class LiveCallbackContainerTest {
    private val callbacks = LiveCallbackContainer<String, String>()

    //region Behavior
    @Test
    fun `runs registered callback when called`() {
        var wasCalled = false

        callbacks.add(TestLifecycle(STARTED)) {
            wasCalled = true
            ""
        }
        callbacks("")

        assert(wasCalled)
    }

    @Test
    fun `runs while stopped`() {
        var wasCalled = false

        callbacks.add(TestLifecycle(CREATED), true) {
            wasCalled = true
            ""
        }
        callbacks("")

        assert(wasCalled)
    }

    @Test
    fun `doesn't runs while stopped`() {
        var wasNotCalled = true

        callbacks.add(TestLifecycle(CREATED)) {
            wasNotCalled = false
            ""
        }
        callbacks("")

        assert(wasNotCalled)
    }

    @Test
    fun `doesn't runs when lifecycle is destroyed`() {
        var wasNotCalled = true

        callbacks.add(TestLifecycle(DESTROYED)) {
            wasNotCalled = false
            ""
        }
        callbacks("")

        assert(wasNotCalled)
    }

    @Test
    fun `doesn't runs after lifecycle is destroyed`() {
        var timesCalled = 0
        val lifecycle = TestLifecycle(STARTED)

        callbacks.add(lifecycle) {
            timesCalled++
            ""
        }
        callbacks("")
        lifecycle.state = DESTROYED
        lifecycle.state = STARTED
        callbacks("")

        assertEquals(1, timesCalled)
    }

    @Test
    fun `runs as many times as called`() {
        var timesCalled = 0

        callbacks.add(TestLifecycle(STARTED)) {
            timesCalled++
            ""
        }
        callbacks("")
        callbacks("")

        assertEquals(2, timesCalled)
    }

    @Test
    fun `whenAllBeDestroyed should be called when lifecycle is destroyed`() {
        var wasCalled = false
        val lifecycle = TestLifecycle(STARTED)
        LiveCallbackContainer<String, String> {
            wasCalled = true
        }.add(lifecycle) { it }

        lifecycle.state = DESTROYED

        assert(wasCalled)
    }

    @Test
    fun `whenAllBeDestroyed shouldn't be called when lifecycle is not destroyed`() {
        var wasNotCalled = true
        val lifecycle = TestLifecycle(STARTED)
        LiveCallbackContainer<String, String> {
            wasNotCalled = false
        }.add(lifecycle) { it }

        lifecycle.state = CREATED

        assert(wasNotCalled)
    }

    @Test
    fun `whenAllBeDestroyed should be called when all lifecycles are destroyed`() {
        var wasCalled = false
        val lifecycle1 = TestLifecycle(STARTED)
        val lifecycle2 = TestLifecycle(STARTED)
        LiveCallbackContainer<String, String> {
            wasCalled = true
        }.apply {
            add(lifecycle1) { it }
            add(lifecycle2) { it }
        }

        lifecycle1.state = DESTROYED
        lifecycle2.state = DESTROYED

        assert(wasCalled)
    }

    @Test
    fun `whenAllBeDestroyed shouldn't be called when some lifecycle remain not destroyed`() {
        var wasNotCalled = true
        val lifecycle1 = TestLifecycle(STARTED)
        val lifecycle2 = TestLifecycle(STARTED)
        LiveCallbackContainer<String, String> {
            wasNotCalled = false
        }.apply {
            add(lifecycle1) { it }
            add(lifecycle2) { it }
        }

        lifecycle1.state = DESTROYED
        lifecycle2.state = CREATED

        assert(wasNotCalled)
    }
    //endregion

    //region Input
    @Test
    fun `callback input`() {
        lateinit var receivedValue: String

        callbacks.add(TestLifecycle(STARTED)) {
            receivedValue = it
            ""
        }
        val input = "input"
        callbacks(input)

        assertEquals(input, receivedValue)
    }

    @Test
    fun `callback input with callback registered many times`() {
        var concatenatedValues = ""

        val callback: (String) -> String = {
            concatenatedValues += it
            ""
        }

        callbacks.add(TestLifecycle(STARTED), callback = callback)
        callbacks.add(TestLifecycle(STARTED), callback = callback)

        val input = "input,"
        callbacks(input)

        assertEquals("input,input,", concatenatedValues)
    }

    @Test
    fun `callback input with many separate callbacks`() {
        lateinit var receivedValue1: String
        lateinit var receivedValue2: String

        callbacks.add(TestLifecycle(STARTED)) {
            receivedValue1 = it
            ""
        }
        callbacks.add(TestLifecycle(STARTED)) {
            receivedValue2 = it
            ""
        }

        val input = "input"
        callbacks(input)

        assertEquals(input, receivedValue1)
        assertEquals(input, receivedValue2)
    }
    //endregion

    //region Output
    @Test
    fun `callback output`() {
        val output = "output"

        callbacks.add(TestLifecycle(STARTED)) { output }
        val receivedValues = callbacks("")

        assertEquals(output, receivedValues.single())
    }

    @Test
    fun `callback output after destroyed`() {
        val output = "output"
        val lifecycle = TestLifecycle(STARTED)

        callbacks.add(lifecycle) { output }
        lifecycle.state = DESTROYED
        val receivedValues = callbacks("")

        assert(receivedValues.isEmpty())
    }

    @Test
    fun `callback output with callback registered many times`() {
        val output = "output"
        val callback: (String) -> String = { output }

        callbacks.add(TestLifecycle(STARTED), callback = callback)
        callbacks.add(TestLifecycle(STARTED), callback = callback)
        val receivedValues = callbacks("")

        assertEquals(2, receivedValues.size)
        assert(receivedValues.all { it == output })
    }

    @Test
    fun `callback output with many dynamic callbacks`() {
        val output1 = "output1"
        val output2 = "output2"
        fun callback(output: String): (String) -> String = { output }

        callbacks.add(TestLifecycle(STARTED), callback = callback(output1))
        callbacks.add(TestLifecycle(STARTED), callback = callback(output2))
        val receivedValues = callbacks("")

        assertEquals(2, receivedValues.size)
        assertEquals(output1, receivedValues.firstOrNull())
        assertEquals(output2, receivedValues.lastOrNull())
    }

    @Test
    fun `callback output with many separate callbacks`() {
        val output1 = "output1"
        val output2 = "output2"

        callbacks.add(TestLifecycle(STARTED)) { output1 }
        callbacks.add(TestLifecycle(STARTED)) { output2 }
        val receivedValues = callbacks("")

        assertEquals(2, receivedValues.size)
        assertEquals(output1, receivedValues.firstOrNull())
        assertEquals(output2, receivedValues.lastOrNull())
    }

    @Test
    fun `callback output after some destroyed`() {
        val output1 = "output1"
        val output2 = "output2"
        val lifecycle1 = TestLifecycle(STARTED)
        val lifecycle2 = TestLifecycle(STARTED)

        callbacks.add(lifecycle1) { output1 }
        callbacks.add(lifecycle2) { output2 }
        lifecycle1.state = DESTROYED
        val receivedValues = callbacks("")

        assertEquals(output2, receivedValues.single())
    }
    //endregion
}