package net.thechance.mena.dukan.presentation.util.text

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

fun buildExpandableText(
    fullText: String,
    isTextExpanded: Boolean,
    seeLessText: String,
    seeMoreText: String,
    visiblePartOfText: String,
    textColor: Color,
    seeLessAndMoreColor: Color,
): AnnotatedString {
    return when {
        visiblePartOfText.isNotEmpty() && isTextExpanded.not() -> buildAnnotatedString {
            withStyle(style = SpanStyle(color = textColor)) {
                append(visiblePartOfText)
            }
            withStyle(style = SpanStyle(color = seeLessAndMoreColor, fontWeight = FontWeight.Bold)) {
                append(seeMoreText)
            }
        }
        isTextExpanded -> buildAnnotatedString {
            withStyle(style = SpanStyle(color = textColor)) {
                append(fullText)
            }
            withStyle(style = SpanStyle(color = seeLessAndMoreColor,fontWeight = FontWeight.Bold)) {
                append(seeLessText)
            }
        }
        else -> buildAnnotatedString {
            withStyle(style = SpanStyle(color = textColor)) {
                append(fullText)
            }
        }
    }
}