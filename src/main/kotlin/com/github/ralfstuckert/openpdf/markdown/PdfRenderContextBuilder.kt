package com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown


interface PdfRenderContextBuilderDsl {
    operator fun <T : Any> set(key: PdfRenderContextKey<T>, value: T)
}

class PdfRenderContextBuilder():PdfRenderContextBuilderDsl {
    private val contextMap: MutableMap<PdfRenderContextKey<*>, Any> = mutableMapOf()

    override operator fun <T : Any> set(key: PdfRenderContextKey<T>, value: T) =
        contextMap.set(key, value)

    fun build():PdfRenderContext = PdfRenderContext(contextMap)
}

fun pdfRenderContext(dsl:PdfRenderContextBuilderDsl.() -> Unit):PdfRenderContext {
    val builder = PdfRenderContextBuilder()
    builder.dsl()
    return builder.build()
}

fun PdfRenderContext.derive(dsl:PdfRenderContextBuilderDsl.() -> Unit):PdfRenderContext =
    this + pdfRenderContext(dsl)

