package wasabi.layout

import event.SakanaListenerRegister
import wasabi.WasabiComponent
import wasabi.WasabiContainer
import wasabi.WasabiContent
import wasabi.event.WasabiGUIEvent

abstract class WasabiLayout<K, M> : WasabiContainer<K>, WasabiContent<M> {
    override var width: Int
        get() = master.getWidth(index)
        set(width) {
            absoluteWidth = width
        }
    override var height: Int
        get() = master.getHeight(index)
        set(height) {
            absoluteHeight = height
        }

    override var absoluteWidth: Int = 0
    override var absoluteHeight: Int = 0

    override val x: Int
        get() = master.getX(index)
    override val y: Int
        get() = master.getY(index)

    override val absoluteX: Int
        get() = x + master.absoluteX
    override val absoluteY: Int
        get() = y + master.absoluteY

    override var listenerRegister = SakanaListenerRegister<WasabiGUIEvent<*>>()

    override var isFocused: Boolean = false
        set(focused) {
            if (field != focused) {
                if (focused) {
                    hold(WasabiComponent.FocusedEvent())
                } else {
                    hold(WasabiComponent.UnfocusedEvent())
                }
            }

            field = focused
        }
}