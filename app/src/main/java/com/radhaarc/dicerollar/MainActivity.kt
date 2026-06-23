package com.radhaarc.dicerollar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.radhaarc.dicerollar.ui.history.HistoryScreen
import com.radhaarc.dicerollar.ui.roll.RollScreen
import com.radhaarc.dicerollar.ui.settings.SettingsScreen
import com.radhaarc.dicerollar.ui.theme.DiceRollarTheme

private enum class Route { Roll, Settings, History }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DiceRollarTheme {
                var route by rememberSaveable { mutableStateOf(Route.Roll) }
                when (route) {
                    Route.Settings -> {
                        BackHandler { route = Route.Roll }
                        SettingsScreen(onBack = { route = Route.Roll })
                    }
                    Route.History -> {
                        BackHandler { route = Route.Roll }
                        HistoryScreen(onBack = { route = Route.Roll })
                    }
                    Route.Roll -> {
                        RollScreen(
                            onOpenSettings = { route = Route.Settings },
                            onOpenHistory = { route = Route.History },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}
