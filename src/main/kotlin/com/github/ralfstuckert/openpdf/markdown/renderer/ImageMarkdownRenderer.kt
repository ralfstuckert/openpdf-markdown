package com.github.ralfstuckert.openpdf.markdown.renderer

import com.github.ralfstuckert.openpdf.markdown.MarkdownRendererContext
import com.github.ralfstuckert.openpdf.markdown.MarkdownRenderContextKey
import com.github.ralfstuckert.openpdf.markdown.MarkdownRenderContextRegistry
import com.github.ralfstuckert.openpdf.markdown.OpenPdfVisitor
import com.lowagie.text.Chunk
import com.lowagie.text.Image
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode
import java.net.URL


interface ImageProcessor {
    fun processImage(visitor: OpenPdfVisitor, rendererContext: MarkdownRendererContext, imageUrl:String, width:Float?=null, height:Float?=null)
}

class ImageMarkdownRenderer : ImageProcessor, AbstractMarkdownRenderer() {

    companion object {
        val IMAGE_RENDER_CONTEXT_KEY = MarkdownRenderContextKey(MarkdownElementTypes.IMAGE.name)
    }

    override fun setupDefaultRenderContexts(registry: MarkdownRenderContextRegistry) {
        registry.registerRenderContextFunction(IMAGE_RENDER_CONTEXT_KEY) {
            this
        }
    }
    override val handledNodeTypes = listOf(MarkdownElementTypes.IMAGE)

    override fun processNode(
        visitor: OpenPdfVisitor, rendererContext: MarkdownRendererContext, node: ASTNode
    ) {
        checkNodeType(node)
//        val alternativeText = getLinkTextNode(node).getTextInNode(markdownText).toString()
        val url = getLinkDestination(node).getTextInNode(rendererContext.markdownText).toString()
        processImage(visitor, rendererContext, url)
    }

    override fun processImage(
        visitor: OpenPdfVisitor,
        rendererContext: MarkdownRendererContext,
        imageUrl: String,
        width: Float?,
        height: Float?
    ) {
        val image = Image.getInstance(URL(imageUrl)).apply {
            when {
                width != null && height != null -> scaleAbsolute(width, height)
                width != null -> scalePercent(width * 100.0f / this.scaledWidth)
                height != null -> scalePercent(height * 100.0f / this.scaledHeight)
            }
        }
        val imageRenderContext = rendererContext.deriveRenderContext(IMAGE_RENDER_CONTEXT_KEY)
        val chunk = Chunk(image, 0f, 0f, true)
            .applyPdfRenderContext(imageRenderContext)
        rendererContext.parentPdfElement.add(chunk)
    }


    fun getLinkDestination(node: ASTNode): ASTNode =
        getChildNode(getInlineLink(node), MarkdownElementTypes.LINK_DESTINATION)

    fun getInlineLink(node: ASTNode): ASTNode =
        getChildNode(node, MarkdownElementTypes.INLINE_LINK)


}

