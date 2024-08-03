package event

import kotlin.reflect.KClass

class SakanaListenerGroup<E : SakanaEvent<*>>(val eventClass: KClass<E>) {
    private val listeners = mutableListOf<SakanaListener<E, *>>()

    fun hold(event: E) {
        listeners.forEach {
            it.hold(event)
        }
    }

    fun add(listener: SakanaListener<E, *>): SakanaListener<E, *> {
        listeners.add(listener)

        return listener
    }
}