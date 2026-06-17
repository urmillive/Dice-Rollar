package com.radhaarc.dicerollar.domain

import kotlinx.serialization.Serializable

@Serializable
data class RollResult(
    val die: Die,
    val count: Int,
    val modifier: Int,
    val values: List<Int>,
    val total: Int,
    val timestamp: Long
) {
    val notation: String get() = buildString {
        append(count)
        append(die.label)
        when {
            modifier > 0 -> append("+").append(modifier)
            modifier < 0 -> append(modifier)
        }
    }
}
