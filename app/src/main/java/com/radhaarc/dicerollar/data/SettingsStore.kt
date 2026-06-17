package com.radhaarc.dicerollar.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.radhaarc.dicerollar.domain.Die
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "app_settings")

class SettingsStore(private val context: Context) {

    val settings: Flow<AppSettings> = context.settingsDataStore.data.map { prefs ->
        AppSettings(
            dieType = prefs[KEY_DIE_TYPE]?.let { name ->
                runCatching { Die.valueOf(name) }.getOrDefault(Die.D6)
            } ?: Die.D6,
            displayMode = prefs[KEY_DISPLAY]?.let { name ->
                runCatching { DisplayMode.valueOf(name) }.getOrDefault(DisplayMode.Dots)
            } ?: DisplayMode.Dots,
            showBorders = prefs[KEY_SHOW_BORDERS] ?: true,
            sameColorAllDice = prefs[KEY_SAME_COLOR] ?: false,
            pipColorIndex = prefs[KEY_PIP_COLOR_INDEX] ?: 0,
            diceCount = (prefs[KEY_DICE_COUNT] ?: 3).coerceIn(1, MAX_DICE)
        )
    }

    suspend fun setDieType(die: Die) = context.settingsDataStore.edit { it[KEY_DIE_TYPE] = die.name }
    suspend fun setDisplayMode(mode: DisplayMode) =
        context.settingsDataStore.edit { it[KEY_DISPLAY] = mode.name }
    suspend fun setShowBorders(value: Boolean) =
        context.settingsDataStore.edit { it[KEY_SHOW_BORDERS] = value }
    suspend fun setSameColorAllDice(value: Boolean) =
        context.settingsDataStore.edit { it[KEY_SAME_COLOR] = value }
    suspend fun setPipColorIndex(index: Int) =
        context.settingsDataStore.edit { it[KEY_PIP_COLOR_INDEX] = index }
    suspend fun setDiceCount(count: Int) = context.settingsDataStore.edit {
        it[KEY_DICE_COUNT] = count.coerceIn(1, MAX_DICE)
    }

    companion object {
        const val MAX_DICE = 12
        private val KEY_DIE_TYPE = stringPreferencesKey("die_type")
        private val KEY_DISPLAY = stringPreferencesKey("display_mode")
        private val KEY_SHOW_BORDERS = booleanPreferencesKey("show_borders")
        private val KEY_SAME_COLOR = booleanPreferencesKey("same_color")
        private val KEY_PIP_COLOR_INDEX = intPreferencesKey("pip_color_index")
        private val KEY_DICE_COUNT = intPreferencesKey("dice_count")
    }
}
