package com.ricarvalho.livecallback.postponable

class Postponable<T, I>(
    private val target: T,
    private val canExecuteNow: (target: T, input: I) -> Boolean
) { // TODO: Kdoc, readme, tests
    private val postponedExecutions = mutableListOf<Postponed<T, I, *>>()

    fun <R> executeImmediately(action: (target: T) -> R) = action(target)

    operator fun <R> invoke(input: I, action: (target: T) -> R) = invoke(input, action) { _, _ -> }

    operator fun <R> invoke(
        input: I,
        action: (target: T) -> R,
        whenDone: (target: T, result: R) -> Unit
    ) {
        postponedExecutions += Postponed(action, whenDone, input)
        performPossiblePostponedActions()
    }

    fun <R> relay(action: (target: T) -> R) =
        action(target).also { performPossiblePostponedActions() }

    private fun performPossiblePostponedActions() = with(postponedExecutions) {
        filter(::canExecute).onEach(::perform).also(::removeAll)
    }

    private fun canExecute(postponed: Postponed<T, I, *>) = canExecuteNow(target, postponed.input)

    private fun perform(postponed: Postponed<T, I, *>) = postponed.perform(target)

    private data class Postponed<T, I, R>(
        val action: (target: T) -> R,
        val whenDone: (target: T, result: R) -> Unit,
        val input: I
    ) {
        fun perform(target: T) {
            val result = action(target)
            whenDone(target, result)
        }
    }
}