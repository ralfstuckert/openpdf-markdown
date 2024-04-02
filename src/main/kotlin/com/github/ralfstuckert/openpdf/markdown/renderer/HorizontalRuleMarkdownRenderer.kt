package com.github.ralfstuckert.openpdf.markdown.renderer

import com.github.ralfstuckert.openpdf.markdown.*
import com.github.ralfstuckert.openpdf.markdown.PdfRenderContextKeys.COLOR
import com.github.ralfstuckert.openpdf.markdown.PdfRenderContextKeys.HORIZONTAL_ALIGNMENT
import com.github.ralfstuckert.openpdf.markdown.PdfRenderContextKeys.LINE_THICKNESS
import com.github.ralfstuckert.openpdf.markdown.PdfRenderContextKeys.PAGE_BREAK_ON_HORIZONTAL_RULE_ENABLED
import com.github.ralfstuckert.openpdf.markdown.PdfRenderContextKeys.WIDTH_PERCENTAGE
import com.lowagie.text.Chunk
import com.lowagie.text.pdf.draw.LineSeparator
import org.intellij.markdown.MarkdownTokenTypes.Companion.HORIZONTAL_RULE
import org.intellij.markdown.ast.ASTNode

class HorizontalRuleMarkdownRenderer: AbstractMarkdownRenderer() {

    companion object {
        val HORIZONTAL_RULE_RENDER_CONTEXT_KEY = MarkdownRenderContextKey(HORIZONTAL_RULE.name)
    }

    override val handledNodeTypes = listOf(HORIZONTAL_RULE)

    override fun setupDefaultRenderContexts(registry: MarkdownRenderContextRegistry) {
        registry.registerRenderContextFunction(HORIZONTAL_RULE_RENDER_CONTEXT_KEY) {
            derive {
                this[COLOR] = registry.defaultRenderContext.color
                this[LINE_THICKNESS] = registry.defaultRenderContext.fontSize *  0.07f
                this[WIDTH_PERCENTAGE] = registry.defaultRenderContext[WIDTH_PERCENTAGE] ?: 100f
                this[HORIZONTAL_ALIGNMENT] = registry.defaultRenderContext[HORIZONTAL_ALIGNMENT] ?: HorizontalAlignment.center
                this[PAGE_BREAK_ON_HORIZONTAL_RULE_ENABLED] = registry.defaultRenderContext[PAGE_BREAK_ON_HORIZONTAL_RULE_ENABLED] ?: false
            }
        }
    }

    override fun processNode(visitor: OpenPdfVisitor, rendererContext: MarkdownRendererContext, node: ASTNode) {
        checkNodeType(node)
        val horizontalRulerRenderContext = rendererContext.deriveRenderContext(HORIZONTAL_RULE_RENDER_CONTEXT_KEY)
        val pageBreakEnabled = horizontalRulerRenderContext[PAGE_BREAK_ON_HORIZONTAL_RULE_ENABLED] ?: false
        val element = if (pageBreakEnabled) {
            Chunk("").apply { setNewPage() }
        } else {
            val alignment = horizontalRulerRenderContext[HORIZONTAL_ALIGNMENT] ?: HorizontalAlignment.center
            val color = horizontalRulerRenderContext.color
            val widthPercentage = horizontalRulerRenderContext[WIDTH_PERCENTAGE] ?: 100f
            val lineThickness = horizontalRulerRenderContext[LINE_THICKNESS]
                ?: (horizontalRulerRenderContext.fontSize * 0.07f)
            LineSeparator(lineThickness, widthPercentage, color, alignment.ordinal, 0f)
        }
        rendererContext.parentPdfElement.add(element)
    }


}



