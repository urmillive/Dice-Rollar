package com.radhaarc.dicerollar.domain

import kotlinx.serialization.Serializable

@Serializable
enum class Die(val sides: Int, val label: String) {
    D4(4, "d4"),
    D6(6, "d6"),
    D8(8, "d8"),
    D10(10, "d10"),
    D12(12, "d12"),
    D20(20, "d20"),
    D100(100, "d100");

    companion object {
        val all: List<Die> = entries
    }
}
