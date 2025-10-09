package net.thechance.mena.core_chat.presentation.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.chats
import mena.core_chat_presentation.generated.resources.coin
import mena.core_chat_presentation.generated.resources.ic_plus
import mena.core_chat_presentation.generated.resources.mena
import net.thechance.mena.core_chat.presentation.screen.home.components.ChatCard
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.button.FabButton
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel<HomeViewModel>(), modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
                .background(color = Theme.colorScheme.background.surface)
        ) {
            AppBar(
                title = stringResource(Res.string.mena),
                trailingContent = {
                    Text(
                        text = "134",
                        color = Theme.colorScheme.shadeSecondary,
                        style = Theme.typography.label.small,
                        modifier = Modifier.padding(vertical = Theme.spacing._4),
                    )
                    Icon(
                        painter = painterResource(Res.drawable.coin),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp).clickable { viewModel.onWalletClicked() })
                })
            Text(
                text = stringResource(Res.string.chats),
                color = Theme.colorScheme.shadePrimary,
                style = Theme.typography.title.small,
                modifier = Modifier.fillMaxWidth().padding(horizontal = Theme.spacing._16)
            )
            LazyColumn(
                modifier = Modifier.padding(horizontal = Theme.spacing._16).fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(vertical = Theme.spacing._12),
                verticalArrangement = Arrangement.spacedBy(Theme.spacing._16)
            ) {
                items(viewModel.state.value.chats.size) {
                    ChatCard(
                        chats = viewModel.state.value.chats[it],
                    )
                }
            }
        }
        FabButton(
            painter = painterResource(Res.drawable.ic_plus),
            onClick = { viewModel.onNewChatClicked() },
            modifier = Modifier.align(Alignment.BottomEnd).padding(Theme.spacing._16)
        )
    }
}

@OptIn(ExperimentalUuidApi::class)
@Composable
@Preview
private fun HomeScreenPreview() {
    MenaTheme {
        HomeScreen()
    }
}