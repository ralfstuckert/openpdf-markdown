package com.github.ralfstuckert.openpdf.markdown.renderer

import com.github.ralfstuckert.openpdf.markdown.MarkdownRendererContext
import com.github.ralfstuckert.openpdf.markdown.MarkdownRenderContextKey
import com.github.ralfstuckert.openpdf.markdown.MarkdownRenderContextRegistry
import com.github.ralfstuckert.openpdf.markdown.OpenPdfVisitor
import com.lowagie.text.Chunk
import org.intellij.markdown.MarkdownTokenTypes.Companion.EOL
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode

class EOLMarkdownRenderer: AbstractMarkdownRenderer() {

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
        val chunk = Chunk(node.getTextInNode(rendererContext.markdownText).toString()).applyPdfRenderContext(eolRenderContext)
        rendererContext.parentPdfElement.add(chunk)
    }

}