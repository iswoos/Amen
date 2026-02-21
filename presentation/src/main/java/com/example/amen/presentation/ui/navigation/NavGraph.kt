package com.example.amen.presentation.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.amen.domain.entity.BibleVerse
import com.example.amen.presentation.ui.bible.BibleBrowserScreen
import com.example.amen.presentation.ui.bible.BibleReaderScreen
import com.example.amen.presentation.ui.card.CardShareScreen
import com.example.amen.presentation.ui.favorites.FavoriteVersesScreen
import com.example.amen.presentation.ui.home.HomeScreen
import com.example.amen.presentation.ui.journal.JournalScreen
import com.example.amen.presentation.ui.relax.RelaxScreen
import com.example.amen.presentation.ui.tracker.TrackerScreen

// ── 하단 탭 정의 ─────────────────────────────────────────────────────────────
sealed class BottomTab(val route: String, val label: String, val icon: ImageVector) {
    object Home       : BottomTab("home",     "홈",        Icons.Default.Home)
    object Bible      : BottomTab("bible_browser", "성경",  Icons.Default.MenuBook)
    object Relax      : BottomTab("relax",    "휴식",      Icons.Default.Bedtime)
    object Favorites  : BottomTab("favorites","좋아요",    Icons.Default.Favorite)
    object Journal    : BottomTab("journal",  "기도일기",  Icons.Default.Book)
}

val bottomTabs = listOf(
    BottomTab.Home,
    BottomTab.Bible,
    BottomTab.Relax,
    BottomTab.Favorites,
    BottomTab.Journal
)

// ── 전체 화면 라우트 (탭 외 서브 화면) ───────────────────────────────────────
sealed class Screen(val route: String) {
    object BibleReader : Screen("bible_reader/{book}/{chapter}")
    object CardShare   : Screen("share/{book}/{chapter}/{verse}/{content}")
    object Tracker     : Screen("tracker")
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmenNavGraph(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // 하단 탭이 보여야 할 라우트들
    val showBottomBar = bottomTabs.any { it.route == currentDestination?.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    bottomTabs.forEach { tab ->
                        NavigationBarItem(
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label) },
                            selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = BottomTab.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            // ── 홈 ──────────────────────────────────────────────────────────
            composable(BottomTab.Home.route) {
                HomeScreen(
                    onNavigateToBibleReading = { navController.navigate(BottomTab.Bible.route) },
                    onNavigateToTracker = { navController.navigate(Screen.Tracker.route) }
                )
            }

            // ── 성경 탐색 ───────────────────────────────────────────────────
            composable(BottomTab.Bible.route) {
                BibleBrowserScreen(
                    onBackClick = { navController.popBackStack() },
                    onChapterClick = { book, chapter ->
                        navController.navigate("bible_reader/$book/$chapter")
                    }
                )
            }

            // ── 휴식과 수면 ─────────────────────────────────────────────────
            composable(BottomTab.Relax.route) {
                RelaxScreen(onBackClick = { navController.popBackStack() })
            }

            // ── 좋아요한 구절 ────────────────────────────────────────────────
            composable(BottomTab.Favorites.route) {
                FavoriteVersesScreen()
            }

            // ── 기도 일기 ───────────────────────────────────────────────────
            composable(BottomTab.Journal.route) {
                JournalScreen(onBackClick = { navController.popBackStack() })
            }

            // ── 서브 화면: 성경 읽기 ─────────────────────────────────────────
            composable(
                route = "bible_reader/{book}/{chapter}",
                arguments = listOf(
                    navArgument("book") { type = NavType.StringType },
                    navArgument("chapter") { type = NavType.IntType }
                )
            ) { back ->
                val book    = back.arguments?.getString("book") ?: ""
                val chapter = back.arguments?.getInt("chapter") ?: 1
                BibleReaderScreen(
                    book = book,
                    chapter = chapter,
                    onBackClick = { navController.popBackStack() }
                )
            }

            // ── 서브 화면: 구절 공유 ─────────────────────────────────────────
            composable("share/{book}/{chapter}/{verse}/{content}") { back ->
                val book    = back.arguments?.getString("book") ?: ""
                val chapter = back.arguments?.getString("chapter")?.toIntOrNull() ?: 1
                val verseNum = back.arguments?.getString("verse")?.toIntOrNull() ?: 1
                val content = back.arguments?.getString("content") ?: ""
                CardShareScreen(
                    verse = BibleVerse(0, book, chapter, verseNum, content),
                    onBackClick = { navController.popBackStack() }
                )
            }

            // ── 서브 화면: 말씀 완독 상세 ────────────────────────────────────
            composable(Screen.Tracker.route) {
                TrackerScreen(onBackClick = { navController.popBackStack() })
            }
        }
    }
}
