package com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown

import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.provider.TableProvider.Companion.TABLE_RENDER_CONTEXT_KEY
import com.lowagie.text.Document
import com.lowagie.text.Element
import com.lowagie.text.Font
import com.lowagie.text.Paragraph
import com.lowagie.text.pdf.PdfWriter
import org.intellij.markdown.MarkdownElementTypes.ATX_3
import org.intellij.markdown.MarkdownElementTypes.IMAGE
import org.intellij.markdown.MarkdownElementTypes.INLINE_LINK
import org.intellij.markdown.flavours.MarkdownFlavourDescriptor
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.parser.MarkdownParser
import java.awt.Color
import java.io.FileOutputStream


class OpenPdfMarkdownGenerator {

    fun generate(markupText: String, registry: ElementProviderRegistry = ElementProviderRegistry(defaultRenderContext),): Element {
        val flavour: MarkdownFlavourDescriptor = GFMFlavourDescriptor()
        val parsedTree = MarkdownParser(flavour).buildMarkdownTreeFromString(markupText)
        val visitor = OpenPdfVisitor(markupText, registry)
        val paragraph = Paragraph()
        visitor.visitNode(paragraph, registry.defaultRenderContext, parsedTree)
        return paragraph
    }
}

fun main() {

    val document: Document = Document()
    val maxWidth = document.right() - document.left()

    val simpleText = "sdfjlsdf"
    val text = """
        |>this a blockquote
        |>another line
        |here I am
        |
        |> first line
        |> >this a nested blockquote
        |> >another line
        |here I am
        |
        |1. first
        |1. second
        |    1. sec-one
        |    1. sec-two
        |    1. sec-one
        |    1. sec-two
        |    1. sec-one
        |    1. sec-two
        |    1. sec-one
        |    1. sec-two
        |    1. sec-one
        |    1. sec-two
        |    1. sec-one
        |    1. sec-two
        |    1. sec-one
        |    1. sec-two
        |    1. sec-one
        |    1. sec-two
        |    1. sec-one
        |    1. sec-two
        |    1. sec-one
        |    1. sec-two
        |    1. sec-one
        |    1. sec-two
        |        - sub one
        |        - sub two
        |- third
        | 
    """.trimMargin()



    val renderContextBase = defaultRenderContext.derive {
        this[PdfRenderContextKeys.FONT_FAMILY] = Font.TIMES_ROMAN
//        this[PdfRenderContextKeys.WEIGHTED_WIDTHS] = true
    }
    val registry = ElementProviderRegistry(renderContextBase).apply {
        registerRenderContextFunction(ElementProviderRenderContextKey(ATX_3.name), true) {
            derive {
                this[PdfRenderContextKeys.FONT_STYLE] = Font.BOLDITALIC
            }
        }
        registerRenderContextFunction(ElementProviderRenderContextKey(INLINE_LINK.name), true) {
            derive {
                this[PdfRenderContextKeys.COLOR] = Color.blue
//                this[PdfRenderContextKeys.UNDERLINE_THICKNESS] = this@registerRenderContextFunction.fontSize * 0.08f
            }
        }
        registerRenderContextFunction(ElementProviderRenderContextKey(IMAGE.name), true) {
            derive {
                this[PdfRenderContextKeys.LINE_THICKNESS] = 0f
            }
        }
        registerRenderContextFunction(TABLE_RENDER_CONTEXT_KEY, true) {
            derive {
                this[PdfRenderContextKeys.BORDER_COLOR] = Color.blue
//                this[PdfRenderContextKeys.BORDER_WIDTH] = 0f
//                this[PdfRenderContextKeys.UNDERLINE_THICKNESS] = this@registerRenderContextFunction.fontSize * 0.08f
            }
        }
    }

        PdfWriter.getInstance(
            document,
            FileOutputStream("markdown.pdf")
        ).apply {
            setStrictImageSequence(true)
        }.use {
            document.open()
            val markdown = OpenPdfMarkdownGenerator().generate(text, registry)
            document.add(markdown)
            document.close()
        }

}