package dev.wenhui.modifiernodedemo

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun HandleGestureScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var animationActive by remember { mutableStateOf(false) }
    var dragEnabled by remember { mutableStateOf(true) }
    var transformEnabled by remember { mutableStateOf(false) }

    Box(modifier = modifier.padding(16.dp)) {
        InteractionRadioGroup(
            dragAndTapEnabled = dragEnabled,
            transformEnabled = transformEnabled,
            onDragAndTapClick = {
                dragEnabled = true
                transformEnabled = false
            },
            onTransformClick = {
                dragEnabled = false
                transformEnabled = true
            },
        )
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .dragAndTap(dragEnabled) {
                    Toast
                        .makeText(context, "Tapped", Toast.LENGTH_SHORT)
                        .show()
                }
                .transform(transformEnabled)
//                .showPositionInfo(true)
        ) {
            AnimatedDownloadIcon(active = animationActive)
        }

        Button(
            onClick = { animationActive = !animationActive },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 36.dp)
        ) {
            Text(text = if (animationActive) "Stop animation" else "Start animation")
        }
    }
}

@Composable
fun InteractionRadioGroup(
    dragAndTapEnabled: Boolean,
    transformEnabled: Boolean,
    onDragAndTapClick: () -> Unit,
    onTransformClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.selectableGroup()) {
        RadioTextButton(
            selected = dragAndTapEnabled,
            onClick = onDragAndTapClick,
            text = "Drag and tap"
        )
        RadioTextButton(
            selected = transformEnabled,
            onClick = onTransformClick,
            text = "Transform"
        )
    }
}


@Composable
private fun RadioTextButton(
    selected: Boolean,
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        RadioButton(selected = selected, onClick = onClick)
        Spacer(Modifier.width(8.dp))
        Text(text = text, style = MaterialTheme.typography.bodyMedium)
    }
}