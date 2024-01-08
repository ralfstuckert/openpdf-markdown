package com.github.ralfstuckert.openpdf.markdown

import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.ElementProviderRegistry
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.PdfRenderContextKeys
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.defaultRenderContext
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.derive
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.provider.HeaderProvider.Companion.HEADER_3_RENDER_CONTEXT_KEY
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.provider.StrongProvider.Companion.STRONG_RENDER_CONTEXT_KEY
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.provider.TableProvider.Companion.TABLE_RENDER_CONTEXT_KEY
import com.lowagie.text.Font
import org.junit.jupiter.api.Test
import rst.pdftools.compare.assertPdfEquals
import java.awt.Color
import java.io.File

class TableProviderTest {

    @Test
    fun header() {
        val doc = document {
            paragraph {
                +"""
                    | **Column 1** | **Column 2** | **Column 3** |
                    |----------|----------|----------|
                    | Hello there, what's going on | Beat's me | The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog |
                    | | hihi | |
                    
                                        
                """.trimIndent()
            }

            paragraph {
                elementProviderRegistry = ElementProviderRegistry(defaultRenderContext).apply {
                    registerRenderContextFunction(TABLE_RENDER_CONTEXT_KEY, true) {
                        derive {
                            this[PdfRenderContextKeys.WIDTH_PERCENTAGE] = 80f
                            this[PdfRenderContextKeys.WEIGHTED_WIDTHS] = true
                        }
                    }
                }
                +"A table with 80% width and weighted columns"
                +"""                   
                    | **Small Column** | **Big Column** | **Medium Column** |
                    |---|----------|-----|
                    | Hello there, what's going on | Beat's me | The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog |
                    | | hihi | |
                    
               """.trimIndent()
            }

            paragraph {
                elementProviderRegistry = ElementProviderRegistry(defaultRenderContext).apply {
                    registerRenderContextFunction(TABLE_RENDER_CONTEXT_KEY, true) {
                        derive {
                            this[PdfRenderContextKeys.BORDER_COLOR] = Color.blue
                            this[PdfRenderContextKeys.BORDER_WIDTH] = 2f
                        }
                    }
                }
                +" A table with a custom border"
                +"""    
                    | **Column 1** | **Column 2** | **Column 3** |
                    |----------|----------|----------|
                    | Hello there, what's going on | Beat's me | The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog |
                    | | hihi | |
                    
               """.trimIndent()
            }

            paragraph {
                elementProviderRegistry = ElementProviderRegistry(defaultRenderContext).apply {
                    registerRenderContextFunction(TABLE_RENDER_CONTEXT_KEY, true) {
                        derive {
                            this[PdfRenderContextKeys.BORDER_WIDTH] = 0f
                        }
                    }
                }
                +"A table without border"
                +"""
                    | **Column 1** | **Column 2** | **Column 3** |
                    |----------|----------|----------|
                    | Hello there, what's going on | Beat's me | The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog |
                    | | hihi | |
                    
               """.trimIndent()
            }
        }
        File("table.pdf").writeBytes(doc)
        doc shouldEqual "table.pdf"


    }
}

