package com.github.ralfstuckert.openpdf.markdown

import com.lowagie.text.Document
import com.lowagie.text.pdf.PdfWriter
import org.junit.jupiter.api.Test
import java.io.FileOutputStream

typealias Filename = String

class Example {

    @Test
    fun basicMarkdown() {
        val markdown = """
# Tempor Invidunt
Lorem ipsum dolor sit amet, **consetetur sadipscing** elitr, sed diam _nonumy eirmod tempor invidunt_ ut labore et dolore magna ~~aliquyam~~ erat, `sed diam voluptua`. [Nam liber tempor](https://github.com/ralfstuckert/openpdf-markdown/wiki/Getting-Started)

| **Stet** | **Sanctus** |
|:---:|-----------|
| Clita   | `Stet clita` kasd> gubergren. |
| Gubergren | Sea _takimata_ sanctus est Lorem |

- Stet
- Clita
    1. Sanctus
    1. Lorem
    1. Dolor    

> At vero _eos et accusam et justo_ 
> duo dolores et ea rebum. 

sdfsdf
        """.trimIndent()
        writeMarkdownPDF(markdown, "example.pdf")
    }

    fun writeMarkdownPDF(markdown:String, filename:Filename) {
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