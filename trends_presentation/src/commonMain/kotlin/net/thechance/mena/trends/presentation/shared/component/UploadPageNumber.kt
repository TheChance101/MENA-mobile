package net.thechance.mena.trends.presentation.shared.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import mena.trends_presentation.generated.resources.Res
import mena.trends_presentation.generated.resources.page_number
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun UploadPageNumber(
    page: Int,
    modifier: Modifier = Modifier,
    total: Int = 3
) {
    Text(
        modifier = modifier
            .clip(RoundedCornerShape(Theme.radius.full))
            .background(Theme.colorScheme.background.surfaceLow)
            .padding(horizontal = Theme.spacing._8, vertical = Theme.spacing._4),
        text = stringResource(Res.string.page_number, page, total),
        style = Theme.typography.label.small,
        color = Theme.colorScheme.shadePrimary,
        textAlign = TextAlign.Center
    )
}