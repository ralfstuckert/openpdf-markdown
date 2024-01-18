package com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.provider

import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.*
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.PdfRenderContextKeys.COLOR
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.PdfRenderContextKeys.LINE_THICKNESS
import com.lowagie.text.Anchor
import org.intellij.markdown.MarkdownElementTypes.INLINE_LINK
import org.intellij.markdown.MarkdownElementTypes.LINK_DESTINATION
import org.intellij.markdown.MarkdownElementTypes.LINK_TEXT
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode

class LinkProvider : AbstractElementProvider() {

    companion object {
        val INLINE_LINK_RENDER_CONTEXT_KEY = ElementProviderRenderContextKey(INLINE_LINK.name)
    }

    override fun setupDefaultRenderContexts(registry: ElementProviderRenderContextRegistry) {
        registry.registerRenderContextFunction(INLINE_LINK_RENDER_CONTEXT_KEY) {
            val parentContext = this
            derive {
                this[LINE_THICKNESS] = parentContext.fontSize *  0.07f
                this[COLOR] = registry.defaultRenderContext.color
            }
        }
    }

    override val handledNodeTypes = listOf(INLINE_LINK)

    override fun processNode(visitor: OpenPdfVisitor, providerContext: ElementProviderContext, node: ASTNode) {
        checkNodeType(node)
        val anchor = Anchor()
        anchor.reference = getLinkDestination(node).getTextInNode(providerContext.markdownText).toString()

        val linkRenderContext = providerContext.deriveRenderContext(INLINE_LINK_RENDER_CONTEXT_KEY)
        visitor.visitChildren(anchor, linkRenderContext, getLinkTextNode(node))
        providerContext.parentPdfElement.add(anchor)
    }

    fun getLinkTextNode(node: ASTNode): ASTNode =
        getChildNode(node, LINK_TEXT)

    fun getLinkDestination(node: ASTNode): ASTNode =
        getChildNode(node, LINK_DESTINATION)


}