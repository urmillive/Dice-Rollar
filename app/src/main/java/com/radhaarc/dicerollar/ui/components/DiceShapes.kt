package com.radhaarc.dicerollar.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import com.radhaarc.dicerollar.domain.Die
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class PolygonShape(
    private val sides: Int,
    private val rotationDeg: Float = -90f
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val radius = min(size.width, size.height) / 2f * 0.96f
        val center = Offset(size.width / 2f, size.height / 2f)
        val path = Path()
        for (i in 0 until sides) {
            val theta = Math.toRadians(rotationDeg + 360.0 * i / sides)
            val x = center.x + radius * cos(theta).toFloat()
            val y = center.y + radius * sin(theta).toFloat()
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        return Outline.Generic(path)
    }
}

fun Die.shape(): Shape = when (this) {
    Die.D4 -> PolygonShape(sides = 3, rotationDeg = -90f)
    Die.D6 -> RoundedCornerShape(percent = 22)
    Die.D8 -> PolygonShape(sides = 4, rotationDeg = -90f)
    Die.D10 -> PolygonShape(sides = 5, rotationDeg = 90f)
    Die.D12 -> PolygonShape(sides = 5, rotationDeg = -90f)
    Die.D20 -> PolygonShape(sides = 6, rotationDeg = -90f)
    Die.D100 -> RoundedCornerShape(percent = 50)
}
