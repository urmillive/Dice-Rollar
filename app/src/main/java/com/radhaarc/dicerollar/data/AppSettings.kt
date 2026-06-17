package com.radhaarc.dicerollar.data

import com.radhaarc.dicerollar.domain.Die

enum class DisplayMode { Dots, Numbers }

data class AppSettings(
    val dieType: Die = Die.D6,
    val displayMode: DisplayMode = DisplayMode.Dots,
    val showBorders: Boolean = true,
    val sameColorAllDice: Boolean = false,
    val pipColorIndex: Int = 0,
    val diceCount: Int = 3
)
