package com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.provider

import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.*
import com.lowagie.text.Chunk
import com.lowagie.text.Font
import org.intellij.markdown.MarkdownElementTypes.CODE_SPAN
import org.intellij.markdown.MarkdownTokenTypes.Companion.BACKTICK
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode

class InlineCodeProvider : AbstractElementProvider() {

    companion object {
        val INLINE_CODE_RENDER_CONTEXT_KEY = ElementProviderRenderContextKey(CODE_SPAN.name)
    }

    override val handledNodeTypes = listOf(CODE_SPAN)

    override fun setupDefaultRenderContexts(registry: ElementProviderRenderContextRegistry) {
        registry.registerRenderContextFunction(INLINE_CODE_RENDER_CONTEXT_KEY) {
            derive {
                this[PdfRenderContextKeys.FONT_FAMILY] = Font.COURIER
            }
        }
    }

    override fun processNode(visitor: OpenPdfVisitor, providerContext: ElementProviderContext, node: ASTNode) {
        checkNodeType(node)
        val codespanRenderContext = providerContext.deriveRenderContext(INLINE_CODE_RENDER_CONTEXT_KEY)
        getCodespanText(node, providerContext.markdownText)
            .map {
                Chunk(it).applyPdfRenderContext(codespanRenderContext)
            }
            .forEach {
                providerContext.parentPdfElement.add(it)
            }
    }

    fun getCodespanText(node: ASTNode, markdownText: String): List<String> =
        node.children
            .filter { it.type != BACKTICK }
            .map { it.getTextInNode(markdownText).toString() }
            .fold(Pair(emptyList<String>(), "")) { (list, last), current ->
                if (current.isLinebreak())
                    Pair(list+last+current, "")
                else
                    Pair(list, last+current)
            }
            .let {
                it.first + it.second
            }

}

fun String.isLinebreak() =
    this == "\r" ||this == "\n" ||this == "\r\n"

