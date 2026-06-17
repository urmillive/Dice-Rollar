package com.radhaarc.dicerollar.domain

import kotlin.random.Random

class DiceRoller(
    private val random: Random = Random.Default,
    private val now: () -> Long = { System.currentTimeMillis() }
) {
    fun roll(request: RollRequest): RollResult {
        val values = List(request.count) { random.nextInt(1, request.die.sides + 1) }
        val total = values.sum() + request.modifier
        return RollResult(
            die = request.die,
            count = request.count,
            modifier = request.modifier,
            values = values,
            total = total,
            timestamp = now()
        )
    }
}
