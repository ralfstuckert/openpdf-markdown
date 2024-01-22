package com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown

import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.provider.*
import org.intellij.markdown.IElementType


data class ElementProviderRenderContextKey(val name: String)

typealias PdfRenderContextFunction = PdfRenderContext.() -> PdfRenderContext

interface ElementProviderRenderContextRegistry {
    val defaultRenderContext:PdfRenderContext

    fun getRenderContextFunction(key: ElementProviderRenderContextKey): PdfRenderContextFunction?

    fun registerRenderContextFunction(key: ElementProviderRenderContextKey, extendExisting:Boolean = false, contextFunction: PdfRenderContextFunction)

}


class ElementProviderRegistry(override val defaultRenderContext:PdfRenderContext) : ElementProviderRenderContextRegistry {

    private val providerMap: MutableMap<IElementType, ElementProvider> = mutableMapOf()
    private val renderContextFunctionMap: MutableMap<ElementProviderRenderContextKey, PdfRenderContextFunction> = mutableMapOf()

    init {
        val providers = listOf(
            TextProvider(),
            StrongProvider(),
            EmphasisProvider(),
            StrikethroughProvider(),
            InlineCodeProvider(),
            CodeBlockProvider(),
            EOLProvider(),
            HeaderProvider(),
            LinkProvider(),
            ImageProvider(),
            TableProvider(),
            HtmlTagProvider(),
            HorizontalRuleProvider(),
            ListProvider(),
            BlockquoteProvider()
        )
            .flatMap { provider ->
                provider.setupDefaultRenderContexts(this)
                provider.handledNodeTypes.map { type -> type to provider }
            }.toMap()
        providerMap.putAll(providers)
    }

    fun getProviderFor(type: IElementType): ElementProvider? =
        providerMap[type]

    fun registerProvider(type: IElementType, provider: ElementProvider) {
        providerMap[type] = provider
    }

    override fun getRenderContextFunction(key: ElementProviderRenderContextKey): PdfRenderContextFunction? =
        renderContextFunctionMap[key]

    override fun registerRenderContextFunction(key: ElementProviderRenderContextKey, extendExisting:Boolean, contextFunction: PdfRenderContextFunction) {
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

