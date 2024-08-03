package wasabi.waml

open class WamlElement(val name: String) {
    val attributes = mutableMapOf<String, String>()
    val contents = mutableListOf<WamlElement>()
    val styles = mutableListOf<WamlStyle>()

    fun addAttribute(name: String, value: String) {
        attributes[name] = value
    }
}