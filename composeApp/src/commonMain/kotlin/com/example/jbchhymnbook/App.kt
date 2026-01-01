package com.example.jbchhymnbook

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.jbchhymnbook.di.appModule
import com.example.jbchhymnbook.di.hymnModule
import com.example.jbchhymnbook.di.settingsModule
import com.example.jbchhymnbook.navigation.NavGraph
import org.koin.compose.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.module

@Composable
fun App() {
    KoinApplication(application = {
        modules(appModule, hymnModule, settingsModule)
    }) {
        MaterialTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}