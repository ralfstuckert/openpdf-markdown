package com.github.ralfstuckert.openpdf.markdown

import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.ElementProviderRegistry
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.PdfRenderContextKeys
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.defaultRenderContext
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.derive
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.document.document
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.provider.ListIndexIteratorFactory
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.provider.ListProvider
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.provider.RomanAlphabetIndexIterator
import org.junit.jupiter.api.Test

class ListProviderTest {

    @Test
    fun list() {
        val doc = document {
            markup {
                +"""An ordered list
                    |1. first
                    |1. second
                    |    1. sub first
                    |    1. sub second
                    |    1. sub third
                """.trimMargin()
            }

            markup {
                +"""An unordered list
                    |- first
                    |- second
                    |    - sub first
                    |    - sub second
                    |    - sub third
                """.trimMargin()
            }

            markup {
                +"""An mixed list
                    |- first
                    |- second
                    |    1. sub first
                    |    1. sub second
                    |    1. sub third
                """.trimMargin()
            }


            markup {
                +"""A list with markup
                    |- **first**
                    |- _second_
                    |    1. sub first
                    |    1. [sub second](https://github.com/ralfstuckert/openpdf-markdown)
                    |    1. sub third
                """.trimMargin()
            }


            markup {
                elementProviderRegistry = ElementProviderRegistry(defaultRenderContext).apply {
                    registerRenderContextFunction(ListProvider.ORDERED_LIST_RENDER_CONTEXT_KEY) {
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

