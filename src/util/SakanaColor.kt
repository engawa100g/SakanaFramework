package util

import kotlin.properties.Delegates

class SakanaColor(var R: Int, var G: Int, var B: Int) {
    constructor(RGB: Int) : this(RGB shr 16 and 0xFF, RGB shr 8 and 0xFF, RGB and 0xFF)
    constructor(hex: String) : this(Integer.decode(hex))
}