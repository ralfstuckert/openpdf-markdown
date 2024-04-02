package com.github.ralfstuckert.openpdf.markdown

import com.github.ralfstuckert.openpdf.markdown.document.document
import org.junit.jupiter.api.Test

class ImageMarkdownRendererTest {

    val imageUrl = "https://avatars.githubusercontent.com/u/23091459?s=200&v=4"

    @Test
    fun image() {
        val doc = document {
            markdown {
                +"Here is an image\n"
                +"![some text](https://avatars.githubusercontent.com/u/23091459?s=200&v=4)\n"
            }

            markdown {
                +"""if you want to scale it, use the HTML img-tag and set e.g. the width\
                    |<img src="https://avatars.githubusercontent.com/u/23091459?s=200&v=4" width="300" />
                    |""".trimMargin()
            }

            markdown {
                +"""or both width and height
                   |<img src="https://avatars.githubusercontent.com/u/23091459?s=200&v=4" width="500" height="100" />
                   |""".trimMargin()
            }

            markdown {
                +"""you can embed an image <img src="https://avatars.githubusercontent.com/u/23091459?s=200&v=4" width="20" /> in your text"""
            }

        }
//        File("image.pdf").writeBytes(doc)
        doc shouldEqual "image.pdf"
    }

}