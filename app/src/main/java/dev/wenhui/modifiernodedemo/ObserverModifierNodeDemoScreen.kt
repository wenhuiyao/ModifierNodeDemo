package dev.wenhui.modifiernodedemo

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.ObserverModifierNode
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.node.observeReads
import androidx.compose.ui.platform.InspectorInfo

@Composable
fun ObserverModifierNodeDemoScreen(modifier: Modifier = Modifier) {
    // This is very similar to how drawWithCache or graphicLayer scope work, when
    // state within scope updated, Modifier.Node will be notified, and react accordingly
    // See CacheDrawModifierNodeImpl and NodeCoordinator.updateLayerParameters

    var color by remember { mutableStateOf(Color.Unspecified) }
    Box(modifier.updateColor {
        this.color = color
    }) {
        Button(
            onClick = { color = randomColor() },
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text(text = "Generate a random background")
        }
    }
}

private fun Modifier.updateColor(stateScope: RandomColorScope.() -> Unit) =
    this then DrawColorModifier(stateScope)


private data class DrawColorModifier(private val scope: RandomColorScope.() -> Unit) :
    ModifierNodeElement<DrawColorNode>() {
    override fun create() = DrawColorNode(scope)

    override fun update(node: DrawColorNode) {
        node.update(scope)
    }

    override fun InspectorInfo.inspectableProperties() {
        name="drawColor"
        properties["scope"] = scope
    }
}

private class DrawColorNode(private var scope: RandomColorScope.() -> Unit) : Modifier.Node(),
    ObserverModifierNode, DrawModifierNode {

    private val scopeImpl = RandomColorScopeImpl()
    private var stateChanged = true

    fun update(scope: RandomColorScope.() -> Unit) {
        Log.d("wenhuiTest", "update: ")
        this.scope = scope
        stateChanged = true
        invalidateDraw()
    }

    override fun onObservedReadsChanged() {
        Log.d(TAG, "onObservedReadsChanged: changed")
        stateChanged = true
        invalidateDraw()
    }

    override fun ContentDrawScope.draw() {
        if (stateChanged) {
            invalidateState()
        }
        drawRect(scopeImpl.color)
        drawContent()
    }

    private fun invalidateState() {
        observeReads {
            scope.invoke(scopeImpl)
        }
        Log.d(TAG, "observerReads: number is ${scopeImpl.color}")
        stateChanged = false
    }

}

interface RandomColorScope {
    var color: Color
}

private class RandomColorScopeImpl(override var color: Color = Color.Unspecified) : RandomColorScope