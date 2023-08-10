package dev.wenhui.modifiernodedemo

import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.SuspendingPointerInputModifierNode
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.DelegatingNode
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.PointerInputModifierNode
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize

fun Modifier.transform(enabled: Boolean) = this then TransformModifier(enabled)

private data class TransformModifier(private val enabled: Boolean) :
    ModifierNodeElement<TransformNode>() {
    override fun create(): TransformNode = TransformNode(enabled)
    override fun update(node: TransformNode) {
        node.update(enabled)
    }
    override fun InspectorInfo.inspectableProperties() {
        name = "transform"
        properties["enabled"] = enabled
    }
}

private class TransformNode(private var enabled: Boolean) : DelegatingNode(),
    LayoutModifierNode, PointerInputModifierNode {

    private var translation by mutableStateOf(Offset.Zero)
    private var scale by mutableFloatStateOf(1f)
    private var rotation by mutableFloatStateOf(0f)

    private val pointerInputNode = delegate(
        SuspendingPointerInputModifierNode {
            if (!enabled) return@SuspendingPointerInputModifierNode
            detectTransformGestures { _, pan, zoom, rotation ->
                translation += pan
                this@TransformNode.rotation += rotation
                scale *= zoom
            }
        }
    )

    fun update(enabled: Boolean) {
        if (enabled != this.enabled) {
            this.enabled = enabled
            pointerInputNode.resetPointerInputHandler()
        }
    }

    override fun onReset() {
        translation = Offset.Zero
        scale = 1f
        rotation = 0f
    }

    override fun onPointerEvent(
        pointerEvent: PointerEvent,
        pass: PointerEventPass,
        bounds: IntSize
    ) {
        pointerInputNode.onPointerEvent(pointerEvent, pass, bounds)
    }

    override fun onCancelPointerInput() {
        pointerInputNode.onCancelPointerInput()
    }

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureResult {
        val placeable = measurable.measure(constraints)
        return layout(placeable.width, placeable.height) {
            placeable.placeWithLayer(0, 0, layerBlock = {
                translationX = translation.x
                translationY = translation.y
                scaleX = scale
                scaleY = scale
                rotationZ = rotation
            })
        }
    }
}