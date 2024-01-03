package com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.provider

import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.*
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.PdfRenderContextKeys.FONT_STYLE
import com.lowagie.text.*
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.ast.ASTNode

class StrongProvider : AbstractElementProvider() {

    companion object {
        val STRONG_RENDER_CONTEXT_KEY = ElementProviderRenderContextKey(MarkdownElementTypes.STRONG.name)
    }

    override val handledNodeTypes = listOf(MarkdownElementTypes.STRONG)

    override fun setupDefaultRenderContexts(registry: ElementProviderRenderContextRegistry) {
        registry.registerRenderContextFunction(STRONG_RENDER_CONTEXT_KEY) {
            derive {
                this[FONT_STYLE] = this@registerRenderContextFunction.fontStyle.or(Font.BOLD)
            }
        }
    }

    override fun processNode(visitor: OpenPdfVisitor, providerContext: ElementProviderContext, node: ASTNode) {
        checkNodeType(node)
        val boldRenderContext = providerContext.deriveRenderContext(STRONG_RENDER_CONTEXT_KEY)
        visitor.visitChildren(providerContext.parentPdfElement, boldRenderContext, node)
    }


}