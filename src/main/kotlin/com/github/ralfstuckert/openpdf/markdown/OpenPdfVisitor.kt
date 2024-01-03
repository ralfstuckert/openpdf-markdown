package com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown

import com.lowagie.text.TextElementArray
import org.intellij.markdown.MarkdownTokenTypes.Companion.WHITE_SPACE
import org.intellij.markdown.ast.ASTNode

class OpenPdfVisitor(val markdownText:String, val registry: ElementProviderRegistry) {

    fun visitNode(parentPdfElement: TextElementArray, pdfRenderContext: PdfRenderContext, node: ASTNode) {
        val nodeProcessor = registry.getProviderFor(node.type)
        if (nodeProcessor != null) {
            nodeProcessor.processNode(
                this,
                ElementProviderContext(markdownText, parentPdfElement, pdfRenderContext, registry),
                node)
        } else {
            visitChildren(parentPdfElement, pdfRenderContext, node)
        }
    }

    fun visitChildren(parentPdfElement: TextElementArray, pdfRenderContext: PdfRenderContext, node: ASTNode, skipLeadingWhitespace:Boolean=false) {
        for (child in getChildren(node,skipLeadingWhitespace)) {
            visitNode(parentPdfElement, pdfRenderContext, child)
        }
    }

    fun getChildren(node: ASTNode, skipLeadingWhitespace:Boolean):List<ASTNode> =
        if (skipLeadingWhitespace) {
            node.children.dropWhile { it.type == WHITE_SPACE }
        } else node.children


}