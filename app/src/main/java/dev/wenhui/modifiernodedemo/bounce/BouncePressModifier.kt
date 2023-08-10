package dev.wenhui.modifiernodedemo.bounce

import androidx.compose.foundation.CombinedClickableNode
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.node.DelegatingNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.PointerInputModifierNode
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.IntSize


/**
 * Show bounce effect on press, and end press effect immediately right after tap registered
 * before press release
 */
fun Modifier.bouncePress(
    onLongClickLabel: String? = null,
    onLongClick: (() -> Unit)? = null,
    onDoubleClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: () -> Unit,
) = this then BouncePressModifier(
    onClick,
    onLongClickLabel,
    onLongClick,
    onDoubleClick,
    enabled,
    onClickLabel,
    role,
)

private data class BouncePressModifier(
    private val onClick: () -> Unit,
    private val onLongClickLabel: String?,
    private val onLongClick: (() -> Unit)?,
    private val onDoubleClick: (() -> Unit)?,
    private val enabled: Boolean,
    private val onClickLabel: String?,
    private val role: Role?,
) : ModifierNodeElement<BouncePressNode>() {
    override fun create() = BouncePressNode(
        onClick,
        onLongClickLabel,
        onLongClick,
        onDoubleClick,
        enabled,
        onClickLabel,
        role,
    )

    override fun update(node: BouncePressNode) {
        node.update(
            onClick,
            onLongClickLabel,
            onLongClick,
            onDoubleClick,
            enabled,
            onClickLabel,
            role,
        )
    }

    override fun InspectorInfo.inspectableProperties() {

    }
}

@OptIn(ExperimentalFoundationApi::class)
private class BouncePressNode(
    onClick: () -> Unit,
    onLongClickLabel: String?,
    onLongClick: (() -> Unit)?,
    onDoubleClick: (() -> Unit)?,
    enabled: Boolean,
    onClickLabel: String?,
    role: Role?,
) : DelegatingNode(), PointerInputModifierNode {

    private val interactionSource = MutableInteractionSource()

    /**
     * Delegate clickable logic to CombinedClickableNode
     */
    private val clickableNode = delegate(
        // This is experimental api, if it's removed or hidden,  we need to write
        // our own version of it, using SuspendingPointerInputModifierNode and handle
        // semantics properties
        CombinedClickableNode(
            onClick = onClick.wrap(),
            onLongClickLabel = onLongClickLabel,
            onLongClick = onLongClick?.wrap(),
            onDoubleClick = onDoubleClick?.wrap(),
            interactionSource = interactionSource,
            enabled = enabled,
            onClickLabel = onClickLabel,
            role = role,
        )
    )

    /**
     * Delegate the bounce effect to BouncePressEffectNode for handling the animation
     */
    private val bounceEffectNode = delegate(
        BouncePressEffectNode(interactionSource)
    )

    /** Wrap click listener to notify press end */
    private fun (() -> Unit).wrap(): (() -> Unit) {
        return {
            bounceEffectNode.tryReleasePress()
            this.invoke()
        }
    }

    fun update(
        onClick: () -> Unit,
        onLongClickLabel: String?,
        onLongClick: (() -> Unit)?,
        onDoubleClick: (() -> Unit)?,
        enabled: Boolean,
        onClickLabel: String?,
        role: Role?,
    ) {
        clickableNode.update(
            onClick = onClick.wrap(),
            onLongClickLabel = onLongClickLabel,
            onLongClick = onLongClick?.wrap(),
            onDoubleClick = onDoubleClick?.wrap(),
            interactionSource = interactionSource,
            enabled = enabled,
            onClickLabel = onClickLabel,
            role = role,
        )
    }

    override fun onPointerEvent(
        pointerEvent: PointerEvent,
        pass: PointerEventPass,
        bounds: IntSize
    ) {
        clickableNode.onPointerEvent(pointerEvent, pass, bounds)
    }

    override fun onCancelPointerInput() {
        clickableNode.onCancelPointerInput()
    }
}