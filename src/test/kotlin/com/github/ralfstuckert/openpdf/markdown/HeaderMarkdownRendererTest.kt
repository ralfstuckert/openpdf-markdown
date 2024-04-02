package com.github.ralfstuckert.openpdf.markdown

import com.github.ralfstuckert.openpdf.markdown.document.document
import com.github.ralfstuckert.openpdf.markdown.renderer.HeaderMarkdownRenderer.Companion.HEADER_3_RENDER_CONTEXT_KEY
import com.lowagie.text.Font
import org.junit.jupiter.api.Test
import java.awt.Color

class HeaderMarkdownRendererTest {

    @Test
    fun header() {
        val doc = document {
            markdown {
                +"""
                    # Header One
                    
                    ## Header Two

                    ### Header Three

                    #### Header Four

                    ##### Header Five

                    ###### Header Six
                    
                    
                """.trimIndent()
            }

            markdown {
                markdownRendererRegistry = MarkdownRendererRegistry(defaultRenderContext).apply {
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

