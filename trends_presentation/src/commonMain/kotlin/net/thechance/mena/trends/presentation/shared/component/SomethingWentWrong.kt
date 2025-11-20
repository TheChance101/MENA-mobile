package net.thechance.mena.trends.presentation.shared.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import mena.trends_presentation.generated.resources.Res
import mena.trends_presentation.generated.resources.ic_something_went_wrong
import mena.trends_presentation.generated.resources.ic_something_went_wrong_dark
import mena.trends_presentation.generated.resources.retry
import mena.trends_presentation.generated.resources.something_went_wrong_description
import mena.trends_presentation.generated.resources.something_went_wrong_title
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.trends.presentation.navigation.LocalDarkTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun SomethingWentWrong(
    modifier: Modifier = Modifier,
    onRetry: () -> Unit
) {
    val icon = if (LocalDarkTheme.current)
        painterResource(Res.drawable.ic_something_went_wrong_dark)
    else
        painterResource(Res.drawable.ic_something_went_wrong)

    StatePlaceholder(
        icon = icon,
        title = stringResource(Res.string.something_went_wrong_title),
        description = stringResource(Res.string.something_went_wrong_description),
        bottomContent = {
            PrimaryButton(
                text = stringResource(Res.string.retry),
                onClick = onRetry,
                modifier = Modifier.fillMaxWidth()
            )
        },
        modifier = modifier
    )
}