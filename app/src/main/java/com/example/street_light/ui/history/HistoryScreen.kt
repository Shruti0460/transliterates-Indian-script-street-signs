package com.example.street_light.ui.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.street_light.StreetLightApplication
import com.example.street_light.data.database.HistoryItem
import com.example.street_light.ui.navigation.Screen

/**
 * History screen showing past transliterations
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onNavigate: (Screen) -> Unit,
    viewModel: HistoryViewModel = viewModel(
        factory = HistoryViewModelFactory(
            application = LocalContext.current.applicationContext as StreetLightApplication
        )
    )
) {
    val historyItems by viewModel.historyItems.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("History") },
                navigationIcon = {
                    IconButton(onClick = { onNavigate(Screen.Camera) }) {
                        Text("← Back")
                    }
                },
                actions = {
                    TextButton(onClick = { viewModel.clearHistory() }) {
                        Text("Clear All")
                    }
                }
            )
        }
    ) { padding ->
        if (historyItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No history yet")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(historyItems) { item ->
                    HistoryItemCard(
                        item = item,
                        onDelete = { 
                            // Note: We need item ID for deletion, but TransliterationResult doesn't have it
                            // This would need to be adjusted based on how history items are tracked
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun HistoryItemCard(
    item: com.example.street_light.data.model.TransliterationResult,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = item.originalText,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.transliteratedText,
                style = MaterialTheme.typography.headlineSmall
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                item.detectedScript?.let {
                    Text(
                        text = "Detected: ${it.displayName}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Text(
                    text = "To: ${item.targetScript.displayName}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

// Factory for HistoryViewModel
class HistoryViewModelFactory(
    private val application: StreetLightApplication
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HistoryViewModel(
                application = application,
                repository = application.transliterationRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
