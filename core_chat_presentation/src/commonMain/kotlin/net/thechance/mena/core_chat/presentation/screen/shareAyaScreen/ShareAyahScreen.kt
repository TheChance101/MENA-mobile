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
import net.thechance.mena.core_chat.presentation.screen.shareAyaScreen.components.ShareAyahSearchContactContent
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
        ShareAyahSearchContactContent(contacts = contacts, state = state, interactions = interactions)
    }
}

@OptIn(ExperimentalUuidApi::class)
@Composable
private fun EffectsHandler(effects: SharedFlow<ShareAyahEffect>, onClickBack: () -> Unit) {
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