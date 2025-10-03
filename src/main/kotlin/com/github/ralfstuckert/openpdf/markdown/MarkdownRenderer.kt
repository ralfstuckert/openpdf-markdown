package com.github.ralfstuckert.openpdf.markdown

import org.intellij.markdown.IElementType
import org.intellij.markdown.ast.ASTNode
import org.openpdf.text.TextElementArray


interface MarkdownRenderer {

    val handledNodeTypes: List<IElementType>

    fun setupDefaultRenderContexts(registry: MarkdownRenderContextRegistry)

    fun processNode(
        visitor: OpenPdfVisitor, rendererContext: MarkdownRendererContext, node: ASTNode
    )

}


data class MarkdownRendererContext(
    val markdownText: String,
    val parentPdfElement: TextElementArray,
    val renderContext: PdfRenderContext,
    val renderContextRegistry:MarkdownRenderContextRegistry
) {

    fun deriveRenderContext(key: MarkdownRenderContextKey): PdfRenderContext =
        renderContextRegistry.getRenderContextFunction(key)?.invoke(renderContext) ?: renderContext

}