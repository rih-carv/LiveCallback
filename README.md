# LiveCallback
LiveCallback is a library project that aims to make dealing with async callbacks on Android safer
and straightforward.

## Benefits:
It provides Lifecycle-Aware components that:
- **Prevents memory leaks**: by releasing the callbacks when lifecycle owners are destroyed.
- **Allows to prevent unwanted executions**: running only while lifecycle owners are into active
  states, ignoring invocations by default before owners enter into `STARTED` state and while
  owners are into `STOPPED` states, running only while it is `STARTED` or `RESUMED`. But allowing to
  be executed into any state until it is `DESTROYED` as well, if indicated to do so.
- **Are easy to use**: having a very simple interface.
- **Are very flexible**: allowing to combine many callbacks as desired.
- **Type-safe**: by using generics to enforce callback compatibilities.
- **Allows to provide different callbacks for the same function**: by retrieving the expected
  callback using a token system to differentiate each invocation context.
- **Allows to receive callbacks after `Activity`/`Fragment` recreations**: by matching the callbacks
  based on those callbacks creation context.

## Drawbacks:
- Not as straightforward as directly passing lambdas as function arguments.
- Limited callback params capability, allowing at most one parameter by callback. Forcing to use
  collections, `Pair`s, `Triple`s, or to declare new classes to aggregate many parameters by passing
  single "param objects" instead of passing multiple params, in order to overcome this limitation on
  more complex use cases.
- By now it doesn't enforces main thread execution, neither handles in which threads the callbacks
  are invoked/executed, with the callback execution happening at the same thread it is invoked.
- By now it doesn't supports signaling for tasks cancellation if there are no more active or
  available callbacks.

# Usage
## On the provider side
Create a `TokenizedLiveCallbackRegistry` instance and expose it publicly as a `LiveCallbackRegistry`
for others to be able to register callbacks, and invoke those externally registered active callbacks
with the `CallbackToken`s received on the registration moment when needed.

```kotlin
// On a long lived component
object Repository {
  // Create a `TokenizedLiveCallbackRegistry` to be able to register and invoke registered callbacks
  private val callbacks = TokenizedLiveCallbackRegistry<String, Int>()
  // Expose it as a `LiveCallbackRegistry`, for others to be able to register callbacks, but don't
  // be able to invoke them from outside
  val registry = callbacks as LiveCallbackRegistry<String, Int>

  // Receive a token as parameter to invoke the corresponding registered callbacks
  fun performLongRunningOperation(
    token: CallbackToken<String, Int>,
    message: String = "callback param"
  ) {
    // Perform a long running operation on another thread
    // ...
    // Execute the appropriate callbacks (identified by the received token) when task completes and
    // result arrives
    val callbacksAnswers: List<Int> = callbacks(token, "Hello, $message!")
  }
}
```

## On the consumer side
Create fields to store the registered `CallbacksToken`s and assign the values returned from the
`LiveCallbackRegistry`s `register` methods when `Lifecycle` become available. Then trigger the
operation when appropriate, passing in the token of the desired callback to be invoked as result.

```kotlin
// On a short lived component
class FragileFragment : Fragment() {
  // Fields to store callback tokens
  private var lengthCallback: CallbackToken<String, Int>? = null
  private var hashCodeCallback: CallbackToken<String, Int>? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // Register callbacks on `LiveCallbackRegistry`
    lengthCallback = Repository.registry.register(lifecycle, runWhileStopped = true) { result ->
      result.length
    }
    hashCodeCallback = Repository.registry.register(lifecycle, runWhileStopped = true) { result ->
      result.hashCode()
    }
  }

  // Trigger operations when appropriate, with the desired callbacks identified by their respective
  // tokens (they may be executed in another instance if this `Fragment` is recreated)
  private fun onButtonClick() {
    lengthCallback?.let { Repository.performLongRunningOperation(it) }
  }

  private fun onSomeEvent() {
    hashCodeCallback?.let { Repository.performLongRunningOperation(it, "custom param") }
  }
}
```

### Known pitfalls
- Avoid registering callbacks inside lazy property initialization, as doing so would result in
  missing callbacks due to them not being registered until the lazy variable is used somewhere else.
- Try to register callbacks as soon as possible, even if the callback may be not invoked into this
  instance, as it may receive the result of a previous instance that fired the event and has been
  destroyed and is being recreated.
- Callbacks that depend on instance values captured from the surrounding context may differ after
  the component is recreated.

# Callback types
- __Full callback__: for use cases where callbacks receive a parameter and return a value
  - Register-only Registry interface: `LiveCallbackRegistry<Input, Output>`
  - Invocable Registry: `TokenizedLiveCallbackRegistry<Input, Output>`
  - CallbackToken: `CallbackToken<Input, Output>`
  - Callback: `(Input) -> Output`
- __Input only callback__: for use cases where callbacks receive a parameter and return no value
  - Register-only Registry interface: `InputLiveCallbackRegistry<Input>`
  - Invocable Registry: `TokenizedInputLiveCallbackRegistry<Input>`
  - CallbackToken: `InputCallbackToken<Input>`
  - Callback: `(Input) -> Unit`
- __Output only callback__: for use cases where callbacks receive no parameters and return a value
  - Register-only Registry interface: `OutputLiveCallbackRegistry<Output>`
  - Invocable Registry: `TokenizedOutputLiveCallbackRegistry<Output>`
  - CallbackToken: `OutputCallbackToken<Output>`
  - Callback: `() -> Output`
- __Simple callback__: for use cases where callbacks receive no parameters and return no value
  - Register-only Registry interface: `SimpleLiveCallbackRegistry`
  - Invocable Registry: `TokenizedSimpleLiveCallbackRegistry`
  - CallbackToken: `SimpleCallbackToken`
  - Callback: `() -> Unit`