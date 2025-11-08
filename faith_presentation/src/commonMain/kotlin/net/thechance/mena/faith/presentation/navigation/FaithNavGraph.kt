package net.thechance.mena.faith.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import net.thechance.mena.faith.presentation.feature.main.MainScreen
import net.thechance.mena.faith.presentation.feature.mosque.NearbyMosquesScreen
import net.thechance.mena.faith.presentation.feature.mosque.uploadImageScreen.UploadImageScreen
import net.thechance.mena.faith.presentation.feature.prayertime.PrayerTimeScreen
import net.thechance.mena.faith.presentation.feature.qiblah.calibratedevice.CalibrateDeviceScreen
import net.thechance.mena.faith.presentation.feature.qiblah.compass.CompassScreen
import net.thechance.mena.faith.presentation.feature.quran.bookmark.BookmarkScreen
import net.thechance.mena.faith.presentation.feature.quran.downloadedSur.DownloadedSurScreen
import net.thechance.mena.faith.presentation.feature.quran.reciter.ReciterSearchScreen
import net.thechance.mena.faith.presentation.feature.quran.search.ayah.SearchScreen
import net.thechance.mena.faith.presentation.feature.quran.sur.SurScreen
import net.thechance.mena.faith.presentation.feature.quran.surah.SurahScreen
import net.thechance.mena.faith.presentation.feature.quran.tilwah.DownloadedReciterScreen
import net.thechance.mena.identity.api.IdentityFeatureApi
import org.koin.compose.getKoin

@Composable
fun FaithNavigation(identityApi: IdentityFeatureApi = getKoin().get()) {
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
                composable<Route.PrayerTimeRoute> {
                    PrayerTimeScreen()
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
                composable<Route.NearbyMosquesRoute> {
                    NearbyMosquesScreen()
                }
                composable<Route.TilawahRoute> {
                    DownloadedSurScreen()
                }
                composable<Route.DownloadedRecitersRoute> {
                    DownloadedReciterScreen()
                }
                composable<Route.ReciterSearch> {
                    ReciterSearchScreen()
                }
                composable<Route.UserAddresses> {
                    identityApi.NavigateToAddressesScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable<Route.UploadImageRoute> {
                    UploadImageScreen()
                }
                composable<Route.UserAddresses> {
                    identityApi.NavigateToAddressesScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}

val LocalNavController = staticCompositionLocalOf<NavHostController> {
    error("No nav controller provided")
}
