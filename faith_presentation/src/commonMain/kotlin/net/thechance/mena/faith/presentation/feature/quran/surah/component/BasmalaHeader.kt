package net.thechance.mena.faith.presentation.feature.quran.surah.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.bismillah
import mena.faith_presentation.generated.resources.ic_bismillah
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import net.thechance.mena.faith.presentation.utils.extentions.noRippleClickable
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun BasmalaHeader(
    selectedAyahIndex: Int?,
    onDismissActionButtons: () -> Unit
) {

    Image(
        painter = painterResource(Res.drawable.ic_bismillah),
        colorFilter = ColorFilter.tint(Theme.colorScheme.primary.primary),
        contentDescription = stringResource(Res.string.bismillah),
        modifier = Modifier
            .padding(horizontal = 74.dp)
            .aspectRatio(4f)
            .noRippleClickable {
                if (selectedAyahIndex != null) onDismissActionButtons()
            }
    )
}

@Preview
@Composable
private fun Preview() {
    MenaTheme {
        QuranTheme {
            BasmalaHeader(selectedAyahIndex = 1, onDismissActionButtons = {})
        }
    }
}
