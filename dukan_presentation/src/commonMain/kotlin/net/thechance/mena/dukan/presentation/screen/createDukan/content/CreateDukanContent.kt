package net.thechance.mena.dukan.presentation.screen.createDukan.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.arrow_left_01
import mena.dukan_presentation.generated.resources.pencil_edit_01
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanUiState
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanViewModel
import org.jetbrains.compose.resources.painterResource

@Composable
fun CreateDukanContent(
    state: CreateDukanUiState,
    listener: CreateDukanInteractionListener
) {

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { CreateDukanViewModel.MAX_STEPS }
    )

    SyncPageWithScreenState(
        state = state,
        pagerState = pagerState
    )

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
                    painter = painterResource(Res.drawable.arrow_left_01),
                    contentDescription = "Back Arrow"
                )
            }
        )
        HorizontalPager(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(top = 12.dp),
            state = pagerState,
            userScrollEnabled = false
        ) { pageIndex ->
            when (pageIndex) {
                0 -> CreateDukanContentBasicInformation()
                1 -> CreateDukanContentSelectImage()
                2 -> CreateDukanContentSelectLocation()
                3 -> CreateDukanContentSelectStyle()
            }
        }

        PrimaryButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            text = if (state.currentStep == 4)
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
    val currentStepIndex = state.currentStep - 1
    LaunchedEffect(currentStepIndex) {
        if (currentStepIndex == pagerState.currentPage) return@LaunchedEffect
        try {
            pagerState.animateScrollToPage(currentStepIndex)
        } catch (_: Exception) {
        }
    }
}