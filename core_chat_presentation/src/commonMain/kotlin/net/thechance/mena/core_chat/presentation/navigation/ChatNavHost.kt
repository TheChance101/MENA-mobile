package net.thechance.mena.core_chat.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import net.thechance.mena.core_chat.presentation.components.AnimatedSnackBarHost
import net.thechance.mena.core_chat.presentation.components.SnackBarData
import net.thechance.mena.core_chat.presentation.screen.chat.ChatScreen
import net.thechance.mena.core_chat.presentation.screen.contacts.ContactsScreen
import net.thechance.mena.core_chat.presentation.screen.home.HomeScreen
import net.thechance.mena.core_chat.presentation.screen.syncContacts.SyncContactsScreen
import net.thechance.mena.core_chat.presentation.utils.UiText
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.api.WalletApi
import org.koin.compose.koinInject

@Composable
fun ChatNavHost(
    walletApi: WalletApi = koinInject()
) {

    val navController = rememberNavController()
    var snackBarDataState by remember {
        mutableStateOf(
            SnackBarData(title = UiText.DynamicString(), message = UiText.DynamicString())
        )
    }
    var isSnackBarVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        CompositionLocalProvider(
            LocalNavController provides navController
        ) {
            NavHost(
                modifier = Modifier.fillMaxSize(),
                navController = navController,
                startDestination = HomeRoute,
            ) {
                composable<HomeRoute> { HomeScreen() }
                composable<ContactsRoute> { ContactsScreen() }
                composable<SyncContactsRoute> { SyncContactsScreen() }
                composable<ChatDetailsRoute> { ChatScreen() }
                composable<WalletRoute> {
                    walletApi.WalletEntry(navigateBack = {
                        navController.popBackStack()
                    })
                }
            }

            Box(
                modifier = Modifier.fillMaxSize().statusBarsPadding()
                    .padding(horizontal = Theme.spacing._16),
                contentAlignment = Alignment.TopCenter
            ) {
                AnimatedSnackBarHost(
                    isVisible = isSnackBarVisible,
                    data = snackBarDataState,
                    onDismiss = { isSnackBarVisible = false }
                )
            }
        }
    }
}
