package com.radhaarc.dicerollar.ui.components

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.radhaarc.dicerollar.data.DisplayMode
import com.radhaarc.dicerollar.domain.Die

data class DiceCellState(
    val value: Int?,
    val bgColor: Color
)

@Composable
fun DiceGrid(
    die: Die,
    cells: List<DiceCellState>,
    rolling: Boolean,
    displayMode: DisplayMode,
    pipColor: Color,
    showBorder: Boolean,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val count = cells.size.coerceAtLeast(1)
        val isLandscape = maxWidth > maxHeight
        val cols = gridColumns(count = count, landscape = isLandscape)
        val rows = (count + cols - 1) / cols

        Column(modifier = Modifier.fillMaxSize()) {
            for (r in 0 until rows) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    for (c in 0 until cols) {
                        val idx = r * cols + c
                        if (idx < count) {
                            val cell = cells[idx]
                            DicePanel(
                                die = die,
                                value = cell.value,
                                rolling = rolling,
                                bgColor = cell.bgColor,
                                displayMode = displayMode,
                                pipColor = pipColor,
                                showBorder = showBorder,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f).fillMaxHeight())
                        }
                    }
                }
            }
        }
    }
}

private fun gridColumns(count: Int, landscape: Boolean): Int = if (landscape) {
    when {
        count <= 1 -> 1
        count <= 2 -> 2
        count <= 4 -> 2
        count <= 6 -> 3
        count <= 9 -> 3
        else -> 4
    }
} else {
    when {
        count <= 1 -> 1
        count <= 3 -> 1
        count <= 8 -> 2
        else -> 3
    }
}
