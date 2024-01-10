package com.github.ralfstuckert.openpdf.markdown

import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.ElementProviderRegistry
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.PdfRenderContextKeys
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.defaultRenderContext
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.derive
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.provider.CodeSpanProvider
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.provider.StrongProvider.Companion.STRONG_RENDER_CONTEXT_KEY
import com.lowagie.text.Font
import org.junit.jupiter.api.Test
import rst.pdftools.compare.assertPdfEquals
import java.awt.Color
import java.io.File

class CodeSpanProviderTest {

    @Test
    fun codespan() {
        val doc = document {
            paragraph {
                +" This is some text with `inline **code** markup`\n"
                +"""markup `of **code** over
                   |linebreaks` works.
                   |
                """.trimMargin()

            }

            paragraph {
                +"""# heading with `inline code markup`
                    """
            }

            paragraph {
                elementProviderRegistry = ElementProviderRegistry(defaultRenderContext).apply {
                    registerRenderContextFunction(CodeSpanProvider.CODE_SPAN_RENDER_CONTEXT_KEY, true) {
                        derive {
                            this[PdfRenderContextKeys.FONT_COLOR] = Color.blue
                            this[PdfRenderContextKeys.FONT_STYLE] = Font.BOLD
                            this[PdfRenderContextKeys.FONT_SIZE] = 9f
                        }
                    }
                }
                +"let's change the rendering of inline code `to blue bold with size 9` or whatever you want"
            }
        }
        File("codespan.pdf").writeBytes(doc)
        doc shouldEqual "codespan.pdf"


    }
}

