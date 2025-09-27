package net.thechance.mena.identity.presentation.components

import androidx.compose.runtime.Composable
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.ic_arrow_left
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import org.jetbrains.compose.resources.painterResource

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
                contentDescription = "Back"
            )
        },
        onLeadingClick = onBackClicked
    )
}