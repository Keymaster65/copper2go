interface Element {
    fun renderContent(builder: StringBuilder)
}

class TextElement(val text: String) : Element {
    override fun renderContent(builder: StringBuilder) {
        builder.append("$text ")
    }
}

@DslMarker
annotation class ElementMarker

@ElementMarker
abstract class ComplexElement() : Element {
    val children = arrayListOf<Element>()
    val attributes = hashMapOf<String, String>()

    protected fun <T : Element> initElement(element: T, init: T.() -> Unit): T {
        element.init()
        children.add(element)
        return element
    }

    final override fun renderContent(builder: StringBuilder) {
        builder.appendLine()
        builder.append(this.javaClass.getSimpleName())
        builder.append(renderAttributes())
        builder.appendLine()
        for (child in children) {
            child.renderContent(builder)
        }
    }

    protected fun renderAttributes(): String {
        val builder = StringBuilder()
        for ((attr, value) in attributes) {
            builder.append(" $attr=\"$value\" ")
        }
        return builder.toString()
    }

    fun renderContent(): String {
        val builder = StringBuilder()
        renderContent(builder)
        return builder.toString()
    }
}

abstract class ElementWithText() : ComplexElement() {
    operator fun String.unaryPlus() {
        children.add(TextElement(this))
    }
}

class Document : ComplexElement() {
    fun header(init: Header.() -> Unit) = initElement(Header(), init)
    fun payload(init: Payload.() -> Unit) = initElement(Payload(), init)
}

class Header : ElementWithText() {
    fun title(init: Title.() -> Unit) = initElement(Title(), init)
}

class Title : ElementWithText()

class Payload : ElementWithText() {
    fun link(type: String, init: Link.() -> Unit) {
        val link = initElement(Link(), init)
        link.type = type
    }
    fun link(init: Link.() -> Unit) = initElement(Link(), init)
}

class Link : ElementWithText() {
    var type: String
        get() = attributes["type"]!!
        set(value) {
            attributes["type"] = value
        }
}

fun document(init: Document.() -> Unit): Document {
    val document = Document()
    document.init()
    return document
}

fun main() {

    val doc =
        document {
            header {
                title { +"Document Title" }
            }
            payload {
                +"data1"
                link(type = "http") { +"http://my-site.com" }
                +"data2"
                link {+"https://my-site.com"
                }
            }
        }
    println(doc.renderContent())
}
