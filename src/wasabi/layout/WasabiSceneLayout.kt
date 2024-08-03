package wasabi.layout

import wasabi.WasabiContainer
import wasabi.WasabiContent

class WasabiSceneLayout<M>(override val master: WasabiContainer<M>, override val index: M) : WasabiLayout<String, M>() {
    companion object {
        const val DEFAULT = "DEFAULT"
    }

    override val contents = mutableMapOf<String, WasabiContent<String>>()
    override val mainIndex = DEFAULT

    var nowScene = DEFAULT

    override fun getWidth(index: String): Int {
        return width
    }
    override fun getHeight(index: String): Int {
        return height
    }

    override fun getY(index: String): Int {
        return 0
    }
    override fun getX(index: String): Int {
        return 0
    }

    init {
        master.add(index, this)

        this.onAdded()
    }
}