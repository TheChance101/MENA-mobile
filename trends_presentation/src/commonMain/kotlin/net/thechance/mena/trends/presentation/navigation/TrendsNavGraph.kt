package net.thechance.mena.trends.presentation.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.domain.service.AppThemeService
import net.thechance.mena.identity.domain.util.AppTheme
import net.thechance.mena.trends.presentation.screen.category_pick.CategoryPickScreen
import net.thechance.mena.trends.presentation.screen.category_publish.CategoryPublishScreen
import net.thechance.mena.trends.presentation.screen.home.HomeScreen
import net.thechance.mena.trends.presentation.screen.main_container.MainContainerScreen
import net.thechance.mena.trends.presentation.screen.manage_my_trends.ManageTrendsScreen
import net.thechance.mena.trends.presentation.screen.update_categories.UpdateCategoriesScreen
import net.thechance.mena.trends.presentation.screen.upload_reel.UploadReelScreen
import net.thechance.mena.trends.presentation.screen.user_reel.UserReelScreen
import net.thechance.mena.trends.presentation.screen.video_description.VideoDescriptionScreen
import net.thechance.mena.trends.presentation.shared.component.snackbar.TrendsSnackBar
import net.thechance.mena.trends.presentation.shared.util.LocalImageLoader
import net.thechance.mena.trends.presentation.shared.util.provideImageLoader
import net.thechance.mena.trends.presentation.snackbar.LocalSnackbarController
import net.thechance.mena.trends.presentation.snackbar.SnackBarControllerImpl
import net.thechance.mena.trends.presentation.snackbar.defaultSnackBarAnimationConfig
import org.koin.compose.koinInject

@Composable
fun TrendsNavHost(
    appThemeService: AppThemeService = koinInject()
) {

    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()
    val snackBarController = remember(coroutineScope) { SnackBarControllerImpl(coroutineScope) }
    val currentSnackbarData by snackBarController.state.collectAsStateWithLifecycle()
    val coilLoader = provideImageLoader()

    val currentTheme by appThemeService.observeAppTheme().collectAsStateWithLifecycle()
    val isDarkTheme = currentTheme == AppTheme.DARK

    CompositionLocalProvider(
        LocalNavController provides navController,
        LocalSnackbarController provides snackBarController,
        LocalImageLoader provides coilLoader,
        LocalDarkTheme provides isDarkTheme
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            NavHost(
                modifier = Modifier.fillMaxSize(),
                navController = navController,
                startDestination = Route.MainContainer,
            ) {

                composable<Route.MainContainer> {
                    MainContainerScreen()
                }

                composable<Route.UpdateCategories> {
                    UpdateCategoriesScreen()
                }

                composable<Route.Categories> {
                    CategoryPickScreen()
                }

                composable<Route.ManageReels> {
                    ManageTrendsScreen()
                }

                composable<Route.ReelDetails> {
                    UserReelScreen()
                }

                composable<Route.UploadReel> {
                    UploadReelScreen()
                }

                composable<Route.VideoDescription> {
                    VideoDescriptionScreen()
                }

                composable<Route.CategoriesPublish> {
                    CategoryPublishScreen()
                }

                composable<Route.Home> {
                    HomeScreen()
                }
            }

            AnimatedVisibility(
                visible = currentSnackbarData.isVisible,
                enter = defaultSnackBarAnimationConfig.enterAnimation,
                exit = defaultSnackBarAnimationConfig.exitAnimation,
                modifier = Modifier.fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = Theme.spacing._16)
            ) {
                currentSnackbarData.data?.let { data ->
                    TrendsSnackBar(
                        message = data.message,
                        status = data.snackBarType
                    )
                }
            }
        }
    }
}

val LocalNavController = compositionLocalOf<NavController> {
    error("NavController not provided")
}
val LocalDarkTheme = compositionLocalOf { false }