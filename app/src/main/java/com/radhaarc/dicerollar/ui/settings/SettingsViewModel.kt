package com.radhaarc.dicerollar.ui.settings

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import com.radhaarc.dicerollar.data.AppSettings
import com.radhaarc.dicerollar.data.DisplayMode
import com.radhaarc.dicerollar.data.SettingsStore
import com.radhaarc.dicerollar.domain.Die
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsStore: SettingsStore
) : ViewModel() {

    val settings: StateFlow<AppSettings> = settingsStore.settings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AppSettings()
    )

    fun setDieType(die: Die) { viewModelScope.launch { settingsStore.setDieType(die) } }
    fun setDisplayMode(mode: DisplayMode) {
        viewModelScope.launch { settingsStore.setDisplayMode(mode) }
    }
    fun setShowBorders(v: Boolean) { viewModelScope.launch { settingsStore.setShowBorders(v) } }
    fun setSameColors(v: Boolean) {
        viewModelScope.launch { settingsStore.setSameColorAllDice(v) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as Application
                SettingsViewModel(settingsStore = SettingsStore(app))
            }
        }
    }
}
