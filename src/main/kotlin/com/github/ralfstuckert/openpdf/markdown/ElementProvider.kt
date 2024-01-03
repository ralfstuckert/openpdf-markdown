package com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown

import com.lowagie.text.TextElementArray
import org.intellij.markdown.IElementType
import org.intellij.markdown.ast.ASTNode


interface ElementProvider {

    val handledNodeTypes: List<IElementType>

    fun setupDefaultRenderContexts(registry: ElementProviderRenderContextRegistry)

    fun processNode(
        visitor: OpenPdfVisitor, providerContext: ElementProviderContext, node: ASTNode
    )

}


data class ElementProviderContext(
    val markdownText: String,
    val parentPdfElement: TextElementArray,
    val renderContext: PdfRenderContext,
    val renderContextRegistry:ElementProviderRenderContextRegistry
) {

    fun deriveRenderContext(key: ElementProviderRenderContextKey): PdfRenderContext =
        renderContextRegistry.getRenderContextFunction(key)?.invoke(renderContext) ?: renderContext

}