package com.radhaarc.dicerollar.ui.roll

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import com.radhaarc.dicerollar.data.AppSettings
import com.radhaarc.dicerollar.data.HistoryStore
import com.radhaarc.dicerollar.data.SettingsStore
import com.radhaarc.dicerollar.domain.DiceRoller
import com.radhaarc.dicerollar.domain.RollRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RollViewModel(
    private val diceRoller: DiceRoller,
    private val historyStore: HistoryStore,
    private val settingsStore: SettingsStore,
    private val rollDurationMs: Long = 650L
) : ViewModel() {

    private val transient = MutableStateFlow(TransientState())

    val uiState: StateFlow<RollUiState> = combine(
        transient,
        settingsStore.settings
    ) { t, settings ->
        RollUiState(
            settings = settings,
            lastResult = t.lastResult,
            isRolling = t.isRolling
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = RollUiState()
    )

    fun onEvent(event: RollEvent) {
        when (event) {
            RollEvent.Roll -> roll()
            RollEvent.IncrementCount -> adjustCount(+1)
            RollEvent.DecrementCount -> adjustCount(-1)
            RollEvent.IncrementModifier -> adjustModifier(+1)
            RollEvent.DecrementModifier -> adjustModifier(-1)
        }
    }

    private fun adjustCount(delta: Int) {
        viewModelScope.launch {
            val current = uiState.value.settings.diceCount
            val next = (current + delta).coerceIn(1, SettingsStore.MAX_DICE)
            if (next != current) settingsStore.setDiceCount(next)
        }
    }

    private fun adjustModifier(delta: Int) {
        viewModelScope.launch {
            val current = uiState.value.settings.modifier
            val next = (current + delta).coerceIn(-SettingsStore.MAX_MODIFIER, SettingsStore.MAX_MODIFIER)
            if (next != current) settingsStore.setModifier(next)
        }
    }

    private fun roll() {
        if (transient.value.isRolling) return
        val settings: AppSettings = uiState.value.settings
        viewModelScope.launch {
            val result = diceRoller.roll(
                RollRequest(
                    die = settings.dieType,
                    count = settings.diceCount,
                    modifier = settings.modifier
                )
            )
            transient.update { it.copy(isRolling = true, lastResult = null) }
            delay(rollDurationMs)
            transient.update { it.copy(isRolling = false, lastResult = result) }
            historyStore.append(result)
        }
    }

    private data class TransientState(
        val isRolling: Boolean = false,
        val lastResult: com.radhaarc.dicerollar.domain.RollResult? = null
    )

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as Application
                RollViewModel(
                    diceRoller = DiceRoller(),
                    historyStore = HistoryStore(app),
                    settingsStore = SettingsStore(app)
                )
            }
        }
    }
}
