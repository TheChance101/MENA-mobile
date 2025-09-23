package net.thechance.mena.faith.presentation.component

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.back_icon
import mena.faith_presentation.generated.resources.ic_arrow_left
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun BackIcon() {
    Icon(
        painter = painterResource(Res.drawable.ic_arrow_left),
        contentDescription = stringResource(Res.string.back_icon),
        tint = Theme.colorScheme.primary.primary,
        modifier = Modifier.size(20.dp)
    )
}
