package net.thechance.mena.faith.presentation.feature.quran.tilwah.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.downloaded
import mena.faith_presentation.generated.resources.ic_tick_double_check
import mena.faith_presentation.generated.resources.success
import net.thechance.mena.designsystem.presentation.component.button.radioButton.RadioButton
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ReciterItem(
    reciter: String,
    recitingType: String,
    isDownloaded: Boolean,
    onSelect: () -> Unit = {},
    isSelectedShown: Boolean,
    isSelected: Boolean = false,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .padding(horizontal = Theme.spacing._16)
            .padding(bottom = Theme.spacing._8)
            .background(
                color = Theme.colorScheme.background.surfaceLow,
                shape = RoundedCornerShape(Theme.radius.md)
            )
            .clickable(onClick = onSelect)
            .padding(Theme.spacing._8),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8)
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = reciter,
                style = Theme.typography.label.medium,
                color = Theme.colorScheme.shadePrimary,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )

            RecitersDetails(
                recitingType = recitingType,
                isDownloaded = isDownloaded
            )
        }
        if (isSelectedShown)
            RadioButton(
                isSelected = isSelected,
                onClick = onSelect
            )
    }
}

@Composable
private fun RecitersDetails(
    recitingType: String,
    isDownloaded: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._4)
    ) {
        Text(
            text = recitingType,
            style = Theme.typography.label.small,
            color = Theme.colorScheme.shadeSecondary
        )
        if (isDownloaded) {
            Icon(
                painterResource(Res.drawable.ic_tick_double_check),
                contentDescription = stringResource(Res.string.success),
                modifier = Modifier.size(Theme.spacing._12)
            )

            Text(
                text = stringResource(Res.string.downloaded),
                style = Theme.typography.label.small,
                color = Theme.colorScheme.success
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    QuranTheme {
        ReciterItem(
            reciter = "Muhammad Siddiq Al-MinshawiMuhammad Siddiq Al-MinshawiMuhammad Siddiq Al-MinshawiMuhammad Siddiq Al-MinshawiMuhammad Siddiq Al-Minshawi",
            recitingType = "Teacher - Tajweed",
            isDownloaded = true,
            isSelected = true,
            onSelect = {},
            isSelectedShown = true
        )
    }
}