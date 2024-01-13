package com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.provider

import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.ElementProviderContext
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.ElementProviderRenderContextKey
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.ElementProviderRenderContextRegistry
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.OpenPdfVisitor
import com.lowagie.text.Chunk
import org.intellij.markdown.MarkdownTokenTypes.Companion.EOL
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode

class EOLProvider: AbstractElementProvider() {

    companion object {
        val EOL_RENDER_CONTEXT_KEY = ElementProviderRenderContextKey(EOL.name)
    }

    override val handledNodeTypes = listOf(EOL)

    override fun setupDefaultRenderContexts(registry: ElementProviderRenderContextRegistry) {
        registry.registerRenderContextFunction(EOL_RENDER_CONTEXT_KEY) {
            this
        }
    }

    override fun processNode(visitor: OpenPdfVisitor, providerContext: ElementProviderContext, node: ASTNode) {
        checkNodeType(node)
        val eolRenderContext = providerContext.deriveRenderContext(EOL_RENDER_CONTEXT_KEY)
        val chunk = Chunk(node.getTextInNode(providerContext.markdownText).toString()).applyPdfRenderContext(eolRenderContext)
        providerContext.parentPdfElement.add(chunk)
    }

}