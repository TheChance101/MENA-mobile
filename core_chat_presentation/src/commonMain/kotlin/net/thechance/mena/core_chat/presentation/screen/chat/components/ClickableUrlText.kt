package net.thechance.mena.core_chat.presentation.screen.chat.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle

@Composable
fun ClickableUrlText(
    text: AnnotatedString,
    style: TextStyle,
    color: Color,
    onUrlClick: (String) -> Unit,
    onTextClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var textLayoutResult: TextLayoutResult? = null

    BasicText(
        text = text,
        style = style.copy(color = color),
        onTextLayout = { textLayoutResult = it },
        modifier = modifier.pointerInput(Unit) {
            detectTapGestures { offset ->
                when (val url = textLayoutResult?.findUrlAtPosition(text, offset)) {
                    null -> onTextClick()
                    else -> onUrlClick(url)
                }
            }
        }
    )
}

private fun TextLayoutResult.findUrlAtPosition(text: AnnotatedString, offset: Offset): String? {
    val position = getOffsetForPosition(offset)
    return text.getStringAnnotations(tag = "URL", start = position, end = position).firstOrNull()?.item
}