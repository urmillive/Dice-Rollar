package com.radhaarc.dicerollar.domain

data class RollRequest(
    val die: Die,
    val count: Int,
    val modifier: Int
) {
    init {
        require(count in 1..MAX_COUNT) { "count must be 1..$MAX_COUNT, was $count" }
        require(modifier in -MAX_MODIFIER..MAX_MODIFIER) {
            "modifier must be -$MAX_MODIFIER..$MAX_MODIFIER, was $modifier"
        }
    }

    companion object {
        const val MAX_COUNT = 10
        const val MAX_MODIFIER = 99
    }
}
