package com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.provider

import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.ElementProviderContext
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.ElementProviderRenderContextRegistry
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.OpenPdfVisitor
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode

class HtmlTagProvider : AbstractElementProvider() {

    override fun setupDefaultRenderContexts(registry: ElementProviderRenderContextRegistry) {
    }

    override val handledNodeTypes = listOf(MarkdownElementTypes.HTML_BLOCK, MarkdownTokenTypes.HTML_TAG)

    override fun processNode(visitor: OpenPdfVisitor, providerContext: ElementProviderContext, node: ASTNode) {
        checkNodeType(node)
        val tag = node.getTextInNode(providerContext.markdownText).toString().toTag()
        if (tag is Tag.StartTag && tag.name == "img") {
            val imageProcessor = visitor.registry.getProviderFor(MarkdownElementTypes.IMAGE)
            val url = tag.attributes["url"]
            val width = tag.attributes["width"]?.toFloatOrNull()
            val height = tag.attributes["height"]?.toFloatOrNull()

            if (url != null && imageProcessor is ImageProcessor) {
                imageProcessor.processImage(visitor, providerContext, url, width, height)
            }
        }
    }
}

sealed class Tag(open val name:String) {


    data class StartTag(override val name:String, val attributes:Map<String,String?>):Tag(name) {
        companion object {
            fun fromString(text:String):StartTag? {
                val result = Regex("""<(\w+)\s+((.*)\s+)/?>""").matchEntire(text)
                return if (result == null) null else {
                    val name = result.groupValues[1].lowercase()
                    val attributes =  result.groupValues[3]
                        .split(" ")
                        .filterNot { it.isEmpty() }
                        .map {
                            val keyValue = it.split("=")
                            val key = keyValue[0]
                            val value = if (keyValue.size > 1) keyValue[1].trim('"', '\'') else null
                            key to value
                        }.toMap()
                    StartTag(name, attributes)
                }
            }
        }
    }

    data class EndTag(override val name:String):Tag(name) {
        companion object {
            fun fromString(text: String): EndTag? {
                val result = Regex("""</(\w+)>""").matchEntire(text)
                return if (result == null) return null else Tag.EndTag(result.groupValues[1].lowercase())
            }
        }
    }
}

fun String.toTag():Tag? =
    Tag.EndTag.fromString(this) ?: Tag.StartTag.fromString(this)

interface TagProvider {
    fun processTag(providerContext: ElementProviderContext, tag:Tag)
}