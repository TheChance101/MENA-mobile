package net.thechance.mena.core_chat.presentation.screen.shareAyaScreen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.cash.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.SharedFlow
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.ic_arrow_left
import mena.core_chat_presentation.generated.resources.send_to
import net.thechance.mena.core_chat.presentation.components.snackBarHost.LocalSnackBarHostController
import net.thechance.mena.core_chat.presentation.navigation.ChatDetailsRoute
import net.thechance.mena.core_chat.presentation.navigation.LocalNavController
import net.thechance.mena.core_chat.presentation.screen.shareAyaScreen.components.SearchContactToShareView
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
fun ShareMessageScreen(
    viewModel: ShareMessageViewModel = koinViewModel<ShareMessageViewModel>(),
    onClickBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val effects = viewModel.effect
    EffectsHandler(effects = effects, onClickBack = onClickBack)
    ShareMessageContent(state = state, interactions = viewModel)
}

@OptIn(ExperimentalUuidApi::class)
@Composable
private fun ShareMessageContent(
    state: ShareMessageScreenState,
    interactions: ShareMessageInteractionListener,
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
                onLeadingClick = interactions::onBackClicked,
            )
        }
    ) {
        SearchContactToShareView(contacts = contacts, state = state, interactions = interactions)
    }
}

@OptIn(ExperimentalUuidApi::class)
@Composable
private fun EffectsHandler(effects: SharedFlow<ShareMessageEffect>, onClickBack: () -> Unit) {
    val snackBarHostController = LocalSnackBarHostController.current
    val navController = LocalNavController.current
    EffectHandler(effects = effects) { effect ->
        when (effect) {
            ShareMessageEffect.NavigateBack -> {
                onClickBack()
            }

            is ShareMessageEffect.NavigateToChatScreen -> {
                navController.navigate(
                    ChatDetailsRoute(
                        chatId = effect.chatId.toString(),
                        chatName = effect.chatName
                    )
                )
            }

            is ShareMessageEffect.ShowSnackBar -> snackBarHostController.showSnackBar(effect.snackBarData)
        }
    }
}