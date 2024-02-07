package com.github.ralfstuckert.openpdf.markdown

import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.*
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.document.document
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.provider.BlockquoteProvider
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.provider.StrikethroughProvider
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.provider.TableProvider
import com.lowagie.text.Font
import org.junit.jupiter.api.Test
import java.awt.Color
import java.io.File

class LetterTest {

    @Test
    fun letter() {
        val doc = document {

            elementProviderRegistry = ElementProviderRegistry(defaultRenderContext).apply {
                registerRenderContextFunction(TableProvider.TABLE_RENDER_CONTEXT_KEY, true) {
                    derive {
                        this[PdfRenderContextKeys.BORDER_WIDTH] = 0f
                        this[PdfRenderContextKeys.WEIGHTED_WIDTHS_ENABLED] = true
                    }
                }
                registerRenderContextFunction(StrikethroughProvider.STRIKETHROUGH_RENDER_CONTEXT_KEY, false) {
                    derive {
                        this[PdfRenderContextKeys.BACKGROUND_COLOR] = Color.orange
                    }
                }
            }

            marginLeft = 40f
            marginRight = 40f
            marginTop = 60f

            markup {
                +"""
                |  |  |  |
                |----|---|----|
                | Frau Sieglinde Siegessicher |  | **Kundennummer: 23409823** |
                | Am Biet 17                  |  | Sachbearbeiterin   |
                | 68234 Ober-Unternau         |  | S. Karbowski   |
                |                             |  | Aktenzeichen 1704   |
                """.trimIndent()

                3 * LineBreak
                +"""#### Bzgl. Ihres Schreibens `"Fragen zum Formular 2345"`"""
                + LineBreak
                +"""
                Lorem ipsum dolor sit amet, **consetetur sadipscing** elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero _eos et accusam et justo_ duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.   

                ~~Duis autem vel eum iriure dolor~~ in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat.   

                Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi.   

                Nam liber tempor cum soluta nobis eleifend option congue nihil imperdiet doming id quod mazim placerat facer
                
""".trimIndent()
                +"""
                Bitte antworten Sie per Mail an **[Sieglinde.Siegessicher@gmx.de](mailto:Sieglinde.Siegessicher@gmx.de?subject=RE:%20Fragen%20zum%20Formular%202345)**
                """.trimIndent()

            }

        }
        File("letter.pdf").writeBytes(doc)
        doc shouldEqual "letter.pdf"


    }

}