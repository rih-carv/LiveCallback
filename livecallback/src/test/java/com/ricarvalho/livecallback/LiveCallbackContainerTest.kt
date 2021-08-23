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

        callbacks.register(TestLifecycle(STARTED)) {
            wasCalled = true
            ""
        }
        callbacks.invoke("")

        assert(wasCalled)
    }

    @Test
    fun `runs while stopped`() {
        var wasCalled = false

        callbacks.register(TestLifecycle(CREATED), true) {
            wasCalled = true
            ""
        }
        callbacks.invoke("")

        assert(wasCalled)
    }

    @Test
    fun `doesn't runs while stopped`() {
        var wasNotCalled = true

        callbacks.register(TestLifecycle(CREATED)) {
            wasNotCalled = false
            ""
        }
        callbacks.invoke("")

        assert(wasNotCalled)
    }

    @Test
    fun `doesn't runs when lifecycle is destroyed`() {
        var wasNotCalled = true

        callbacks.register(TestLifecycle(DESTROYED)) {
            wasNotCalled = false
            ""
        }
        callbacks.invoke("")

        assert(wasNotCalled)
    }

    @Test
    fun `doesn't runs after lifecycle is destroyed`() {
        var timesCalled = 0
        val lifecycle = TestLifecycle(STARTED)

        callbacks.register(lifecycle) {
            timesCalled++
            ""
        }
        callbacks.invoke("")
        lifecycle.state = DESTROYED
        lifecycle.state = STARTED
        callbacks.invoke("")

        assertEquals(1, timesCalled)
    }

    @Test
    fun `runs as many times as called`() {
        var timesCalled = 0

        callbacks.register(TestLifecycle(STARTED)) {
            timesCalled++
            ""
        }
        callbacks.invoke("")
        callbacks.invoke("")

        assertEquals(2, timesCalled)
    }

    @Test
    fun `whenAllBeDestroyed should be called when lifecycle is destroyed`() {
        var wasCalled = false
        val lifecycle = TestLifecycle(STARTED)
        LiveCallbackContainer<String, String> {
            wasCalled = true
        }.register(lifecycle) { it }

        lifecycle.state = DESTROYED

        assert(wasCalled)
    }

    @Test
    fun `whenAllBeDestroyed shouldn't be called when lifecycle is not destroyed`() {
        var wasNotCalled = true
        val lifecycle = TestLifecycle(STARTED)
        LiveCallbackContainer<String, String> {
            wasNotCalled = false
        }.register(lifecycle) { it }

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
            register(lifecycle1) { it }
            register(lifecycle2) { it }
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
            register(lifecycle1) { it }
            register(lifecycle2) { it }
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

        callbacks.register(TestLifecycle(STARTED)) {
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

        callbacks.register(TestLifecycle(STARTED), callback = callback)
        callbacks.register(TestLifecycle(STARTED), callback = callback)

        val input = "input,"
        callbacks.invoke(input)

        assertEquals("input,input,", concatenatedValues)
    }

    @Test
    fun `callback input with many separate callbacks`() {
        lateinit var receivedValue1: String
        lateinit var receivedValue2: String

        callbacks.register(TestLifecycle(STARTED)) {
            receivedValue1 = it
            ""
        }
        callbacks.register(TestLifecycle(STARTED)) {
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

        callbacks.register(TestLifecycle(STARTED)) { output }
        val receivedValues = callbacks.invoke("")

        assertEquals(output, receivedValues.single())
    }

    @Test
    fun `callback output after destroyed`() {
        val output = "output"
        val lifecycle = TestLifecycle(STARTED)

        callbacks.register(lifecycle) { output }
        lifecycle.state = DESTROYED
        val receivedValues = callbacks.invoke("")

        assert(receivedValues.isEmpty())
    }

    @Test
    fun `callback output with callback registered many times`() {
        val output = "output"
        val callback: (String) -> String = { output }

        callbacks.register(TestLifecycle(STARTED), callback = callback)
        callbacks.register(TestLifecycle(STARTED), callback = callback)
        val receivedValues = callbacks.invoke("")

        assertEquals(2, receivedValues.size)
        assert(receivedValues.all { it == output })
    }

    @Test
    fun `callback output with many dynamic callbacks`() {
        val output1 = "output1"
        val output2 = "output2"
        fun callback(output: String): (String) -> String = { output }

        callbacks.register(TestLifecycle(STARTED), callback = callback(output1))
        callbacks.register(TestLifecycle(STARTED), callback = callback(output2))
        val receivedValues = callbacks.invoke("")

        assertEquals(2, receivedValues.size)
        assertEquals(output1, receivedValues.firstOrNull())
        assertEquals(output2, receivedValues.lastOrNull())
    }

    @Test
    fun `callback output with many separate callbacks`() {
        val output1 = "output1"
        val output2 = "output2"

        callbacks.register(TestLifecycle(STARTED)) { output1 }
        callbacks.register(TestLifecycle(STARTED)) { output2 }
        val receivedValues = callbacks.invoke("")

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

        callbacks.register(lifecycle1) { output1 }
        callbacks.register(lifecycle2) { output2 }
        lifecycle1.state = DESTROYED
        val receivedValues = callbacks.invoke("")

        assertEquals(output2, receivedValues.single())
    }
    //endregion
}