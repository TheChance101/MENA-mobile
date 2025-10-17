package net.thechance.mena.identity.presentation.components

import androidx.compose.runtime.Composable
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.back
import mena.identity_presentation.generated.resources.ic_arrow_left
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun AuthAppBar(
    title: String,
    onBackClicked: () -> Unit
) {
    AppBar(
        title = title,
        leadingContent = {
            Icon(
                painter = painterResource(Res.drawable.ic_arrow_left),
                contentDescription = stringResource(Res.string.back),
            )
        },
        onLeadingClick = onBackClicked
    )
}