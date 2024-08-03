package wasabi.event

import event.SakanaEvent
import wasabi.WasabiComponent

sealed class WasabiGUIEvent<C>(condition: C) : SakanaEvent<C>(condition) {
    abstract class WasabiBroadcastEvent<C>(condition: C) : WasabiGUIEvent<C>(condition)
    abstract class WasabiWaveEvent<C>(condition: C) : WasabiGUIEvent<C>(condition)
    abstract class WasabiBasicEvent<C>(condition: C) : WasabiGUIEvent<C>(condition)

    open fun init(component: WasabiComponent) {

    }
}