package dev.wenhui.modifiernodedemo

import androidx.compose.ui.graphics.Color
import kotlin.random.Random


const val TAG = "modifierNodeDemo"

fun randomColor() = Color(Random.nextInt(0, 255), Random.nextInt(0, 255), Random.nextInt(0, 255))
