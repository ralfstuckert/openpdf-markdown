package com.github.ralfstuckert.openpdf.markdown

import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.ElementProviderRegistry
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.PdfRenderContextKeys
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.defaultRenderContext
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.derive
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.document.document
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.provider.LinkProvider.Companion.INLINE_LINK_RENDER_CONTEXT_KEY
import org.junit.jupiter.api.Test
import java.awt.Color

class LinkProviderTest {

    @Test
    fun link() {
        val doc = document {
            markup {
                +"""There is [a link](https://github.com/ralfstuckert/openpdf-markdown) in the text
                """
            }

            markup {
                +"""# heading with [a link](https://github.com/ralfstuckert/openpdf-markdown)
                    """
            }

            markup {
                +"""You can [use **markup** in links <img url="https://avatars.githubusercontent.com/u/23091459?s=200&v=4" height="50" />](https://github.com/ralfstuckert/openpdf-markdown)
                    """
            }



            markup {
                elementProviderRegistry = ElementProviderRegistry(defaultRenderContext).apply {
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

