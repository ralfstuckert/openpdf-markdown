package com.github.ralfstuckert.openpdf.markdown

import com.github.ralfstuckert.openpdf.markdown.renderer.*
import org.intellij.markdown.IElementType


data class MarkdownRenderContextKey(val name: String)

typealias PdfRenderContextFunction = PdfRenderContext.() -> PdfRenderContext

interface MarkdownRenderContextRegistry {
    val defaultRenderContext:PdfRenderContext

    fun getRenderContextFunction(key: MarkdownRenderContextKey): PdfRenderContextFunction?

    fun registerRenderContextFunction(key: MarkdownRenderContextKey, extendExisting:Boolean = false, contextFunction: PdfRenderContextFunction)

}


class MarkdownRendererRegistry(override val defaultRenderContext:PdfRenderContext) : MarkdownRenderContextRegistry {

    private val rendererMap: MutableMap<IElementType, MarkdownRenderer> = mutableMapOf()
    private val renderContextFunctionMap: MutableMap<MarkdownRenderContextKey, PdfRenderContextFunction> = mutableMapOf()

    init {
        val renderers = listOf(
            TextMarkdownRenderer(),
            StrongMarkdownRenderer(),
            EmphasisMarkdownRenderer(),
            StrikethroughMarkdownRenderer(),
            InlineCodeMarkdownRenderer(),
            CodeBlockMarkdownRenderer(),
            EOLMarkdownRenderer(),
            HeaderMarkdownRenderer(),
            LinkMarkdownRenderer(),
            ImageMarkdownRenderer(),
            TableMarkdownRenderer(),
            HtmlTagMarkdownRenderer(),
            HorizontalRuleMarkdownRenderer(),
            ListMarkdownRenderer(),
            BlockquoteMarkdownRenderer()
        )
            .flatMap { renderer ->
                renderer.setupDefaultRenderContexts(this)
                renderer.handledNodeTypes.map { type -> type to renderer }
            }.toMap()
        rendererMap.putAll(renderers)
    }

    fun getMarkdownRendererFor(type: IElementType): MarkdownRenderer? =
        rendererMap[type]

    fun registerMarkdownRenderer(type: IElementType, renderer: MarkdownRenderer) {
        rendererMap[type] = renderer
    }

    override fun getRenderContextFunction(key: MarkdownRenderContextKey): PdfRenderContextFunction? =
        renderContextFunctionMap[key]

    override fun registerRenderContextFunction(key: MarkdownRenderContextKey, extendExisting:Boolean, contextFunction: PdfRenderContextFunction) {
        val existingFunction = getRenderContextFunction(key)
        val function = if (extendExisting && existingFunction != null) {
            existingFunction then contextFunction
        } else contextFunction
        renderContextFunctionMap[key] = function
    }

    infix fun PdfRenderContextFunction.then(other: PdfRenderContextFunction):PdfRenderContextFunction = {
        other(this@then(this))
    }

}

