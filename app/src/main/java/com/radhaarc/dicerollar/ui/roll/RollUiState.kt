package com.radhaarc.dicerollar.ui.roll

import com.radhaarc.dicerollar.data.AppSettings
import com.radhaarc.dicerollar.domain.RollResult

data class RollUiState(
    val settings: AppSettings = AppSettings(),
    val lastResult: RollResult? = null,
    val isRolling: Boolean = false
)
