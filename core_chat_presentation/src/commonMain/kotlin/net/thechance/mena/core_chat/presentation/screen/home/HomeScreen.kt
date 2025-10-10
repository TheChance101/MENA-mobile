package net.thechance.mena.core_chat.presentation.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.chats
import mena.core_chat_presentation.generated.resources.coin
import mena.core_chat_presentation.generated.resources.ic_plus
import mena.core_chat_presentation.generated.resources.mena
import net.thechance.mena.core_chat.presentation.screen.home.components.ChatCard
import net.thechance.mena.core_chat.presentation.screen.home.components.NoChatsHistoryView // Assuming this is your empty state view
import net.thechance.mena.core_chat.presentation.utils.PaginationTrigger
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.button.FabButton
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.indicator.DotsProgressIndicator
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val listState = rememberLazyListState()

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Theme.colorScheme.background.surface)
        ) {
            AppBar(
                title = stringResource(Res.string.mena),
                trailingContent = {
                    Text(
                        text = "134", // TODO : Replace with the actual balance from wallet feature
                        color = Theme.colorScheme.shadeSecondary,
                        style = Theme.typography.label.small,
                        modifier = Modifier.padding(vertical = Theme.spacing._4),
                    )
                    Icon(
                        painter = painterResource(Res.drawable.coin),
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { viewModel.onWalletClicked() })
                }
            )

            when {
                state.chats.isEmpty() && state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        DotsProgressIndicator()
                    }
                }

                state.chats.isEmpty() && !state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        NoChatsHistoryView(modifier = Modifier.padding(Theme.spacing._24))
                    }
                }

                else -> {
                    Text(
                        text = stringResource(Res.string.chats),
                        color = Theme.colorScheme.shadePrimary,
                        style = Theme.typography.title.small,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Theme.spacing._16)
                    )
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .padding(horizontal = Theme.spacing._16)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(vertical = Theme.spacing._12),
                        verticalArrangement = Arrangement.spacedBy(Theme.spacing._16)
                    ) {
                        items(
                            items = state.chats,
                            key = { it.id }
                        ) { chat ->
                            ChatCard(chats = chat)
                        }

                        if (state.isLoading) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = Theme.spacing._16),
                                    contentAlignment = Alignment.Center
                                ) {
                                    DotsProgressIndicator()
                                }
                            }
                        }
                    }
                }
            }
        }
        FabButton(
            painter = painterResource(Res.drawable.ic_plus),
            onClick = viewModel::onNewChatClicked,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(Theme.spacing._16)
        )
    }

    PaginationTrigger(
        list = state.chats,
        listState = listState,
        buffer = 5, // Load next page when 5 items are left
        loadNextItems = viewModel::loadChatsSummary
    )
}