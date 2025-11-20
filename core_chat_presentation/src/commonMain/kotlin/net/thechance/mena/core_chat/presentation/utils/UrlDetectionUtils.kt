package net.thechance.mena.core_chat.presentation.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration

fun detectAndStyleUrls(text: String, linkColor: Color): AnnotatedString {
    val matches = UrlDetectionHelper.URL_PATTERN.findAll(text).toList()

    if (matches.isEmpty()) return AnnotatedString(text)

    return buildAnnotatedString {
        var currentIndex = 0

        for (match in matches) {
            val start = match.range.first
            val end = match.range.last + 1

            if (currentIndex < start) append(text.substring(currentIndex, start))

            val urlStartIndex = length
            append(match.value)
            val urlEndIndex = length

            addStringAnnotation(
                tag = "URL",
                annotation = "https://${match.value}",
                start = urlStartIndex,
                end = urlEndIndex
            )

            addStyle(
                style = SpanStyle(color = linkColor, textDecoration = TextDecoration.Underline),
                start = urlStartIndex,
                end = urlEndIndex
            )

            currentIndex = end
        }

        if (currentIndex < text.length) append(text.substring(currentIndex))

    }
}

fun containsUrl(text: String): Boolean = UrlDetectionHelper.URL_PATTERN.containsMatchIn(text)

private object UrlDetectionHelper {
    val URL_PATTERN =
        Regex(pattern = "(https?://[\\w\\-._~:/?#\\[\\]@!$&'()*+,;=%]+|www\\.[\\w\\-._~:/?#\\[\\]@!$&'()*+,;=%]+)", option = RegexOption.IGNORE_CASE)
}