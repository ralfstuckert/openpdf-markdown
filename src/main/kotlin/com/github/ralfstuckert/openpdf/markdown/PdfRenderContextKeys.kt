package com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown

object PdfRenderContextKeys {
    val FONT_FAMILY = PdfRenderContextKey<Int>("FONT_FAMILY")
    val FONT_SIZE = PdfRenderContextKey<Float>("FONT_SIZE")
    val FONT_STYLE = PdfRenderContextKey<Int>("FONT_TYPE")

    val UNDERLINE_THICKNESS = PdfRenderContextKey<Float>("UNDERLINE_THICKNESS")
}

object DefaultPdfRenderContextKeys {
    val DEFAULT_FONT_FAMILY = PdfRenderContextKey<Int>("DEFAULT_FONT_FAMILY")
    val DEFAULT_FONT_SIZE = PdfRenderContextKey<Float>("DEFAULT_FONT_SIZE")
    val DEFAULT_FONT_STYLE = PdfRenderContextKey<Int>("DEFAULT_FONT_TYPE")
}


