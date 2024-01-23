package com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown

import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.provider.ListIndexIteratorFactory
import java.awt.Color

object PdfRenderContextKeys {
    val FONT_FAMILY = PdfRenderContextKey<Int>("FONT_FAMILY")
    val FONT_SIZE = PdfRenderContextKey<Float>("FONT_SIZE")
    val FONT_STYLE = PdfRenderContextKey<Int>("FONT_TYPE")
    val COLOR = PdfRenderContextKey<Color>("COLOR")
    val BACKGROUND_COLOR = PdfRenderContextKey<Color>("BACKGROUND_COLOR")

    val LINE_THICKNESS = PdfRenderContextKey<Float>("LINE_THICKNESS")

    val BORDER_WIDTH = PdfRenderContextKey<Float>("BORDER_WIDTH")
    val BORDER_COLOR = PdfRenderContextKey<Color>("BORDER_COLOR")

    val WIDTH_PERCENTAGE = PdfRenderContextKey<Float>("WIDTH_PERCENTAGE")
    val HORIZONTAL_ALIGNMENT = PdfRenderContextKey<HorizontalAlignment>("HORIZONTAL_ALIGNMENT")

    val PADDING_LEFT = PdfRenderContextKey<Float>("PADDING_LEFT")

    val WEIGHTED_WIDTHS_ENABLED = PdfRenderContextKey<Boolean>("WEIGHTED_WIDTHS")
    val COLSPAN_ENABLED = PdfRenderContextKey<Boolean>("COLSPAN_ENABLED")
    val PAGE_BREAK_ON_HORIZONTAL_RULE_ENABLED = PdfRenderContextKey<Boolean>("PAGE_BREAK_ON_HORIZONTAL_RULE_ENABLED")
    val LIST_INDEX_ITERATOR_FACTORY = PdfRenderContextKey<ListIndexIteratorFactory>("LIST_INDEX_ITERATOR_FACTORY")

}


enum class HorizontalAlignment {
    left, center, right
}
