package net.thechance.mena.dukan.presentation.screen.dukanDetails.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.back_arrow
import mena.dukan_presentation.generated.resources.dukan_location
import mena.dukan_presentation.generated.resources.ic_arrow_left
import mena.dukan_presentation.generated.resources.ic_favorite
import mena.dukan_presentation.generated.resources.ic_share
import mena.dukan_presentation.generated.resources.ic_shopping_basket
import mena.dukan_presentation.generated.resources.shopping_basket_icon
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.appBar.AppBarOptionContainer
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.state.NoInternetContent
import net.thechance.mena.dukan.presentation.screen.dukanDetails.components.smallImageDukanDetails.SmallImageDukanIconButton
import net.thechance.mena.dukan.presentation.screen.dukanDetails.components.smallImageDukanDetails.SmallImageDukanShelves
import net.thechance.mena.dukan.presentation.screen.dukanDetails.components.smallImageDukanDetails.SmallImageDukanStoreImage
import net.thechance.mena.dukan.presentation.util.OnSystemBackPressed
import net.thechance.mena.dukan.presentation.util.stubPreviews.PreviewDukanDetailsInteractionListener
import net.thechance.mena.dukan.presentation.util.stubPreviews.fakeDukanDetails
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SmallImageDukanDetails(
    state: DukanDetailsUiState,
    listener: DukanDetailsInteractionListener,
) {
    OnSystemBackPressed(listener::onBackClicked)

    Scaffold(
        topBar = {
            SmallImageAppBar(listener)
        }
    ) {
        if (state.dukanDetailsState==DukanDetailsUiState.DukanDetailsState.ERROR){
            NoInternetContent(
                onRetry = listener::onRetryClicked,
                modifier = Modifier.fillMaxSize()
            )
            return@Scaffold
        }
        Column {
            SmallImageDukanStoreImage(
                dukanInfoState = state.dukanInfo,
                modifier = Modifier.padding(
                    start = Theme.spacing._16,
                    end = Theme.spacing._16,
                    top = Theme.spacing._4,
                    bottom = Theme.spacing._16
                )
            )
            Row(
                modifier = Modifier.padding(horizontal = Theme.spacing._16),
                horizontalArrangement = Arrangement.spacedBy(Theme.spacing._4),
            ) {
                SmallImageDukanIconButton(
                    icon = painterResource(Res.drawable.ic_favorite),
                    iconColor = Color(state.dukanInfo.color),
                    modifier = Modifier.weight(1f)
                )
                SmallImageDukanIconButton(
                    icon = painterResource(Res.drawable.ic_share),
                    iconColor = Color(state.dukanInfo.color),
                    modifier = Modifier.weight(1f)
                )
                SmallImageDukanIconButton(
                    icon = painterResource(Res.drawable.dukan_location),
                    iconColor = Color(state.dukanInfo.color),
                    modifier = Modifier.weight(1f)
                )
            }
            SmallImageDukanShelves(
                state = state,
                listener = listener,
                modifier = Modifier.padding(top = Theme.spacing._16)
            )
        }
    }
}

@Composable
private fun SmallImageAppBar(
    listener: DukanDetailsInteractionListener
) {
    AppBar(
        title = "",
        leadingContent = {
            Icon(
                painter = painterResource(Res.drawable.ic_arrow_left),
                contentDescription = stringResource(Res.string.back_arrow)
            )
        },
        onLeadingClick = listener::onBackClicked,
        trailingContent = {
            AppBarOptionContainer {
                Icon(
                    painter = painterResource(Res.drawable.ic_shopping_basket),
                    contentDescription = stringResource(Res.string.shopping_basket_icon)
                )
            }
        }
    )
}

@Preview
@Composable
private fun SmallImageDukanDetailsPreview() {
    MenaTheme {
        SmallImageDukanDetails(
            state = fakeDukanDetails,
            listener = PreviewDukanDetailsInteractionListener,
        )
    }
}

@Preview
@Composable
private fun SmallImageDukanDetailsLoadingPreview() {
    MenaTheme {
        SmallImageDukanDetails(
            state = fakeDukanDetails,
            listener = PreviewDukanDetailsInteractionListener,
        )
    }
}
