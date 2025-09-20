package net.thechance.mena.faith.presentation.feature.quran.surah.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.arrow_left
import mena.faith_presentation.generated.resources.bismillah
import mena.faith_presentation.generated.resources.ic_arrow_left
import mena.faith_presentation.generated.resources.ic_bismillah
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.image.Image
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.domain.entity.Ayah
import net.thechance.mena.faith.presentation.designSystem.theme.quran
import net.thechance.mena.faith.presentation.feature.quran.surah.SurahInteractionListener
import net.thechance.mena.faith.presentation.feature.quran.surah.SurahScreenState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource


@Composable
internal fun SurahAppBar(
    surahName: String,
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
        )
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
    state: SurahScreenState,
    listener: SurahInteractionListener,
    modifier: Modifier = Modifier
) {

    AnimatedVisibility(
        visible = state.isAyahActionButtonsVisible,
        enter = fadeIn(animationSpec = tween(durationMillis = 300)),
        exit = fadeOut(animationSpec = tween(durationMillis = 300)),
        modifier = modifier
    ) {
        if (isValidAyahSelection(state)) {
            val selectedAyah = state.selectedAyahIndex?.let { state.ayatOfSurah[it] }
            AyahActionButtons(
                onBookmarkClick = { listener.onBookmarkClick(selectedAyah?.number ?: 0) },
                onCopyClick = { listener.onCopyClick(ayahContent = state.selectedAyah) },
                onShareClick = { listener.onShareClick(state.selectedAyah) }
            )
        }
    }
}

private fun isValidAyahSelection(state: SurahScreenState): Boolean {
    return state.selectedAyahIndex != null &&
            state.selectedAyahIndex >= 0 &&
            state.selectedAyahIndex < state.ayatOfSurah.size
}

@Composable
internal fun AyatContent(
    annotatedText: AnnotatedString,
    state: SurahScreenState,
    ayat: List<Ayah>,
    listener: SurahInteractionListener
) {
    var textLayoutResult by remember {
        mutableStateOf<TextLayoutResult?>(null )
    }

    BasicText(
        text = annotatedText,
        onTextLayout = { textLayoutResult = it },
        style = getAyahTextStyle(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Theme.spacing._16)
            .pointerInput(state.selectedAyahIndex) {
                detectTapGestures(
                    onTap = {
                        if (state.isAyahActionButtonsVisible) listener.onDismissActionButtons()
                    },
                    onLongPress = { offset ->
                        handleAyahLongPress(
                            offset = offset,
                            textLayoutResult = textLayoutResult,
                            annotatedText = annotatedText,
                            ayat = ayat,
                            listener = listener
                        )
                    }
                )
            }
    )
}

@Composable
private fun getAyahTextStyle() = Theme.typography.quran.large.copy(
        textDirection = TextDirection.Rtl,
        textAlign = TextAlign.Justify
    )

 private fun handleAyahLongPress(
     offset: Offset,
     textLayoutResult: TextLayoutResult?,
     annotatedText: AnnotatedString,
     ayat: List<Ayah>,
     listener: SurahInteractionListener
) {
    textLayoutResult?.let { layoutResult ->
        val position = layoutResult.getOffsetForPosition(offset)
        val clickedAyahIndex = findClickedAyahIndex(annotatedText, position)
        if (clickedAyahIndex >= 0 && clickedAyahIndex < ayat.size) {
            val ayahContent = ayat[clickedAyahIndex].plainTextContent
            listener.onAyahLongPress(ayahContent, clickedAyahIndex)
        }
    }
}

private fun findClickedAyahIndex(
    annotatedText: AnnotatedString,
    offset: Int
): Int {
    val annotations = annotatedText.getStringAnnotations(
        tag = "AYAH",
        start = offset,
        end = offset
    )
    return annotations.firstOrNull()?.item?.toIntOrNull() ?: -1
}