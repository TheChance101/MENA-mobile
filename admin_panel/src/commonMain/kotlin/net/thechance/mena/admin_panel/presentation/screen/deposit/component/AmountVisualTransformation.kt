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

    return clean.reversed()
        .chunked(3)
        .joinToString(",")
        .reversed()
}
