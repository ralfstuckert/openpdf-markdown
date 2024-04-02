package com.github.ralfstuckert.openpdf.markdown

import com.lowagie.text.TextElementArray
import org.intellij.markdown.MarkdownElementTypes.BLOCK_QUOTE
import org.intellij.markdown.MarkdownTokenTypes.Companion.EOL
import org.intellij.markdown.MarkdownTokenTypes.Companion.WHITE_SPACE
import org.intellij.markdown.ast.ASTNode

class OpenPdfVisitor(val markdownText:String, val registry: MarkdownRendererRegistry) {

    fun visitNode(parentPdfElement: TextElementArray, pdfRenderContext: PdfRenderContext, node: ASTNode) {
        val nodeProcessor = registry.getMarkdownRendererFor(node.type)
        if (nodeProcessor != null) {
            nodeProcessor.processNode(
                this,
                MarkdownRendererContext(markdownText, parentPdfElement, pdfRenderContext, registry),
                node)
        } else {
            visitChildren(parentPdfElement, pdfRenderContext, node, trim=true)
        }
    }

    fun visitChildren(parentPdfElement: TextElementArray, pdfRenderContext: PdfRenderContext, node: ASTNode, trim:Boolean=false) {
        for (child in getChildren(node,trim)) {
            visitNode(parentPdfElement, pdfRenderContext, child)
        }
    }

}

fun getChildren(node: ASTNode, trimWhitespace:Boolean):List<ASTNode> =
   node.children.filterChildren(trimWhitespace).fixEolAfterBlockQuote()

fun List<ASTNode>.filterChildren(trimWhitespace:Boolean) =
    if (trimWhitespace) {
        this
            .dropWhile { it.type == WHITE_SPACE }
            .dropLastWhile { it.type == WHITE_SPACE }
    } else this

fun List<ASTNode>.fixEolAfterBlockQuote() =
    this.fold(mutableListOf<ASTNode>()) { result, node ->
        if (node.type != EOL || result.lastOrNull()?.type != BLOCK_QUOTE) {
            result.add(node)
        }
        result
    }
