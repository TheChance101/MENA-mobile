package net.thechance.mena.faith.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cafe.adriel.voyager.navigator.Navigator
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import net.thechance.mena.faith.presentation.feature.main.MainScreen
import net.thechance.mena.faith.presentation.feature.qiblah.calibratedevice.CalibrateDeviceScreen
import net.thechance.mena.faith.presentation.feature.qiblah.compass.CompassScreen
import net.thechance.mena.faith.presentation.feature.quran.bookmark.BookmarkScreen
import net.thechance.mena.faith.presentation.feature.quran.search.SearchScreen
import net.thechance.mena.faith.presentation.feature.quran.sur.SurScreen
import net.thechance.mena.faith.presentation.feature.quran.surah.SurahScreen
import net.thechance.mena.identity.presentation.screen.addresses.myAddresses.AddressesScreen
import net.thechance.mena.identity.presentation.screen.enableLocationScreen.EnableLocationScreen

@Composable
fun FaithNavigation() {
    val navController = rememberNavController()
    CompositionLocalProvider(
        LocalNavController provides navController
    ) {
        QuranTheme {
            NavHost(
                navController = navController,
                startDestination = Route.MainRoute
            ) {
                composable<Route.MainRoute> {
                    MainScreen()
                }
                composable<Route.SurRoute> {
                    SurScreen()
                }
                composable<Route.BookmarksRoute> {
                    BookmarkScreen()
                }
                composable<Route.CalibrateDeviceRoute> {
                    CalibrateDeviceScreen()
                }
                composable<Route.SearchRoute> {
                    SearchScreen()
                }
                composable<Route.SurahDetailsRoute> {
                    SurahScreen()
                }
                composable<Route.CompassRoute> {
                    CompassScreen()
                }
                composable<Route.EnableLocation> {
                    Navigator(screen = EnableLocationScreen())
                }
                composable<Route.MyLocation> {
                    Navigator(screen = AddressesScreen())
                }
            }
        }
    }
}

val LocalNavController = staticCompositionLocalOf<NavHostController> {
    error("No nav controller provided")
}
