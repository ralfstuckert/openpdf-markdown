package com.github.ralfstuckert.openpdf.markdown

import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.*
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.document.document
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.provider.StrikethroughProvider.Companion.STRIKETHROUGH_RENDER_CONTEXT_KEY
import org.junit.jupiter.api.Test
import java.awt.Color

class StrikethroughProviderTest {

    @Test
    fun strikethrough() {
        val doc = document {
            markup {
                +" This is some text with ~~strikethrough markup~~\n"
                +"""markup ~~over
                    | linebreaks~~ works.
                    | 
                """.trimMargin()
            }

            markup {
                +"""# heading with ~~strikethrough markup~~
                   |""".trimMargin()
            }

            markup {
                elementProviderRegistry = ElementProviderRegistry(defaultRenderContext).apply {
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

            markup {
                elementProviderRegistry = ElementProviderRegistry(defaultRenderContext).apply {
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

