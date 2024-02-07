package com.github.ralfstuckert

import com.lowagie.text.*
import com.lowagie.text.pdf.PdfWriter
import java.io.FileOutputStream
import java.io.IOException


fun main() {

    com.lowagie.text.Document.compress = false
    kotlin.io.println("images wrapped in a Chunk")

    // step 1: creation of a document-object
    var document: Document = Document()
    try
    {
        // step 2:
        // we create a writer that listens to the document
        // and directs a PDF-stream to a file
        PdfWriter.getInstance(document, FileOutputStream("imageChunks.pdf"))
        // step 3: we open the document
        document.open()
        // step 4: we create a table and add it to the document
        val img =
            Image.getInstance("https://user-images.githubusercontent.com/110724391/184472398-c590b47c-e1f2-41f8-87e6-2a1f68e8850d.png")
                .apply {
//                    scaleAbsoluteHeight(200f)
                }

//        val table = PdfPTable(1)
//        table.widthPercentage = 100f
//        table.defaultCell.border = 0
//        val cell = PdfPCell(img).apply {
//            border = 0
//            horizontalAlignment = Element.ALIGN_CENTER
//        }
////        cell.addElement(Chunk(img, 0f, 0f))
//        table.addCell(cell)
//
        val p1 = Phrase("This is an image ")
        p1.add(" just here.\n")
        p1.add(Chunk(img, 0f, 0f))
        document.add(p1)
        document.add(p1)
        document.add(p1)
//        document.add(Chunk("").apply { setNewPage() })
        document.add(p1)
        document.add(p1)
        document.add(p1)
        document.add(p1)

        document.close()
    }
    catch (de:DocumentException)
    {
        System.err.println(de.message)
    }catch (de: IOException)
    {
        System.err.println(de.message)
    }

    // step 5: we close the document
}

