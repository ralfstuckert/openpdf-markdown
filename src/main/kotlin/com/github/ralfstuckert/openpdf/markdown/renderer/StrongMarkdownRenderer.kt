package com.github.ralfstuckert.openpdf.markdown.renderer

import com.github.ralfstuckert.openpdf.markdown.*
import com.github.ralfstuckert.openpdf.markdown.PdfRenderContextKeys.FONT_STYLE
import com.lowagie.text.Font
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.ast.ASTNode

class StrongMarkdownRenderer : AbstractMarkdownRenderer() {

    companion object {
        val STRONG_RENDER_CONTEXT_KEY = MarkdownRenderContextKey(MarkdownElementTypes.STRONG.name)
    }

    override val handledNodeTypes = listOf(MarkdownElementTypes.STRONG)

    override fun setupDefaultRenderContexts(registry: MarkdownRenderContextRegistry) {
        registry.registerRenderContextFunction(STRONG_RENDER_CONTEXT_KEY) {
            val parentContext = this
            derive {
                this[FONT_STYLE] = parentContext.fontStyle.or(Font.BOLD)
            }
        }
    }

    override fun processNode(visitor: OpenPdfVisitor, rendererContext: MarkdownRendererContext, node: ASTNode) {
        checkNodeType(node)
        val boldRenderContext = rendererContext.deriveRenderContext(STRONG_RENDER_CONTEXT_KEY)
        visitor.visitChildren(rendererContext.parentPdfElement, boldRenderContext, node)
    }


}