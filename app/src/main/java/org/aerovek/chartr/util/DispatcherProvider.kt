package org.aerovek.chartr.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Best practice is to always inject dispatchers instead of using them directly in your classes,
 * makes for easier unit testing, so by adding this interface we can inject dispatchers as a dependency
 * in classes where we need to run a coroutine.
 * https://medium.com/androiddevelopers/testing-two-consecutive-livedata-emissions-in-coroutines-5680b693cbf8
 * */
interface DispatcherProvider {
    /**
     * The default CoroutineDispatcher that is used by all standard builders like
     * launch, async, etc if no dispatcher nor any other ContinuationInterceptor
     * is specified in their context.
     * See [Dispatchers.Default]
     * */
    val Default: CoroutineDispatcher

    /**
     * The CoroutineDispatcher that is designed for offloading blocking IO tasks to a shared pool of threads.
     * See [Dispatchers.IO]
     */
    val IO: CoroutineDispatcher

    /**
     * A coroutine dispatcher that is confined to the Main thread operating with UI objects.
     * See [Dispatchers.Main]
     */
    val Main: CoroutineDispatcher

    /**
     * A coroutine dispatcher that is not confined to any specific thread.
     * See [Dispatchers.Unconfined]
     */
    val Unconfined: CoroutineDispatcher
}