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