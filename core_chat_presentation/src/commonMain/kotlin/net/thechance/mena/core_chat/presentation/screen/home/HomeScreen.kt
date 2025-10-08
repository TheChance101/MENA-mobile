package net.thechance.mena.core_chat.presentation.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import net.thechance.mena.designsystem.presentation.component.button.Button
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel<HomeViewModel>(),
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Theme.spacing._16),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._32, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Button(onClick = viewModel::onNewChatClicked) {
            Text(
                text = "Chat",
                style = Theme.typography.title.medium
            )
        }
        Button(onClick = viewModel::onWalletClicked) {
            Text(
                text = "Wallet",
                style = Theme.typography.title.medium
            )
        }
    }
}