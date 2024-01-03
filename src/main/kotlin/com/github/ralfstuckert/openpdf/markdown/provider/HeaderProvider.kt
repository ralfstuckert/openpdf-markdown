package com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.provider

import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.*
import org.intellij.markdown.IElementType
import org.intellij.markdown.MarkdownElementTypes.ATX_1
import org.intellij.markdown.MarkdownElementTypes.ATX_2
import org.intellij.markdown.MarkdownElementTypes.ATX_3
import org.intellij.markdown.MarkdownElementTypes.ATX_4
import org.intellij.markdown.MarkdownElementTypes.ATX_5
import org.intellij.markdown.MarkdownElementTypes.ATX_6
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.MarkdownTokenTypes.Companion.ATX_CONTENT
import org.intellij.markdown.ast.ASTNode

class HeaderProvider : AbstractElementProvider() {

    override fun setupDefaultRenderContexts(registry: ElementProviderRenderContextRegistry) {
        handledNodeTypes.forEach { elementType ->
            registry.registerRenderContextFunction(ElementProviderRenderContextKey(elementType.name)) {
                derive {
                    this[PdfRenderContextKeys.FONT_SIZE] = getFontSize(elementType)
                }
            }
        }
    }

    override val handledNodeTypes = listOf(ATX_1,ATX_2,ATX_3,ATX_4,ATX_5,ATX_6)

    override fun processNode(visitor: OpenPdfVisitor, providerContext: ElementProviderContext, node: ASTNode) {
        checkNodeType(node)
        val headerRenderContext = providerContext.deriveRenderContext(ElementProviderRenderContextKey(node.type.name))
        visitor.visitChildren(providerContext.parentPdfElement, headerRenderContext, getContentNode(node), trim = true)
    }


    fun getFontSize(type:IElementType) :Float =
        when (type) {
            ATX_1 -> 24f
            ATX_2 -> 20f
            ATX_3 -> 18f
            ATX_4 -> 16f
            ATX_5 -> 14f
            ATX_6 -> 12f
            else -> 12f
        }

    fun getContentNode(node:ASTNode):ASTNode =
        getChildNode(node, ATX_CONTENT)

}