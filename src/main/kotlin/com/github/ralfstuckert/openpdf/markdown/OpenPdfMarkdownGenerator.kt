package com.github.ralfstuckert.openpdf.markdown

import com.lowagie.text.Element
import com.lowagie.text.Paragraph
import org.intellij.markdown.flavours.MarkdownFlavourDescriptor
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.parser.MarkdownParser


class OpenPdfMarkdownGenerator {

    fun generate(markdownText: String, registry: MarkdownRendererRegistry = MarkdownRendererRegistry(defaultRenderContext),): Element {
        val flavour: MarkdownFlavourDescriptor = GFMFlavourDescriptor()
        val parsedTree = MarkdownParser(flavour).buildMarkdownTreeFromString(markdownText)
        val visitor = OpenPdfVisitor(markdownText, registry)
        val paragraph = Paragraph()
        visitor.visitNode(paragraph, registry.defaultRenderContext, parsedTree)
        return paragraph
    }
}

