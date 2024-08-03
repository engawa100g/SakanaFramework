package event

import kotlinx.coroutines.*
import kotlin.coroutines.resume
import kotlin.reflect.KClass

class SakanaListener<E : SakanaEvent<C>, C>(val eventClass: KClass<E>, private vararg val filters: C, private val action: ((event: E) -> Unit)? = null, private val once: Boolean = false) {
    var held = false

    fun hold(event: E) {
        if (!once || !held) {
            if (event.condition == null || filters.isEmpty() || filters.contains(event.condition)) {
                held = true
                action?.invoke(event)
                awaitAction?.invoke(event)
            }
        }
    }

    private var awaitAction: ((event: E) -> Unit)? = null

    fun await(): E {
        return runBlocking {
            suspendCancellableCoroutine { continuation ->
                awaitAction = {
                    continuation.resume(it)
                }
            }
        }
    }
}