package com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown

import com.lowagie.text.Element
import java.awt.Color

object PdfRenderContextKeys {
    val FONT_FAMILY = PdfRenderContextKey<Int>("FONT_FAMILY")
    val FONT_SIZE = PdfRenderContextKey<Float>("FONT_SIZE")
    val FONT_STYLE = PdfRenderContextKey<Int>("FONT_TYPE")
    val FONT_COLOR = PdfRenderContextKey<Color>("FONT_COLOR")

    val UNDERLINE_THICKNESS = PdfRenderContextKey<Float>("UNDERLINE_THICKNESS")

    val BORDER_WIDTH = PdfRenderContextKey<Float>("BORDER_WIDTH")
    val BORDER_COLOR = PdfRenderContextKey<Color>("BORDER_COLOR")

    val WIDTH_PERCENTAGE = PdfRenderContextKey<Float>("WIDTH_PERCENTAGE")
    val HORIZONTAL_ALIGNMENT = PdfRenderContextKey<HorizontalAlignment>("HORIZONTAL_ALIGNMENT")

    val WEIGHTED_WIDTHS_ENABLED = PdfRenderContextKey<Boolean>("WEIGHTED_WIDTHS")
    val COLSPAN_ENABLED = PdfRenderContextKey<Boolean>("COLSPAN_ENABLED")

}


enum class HorizontalAlignment {
    left, center, right
}
