package net.thechance.mena.dukan.presentation.screen.createDukan.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.ic_arrow_left
import mena.dukan_presentation.generated.resources.pencil_edit_01
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.util.OnSystemBackPressed
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanUiState
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanUiState.CreateDukanStep
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalComposeUiApi::class)
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colorScheme.background.surface)
            .systemBarsPadding()
    ) {
        AppBar(
            title = "Create New Dukan",
            onLeadingClick = listener::onBackClicked,
            leadingContent = {
                Icon(
                    painter = painterResource(Res.drawable.ic_arrow_left),
                    contentDescription = "Back Arrow"
                )
            }
        )
        HorizontalPager(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(top = Theme.spacing._12),
            state = pagerState,
            userScrollEnabled = false
        ) { pageIndex ->
            when (CreateDukanStep.steps[pageIndex]) {
                CreateDukanStep.BASIC_INFORMATION -> CreateDukanContentBasicInformation()
                CreateDukanStep.SELECT_IMAGE -> CreateDukanContentSelectImage()
                CreateDukanStep.SELECT_LOCATION -> CreateDukanContentSelectLocation()
                CreateDukanStep.SELECT_STYLE -> CreateDukanContentSelectStyle()
            }
        }

        PrimaryButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Theme.spacing._16),
            text = if (state.currentStep == CreateDukanStep.SELECT_STYLE)
                "Create"
            else
                "Next",
            onClick = listener::onButtonClicked,
            trailingIcon = painterResource(Res.drawable.pencil_edit_01),
            isEnabled = state.isButtonEnabled,
            isLoading = state.isButtonLoading
        )
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
        try {
            pagerState.animateScrollToPage(currentStepIndex)
        } catch (_: Exception) {
        }
    }
}