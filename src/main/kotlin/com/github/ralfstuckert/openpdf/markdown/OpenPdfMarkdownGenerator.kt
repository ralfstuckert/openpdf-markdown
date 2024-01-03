package com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown

import com.lowagie.text.*
import com.lowagie.text.pdf.PdfWriter
import org.intellij.markdown.MarkdownElementTypes.ATX_3
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
    val simpleText = "sdfjlsdf"
    val text = """
### This is [a](http://wtf) Test
Would you do with a **drunken** _sailor_?
And some very long sentence and some very long sentence and some very long sentence and some very long sentence and some very long sentence and some very long sentence and some very long sentence and some very long sentence and some very long sentence and some very long sentence and some very long sentence and some very long sentence and some very long sentence and some very long sentence and some very long sentence and some very long sentence 

![image search api](https://user-images.githubusercontent.com/110724391/184472398-c590b47c-e1f2-41f8-87e6-2a1f68e8850d.png{width=200})

asdfsd [link test ![image search api](https://user-images.githubusercontent.com/110724391/184472398-c590b47c-e1f2-41f8-87e6-2a1f68e8850d.png{width=200})](https://www.youtube.com/watch?v=3HIr0imLgxM)

| Syntax      | Description | **Test Text**     |
| :---        |    :----:   |          ---: |
| Header      | Title       | Here's this   |
| Paragraph   | Text Text Text Text Text Text Text Text Text Text Text Text Text Text Text Text Text | Text Text Text Text Text Text Text  ![image search api](https://user-images.githubusercontent.com/110724391/184472398-c590b47c-e1f2-41f8-87e6-2a1f68e8850d.png{width=20}) |
"""



    val registry = ElementProviderRegistry(defaultRenderContext).apply {
        registerRenderContextFunction(ElementProviderRenderContextKey(ATX_3.name)) {
            derive {
                this[PdfRenderContextKeys.FONT_SIZE] = 40f
            }
        }
        registerRenderContextFunction(ElementProviderRenderContextKey(INLINE_LINK.name)) {
            derive {
                this[PdfRenderContextKeys.FONT_COLOR] = Color.blue
                this[PdfRenderContextKeys.UNDERLINE_THICKNESS] = this@registerRenderContextFunction.fontSize * 0.08f
            }
        }
    }

    val document: Document = Document()
    document.use {
        PdfWriter.getInstance(
            document,
            FileOutputStream("markdown.pdf")
        )
        document.open()
        val markdown = OpenPdfMarkdownGenerator().generate(text, registry)
        document.add(markdown)
    }

}