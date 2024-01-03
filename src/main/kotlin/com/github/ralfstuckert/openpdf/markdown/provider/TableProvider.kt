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
                this[PdfRenderContextKeys.BORDER_WIDTH] = registry.defaultRenderContext[PdfRenderContextKeys.BORDER_WIDTH] ?: 1f
                this[PdfRenderContextKeys.BORDER_COLOR] = registry.defaultRenderContext.fontColor
                this[PdfRenderContextKeys.WIDTH_PERCENTAGE] = registry.defaultRenderContext[PdfRenderContextKeys.WIDTH_PERCENTAGE] ?: 100f
                this[PdfRenderContextKeys.WEIGHTED_WIDTHS] = registry.defaultRenderContext[PdfRenderContextKeys.WEIGHTED_WIDTHS] ?: false
            }
        }
    }

    override val handledNodeTypes = listOf(GFMElementTypes.TABLE)

    override fun processNode(visitor: OpenPdfVisitor, providerContext: ElementProviderContext, node: ASTNode) {
        checkNodeType(node)

        val tableRenderContext = providerContext.deriveRenderContext(TABLE_RENDER_CONTEXT_KEY)

        val headers = processHeaderNode(visitor, tableRenderContext, getHeader(node))

        val weightedWidths = tableRenderContext[PdfRenderContextKeys.WEIGHTED_WIDTHS] ?: false
        val rowDescriptor = processRowDescriptor(providerContext.markdownText, getRowDescriptor(node), weightedWidths)
        val borderDescriptor = tableRenderContext.getBorderDescriptor()

        val relativeWidths = rowDescriptor.columnDescriptors.map { it.weight }.toFloatArray()
        val table = PdfPTable(relativeWidths).apply {
            widthPercentage = tableRenderContext[PdfRenderContextKeys.WIDTH_PERCENTAGE] ?: 100f
        }

        headers.forEachIndexed { index, paragraph ->
            val cell = createCell(paragraph, index, rowDescriptor, borderDescriptor)
            table.addCell(cell)
        }

        val rows = node.children.map {
            if (it.type == GFMElementTypes.ROW) processRowNode(visitor, tableRenderContext, it) else null
        }.filterNotNull()

        rows.forEach { row ->
            row.forEachIndexed { index, paragraph ->
                val cell = createCell(paragraph, index, rowDescriptor, borderDescriptor)
                table.addCell(cell)
            }
        }

        providerContext.parentPdfElement.add(table)
    }

    fun createCell(paragraph: Paragraph, index:Int, rowDescriptor: TableRowDescriptor, borderDescriptor:BorderDescriptor) =
        PdfPCell(paragraph).apply {
            horizontalAlignment = rowDescriptor[index].alignment.ordinal
            borderColor = borderDescriptor.color
            borderWidth = borderDescriptor.width
            if (borderDescriptor.width == 0f) {
                border = Rectangle.NO_BORDER
                if (index == 0) {
                    paddingLeft = 0f
                }
                if (index == rowDescriptor.size-1) {
                    paddingRight = 0f
                }
            }
        }

    fun processRowDescriptor(
        markdownText:String,
        node: ASTNode,
        weightedWidths:Boolean
    ): TableRowDescriptor {
        assert(node.type == GFMTokenTypes.TABLE_SEPARATOR) { "expected TABLE_SEPARATOR but is '${node.type}'" }
        val text = node.getTextInNode(markdownText).toString()
        val sumDashCount = text.dashCount.toFloat()

        return TableRowDescriptor(
            text.split("|")
                .filterNot { it.isEmpty() }
                .map { it.trim() }
                .map {
                    val weight = if(weightedWidths) it.dashCount/sumDashCount else 1f
                    when {
                        it.startsWith(":") && it.endsWith(":") -> TableColumnDescriptor(TableColumnAlignment.center, weight)
                        it.endsWith(":") -> TableColumnDescriptor(TableColumnAlignment.right, weight)
                        else -> TableColumnDescriptor(TableColumnAlignment.left, weight)
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

data class TableColumnDescriptor(val alignment: TableColumnAlignment, val weight:Float)

data class TableRowDescriptor(val columnDescriptors: List<TableColumnDescriptor>) {

    operator fun get(index: Int): TableColumnDescriptor = // be gentle
        if (index < columnDescriptors.size) columnDescriptors[index] else TableColumnDescriptor(TableColumnAlignment.left, 1f)

    val size:Int
        get() = columnDescriptors.size
}

data class BorderDescriptor(val width:Float, val color:Color)

fun PdfRenderContext.getBorderDescriptor() = BorderDescriptor(
this[PdfRenderContextKeys.BORDER_WIDTH] ?: 1f,
this[PdfRenderContextKeys.BORDER_COLOR] ?: PdfRenderContextDefaults.fontColor
)

val String.dashCount
    get() = count { it == '-' }