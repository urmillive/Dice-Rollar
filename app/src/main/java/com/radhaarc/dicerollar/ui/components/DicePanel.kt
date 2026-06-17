package com.radhaarc.dicerollar.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.radhaarc.dicerollar.data.DisplayMode
import com.radhaarc.dicerollar.domain.Die
import kotlinx.coroutines.delay
import kotlin.math.min
import kotlin.random.Random

/**
 * A full-bleed dice cell painted with [bgColor]. Fills its parent.
 * Tumbles while [rolling] is true, settles on [value] with a small bounce afterwards.
 */
@Composable
fun DicePanel(
    die: Die,
    value: Int?,
    rolling: Boolean,
    bgColor: Color,
    displayMode: DisplayMode,
    pipColor: Color,
    showBorder: Boolean,
    modifier: Modifier = Modifier
) {
    val rotation = remember { Animatable(0f) }
    val scale = remember { Animatable(1f) }
    var displayedValue by remember(die) { mutableIntStateOf(value ?: 1) }

    LaunchedEffect(rolling, value, die) {
        if (rolling) {
            rotation.animateTo(
                targetValue = rotation.value + 720f + Random.nextInt(-90, 90),
                animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
            )
        } else if (value != null) {
            displayedValue = value
            scale.snapTo(1.2f)
            scale.animateTo(1f, tween(durationMillis = 240, easing = FastOutSlowInEasing))
        }
    }

    LaunchedEffect(rolling, die) {
        while (rolling) {
            displayedValue = Random.nextInt(1, die.sides + 1)
            delay(55)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor)
            .then(
                if (showBorder) Modifier.border(width = 1.dp, color = Color.White.copy(alpha = 0.18f))
                else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        BoxWithConstraints {
            val cellMin = min(maxWidth.value, maxHeight.value)
            val faceSize = (cellMin * 0.7f).coerceAtLeast(48f).dp

            Box(
                modifier = Modifier
                    .size(faceSize)
                    .graphicsLayer {
                        rotationZ = rotation.value
                        scaleX = scale.value
                        scaleY = scale.value
                    },
                contentAlignment = Alignment.Center
            ) {
                val showAsPips = die == Die.D6 && displayMode == DisplayMode.Dots
                if (showAsPips) {
                    PipFace(value = displayedValue, size = faceSize, pipColor = pipColor)
                } else {
                    Text(
                        text = displayedValue.toString(),
                        color = pipColor,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Black,
                        fontSize = (faceSize.value * 0.55f).coerceIn(28f, 120f).sp
                    )
                }
            }
        }
    }
}
