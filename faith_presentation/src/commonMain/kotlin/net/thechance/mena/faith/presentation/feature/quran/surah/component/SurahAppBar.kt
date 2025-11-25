package net.thechance.mena.faith.presentation.feature.quran.surah.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.arrow_left
import mena.faith_presentation.generated.resources.ic_arrow_left
import mena.faith_presentation.generated.resources.ic_search
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
internal fun SurahAppBar(
    surahName: String,
    onSearchClick: () -> Unit,
    onBackClick: () -> Unit
) {
    AppBar(
        leadingContent = {
            Icon(
                painter = painterResource(Res.drawable.ic_arrow_left),
                tint = Theme.colorScheme.primary.primary,
                contentDescription = stringResource(Res.string.arrow_left)
            )
        },
        onLeadingClick = onBackClick,
        title = surahName,
        contentPadding = PaddingValues(
            vertical = Theme.spacing._8,
            horizontal = Theme.spacing._16
        ),
        trailingContent = {
            AppBarOptionContainer(
                onClick = onSearchClick,
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_search),
                    tint = Theme.colorScheme.primary.primary,
                    contentDescription = stringResource(Res.string.arrow_left)
                )
            }
        }
    )
}

@Preview
@Composable
private fun Preview() {
    MenaTheme {
        QuranTheme {
            SurahAppBar(surahName = "الفاتحة", onSearchClick = {}, onBackClick = {})
        }
    }
}
