package net.thechance.mena.faith.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import net.thechance.mena.faith.presentation.feature.quran.sur.SurScreen

@Composable
fun FaithNavigation() {
    val navController = rememberNavController()

    CompositionLocalProvider(
        LocalNavController provides navController
    ) {
        NavHost(
            navController = navController,
            startDestination = SurRoute
        ) {
            composable<SurRoute> {
                SurScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateToBookmarks = {
                        navController.navigate(BookmarksRoute)
                    },
                    onNavigateToSurahDetails = { surahId, surahName ->
                        navController.navigate(
                            SurahDetailsRoute(
                                surahId = surahId,
                                surahName = surahName
                            )
                        )
                    }
                )
            }
        }
    }
}

val LocalNavController = staticCompositionLocalOf<NavHostController> {
    error("No nav controller provided")
}
