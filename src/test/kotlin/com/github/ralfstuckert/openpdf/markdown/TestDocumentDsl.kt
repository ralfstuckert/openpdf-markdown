package com.github.ralfstuckert.openpdf.markdown

import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.ElementProviderRegistry
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.OpenPdfMarkdownGenerator
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.defaultRenderContext
import com.lowagie.text.*
import com.lowagie.text.pdf.PdfPageEvent
import com.lowagie.text.pdf.PdfWriter
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

fun document(block: DocumentBuilder.()->Unit):ByteArray {
    val builder = DocumentBuilder()
    builder.block()
    return builder.build()
}

sealed  class DocumentSize(val rectangle: Rectangle) {
    object A4_PORTRAIT: DocumentSize(PageSize.A4)
    object A4_LANDSCAPE: DocumentSize(PageSize.A4.rotate())
}

class DocumentBuilder {

    private val paragraphs = mutableListOf<Paragraph>()

    var marginTop:Float = 20f
    var marginBottom:Float = 20f
    var marginLeft:Float = 20f
    var marginRight:Float = 20f
    var size = DocumentSize.A4_PORTRAIT

    fun paragraph(block: MarkupBuilder.()->Unit) {
        val markupBuilder = MarkupBuilder()
        markupBuilder.block()
        val paragraph = Paragraph().apply {
            addAll(markupBuilder.build())
        }
        paragraphs.add(paragraph)
    }

    internal fun build():ByteArray =
        with(ByteArrayOutputStream()) {
            val document: Document = Document(size.rectangle, marginLeft, marginRight, marginTop, marginBottom)
            PdfWriter.getInstance(document, this).apply {
                pageEvent = object:PdfPageEvent {
                    override fun onOpenDocument(p0: PdfWriter?, p1: Document?) {
                        println("onOpenDocument")
                    }

                    override fun onStartPage(p0: PdfWriter?, p1: Document?) {
                        println("onStartPage")
                    }

                    override fun onEndPage(p0: PdfWriter?, p1: Document?) {
                        println("onEndPage")
                    }

                    override fun onCloseDocument(p0: PdfWriter?, p1: Document?) {
                        println("onCloseDocument")
                    }

                    override fun onParagraph(p0: PdfWriter?, p1: Document?, p2: Float) {
                        println("onParagraph $p2")
                    }

                    override fun onParagraphEnd(p0: PdfWriter?, p1: Document?, p2: Float) {
                        println("onParagraphEnd $p2")
                    }

                    override fun onChapter(p0: PdfWriter?, p1: Document?, p2: Float, p3: Paragraph?) {
                        println("onChapter")
                    }

                    override fun onChapterEnd(p0: PdfWriter?, p1: Document?, p2: Float) {
                        println("onChapterEnd")
                    }

                    override fun onSection(p0: PdfWriter?, p1: Document?, p2: Float, p3: Int, p4: Paragraph?) {
                        println("onSection")
                    }

                    override fun onSectionEnd(p0: PdfWriter?, p1: Document?, p2: Float) {
                        println("onSectionEnd")
                    }

                    override fun onGenericTag(p0: PdfWriter?, p1: Document?, p2: Rectangle?, p3: String?) {
                        println("onGenericTag")
                    }

                }
            }
            document.open()
            paragraphs.forEach {
                document.add(it)
            }
            document.close()
            this.toByteArray()
        }

}

class MarkupBuilder {

    private val markupList = mutableListOf<String>()

    var elementProviderRegistry: ElementProviderRegistry = ElementProviderRegistry(defaultRenderContext)

    operator fun String.unaryPlus() =
        markupList.add(this)

    internal fun build():List<Element> =
        markupList.map { OpenPdfMarkdownGenerator().generate(it, elementProviderRegistry) }
}


fun ByteArray.toInputStream() =
    ByteArrayInputStream(this)

