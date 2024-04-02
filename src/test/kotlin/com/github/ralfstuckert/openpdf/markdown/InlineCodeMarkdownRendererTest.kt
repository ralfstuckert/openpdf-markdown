package com.github.ralfstuckert.openpdf.markdown

import com.github.ralfstuckert.openpdf.markdown.document.document
import com.github.ralfstuckert.openpdf.markdown.renderer.InlineCodeMarkdownRenderer
import com.lowagie.text.Font
import org.junit.jupiter.api.Test
import java.awt.Color
import java.io.File

class InlineCodeMarkdownRendererTest {

    @Test
    fun inlineCode() {
        val doc = document {
            markdown {
                +" This is some text with `inline **code** markdown`\n"
                +"""markdown `of **code** over
                   |linebreaks` works.
                   |
                """.trimMargin()

            }

            markdown {
                +"""# heading with `inline code markdown`
                    """
            }

            markdown {
                markdownRendererRegistry = MarkdownRendererRegistry(defaultRenderContext).apply {
                    registerRenderContextFunction(InlineCodeMarkdownRenderer.INLINE_CODE_RENDER_CONTEXT_KEY, true) {
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

