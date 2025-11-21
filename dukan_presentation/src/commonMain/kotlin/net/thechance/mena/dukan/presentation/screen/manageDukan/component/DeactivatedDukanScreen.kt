package net.thechance.mena.dukan.presentation.screen.manageDukan.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.deactivated_dukan
import mena.dukan_presentation.generated.resources.dukan_waiting_approval
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.state.ImageWithTextContainer
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DeactivatedDukanScreen(
    onBackClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colorScheme.background.surface)
            .systemBarsPadding(), horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ImageWithTextContainer(
            foregroundImageRes = Res.drawable.deactivated_dukan,
            haveBlurBackground = false,
            header = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(Theme.spacing._2),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Your dukan has been deactivated because:",
                        style = (Theme.typography.title.small).copy(textAlign = TextAlign.Center),
                        color = Theme.colorScheme.shadePrimary
                    )
                    Text(
                        text = "Wrong information provided.",
                        style = Theme.typography.body.small.copy(textAlign = TextAlign.Center),
                        color = Theme.colorScheme.shadeSecondary
                    )
                }
            },
            bodyText = stringResource(Res.string.dukan_waiting_approval),
        )

    }
}

@Preview
@Composable
fun DeactivatedDukanScreenPreview() {
    MenaTheme {
        DeactivatedDukanScreen(onBackClick = {})
    }
}
