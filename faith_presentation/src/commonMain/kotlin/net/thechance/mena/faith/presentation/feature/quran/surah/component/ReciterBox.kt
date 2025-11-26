package net.thechance.mena.faith.presentation.feature.quran.surah.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.cancel_icon
import mena.faith_presentation.generated.resources.icon_cancel
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ReciterBox(
    reciterName: String,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(
                color = Theme.colorScheme.background.surfaceLow,
                shape = RoundedCornerShape(topStart = Theme.radius.sm, topEnd = Theme.radius.sm)
            )
            .padding(horizontal = Theme.spacing._16, vertical = Theme.spacing._4),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = reciterName,
            style = Theme.typography.label.medium,
            color = Theme.colorScheme.shadePrimary,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier.weight(1f)
        )

        Icon(
            painter = painterResource(Res.drawable.icon_cancel),
            tint = Theme.colorScheme.primary.primary,
            contentDescription = stringResource(Res.string.cancel_icon),
            modifier = modifier.size(24.dp)
                .clickable(onClick = onCancelClick)
        )
    }
}

@Preview
@Composable
private fun Preview() {
    MenaTheme {
        QuranTheme {
            ReciterBox(
                reciterName = "Maytham Al-Tammar",
                onCancelClick = {}
            )
        }
    }
}
