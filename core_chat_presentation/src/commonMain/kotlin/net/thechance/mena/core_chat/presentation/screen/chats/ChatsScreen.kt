package net.thechance.mena.core_chat.presentation.screen.chats

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import net.thechance.mena.designsystem.presentation.component.button.Button
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ChatsScreen(
    viewModel: ChatsViewModel = koinViewModel<ChatsViewModel>()
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Button(onClick = viewModel::onNewChatClicked) {
            Text(
                text = "Show contacts",
                style = Theme.typography.title.medium
            )
        }
    }
}