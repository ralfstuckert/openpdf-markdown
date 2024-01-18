package com.github.ralfstuckert.openpdf.markdown

import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.ElementProviderRegistry
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.PdfRenderContextKeys
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.defaultRenderContext
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.derive
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.provider.ArabicNumberIndexIterator
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.provider.LinkProvider.Companion.INLINE_LINK_RENDER_CONTEXT_KEY
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.provider.ListIndexIteratorFactory
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.provider.ListProvider
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.provider.RomanAlphabetIndexIterator
import org.junit.jupiter.api.Test
import java.awt.Color
import java.io.File

class ListProviderTest {

    @Test
    fun list() {
        val doc = document {
            paragraph {
                +"""An ordered list
                    |1. first
                    |1. second
                    |    1. sub first
                    |    1. sub second
                    |    1. sub third
                """.trimMargin()
            }

            paragraph {
                +"""An unordered list
                    |- first
                    |- second
                    |    - sub first
                    |    - sub second
                    |    - sub third
                """.trimMargin()
            }

            paragraph {
                +"""An mixed list
                    |- first
                    |- second
                    |    1. sub first
                    |    1. sub second
                    |    1. sub third
                """.trimMargin()
            }


            paragraph {
                +"""A list with markup
                    |- **first**
                    |- _second_
                    |    1. sub first
                    |    1. [sub second](https://github.com/ralfstuckert/openpdf-markdown)
                    |    1. sub third
                """.trimMargin()
            }


            paragraph {
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

