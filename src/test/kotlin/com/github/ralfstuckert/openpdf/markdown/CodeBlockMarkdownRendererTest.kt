package com.github.ralfstuckert.openpdf.markdown

import com.github.ralfstuckert.openpdf.markdown.document.document
import com.github.ralfstuckert.openpdf.markdown.renderer.CodeBlockMarkdownRenderer
import org.openpdf.text.Font
import org.junit.jupiter.api.Test
import java.awt.Color

class CodeBlockMarkdownRendererTest {

    @Test
    fun codeblock() {
        val doc = document {
            markdown {
                +"This is some text **"
                +"""```
                   |this ist a 
                   |**code** <T> block
                   |``` 
                   |**""".trimMargin()

            }


            markdown {
                markdownRendererRegistry = MarkdownRendererRegistry(defaultRenderContext).apply {
                    registerRenderContextFunction(CodeBlockMarkdownRenderer.CODE_BlOCK_RENDER_CONTEXT_KEY, true) {
                        val parentRenderContext = this
                        derive {
                            this[PdfRenderContextKeys.COLOR] = Color.blue
                            this[PdfRenderContextKeys.FONT_STYLE] = Font.BOLD
                            this[PdfRenderContextKeys.FONT_SIZE] = parentRenderContext.fontSize * 0.9f
                        }
                    }
                }
                +"let's change the rendering of a code block"
                +"""```
                   |to blue bold with a slightly smaller font
                   |**code** <T> block
                   |``` 
                   |**""".trimMargin()
            }
        }
//        File("codeblock.pdf").writeBytes(doc)
        doc shouldEqual "codeblock.pdf"


    }
}

