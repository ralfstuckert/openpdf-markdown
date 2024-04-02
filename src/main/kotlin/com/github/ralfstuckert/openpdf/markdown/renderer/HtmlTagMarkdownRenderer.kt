package com.github.ralfstuckert.openpdf.markdown.renderer

import com.github.ralfstuckert.openpdf.markdown.MarkdownRendererContext
import com.github.ralfstuckert.openpdf.markdown.MarkdownRenderContextRegistry
import com.github.ralfstuckert.openpdf.markdown.OpenPdfVisitor
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode

class HtmlTagMarkdownRenderer : AbstractMarkdownRenderer() {

    override fun setupDefaultRenderContexts(registry: MarkdownRenderContextRegistry) {
    }

    override val handledNodeTypes = listOf(MarkdownElementTypes.HTML_BLOCK, MarkdownTokenTypes.HTML_TAG)

    override fun processNode(visitor: OpenPdfVisitor, rendererContext: MarkdownRendererContext, node: ASTNode) {
        checkNodeType(node)
        val tag = node.getTextInNode(rendererContext.markdownText).toString().toTag()
        if (tag is Tag.StartTag && tag.name == "img") {
            val imageProcessor = visitor.registry.getMarkdownRendererFor(MarkdownElementTypes.IMAGE)
            val src = tag.attributes["src"]
            val width = tag.attributes["width"]?.toFloatOrNull()
            val height = tag.attributes["height"]?.toFloatOrNull()

            if (src != null && imageProcessor is ImageProcessor) {
                imageProcessor.processImage(visitor, rendererContext, src, width, height)
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

interface TagRenderer {
    fun processTag(rendererContext: MarkdownRendererContext, tag:Tag)
}