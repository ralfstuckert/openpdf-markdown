package com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.provider

import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.ElementProvider
import org.intellij.markdown.IElementType
import org.intellij.markdown.ast.ASTNode

abstract class AbstractElementProvider: ElementProvider {

    fun checkNodeType(node: ASTNode) {
        assert(node.type in handledNodeTypes) { "unhandled node type '${node.type}'" }
    }

    fun getChildNode(node:ASTNode, type: IElementType):ASTNode =
        getChildNodeOrNull(node, type) ?: throw IllegalStateException("child node of type $type not found")

    fun getChildNodeOrNull(node:ASTNode, type: IElementType):ASTNode? =
        node.children.find {
            it.type == type
        }

}