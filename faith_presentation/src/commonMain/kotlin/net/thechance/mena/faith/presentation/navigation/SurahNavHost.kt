package net.thechance.mena.faith.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import net.thechance.mena.core_chat.api.CoreChatApi
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import net.thechance.mena.faith.presentation.feature.quran.reciter.surahRecitersScreen.SurahRecitersScreen
import net.thechance.mena.faith.presentation.feature.quran.search.ayah.SearchScreen
import net.thechance.mena.faith.presentation.feature.quran.surah.SurahScreen
import org.koin.compose.getKoin

@Composable
fun SurahNavHost(
    surahId: Int,
    ayahNumber: Int,
    onNavigateBack: () -> Unit,
    chatApi: CoreChatApi = getKoin().get()

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
                composable<Route.SearchRoute> {
                    SearchScreen()
                }
                composable<Route.SurahRecitersRoute> {
                    SurahRecitersScreen()
                }
                composable<Route.ShareAyahToChatRoute>{
                    val argument = it.toRoute<Route.ShareAyahToChatRoute>()
                    chatApi.ShareAyahToChatEntry(
                        surahId = argument.surahId,
                        ayahNumber = argument.ayahNumber,
                        ayahContent = argument.ayahContent,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
