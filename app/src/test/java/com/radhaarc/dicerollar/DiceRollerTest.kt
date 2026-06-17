package com.radhaarc.dicerollar

import com.radhaarc.dicerollar.domain.Die
import com.radhaarc.dicerollar.domain.DiceRoller
import com.radhaarc.dicerollar.domain.RollRequest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class DiceRollerTest {

    private fun roller(seed: Long = 42L, now: Long = 1_000L) =
        DiceRoller(random = Random(seed), now = { now })

    @Test
    fun `rolls within die bounds`() {
        val r = roller()
        repeat(200) {
            Die.all.forEach { die ->
                val result = r.roll(RollRequest(die, count = 1, modifier = 0))
                val v = result.values.single()
                assertTrue("$die value $v out of bounds", v in 1..die.sides)
            }
        }
    }

    @Test
    fun `count produces that many values`() {
        val result = roller().roll(RollRequest(Die.D20, count = 5, modifier = 0))
        assertEquals(5, result.values.size)
    }

    @Test
    fun `total equals sum of values plus modifier`() {
        val result = roller().roll(RollRequest(Die.D6, count = 3, modifier = 4))
        assertEquals(result.values.sum() + 4, result.total)
    }

    @Test
    fun `negative modifier subtracts from total`() {
        val result = roller().roll(RollRequest(Die.D20, count = 1, modifier = -3))
        assertEquals(result.values.sum() - 3, result.total)
    }

    @Test
    fun `timestamp comes from injected clock`() {
        val result = roller(now = 9_999L).roll(RollRequest(Die.D6, count = 1, modifier = 0))
        assertEquals(9_999L, result.timestamp)
    }

    @Test
    fun `same seed reproduces same roll`() {
        val a = roller(seed = 123L).roll(RollRequest(Die.D100, count = 4, modifier = 2))
        val b = roller(seed = 123L).roll(RollRequest(Die.D100, count = 4, modifier = 2))
        assertEquals(a.values, b.values)
        assertEquals(a.total, b.total)
    }

    @Test
    fun `notation includes positive modifier`() {
        val result = roller().roll(RollRequest(Die.D20, count = 2, modifier = 5))
        assertEquals("2d20+5", result.notation)
    }

    @Test
    fun `notation includes negative modifier`() {
        val result = roller().roll(RollRequest(Die.D8, count = 1, modifier = -2))
        assertEquals("1d8-2", result.notation)
    }

    @Test
    fun `notation omits zero modifier`() {
        val result = roller().roll(RollRequest(Die.D6, count = 3, modifier = 0))
        assertEquals("3d6", result.notation)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `rejects count above max`() {
        RollRequest(Die.D6, count = 11, modifier = 0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `rejects modifier above max`() {
        RollRequest(Die.D6, count = 1, modifier = 100)
    }
}
