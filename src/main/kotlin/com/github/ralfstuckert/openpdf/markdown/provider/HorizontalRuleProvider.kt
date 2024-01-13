package com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.provider

import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.*
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.PdfRenderContextKeys.COLOR
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.PdfRenderContextKeys.HORIZONTAL_ALIGNMENT
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.PdfRenderContextKeys.LINE_THICKNESS
import com.github.ralfstuckert.com.github.ralfstuckert.openpdf.markdown.PdfRenderContextKeys.WIDTH_PERCENTAGE
import com.lowagie.text.pdf.draw.LineSeparator
import org.intellij.markdown.MarkdownTokenTypes.Companion.HORIZONTAL_RULE
import org.intellij.markdown.ast.ASTNode

class HorizontalRuleProvider: AbstractElementProvider() {

    companion object {
        val HORIZONTAL_RULE_RENDER_CONTEXT_KEY = ElementProviderRenderContextKey(HORIZONTAL_RULE.name)
    }

    override val handledNodeTypes = listOf(HORIZONTAL_RULE)

    override fun setupDefaultRenderContexts(registry: ElementProviderRenderContextRegistry) {
        registry.registerRenderContextFunction(HORIZONTAL_RULE_RENDER_CONTEXT_KEY) {
            derive {
                this[COLOR] = registry.defaultRenderContext.color
                this[LINE_THICKNESS] = registry.defaultRenderContext.fontSize *  0.07f
                this[WIDTH_PERCENTAGE] = registry.defaultRenderContext[WIDTH_PERCENTAGE] ?: 100f
                this[HORIZONTAL_ALIGNMENT] = registry.defaultRenderContext[HORIZONTAL_ALIGNMENT] ?: HorizontalAlignment.center
            }
        }
    }

    override fun processNode(visitor: OpenPdfVisitor, providerContext: ElementProviderContext, node: ASTNode) {
        checkNodeType(node)
        val horizontalRulerRenderContext = providerContext.deriveRenderContext(HORIZONTAL_RULE_RENDER_CONTEXT_KEY)
        val alignment = horizontalRulerRenderContext[HORIZONTAL_ALIGNMENT] ?: HorizontalAlignment.center
        val color = horizontalRulerRenderContext.color
        val widthPercentage = horizontalRulerRenderContext[WIDTH_PERCENTAGE] ?: 100f
        val lineThickness = horizontalRulerRenderContext[LINE_THICKNESS]
            ?: (horizontalRulerRenderContext.fontSize * 0.07f)

        val lineSeparator = LineSeparator(lineThickness, widthPercentage, color, alignment.ordinal, 0f)
        providerContext.parentPdfElement.add(lineSeparator)
    }


}



