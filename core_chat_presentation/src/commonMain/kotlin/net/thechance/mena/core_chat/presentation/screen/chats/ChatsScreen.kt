package net.thechance.mena.core_chat.presentation.screen.chats

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.Flow
import net.thechance.mena.core_chat.presentation.navigation.ContactsRoute
import net.thechance.mena.core_chat.presentation.navigation.LocalNavController
import net.thechance.mena.core_chat.presentation.navigation.SyncContactsRoute
import net.thechance.mena.core_chat.presentation.utils.EffectHandler
import net.thechance.mena.designsystem.presentation.component.button.Button
import net.thechance.mena.designsystem.presentation.component.text.MenaText
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ChatsScreen(
    viewModel: ChatsViewModel = koinViewModel<ChatsViewModel>()
) {
    ChatsEffectHandler(viewModel.effect)

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Button(onClick = viewModel::onNewChatClicked) {
            MenaText(
                text = "Show contacts",
                style = Theme.typography.title.medium
            )
        }
    }
}

@Composable
fun ChatsEffectHandler(effects: Flow<ChatsScreenEffect>) {
    val navController = LocalNavController.current
    EffectHandler(effects) { effect ->
        when (effect) {
            ChatsScreenEffect.NavigateToContacts -> {
                navController.navigate(ContactsRoute)
            }

            ChatsScreenEffect.NavigateToSyncContacts -> {
                navController.navigate(SyncContactsRoute(forceSync = false))
            }
        }
    }
}
