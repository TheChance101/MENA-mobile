package net.thechance.mena.faith.presentation.feature.mosque.component

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.add
import mena.faith_presentation.generated.resources.arrow_left
import mena.faith_presentation.generated.resources.ic_add
import mena.faith_presentation.generated.resources.nearby_mosques
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.appBar.AppBarOptionContainer
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun NearbyMosqueTopbar(
    onBackClick: () -> Unit,
    onAddMosqueClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppBar(
        modifier = modifier,
        title = stringResource(Res.string.nearby_mosques),
        leadingContent = {
            Icon(
                painter = painterResource(Res.drawable.arrow_left),
                contentDescription = stringResource(Res.string.arrow_left),
                tint = Theme.colorScheme.primary.primary,
                modifier = Modifier.size(20.dp)
            )
        },
        onLeadingClick = onBackClick,
        trailingContent = {
            AppBarIcon(
                iconRes = painterResource(Res.drawable.ic_add),
                contentDescription = stringResource(Res.string.add),
                onClick = onAddMosqueClick
            )
        }
    )
}

@Composable
private fun AppBarIcon(
    iconRes: Painter,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppBarOptionContainer(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            painter = iconRes,
            contentDescription = contentDescription,
            tint = Theme.colorScheme.primary.primary
        )
    }
}

@Preview
@Composable
private fun Preview() {
    MenaTheme {
        QuranTheme {
            NearbyMosqueTopbar(
                onBackClick = {},
                onAddMosqueClick = {},
            )
        }
    }
}
