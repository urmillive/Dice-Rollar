package com.radhaarc.dicerollar.ui.roll

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.radhaarc.dicerollar.data.SettingsStore
import com.radhaarc.dicerollar.ui.components.BottomBar
import com.radhaarc.dicerollar.ui.components.DiceCellState
import com.radhaarc.dicerollar.ui.components.DiceGrid
import com.radhaarc.dicerollar.ui.components.TotalPill
import com.radhaarc.dicerollar.ui.theme.DicePalette

@Composable
fun RollScreen(
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RollViewModel = viewModel(factory = RollViewModel.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val settings = state.settings

    val resultValues = state.lastResult?.values
    val cells = remember(settings.diceCount, settings.sameColorAllDice, resultValues) {
        val baseColor = DicePalette.colorAt(0)
        List(settings.diceCount) { idx ->
            DiceCellState(
                value = resultValues?.getOrNull(idx),
                bgColor = if (settings.sameColorAllDice) baseColor else DicePalette.colorAt(idx)
            )
        }
    }

    val rollInteraction = remember { MutableInteractionSource() }
    val barInteraction = remember { MutableInteractionSource() }

    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = rollInteraction,
                    indication = null,
                    enabled = !state.isRolling
                ) { viewModel.onEvent(RollEvent.Roll) }
        ) {
            DiceGrid(
                die = settings.dieType,
                cells = cells,
                rolling = state.isRolling,
                displayMode = settings.displayMode,
                pipColor = Color.White,
                showBorder = settings.showBorders
            )
        }

        AnimatedVisibility(
            visible = state.lastResult != null && !state.isRolling,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(WindowInsetsSides.Top)
                )
                .padding(top = 12.dp)
        ) {
            TotalPill(total = state.lastResult?.total ?: 0)
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .widthIn(max = 520.dp)
                    .background(
                        color = Color(0x55FFFFFF),
                        shape = RoundedCornerShape(28.dp)
                    )
                    .padding(PaddingValues(horizontal = 4.dp, vertical = 4.dp))
                    .clickable(
                        interactionSource = barInteraction,
                        indication = null
                    ) { /* swallow taps so they don't roll dice */ }
            ) {
                BottomBar(
                    count = settings.diceCount,
                    onDecrement = { viewModel.onEvent(RollEvent.DecrementCount) },
                    onIncrement = { viewModel.onEvent(RollEvent.IncrementCount) },
                    onSettings = onOpenSettings,
                    decrementEnabled = settings.diceCount > 1 && !state.isRolling,
                    incrementEnabled = settings.diceCount < SettingsStore.MAX_DICE && !state.isRolling
                )
            }
        }
    }
}

