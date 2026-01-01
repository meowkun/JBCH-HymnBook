package com.example.jbchhymnbook.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.jbchhymnbook.hymn.presentation.display.HymnDisplayScreen
import com.example.jbchhymnbook.hymn.presentation.list.HymnListScreen
import com.example.jbchhymnbook.hymn.presentation.toc.TableOfContentsScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "table_of_contents"
    ) {
        composable("table_of_contents") {
            TableOfContentsScreen(
                onGroupSelected = { groupId ->
                    navController.navigate("hymn_list/$groupId")
                },
                onHymnSelected = { hymnId ->
                    navController.navigate("hymn_display/$hymnId")
                }
            )
        }
        
        composable("hymn_list/{groupId}") { backStackEntry ->
            val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
            HymnListScreen(
                groupId = groupId,
                onHymnSelected = { hymnId ->
                    navController.navigate("hymn_display/$hymnId")
                },
                onBack = { navController.popBackStack() }
            )
        }
        
        composable("hymn_display/{hymnId}") { backStackEntry ->
            val hymnId = backStackEntry.arguments?.getString("hymnId") ?: ""
            HymnDisplayScreen(
                hymnId = hymnId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

