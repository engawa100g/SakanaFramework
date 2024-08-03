package wasabi.waml

import java.io.File

class WamlDOM {
    enum class TokenType(val regex: Regex) {
        CLOSED_START_TAG(Regex("</")),
        START_TAG(Regex("<")),
        CLOSED_END_TAG(Regex("/>")),
        END_TAG(Regex(">")),
        EQUAL(Regex("=")),
        WHITESPACE(Regex("\\s+")),
        STRING(Regex("\"[^\"]*\"")),
        KEYWORD(Regex("[^\"\\s/<>=]+"))
    }
    class Token(val tokenType: TokenType, val text: String)
    sealed class WamlTag {
        class StartTag(val name: String, val attributes: Map<String, String>) : WamlTag() {
            override fun toString(): String {
                return "<$name${if (attributes.isEmpty()) "" else " ${attributes.entries.map { "${it.key}=\"${it.value}\"" }.joinToString("")}"}>"
            }
        }
        class EmptyTag(val name: String, val attributes: Map<String, String>) : WamlTag() {
            override fun toString(): String {
                return "<$name${if (attributes.isEmpty()) "" else " ${attributes.entries.map { "${it.key}=\"${it.value}\"" }.joinToString("")}"}/>"
            }
        }
        class EndTag(val name: String) : WamlTag() {
            override fun toString(): String {
                return "</$name>"
            }
        }
        class Text(val text: String) : WamlTag() {
            override fun toString(): String {
                return text
            }
        }
        class Whitespace(val space: String) : WamlTag() {
            override fun toString(): String {
                return space
            }
        }
    }

    private fun tokenize(input: String): List<Token> {
        val tokens = mutableListOf<Token>()

        var index = 0
        main@ while (input.length > index) {
            for (tokenType in TokenType.entries) {
                val result = tokenType.regex.matchAt(input, index)
                if (result != null) {
                    tokens.add(Token(tokenType, result.value))
                    index += result.value.length
                    continue@main
                }
            }

            error("ERROR!")
        }

        return tokens
    }

    private fun toTag(tokens: List<Token>): List<WamlTag> {
        val tags = mutableListOf<WamlTag>()

        var index = 0
        var process = 0
        while (tokens.size > index) {
            when (process) {
                0 -> {
                    when (tokens[index].tokenType) {
                        TokenType.CLOSED_START_TAG -> {
                            process = 2
                        }
                        TokenType.START_TAG -> {
                            process = 1
                        }
                        TokenType.WHITESPACE -> {
                            tags.add(WamlTag.Whitespace(tokens[index].text))
                        }
                        TokenType.STRING, TokenType.KEYWORD -> {
                            tags.add(WamlTag.Text(tokens[index].text))
                        }
                        else -> {
                            println(index)
                            error("Unexpected token: ${tokens[index].text}")
                        }
                    }
                    index++
                }
                else -> {
                    var name: String
                    if (tokens[index].tokenType == TokenType.KEYWORD) {
                        name = tokens[index].text
                    } else {
                        error("Unexpected token: ${tokens[index].text}")
                    }

                    val attributes = mutableMapOf<String, String>()

                    var isEqual = false

                    var index2 = 1

                    var attributeName: String? = null
                    while (tokens.size > index + index2) {
                        when (tokens[index + index2].tokenType) {
                            TokenType.CLOSED_END_TAG -> {
                                if (attributeName == null && !isEqual) {
                                    if (process == 2) {
                                        error("Unexpected token: ${tokens[index + index2].text}")
                                    }
                                    tags.add(WamlTag.EmptyTag(name, attributes))
                                    break
                                } else {
                                    error("Unexpected token: ${tokens[index + index2].text}")
                                }
                            }
                            TokenType.END_TAG -> {
                                if (attributeName == null && !isEqual) {
                                    tags.add(if (process == 1) WamlTag.StartTag(name, attributes) else WamlTag.EndTag(name))
                                    break
                                } else {
                                    error("Unexpected token: ${tokens[index + index2].text}")
                                }
                            }
                            TokenType.EQUAL -> {
                                if (attributeName != null) {
                                    isEqual = true
                                } else {
                                    error("Unexpected token: ${tokens[index + index2].text}")
                                }
                            }
                            TokenType.WHITESPACE -> {

                            }
                            TokenType.STRING -> {
                                if (attributeName != null && isEqual) {
                                    attributes[attributeName] = tokens[index + index2].text.slice(1..(tokens[index + index2].text.length - 2))
                                    attributeName = null
                                    isEqual = false
                                } else {
                                    error("Unexpected token: ${tokens[index + index2].text}")
                                }
                            }
                            TokenType.KEYWORD -> {
                                if (process == 2) {
                                    error("Unexpected token: ${tokens[index + index2].text}")
                                }
                                if (attributeName == null) {
                                    attributeName = tokens[index + index2].text
                                } else {
                                    error("Unexpected token: ${tokens[index + index2].text}")
                                }
                            }
                            else -> {
                                error("Unexpected token: ${tokens[index + index2].text}")
                            }
                        }

                        index2++
                    }

                    index += index2 + 1

                    process = 0
                }
            }
        }

        return tags
    }

    fun elementize(tags: List<WamlTag>): List<WamlElement> {
        var index = 0

        fun main(root: Boolean = false): List<WamlElement> {
            val elements = mutableListOf<WamlElement>()

            var element: WamlElement? = null

            while (tags.size > index) {
                when (val tag = tags[index]) {
                    is WamlTag.StartTag -> {
                        if (element == null) {
                            element = WamlElement(tag.name).apply {
                                tag.attributes.forEach {
                                    addAttribute(it.key, it.value)
                                }
                            }
                        } else {
                            element.contents.addAll(main())
                        }
                    }
                    is WamlTag.EmptyTag -> {
                        if (element == null) {
                            elements.add(WamlElement(tag.name).apply {
                                tag.attributes.forEach {
                                    addAttribute(it.key, it.value)
                                }
                            })
                        } else {
                            element.contents.addAll(main())
                        }
                    }
                    is WamlTag.EndTag -> {
                        if (element == null) {
                            if (root) {
                                error("Unexpected tag: $tag")
                            } else {
                                index--
                                break
                            }
                        } else {
                            if (element.name == tag.name) {
                                elements.add(element)
                                element = null
                            } else {
                                error("Unexpected tag: $tag")
                            }
                        }
                    }
                    is WamlTag.Text -> {

                    }
                    is WamlTag.Whitespace -> {

                    }
                }

                index++
            }

            if (element != null) {
                error("${element.name} tag hasn't closed.")
            }

            return elements
        }

        return main(root = true)
    }

    fun parse(input: String): List<WamlElement> {
        return elementize(toTag(tokenize(input)))
    }
    fun parse(waml: File) : List<WamlElement> {
        return parse(waml.readText())
    }
}