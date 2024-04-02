package com.github.ralfstuckert.openpdf.markdown.document

import com.github.ralfstuckert.openpdf.markdown.MarkdownRendererRegistry
import com.github.ralfstuckert.openpdf.markdown.OpenPdfMarkdownGenerator
import com.github.ralfstuckert.openpdf.markdown.defaultRenderContext
import com.lowagie.text.*
import com.lowagie.text.pdf.PdfWriter
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

@DslMarker
annotation class DocumentDsl

fun document(block: DocumentBuilder.()->Unit):ByteArray {
    val builder = DocumentBuilder()
    builder.block()
    return builder.build()
}

sealed  class DocumentSize(val rectangle: Rectangle) {
    object A4_PORTRAIT: DocumentSize(PageSize.A4)
    object A4_LANDSCAPE: DocumentSize(PageSize.A4.rotate())
}

@DocumentDsl
class DocumentBuilder {

    private val paragraphs = mutableListOf<Paragraph>()

    var marginTop:Float = 20f
    var marginBottom:Float = 20f
    var marginLeft:Float = 20f
    var marginRight:Float = 20f
    var size = DocumentSize.A4_PORTRAIT

    var markdownRendererRegistry: MarkdownRendererRegistry = MarkdownRendererRegistry(defaultRenderContext)

    fun markdown(block: MarkdownBuilder.()->Unit) {
        val markdownBuilder = MarkdownBuilder(this.markdownRendererRegistry)
        markdownBuilder.block()
        val paragraph = Paragraph().apply {
            addAll(markdownBuilder.build())
        }
        paragraphs.add(paragraph)
    }


    internal fun build():ByteArray =
        with(ByteArrayOutputStream()) {
            val document: Document = Document(size.rectangle, marginLeft, marginRight, marginTop, marginBottom)
            PdfWriter.getInstance(document, this)
            document.open()
            paragraphs.forEach {
                document.add(it)
            }
            document.close()
            this.toByteArray()
        }

}

@DocumentDsl
class MarkdownBuilder(var markdownRendererRegistry: MarkdownRendererRegistry) {

    val LineBreak = "\n"

    private val markdownList = mutableListOf<String>()

    operator fun String.unaryPlus() =
        markdownList.add(this)

    operator fun Int.times(text:String) =
        repeat(this) { markdownList.add(text) }


    internal fun build():List<Element> =
        markdownList.map { OpenPdfMarkdownGenerator().generate(it, markdownRendererRegistry) }
}



fun ByteArray.toInputStream() =
    ByteArrayInputStream(this)

