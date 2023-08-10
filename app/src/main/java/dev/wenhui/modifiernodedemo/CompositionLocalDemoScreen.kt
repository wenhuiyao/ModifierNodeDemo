package dev.wenhui.modifiernodedemo

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.ObserverModifierNode
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.node.observeReads
import androidx.compose.ui.platform.InspectorInfo

@Composable
fun CompositionLocalDemoScreen(modifier: Modifier = Modifier) {
    var color by remember { mutableStateOf(Color.Unspecified) }
    CompositionLocalProvider(
        LocalCompositionLocalValue provides color
    ) {
        Box(modifier.testCompositionLocal()) {
            Button(
                onClick = { color = randomColor() },
                modifier = Modifier.align(Alignment.Center)
            ) {
                Text(text = "Generate a random background")
            }
        }
    }
}

private val LocalCompositionLocalValue = compositionLocalOf { Color.Unspecified }

private fun Modifier.testCompositionLocal() = this then CompositionLocalModifier(true)

private data class CompositionLocalModifier(private val enabled: Boolean) :
    ModifierNodeElement<CompositionLocalNode>() {
    override fun create(): CompositionLocalNode = CompositionLocalNode(enabled)
    override fun update(node: CompositionLocalNode) {
        node.update(enabled)
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "compositionLocal"
        properties["enabled"] = enabled
    }
}

private class CompositionLocalNode(private var enabled: Boolean) : Modifier.Node(),
    CompositionLocalConsumerModifierNode,
    DrawModifierNode,
    ObserverModifierNode {
    private var localValue: Color? = null

    fun update(enabled: Boolean) {
        this.enabled = enabled
    }

    override fun onAttach() {
        onObservedReadsChanged()
    }

    override fun ContentDrawScope.draw() {
        localValue?.let { drawRect(it) }
        drawContent()
        // This is automatically tracked, and update within draw scope
//        Log.d(TAG, "draw: current value is ${currentValueOf(LocalValue)}")
    }

    override fun onObservedReadsChanged() {
        observeReads {
            localValue = currentValueOf(LocalCompositionLocalValue)
        }
        invalidateDraw()
    }
}