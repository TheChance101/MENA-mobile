package net.thechance.mena.admin_panel.presentation.screen.dukan_managements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import net.thechance.mena.admin_panel.navigation.DukanDetails
import net.thechance.mena.admin_panel.navigation.LocalNavController
import net.thechance.mena.admin_panel.presentation.component.EmptyDukansState
import net.thechance.mena.admin_panel.presentation.component.EmptySearchState
import net.thechance.mena.admin_panel.presentation.component.PanelScaffold
import net.thechance.mena.admin_panel.presentation.component.SnackBarContainer
import net.thechance.mena.admin_panel.presentation.screen.dukan_managements.component.DukanManagementHeader
import net.thechance.mena.admin_panel.presentation.screen.dukan_managements.component.DukanManagementTableContent
import net.thechance.mena.admin_panel.presentation.utils.ObserveAsEffect
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.dukan_management
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.uuid.ExperimentalUuidApi

@Composable
fun DukanManagementsScreen(
    viewmodel: DukanManagementViewmodel = koinViewModel()
) {
    val state by viewmodel.state.collectAsStateWithLifecycle()
    val navController = LocalNavController.current
    ObserveAsEffect(
        effect = viewmodel.uiEffect,
        onEffect = { effect ->
            onDukanManagementEffect(
                effect = effect,
                navController = navController
            )
        }
    )
    DukanManagementsContent(
        state = state,
        interactionListener = viewmodel
    )
}

@Composable
fun DukanManagementsContent(
    state: DukanManagementScreenState,
    interactionListener: DukanManagementInteractionListener,
    modifier: Modifier = Modifier
) {
    PanelScaffold(
        topBar = { DukanManagementScreenTopBar() },
        snackBar = { SnackBarContainer(snackBarState = state.snackBar) },
        errorState = state.errorState,
        isLoading = state.isInitialLoading,
        onRetry = interactionListener::onRetryClicked
    ) {
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DukanManagementHeader(
                dukansNumbers = state.totalDukans,
                onQueryChange = interactionListener::onSearchQueryChanged,
                onClearQueryClicked = interactionListener::onClearQueryClicked,
                query = state.query
            )
            when {
                state.dukans.isEmpty() && !state.isLoading -> {
                    if (state.query.isNotEmpty())
                        EmptySearchState(modifier = Modifier.fillMaxSize().offset(y = -(76.dp)))
                    else {
                        EmptyDukansState(modifier = Modifier.fillMaxSize().offset(y = -(76.dp)))
                    }
                }

                else -> {
                    DukanManagementTableContent(state, interactionListener)
                }
            }
        }
    }
}

@Composable
private fun DukanManagementScreenTopBar() {
    AppBar(
        title = stringResource(Res.string.dukan_management),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 18.dp),
        modifier = Modifier.background(Theme.colorScheme.background.surfaceLow)
    )
}


@OptIn(ExperimentalUuidApi::class)
private fun onDukanManagementEffect(
    effect: DukanManagementEffect,
    navController: NavController
) {
    when (effect) {
        is DukanManagementEffect.NavigateToDukanDetails -> {
            navController.navigate(DukanDetails) {
                popUpTo(DukanDetails)
            }
        }
    }
}

