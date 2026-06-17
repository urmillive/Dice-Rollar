package com.radhaarc.dicerollar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BottomBar(
    count: Int,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    onSettings: () -> Unit,
    decrementEnabled: Boolean,
    incrementEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircleButton(
            icon = { Icon(Icons.Filled.Remove, contentDescription = "Decrease dice") },
            bg = Color(0xFF7DD8A6),
            enabled = decrementEnabled,
            onClick = onDecrement,
            modifier = Modifier.weight(1f)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .background(Color.White, RoundedCornerShape(20.dp))
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = count.toString(),
                color = Color(0xFF1A1112),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }
        CircleButton(
            icon = { Icon(Icons.Filled.Add, contentDescription = "Increase dice") },
            bg = Color(0xFF7DD8A6),
            enabled = incrementEnabled,
            onClick = onIncrement,
            modifier = Modifier.weight(1f)
        )
        CircleButton(
            icon = { Icon(Icons.Filled.Settings, contentDescription = "Settings") },
            bg = Color.White,
            enabled = true,
            onClick = onSettings,
            modifier = Modifier.size(52.dp)
        )
    }
}

@Composable
private fun CircleButton(
    icon: @Composable () -> Unit,
    bg: Color,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                color = if (enabled) bg else bg.copy(alpha = 0.4f),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(enabled = enabled) { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        icon()
    }
}
