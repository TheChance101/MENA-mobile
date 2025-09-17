package net.thechance.mena.core_chat.presentation.screen.syncContacts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import kotlinx.coroutines.flow.Flow
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.ic_arrow_left
import mena.core_chat_presentation.generated.resources.sync_contacts
import net.thechance.mena.core_chat.presentation.components.AnimatedSnackBarHost
import net.thechance.mena.core_chat.presentation.navigation.ContactsRoute
import net.thechance.mena.core_chat.presentation.navigation.LocalNavController
import net.thechance.mena.core_chat.presentation.screen.syncContacts.components.ContactsSyncedView
import net.thechance.mena.core_chat.presentation.screen.syncContacts.components.GoToSettingsView
import net.thechance.mena.core_chat.presentation.screen.syncContacts.components.NoContactsSyncView
import net.thechance.mena.core_chat.presentation.screen.syncContacts.components.PhoneIcon
import net.thechance.mena.core_chat.presentation.utils.EffectHandler
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.icon.MenaIcon
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf


@Composable
fun SyncContactsScreen(forceSync: Boolean = false) {

    val factory = rememberPermissionsControllerFactory()
    val controller = remember(factory) { factory.createPermissionsController() }
    val viewModel: SyncContactsViewModel = koinViewModel { parametersOf(controller) }
    BindEffect(controller)

    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onForceSync(forceSync = forceSync)
    }

    SyncContactsEffectsHandler(viewModel.effect)

    SyncContactsContent(
        state = state,
        interactionListener = viewModel,
    )
}


@Composable
private fun SyncContactsContent(
    state: SyncContactsState,
    interactionListener: SyncContactsScreenInteractionListener,
) {

    Column(
        modifier = Modifier.fillMaxSize()
            .background(color = Theme.colorScheme.background.surface)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        AppBar(
            title = stringResource(Res.string.sync_contacts),
            contentPadding = PaddingValues(horizontal = Theme.spacing._12, vertical = Theme.spacing._8),
            leadingContent = {
                MenaIcon(
                    painter = painterResource(Res.drawable.ic_arrow_left),
                    modifier = Modifier.size(20.dp),
                    contentDescription = null,
                    tint = Theme.colorScheme.primary.primary,
                )
            },
            onLeadingClick = interactionListener::onBackClick,
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Theme.spacing._24),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            if (state.showSyncView) {
                PhoneIcon()
                when {
                    state.isLoading -> {
                        ContactsSyncedView(modifier = Modifier.padding(top = Theme.spacing._24))
                    }

                    state.deniedPermanently -> {
                        GoToSettingsView(
                            onSyncClick = interactionListener::onSyncClick,
                            modifier = Modifier.padding(top = Theme.spacing._12)
                        )
                    }

                    else -> {
                        NoContactsSyncView(
                            modifier = Modifier.padding(top = Theme.spacing._12),
                            onSyncClick = interactionListener::onSyncClick,
                        )
                    }
                }
            }
        }
    }
    Box(
        modifier = Modifier.fillMaxSize().statusBarsPadding().padding(horizontal =  Theme.spacing._16),
        contentAlignment = Alignment.TopCenter
    ) {
        AnimatedSnackBarHost(
            data = state.snackBarData,
            onDismiss = interactionListener::onSnackBarDismiss
        )
    }
}

@Composable
private fun SyncContactsEffectsHandler(effects: Flow<SyncContactsScreenEffect>) {
    val navController = LocalNavController.current
    EffectHandler(effects) { effect ->
        when (effect) {
            SyncContactsScreenEffect.NavigateToContacts -> {
                navController.popBackStack()
                navController.navigate(ContactsRoute)
            }

            SyncContactsScreenEffect.NavigateBack -> {
                navController.popBackStack()
            }

            SyncContactsScreenEffect.NavigateBackWithResult -> {
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set("is_sync_success", true)
                navController.popBackStack()
            }
        }
    }
}

@Composable
@Preview()
private fun SyncContactsScreenPreview() {
    MenaTheme {
        SyncContactsContent(
            state = SyncContactsState(showSyncView = true, isLoading = false),
            interactionListener = object :
                SyncContactsScreenInteractionListener {
                override fun onForceSync(forceSync: Boolean) {}
                override fun onSnackBarDismiss() {}
                override fun onBackClick() {}
                override fun onSyncClick() {}
            }
        )
    }
}
