package com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.provider

import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.ElementProviderContext
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.ElementProviderRenderContextKey
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.ElementProviderRenderContextRegistry
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.OpenPdfVisitor
import com.lowagie.text.Paragraph
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import org.intellij.markdown.IElementType
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode
import org.intellij.markdown.flavours.gfm.GFMElementTypes
import org.intellij.markdown.flavours.gfm.GFMTokenTypes

class TableProvider : AbstractElementProvider() {

    companion object {
        val TABLE_RENDER_CONTEXT_KEY = ElementProviderRenderContextKey(GFMElementTypes.TABLE.name)
    }

    override fun setupDefaultRenderContexts(registry: ElementProviderRenderContextRegistry) {
        registry.registerRenderContextFunction(TABLE_RENDER_CONTEXT_KEY) {
            this
        }
    }

    override val handledNodeTypes = listOf(GFMElementTypes.TABLE)

    override fun processNode(visitor: OpenPdfVisitor, providerContext: ElementProviderContext, node: ASTNode) {
        checkNodeType(node)
        val headers = processHeaderNode(visitor, providerContext, getHeader(node))
        val table = PdfPTable(headers.size).apply {
            widthPercentage = 100f
        }

        val rowDescriptor = processRowDescriptor(visitor, providerContext, getRowDescriptor(node))

        headers.forEachIndexed { index, paragraph ->
            val cell = PdfPCell(paragraph).apply {
                horizontalAlignment = rowDescriptor[index].alignment.ordinal
            }
            table.addCell(cell)
        }

        val rows = node.children.map {
            if (it.type == GFMElementTypes.ROW) processRowNode(visitor, providerContext, it) else null
        }.filterNotNull()

        rows.forEach { row ->
            row.forEachIndexed { index, paragraph ->
                val cell = PdfPCell(paragraph).apply {
                    horizontalAlignment = rowDescriptor[index].alignment.ordinal
                }
                table.addCell(cell)
            }
        }

        providerContext.parentPdfElement.add(table)
    }

    fun processRowDescriptor(
        visitor: OpenPdfVisitor,
        providerContext: ElementProviderContext,
        node: ASTNode
    ): TableRowDescriptor {
        assert(node.type == GFMTokenTypes.TABLE_SEPARATOR) { "expected TABLE_SEPARATOR but is '${node.type}'" }
        return TableRowDescriptor(
            node.getTextInNode(providerContext.markdownText).toString()
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
        providerContext: ElementProviderContext,
        node: ASTNode
    ): List<Paragraph> =
        processTableRow(GFMElementTypes.HEADER, visitor, providerContext, node)

    fun processRowNode(
        visitor: OpenPdfVisitor,
        providerContext: ElementProviderContext,
        node: ASTNode
    ): List<Paragraph> =
        processTableRow(GFMElementTypes.ROW, visitor, providerContext, node)

    fun processTableRow(
        expectedElementType: IElementType,
        visitor: OpenPdfVisitor,
        providerContext: ElementProviderContext,
        node: ASTNode
    ): List<Paragraph> {
        assert(node.type == expectedElementType) { "expected $expectedElementType but is '${node.type}'" }
        return node.children.map {
            if (it.type == GFMTokenTypes.CELL) processCellNode(visitor, providerContext, it) else null
        }.filterNotNull()
    }

    fun processCellNode(visitor: OpenPdfVisitor, providerContext: ElementProviderContext, node: ASTNode): Paragraph {
        assert(node.type == GFMTokenTypes.CELL) { "expected CELL but is '${node.type}'" }
        val paragraph = Paragraph()
        visitor.visitChildren(paragraph, providerContext.renderContext, node)
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