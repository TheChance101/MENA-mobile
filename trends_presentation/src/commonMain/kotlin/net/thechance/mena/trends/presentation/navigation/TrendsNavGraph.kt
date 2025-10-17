package net.thechance.mena.trends.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.trends.presentation.screen.category_pick.CategoryPickScreen
import net.thechance.mena.trends.presentation.screen.category_publish.CategoryPublishScreen
import net.thechance.mena.trends.presentation.screen.main_container.MainContainerScreen
import net.thechance.mena.trends.presentation.screen.manage_my_trends.ManageTrendsScreen
import net.thechance.mena.trends.presentation.screen.update_categories.UpdateCategoriesScreen
import net.thechance.mena.trends.presentation.screen.upload_reel.UploadReelScreen
import net.thechance.mena.trends.presentation.screen.user_reel.UserReelScreen
import net.thechance.mena.trends.presentation.screen.video_description.VideoDescriptionScreen

@Composable
fun TrendsNavHost() {

    val navController = rememberNavController()

    CompositionLocalProvider(
        LocalNavController provides navController
    ) {
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

            composable<Route.Trends> {
                // TODO: Just a placeholder for navigation until its user story
                Text(
                    text = "Trends Screen",
                    style = Theme.typography.headline.large,
                    color = Theme.colorScheme.primary.primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 100.dp)
                )
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
        }
    }
}

val LocalNavController = compositionLocalOf<NavController> {
    error("NavController not provided")
}