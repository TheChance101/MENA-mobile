package net.thechance.mena.identity.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.back
import mena.identity_presentation.generated.resources.ic_arrow_left
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun AuthAppBar(
    title: String,
    onClickBack: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Theme.colorScheme.background.surface
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        AppBar(
            title = title,
            leadingContent = {
                Icon(
                    painter = painterResource(Res.drawable.ic_arrow_left),
                    contentDescription = stringResource(Res.string.back),
                    tint = Theme.colorScheme.shadePrimary
                )
            },
            onLeadingClick = onClickBack
        )
    }
}