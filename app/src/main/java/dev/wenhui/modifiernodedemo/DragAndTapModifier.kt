package dev.wenhui.modifiernodedemo

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.node.invalidatePlacement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.round

fun Modifier.dragAndTap(enabled: Boolean, onTapCallback: () -> Unit) =
    this then DragAndTapModifier(enabled, onTapCallback)

private data class DragAndTapModifier(
    private val enabled: Boolean,
    private val onTapCallback: () -> Unit
) :
    ModifierNodeElement<DragAndTapNode>() {
    override fun create(): DragAndTapNode = DragAndTapNode(enabled, onTapCallback)
    override fun update(node: DragAndTapNode) {
        node.update(enabled, onTapCallback)
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "dragAndTap"
        properties["enabled"] = enabled
        properties["onTapCallback"] = onTapCallback
    }
}

class DragAndTapNode(
    private var enabled: Boolean,
    private var onTapCallback: () -> Unit
) : DelegatingNode(),
    LayoutModifierNode,
    PointerInputModifierNode {

    private var positionOffset: Offset = Offset.Zero

    fun update(enabled: Boolean, onTapCallback: () -> Unit) {
        if (onTapCallback != this.onTapCallback || enabled != this.enabled) {
            this.onTapCallback = onTapCallback
            this.enabled = enabled
            dragNode.resetPointerInputHandler()
            tapNode.resetPointerInputHandler()
        }
    }

    override fun onReset() {
        positionOffset = Offset.Zero
    }

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureResult {
        val placeable = measurable.measure(constraints)
        return layout(placeable.width, placeable.height) {
            placeable.place(positionOffset.round())
        }
    }

    private val dragNode = delegate(SuspendingPointerInputModifierNode {
        if (!enabled) return@SuspendingPointerInputModifierNode

        detectDragGestures { _, dragAmount ->
            positionOffset += Offset(dragAmount.x, dragAmount.y)
            // Just position change, measurement doesn't change, don't remeasure
//            invalidateMeasurement()

            // Invalidate placement block only
            invalidatePlacement()
        }
    })

    private val tapNode = delegate(SuspendingPointerInputModifierNode {
        if (!enabled) return@SuspendingPointerInputModifierNode
        detectTapGestures(onTap = { onTapCallback() })
    })

    override fun onPointerEvent(
        pointerEvent: PointerEvent,
        pass: PointerEventPass,
        bounds: IntSize
    ) {
        tapNode.onPointerEvent(pointerEvent, pass, bounds)
        dragNode.onPointerEvent(pointerEvent, pass, bounds)
    }

    override fun onCancelPointerInput() {
        tapNode.onCancelPointerInput()
        dragNode.onCancelPointerInput()
    }

    override fun onAttach() {
        positionOffset = Offset.Zero
    }
}