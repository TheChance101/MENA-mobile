package net.thechance.mena.dukan.presentation.screen.createDukan.content

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.back_arrow
import mena.dukan_presentation.generated.resources.create
import mena.dukan_presentation.generated.resources.create_new_dukan
import mena.dukan_presentation.generated.resources.dukan_image
import mena.dukan_presentation.generated.resources.ic_arrow_left
import mena.dukan_presentation.generated.resources.next
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.shared.SnackBar
import net.thechance.mena.dukan.presentation.util.OnSystemBackPressed
import net.thechance.mena.dukan.presentation.util.stubPreviews.PreviewCreateDukanInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanUiState
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanUiState.CreateDukanStep
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun CreateDukanContent(
    state: CreateDukanUiState,
    listener: CreateDukanInteractionListener
) {

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { CreateDukanStep.steps.size }
    )

    SyncPageWithScreenState(
        state = state,
        pagerState = pagerState
    )

    OnSystemBackPressed(listener::onBackClicked)
    CreateDukanScaffold(
        state = state,
        listener = listener,
        pagerState = pagerState
    )
}

@Composable
private fun CreateDukanScaffold(
    state: CreateDukanUiState,
    listener: CreateDukanInteractionListener,
    pagerState: PagerState
) {
    Scaffold(
        topBar = {
            CreateDukanAppBar(state, listener)
        },
        snakeBar = {
            state.snackBarState?.let { snackBarState ->
                SnackBar(
                    snackBarUiState = snackBarState,
                    onDismiss = listener::onDismissSnackBar
                )
            }
        },
        bottomBar = {
            if (state.isImageBeingCropped.not())
                PrimaryButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Theme.spacing._16),
                    text = if (state.currentStep == CreateDukanStep.SELECT_STYLE)
                        stringResource(Res.string.create)
                    else
                        stringResource(Res.string.next),
                    onClick = listener::onNextOrCreateClicked,
                    isEnabled = state.isButtonEnabled,
                    isLoading = state.isNextCreateButtonLoading,
                    contentPadding = PaddingValues(vertical = Theme.spacing._12)
                )
        }
    ) {
        CreateDukanPagerContent(
            pagerState = pagerState,
            state = state,
            listener = listener
        )
    }
}

@Composable
private fun CreateDukanAppBar(
    state: CreateDukanUiState,
    listener: CreateDukanInteractionListener
) {
    AppBar(
        title = if (state.isImageBeingCropped)
            stringResource(Res.string.dukan_image)
        else stringResource(
            Res.string.create_new_dukan
        ),
        onLeadingClick = listener::onBackClicked,
        contentPadding = PaddingValues(
            horizontal = Theme.spacing._12,
            vertical = Theme.spacing._8
        ),
        leadingContent = {
            Icon(
                painter = painterResource(Res.drawable.ic_arrow_left),
                contentDescription = stringResource(Res.string.back_arrow),
                tint = Theme.colorScheme.primary.primary
            )
        }
    )
}

@Composable
private fun CreateDukanPagerContent(
    pagerState: PagerState,
    state: CreateDukanUiState,
    listener: CreateDukanInteractionListener
) {
    HorizontalPager(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = Theme.spacing._12),
        state = pagerState,
        userScrollEnabled = false
    ) { pageIndex ->
        when (CreateDukanStep.steps[pageIndex]) {
            CreateDukanStep.BASIC_INFORMATION -> CreateDukanPagerContent(
                state = state,
                interactionListener = listener
            )

            CreateDukanStep.SELECT_IMAGE -> UploadDukanImageContent(
                state = state,
                interactionListener = listener
            )

            CreateDukanStep.SELECT_LOCATION -> CreateDukanContentSelectLocation(
                state = state,
                listener = listener
            )

            CreateDukanStep.SELECT_STYLE -> CreateDukanContentSelectStyle(
                state = state,
                listener = listener
            )
        }
    }
}

@Composable
private fun SyncPageWithScreenState(
    state: CreateDukanUiState,
    pagerState: PagerState
) {
    val currentStepIndex = state.currentStep.ordinal
    LaunchedEffect(currentStepIndex) {
        if (currentStepIndex == pagerState.currentPage) return@LaunchedEffect
        pagerState.animateScrollToPage(currentStepIndex)
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewCreateDukanContent() {
    val mockState = CreateDukanUiState(
        name = "My Dukan",
        currentStep = CreateDukanStep.BASIC_INFORMATION,
        isButtonEnabled = true,
        isNextCreateButtonLoading = false,
        isImageBeingCropped = false,
        snackBarState = null
    )
    MenaTheme {
        CreateDukanContent(
            state = mockState,
            listener = PreviewCreateDukanInteractionListener
        )
    }
}