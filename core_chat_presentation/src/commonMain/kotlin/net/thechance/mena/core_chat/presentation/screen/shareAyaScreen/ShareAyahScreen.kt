package net.thechance.mena.core_chat.presentation.screen.shareAyaScreen

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import app.cash.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.SharedFlow
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.could_not_load_contacts
import mena.core_chat_presentation.generated.resources.ic_arrow_left
import mena.core_chat_presentation.generated.resources.search_by_name
import mena.core_chat_presentation.generated.resources.send_to
import mena.core_chat_presentation.generated.resources.something_went_wrong
import net.thechance.mena.core_chat.presentation.components.ErrorView
import net.thechance.mena.core_chat.presentation.components.LoadingView
import net.thechance.mena.core_chat.presentation.components.snackBarHost.LocalSnackBarHostController
import net.thechance.mena.core_chat.presentation.navigation.ChatDetailsRoute
import net.thechance.mena.core_chat.presentation.navigation.LocalNavController
import net.thechance.mena.core_chat.presentation.screen.contacts.components.ContactsList
import net.thechance.mena.core_chat.presentation.screen.shareAyaScreen.components.SearchBar
import net.thechance.mena.core_chat.presentation.utils.EffectHandler
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.uuid.ExperimentalUuidApi

@Composable
fun ShareAyahScreen(
    viewModel: ShareAyahViewModel = koinViewModel<ShareAyahViewModel>(),
    onClickBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val effects = viewModel.effect
    EffectsHandler(effects = effects, onClickBack = onClickBack)
    ShareAyahContent(state = state, interactions = viewModel as ShareAyahInterActionListener)
}

@OptIn(ExperimentalUuidApi::class)
@Composable
fun ShareAyahContent(
    state: ShareAyahScreenState,
    interactions: ShareAyahInterActionListener,
) {

    val contacts = state.contacts.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            AppBar(
                modifier = Modifier,
                title = stringResource(Res.string.send_to),
                contentPadding = PaddingValues(
                    horizontal = Theme.spacing._12,
                    vertical = Theme.spacing._8
                ),
                leadingContent = {
                    Icon(
                        painter = painterResource(Res.drawable.ic_arrow_left),
                        modifier = Modifier.size(20.dp),
                        contentDescription = null,
                        tint = Theme.colorScheme.primary.primary,
                    )
                },
                onLeadingClick = interactions::onClickBack,
            )
        }
    ) {

        AnimatedContent(
            targetState = contacts.loadState.refresh,
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) { loadState ->
            when (loadState) {
                is LoadState.Loading -> {
                    LoadingView()
                }

                is LoadState.Error -> {
                    ErrorView(
                        title = stringResource(Res.string.something_went_wrong),
                        message = stringResource(Res.string.could_not_load_contacts),
                        onRetry = { }
                    )
                }

                else -> {
                    Column {
                        SearchBar(
                            value = state.searchQuery,
                            hint = stringResource(Res.string.search_by_name),
                            onValueChange = { query -> interactions.onChangeSearchQuery(query = query) },
                            onClearQueryClicked = interactions::onClickClearQuery,
                            modifier = Modifier.padding(
                                horizontal = Theme.spacing._16,
                                vertical = Theme.spacing._8
                            )
                        )
                        ContactsList(
                            contacts = contacts,
                            onContactClick = interactions::onClickContact
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalUuidApi::class)
@Composable
fun EffectsHandler(effects: SharedFlow<ShareAyahEffect>, onClickBack: () -> Unit) {
    val snackBarHostController = LocalSnackBarHostController.current
    val navController = LocalNavController.current
    EffectHandler(effects = effects) { effect ->
        when (effect) {
            ShareAyahEffect.NavigateBack -> {
                onClickBack()
            }

            is ShareAyahEffect.NavigateToChatScreen -> {
                navController.navigate(
                    ChatDetailsRoute(
                        chatId = effect.chatId.toString(),
                        chatName = effect.chatName
                    )
                )
            }

            is ShareAyahEffect.ShowSnackBar -> snackBarHostController.showSnackBar(effect.snackBarData)
        }
    }
}