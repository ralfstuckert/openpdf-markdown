package com.github.ralfstuckert.openpdf.markdown.renderer

import com.github.ralfstuckert.openpdf.markdown.*
import com.lowagie.text.Chunk
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


class TableMarkdownRenderer : AbstractMarkdownRenderer() {

    companion object {
        val TABLE_RENDER_CONTEXT_KEY = MarkdownRenderContextKey(GFMElementTypes.TABLE.name)
    }

    override fun setupDefaultRenderContexts(registry: MarkdownRenderContextRegistry) {
        registry.registerRenderContextFunction(TABLE_RENDER_CONTEXT_KEY) {
            derive {
                this[PdfRenderContextKeys.BORDER_WIDTH] = registry.defaultRenderContext[PdfRenderContextKeys.BORDER_WIDTH] ?: 1f
                this[PdfRenderContextKeys.BORDER_COLOR] = registry.defaultRenderContext.color
                this[PdfRenderContextKeys.WIDTH_PERCENTAGE] = registry.defaultRenderContext[PdfRenderContextKeys.WIDTH_PERCENTAGE] ?: 100f
                this[PdfRenderContextKeys.WEIGHTED_WIDTHS_ENABLED] = registry.defaultRenderContext[PdfRenderContextKeys.WEIGHTED_WIDTHS_ENABLED] ?: false
                this[PdfRenderContextKeys.COLSPAN_ENABLED] = registry.defaultRenderContext[PdfRenderContextKeys.COLSPAN_ENABLED] ?: false
                this[PdfRenderContextKeys.HORIZONTAL_ALIGNMENT] = registry.defaultRenderContext[PdfRenderContextKeys.HORIZONTAL_ALIGNMENT] ?: HorizontalAlignment.center
            }
        }
    }

    override val handledNodeTypes = listOf(GFMElementTypes.TABLE)

    override fun processNode(visitor: OpenPdfVisitor, rendererContext: MarkdownRendererContext, node: ASTNode) {
        checkNodeType(node)

        val parentRenderContext = rendererContext.renderContext
        val tableRenderContext = rendererContext.deriveRenderContext(TABLE_RENDER_CONTEXT_KEY)
        val weightedWidthsEnabled = tableRenderContext[PdfRenderContextKeys.WEIGHTED_WIDTHS_ENABLED] ?: false
        val colspanEnabled = tableRenderContext[PdfRenderContextKeys.COLSPAN_ENABLED] ?: false
        val alignment = tableRenderContext[PdfRenderContextKeys.HORIZONTAL_ALIGNMENT] ?: HorizontalAlignment.center

        val headers = processHeaderNode(visitor, parentRenderContext, getHeader(node), colspanEnabled)
        val rowDescriptor = processRowDescriptor(rendererContext.markdownText, getRowDescriptor(node), weightedWidthsEnabled)
        val borderDescriptor = tableRenderContext.getBorderDescriptor()

        val relativeWidths = rowDescriptor.columnDescriptors.map { it.weight }.toFloatArray()
        val table = PdfPTable(relativeWidths).apply {
            widthPercentage = tableRenderContext[PdfRenderContextKeys.WIDTH_PERCENTAGE] ?: 100f
            horizontalAlignment = alignment.ordinal
        }

        headers.forEachIndexed { index, paragraph ->
            val cell = createCell(paragraph, index, rowDescriptor, borderDescriptor)
            table.addCell(cell)
        }

        val rows = node.children.map {
            if (it.type == GFMElementTypes.ROW) processRowNode(visitor, parentRenderContext, it, colspanEnabled) else null
        }.filterNotNull()

        rows.forEach { row ->
            row.forEachIndexed { index, paragraph ->
                val cell = createCell(paragraph, index, rowDescriptor, borderDescriptor)
                table.addCell(cell)
            }
        }

        rendererContext.parentPdfElement.add(table)
    }

    fun createCell(tableCell: TableCell, index:Int, rowDescriptor: TableRowDescriptor, borderDescriptor:BorderDescriptor) =
        PdfPCell(tableCell.paragraph).apply {
            colspan = tableCell.colspan
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
        node: ASTNode,
        colspanEnabled:Boolean
    ): List<TableCell> =
        processTableRow(GFMElementTypes.HEADER, visitor, renderContext, node, colspanEnabled)

    fun processRowNode(
        visitor: OpenPdfVisitor,
        renderContext: PdfRenderContext,
        node: ASTNode,
        colspanEnabled:Boolean
    ): List<TableCell> =
        processTableRow(GFMElementTypes.ROW, visitor, renderContext, node, colspanEnabled)

    fun processTableRow(
        expectedElementType: IElementType,
        visitor: OpenPdfVisitor,
        renderContext: PdfRenderContext,
        node: ASTNode,
        colspanEnabled:Boolean
    ): List<TableCell> {
        assert(node.type == expectedElementType) { "expected $expectedElementType but is '${node.type}'" }
        val children = node.children
        val (list, paragraph, colspan) = children.foldIndexed(Triple<List<TableCell>, Paragraph?, Int>(listOf<TableCell>(), null, 0)) { index, (list, paragraph, colspan), child ->
            when (child.type) {
                GFMTokenTypes.TABLE_SEPARATOR -> {
                    if (index == 0)
                        Triple(list, paragraph, colspan)
                    else {
                        if (colspanEnabled)
                            Triple(list, paragraph ?: Paragraph(Chunk("")), colspan + 1)
                        else {
                            val tableCell = TableCell(paragraph ?: Paragraph(Chunk("")), 1)
                            Triple(list+tableCell, null, 0)
                        }
                    }
                }
                GFMTokenTypes.CELL -> {
                    if (child.isEmptyColspanCell(colspanEnabled)) {
                        // fix for IDEA-315374 Table values are shifted in Markdown preview
                        Triple(list, paragraph, colspan)
                    } else {
                        val nextCell = processCellNode(visitor, renderContext, child)
                        val actualColspan = if (colspan > 0) colspan else 1
                        val nextList = if (paragraph != null) list + TableCell(paragraph, actualColspan) else list
                        Triple(nextList, nextCell, 0)
                    }
                }
                else -> Triple(list, paragraph, colspan)
            }
        }
        return if (paragraph != null)
            list + TableCell(paragraph, colspan)
        else list
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

fun ASTNode.isEmptyColspanCell(colspanEnabled:Boolean) =
    colspanEnabled && this.children.isEmpty()

data class TableCell(val paragraph: Paragraph, val colspan:Int =1)

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
this[PdfRenderContextKeys.BORDER_COLOR] ?: PdfRenderContextDefaults.color
)

val String.dashCount
    get() = count { it == '-' }