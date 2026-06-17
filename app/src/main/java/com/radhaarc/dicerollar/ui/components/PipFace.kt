package com.radhaarc.dicerollar.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

/**
 * Renders the classic dot (pip) pattern of a d6 face for [value] in 1..6.
 * Layout uses a 3x3 grid of positions (corners, edges, center).
 */
@Composable
fun PipFace(
    value: Int,
    size: Dp,
    pipColor: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val pipRadius = (minOf(w, h) * 0.085f)
        val inset = minOf(w, h) * 0.22f
        val cx = w / 2f
        val cy = h / 2f
        val left = inset
        val right = w - inset
        val top = inset
        val bottom = h - inset

        val positions: List<Offset> = when (value) {
            1 -> listOf(Offset(cx, cy))
            2 -> listOf(Offset(left, top), Offset(right, bottom))
            3 -> listOf(Offset(left, top), Offset(cx, cy), Offset(right, bottom))
            4 -> listOf(
                Offset(left, top), Offset(right, top),
                Offset(left, bottom), Offset(right, bottom)
            )
            5 -> listOf(
                Offset(left, top), Offset(right, top),
                Offset(cx, cy),
                Offset(left, bottom), Offset(right, bottom)
            )
            6 -> listOf(
                Offset(left, top), Offset(right, top),
                Offset(left, cy), Offset(right, cy),
                Offset(left, bottom), Offset(right, bottom)
            )
            else -> emptyList()
        }

        positions.forEach { p ->
            drawCircle(color = pipColor, radius = pipRadius, center = p)
        }
    }
}
