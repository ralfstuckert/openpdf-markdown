package com.github.ralfstuckert.openpdf.markdown

import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.PdfRenderContextKeys.FONT_FAMILY
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.PdfRenderContextKeys.FONT_SIZE
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.pdfRenderContext
import com.lowagie.text.Font
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class PdfRenderContextTest {

    @Test
    fun dsl() {
        val context = pdfRenderContext {
            this[FONT_FAMILY] = Font.COURIER
            this[FONT_SIZE] = 12f
        }
        context.keys().toList().size shouldBe 2
        context[FONT_FAMILY] shouldBe Font.COURIER
        context[FONT_SIZE] shouldBe 12f
    }

}