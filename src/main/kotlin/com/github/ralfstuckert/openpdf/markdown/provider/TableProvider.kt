package com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.provider

import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.*
import com.lowagie.text.Paragraph
import com.lowagie.text.Rectangle
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import org.intellij.markdown.IElementType
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode
import org.intellij.markdown.flavours.gfm.GFMElementTypes
import org.intellij.markdown.flavours.gfm.GFMTokenTypes
import java.awt.Color


class TableProvider : AbstractElementProvider() {

    companion object {
        val TABLE_RENDER_CONTEXT_KEY = ElementProviderRenderContextKey(GFMElementTypes.TABLE.name)
    }

    override fun setupDefaultRenderContexts(registry: ElementProviderRenderContextRegistry) {
        registry.registerRenderContextFunction(TABLE_RENDER_CONTEXT_KEY) {
            derive {
                this[PdfRenderContextKeys.BORDER_WIDTH] = this@registerRenderContextFunction[PdfRenderContextKeys.BORDER_WIDTH] ?: 1f
                this[PdfRenderContextKeys.BORDER_COLOR] = this@registerRenderContextFunction.fontColor
            }
        }
    }

    override val handledNodeTypes = listOf(GFMElementTypes.TABLE)

    override fun processNode(visitor: OpenPdfVisitor, providerContext: ElementProviderContext, node: ASTNode) {
        checkNodeType(node)

        val tableRenderContext = providerContext.deriveRenderContext(TABLE_RENDER_CONTEXT_KEY)

        val headers = processHeaderNode(visitor, tableRenderContext, getHeader(node))
        val columnCount = headers.size
        val table = PdfPTable(columnCount).apply {
            widthPercentage = 100f
            defaultCell.paddingLeft = 0f
        }

        val rowDescriptor = processRowDescriptor(providerContext.markdownText, getRowDescriptor(node))
        val borderDescriptor = tableRenderContext.getBorderDescriptor()

        headers.forEachIndexed { index, paragraph ->
            val cell = createCell(paragraph, index, columnCount, rowDescriptor[index], borderDescriptor)
            table.addCell(cell)
        }

        val rows = node.children.map {
            if (it.type == GFMElementTypes.ROW) processRowNode(visitor, tableRenderContext, it) else null
        }.filterNotNull()

        rows.forEach { row ->
            row.forEachIndexed { index, paragraph ->
                val cell = createCell(paragraph, index, columnCount, rowDescriptor[index], borderDescriptor)
                table.addCell(cell)
            }
        }

        providerContext.parentPdfElement.add(table)
    }

    fun createCell(paragraph: Paragraph, index:Int, columnCount:Int, columnDescriptor: TableColumnDescriptor, borderDescriptor:BorderDescriptor) =
        PdfPCell(paragraph).apply {
            horizontalAlignment = columnDescriptor.alignment.ordinal
            borderColor = borderDescriptor.color
            borderWidth = borderDescriptor.width
            if (borderDescriptor.width == 0f) {
                border = Rectangle.NO_BORDER
                if (index == 0) {
                    paddingLeft = 0f
                }
                if (index == columnCount-1) {
                    paddingRight = 0f
                }
            }
        }

    fun processRowDescriptor(
        markdownText:String,
        node: ASTNode
    ): TableRowDescriptor {
        assert(node.type == GFMTokenTypes.TABLE_SEPARATOR) { "expected TABLE_SEPARATOR but is '${node.type}'" }
        return TableRowDescriptor(
            node.getTextInNode(markdownText).toString()
                .split("|")
                .filterNot { it.isEmpty() }
                .map { it.trim() }
                .map {
                    when {
                        it.startsWith(":") && it.endsWith(":") -> TableColumnDescriptor(TableColumnAlignment.center)
                        it.endsWith(":") -> TableColumnDescriptor(TableColumnAlignment.right)
                        else -> TableColumnDescriptor(TableColumnAlignment.left)
                    }
                }
        )
    }

    fun processHeaderNode(
        visitor: OpenPdfVisitor,
        renderContext: PdfRenderContext,
        node: ASTNode
    ): List<Paragraph> =
        processTableRow(GFMElementTypes.HEADER, visitor, renderContext, node)

    fun processRowNode(
        visitor: OpenPdfVisitor,
        renderContext: PdfRenderContext,
        node: ASTNode
    ): List<Paragraph> =
        processTableRow(GFMElementTypes.ROW, visitor, renderContext, node)

    fun processTableRow(
        expectedElementType: IElementType,
        visitor: OpenPdfVisitor,
        renderContext: PdfRenderContext,
        node: ASTNode
    ): List<Paragraph> {
        assert(node.type == expectedElementType) { "expected $expectedElementType but is '${node.type}'" }
        return node.children.map {
            if (it.type == GFMTokenTypes.CELL) processCellNode(visitor, renderContext, it) else null
        }.filterNotNull()
    }

    fun processCellNode(visitor: OpenPdfVisitor, renderContext: PdfRenderContext, node: ASTNode): Paragraph {
        assert(node.type == GFMTokenTypes.CELL) { "expected CELL but is '${node.type}'" }
        val paragraph = Paragraph()
        visitor.visitChildren(paragraph, renderContext, node, trim = true)
        return paragraph
    }

    fun getHeader(node: ASTNode): ASTNode =
        getChildNode(node, GFMElementTypes.HEADER)

    fun getRowDescriptor(node: ASTNode): ASTNode =
        getChildNode(node, GFMTokenTypes.TABLE_SEPARATOR)


}

enum class TableColumnAlignment {
    left, center, right
}

data class TableColumnDescriptor(val alignment: TableColumnAlignment)

data class TableRowDescriptor(private val columnDescriptors: List<TableColumnDescriptor>) {

    operator fun get(index: Int): TableColumnDescriptor = // be gentle
        if (index < columnDescriptors.size) columnDescriptors[index] else TableColumnDescriptor(TableColumnAlignment.left)
}

data class BorderDescriptor(val width:Float, val color:Color)

fun PdfRenderContext.getBorderDescriptor() = BorderDescriptor(
this[PdfRenderContextKeys.BORDER_WIDTH] ?: 1f,
this[PdfRenderContextKeys.BORDER_COLOR] ?: PdfRenderContextDefaults.fontColor
)
