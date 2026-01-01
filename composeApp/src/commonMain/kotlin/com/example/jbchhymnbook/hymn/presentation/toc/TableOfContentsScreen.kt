package com.example.jbchhymnbook.hymn.presentation.toc

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TableOfContentsScreen(
    onGroupSelected: (String) -> Unit,
    onHymnSelected: (String) -> Unit = {},
    viewModel: TableOfContentsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Hymn Book",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
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
                        Button(onClick = { viewModel.onIntent(TableOfContentsIntent.LoadGroups) }) {
                            Text("Retry")
                        }
                    }
                }
            }
            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Always show test hymn at the top
                    item {
                        Text(
                            "Test Hymn",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onHymnSelected("test_hymn") }
                        ) {
                            Text(
                                text = "287. 오늘 모여 찬송함은 (Gathering Today to Sing)",
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                    
                    // Show groups if available
                    if (state.groups.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Groups",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                        items(state.groups) { group ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onGroupSelected(group.id) }
                            ) {
                                Text(
                                    text = group.names.values.firstOrNull() ?: group.id,
                                    modifier = Modifier.padding(16.dp),
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

