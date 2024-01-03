package com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown

import com.lowagie.text.Font
import java.awt.Color

object PdfRenderContextDefaults {
    val fontFamily = Font.HELVETICA
    val fontSize = 12f
    val fontStyle = Font.NORMAL
    val fontColor = Color.black

    val linkUnderlineThicknessFactor = 0.07f
    val linkColor = Color.black
}


val defaultRenderContext = pdfRenderContext {
    this[DefaultPdfRenderContextKeys.DEFAULT_FONT_FAMILY] = PdfRenderContextDefaults.fontFamily
    this[DefaultPdfRenderContextKeys.DEFAULT_FONT_SIZE] = PdfRenderContextDefaults.fontSize
    this[DefaultPdfRenderContextKeys.DEFAULT_FONT_STYLE] = PdfRenderContextDefaults.fontStyle

    this[DefaultPdfRenderContextKeys.DEFAULT_FONT_COLOR] = PdfRenderContextDefaults.fontColor

    this[DefaultPdfRenderContextKeys.DEFAULT_LINK_UNDERLINE_THICKNESS_FACTOR] = PdfRenderContextDefaults.linkUnderlineThicknessFactor
    this[DefaultPdfRenderContextKeys.DEFAULT_LINK_COLOR] = PdfRenderContextDefaults.linkColor
}
