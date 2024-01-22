package com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.provider

import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.*
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.PdfRenderContextKeys.FONT_STYLE
import com.lowagie.text.Font
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.flavours.gfm.GFMElementTypes
import org.intellij.markdown.flavours.gfm.GFMElementTypes.STRIKETHROUGH

class StrikethroughProvider : AbstractElementProvider() {

    companion object {
        val STRIKETHROUGH_RENDER_CONTEXT_KEY = ElementProviderRenderContextKey(STRIKETHROUGH.name)
    }

    override val handledNodeTypes = listOf(STRIKETHROUGH)

    override fun setupDefaultRenderContexts(registry: ElementProviderRenderContextRegistry) {
        registry.registerRenderContextFunction(STRIKETHROUGH_RENDER_CONTEXT_KEY) {
            val parentContext = this
            derive {
                this[FONT_STYLE] = parentContext.fontStyle.or(Font.STRIKETHRU)
            }
        }
    }

    override fun processNode(visitor: OpenPdfVisitor, providerContext: ElementProviderContext, node: ASTNode) {
        checkNodeType(node)
        val strikethroughRenderContext = providerContext.deriveRenderContext(STRIKETHROUGH_RENDER_CONTEXT_KEY)
        visitor.visitChildren(providerContext.parentPdfElement, strikethroughRenderContext, node)
    }


}