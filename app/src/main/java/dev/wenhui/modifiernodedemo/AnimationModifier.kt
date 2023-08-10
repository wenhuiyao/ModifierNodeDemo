package dev.wenhui.modifiernodedemo

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.TweenSpec
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.requireDensity
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

fun Modifier.animateInfinitely(active: Boolean) = this then InfiniteAnimationModifier(active)

data class InfiniteAnimationModifier(private val active: Boolean) :
    ModifierNodeElement<InfiniteAnimationNode>() {

    override fun create(): InfiniteAnimationNode = InfiniteAnimationNode(active)

    override fun update(node: InfiniteAnimationNode) {
        node.update(active)
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "infiniteAnimation"
        properties["active"] = active
    }
}

class InfiniteAnimationNode(private var active: Boolean) :
    Modifier.Node(),
    LayoutModifierNode,
    DrawModifierNode {

    private val colorAnimation = Animatable(START_COLOR)
    private val scaleAnimation = Animatable(initialValue = 1f)
    private val translationYAnimation = Animatable(initialValue = 0f)

    fun update(active: Boolean) {
        this.active = active
        if (active) {
            startAnimation()
        } else {
            stopAnimation()
        }
    }

    override fun onAttach() {
        if (active) {
            startAnimation()
        }
    }

    private fun startAnimation() {
        if (isAttached && !colorAnimation.isRunning) {
            coroutineScope.launch {
                colorAnimation.animateTo(
                    targetValue = END_COLOR,
                    animationSpec = InfiniteRepeatableSpec(
                        TweenSpec(durationMillis = 1500),
                        repeatMode = RepeatMode.Reverse,
                    )
                )
            }
            coroutineScope.launch {
                scaleAnimation.animateTo(
                    targetValue = 1.2f,
                    animationSpec = InfiniteRepeatableSpec(
                        TweenSpec(durationMillis = 1600),
                        repeatMode = RepeatMode.Reverse,
                    )
                )
            }
            coroutineScope.launch {
                translationYAnimation.animateTo(
                    targetValue = with(requireDensity()) { -10.dp.toPx() },
                    animationSpec = InfiniteRepeatableSpec(
                        TweenSpec(durationMillis = 1600),
                        repeatMode = RepeatMode.Reverse,
                    )
                )
            }
        }
    }

    private fun stopAnimation() {
        if (isAttached && colorAnimation.isRunning) {
            coroutineScope.launch {
                colorAnimation.snapTo(START_COLOR)
                scaleAnimation.snapTo(1f)
                translationYAnimation.snapTo(0f)
            }
        }
    }

    /**** DrawModifierNode ******/

    override fun ContentDrawScope.draw() {
        withTransform(
            transformBlock = {
                scale(scaleAnimation.value)
                translate(left = 0f, top = translationYAnimation.value)
            }
        ) {
            this@draw.drawContent()
            drawRect(colorAnimation.value, blendMode = BlendMode.SrcIn)
        }
    }

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureResult {
        val placeable = measurable.measure(constraints)
        return layout(placeable.width, placeable.height) {
            placeable.placeRelativeWithLayer(
                0, 0, layerBlock = {
                    compositingStrategy = CompositingStrategy.Offscreen
                }
            )
        }
    }

    companion object {
        val START_COLOR = Color.Red
        val END_COLOR = Color.Green
    }
}