package com.github.ralfstuckert.openpdf.markdown

import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.*
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.provider.CodeSpanProvider
import com.lowagie.text.Font
import org.junit.jupiter.api.Test
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
                        val parentRenderContext = this
                        derive {
                            this[PdfRenderContextKeys.FONT_COLOR] = Color.blue
                            this[PdfRenderContextKeys.FONT_STYLE] = Font.BOLD
                            this[PdfRenderContextKeys.FONT_SIZE] = parentRenderContext.fontSize * 0.9f
                        }
                    }
                }
                +"let's change the rendering of inline code `to blue bold with a slightly smaller font` or whatever you want"
            }
        }
        File("codespan.pdf").writeBytes(doc)
        doc shouldEqual "codespan.pdf"


    }
}

