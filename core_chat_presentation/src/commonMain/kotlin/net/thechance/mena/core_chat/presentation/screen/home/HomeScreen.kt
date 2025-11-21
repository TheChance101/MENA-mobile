package net.thechance.mena.core_chat.presentation.screen.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.SharedFlow
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.chats
import mena.core_chat_presentation.generated.resources.ic_coin
import mena.core_chat_presentation.generated.resources.ic_plus
import mena.core_chat_presentation.generated.resources.mena
import net.thechance.mena.core_chat.presentation.components.snackBarHost.LocalSnackBarHostController
import net.thechance.mena.core_chat.presentation.navigation.ChatDetailsRoute
import net.thechance.mena.core_chat.presentation.navigation.ContactsRoute
import net.thechance.mena.core_chat.presentation.navigation.LocalNavController
import net.thechance.mena.core_chat.presentation.navigation.SyncContactsRoute
import net.thechance.mena.core_chat.presentation.navigation.WalletRoute
import net.thechance.mena.core_chat.presentation.screen.home.HomeScreenState.ChatUiState
import net.thechance.mena.core_chat.presentation.screen.home.components.BalanceSkeleton
import net.thechance.mena.core_chat.presentation.screen.home.components.ChatItem
import net.thechance.mena.core_chat.presentation.screen.home.components.ChatSummaryListSkeleton
import net.thechance.mena.core_chat.presentation.screen.home.components.NoChatsHistoryView
import net.thechance.mena.core_chat.presentation.screen.home.components.WeatherAndNextPrayerCard
import net.thechance.mena.core_chat.presentation.utils.EffectHandler
import net.thechance.mena.core_chat.presentation.utils.PaginationTrigger
import net.thechance.mena.core_chat.presentation.utils.noHoverClickable
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.button.FabButton
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.abs
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel<HomeViewModel>()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val effects = viewModel.effect

    EffectsHandler(effects = effects)

    HomeContent(
        state = state,
        interactionListener = viewModel
    )
}

@OptIn(ExperimentalUuidApi::class)
@Composable
private fun HomeContent(
    state: HomeScreenState,
    interactionListener: HomeScreenInteractionListener,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    Scaffold(
        topBar = {
            HomeScreenAppBar(
                balanceAmount = state.balanceAmount.toString(),
                isLoading = state.isBalanceLoading,
                onWalletClicked = interactionListener::onWalletClicked
            )
        }
    ) {
        Box(modifier = modifier.fillMaxSize()) {
            Column(
                verticalArrangement = Arrangement.spacedBy(Theme.spacing._12),
                modifier = Modifier.padding(top = Theme.spacing._8)
            ) {
                WeatherAndNextPrayerCard(
                    isLoading = state.isPrayerTimeLoading || state.isWeatherLoading,
                    prayerUiState = state.prayerUiState,
                    weatherUiState = state.weatherUiState,
                    modifier = Modifier.padding(horizontal = Theme.spacing._16)
                )

                ChatSummaryList(
                    isLoading = state.isChatsLoading,
                    listState = listState,
                    chats = state.chats,
                    onChatClicked = interactionListener::onChatClicked
                )
            }

            FabButton(
                painter = painterResource(Res.drawable.ic_plus),
                onClick = interactionListener::onNewChatClicked,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(Theme.spacing._16)
            )
        }

    }

    PaginationTrigger(
        list = state.chats,
        listState = listState,
        remainingItemsToLoadNextPage = 5,
        loadNextItems = interactionListener::onChatsListScrolled
    )
}

@Composable
private fun HomeScreenAppBar(
    balanceAmount: String,
    isLoading: Boolean = false,
    onWalletClicked: () -> Unit
) {
    AppBar(
        title = stringResource(Res.string.mena),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 18.dp),
        trailingContent = {
            if (isLoading) {
                BalanceSkeleton()
            } else {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Theme.spacing._4),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.noHoverClickable { onWalletClicked() }
                ) {
                    Text(
                        text = if(balanceAmount.isNotEmpty()) formatBalance(balanceAmount.toDouble()) else "",
                        color = Theme.colorScheme.shadeSecondary,
                        style = Theme.typography.label.small,
                    )
                    Image(
                        painter = painterResource(Res.drawable.ic_coin),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    )
}

@Composable
@OptIn(ExperimentalUuidApi::class)
private fun ChatSummaryList(
    isLoading: Boolean,
    listState: LazyListState,
    chats: List<ChatUiState>,
    onChatClicked: (ChatUiState) -> Unit
) {
    AnimatedContent(
        targetState = chats.isEmpty() to isLoading,
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) { (isEmpty, isLoading) ->

        when {
            isEmpty && isLoading -> {
                ChatSummaryListSkeleton()
            }

            isEmpty && !isLoading -> {
                NoChatsHistoryView(modifier = Modifier.padding(Theme.spacing._24))
            }

            else -> {
                ChatSummaryListContent(listState, chats, onChatClicked)
            }
        }
    }
}

@Composable
@OptIn(ExperimentalUuidApi::class)
private fun ChatSummaryListContent(
    listState: LazyListState,
    chats: List<ChatUiState>,
    onChatClicked: (ChatUiState) -> Unit
) {
    Column {
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
                .fillMaxWidth(),
            contentPadding = PaddingValues(vertical = Theme.spacing._12),
            verticalArrangement = Arrangement.spacedBy(Theme.spacing._16)
        ) {
            items(
                items = chats,
                key = { it.id }
            ) { chat ->
                ChatItem(
                    chat = chat,
                    onChatClicked = onChatClicked
                )
            }
        }
    }
}

@Composable
private fun EffectsHandler(
    effects: SharedFlow<HomeScreenEffect>,
) {
    val snackBarHostController = LocalSnackBarHostController.current
    val navController = LocalNavController.current

    EffectHandler(effects = effects) { effect ->
        when (effect) {

            is HomeScreenEffect.NavigateToChat -> {
                navController.navigate(
                    ChatDetailsRoute(
                        chatId = effect.chatId,
                        chatName = effect.chatName
                    )
                )
            }

            HomeScreenEffect.NavigateToSyncContacts -> {
                navController.navigate(SyncContactsRoute(forceSync = false))
            }

            HomeScreenEffect.NavigateToContacts -> {
                navController.navigate(ContactsRoute)
            }

            HomeScreenEffect.NavigateToWallet -> {
                navController.navigate(WalletRoute)
            }

            is HomeScreenEffect.ShowSnackBar -> {
                snackBarHostController.showSnackBar(effect.snackBarData)
            }
        }
    }
}

private fun formatBalance(balance: Double): String {
    val wholePart = balance.toLong()
    val wholeString = wholePart.toString()
        .reversed()
        .chunked(3)
        .joinToString(",")
        .reversed()

    return wholeString
}