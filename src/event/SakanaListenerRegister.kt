package event

import kotlin.reflect.KClass

class SakanaListenerRegister<S : SakanaEvent<*>> {
    private val listenerGroups = mutableMapOf<KClass<out S>, SakanaListenerGroup<out S>>()

    @Suppress("UNCHECKED_CAST")
    fun <E : S> hold(event: E) {
        for ((listenerClass, listenerGroup) in listenerGroups) {
            if (listenerClass == event::class) {
                (listenerGroup as SakanaListenerGroup<E>).hold(event)
                return
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <E : S> add(listener: SakanaListener<E, *>): SakanaListener<E, *> {
        listenerGroups.putIfAbsent(listener.eventClass, SakanaListenerGroup(listener.eventClass))

        (listenerGroups[listener.eventClass]!! as SakanaListenerGroup<E>).add(listener)

        return listener
    }
}