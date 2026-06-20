package com.example.bookapp.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.material.icons.Icons
import com.example.bookapp.R
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.bookapp.ui.bookmarks.BookmarksScreen
import com.example.bookapp.ui.detail.BookDetailScreen
import com.example.bookapp.ui.search.SearchScreen

sealed class Route(val path: String) {
    data object Search : Route("search")
    data object Bookmarks : Route("bookmarks")
    data object BookDetail : Route("book/{workId}") {
        fun create(workId: String) = "book/$workId"
    }
}

@Composable
fun BookshelfApp() {
    val navController = rememberNavController()
    val tabs = listOf(Route.Search, Route.Bookmarks)

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            val showBottomBar = tabs.any { it.path == currentDestination?.route }
            if (showBottomBar) {
                NavigationBar {
                    tabs.forEach { tab ->
                        val selected = currentDestination?.hierarchy?.any { it.route == tab.path } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(tab.path) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (tab is Route.Search) Icons.Default.Search else Icons.Default.Bookmarks,
                                    contentDescription = tab.path
                                )
                            },
                            label = {
                                Text(if (tab is Route.Search) stringResource(R.string.nav_tab_search) else stringResource(R.string.nav_tab_bookmarks))
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Route.Search.path,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Route.Search.path) {
                SearchScreen(onNavigateToDetail = { workId ->
                    navController.navigate(Route.BookDetail.create(workId))
                })
            }
            composable(Route.Bookmarks.path) {
                BookmarksScreen(onNavigateToDetail = { workId ->
                    navController.navigate(Route.BookDetail.create(workId))
                })
            }
            composable(Route.BookDetail.path) { backStackEntry ->
                val workId = backStackEntry.arguments?.getString("workId") ?: return@composable
                BookDetailScreen(workId = workId, onBack = { navController.popBackStack() })
            }
        }
    }
}
