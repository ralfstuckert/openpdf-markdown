package com.github.ralfstuckert.openpdf.markdown

import com.github.ralfstuckert.openpdf.markdown.document.document
import com.github.ralfstuckert.openpdf.markdown.renderer.LinkMarkdownRenderer.Companion.INLINE_LINK_RENDER_CONTEXT_KEY
import org.junit.jupiter.api.Test
import java.awt.Color
import java.io.File

class LinkMarkdownRendererTest {

    @Test
    fun link() {
        val doc = document {
            markdown {
                +"""There is [a link](https://github.com/ralfstuckert/openpdf-markdown) in the text
                """
            }

            markdown {
                +"""# heading with [a link](https://github.com/ralfstuckert/openpdf-markdown)
                    """
            }

            markdown {
                +"""You can [use **markdown** in links <img src="https://avatars.githubusercontent.com/u/23091459?s=200&v=4" height="50" />](https://github.com/ralfstuckert/openpdf-markdown)
                    """
            }



            markdown {
                markdownRendererRegistry = MarkdownRendererRegistry(defaultRenderContext).apply {
                    registerRenderContextFunction(INLINE_LINK_RENDER_CONTEXT_KEY, true) {
                        derive {
                            this[PdfRenderContextKeys.COLOR] = Color.blue
                            this[PdfRenderContextKeys.LINE_THICKNESS] = 0f
                        }
                    }
                }
                +"let's change the rendering of a link [to blue without underline](https://github.com/ralfstuckert/openpdf-markdown) or whatever you want"
            }
        }
//        File("link.pdf").writeBytes(doc)
        doc shouldEqual "link.pdf"


    }
}

