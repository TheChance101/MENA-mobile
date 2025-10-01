package net.thechance.mena.faith.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import net.thechance.mena.faith.presentation.feature.main.MainScreen
import net.thechance.mena.faith.presentation.feature.quran.sur.SurScreen
import net.thechance.mena.faith.presentation.feature.quran.surah.SurahScreen

@Composable
fun FaithNavigation() {
    val navController = rememberNavController()

    CompositionLocalProvider(
        LocalNavController provides navController
    ) {
        QuranTheme {
            NavHost(
                navController = navController,
                startDestination = MainRoute
            ) {

                composable<MainRoute> {
                    MainScreen()
                }
                composable<SurRoute> {
                    SurScreen()
                }

                composable<SurahDetailsRoute> { backStackEntry ->
                    val args = backStackEntry.toRoute<SurahDetailsRoute>()

                    SurahScreen(
                        surahId = args.surahId,
                        surahName = args.surahName
                    )

                }
            }
        }
    }
}

val LocalNavController = staticCompositionLocalOf<NavHostController> {
    error("No nav controller provided")
}
