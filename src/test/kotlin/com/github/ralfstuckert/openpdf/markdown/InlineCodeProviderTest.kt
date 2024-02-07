package com.github.ralfstuckert.openpdf.markdown

import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.*
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.document.document
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.provider.InlineCodeProvider
import com.lowagie.text.Font
import org.junit.jupiter.api.Test
import java.awt.Color

class InlineCodeProviderTest {

    @Test
    fun inlineCode() {
        val doc = document {
            markup {
                +" This is some text with `inline **code** markup`\n"
                +"""markup `of **code** over
                   |linebreaks` works.
                   |
                """.trimMargin()

            }

            markup {
                +"""# heading with `inline code markup`
                    """
            }

            markup {
                elementProviderRegistry = ElementProviderRegistry(defaultRenderContext).apply {
                    registerRenderContextFunction(InlineCodeProvider.INLINE_CODE_RENDER_CONTEXT_KEY, true) {
                        val parentRenderContext = this
                        derive {
                            this[PdfRenderContextKeys.COLOR] = Color.blue
                            this[PdfRenderContextKeys.FONT_STYLE] = Font.BOLD
                            this[PdfRenderContextKeys.FONT_SIZE] = parentRenderContext.fontSize * 0.9f
                        }
                    }
                }
                +"let's change the rendering of inline code `to blue bold with a slightly smaller font` or whatever you want"
            }
        }
//        File("inlinecode.pdf").writeBytes(doc)
        doc shouldEqual "inlinecode.pdf"


    }
}

