package com.github.ralfstuckert.openpdf.markdown

import org.junit.jupiter.api.Test

class ImageProviderTest {

    val imageUrl = "https://avatars.githubusercontent.com/u/23091459?s=200&v=4"

    @Test
    fun image() {
        val doc = document {
            paragraph {
                +"Here is an image\n"
                +"![some text](https://avatars.githubusercontent.com/u/23091459?s=200&v=4)\n"
            }

            paragraph {
                +"""if you want to scale it, use the HTML img-tag and set e.g. the width\
                    |<img url="https://avatars.githubusercontent.com/u/23091459?s=200&v=4" width="300" />
                    |""".trimMargin()
            }

            paragraph {
                +"""or both width and height
                   |<img url="https://avatars.githubusercontent.com/u/23091459?s=200&v=4" width="500" height="100" />
                   |""".trimMargin()
            }

            paragraph {
                +"""you can embed an image <img url="https://avatars.githubusercontent.com/u/23091459?s=200&v=4" width="20" /> in your text"""
            }

        }
//        File("image.pdf").writeBytes(doc)
        doc shouldEqual "image.pdf"
    }

}