package wasabi.layout

import wasabi.WasabiContainer
import wasabi.WasabiContent

class WasabiBorderLayout<M>(override val master: WasabiContainer<M>, override val index: M) : WasabiLayout<WasabiBorderLayout.Position, M>() {
    enum class Position {
        CENTER,
        TOP,
        BOTTOM,
        LEFT,
        RIGHT
    }

    override val contents = mutableMapOf<Position, WasabiContent<Position>>()
    override val mainIndex = Position.CENTER

    override fun getWidth(index: Position): Int {
        return when(index) {
            Position.CENTER -> width - getWidth(Position.LEFT) - getWidth(Position.RIGHT)
            Position.TOP, Position.BOTTOM -> width
            Position.LEFT, Position.RIGHT -> contents[index]?.absoluteWidth ?: 0
        }
    }
    override fun getHeight(index: Position): Int {
        return when(index) {
            Position.CENTER, Position.LEFT, Position.RIGHT -> height - getHeight(Position.TOP) - getHeight(Position.BOTTOM)
            Position.TOP, Position.BOTTOM -> contents[index]?.absoluteHeight ?: 0
        }
    }

    override fun getX(index: Position): Int {
        return when (index) {
            Position.CENTER -> getWidth(Position.LEFT)
            Position.TOP, Position.BOTTOM, Position.LEFT -> 0
            Position.RIGHT -> width - getWidth(Position.RIGHT)
        }
    }
    override fun getY(index: Position): Int {
        return when(index) {
            Position.CENTER, Position.LEFT, Position.RIGHT -> getHeight(Position.TOP)
            Position.TOP -> 0
            Position.BOTTOM -> height - getHeight(Position.BOTTOM)
        }
    }

    init {
        master.add(index, this)

        this.onAdded()
    }
}