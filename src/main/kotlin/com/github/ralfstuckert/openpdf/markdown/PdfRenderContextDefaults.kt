package com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown

import com.lowagie.text.Font

object PdfRenderContextDefaults {
    val fontFamily = Font.HELVETICA
    val fontSize = 12f
    val fontStyle = Font.NORMAL
}

val defaultRenderContext = pdfRenderContext {
    this[DefaultPdfRenderContextKeys.DEFAULT_FONT_FAMILY] = PdfRenderContextDefaults.fontFamily
    this[DefaultPdfRenderContextKeys.DEFAULT_FONT_SIZE] = PdfRenderContextDefaults.fontSize
    this[DefaultPdfRenderContextKeys.DEFAULT_FONT_STYLE] = PdfRenderContextDefaults.fontStyle
}
