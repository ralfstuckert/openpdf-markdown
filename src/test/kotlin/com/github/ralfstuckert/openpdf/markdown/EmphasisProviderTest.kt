package com.github.ralfstuckert.openpdf.markdown

import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.ElementProviderRegistry
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.PdfRenderContextKeys
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.defaultRenderContext
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.derive
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.provider.EmphasisProvider.Companion.EMPHASIS_RENDER_CONTEXT_KEY
import com.lowagie.text.Font
import org.junit.jupiter.api.Test
import java.awt.Color

class EmphasisProviderTest {

    @Test
    fun emphasis() {
        val doc = document {
            paragraph {
                +" This is some text with _emphasis markup_\n"
                +"""markup *over
                    | linebreaks* works.
                    | 
                """.trimMargin()
            }

            paragraph {
                +"""# heading with _emphasis markup_
                    """
            }

            paragraph {
                elementProviderRegistry = ElementProviderRegistry(defaultRenderContext).apply {
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

