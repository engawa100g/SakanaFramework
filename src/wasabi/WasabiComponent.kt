package wasabi

import event.SakanaListener
import event.SakanaListenerRegister
import wasabi.event.WasabiGUIEvent
import kotlin.reflect.KClass

interface WasabiComponent {
    class FocusedEvent : WasabiGUIEvent.WasabiBasicEvent<Unit?>(null)
    class UnfocusedEvent : WasabiGUIEvent.WasabiBasicEvent<Unit?>(null)

    var width: Int
    var height: Int

    var absoluteWidth: Int
    var absoluteHeight: Int

    val x: Int
    val y: Int

    val absoluteX: Int
    val absoluteY: Int

    var listenerRegister: SakanaListenerRegister<WasabiGUIEvent<*>>

    fun <E : WasabiGUIEvent<*>> hold(event: E) {
        event.init(this)

        listenerRegister.hold(event)
    }
    fun <E : WasabiGUIEvent<C>, C> on(eventClass: KClass<E>, vararg filters: C, action: ((event: E) -> Unit)? = null) {
        listenerRegister.add(SakanaListener(eventClass, *filters, action = action))
    }

    fun hold(event: WasabiGUIEvent.WasabiBroadcastEvent<*>)

    var isFocused: Boolean
}