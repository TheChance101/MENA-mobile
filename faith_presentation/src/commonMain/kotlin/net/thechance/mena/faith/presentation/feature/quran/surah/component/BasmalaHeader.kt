package net.thechance.mena.faith.presentation.feature.quran.surah.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.bismillah
import mena.faith_presentation.generated.resources.ic_bismillah
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
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
        contentDescription = stringResource(Res.string.bismillah),
        modifier = Modifier
            .padding(horizontal = 74.dp)
            .aspectRatio(4f)
            .pointerInput(selectedAyahIndex) {
                detectTapGestures(
                    onTap = {
                        if (selectedAyahIndex != null) onDismissActionButtons()
                    }
                )
            }
    )
}

@Preview()
@Composable
private fun Preview() {
    QuranTheme {
        BasmalaHeader(selectedAyahIndex = 1, onDismissActionButtons = {})
    }
}