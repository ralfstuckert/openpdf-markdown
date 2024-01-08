package com.github.ralfstuckert.openpdf.markdown

import rst.pdftools.compare.assertPdfEquals


infix fun ByteArray.shouldEqual(fileName:String) =
    assertPdfEquals(this.toInputStream(), checkNotNull(PdfRenderContextTest::class.java.getResourceAsStream(fileName)), fileName)
