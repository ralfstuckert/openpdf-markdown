package com.github.ralfstuckert.openpdf.markdown

import com.lowagie.text.Document
import com.lowagie.text.pdf.PdfWriter
import org.junit.jupiter.api.Test
import java.io.FileOutputStream

typealias Filename = String

class Example {

    @Test
    fun basicMarkup() {
        writeMarkupPDF("This is some **strong text**", "example.pdf")
    }

    fun writeMarkupPDF(markdown:String, filename:Filename) {
        val element = OpenPdfMarkdownGenerator().generate(markdown)

        with(FileOutputStream(filename)) {
            val document: Document = Document()
            PdfWriter.getInstance(document, this)
            document.open()
            document.add(element)
            document.close()
        }
    }

}