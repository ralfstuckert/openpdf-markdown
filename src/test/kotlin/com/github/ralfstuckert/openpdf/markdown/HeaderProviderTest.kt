package com.github.ralfstuckert.openpdf.markdown

import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.ElementProviderRegistry
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.PdfRenderContextKeys
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.defaultRenderContext
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.derive
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.document.document
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.provider.HeaderProvider.Companion.HEADER_3_RENDER_CONTEXT_KEY
import com.lowagie.text.Font
import org.junit.jupiter.api.Test
import java.awt.Color

class HeaderProviderTest {

    @Test
    fun header() {
        val doc = document {
            markup {
                +"""
                    # Header One
                    
                    ## Header Two

                    ### Header Three

                    #### Header Four

                    ##### Header Five

                    ###### Header Six
                    
                    
                """.trimIndent()
            }

            markup {
                elementProviderRegistry = ElementProviderRegistry(defaultRenderContext).apply {
                    registerRenderContextFunction(HEADER_3_RENDER_CONTEXT_KEY, true) {
                        derive {
                            this[PdfRenderContextKeys.COLOR] = Color.blue
                            this[PdfRenderContextKeys.FONT_FAMILY] = Font.TIMES_ROMAN
                            this[PdfRenderContextKeys.FONT_STYLE] = Font.BOLDITALIC
                            this[PdfRenderContextKeys.FONT_SIZE] = 17f
                        }
                    }
                }
                +"""
                    
                    # Header One
                    
                    ## Header Two

                    ### Header Three with a custom rendering

                    #### Header Four

                    ##### Header Five

                    ###### Header Six
                """.trimIndent()
            }
        }
//        File("header.pdf").writeBytes(doc)
        doc shouldEqual "header.pdf"


    }
}

