package com.example.jbchhymnbook.hymn.presentation.display

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.jbchhymnbook.hymn.domain.model.Language
import com.example.jbchhymnbook.hymn.domain.model.VoicePart
import com.example.jbchhymnbook.settings.domain.model.AppSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsBottomSheet(
    settings: AppSettings,
    onDismiss: () -> Unit,
    onPartsChanged: (Set<VoicePart>) -> Unit,
    onLanguagesChanged: (Set<Language>) -> Unit,
    onAppLanguageChanged: (Language) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxHeight(0.8f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineSmall
            )
            
            // Voice Parts Selection
            Text(
                text = "Voice Parts",
                style = MaterialTheme.typography.titleMedium
            )
            VoicePart.entries.forEach { part ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = settings.visibleParts.contains(part),
                            onClick = {
                                val newParts = if (settings.visibleParts.contains(part)) {
                                    settings.visibleParts - part
                                } else {
                                    settings.visibleParts + part
                                }
                                onPartsChanged(newParts)
                            }
                        )
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = settings.visibleParts.contains(part),
                        onCheckedChange = {
                            val newParts = if (it) {
                                settings.visibleParts + part
                            } else {
                                settings.visibleParts - part
                            }
                            onPartsChanged(newParts)
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = part.displayName)
                }
            }
            
            Divider()
            
            // Lyrics Languages Selection
            Text(
                text = "Lyrics Languages",
                style = MaterialTheme.typography.titleMedium
            )
            Language.entries.forEach { language ->
                val isSelected = settings.visibleLanguages.contains(language)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = isSelected,
                            onClick = {
                                val newLanguages = if (isSelected) {
                                    settings.visibleLanguages - language
                                } else {
                                    settings.visibleLanguages + language
                                }
                                onLanguagesChanged(newLanguages)
                            }
                        )
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { checked ->
                            val newLanguages = if (checked) {
                                settings.visibleLanguages + language
                            } else {
                                settings.visibleLanguages - language
                            }
                            onLanguagesChanged(newLanguages)
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = language.name)
                }
            }
            
            Divider()
            
            // App Language Selection
            Text(
                text = "App Language",
                style = MaterialTheme.typography.titleMedium
            )
            Language.entries.forEach { language ->
                val isSelected = settings.appLanguage == language
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = isSelected,
                            onClick = { onAppLanguageChanged(language) }
                        )
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = { onAppLanguageChanged(language) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = language.name)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

