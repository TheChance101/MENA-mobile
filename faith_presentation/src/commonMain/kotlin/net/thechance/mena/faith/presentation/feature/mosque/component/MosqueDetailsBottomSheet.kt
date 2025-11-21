package net.thechance.mena.faith.presentation.feature.mosque.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.ic_arrow_right
import mena.faith_presentation.generated.resources.kilometer_unit
import mena.faith_presentation.generated.resources.mosque_details
import mena.faith_presentation.generated.resources.view_on_map
import net.thechance.mena.designsystem.presentation.component.bottomSheet.BottomSheet
import net.thechance.mena.designsystem.presentation.component.scaffold.ScaffoldScope
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import net.thechance.mena.faith.presentation.feature.mosque.MosqueUiState
import net.thechance.mena.faith.presentation.feature.mosque.fakeMosqueDetails
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun ScaffoldScope.MosqueDetailsBottomSheet(
    isVisible: Boolean,
    mosque: MosqueUiState = fakeMosqueDetails,
    onNavigationClick: () -> Unit = {},
    onDismiss: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    BottomSheet(
        isVisible = isVisible,
        skipPartiallyExpanded = true,
        onDismissRequest = onDismiss,
        modifier = modifier.navigationBarsPadding(),
        sheetContent = {
            MosqueDetailsContent(
                mosque = mosque,
                onNavigationClick = onNavigationClick,
            )
        }
    )
}

@Composable
private fun MosqueDetailsContent(
    mosque: MosqueUiState,
    onNavigationClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp, bottom = 24.dp)
    ) {
        Text(
            modifier = Modifier.padding(bottom = Theme.spacing._12),
            text = stringResource(Res.string.mosque_details),
            style = Theme.typography.title.small,
            color = Theme.colorScheme.shadePrimary
        )
        AsyncImage(
            model = mosque.imageUrl,
            contentDescription = mosque.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth().aspectRatio(328 / 164f)
                .clip(RoundedCornerShape(Theme.radius.lg))
        )
        Text(
            modifier = Modifier.padding(top = Theme.spacing._8, bottom = Theme.spacing._4),
            text = mosque.name,
            style = Theme.typography.title.large,
            color = Theme.colorScheme.shadePrimary
        )
        Text(
            modifier = Modifier.padding(bottom = Theme.spacing._4),
            text = "${mosque.distance} ${stringResource(Res.string.kilometer_unit)}",
            style = Theme.typography.label.medium,
            color = Theme.colorScheme.shadeSecondary
        )
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .clickable(onClick = onNavigationClick),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing._4)
        ) {
            Text(
                text = stringResource(Res.string.view_on_map),
                style = Theme.typography.label.medium,
                color = Theme.colorScheme.primary.primary
            )
            Image(
                modifier = Modifier.size(16.dp),
                painter = painterResource(Res.drawable.ic_arrow_right),
                contentDescription = stringResource(Res.string.view_on_map),
                colorFilter = ColorFilter.tint(color = Theme.colorScheme.primary.primary)
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    MenaTheme {
        QuranTheme {
            MosqueDetailsContent(mosque = fakeMosqueDetails)
        }
    }
}