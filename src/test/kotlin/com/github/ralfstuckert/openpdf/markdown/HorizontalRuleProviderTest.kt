package com.github.ralfstuckert.openpdf.markdown

import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.*
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.provider.HorizontalRuleProvider.Companion.HORIZONTAL_RULE_RENDER_CONTEXT_KEY
import org.junit.jupiter.api.Test
import java.awt.Color
import java.io.File

class HorizontalRuleProviderTest {

    @Test
    fun horizontalRuler() {
        val doc = document {
            paragraph {
                +"A simple horizontal ruler"
                +"""---
                    |
                    |
                """.trimMargin()

            }


            paragraph {
                elementProviderRegistry = ElementProviderRegistry(defaultRenderContext).apply {
                    registerRenderContextFunction(HORIZONTAL_RULE_RENDER_CONTEXT_KEY, true) {
                        val parentRenderContext = this
                        derive {
                            this[PdfRenderContextKeys.WIDTH_PERCENTAGE] = 80f
                            this[PdfRenderContextKeys.HORIZONTAL_ALIGNMENT] = HorizontalAlignment.right
                        }
                    }
                }
                +"width of 80% right aligned"
                +"""---
                    |
                    |
                """.trimMargin()
            }

            paragraph {
                elementProviderRegistry = ElementProviderRegistry(defaultRenderContext).apply {
                    registerRenderContextFunction(HORIZONTAL_RULE_RENDER_CONTEXT_KEY, true) {
                        val parentRenderContext = this
                        derive {
                            this[PdfRenderContextKeys.COLOR] = Color.blue
                            this[PdfRenderContextKeys.LINE_THICKNESS] = 3f
                        }
                    }
                }
                +"in blue with a thickness of 3"
                +"""---
                    |
                    |
                """.trimMargin()
            }
        }
//        File("horizontalRuler.pdf").writeBytes(doc)
        doc shouldEqual "horizontalRuler.pdf"


    }
}

