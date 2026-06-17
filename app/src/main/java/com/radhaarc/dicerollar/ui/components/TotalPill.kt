package com.radhaarc.dicerollar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun TotalPill(
    total: Int,
    modifier: Modifier = Modifier
) {
    Text(
        text = "Total: $total",
        color = Color.White,
        fontWeight = FontWeight.SemiBold,
        style = MaterialTheme.typography.titleMedium,
        modifier = modifier
            .background(
                color = Color(0xCC1A1112),
                shape = RoundedCornerShape(14.dp)
            )
            .padding(PaddingValues(horizontal = 18.dp, vertical = 8.dp))
    )
}
