package com.radhaarc.dicerollar.ui.roll

sealed interface RollEvent {
    data object Roll : RollEvent
    data object IncrementCount : RollEvent
    data object DecrementCount : RollEvent
}
