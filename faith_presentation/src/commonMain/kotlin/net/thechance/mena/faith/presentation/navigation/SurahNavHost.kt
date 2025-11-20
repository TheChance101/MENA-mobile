package net.thechance.mena.faith.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import net.thechance.mena.faith.presentation.feature.quran.surah.SurahScreen

@Composable
fun SurahNavHost(
    surahId: Int,
    ayahNumber: Int,
    onNavigateBack: () -> Unit
) {
    QuranTheme {
        val navController = rememberNavController()

        CompositionLocalProvider(
            LocalNavController provides navController
        ) {
            NavHost(
                navController = navController,
                startDestination = Route.SurahDetailsRoute(surahId = surahId, ayahNumber = ayahNumber)
            ) {
                composable<Route.SurahDetailsRoute> {
                    SurahScreen(
                        onClickBack = onNavigateBack
                    )
                }
            }
        }
    }
}
