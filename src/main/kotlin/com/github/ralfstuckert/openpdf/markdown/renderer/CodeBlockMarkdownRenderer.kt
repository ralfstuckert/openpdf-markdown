package com.github.ralfstuckert.openpdf.markdown.renderer

import com.github.ralfstuckert.openpdf.markdown.*
import com.lowagie.text.Chunk
import com.lowagie.text.Font
import com.lowagie.text.Paragraph
import org.intellij.markdown.MarkdownElementTypes.CODE_FENCE
import org.intellij.markdown.MarkdownTokenTypes.Companion.CODE_FENCE_END
import org.intellij.markdown.MarkdownTokenTypes.Companion.CODE_FENCE_START
import org.intellij.markdown.MarkdownTokenTypes.Companion.FENCE_LANG
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode

class CodeBlockMarkdownRenderer : AbstractMarkdownRenderer() {

    companion object {
        val CODE_BlOCK_RENDER_CONTEXT_KEY = MarkdownRenderContextKey(CODE_FENCE.name)
    }

    override val handledNodeTypes = listOf(CODE_FENCE)

    override fun setupDefaultRenderContexts(registry: MarkdownRenderContextRegistry) {
        registry.registerRenderContextFunction(CODE_BlOCK_RENDER_CONTEXT_KEY) {
            derive {
                this[PdfRenderContextKeys.FONT_FAMILY] = Font.COURIER
            }
        }
    }

    override fun processNode(visitor: OpenPdfVisitor, rendererContext: MarkdownRendererContext, node: ASTNode) {
        checkNodeType(node)
        val codespanRenderContext = rendererContext.deriveRenderContext(CODE_BlOCK_RENDER_CONTEXT_KEY)
        val paragraph = Paragraph()
        getCodespanText(node, rendererContext.markdownText)
            .map {
                Chunk(it).applyPdfRenderContext(codespanRenderContext)
            }
            .forEach {
                paragraph.add(it)
            }
        rendererContext.parentPdfElement.add(paragraph)
    }

    fun getCodespanText(node: ASTNode, markdownText: String): List<String> =
        node.children
            .filter { it.type != CODE_FENCE_START && it.type != CODE_FENCE_END && it.type != FENCE_LANG  }
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

