package com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.provider

import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.*
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.PdfRenderContextKeys.COLOR
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.PdfRenderContextKeys.LINE_THICKNESS
import com.lowagie.text.Anchor
import com.lowagie.text.Chunk
import com.lowagie.text.ListItem
import com.lowagie.text.pdf.PdfDocument.Indentation
import org.intellij.markdown.MarkdownElementTypes.INLINE_LINK
import org.intellij.markdown.MarkdownElementTypes.LINK_DESTINATION
import org.intellij.markdown.MarkdownElementTypes.LINK_TEXT
import org.intellij.markdown.MarkdownElementTypes.LIST_ITEM
import org.intellij.markdown.MarkdownElementTypes.ORDERED_LIST
import org.intellij.markdown.MarkdownElementTypes.PARAGRAPH
import org.intellij.markdown.MarkdownElementTypes.UNORDERED_LIST
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode

class ListProvider : AbstractElementProvider() {

    companion object {
        val ORDERED_LIST_RENDER_CONTEXT_KEY = ElementProviderRenderContextKey(ORDERED_LIST.name)
        val UNORDERED_LIST_RENDER_CONTEXT_KEY = ElementProviderRenderContextKey(UNORDERED_LIST.name)
    }

    override fun setupDefaultRenderContexts(registry: ElementProviderRenderContextRegistry) {
        registry.registerRenderContextFunction(ORDERED_LIST_RENDER_CONTEXT_KEY) { this }
        registry.registerRenderContextFunction(UNORDERED_LIST_RENDER_CONTEXT_KEY) { this }
    }

    override val handledNodeTypes = listOf(ORDERED_LIST, UNORDERED_LIST)

    override fun processNode(visitor: OpenPdfVisitor, providerContext: ElementProviderContext, node: ASTNode) {
        checkNodeType(node)
        val list = createListNode(visitor, providerContext, node)
        providerContext.parentPdfElement.add(list)
    }

    fun createListNode(
        visitor: OpenPdfVisitor,
        providerContext: ElementProviderContext,
        node: ASTNode,
        indentation: Float = 0f,
        listItemIndex:ListItemIndex = ListItemIndex()
    ): com.lowagie.text.List {
        val (renderContext, factory) = when (node.type) {
            ORDERED_LIST -> providerContext.deriveRenderContext(ORDERED_LIST_RENDER_CONTEXT_KEY) to ArabicNumberSymbolFactory()
            else -> providerContext.deriveRenderContext(UNORDERED_LIST_RENDER_CONTEXT_KEY) to RepeatingSymbolFactory()
        }

        val list = com.lowagie.text.List(false, false).apply {
            indentationLeft = indentation
        }

        val listItems = getListItems(node)
        var currentIndex = listItemIndex
        listItems.forEach {
            val listItemSymbol = factory.createSymbolFor(currentIndex)
            val listItem = ListItem().apply {
                listSymbol = Chunk(listItemSymbol).applyPdfRenderContext(renderContext)
                visitor.visitChildren(this, renderContext, getListItemParagraph(it))
            }
            list.add(listItem)

            getListItemSublistOrNull(it)?.let {
                val sublistIndex = currentIndex.subIndex()
                val sublist = createListNode(visitor, providerContext, it, 10f, sublistIndex)
                list.add(sublist)
            }

            currentIndex = currentIndex.nextIndex()
        }
        return list
    }


    fun getListItemParagraph(node: ASTNode): ASTNode =
        getChildNode(node, PARAGRAPH)

    fun getListItemSublistOrNull(node: ASTNode): ASTNode? =
        getChildNodeOrNull(node, ORDERED_LIST) ?: getChildNodeOrNull(node, UNORDERED_LIST)


    fun getListItems(node: ASTNode) =
        node.children.filter { it.type == LIST_ITEM }
}

data class ListItemIndex(val indeces: List<Int> = listOf(1)) {
    fun nextIndex():ListItemIndex {
        val newLast = indeces.last()+1
        val newIndeces = indeces.subList(0, indeces.size-1) + newLast
        return ListItemIndex(newIndeces)
    }

    fun subIndex() = ListItemIndex(indeces + 1)
}

interface ListItemSymbolFactory {
    fun createSymbolFor(itemIndex: ListItemIndex): String
}

class ArabicNumberSymbolFactory : ListItemSymbolFactory {
    override fun createSymbolFor(itemIndex: ListItemIndex): String =
        itemIndex.indeces.map { it.toString() }.joinToString(".", postfix = ". ")

}

class RepeatingSymbolFactory(val symbolPool: List<String> = listOf("\u2022 ", "- ")) : ListItemSymbolFactory {
    override fun createSymbolFor(itemIndex: ListItemIndex): String =
        symbolPool[(itemIndex.indeces.size-1) % symbolPool.size]

}