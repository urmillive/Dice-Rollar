package com.radhaarc.dicerollar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
    modifier: Int,
    onDecrementCount: () -> Unit,
    onIncrementCount: () -> Unit,
    onDecrementModifier: () -> Unit,
    onIncrementModifier: () -> Unit,
    onHistory: () -> Unit,
    onSettings: () -> Unit,
    decrementCountEnabled: Boolean,
    incrementCountEnabled: Boolean,
    decrementModifierEnabled: Boolean,
    incrementModifierEnabled: Boolean,
    layoutModifier: Modifier = Modifier
) {
    Column(
        modifier = layoutModifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StepperRow(
            label = "Dice",
            value = count.toString(),
            onDecrement = onDecrementCount,
            onIncrement = onIncrementCount,
            decrementEnabled = decrementCountEnabled,
            incrementEnabled = incrementCountEnabled,
            accent = Color(0xFF7DD8A6)
        )
        StepperRow(
            label = "Modifier",
            value = formatModifier(modifier),
            onDecrement = onDecrementModifier,
            onIncrement = onIncrementModifier,
            decrementEnabled = decrementModifierEnabled,
            incrementEnabled = incrementModifierEnabled,
            accent = Color(0xFFFFC371)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircleButton(
                icon = { Icon(Icons.Filled.History, contentDescription = "History") },
                bg = Color.White,
                enabled = true,
                onClick = onHistory,
                modifier = Modifier.size(52.dp)
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
}

@Composable
private fun StepperRow(
    label: String,
    value: String,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    decrementEnabled: Boolean,
    incrementEnabled: Boolean,
    accent: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(20.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        )
        CircleButton(
            icon = { Icon(Icons.Filled.Remove, contentDescription = "Decrease $label") },
            bg = accent,
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
                text = value,
                color = Color(0xFF1A1112),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }
        CircleButton(
            icon = { Icon(Icons.Filled.Add, contentDescription = "Increase $label") },
            bg = accent,
            enabled = incrementEnabled,
            onClick = onIncrement,
            modifier = Modifier.weight(1f)
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

private fun formatModifier(value: Int): String = when {
    value > 0 -> "+$value"
    else -> value.toString()
}
