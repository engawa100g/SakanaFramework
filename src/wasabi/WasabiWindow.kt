package wasabi

import event.SakanaListenerRegister
import wasabi.event.WasabiGUIEvent
import wasabi.event.WasabiMouse
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.event.*
import javax.swing.JFrame
import javax.swing.JPanel

open class WasabiWindow : WasabiContainer<WasabiWindow.Position> {
    enum class Position {
        CENTER
    }
    class ClosedEvent : WasabiGUIEvent.WasabiBasicEvent<Unit?>(null)
    class ActivatedEvent : WasabiGUIEvent.WasabiBasicEvent<Unit?>(null)
    class DeactivatedEvent : WasabiGUIEvent.WasabiBasicEvent<Unit?>(null)
    class DrawEvent(private val screenGraphics: Graphics2D) : WasabiGUIEvent.WasabiBroadcastEvent<Unit?>(null) {
        lateinit var graphics: Graphics2D

        override fun init(component: WasabiComponent) {
            graphics = screenGraphics.create(component.absoluteX, component.absoluteY, component.width, component.height) as Graphics2D
        }
    }

    private val jFrame = JFrame()
    private val jPanel = jFrame.add(object : JPanel() {
        override fun paintComponent(g: Graphics) {
            val g2 = g as Graphics2D
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
            hold(DrawEvent(g2))
        }
    })

    init {
        jPanel.addMouseMotionListener(object : MouseMotionListener {
            override fun mouseMoved(e: MouseEvent) {
                hold(WasabiMouse.MotionEvent(WasabiMouse.MotionEvent.Condition.MOVED, e.x, e.y, e.xOnScreen, e.yOnScreen))
            }
            override fun mouseDragged(e: MouseEvent) {
                hold(WasabiMouse.MotionEvent(WasabiMouse.MotionEvent.Condition.DRAGGED, e.x, e.y, e.xOnScreen, e.yOnScreen))
            }
        })
        jPanel.addMouseListener(object : MouseListener {
            override fun mouseClicked(e: MouseEvent) {

            }
            override fun mousePressed(e: MouseEvent) {
                focusUpdate(e.x, e.y)

                hold(WasabiMouse.PressEvent(when (e.button) {
                    MouseEvent.BUTTON1 -> WasabiMouse.Button.LEFT
                    MouseEvent.BUTTON2 -> WasabiMouse.Button.MIDDLE
                    MouseEvent.BUTTON3 -> WasabiMouse.Button.RIGHT
                    else -> error("This button is unknown. (${e.button})")
                }, e.x, e.y, e.xOnScreen, e.yOnScreen))
            }
            override fun mouseReleased(e: MouseEvent) {
                hold(WasabiMouse.ReleaseEvent(when (e.button) {
                    MouseEvent.BUTTON1 -> WasabiMouse.Button.LEFT
                    MouseEvent.BUTTON2 -> WasabiMouse.Button.MIDDLE
                    MouseEvent.BUTTON3 -> WasabiMouse.Button.RIGHT
                    else -> error("This button is unknown. (${e.button})")
                }, e.x, e.y, e.xOnScreen, e.yOnScreen))
            }
            override fun mouseEntered(e: MouseEvent?) {

            }
            override fun mouseExited(e: MouseEvent?) {

            }
        })
        jFrame.addWindowListener(object : WindowListener {
            override fun windowOpened(e: WindowEvent) {

            }
            override fun windowClosing(e: WindowEvent) {
                hold(ClosedEvent())
            }
            override fun windowClosed(e: WindowEvent) {

            }
            override fun windowIconified(e: WindowEvent) {

            }
            override fun windowDeiconified(e: WindowEvent?) {

            }
            override fun windowActivated(e: WindowEvent) {
                hold(ActivatedEvent())
            }
            override fun windowDeactivated(e: WindowEvent) {
                focusedComponents.clear()

                hold(DeactivatedEvent())
            }
        })
    }

    var title: String
        get() = jFrame.title
        set(title) {
            jFrame.title = title
        }

    fun resize(width: Int? = null, height: Int? = null) {
        jPanel.preferredSize = Dimension(width ?: this.width, height ?: this.height)
        jFrame.pack()
    }

    var isVisible: Boolean
        get() = jFrame.isVisible
        set(visible) {
            if (visible) {
                val before = focusedComponents.toList()

                /*
                focusedComponents.clear()
                var component: WasabiContainer<*> = this
                while (true) {
                    focusedComponents.add(component)
                    val content = component.main
                    if (content == null) {
                        break
                    } else {
                        if (content is WasabiContainer<*>) {
                            component = content
                        } else {
                            focusedComponents.add(content)
                            break
                        }
                    }
                }

                (before - focusedComponents).forEach {
                    it.isFocused = false
                }
                (focusedComponents - before).forEach {
                    it.isFocused = true
                }
                 */
            }

            jFrame.isVisible = visible
        }
    fun show() {
        isVisible = true
    }
    fun hide() {
        isVisible = false
    }

    override val contents = mutableMapOf<Position, WasabiContent<Position>>()
    override val mainIndex: Position = Position.CENTER

    override var width: Int
        get() = jPanel.width
        set(width) = resize(width = width)

    override var height: Int
        get() = jPanel.height
        set(height) = resize(height = height)

    override var absoluteWidth: Int = 0
    override var absoluteHeight: Int = 0

    override val x: Int = 0
    override val y: Int = 0

    override val absoluteX: Int
        get() = x
    override val absoluteY: Int
        get() = y


    override fun getWidth(index: Position): Int {
        return width
    }
    override fun getHeight(index: Position): Int {
        return height
    }

    override fun getX(index: Position): Int {
        return 0
    }
    override fun getY(index: Position): Int {
        return 0
    }

    override var listenerRegister = SakanaListenerRegister<WasabiGUIEvent<*>>()

    val focusedComponents = mutableListOf<WasabiComponent>()

    fun focusUpdate(x: Int, y: Int) {
        var deltaX = x
        var deltaY = y

        val before = focusedComponents.toList()

        focusedComponents.clear()
        var component: WasabiContainer<*> = this
        while (true) {
            focusedComponents.add(component)
            val content = component.getContent(x, y)
            if (content == null) {
                break
            } else {
                if (content is WasabiContainer<*>) {
                    component = content

                    deltaX -= content.x
                    deltaY -= content.y
                } else {
                    focusedComponents.add(content)
                    break
                }
            }
        }

        (before - focusedComponents.toSet()).forEach {
            it.isFocused = false
        }
        (focusedComponents - before.toSet()).forEach {
            it.isFocused = true
        }
    }

    fun hold(event: WasabiGUIEvent.WasabiWaveEvent<*>) {
        focusedComponents.reversed().forEach { it.hold(event) }
    }

    fun hold(event: WasabiMouse.MotionEvent) {
        focusedComponents.reversed().forEach {
            if (event.isDragged) {
                it.hold(event)
            } else if (
                it.absoluteX <= event.absoluteX && event.absoluteX < it.absoluteX + it.width &&
                it.absoluteY <= event.absoluteY && event.absoluteY < it.absoluteY + it.height
            ) {
                it.hold(event)
            }
        }
    }

    open fun onLaunch() {

    }

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

    init {
        this.onLaunch()
    }
}