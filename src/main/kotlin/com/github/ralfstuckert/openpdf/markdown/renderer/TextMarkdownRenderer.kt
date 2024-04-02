package com.github.ralfstuckert.openpdf.markdown.renderer

import com.github.ralfstuckert.openpdf.markdown.*
import com.github.ralfstuckert.openpdf.markdown.PdfRenderContextKeys.BACKGROUND_COLOR
import com.github.ralfstuckert.openpdf.markdown.PdfRenderContextKeys.LINE_THICKNESS
import com.lowagie.text.Chunk
import org.intellij.markdown.MarkdownTokenTypes.Companion.TEXT
import org.intellij.markdown.MarkdownTokenTypes.Companion.WHITE_SPACE
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode

class TextMarkdownRenderer: AbstractMarkdownRenderer() {

    override fun setupDefaultRenderContexts(registry: MarkdownRenderContextRegistry) {
        handledNodeTypes.forEach { elementType ->
            registry.registerRenderContextFunction(MarkdownRenderContextKey(elementType.name)) {
                this
            }
        }
    }

    override val handledNodeTypes = listOf(TEXT, WHITE_SPACE)

    override fun processNode(visitor: OpenPdfVisitor, rendererContext: MarkdownRendererContext, node: ASTNode) {
        checkNodeType(node)
        val textRenderContext = rendererContext.deriveRenderContext(MarkdownRenderContextKey(node.type.name))
        val chunk = Chunk(node.getTextInNode(rendererContext.markdownText).toString())
            .applyPdfRenderContext(textRenderContext)
        rendererContext.parentPdfElement.add(chunk)
    }

}

fun Chunk.applyPdfRenderContext(pdfRenderContext: PdfRenderContext):Chunk {
    font = pdfRenderContext.font

    pdfRenderContext[BACKGROUND_COLOR]?.let { backgroundColor ->
        setBackground(backgroundColor)
    }

    val underlineThickness = pdfRenderContext[LINE_THICKNESS] ?: 0f
    if (underlineThickness != 0f) {
        setUnderline(underlineThickness, -1.75f * underlineThickness)
    }
    return this
}

