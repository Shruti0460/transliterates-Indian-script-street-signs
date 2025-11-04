package com.example.street_light.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.street_light.StreetLightApplication
import com.example.street_light.data.model.IndianScript
import com.example.street_light.ui.navigation.Screen

/**
 * Settings screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigate: (Screen) -> Unit,
    viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(
            application = LocalContext.current.applicationContext as StreetLightApplication
        )
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    var scriptExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { onNavigate(Screen.Camera) }) {
                        Text("← Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Preferred Script
            Text(
                text = "Preferred Script",
                style = MaterialTheme.typography.titleMedium
            )
            
            ExposedDropdownMenuBox(
                expanded = scriptExpanded,
                onExpandedChange = { scriptExpanded = !scriptExpanded }
            ) {
                OutlinedTextField(
                    value = uiState.preferredScript.displayName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Select Script") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = scriptExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = scriptExpanded,
                    onDismissRequest = { scriptExpanded = false }
                ) {
                    IndianScript.values().forEach { script ->
                        DropdownMenuItem(
                            text = { Text("${script.displayName} (${script.nativeName})") },
                            onClick = {
                                viewModel.setPreferredScript(script)
                                scriptExpanded = false
                            }
                        )
                    }
                }
            }

            Divider()

            // Font Size
            Text(
                text = "Font Size",
                style = MaterialTheme.typography.titleMedium
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Size: ${uiState.fontSizeMultiplier}x")
                Slider(
                    value = uiState.fontSizeMultiplier,
                    onValueChange = viewModel::setFontSizeMultiplier,
                    valueRange = 0.75f..2.0f,
                    steps = 4,
                    modifier = Modifier.weight(1f).padding(horizontal = 16.dp)
                )
            }
        }
    }
}

// Factory for SettingsViewModel
class SettingsViewModelFactory(
    private val application: StreetLightApplication
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(
                application = application,
                preferences = application.preferences
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
