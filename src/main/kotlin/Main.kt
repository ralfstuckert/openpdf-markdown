package com.github.ralfstuckert

import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.LeafASTNode
import org.intellij.markdown.ast.getTextInNode
import org.intellij.markdown.flavours.MarkdownFlavourDescriptor
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser


fun main() {

    val text = """
        | --this is striked through-- text
        | ~~this is striked through~~ text
        |
        |here I am
""".trimMargin()

val text2 = """
![jetbrains logo](https://www.jetbrains.com/company/brand/img/logo6.svg|x=1)

| simple | table | 
| --- | --- |
| column 1 | ![jetbrains logo](https://www.jetbrains.com/company/brand/img/logo6.svg{width=200,height=100}) | 
 """

    val flavour: MarkdownFlavourDescriptor = GFMFlavourDescriptor()
    val parsedTree = MarkdownParser(flavour).buildMarkdownTreeFromString(text)

    visit(parsedTree, text)

    val html = HtmlGenerator(text, parsedTree, flavour, false).generateHtml()
    println(html)
}

fun visit(node:ASTNode, source:String, indent:Int = 0) {
    println("${indent(indent)}${node.type} ${if (node is LeafASTNode) "leaf" else ""}: '${node.getTextInNode(source)}'")
    if (node.children.size > 0) {
//        println("visiting children of ${node.type}")
        node.children.forEach {
            visit(it, source, indent+1)
        }
//        println("finished children ${node.type}")
//        println()
    }
}

fun indent(indent:Int): String = "   ".repeat(indent)


