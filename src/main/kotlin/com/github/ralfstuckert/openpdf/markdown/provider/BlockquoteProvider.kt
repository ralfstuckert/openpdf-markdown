package com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.provider

import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.*
import com.lowagie.text.*
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import org.intellij.markdown.MarkdownElementTypes.BLOCK_QUOTE
import org.intellij.markdown.MarkdownElementTypes.PARAGRAPH
import org.intellij.markdown.MarkdownTokenTypes.Companion.EOL
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.LeafASTNode
import java.awt.Color

class BlockquoteProvider : AbstractElementProvider() {

    companion object {
        val BlOCKQUOTE_RENDER_CONTEXT_KEY = ElementProviderRenderContextKey(BLOCK_QUOTE.name)
    }

    override val handledNodeTypes = listOf(BLOCK_QUOTE)

    override fun setupDefaultRenderContexts(registry: ElementProviderRenderContextRegistry) {
        registry.registerRenderContextFunction(BlOCKQUOTE_RENDER_CONTEXT_KEY) {
            val parentContext = this
            derive {
                this[PdfRenderContextKeys.BORDER_COLOR] = Color.LIGHT_GRAY
                this[PdfRenderContextKeys.BORDER_WIDTH] = parentContext.defaultBarWidth
                this[PdfRenderContextKeys.PADDING_LEFT] = parentContext.defaultPaddingLeft
            }
        }
    }

    override fun processNode(visitor: OpenPdfVisitor, providerContext: ElementProviderContext, node: ASTNode) {
        checkNodeType(node)
        if (node is LeafASTNode)
            return
        processBlockquoteNode(visitor, providerContext, node)
    }

    fun processBlockquoteNode(
        visitor: OpenPdfVisitor,
        providerContext: ElementProviderContext,
        blockquoteNode: ASTNode
    ) {
        val table = createBlockquoteTable(visitor, providerContext, blockquoteNode)
        providerContext.parentPdfElement.add(table)
    }

    fun createBlockquoteTable(
        visitor: OpenPdfVisitor,
        providerContext: ElementProviderContext,
        blockquoteNode: ASTNode
    ): PdfPTable {
        val blockquoteRenderContext = providerContext.deriveRenderContext(BlOCKQUOTE_RENDER_CONTEXT_KEY)

        val table = PdfPTable(1).apply {
            widthPercentage = 100f
        }

        var phrase = Phrase()
        blockquoteNode.children.forEach { child ->

            if (child.type == BLOCK_QUOTE) {
                if (child !is LeafASTNode) {
                    if (!phrase.isEmpty()) {
                        // use phrase until nested table needs to be added
                        table.addCell(PdfPCell(phrase).style(blockquoteRenderContext))
                        phrase = Phrase()
                    }
                    val blockquote = createBlockquoteTable(visitor, providerContext, child)
                    table.addCell(PdfPCell(blockquote).style(blockquoteRenderContext))
                }
            } else {
                visitor.visitNode(phrase, blockquoteRenderContext, child)
            }
        }
        if (!phrase.isEmpty()) {
            table.addCell(PdfPCell(phrase).style(blockquoteRenderContext))
            phrase = Phrase()
        }

        return table
    }

    fun PdfPCell.style(blockquoteRenderContext: PdfRenderContext): PdfPCell =
        apply {
            val barWidth =
                blockquoteRenderContext[PdfRenderContextKeys.BORDER_WIDTH] ?: blockquoteRenderContext.defaultBarWidth
            val barColor = blockquoteRenderContext[PdfRenderContextKeys.BORDER_COLOR] ?: Color.LIGHT_GRAY
            val padding = blockquoteRenderContext[PdfRenderContextKeys.PADDING_LEFT] ?: blockquoteRenderContext.defaultPaddingLeft
            borderWidth = 0f
            borderWidthLeft = barWidth
            borderColor = barColor
            paddingLeft = padding
        }

    val PdfRenderContext.defaultBarWidth
        get() = fontSize / 4f

    val PdfRenderContext.defaultPaddingLeft
        get() = fontSize
}

