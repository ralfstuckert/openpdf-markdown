package com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown

import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.DefaultPdfRenderContextKeys.DEFAULT_FONT_COLOR
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.DefaultPdfRenderContextKeys.DEFAULT_FONT_FAMILY
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.DefaultPdfRenderContextKeys.DEFAULT_FONT_SIZE
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.DefaultPdfRenderContextKeys.DEFAULT_FONT_STYLE
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.PdfRenderContextKeys.FONT_COLOR
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.PdfRenderContextKeys.FONT_FAMILY
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.PdfRenderContextKeys.FONT_SIZE
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.PdfRenderContextKeys.FONT_STYLE
import com.lowagie.text.Font


data class PdfRenderContextKey<T : Any>(val name: String, val type: Class<T>)

inline fun <reified T : Any> PdfRenderContextKey(name: String) =
    PdfRenderContextKey(name, T::class.java)


data class PdfRenderContext internal constructor(internal val contextMap: Map<PdfRenderContextKey<*>, Any>) {

    @Suppress("NON_PUBLIC_CALL_FROM_PUBLIC_INLINE")
    inline operator fun <reified T : Any> get(key: PdfRenderContextKey<T>): T? =
        when (val value = contextMap[key]) {
            null -> null
            else -> {
                check(value is T)
                value
            }
        }

    operator fun plus(other: PdfRenderContext): PdfRenderContext =
        PdfRenderContext(contextMap + other.contextMap)

    fun keys(): Iterable<PdfRenderContextKey<*>> =
        contextMap.keys
}


val PdfRenderContext.fontSize
    get() = this[FONT_SIZE] ?: this[DEFAULT_FONT_SIZE] ?: PdfRenderContextDefaults.fontSize
val PdfRenderContext.fontFamily
    get() = this[FONT_FAMILY] ?: this[DEFAULT_FONT_FAMILY] ?: PdfRenderContextDefaults.fontFamily
val PdfRenderContext.fontStyle
    get() = this[FONT_STYLE] ?: this[DEFAULT_FONT_STYLE] ?: PdfRenderContextDefaults.fontStyle
val PdfRenderContext.fontColor
    get() = this[FONT_COLOR] ?: this[DEFAULT_FONT_COLOR] ?: PdfRenderContextDefaults.fontColor
val PdfRenderContext.font: Font
    get() = Font(fontFamily, fontSize, fontStyle, fontColor)
