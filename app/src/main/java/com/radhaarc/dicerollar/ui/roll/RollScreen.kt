package com.radhaarc.dicerollar.ui.roll

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.radhaarc.dicerollar.data.SettingsStore
import com.radhaarc.dicerollar.ui.components.BottomBar
import com.radhaarc.dicerollar.ui.components.DiceCellState
import com.radhaarc.dicerollar.ui.components.DiceGrid
import com.radhaarc.dicerollar.ui.components.RollResultCard
import com.radhaarc.dicerollar.ui.theme.DicePalette

@Composable
fun RollScreen(
    onOpenSettings: () -> Unit,
    onOpenHistory: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RollViewModel = viewModel(factory = RollViewModel.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val settings = state.settings
    val haptics = LocalHapticFeedback.current

    val resultValues = state.lastResult?.values
    val colorSeed = state.lastResult?.timestamp ?: 0L
    val cells = remember(
        settings.diceCount,
        settings.sameColorAllDice,
        resultValues,
        colorSeed
    ) {
        val rng = kotlin.random.Random(colorSeed)
        val shuffled = DicePalette.cellColors.shuffled(rng)
        val baseColor = shuffled.first()
        List(settings.diceCount) { idx ->
            DiceCellState(
                value = resultValues?.getOrNull(idx),
                bgColor = if (settings.sameColorAllDice) baseColor else shuffled[idx % shuffled.size]
            )
        }
    }

    val rollInteraction = remember { MutableInteractionSource() }
    val barInteraction = remember { MutableInteractionSource() }

    val triggerRoll = {
        if (!state.isRolling) {
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            viewModel.onEvent(RollEvent.Roll)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = rollInteraction,
                    indication = null,
                    enabled = !state.isRolling
                ) { triggerRoll() }
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

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RollResultCard(
                notation = state.lastResult?.notation.orEmpty(),
                total = state.lastResult?.total ?: 0,
                visible = state.lastResult != null && !state.isRolling,
                modifier = Modifier.widthIn(max = 520.dp)
            )

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
                    modifier = settings.modifier,
                    onDecrementCount = { viewModel.onEvent(RollEvent.DecrementCount) },
                    onIncrementCount = { viewModel.onEvent(RollEvent.IncrementCount) },
                    onDecrementModifier = { viewModel.onEvent(RollEvent.DecrementModifier) },
                    onIncrementModifier = { viewModel.onEvent(RollEvent.IncrementModifier) },
                    onHistory = onOpenHistory,
                    onSettings = onOpenSettings,
                    decrementCountEnabled = settings.diceCount > 1 && !state.isRolling,
                    incrementCountEnabled = settings.diceCount < SettingsStore.MAX_DICE && !state.isRolling,
                    decrementModifierEnabled = settings.modifier > -SettingsStore.MAX_MODIFIER && !state.isRolling,
                    incrementModifierEnabled = settings.modifier < SettingsStore.MAX_MODIFIER && !state.isRolling
                )
            }
        }
    }
}
