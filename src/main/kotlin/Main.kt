package com.github.ralfstuckert

import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.LeafASTNode
import org.intellij.markdown.ast.accept
import org.intellij.markdown.ast.getTextInNode
import org.intellij.markdown.flavours.MarkdownFlavourDescriptor
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser


fun main() {

    val text = """
### This is [a](http://wtf) Test
Would you do with a **drunken** _sailor_?
And some very long sentence and some very long sentence and some very long sentence and some very long sentence and some very long sentence and some very long sentence and some very long sentence and some very long sentence and some very long sentence and some very long sentence and some very long sentence and some very long sentence and some very long sentence and some very long sentence and some very long sentence and some very long sentence 

![image search api](https://user-images.githubusercontent.com/110724391/184472398-c590b47c-e1f2-41f8-87e6-2a1f68e8850d.png|width=200,height=100)

asdfsd [link test ![image search api](https://user-images.githubusercontent.com/110724391/184472398-c590b47c-e1f2-41f8-87e6-2a1f68e8850d.png)](https://www.youtube.com/watch?v=3HIr0imLgxM)

| Syntax      | Description | **Test Text**     |
| :---        |    :----:   |          ---: |
| Header      | Title       | Here's this   |
| Paragraph   | ![x](https://user-images.githubusercontent.com/110724391/184472398-c590b47c-e1f2-41f8-87e6-2a1f68e8850d.png|width=200)        | And more      |
"""

val text2 = """
![jetbrains logo](https://www.jetbrains.com/company/brand/img/logo6.svg|x=1)

| simple | table | 
| --- | --- |
| column 1 | ![jetbrains logo](https://www.jetbrains.com/company/brand/img/logo6.svg{width=200,height=100}) | 
 """

    val flavour: MarkdownFlavourDescriptor = GFMFlavourDescriptor()
    val parsedTree = MarkdownParser(flavour).buildMarkdownTreeFromString(text2)

    visit(parsedTree, text2)

    val html = HtmlGenerator(text2, parsedTree, flavour, false).generateHtml()
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