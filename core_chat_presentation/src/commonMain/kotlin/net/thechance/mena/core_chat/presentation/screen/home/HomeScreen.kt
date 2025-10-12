package net.thechance.mena.core_chat.presentation.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import mena.core_chat_presentation.generated.resources.ic_coin
import mena.core_chat_presentation.generated.resources.ic_plus
import mena.core_chat_presentation.generated.resources.mena
import net.thechance.mena.core_chat.presentation.screen.home.components.ChatItem
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.button.FabButton
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel<HomeViewModel>()
) {
    HomeContent(viewModel)
}

@Composable
private fun HomeContent(
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier
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
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._4),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "134",
                            color = Theme.colorScheme.shadeSecondary,
                            style = Theme.typography.label.small,
                        )
                        Image(
                            painter = painterResource(Res.drawable.ic_coin),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                                .clickable { viewModel.onWalletClicked() })
                    }
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
                    ChatItem(
                        chat = viewModel.state.value.chats[it],
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
