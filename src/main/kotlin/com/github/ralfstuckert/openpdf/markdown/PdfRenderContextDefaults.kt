package com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown

import com.lowagie.text.Font
import java.awt.Color

object PdfRenderContextDefaults {
    val fontFamily = Font.HELVETICA
    val fontSize = 12f
    val fontStyle = Font.NORMAL
    val fontColor = Color.black
}


val defaultRenderContext = pdfRenderContext {
    this[PdfRenderContextKeys.FONT_FAMILY] = PdfRenderContextDefaults.fontFamily
    this[PdfRenderContextKeys.FONT_SIZE] = PdfRenderContextDefaults.fontSize
    this[PdfRenderContextKeys.FONT_STYLE] = PdfRenderContextDefaults.fontStyle

    this[PdfRenderContextKeys.FONT_COLOR] = PdfRenderContextDefaults.fontColor
}
