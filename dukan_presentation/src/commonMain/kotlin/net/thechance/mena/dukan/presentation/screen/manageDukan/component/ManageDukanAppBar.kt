package net.thechance.mena.dukan.presentation.screen.manageDukan.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.back_arrow
import mena.dukan_presentation.generated.resources.ic_arrow_left
import mena.dukan_presentation.generated.resources.my_dukan
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.util.stubPreviews.PreviewManageDukanInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.manageDukan.ManageDukanInteractionListener
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ManageDukanAppBar(listener: ManageDukanInteractionListener) {
    AppBar(
        title = stringResource(Res.string.my_dukan),
        onLeadingClick = listener::onBackClicked,
        contentPadding = PaddingValues(
            horizontal = Theme.spacing._16,
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

@Preview
@Composable
private fun ManageDukanAppBarPreview() {
    MenaTheme {
        ManageDukanAppBar(
            listener = PreviewManageDukanInteractionListener
        )

    }
}