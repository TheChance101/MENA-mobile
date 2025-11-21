package net.thechance.mena.core_chat.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil3.compose.setSingletonImageLoaderFactory
import net.thechance.mena.core_chat.presentation.components.snackBarHost.AnimatedSnackBarHost
import net.thechance.mena.core_chat.presentation.components.snackBarHost.LocalSnackBarHostController
import net.thechance.mena.core_chat.presentation.components.snackBarHost.SnackBarHostController
import net.thechance.mena.core_chat.presentation.screen.chat.ChatScreen
import net.thechance.mena.core_chat.presentation.screen.contacts.ContactsScreen
import net.thechance.mena.core_chat.presentation.screen.home.HomeScreen
import net.thechance.mena.core_chat.presentation.screen.shareAyaScreen.ShareMessageScreen
import net.thechance.mena.core_chat.presentation.screen.syncContacts.SyncContactsScreen
import net.thechance.mena.core_chat.presentation.utils.rememberImageLoader
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.api.WalletApi
import org.koin.compose.koinInject

val LocalNavController = staticCompositionLocalOf<NavController> {
    error("No NavController provided")
}

@Composable
fun ChatNavHost(
    walletApi: WalletApi = koinInject(),
    onNavigateBackFromChat: () -> Unit = {},
    onNavigateBackFromShareMessage: () -> Unit = {},
    startDestination: ChatRoute = HomeRoute
) {

    val coilImageLoader = rememberImageLoader()
    setSingletonImageLoaderFactory { coilImageLoader }
    val navController = rememberNavController()
    val snackBarHostController = remember { SnackBarHostController() }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        CompositionLocalProvider(
            LocalNavController provides navController,
            LocalSnackBarHostController provides snackBarHostController
        ) {
            NavHost(
                modifier = Modifier.fillMaxSize(),
                navController = navController,
                startDestination = startDestination,
            ) {
                composable<HomeRoute> { HomeScreen() }
                composable<ContactsRoute> { ContactsScreen() }
                composable<SyncContactsRoute> { SyncContactsScreen() }
                composable<ChatDetailsRoute> { ChatScreen(onClickBackFromChat = onNavigateBackFromChat) }
                composable<WalletRoute> {
                    walletApi.WalletEntry(
                        navigateBack = {
                            navController.popBackStack()
                        },
                        updateBottomNavigationVisibility = {
                            //pass updateBottomNavigationVisibility here
                        },
                    )
                }
                composable<ShareMessageRoute> { ShareMessageScreen(onClickBack = onNavigateBackFromShareMessage) }
            }

            Box(
                modifier = Modifier.fillMaxSize().statusBarsPadding()
                    .padding(horizontal = Theme.spacing._16),
                contentAlignment = Alignment.TopCenter
            ) {
                AnimatedSnackBarHost(snackBarHostController)
            }
        }
    }
}