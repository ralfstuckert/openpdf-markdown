package com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown

import java.awt.Color

object PdfRenderContextKeys {
    val FONT_FAMILY = PdfRenderContextKey<Int>("FONT_FAMILY")
    val FONT_SIZE = PdfRenderContextKey<Float>("FONT_SIZE")
    val FONT_STYLE = PdfRenderContextKey<Int>("FONT_TYPE")
    val FONT_COLOR = PdfRenderContextKey<Color>("FONT_COLOR")

    val UNDERLINE_THICKNESS = PdfRenderContextKey<Float>("UNDERLINE_THICKNESS")
}

object DefaultPdfRenderContextKeys {
    val DEFAULT_FONT_FAMILY = PdfRenderContextKey<Int>("DEFAULT_FONT_FAMILY")
    val DEFAULT_FONT_SIZE = PdfRenderContextKey<Float>("DEFAULT_FONT_SIZE")
    val DEFAULT_FONT_STYLE = PdfRenderContextKey<Int>("DEFAULT_FONT_TYPE")
    val DEFAULT_FONT_COLOR = PdfRenderContextKey<Color>("DEFAULT_FONT_COLOR")

    val DEFAULT_LINK_UNDERLINE_THICKNESS_FACTOR = PdfRenderContextKey<Float>("DEFAULT_LINK_UNDERLINE_THICKNESS_FACTOR")
    val DEFAULT_LINK_COLOR = PdfRenderContextKey<Color>("DEFAULT_LINK_COLOR")
}


