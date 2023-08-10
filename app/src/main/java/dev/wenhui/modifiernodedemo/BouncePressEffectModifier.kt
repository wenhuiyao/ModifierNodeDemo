package dev.wenhui.modifiernodedemo

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Constraints
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch

fun Modifier.bouncePressEffect(interactionSource: InteractionSource) =
    this then BouncePressEffectModifier(interactionSource)

private data class BouncePressEffectModifier(
    private val interactionSource: InteractionSource
) : ModifierNodeElement<BouncePressEffectNode>() {
    override fun create() = BouncePressEffectNode(interactionSource)
    override fun update(node: BouncePressEffectNode) {
        node.update(interactionSource)
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "BouncePressEffect"
        properties["interactionSource"] = interactionSource
    }
}

private const val MIN_SCALE = 0.8f

private class BouncePressEffectNode(
    private var interactionSource: InteractionSource
) : Modifier.Node(), LayoutModifierNode {

    private var scale by mutableFloatStateOf(1f)
    private var effectJob: Job? = null
    private var bounceDownAnimation: Job? = null
    private var bounceUpAnimation: Job? = null

    private val animationSpec: SpringSpec<Float>
        get() = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow,
        )

    fun update(interactionSource: InteractionSource) {
        onReset()
        this.interactionSource = interactionSource
        startCollectInteractions()
    }

    override fun onAttach() {
        startCollectInteractions()
    }

    override fun onReset() {
        scale = 1f
        effectJob?.cancel()
        effectJob = null
        bounceDownAnimation?.cancel()
        bounceDownAnimation = null
        bounceUpAnimation?.cancel()
        bounceUpAnimation = null
    }

    private fun startCollectInteractions() {
        if (!isAttached) return
        effectJob = coroutineScope.launch {
            interactionSource.interactions.collect { interaction ->
                if (interaction is PressInteraction.Press) {
                    runBounceDownAnimation()
                } else if (interaction is PressInteraction.Cancel || interaction is PressInteraction.Release) {
                    runBounceUpAnimation()
                }
            }
        }
    }

    private fun runBounceDownAnimation() {
        if (!isAttached || bounceDownAnimation?.isActive == true) return

        bounceDownAnimation = coroutineScope.launch {
            Animatable(1f).animateTo(
                targetValue = MIN_SCALE,
                animationSpec = animationSpec,
            ) {
                scale = value
            }
        }
    }

    private fun runBounceUpAnimation() {
        if (!isAttached || bounceUpAnimation?.isActive == true) return

        bounceUpAnimation = coroutineScope.launch {
            bounceDownAnimation?.cancelAndJoin()
            Animatable(scale).animateTo(
                targetValue = 1f,
                animationSpec = animationSpec,
            ) {
                scale = value
            }
        }
    }

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureResult {
        val placeable = measurable.measure(constraints)
        return layout(placeable.width, placeable.height) {
            // A layer is a RenderNode backed layer after API 23, it's very efficient when
            // update transform properties. It effectively skip application execution, only
            // need to update its displayList's properties. See more on RenderNode
            // Before API 23, it's ViewLayer
            placeable.placeWithLayer(0, 0, layerBlock = {
                scaleX = this@BouncePressEffectNode.scale
                scaleY = this@BouncePressEffectNode.scale
            })
        }
    }
}