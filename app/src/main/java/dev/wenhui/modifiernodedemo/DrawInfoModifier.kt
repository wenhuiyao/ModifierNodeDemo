package dev.wenhui.modifiernodedemo

import android.annotation.SuppressLint
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.GlobalPositionAwareModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.node.requireDensity
import androidx.compose.ui.node.requireLayoutDirection
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.toOffset
import kotlin.math.roundToInt

@Stable
fun Modifier.showPositionInfo(enabled: Boolean) = this then DrawInfoModifier(enabled)

@SuppressLint("ModifierNodeInspectableProperties")
private data class DrawInfoModifier(private val enabled: Boolean) :
    ModifierNodeElement<DrawInfoNode>() {
    override fun create(): DrawInfoNode = DrawInfoNode(enabled)

    override fun update(node: DrawInfoNode) {
        node.update(enabled)
    }
}

@SuppressLint("SuspiciousCompositionLocalModifierRead")
private class DrawInfoNode(private var enabled: Boolean) :
    Modifier.Node(),
    DrawModifierNode,
    GlobalPositionAwareModifierNode,
    CompositionLocalConsumerModifierNode {

    private val textMeasurer by lazy {
        check(isAttached)
        val fontFamilyResolver = currentValueOf(LocalFontFamilyResolver)
        val density = requireDensity()
        val layoutDirection = requireLayoutDirection()
        TextMeasurer(fontFamilyResolver, density, layoutDirection, 8)
    }
    private var positionText: TextLayoutResult? = null

    fun update(enabled: Boolean) {
        this.enabled = enabled
    }

    override fun onReset() {
        positionText = null
    }

    /**** GlobalPositionAwareModifierNode *******/

    override fun onGloballyPositioned(coordinates: LayoutCoordinates) {
        val bounds = coordinates.boundsInParent()
        val center = bounds.center.round()
        val size = bounds.size
        positionText = textMeasurer.measure(
            text = AnnotatedString("center: (${center.x}, ${center.y})\nsize: (${size.width.roundToInt()}, ${size.height.roundToInt()})"),
            constraints = Constraints(
                maxWidth = coordinates.size.width,
                maxHeight = coordinates.size.height
            ),
        )
        invalidateDraw()
    }

    /**** DrawModifierNode *******/

    override fun ContentDrawScope.draw() {
        drawContent()
        if (enabled) {
            positionText?.let {
                drawText(it, topLeft = size.center - it.size.center.toOffset())
            }
        }
    }
}