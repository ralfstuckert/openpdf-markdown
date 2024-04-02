package com.github.ralfstuckert.openpdf.markdown

import com.github.ralfstuckert.openpdf.markdown.document.document
import com.github.ralfstuckert.openpdf.markdown.renderer.StrikethroughMarkdownRenderer.Companion.STRIKETHROUGH_RENDER_CONTEXT_KEY
import org.junit.jupiter.api.Test
import java.awt.Color
import java.io.File

class StrikethroughMarkdownRendererTest {

    @Test
    fun strikethrough() {
        val doc = document {
            markdown {
                +" This is some text with ~~strikethrough markdown~~\n"
                +"""markdown ~~over
                    | linebreaks~~ works.
                    | 
                """.trimMargin()
            }

            markdown {
                +"""# heading with ~~strikethrough markdown~~
                   |""".trimMargin()
            }

            markdown {
                markdownRendererRegistry = MarkdownRendererRegistry(defaultRenderContext).apply {
                    registerRenderContextFunction(STRIKETHROUGH_RENDER_CONTEXT_KEY, true) {
                        val parentRenderContext = this
                        derive {
                            this[PdfRenderContextKeys.COLOR] = Color.lightGray
                            this[PdfRenderContextKeys.FONT_SIZE] = parentRenderContext.fontSize * 0.9f
                        }
                    }
                }
                +"""let's change the rendering of strikethrough ~~to gray font with a smaller font~~ or whatever you want
                   |""".trimMargin()
            }

            markdown {
                markdownRendererRegistry = MarkdownRendererRegistry(defaultRenderContext).apply {
                    registerRenderContextFunction(STRIKETHROUGH_RENDER_CONTEXT_KEY, false) {
                        derive {
                            this[PdfRenderContextKeys.BACKGROUND_COLOR] = Color.orange
                        }
                    }
                }
                +"""let`s (mis-)use strikethrough as a text marker ~~with orange color~~
                   |""".trimMargin()
            }

        }
//        File("strikethrough.pdf").writeBytes(doc)
        doc shouldEqual "strikethrough.pdf"


    }
}

