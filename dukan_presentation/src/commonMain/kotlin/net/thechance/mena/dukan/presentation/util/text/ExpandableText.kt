package net.thechance.mena.dukan.presentation.util.text

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import kotlinx.coroutines.delay
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.shared.AnnotatedText

@Composable
fun ExpandableText(
    text: String,
    style: TextStyle,
    seeMoreText: String,
    textColor: Color,
    seeLessAndMoreColor: Color,
    initialMaxLine:Int,
    modifier: Modifier = Modifier,
    seeLessText: String = "",
    minLines: Int = 1,
    overflow: TextOverflow = TextOverflow.Ellipsis
){
    val isTextExpanded = remember { mutableStateOf(false) }
    val isTextClickable = remember { mutableStateOf(false) }
    val visibleText = remember { mutableStateOf("") }
    val maxLine = if (isTextExpanded.value) Int.MAX_VALUE else initialMaxLine
    val bringIntoViewRequester = BringIntoViewRequester()

    val displayedText = remember(isTextExpanded.value, visibleText.value) {
        buildExpandableText(
            fullText = text,
            isTextExpanded = isTextExpanded.value,
            visiblePartOfText = visibleText.value,
            textColor = textColor,
            seeLessAndMoreColor = seeLessAndMoreColor,
            seeLessText = seeLessText,
            seeMoreText = seeMoreText
        )
    }

    LaunchedEffect(isTextExpanded.value) {
        if (isTextExpanded.value) {
            delay(300)
            bringIntoViewRequester.bringIntoView()
        }
    }

    AnnotatedText(
        text = displayedText,
        style = style,
        minLines = minLines,
        maxLines = maxLine,
        overflow = overflow,
        modifier = modifier
            .bringIntoViewRequester(bringIntoViewRequester)
            .padding(top = Theme.spacing._8, bottom = Theme.spacing._8)
            .clickable(
                enabled = isTextClickable.value,
                indication = null,
                interactionSource = null,
                onClick = { isTextExpanded.value = !isTextExpanded.value }
            )
            .animateContentSize(TweenSpec()),
        onTextLayout = { textLayoutResult ->
            if (textLayoutResult.hasVisualOverflow) {
                val lastIndex = minOf(maxLine - 1, textLayoutResult.lineCount - 1)
                val lastCharIndex = textLayoutResult.getLineEnd(lastIndex, visibleEnd = true)
                visibleText.value = text.take(lastCharIndex).dropLast(12)
                isTextClickable.value = true
            }
        }
    )
}