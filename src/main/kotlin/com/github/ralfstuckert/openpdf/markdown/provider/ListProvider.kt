package com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.provider

import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.*
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.PdfRenderContextKeys.COLOR
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.PdfRenderContextKeys.LINE_THICKNESS
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.PdfRenderContextKeys.LIST_INDEX_ITERATOR_FACTORY
import com.lowagie.text.Anchor
import com.lowagie.text.Chunk
import com.lowagie.text.ListItem
import com.lowagie.text.factories.RomanAlphabetFactory
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

    val defaultOrdedListIndexIteratorFactory = ListIndexIteratorFactory { listLevel: Int, parentPrefix: String ->
        ArabicNumberIndexIterator(listLevel, parentPrefix)
    }
    val defaultUnordedListIndexIteratorFactory = ListIndexIteratorFactory { listLevel: Int, parentPrefix: String ->
        RepeatingSymbolIndexIterator(listLevel, parentPrefix)
    }

    override fun setupDefaultRenderContexts(registry: ElementProviderRenderContextRegistry) {
        registry.registerRenderContextFunction(ORDERED_LIST_RENDER_CONTEXT_KEY) {
            derive {
                this[LIST_INDEX_ITERATOR_FACTORY] = defaultOrdedListIndexIteratorFactory
            }
        }
        registry.registerRenderContextFunction(UNORDERED_LIST_RENDER_CONTEXT_KEY) {
            derive {
                this[LIST_INDEX_ITERATOR_FACTORY] = defaultUnordedListIndexIteratorFactory
            }
        }
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
        listLevel: Int = 0,
        listItemPrefix: String = "",
        indentation: Float = 0f,
    ): com.lowagie.text.List {
        val renderContext = getRenderContext(providerContext, node)
        val listIndexIterator = getListIndexIterator(renderContext, node, listLevel, listItemPrefix)

        val list = com.lowagie.text.List(false, false).apply {
            indentationLeft = indentation
        }

        val listItems = getListItems(node)
        listItems.forEach {
            val listItemSymbol = listIndexIterator.nextIndex()
            val listItem = ListItem().apply {
                listSymbol = Chunk(listItemSymbol).applyPdfRenderContext(renderContext)
                visitor.visitChildren(this, renderContext, getListItemParagraph(it))
            }
            list.add(listItem)

            getListItemSublistOrNull(it)?.let {
                val sublistPrefix = listIndexIterator.sublistPrefix()
                val sublist = createListNode(visitor, providerContext, it, listLevel + 1, sublistPrefix, 10f)
                list.add(sublist)
            }
        }
        return list
    }

    fun getRenderContext(providerContext: ElementProviderContext, node: ASTNode) = when (node.type) {
        ORDERED_LIST -> providerContext.deriveRenderContext(ORDERED_LIST_RENDER_CONTEXT_KEY)
        else -> providerContext.deriveRenderContext(UNORDERED_LIST_RENDER_CONTEXT_KEY)
    }

    fun getListIndexIterator(
        context: PdfRenderContext, node: ASTNode,
        listLevel: Int, listItemPrefix: String
    ) = when (node.type) {
        ORDERED_LIST -> context[LIST_INDEX_ITERATOR_FACTORY] ?: defaultOrdedListIndexIteratorFactory
        else -> context[LIST_INDEX_ITERATOR_FACTORY] ?: defaultUnordedListIndexIteratorFactory
    }.createListIndexIterator(listLevel, listItemPrefix)

    fun getListItemParagraph(node: ASTNode): ASTNode =
        getChildNode(node, PARAGRAPH)

    fun getListItemSublistOrNull(node: ASTNode): ASTNode? =
        getChildNodeOrNull(node, ORDERED_LIST) ?: getChildNodeOrNull(node, UNORDERED_LIST)


    fun getListItems(node: ASTNode) =
        node.children.filter { it.type == LIST_ITEM }
}


interface ListIndexIterator {
    fun nextIndex(): String

    fun sublistPrefix(): String
}

fun interface ListIndexIteratorFactory {
    fun createListIndexIterator(listLevel: Int, parentPrefix: String): ListIndexIterator
}


open class ArabicNumberIndexIterator(
    val listLevel: Int,
    val parentPrefix: String = ""
) : ListIndexIterator {

    private var index = 0

    override fun nextIndex(): String =
        indexString(++index)

    private fun indexString(value: Int) =
        listOf(parentPrefix, value.toString(), ". ")
            .joinToString("")


    override fun sublistPrefix(): String =
        indexString(index)

}

open class RepeatingSymbolIndexIterator(
    val listLevel: Int,
    val parentPrefix: String = "",
    val symbolPool: List<String> = listOf("\u2022 ", "- ")
) : ListIndexIterator {

    override fun nextIndex(): String =
        symbolPool[listLevel % symbolPool.size]

    override fun sublistPrefix(): String = ""

}

open class RomanAlphabetIndexIterator(
    val listLevel: Int,
    val parentPrefix: String = "",
    val lowercase: Boolean = true
) : ListIndexIterator {

    private var index = 0

    override fun nextIndex(): String = (index++).let { next ->
        RomanAlphabetFactory.getString(next+1, isLowercase(next)) + ") "
    }

    open fun isLowercase(index:Int) = lowercase

    override fun sublistPrefix(): String = ""

}

