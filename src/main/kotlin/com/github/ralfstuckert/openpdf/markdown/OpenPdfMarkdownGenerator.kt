package com.github.ralfstuckert.openpdf.markdown

import org.intellij.markdown.flavours.MarkdownFlavourDescriptor
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.parser.MarkdownParser
import org.openpdf.text.Element
import org.openpdf.text.Paragraph


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

