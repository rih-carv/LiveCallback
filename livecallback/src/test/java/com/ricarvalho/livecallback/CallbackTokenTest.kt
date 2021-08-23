package com.ricarvalho.livecallback

import org.junit.Assert.*
import org.junit.Test

class CallbackTokenTest {
    //region CallbackToken
    @Test
    fun `tokens of different callbacks shouldn't be equal`() {
        val token1 = CallbackToken<String, String> { it }
        val token2 = CallbackToken<String, String> { it }

        assertNotEquals(token1, token2)
    }

    @Test
    fun `tokens of the same callback should be equal`() {
        val callback: (String) -> String = { it }
        val token1 = CallbackToken(callback)
        val token2 = CallbackToken(callback)

        assertEquals(token1, token2)
    }

    @Test
    fun `tokens of different callback instances with the same origin should be equal`() {
        val externalValueToForceNewLambdaInstance = ""
        fun callback(): (String) -> String = { externalValueToForceNewLambdaInstance }

        val callback1 = callback()
        val callback2 = callback()
        val token1 = CallbackToken(callback1)
        val token2 = CallbackToken(callback2)

        assertNotSame(callback1, callback2)
        assertEquals(token1, token2)
    }
    //endregion

    //region InputCallbackToken
    @Test
    fun `input tokens of different callbacks shouldn't be equal`() {
        val token1 = CallbackToken.input<String> {}
        val token2 = CallbackToken.input<String> {}

        assertNotEquals(token1, token2)
    }

    @Test
    fun `input tokens of the same callback should be equal`() {
        val callback: (String) -> Unit = {}
        val token1 = CallbackToken.input(callback)
        val token2 = CallbackToken.input(callback)

        assertEquals(token1, token2)
    }

    @Test
    fun `input tokens of different callback instances with the same origin should be equal`() {
        val externalValueToForceNewLambdaInstance = ""
        fun callback(): (String) -> Unit = { externalValueToForceNewLambdaInstance.run {} }

        val callback1 = callback()
        val callback2 = callback()
        val token1 = CallbackToken.input(callback1)
        val token2 = CallbackToken.input(callback2)

        assertNotSame(callback1, callback2)
        assertEquals(token1, token2)
    }
    //endregion

    //region OutputCallbackToken
    @Test
    fun `output tokens of different callbacks shouldn't be equal`() {
        val token1 = CallbackToken.output { "" }
        val token2 = CallbackToken.output { "" }

        assertNotEquals(token1, token2)
    }

    @Test
    fun `output tokens of the same callback should be equal`() {
        val callback: () -> String = { "" }
        val token1 = CallbackToken.output(callback)
        val token2 = CallbackToken.output(callback)

        assertEquals(token1, token2)
    }

    @Test
    fun `output tokens of different callback instances with the same origin should be equal`() {
        val externalValueToForceNewLambdaInstance = ""
        fun callback(): () -> String = { externalValueToForceNewLambdaInstance }

        val callback1 = callback()
        val callback2 = callback()
        val token1 = CallbackToken.output(callback1)
        val token2 = CallbackToken.output(callback2)

        assertNotSame(callback1, callback2)
        assertEquals(token1, token2)
    }
    //endregion
}