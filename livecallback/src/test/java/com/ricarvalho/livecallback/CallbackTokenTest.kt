package com.ricarvalho.livecallback

import org.junit.Assert.*
import org.junit.Test

class CallbackTokenTest {
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
}