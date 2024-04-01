package com.github.ralfstuckert.openpdf.markdown

import com.github.ralfstuckert.openpdf.markdown.provider.TableProvider.Companion.TABLE_RENDER_CONTEXT_KEY
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

