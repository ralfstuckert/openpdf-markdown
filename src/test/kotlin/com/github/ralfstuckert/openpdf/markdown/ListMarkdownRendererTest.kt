package com.github.ralfstuckert.openpdf.markdown

import com.github.ralfstuckert.openpdf.markdown.document.document
import com.github.ralfstuckert.openpdf.markdown.renderer.ListIndexIteratorFactory
import com.github.ralfstuckert.openpdf.markdown.renderer.ListMarkdownRenderer
import com.github.ralfstuckert.openpdf.markdown.renderer.RomanAlphabetIndexIterator
import org.junit.jupiter.api.Test
import java.io.File

class ListMarkdownRendererTest {

    @Test
    fun list() {
        val doc = document {
            markdown {
                +"""An ordered list
                    |1. first
                    |1. second
                    |    1. sub first
                    |    1. sub second
                    |    1. sub third
                """.trimMargin()
            }

            markdown {
                +"""An unordered list
                    |- first
                    |- second
                    |    - sub first
                    |    - sub second
                    |    - sub third
                """.trimMargin()
            }

            markdown {
                +"""A mixed list
                    |- first
                    |- second
                    |    1. sub first
                    |    1. sub second
                    |    1. sub third
                """.trimMargin()
            }


            markdown {
                +"""A list with markdown
                    |- **first**
                    |- _second_
                    |    1. sub first
                    |    1. [sub second](https://github.com/ralfstuckert/openpdf-markdown)
                    |    1. sub third
                """.trimMargin()
            }


            markdown {
                markdownRendererRegistry = MarkdownRendererRegistry(defaultRenderContext).apply {
                    registerRenderContextFunction(ListMarkdownRenderer.ORDERED_LIST_RENDER_CONTEXT_KEY) {
                        derive {
                            this[PdfRenderContextKeys.LIST_INDEX_ITERATOR_FACTORY] = ListIndexIteratorFactory { listLevel: Int, parentPrefix: String ->
                                object:RomanAlphabetIndexIterator(listLevel, parentPrefix) {
                                    override fun isLowercase(index: Int): Boolean = listLevel%2 == 1
                                }
                            }
                        }
                    }
                }
                +"""let's change the rendering of a an ordered list to roman letters
                    |1. first
                    |1. second
                    |    1. sub first
                    |    1. sub second
                    |    1. sub third
                """.trimMargin()

            }
        }
//        File("list.pdf").writeBytes(doc)
        doc shouldEqual "list.pdf"


    }
}

