package net.thechance.mena.faith.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.collectLatest
import net.thechance.mena.core_chat.api.CoreChatApi
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import net.thechance.mena.faith.presentation.feature.main.MainScreen
import net.thechance.mena.faith.presentation.feature.mosque.NearbyMosquesScreen
import net.thechance.mena.faith.presentation.feature.mosque.create.CreateMosqueScreen
import net.thechance.mena.faith.presentation.feature.mosque.pickLocationMap.PickLocationScreen
import net.thechance.mena.faith.presentation.feature.mosque.uploadImageScreen.UploadImageScreen
import net.thechance.mena.faith.presentation.feature.prayertime.PrayerTimeScreen
import net.thechance.mena.faith.presentation.feature.qiblah.calibratedevice.CalibrateDeviceScreen
import net.thechance.mena.faith.presentation.feature.qiblah.compass.CompassScreen
import net.thechance.mena.faith.presentation.feature.quran.bookmark.BookmarkScreen
import net.thechance.mena.faith.presentation.feature.quran.downloadedSur.DownloadedSurScreen
import net.thechance.mena.faith.presentation.feature.quran.reciter.downloadedReciters.DownloadedRecitersScreen
import net.thechance.mena.faith.presentation.feature.quran.reciter.reciterSelection.RecitersSelectionScreen
import net.thechance.mena.faith.presentation.feature.quran.reciter.surahRecitersScreen.SurahRecitersScreen
import net.thechance.mena.faith.presentation.feature.quran.search.ayah.SearchScreen
import net.thechance.mena.faith.presentation.feature.quran.sur.SurScreen
import net.thechance.mena.faith.presentation.feature.quran.surah.SurahScreen
import net.thechance.mena.identity.api.IdentityFeatureApi
import org.koin.compose.getKoin

@Composable
fun FaithNavigation(
    identityApi: IdentityFeatureApi = getKoin().get(),
    chatApi: CoreChatApi = getKoin().get(),
    updateBottomNavigationVisibility: (Boolean) -> Unit = {}
    ) {
    val navController = rememberNavController()
    
    LaunchedEffect(Unit) {
        navController.currentBackStack.collectLatest {
            if (navController.currentDestination?.route in routesWithBottomNavigation) {
                updateBottomNavigationVisibility(true)
            } else {
                updateBottomNavigationVisibility(false)
            }
        }
    }
    
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
                    SurahScreen(
                        onClickBack = { navController.navigateUp() }
                    )
                }
                composable<Route.CompassRoute> {
                    CompassScreen()
                }
                composable<Route.NearbyMosquesRoute> {
                    NearbyMosquesScreen()
                }
                composable<Route.DownloadedSurScreen> {
                    DownloadedSurScreen()
                }
                composable<Route.SurahRecitersRoute> {
                    SurahRecitersScreen()
                }
                composable<Route.ReciterSelectionRoute> {
                    RecitersSelectionScreen()
                }
                composable<Route.DownloadedRecitersRoute> {
                    DownloadedRecitersScreen()
                }
                composable<Route.UserAddresses> {
                    identityApi.NavigateToAddressesScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable<Route.UploadImageRoute> {
                    UploadImageScreen()
                }
                composable<Route.CreateMosqueRoute> {
                    CreateMosqueScreen()
                }
                composable<Route.PickLocationRoute>{
                    PickLocationScreen()
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

val LocalNavController = staticCompositionLocalOf<NavHostController> {
    error("No nav controller provided")
}

private val routesWithBottomNavigation = listOf(
    Route.MainRoute::class.qualifiedName
)
