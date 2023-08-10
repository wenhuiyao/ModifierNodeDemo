package dev.wenhui.modifiernodedemo

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun AnimatedDownloadIcon(active: Boolean, modifier: Modifier = Modifier) {
    if (active) {
        val progress = remember { Animatable(0f) }
        val startColor = MaterialTheme.colorScheme.primary
        val targetColor = MaterialTheme.colorScheme.tertiary
        val color = remember(startColor) {
            androidx.compose.animation.Animatable(startColor)
        }
        val alpha = remember { Animatable(1f) }
        LaunchedEffect(true) {
            launch {
                progress.animateTo(
                    -0.5f,
                    animationSpec = InfiniteRepeatableSpec(
                        animation = TweenSpec(
                            durationMillis = 400,
                            delay = 100, easing = LinearEasing
                        ),
                        repeatMode = RepeatMode.Reverse
                    )
                )
            }
            launch {
                color.animateTo(
                    targetColor,
                    animationSpec = InfiniteRepeatableSpec(
                        animation = TweenSpec(
                            durationMillis = 1000,
                            delay = 100, easing = LinearEasing
                        ),
                        repeatMode = RepeatMode.Reverse
                    )
                )
            }
            launch {
                alpha.animateTo(
                    0.2f,
                    animationSpec = InfiniteRepeatableSpec(
                        animation = TweenSpec(
                            durationMillis = 1000,
                            delay = 100, easing = LinearEasing
                        ),
                        repeatMode = RepeatMode.Reverse
                    )
                )
            }
        }
        DownloadIcon(
            alpha = alpha.value,
            color = color.value,
            progress = progress.value,
            modifier = modifier
        )
    } else {
        DownloadIcon(
            modifier = modifier
        )
    }
}

@Composable
private fun DownloadIcon(
    modifier: Modifier = Modifier,
    alpha: Float = 1f,
    color: Color = MaterialTheme.colorScheme.primary,
    progress: Float = 0f,
) {
    Box(modifier = modifier) {
        Image(
            painter = painterResource(id = R.drawable.cloud),
            contentDescription = null,
            colorFilter = ColorFilter.tint(color),
            alpha = alpha,
            modifier = Modifier.size(200.dp),
        )
        Image(
            painter = painterResource(id = R.drawable.download),
            contentDescription = null,
            colorFilter = ColorFilter.tint(color),
            alpha = alpha,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(top = 80.dp)
                .size(140.dp)
                .clipToBounds()
                .graphicsLayer {
                    translationY = size.height * progress
                },
        )
    }
}
