package com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.provider

import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.*
import com.lowagie.text.*
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode
import java.net.URL


class ImageProvider : AbstractElementProvider() {

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
        val destination = getLinkDestination(node).getTextInNode(providerContext.markdownText).toString()
        val (url, width, height) = getUrlWidthHeight(destination)

        val image = Image.getInstance(URL(url)).apply {
            scaleToFit(
                width?.toFloat() ?: this.width,
                height?.toFloat() ?: this.height
            )
        }
        val imageRenderContext = providerContext.deriveRenderContext(IMAGE_RENDER_CONTEXT_KEY)
        val chunk = Chunk(image, 0f, 0f, true)
            .applyPdfRenderContext(imageRenderContext)
        providerContext.parentPdfElement.add(chunk)
    }

    fun getUrlWidthHeight(destinationText: String): Triple<String, Int?, Int?> {
        val regex = Regex("""([^|]*)(\|(.*))?""")

        val result = regex.find(destinationText)
        val url = result?.groupValues?.get(1) ?: destinationText
        val styles = result?.groupValues?.get(3)
        val stylesMap = styles?.split(',')?.map {
            val keyValuePair = it.split('=')
            if (keyValuePair.size == 2) keyValuePair[0] to keyValuePair[1] else null
        }?.filterNotNull()?.toMap() ?: emptyMap()

        val width = stylesMap["width"]?.toIntOrNull()
        val height = stylesMap["height"]?.toIntOrNull()

        return Triple(url, width, height)
    }

    fun getLinkTextNode(node: ASTNode): ASTNode =
        getChildNode(getInlineLink(node), MarkdownElementTypes.LINK_TEXT)

    fun getLinkDestination(node: ASTNode): ASTNode =
        getChildNode(getInlineLink(node), MarkdownElementTypes.LINK_DESTINATION)

    fun getInlineLink(node: ASTNode): ASTNode =
        getChildNode(node, MarkdownElementTypes.INLINE_LINK)


}

fun main() {
    val regex = Regex("""(.*)(|.*)?""")

    val text = "https://user-images.githubusercontent.com|width=200,height=100"
    val result = regex.find(text)
    val url = result?.groupValues?.get(1)
    val width = result?.groupValues?.get(2)
    println(url)
    println(width)
}