package net.thechance.mena.faith.presentation.feature.quran.downloadedSur.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.empty_download_icon
import mena.faith_presentation.generated.resources.empty_download_text
import mena.faith_presentation.generated.resources.icon_empty_download
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun EmptyDownloadState(
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(Res.drawable.icon_empty_download),
            contentDescription = stringResource(Res.string.empty_download_icon),
            modifier = Modifier.fillMaxWidth()
                .size(128.dp)
                .padding(bottom = Theme.spacing._12)
        )

        Text(
            text = stringResource(Res.string.empty_download_text),
            style = Theme.typography.title.small,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview
@Composable
fun EmptyDownloadStatePreview() {
    MenaTheme {
        QuranTheme {
            EmptyDownloadState()
        }
    }
}