package com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.provider

import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.ElementProviderContext
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.ElementProviderRenderContextKey
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.ElementProviderRenderContextRegistry
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.OpenPdfVisitor
import com.lowagie.text.Chunk
import com.lowagie.text.Image
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode
import java.net.URL


interface ImageProcessor {
    fun processImage(visitor: OpenPdfVisitor, providerContext: ElementProviderContext, imageUrl:String, width:Float?=null, height:Float?=null)
}

class ImageProvider : ImageProcessor, AbstractElementProvider() {

    companion object {
        val IMAGE_RENDER_CONTEXT_KEY = ElementProviderRenderContextKey(MarkdownElementTypes.IMAGE.name)
    }

    override fun setupDefaultRenderContexts(registry: ElementProviderRenderContextRegistry) {
        registry.registerRenderContextFunction(IMAGE_RENDER_CONTEXT_KEY) {
            this
        }
    }
    override val handledNodeTypes = listOf(MarkdownElementTypes.IMAGE)

    override fun processNode(
        visitor: OpenPdfVisitor, providerContext: ElementProviderContext, node: ASTNode
    ) {
        checkNodeType(node)
//        val alternativeText = getLinkTextNode(node).getTextInNode(markdownText).toString()
        val url = getLinkDestination(node).getTextInNode(providerContext.markdownText).toString()
        processImage(visitor, providerContext, url)
    }

    override fun processImage(
        visitor: OpenPdfVisitor,
        providerContext: ElementProviderContext,
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
        val imageRenderContext = providerContext.deriveRenderContext(IMAGE_RENDER_CONTEXT_KEY)
        val chunk = Chunk(image, 0f, 0f, true)
            .applyPdfRenderContext(imageRenderContext)
        providerContext.parentPdfElement.add(chunk)
    }


    fun getLinkDestination(node: ASTNode): ASTNode =
        getChildNode(getInlineLink(node), MarkdownElementTypes.LINK_DESTINATION)

    fun getInlineLink(node: ASTNode): ASTNode =
        getChildNode(node, MarkdownElementTypes.INLINE_LINK)


}

