package net.thechance.mena.core_chat.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import net.thechance.mena.core_chat.presentation.screen.contacts.ContactsScreen
import net.thechance.mena.core_chat.presentation.screen.syncContacts.SyncContactsScreen
import net.thechance.mena.core_chat.presentation.screen.chats.ChatsScreen

@Composable
fun ChatNavHost() {

    val navController = rememberNavController()

    CompositionLocalProvider(
        LocalNavController provides navController
    ) {
        NavHost(
            modifier = Modifier.fillMaxSize(),
            navController = navController,
            startDestination = ChatsRoute,
        ) {
            composable<ChatsRoute> { ChatsScreen() }
            composable<ContactsRoute> { ContactsScreen() }
            composable<SyncContactsRoute> { SyncContactsScreen() }
        }
    }
}