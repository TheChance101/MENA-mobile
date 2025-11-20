package net.thechance.mena.trends.presentation.shared.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import mena.trends_presentation.generated.resources.Res
import mena.trends_presentation.generated.resources.back_arrow
import mena.trends_presentation.generated.resources.ic_arrow_left
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun BackIcon(
    modifier: Modifier = Modifier,
) {
    Icon(
        painter = painterResource(Res.drawable.ic_arrow_left),
        contentDescription = stringResource(Res.string.back_arrow),
        tint = Theme.colorScheme.shadePrimary,
        modifier = modifier
    )
}