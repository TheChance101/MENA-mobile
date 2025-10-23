package net.thechance.mena.faith.presentation.feature.quran.surah.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.arrow_left
import mena.faith_presentation.generated.resources.bismillah
import mena.faith_presentation.generated.resources.ic_arrow_left
import mena.faith_presentation.generated.resources.ic_bismillah
import mena.faith_presentation.generated.resources.ic_search
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.appBar.AppBarOptionContainer
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.domain.entity.Ayah
import net.thechance.mena.faith.presentation.designSystem.theme.quran
import net.thechance.mena.faith.presentation.feature.quran.surah.SurahInteractionListener
import net.thechance.mena.faith.presentation.feature.quran.surah.SurahUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

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
                    contentDescription = stringResource(Res.string.arrow_left)
                )
            }
        }
    )
}

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

@Composable
internal fun AnimatedAyahActionButtons(
    state: SurahUiState,
    listener: SurahInteractionListener,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = state.isAyahActionButtonsVisible,
        enter = fadeIn(animationSpec = tween()),
        exit = fadeOut(animationSpec = tween()),
        modifier = modifier
    ) {
        if (isValidAyahSelection(state)) {
            val selectedAyah = state.selectedAyahNumber?.let { state.ayatOfSurah[it.dec()] }
            AyahActionButtons(
                onBookmarkClick = { listener.onBookmarkClick(selectedAyah?.number ?: 0) },
                onCopyClick = { listener.onCopyClick(ayahContent = state.selectedAyah) },
                onShareClick = { listener.onShareClick(state.selectedAyah) }
            )
        }
    }
}

private fun isValidAyahSelection(state: SurahUiState): Boolean {
    return state.selectedAyahNumber != null &&
            state.selectedAyahNumber >= 0 &&
            state.selectedAyahNumber <= state.ayatOfSurah.size
}

@Composable
fun getAyahTextStyle() = Theme.typography.quran.large.copy(
    textDirection = TextDirection.Rtl,
    textAlign = TextAlign.Justify
)

@Composable
internal fun UnifiedChunkAyat(
    chunkAyat: List<Ayah>,
    selectedAyahIndex: Int?,
    textLayoutResult:TextLayoutResult?,
    onTextLayoutResultChange: (TextLayoutResult) -> Unit,
    onLongPress: (Ayah) -> Unit,
    onDismiss: () -> Unit
) {
    val styledText = buildAnnotatedString {
        chunkAyat.forEach { ayah ->
            val color = getAyahTextColor(selectedAyahIndex, ayah.number)
            pushStyle(SpanStyle(color = color))
            append(ayah.content)
            pop()
            append(" ")
        }
    }
    BasicText(
        text = styledText,
        style = getAyahTextStyle(),
        onTextLayout = { onTextLayoutResultChange(it) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Theme.spacing._16, vertical = Theme.spacing._8)
            .pointerInput(selectedAyahIndex) {
                detectTapGestures(onTap = { onDismiss() }, onLongPress = { offset ->
                    textLayoutResult?.let { layout ->
                        val ayahPosition = layout.getOffsetForPosition(offset)
                        val clickedAyahIndex = findClickedAyahIndexFromPosition(
                            ayat = chunkAyat, position = ayahPosition
                        )
                        if (clickedAyahIndex != -1) {
                            val ayah = chunkAyat[clickedAyahIndex]
                            onLongPress(ayah)
                        }
                    }
                })
            })
}

@Composable
private fun getAyahTextColor(selectedAyahIndex: Int?, currentIndex: Int): Color {
    return when (selectedAyahIndex) {
        null -> Theme.colorScheme.shadePrimary
        currentIndex -> Theme.colorScheme.shadeSecondary
        else -> Theme.colorScheme.shadeTertiary
    }
}

private fun findClickedAyahIndexFromPosition(ayat: List<Ayah>, position: Int): Int {
    var currentPosition = 0
    ayat.forEachIndexed { index, ayah ->
        val end = currentPosition + ayah.content.length
        if (position in currentPosition..end) return index
        currentPosition = end + 1
    }
    return -1
}