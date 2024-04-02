package com.github.ralfstuckert.openpdf.markdown.renderer

import com.github.ralfstuckert.openpdf.markdown.*
import org.intellij.markdown.IElementType
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.MarkdownElementTypes.ATX_1
import org.intellij.markdown.MarkdownElementTypes.ATX_2
import org.intellij.markdown.MarkdownElementTypes.ATX_3
import org.intellij.markdown.MarkdownElementTypes.ATX_4
import org.intellij.markdown.MarkdownElementTypes.ATX_5
import org.intellij.markdown.MarkdownElementTypes.ATX_6
import org.intellij.markdown.MarkdownTokenTypes.Companion.ATX_CONTENT
import org.intellij.markdown.ast.ASTNode
import kotlin.math.round

class HeaderMarkdownRenderer : AbstractMarkdownRenderer() {

    companion object {
        val HEADER_1_RENDER_CONTEXT_KEY = MarkdownRenderContextKey(MarkdownElementTypes.ATX_1.name)
        val HEADER_2_RENDER_CONTEXT_KEY = MarkdownRenderContextKey(MarkdownElementTypes.ATX_2.name)
        val HEADER_3_RENDER_CONTEXT_KEY = MarkdownRenderContextKey(MarkdownElementTypes.ATX_3.name)
        val HEADER_4_RENDER_CONTEXT_KEY = MarkdownRenderContextKey(MarkdownElementTypes.ATX_4.name)
        val HEADER_5_RENDER_CONTEXT_KEY = MarkdownRenderContextKey(MarkdownElementTypes.ATX_5.name)
        val HEADER_6_RENDER_CONTEXT_KEY = MarkdownRenderContextKey(MarkdownElementTypes.ATX_6.name)
    }

    override fun setupDefaultRenderContexts(registry: MarkdownRenderContextRegistry) {
        handledNodeTypes.forEach { elementType ->
            registry.registerRenderContextFunction(MarkdownRenderContextKey(elementType.name)) {
                val defaultFontSize = registry.defaultRenderContext.fontSize
                derive {
                    this[PdfRenderContextKeys.FONT_SIZE] = getFontSize(elementType, defaultFontSize)
                }
            }
        }
    }

    override val handledNodeTypes = listOf(ATX_1,ATX_2,ATX_3,ATX_4,ATX_5,ATX_6)

    override fun processNode(visitor: OpenPdfVisitor, rendererContext: MarkdownRendererContext, node: ASTNode) {
        checkNodeType(node)
        val headerRenderContext = rendererContext.deriveRenderContext(MarkdownRenderContextKey(node.type.name))
        visitor.visitChildren(rendererContext.parentPdfElement, headerRenderContext, getContentNode(node), trim = true)
    }


    fun getFontSize(type:IElementType, defaultFontSize:Float) :Float =
        when (type) {
            ATX_1 -> round(defaultFontSize * 2.25f)
            ATX_2 -> round(defaultFontSize * 2)
            ATX_3 -> round(defaultFontSize * 1.6666666f)
            ATX_4 -> round(defaultFontSize * 1.5f)
            ATX_5 -> round(defaultFontSize * 1.3333333f)
            ATX_6 -> round(defaultFontSize * 1.1666666f)
            else -> defaultFontSize
        }

    fun getContentNode(node:ASTNode):ASTNode =
        getChildNode(node, ATX_CONTENT)

}