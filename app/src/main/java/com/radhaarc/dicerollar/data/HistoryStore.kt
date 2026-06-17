package com.radhaarc.dicerollar.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.radhaarc.dicerollar.domain.RollResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.historyDataStore by preferencesDataStore(name = "roll_history")

class HistoryStore(private val context: Context) {

    val history: Flow<List<RollResult>> = context.historyDataStore.data.map { prefs ->
        prefs[HISTORY_KEY]?.let { decode(it) } ?: emptyList()
    }

    suspend fun append(result: RollResult) {
        context.historyDataStore.edit { prefs ->
            val existing = prefs[HISTORY_KEY]?.let { decode(it) } ?: emptyList()
            val updated = (listOf(result) + existing).take(MAX_HISTORY)
            prefs[HISTORY_KEY] = json.encodeToString(updated)
        }
    }

    suspend fun clear() {
        context.historyDataStore.edit { it.remove(HISTORY_KEY) }
    }

    private fun decode(raw: String): List<RollResult> =
        runCatching { json.decodeFromString<List<RollResult>>(raw) }.getOrElse { emptyList() }

    companion object {
        private val HISTORY_KEY = stringPreferencesKey("history_json")
        const val MAX_HISTORY = 20
        private val json = Json { ignoreUnknownKeys = true }
    }
}
