package com.example.jbchhymnbook.hymn.presentation.display

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.jbchhymnbook.webview.SheetMusicWebView
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HymnDisplayScreen(
    hymnId: String,
    onBack: () -> Unit,
    viewModel: HymnDisplayViewModel = koinViewModel(parameters = { parametersOf(hymnId) })
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = { 
                Text(
                    text = state.hymn?.titles?.values?.firstOrNull() ?: "Hymn"
                ) 
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = { 
                    viewModel.onIntent(HymnDisplayIntent.ToggleSettingsSheet) 
                }) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings")
                }
            }
        )
        
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            state.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Error: ${state.error}",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.onIntent(HymnDisplayIntent.LoadHymn(hymnId)) }) {
                            Text("Retry")
                        }
                    }
                }
            }
            state.filteredMusicXml.isNotEmpty() -> {
                SheetMusicWebView(
                    musicXml = state.filteredMusicXml,
                    modifier = Modifier.fillMaxSize()
                )
            }
            else -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No content to display")
                        if (state.hymn != null) {
                            Text("Hymn loaded but XML is empty", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
    
    if (state.showSettingsSheet) {
        SettingsBottomSheet(
            settings = state.settings,
            onDismiss = { viewModel.onIntent(HymnDisplayIntent.ToggleSettingsSheet) },
            onPartsChanged = { parts ->
                viewModel.onIntent(HymnDisplayIntent.UpdateParts(parts))
            },
            onLanguagesChanged = { languages ->
                viewModel.onIntent(HymnDisplayIntent.UpdateLanguages(languages))
            },
            onAppLanguageChanged = { language ->
                viewModel.onIntent(HymnDisplayIntent.UpdateAppLanguage(language))
            }
        )
    }
}

