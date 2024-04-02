package com.github.ralfstuckert.openpdf.markdown

import com.github.ralfstuckert.openpdf.markdown.document.document
import com.github.ralfstuckert.openpdf.markdown.renderer.EmphasisMarkdownRenderer.Companion.EMPHASIS_RENDER_CONTEXT_KEY
import com.lowagie.text.Font
import org.junit.jupiter.api.Test
import java.awt.Color
import java.io.File

class EmphasisMarkdownRendererTest {

    @Test
    fun emphasis() {
        val doc = document {
            markdown {
                +" This is some text with _emphasis markdown_\n"
                +"""markdown *over
                    | linebreaks* works.
                    | 
                """.trimMargin()
            }

            markdown {
                +"""# heading with _emphasis markdown_
                    """
            }

            markdown {
                markdownRendererRegistry = MarkdownRendererRegistry(defaultRenderContext).apply {
                    registerRenderContextFunction(EMPHASIS_RENDER_CONTEXT_KEY, true) {
                        derive {
                            this[PdfRenderContextKeys.COLOR] = Color.blue
                            this[PdfRenderContextKeys.FONT_FAMILY] = Font.TIMES_ROMAN
                            this[PdfRenderContextKeys.FONT_SIZE] = 17f
                        }
                    }
                }
                +"let's change the rendering of emphasis _to blue times roman with size 17_ or whatever you want"
            }
        }
//        File("emphasis.pdf").writeBytes(doc)
        doc shouldEqual "emphasis.pdf"


    }
}

