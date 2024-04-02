package com.github.ralfstuckert.openpdf.markdown.renderer

import com.github.ralfstuckert.openpdf.markdown.*
import com.github.ralfstuckert.openpdf.markdown.PdfRenderContextKeys.FONT_STYLE
import com.lowagie.text.Font
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.flavours.gfm.GFMElementTypes.STRIKETHROUGH

class StrikethroughMarkdownRenderer : AbstractMarkdownRenderer() {

    companion object {
        val STRIKETHROUGH_RENDER_CONTEXT_KEY = MarkdownRenderContextKey(STRIKETHROUGH.name)
    }

    override val handledNodeTypes = listOf(STRIKETHROUGH)

    override fun setupDefaultRenderContexts(registry: MarkdownRenderContextRegistry) {
        registry.registerRenderContextFunction(STRIKETHROUGH_RENDER_CONTEXT_KEY) {
            val parentContext = this
            derive {
                this[FONT_STYLE] = parentContext.fontStyle.or(Font.STRIKETHRU)
            }
        }
    }

    override fun processNode(visitor: OpenPdfVisitor, rendererContext: MarkdownRendererContext, node: ASTNode) {
        checkNodeType(node)
        val strikethroughRenderContext = rendererContext.deriveRenderContext(STRIKETHROUGH_RENDER_CONTEXT_KEY)
        visitor.visitChildren(rendererContext.parentPdfElement, strikethroughRenderContext, node)
    }


}