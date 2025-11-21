package net.thechance.mena.admin_panel.presentation.screen.deposit.component

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class AmountVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val original = text.text
        val formatted = formatAmount(original)

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val commasBefore = formatAmount(original.substring(0, offset))
                    .count { it == ',' }
                return offset + commasBefore
            }

            override fun transformedToOriginal(offset: Int): Int {
                val removed = formatted.substring(0, offset).count { it == ',' }
                return offset - removed
            }
        }

        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}

fun formatAmount(input: String): String {
    if (input.isBlank()) return ""
    val clean = input.replace(",", "")

    val parts = clean.split(".")
    val integerPart = parts[0]
    val decimalPart = if (parts.size > 1) parts[1] else null

    val formattedInteger = if (integerPart.isNotEmpty()) {
        integerPart.reversed()
            .chunked(3)
            .joinToString(",")
            .reversed()
    } else {
        ""
    }

    return if (decimalPart != null) {
        "$formattedInteger.$decimalPart"
    } else if (clean.endsWith(".")) {
        "$formattedInteger."
    } else {
        formattedInteger
    }
}