package wasabi.waml

import util.SakanaColor

class WamlStyle {
    sealed class Style<V>(value: V) {
        class Display(type: DisplayType) : Style<Display.DisplayType>(type) {
            enum class DisplayType {
                DISPLAY,
                INLINE,
                FLEX,
                NONE
            }
        }

        class BackgroundColor(color: SakanaColor) : Style<SakanaColor>(color)

        class Width(width: Double) : Style<Double>(width) {
            constructor(width: Int) : this(width.toDouble())
        }
        class Height(height: Double) : Style<Double>(height) {
            constructor(height: Int) : this(height.toDouble())
        }

        class Padding(padding: Double) : Style<Double>(padding) {
            constructor(padding: Int) : this(padding.toDouble())
        }
        class PaddingX(padding: Double) : Style<Double>(padding) {
            constructor(padding: Int) : this(padding.toDouble())
        }
        class PaddingY(padding: Double) : Style<Double>(padding) {
            constructor(padding: Int) : this(padding.toDouble())
        }
        class PaddingTop(padding: Double) : Style<Double>(padding) {
            constructor(padding: Int) : this(padding.toDouble())
        }
        class PaddingBottom(padding: Double) : Style<Double>(padding) {
            constructor(padding: Int) : this(padding.toDouble())
        }
        class PaddingLeft(padding: Double) : Style<Double>(padding) {
            constructor(padding: Int) : this(padding.toDouble())
        }
        class PaddingRight(padding: Double) : Style<Double>(padding) {
            constructor(padding: Int) : this(padding.toDouble())
        }

        class Margin(margin: Double) : Style<Double>(margin) {
            constructor(margin: Int) : this(margin.toDouble())
        }
        class MarginX(margin: Double) : Style<Double>(margin) {
            constructor(margin: Int) : this(margin.toDouble())
        }
        class MarginY(margin: Double) : Style<Double>(margin) {
            constructor(margin: Int) : this(margin.toDouble())
        }
        class MarginTop(margin: Double) : Style<Double>(margin) {
            constructor(margin: Int) : this(margin.toDouble())
        }
        class MarginBottom(margin: Double) : Style<Double>(margin) {
            constructor(margin: Int) : this(margin.toDouble())
        }
        class MarginLeft(margin: Double) : Style<Double>(margin) {
            constructor(margin: Int) : this(margin.toDouble())
        }
        class MarginRight(margin: Double) : Style<Double>(margin) {
            constructor(margin: Int) : this(margin.toDouble())
        }
    }
}