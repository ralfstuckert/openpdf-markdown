package com.github.ralfstuckert.openpdf.markdown

import com.github.ralfstuckert.openpdf.markdown.document.document
import com.github.ralfstuckert.openpdf.markdown.renderer.BlockquoteMarkdownRenderer.Companion.BlOCKQUOTE_RENDER_CONTEXT_KEY
import com.lowagie.text.Font
import org.junit.jupiter.api.Test
import java.io.File

class BlockquoteMarkdownRendererTest {

    @Test
    fun blockquote() {
        val doc = document {
            markdown {
                +"This is a simple blockquote"
                val blockquote = """
                    |>here comes a blockquote
                    |>another line  
                    |
                    |after blockquote
                    |""".trimMargin()
                + """```
                    |${blockquote}```""".trimMargin()
                + blockquote

            }

            markdown {
                +"This is a blockquote with markdown"
                +"""
                    |>here comes **a blockquote**
                    |>_another line_  
                    |
                    |after blockquote
                   |**""".trimMargin()

            }


            markdown {
                markdownRendererRegistry = MarkdownRendererRegistry(defaultRenderContext).apply {
                    registerRenderContextFunction(BlOCKQUOTE_RENDER_CONTEXT_KEY, true) {
                        val parentRenderContext = this
                        derive {
                            this[PdfRenderContextKeys.BORDER_WIDTH] = 0f
                            this[PdfRenderContextKeys.FONT_STYLE] = parentRenderContext.fontStyle.or(Font.BOLDITALIC)
                            this[PdfRenderContextKeys.PADDING_LEFT] = parentRenderContext.fontSize * 2f
                        }
                    }
                }
                +"let's change the rendering of a blockquote"
                +"""
                    |>here come a blockquote
                    |>another line
                    |
                    |after blockquote
                   |**""".trimMargin()
            }
        }
//        File("blockquote.pdf").writeBytes(doc)
        doc shouldEqual "blockquote.pdf"


    }
}

