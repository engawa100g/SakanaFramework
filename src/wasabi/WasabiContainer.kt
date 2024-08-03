package wasabi

import wasabi.event.WasabiGUIEvent

interface WasabiContainer<K> : WasabiComponent {
    val contents: MutableMap<K, WasabiContent<K>>
    val mainIndex: K
    val main
        get() = contents[mainIndex]

    fun <C : WasabiContent<K>> add(index: K, content: C): C {
        contents[index] = content
        return content
    }

    fun getContent(x: Int, y: Int): WasabiContent<K>? {
        for (content in contents.values) {
            if (content.x <= x && x < content.x + content.width && content.y <= y && y < content.y + content.height) {
                return content
            }
        }
        return null
    }

    fun getWidth(index: K): Int
    fun getHeight(index: K): Int

    fun getX(index: K): Int
    fun getY(index: K): Int

    override fun hold(event: WasabiGUIEvent.WasabiBroadcastEvent<*>) {
        hold(event as WasabiGUIEvent<*>)

        for (content in contents.values) {
            content.hold(event)
        }
    }
}