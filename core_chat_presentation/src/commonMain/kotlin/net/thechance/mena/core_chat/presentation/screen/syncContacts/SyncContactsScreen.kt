package net.thechance.mena.core_chat.presentation.screen.syncContacts

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import kotlinx.coroutines.flow.SharedFlow
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.ic_arrow_left
import mena.core_chat_presentation.generated.resources.sync_contacts
import net.thechance.mena.core_chat.presentation.navigation.ContactsRoute
import net.thechance.mena.core_chat.presentation.utils.EffectHandler
import net.thechance.mena.core_chat.presentation.navigation.LocalNavController
import net.thechance.mena.core_chat.presentation.components.snackBarHost.LocalSnackBarHostController
import net.thechance.mena.core_chat.presentation.screen.syncContacts.components.ContactsSyncingView
import net.thechance.mena.core_chat.presentation.screen.syncContacts.components.GoToSettingsView
import net.thechance.mena.core_chat.presentation.screen.syncContacts.components.NoContactsSyncView
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf


@Composable
fun SyncContactsScreen() {

    val factory = rememberPermissionsControllerFactory()
    val controller = remember(factory) { factory.createPermissionsController() }
    val viewModel: SyncContactsViewModel = koinViewModel { parametersOf(controller) }
    BindEffect(viewModel.permissionsController)

    val state by viewModel.state.collectAsStateWithLifecycle()
    val effects = viewModel.effect

    EffectsHandler(effects = effects, isFirstSync = state.isFirstSync)

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        if (!state.isOpenSettingsCalled) return@LifecycleEventEffect

        viewModel.checkPermissions()
    }

    SyncContactsContent(
        state = state,
        interactionListener = viewModel,
    )
}

@Composable
private fun SyncContactsContent(
    state: SyncContactsScreenState,
    interactionListener: SyncContactsInteractionListener,
) {
    Column(
        modifier = Modifier.fillMaxSize()
            .background(color = Theme.colorScheme.background.surface)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        AppBar(
            title = stringResource(Res.string.sync_contacts),
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
            onLeadingClick = interactionListener::onBackClicked,
        )
        AnimatedContent(
            targetState = state.isPermissionDeniedPermanently,
            label = "contacts_content_animation",
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Theme.spacing._24),
            contentAlignment = Alignment.Center
        ) { isDeniedPermanently ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isDeniedPermanently -> {
                        GoToSettingsView(
                            onGoToSettingClick = interactionListener::onGoToSettingsClicked,
                            modifier = Modifier.padding(top = Theme.spacing._12)
                        )
                    }

                    state.showSyncView && state.isLoading -> {
                        ContactsSyncingView(modifier = Modifier.padding(top = Theme.spacing._24))
                    }

                    state.showSyncView -> {
                        NoContactsSyncView(
                            modifier = Modifier.padding(top = Theme.spacing._12),
                            onSyncClick = interactionListener::onSyncClicked,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EffectsHandler(
    effects: SharedFlow<SyncContactsScreenEffect>,
    isFirstSync: Boolean
) {
    val snackBarHostController = LocalSnackBarHostController.current
    val navController = LocalNavController.current

    EffectHandler(effects) { effect ->
        when (effect) {
            is SyncContactsScreenEffect.NavigateBack -> {
                navController.popBackStack()
            }

            SyncContactsScreenEffect.NavigateToContactsAfterSyncSuccess -> {
                if (isFirstSync) {
                    navController.popBackStack()
                    navController.navigate(ContactsRoute)
                } else {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(IS_SYNC_SUCCESS, true)
                    navController.popBackStack()
                }
            }

            is SyncContactsScreenEffect.ShowSnackBar -> {
                snackBarHostController.showSnackBar(effect.snackBarData)
            }
        }
    }
}