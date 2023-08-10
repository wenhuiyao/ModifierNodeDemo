package dev.wenhui.modifiernodedemo

import android.annotation.SuppressLint
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.modifier.ModifierLocalModifierNode
import androidx.compose.ui.modifier.modifierLocalOf
import androidx.compose.ui.modifier.modifierLocalProvider
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement

@Composable
fun ModifierLocalDemoScreen(modifier: Modifier = Modifier) {
    var color by remember { mutableStateOf(Color.Unspecified) }
    val colorValue = color
    Box(modifier.provideLocalValue(colorValue)) {
        Button(
            onClick = { color = randomColor() },
            modifier = Modifier
                .align(Alignment.Center)
                .testModifierLocal()
        ) {
            Text(text = "Generate a random background")
        }
    }
}

private val ModifierLocalValue = modifierLocalOf { Color.Unspecified }

@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.provideLocalValue(value: Color): Modifier =
    modifierLocalProvider(ModifierLocalValue) { value }

fun Modifier.testModifierLocal() = this then ModifierLocalModifier(enabled = true)

@SuppressLint("ModifierNodeInspectableProperties")
private data class ModifierLocalModifier(private val enabled: Boolean) :
    ModifierNodeElement<ModifierLocalNode>() {
    override fun create(): ModifierLocalNode = ModifierLocalNode(enabled)
    override fun update(node: ModifierLocalNode) {
        node.update(enabled)
    }
}

private class ModifierLocalNode(private var enabled: Boolean) : Modifier.Node(),
    ModifierLocalModifierNode,
    DrawModifierNode {

    private val localValue: Color
        get() = if (isAttached) ModifierLocalValue.current else Color.Unspecified

    fun update(enabled: Boolean) {
        this.enabled = enabled
    }

    override fun ContentDrawScope.draw() {
        if (localValue.isSpecified) {
            drawRect(localValue)
        }
        drawContent()
        Log.d(TAG, "draw: local value is $localValue")
    }
}
