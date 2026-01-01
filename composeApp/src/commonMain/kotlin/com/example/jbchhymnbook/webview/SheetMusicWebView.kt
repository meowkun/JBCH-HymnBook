package com.example.jbchhymnbook.webview

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun SheetMusicWebView(
    musicXml: String,
    modifier: Modifier = Modifier,
    scale: Float = 1f, // Scale factor for music notes (0.6 = 60% of normal size)
    fontSize: Float = 15f // Font size in pixels for lyrics and text
)

