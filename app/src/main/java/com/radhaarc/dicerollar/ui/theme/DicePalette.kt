package com.radhaarc.dicerollar.ui.theme

import androidx.compose.ui.graphics.Color

object DicePalette {
    val cellColors: List<Color> = listOf(
        Color(0xFF65C8E8),
        Color(0xFF4FCDA1),
        Color(0xFFFF8FB1),
        Color(0xFFFFB997),
        Color(0xFFB8A6F0),
        Color(0xFFAFE070),
        Color(0xFFFF9E5C),
        Color(0xFFFCE07B),
        Color(0xFF7DD8F0),
        Color(0xFFFF7E7E),
        Color(0xFF8FD9F5),
        Color(0xFFFFC97A)
    )

    fun colorAt(index: Int): Color = cellColors[index % cellColors.size]
}
