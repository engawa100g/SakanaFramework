package wasabi.event

import wasabi.WasabiComponent
import kotlin.properties.Delegates

class WasabiMouse(component: WasabiComponent, private val autoRefreshable: Boolean = true) {
    enum class Button {
        LEFT,
        MIDDLE,
        RIGHT
    }
    abstract class MouseEvent<C>(condition: C, val absoluteX: Int, val absoluteY: Int, val screenX: Int, val screenY: Int) : WasabiGUIEvent.WasabiWaveEvent<C>(condition) {
        var mouseX by Delegates.notNull<Int>()
        var mouseY by Delegates.notNull<Int>()

        override fun init(component: WasabiComponent) {
            mouseX = absoluteX - component.absoluteX
            mouseY = absoluteY - component.absoluteY
        }
    }
    class MotionEvent(condition: Condition, absoluteX: Int, absoluteY: Int, screenX: Int, screenY: Int) : MouseEvent<MotionEvent.Condition>(condition, absoluteX, absoluteY, screenX, screenY) {
        enum class Condition {
            MOVED,
            DRAGGED
        }

        val isMoved = condition == Condition.MOVED
        val isDragged = condition == Condition.DRAGGED
    }
    class PressEvent(condition: Button, absoluteX: Int, absoluteY: Int, screenX: Int, screenY: Int) : MouseEvent<Button>(condition, absoluteX, absoluteY, screenX, screenY) {
        val button = condition

        var isLeftButton   = button == Button.LEFT
        var isMiddleButton = button == Button.MIDDLE
        var isRightButton  = button == Button.RIGHT
    }
    class ReleaseEvent(condition: Button, absoluteX: Int, absoluteY: Int, screenX: Int, screenY: Int) : MouseEvent<Button>(condition, absoluteX, absoluteY, screenX, screenY) {
        val button = condition
    }

    var mouseX by Delegates.notNull<Int>()
    var mouseY by Delegates.notNull<Int>()

    var isLeftClicked = false
    var isMiddleClicked = false
    var isRightClicked = false

    init {
        component.on(MotionEvent::class) {
            mouseX = it.mouseX
            mouseY = it.mouseY
        }
        component.on(PressEvent::class) {
            when (it.button) {
                Button.LEFT   -> isLeftClicked   = true
                Button.MIDDLE -> isMiddleClicked = true
                Button.RIGHT  -> isRightClicked  = true
            }
        }
        if (autoRefreshable) {
            component.on(ReleaseEvent::class) {
                when (it.button) {
                    Button.LEFT   -> isLeftClicked   = false
                    Button.MIDDLE -> isMiddleClicked = false
                    Button.RIGHT  -> isRightClicked  = false
                }
            }
        }
    }

    fun refresh() {
        isLeftClicked   = false
        isMiddleClicked = false
        isRightClicked  = false
    }
}