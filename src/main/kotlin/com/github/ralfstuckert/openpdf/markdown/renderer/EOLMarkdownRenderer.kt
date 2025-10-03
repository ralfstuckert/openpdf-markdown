package com.github.ralfstuckert.openpdf.markdown.renderer

import com.github.ralfstuckert.openpdf.markdown.MarkdownRendererContext
import com.github.ralfstuckert.openpdf.markdown.MarkdownRenderContextKey
import com.github.ralfstuckert.openpdf.markdown.MarkdownRenderContextRegistry
import com.github.ralfstuckert.openpdf.markdown.OpenPdfVisitor
import org.openpdf.text.Chunk
import org.openpdf.text.List
import org.openpdf.text.Phrase
import org.openpdf.text.pdf.PdfPTable
import org.intellij.markdown.MarkdownTokenTypes.Companion.EOL
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode

class EOLMarkdownRenderer : AbstractMarkdownRenderer() {

    companion object {
        val EOL_RENDER_CONTEXT_KEY = MarkdownRenderContextKey(EOL.name)
    }

    override val handledNodeTypes = listOf(EOL)

    override fun setupDefaultRenderContexts(registry: MarkdownRenderContextRegistry) {
        registry.registerRenderContextFunction(EOL_RENDER_CONTEXT_KEY) {
            this
        }
    }

    override fun processNode(visitor: OpenPdfVisitor, rendererContext: MarkdownRendererContext, node: ASTNode) {
        checkNodeType(node)
        val eolRenderContext = rendererContext.deriveRenderContext(EOL_RENDER_CONTEXT_KEY)
        val chunk = if (omitEOL(rendererContext))
            Chunk("")
        else
            Chunk(node.getTextInNode(rendererContext.markdownText).toString()).applyPdfRenderContext(eolRenderContext)
        rendererContext.parentPdfElement.add(chunk)
    }

    fun omitEOL(rendererContext: MarkdownRendererContext): Boolean {
        val parent = rendererContext.parentPdfElement
        if (parent !is Phrase || parent.size == 0) {
            return false
        }
        val last = parent.elementAt(parent.size - 1)
        return (last is PdfPTable || last is List)
    }
}