package com.github.ralfstuckert.openpdf.markdown

import com.github.ralfstuckert.openpdf.markdown.document.document
import com.github.ralfstuckert.openpdf.markdown.renderer.StrongMarkdownRenderer.Companion.STRONG_RENDER_CONTEXT_KEY
import com.lowagie.text.Font
import org.junit.jupiter.api.Test
import java.awt.Color
import java.io.File

class StrongMarkdownRendererTest {

    @Test
    fun strong() {
        val doc = document {
            markdown {
                +" This is some text with **strong markdown**\n"
                +"""markdown __over
                    | linebreaks__ works.
                    | 
                """.trimMargin()
            }

            markdown {
                +"""# heading with **strong markdown**
                    """
            }

            markdown {
                markdownRendererRegistry = MarkdownRendererRegistry(defaultRenderContext).apply {
                    registerRenderContextFunction(STRONG_RENDER_CONTEXT_KEY, true) {
                        derive {
                            this[PdfRenderContextKeys.COLOR] = Color.blue
                            this[PdfRenderContextKeys.FONT_FAMILY] = Font.TIMES_ROMAN
                            this[PdfRenderContextKeys.FONT_SIZE] = 17f
                        }
                    }
                }
                +"let's change the rendering of strong **to blue times roman with size 17** or whatever you want"
            }
        }
//        File("strong.pdf").writeBytes(doc)
        doc shouldEqual "strong.pdf"


    }
}

