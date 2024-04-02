package com.github.ralfstuckert.openpdf.markdown.renderer

import com.github.ralfstuckert.openpdf.markdown.*
import com.github.ralfstuckert.openpdf.markdown.PdfRenderContextKeys.FONT_STYLE
import com.lowagie.text.Font
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.ast.ASTNode

class EmphasisMarkdownRenderer : AbstractMarkdownRenderer() {

    companion object {
        val EMPHASIS_RENDER_CONTEXT_KEY = MarkdownRenderContextKey(MarkdownElementTypes.EMPH.name)
    }

    override val handledNodeTypes = listOf(MarkdownElementTypes.EMPH)

    override fun setupDefaultRenderContexts(registry: MarkdownRenderContextRegistry) {
        registry.registerRenderContextFunction(EMPHASIS_RENDER_CONTEXT_KEY) {
            val parentContext = this
            derive {
                this[FONT_STYLE] = parentContext.fontStyle.or(Font.ITALIC)
            }
        }
    }

    override fun processNode(visitor: OpenPdfVisitor, rendererContext: MarkdownRendererContext, node: ASTNode) {
        checkNodeType(node)
        val boldRenderContext = rendererContext.deriveRenderContext(EMPHASIS_RENDER_CONTEXT_KEY)
        visitor.visitChildren(rendererContext.parentPdfElement, boldRenderContext, node)
    }


}