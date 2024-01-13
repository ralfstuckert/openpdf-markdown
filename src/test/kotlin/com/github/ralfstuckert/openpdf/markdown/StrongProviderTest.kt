package com.github.ralfstuckert.openpdf.markdown

import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.ElementProviderRegistry
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.PdfRenderContextKeys
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.defaultRenderContext
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.derive
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.provider.StrongProvider.Companion.STRONG_RENDER_CONTEXT_KEY
import com.lowagie.text.Font
import org.junit.jupiter.api.Test
import java.awt.Color

class StrongProviderTest {

    @Test
    fun strong() {
        val doc = document {
            paragraph {
                +" This is some text with **strong markup**\n"
                +"""markup __over
                    | linebreaks__ works.
                    | 
                """.trimMargin()
            }

            paragraph {
                +"""# heading with **strong markup**
                    """
            }

            paragraph {
                elementProviderRegistry = ElementProviderRegistry(defaultRenderContext).apply {
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

