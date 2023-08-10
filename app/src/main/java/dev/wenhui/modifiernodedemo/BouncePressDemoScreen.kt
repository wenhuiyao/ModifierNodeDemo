package dev.wenhui.modifiernodedemo

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import dev.wenhui.modifiernodedemo.bounce.bouncePress

@Composable
fun BouncePressDemoScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    var animationActive by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        AnimatedDownloadIcon(
            active = animationActive,
            modifier = Modifier
                .align(Alignment.Center)
                .bouncePress(
                    onLongClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        Toast
                            .makeText(context, "Long clicked", Toast.LENGTH_SHORT)
                            .show()
                    },
                    onDoubleClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        Toast
                            .makeText(context, "Double clicked", Toast.LENGTH_SHORT)
                            .show()
                    }
                ) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    Toast
                        .makeText(context, "Clicked", Toast.LENGTH_SHORT)
                        .show()
                }
        )

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