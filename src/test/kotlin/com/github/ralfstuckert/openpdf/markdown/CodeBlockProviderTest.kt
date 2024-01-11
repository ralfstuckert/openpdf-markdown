package com.github.ralfstuckert.openpdf.markdown

import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.*
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.provider.CodeBlockProvider
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.provider.InlineCodeProvider
import com.lowagie.text.Font
import org.junit.jupiter.api.Test
import java.awt.Color
import java.io.File

class CodeBlockProviderTest {

    @Test
    fun codeblock() {
        val doc = document {
            paragraph {
                +"This is some text **"
                +"""```
                   |this ist a 
                   |**code** <T> block
                   |``` 
                   |**""".trimMargin()

            }


            paragraph {
                elementProviderRegistry = ElementProviderRegistry(defaultRenderContext).apply {
                    registerRenderContextFunction(CodeBlockProvider.CODE_BlOCK_RENDER_CONTEXT_KEY, true) {
                        val parentRenderContext = this
                        derive {
                            this[PdfRenderContextKeys.FONT_COLOR] = Color.blue
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
        File("codeblock.pdf").writeBytes(doc)
        doc shouldEqual "codeblock.pdf"


    }
}

