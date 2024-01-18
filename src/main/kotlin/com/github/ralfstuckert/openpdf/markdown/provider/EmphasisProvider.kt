package com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.provider

import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.*
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.PdfRenderContextKeys.FONT_STYLE
import com.lowagie.text.Font
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.ast.ASTNode

class EmphasisProvider : AbstractElementProvider() {

    companion object {
        val EMPHASIS_RENDER_CONTEXT_KEY = ElementProviderRenderContextKey(MarkdownElementTypes.EMPH.name)
    }

    override val handledNodeTypes = listOf(MarkdownElementTypes.EMPH)

    override fun setupDefaultRenderContexts(registry: ElementProviderRenderContextRegistry) {
        registry.registerRenderContextFunction(EMPHASIS_RENDER_CONTEXT_KEY) {
            val parentContext = this
            derive {
                this[FONT_STYLE] = parentContext.fontStyle.or(Font.ITALIC)
            }
        }
    }

    override fun processNode(visitor: OpenPdfVisitor, providerContext: ElementProviderContext, node: ASTNode) {
        checkNodeType(node)
        val boldRenderContext = providerContext.deriveRenderContext(EMPHASIS_RENDER_CONTEXT_KEY)
        visitor.visitChildren(providerContext.parentPdfElement, boldRenderContext, node)
    }


}