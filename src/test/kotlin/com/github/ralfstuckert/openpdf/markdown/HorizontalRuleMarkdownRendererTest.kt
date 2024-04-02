package com.github.ralfstuckert.openpdf.markdown

import com.github.ralfstuckert.openpdf.markdown.document.document
import com.github.ralfstuckert.openpdf.markdown.renderer.HorizontalRuleMarkdownRenderer.Companion.HORIZONTAL_RULE_RENDER_CONTEXT_KEY
import org.junit.jupiter.api.Test
import java.awt.Color

class HorizontalRuleMarkdownRendererTest {

    @Test
    fun horizontalRuler() {
        val doc = document {
            markdown {
                +"A simple horizontal ruler"
                +"""---
                    |
                    |
                """.trimMargin()

            }


            markdown {
                markdownRendererRegistry = MarkdownRendererRegistry(defaultRenderContext).apply {
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

            markdown {
                markdownRendererRegistry = MarkdownRendererRegistry(defaultRenderContext).apply {
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

            markdown {
                markdownRendererRegistry = MarkdownRendererRegistry(defaultRenderContext).apply {
                    registerRenderContextFunction(HORIZONTAL_RULE_RENDER_CONTEXT_KEY, true) {
                        derive {
                            this[PdfRenderContextKeys.PAGE_BREAK_ON_HORIZONTAL_RULE_ENABLED] = true
                        }
                    }
                }
                +"you can configure horizontal ruler as a page break"
                +"""---
                    | Here comes a new page...
                    |
                """.trimMargin()
            }
        }
//        File("horizontalRuler.pdf").writeBytes(doc)
        doc shouldEqual "horizontalRuler.pdf"


    }
}

